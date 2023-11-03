/**
 *     Copyright (C) 2019-2023 Ubiqube.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see https://www.gnu.org/licenses/.
 */
package com.ubiqube.etsi.mano.orchestrator.scale;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

import org.jgrapht.ListenableGraph;
import org.jgrapht.graph.DefaultListenableGraph;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ubiqube.etsi.mano.orchestrator.ContextHolder;
import com.ubiqube.etsi.mano.orchestrator.Edge2d;
import com.ubiqube.etsi.mano.orchestrator.SclableResources;
import com.ubiqube.etsi.mano.orchestrator.Vertex2d;
import com.ubiqube.etsi.mano.orchestrator.exceptions.OrchestrationException;
import com.ubiqube.etsi.mano.orchestrator.nodes.Node;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTaskConnectivityV3;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTaskV3;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTaskVertexListenerV3;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 *
 * @author Olivier Vignaud
 *
 */
public class PlanMultiplier<U> {
	private static final String ADD_VT = "Add VT {}";
	private static final Logger LOG = LoggerFactory.getLogger(PlanMultiplier.class);

	@Nonnull
	private final List<SclableResources<U>> scaleResources;
	private final Function<U, VirtualTaskV3<U>> converter;
	@Nonnull
	private final List<ContextHolder> liveItems;

	public PlanMultiplier(final List<SclableResources<U>> scaleResources, final Function<U, VirtualTaskV3<U>> converter, final List<ContextHolder> liveItems) {
		this.scaleResources = scaleResources;
		this.converter = converter;
		this.liveItems = liveItems;
	}

	/**
	 * Take a plan as input and multiply it by the given {@link SclableResources}.
	 *
	 * @param plan A 2D plan of the connected resources.
	 * @param sr   A SclableResources.
	 * @return The new graph.
	 */
	public ListenableGraph<VirtualTaskV3<U>, VirtualTaskConnectivityV3<U>> multiply(final ListenableGraph<Vertex2d, Edge2d> plan, final SclableResources<U> sr) {
		final Set<ContextHolder> cache = new HashSet<>();
		final ListenableGraph<VirtualTaskV3<U>, VirtualTaskConnectivityV3<U>> d = new DefaultListenableGraph(new DirectedAcyclicGraph<>(VirtualTaskConnectivityV3.class));
		d.addGraphListener(new VirtualTaskVertexListenerV3<>());
		final Map<String, VirtualTaskV3<U>> hash = new HashMap<>();
		/**
		 * Have = 1 , want = 2 => +1 have = 1 , want = 0 => -1
		 */
		final int max = Math.max(sr.getHave(), sr.getWant());
		LOG.debug("SR: {}", sr);
		for (int i = 0; i < max; i++) {
			final boolean delete = i >= sr.getWant();
			final int ii = i;
			plan.edgeSet().forEach(x -> {
				final String uniqIdSrc = getUniqId(x.getSource(), ii);
				final VirtualTaskV3<U> src = hash.computeIfAbsent(uniqIdSrc, y -> createVertex(d, delete, ii, x.getSource(), uniqIdSrc, cache));
				final String uniqIdDst = getUniqId(x.getTarget(), ii);
				final VirtualTaskV3<U> dst = hash.computeIfAbsent(uniqIdDst, y -> createVertex(d, delete, ii, x.getTarget(), uniqIdDst, cache));
				Optional.ofNullable(d.addEdge(src, dst)).ifPresent(y -> y.setRelation(x.getRelation()));
			});
			plan.vertexSet().forEach(x -> {
				final String uniqIdSrc = getUniqId(x, ii);
				hash.computeIfAbsent(uniqIdSrc, y -> createVertex(d, delete, ii, x, uniqIdSrc, cache));
			});
		}
		collectOrphan(d, plan, max, cache);
		return d;
	}

	private void collectOrphan(final ListenableGraph<VirtualTaskV3<U>, VirtualTaskConnectivityV3<U>> d, final ListenableGraph<Vertex2d, Edge2d> plan, final int i, final Set<ContextHolder> cache) {
		final List<Vertex2d> res = plan.vertexSet().stream()
				.filter(x -> match(x, liveItems))
				.toList();
		final List<Vertex2d> should = res.stream().filter(x -> isNotIn(x, d)).toList();
		should.stream().forEach(x -> createVertex(d, true, i, x, UUID.randomUUID().toString(), cache));
		LOG.debug("{}", should);
	}

	private static <U> boolean isNotIn(final Vertex2d v, final ListenableGraph<VirtualTaskV3<U>, VirtualTaskConnectivityV3<U>> d) {
		return d.vertexSet().stream()
				.filter(x -> x.getType() == v.getType())
				.noneMatch(x -> x.getName().equals(v.getName()));
	}

	/**
	 * Match a given vertex2d in live instances.
	 *
	 * @param v         The given vertex2d to match among live instances.
	 * @param liveItems A list of live instances.
	 * @return True or false.
	 */
	private static boolean match(final Vertex2d v, final List<ContextHolder> liveItems) {
		return liveItems.stream()
				.filter(x -> x.getType() == v.getType())
				.anyMatch(x -> x.getName().equals(v.getName()));
	}

	/**
	 *
	 * @param d
	 * @param delete
	 * @param ii
	 * @param x
	 * @param uniqIdDst
	 * @param cache
	 * @return
	 */
	private VirtualTaskV3<U> createVertex(final ListenableGraph<VirtualTaskV3<U>, VirtualTaskConnectivityV3<U>> d,
			final boolean delete, final int ii, final Vertex2d x, final String uniqIdDst, final Set<ContextHolder> cache) {
		final SclableResources<U> templSr = findTemplate(scaleResources, x);
		final VirtualTaskV3<U> t = createTask(templSr, uniqIdDst, x, ii, delete, cache);
		LOG.debug(ADD_VT, t.getAlias());
		d.addVertex(t);
		return t;
	}

	/**
	 * Find the corresponding scale resource for a given Vertex2D.
	 *
	 * @param <U>
	 * @param plan   The plan.
	 * @param target The desired vertex2D.
	 * @return A ScalableResources matching the Vertext2d. Throw an exception if not
	 *         found.
	 */
	private static <U> SclableResources<U> findTemplate(final List<SclableResources<U>> plan, final Vertex2d target) {
		final List<SclableResources<U>> l = plan.stream()
				.filter(x -> x.getType() == target.getType())
				.filter(x -> x.getName().equals(target.getName()))
				.toList();
		if (l.size() > 1) {
			throw new OrchestrationException("Multiple match for vertex " + target);
		}
		if (l.isEmpty()) {
			throw new OrchestrationException("No match for vertex " + target);
		}
		LOG.trace("Found {}", l.get(0));
		return l.get(0);
	}

	/**
	 * Create Task When :
	 *
	 * <ul>
	 * <li>A live instance exist and delete is enable => Create Task</li>
	 * <li>A live instance exist and delete is disable => Create Context dummy
	 * task</li>
	 * <li>A live instance does not exist and delete is enable => Create dummy
	 * task</li>
	 * <li>A live instance does not exist and delete is disable => Create Task</li>
	 * </ul>
	 *
	 * @param sr
	 * @param converter
	 * @param liveItems
	 * @param uniqIdSrc
	 * @param xVertex
	 * @param ii
	 * @param delete
	 * @param cache
	 * @param vimConnectionId
	 * @return
	 */
	private VirtualTaskV3<U> createTask(final SclableResources<U> sr, final String uniqIdSrc, final Vertex2d x, final int ii, final boolean delete, final Set<ContextHolder> cache) {
		final Optional<ContextHolder> ctx = findInContext(liveItems, x, ii);
		/**
		 * /!\ Create task is cloning the task and assign a new ToscaId to it, witch
		 * later is used for re mapping DB tasks to WF task. Once you have created a
		 * task, you must use it's toscaId
		 */
		VirtualTaskV3<U> t = createTask(uniqIdSrc, x, ii, delete, sr.getTemplateParameter());
		if (ctx.isPresent()) {
			final ContextHolder liveInstance = ctx.get();
			cache.add(liveInstance);
			if (!delete) {
				return createContext(uniqIdSrc, x, ii, delete, t.getTemplateParameters(), liveInstance.getResourceId(), t.getType(), liveInstance.getVimConnectionId());
			}
			t.setVimResourceId(liveInstance.getResourceId());
			t.setVimConnectionId(liveInstance.getVimConnectionId());
			t.setRemovedLiveInstanceId(Objects.requireNonNull(liveInstance.getLiveInstanceId()));
		} else {
			if (delete) {
				if ((sr.getHave() != 0) || (sr.getWant() != 0)) {
					LOG.warn("Deleting {} but not found in context.", uniqIdSrc);
				}
				t = createContext(uniqIdSrc, x, ii, delete, t.getTemplateParameters(), null, t.getType(), null);
			}
			LOG.trace("creating task: {}/{}", x.getType().getSimpleName(), x.getName());
		}
		return t;
	}

	/**
	 * Create a Virtual task from a resourceId.
	 *
	 * @param <U>
	 * @param uniqIdSrc
	 * @param source
	 * @param i
	 * @param delete
	 * @param u
	 * @param resourceId
	 * @param class1
	 * @param vimConnectionId
	 * @return A {@link ContextVt} instance
	 */
	@SuppressWarnings("unchecked")
	private static <U> VirtualTaskV3<U> createContext(final String uniqIdSrc, final Vertex2d source, final int i, final boolean delete,
			final U u, @Nullable final String resourceId, final Class<? extends Node> class1, @Nullable final String vimConnectionId) {
		return (VirtualTaskV3<U>) ContextVt.builder()
				.alias(uniqIdSrc)
				.delete(delete)
				.name(source.getName())
				.rank(i)
				.templateParameters(u)
				.vimResourceId(resourceId)
				.vimConnectionId(vimConnectionId)
				.parent(class1)
				.build();
	}

	/**
	 * Find resource (Vertex2d) in a list of live items, using rank.
	 *
	 * @param liveItems Live items.
	 * @param source    The vertex to search.
	 * @param i         The rank.
	 * @return An optional {@link ContextHolder}, Throw an exception if multiple
	 *         match.
	 */
	private static Optional<ContextHolder> findInContext(final List<ContextHolder> liveItems, final Vertex2d source, final int i) {
		final List<ContextHolder> lst = liveItems.stream()
				.filter(x -> x.getType() == source.getType())
				.filter(x -> x.getName().equals(source.getName()))
				.filter(x -> x.getRank() == i)
				.toList();
		if (lst.size() > 1) {
			LOG.warn("List is more than 1 for {}-{}", source.getType().getSimpleName(), source.getName());
		}
		return Optional.ofNullable(lst).filter(x -> !lst.isEmpty()).map(x -> x.get(0));
	}

	/**
	 * Create an unique id using vertex name and rank.
	 *
	 * @param source The vertex.
	 * @param i      The rank.
	 * @return The unique name.
	 */
	private static String getUniqId(final Vertex2d source, final int i) {
		final StringBuilder sb = new StringBuilder(source.getName());
		if (null != source.getParent()) {
			sb.append("-").append(source.getParent());
		}
		sb.append("-").append(source.getType().getSimpleName());
		sb.append("-").append(String.format("%04d", i));
		return sb.toString();
	}

	/**
	 * Create a task from a given vertex.
	 *
	 * @param uniqId The task id.
	 * @param source The source vertex.
	 * @param i      The rank.
	 * @param delete True if vertex have to be deleted.
	 * @param params Parameters for vertex creation.
	 * @return The newly created task.
	 */
	private VirtualTaskV3<U> createTask(final String uniqId, final Vertex2d source, final int i, final boolean delete, final U params) {
		final VirtualTaskV3<U> vt = converter.apply(params);
		vt.setRank(i);
		vt.setName(source.getName());
		vt.setAlias(uniqId);
		vt.setDelete(delete);
		return vt;
	}

}
