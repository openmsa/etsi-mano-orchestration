/**
 *     Copyright (C) 2019-2020 Ubiqube.
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
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.ubiqube.etsi.mano.orchestrator.scale;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import org.jgrapht.ListenableGraph;
import org.jgrapht.graph.DefaultListenableGraph;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ubiqube.etsi.mano.orchestrator.ContextHolder;
import com.ubiqube.etsi.mano.orchestrator.SclableResources;
import com.ubiqube.etsi.mano.orchestrator.exceptions.OrchestrationException;
import com.ubiqube.etsi.mano.orchestrator.nodes.Node;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTaskConnectivityV3;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTaskV3;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTaskVertexListenerV3;
import com.ubiqube.etsi.mano.service.graph.Edge2d;
import com.ubiqube.etsi.mano.service.graph.Vertex2d;

/**
 *
 * @author olivier
 *
 */
public class PlanMultiplier {
	private static final String ADD_VT = "Add VT {}";
	private static final Logger LOG = LoggerFactory.getLogger(PlanMultiplier.class);

	public <U> ListenableGraph<VirtualTaskV3<U>, VirtualTaskConnectivityV3<U>> multiply(final ListenableGraph<Vertex2d, Edge2d> plan,
			final SclableResources<U> sr, final Function<U, VirtualTaskV3<U>> converter, final List<ContextHolder> liveItems,
			final List<SclableResources<U>> scaleResources) {
		final ListenableGraph<VirtualTaskV3<U>, VirtualTaskConnectivityV3<U>> d = new DefaultListenableGraph<>(new DirectedAcyclicGraph<>(VirtualTaskConnectivityV3.class));
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
				final VirtualTaskV3<U> src = hash.computeIfAbsent(uniqIdSrc, y -> {
					final SclableResources<U> templSr = findTemplate(scaleResources, x.getSource());
					final VirtualTaskV3<U> t = createTask(templSr, converter, liveItems, uniqIdSrc, x.getSource(), ii, delete);
					LOG.debug(ADD_VT, t.getAlias());
					d.addVertex(t);
					return t;
				});
				final String uniqIdDst = getUniqId(x.getTarget(), ii);
				final VirtualTaskV3<U> dst = hash.computeIfAbsent(uniqIdDst, y -> {
					final SclableResources<U> templSr = findTemplate(scaleResources, x.getTarget());
					final VirtualTaskV3<U> t = createTask(templSr, converter, liveItems, uniqIdDst, x.getTarget(), ii, delete);
					LOG.debug(ADD_VT, t.getAlias());
					d.addVertex(t);
					return t;
				});
				Optional.ofNullable(d.addEdge(src, dst)).ifPresent(y -> y.setRelation(x.getRelation()));
			});
			plan.vertexSet().forEach(x -> {
				final String uniqIdSrc = getUniqId(x, ii);
				hash.computeIfAbsent(uniqIdSrc, y -> {
					final SclableResources<U> templSr = findTemplate(scaleResources, x);
					final VirtualTaskV3<U> t = createTask(templSr, converter, liveItems, uniqIdSrc, x, ii, delete);
					LOG.debug(ADD_VT, t.getAlias());
					d.addVertex(t);
					return t;
				});
			});
		}
		return d;
	}

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
	 * @param <U>
	 * @param sr
	 * @param converter
	 * @param liveItems
	 * @param uniqIdSrc
	 * @param x
	 * @param ii
	 * @param delete
	 * @param vimConnectionId
	 * @return
	 */
	private static <U> VirtualTaskV3<U> createTask(final SclableResources<U> sr, final Function<U, VirtualTaskV3<U>> converter, final List<ContextHolder> liveItems, final String uniqIdSrc, final Vertex2d x, final int ii, final boolean delete) {
		final Optional<ContextHolder> ctx = findInContext(liveItems, x, ii);
		VirtualTaskV3<U> t = createTask(uniqIdSrc, x, ii, delete, sr.getTemplateParameter(), converter);
		if (ctx.isPresent()) {
			if (!delete) {
				t = createContext(uniqIdSrc, x, ii, delete, sr.getTemplateParameter(), ctx.get().getResourceId(), t.getType(), ctx.get().getVimConnectionId());
			} else {
				t.setVimResourceId(ctx.get().getResourceId());
				t.setVimConnectionId(ctx.get().getVimConnectionId());
				t.setRemovedLiveInstanceId(Objects.requireNonNull(ctx.get().getLiveInstanceId()));
			}
		} else {
			if (delete) {
				if ((sr.getHave() != 0) || (sr.getWant() != 0)) {
					LOG.warn("Deleting {} but not found in context.", uniqIdSrc);
				}
				t = createContext(uniqIdSrc, x, ii, delete, sr.getTemplateParameter(), null, t.getType(), null);
			}
			LOG.trace("creating task: {}/{}", x.getType().getSimpleName(), x.getName());
		}
		return t;
	}

	@SuppressWarnings("unchecked")
	private static <U> VirtualTaskV3<U> createContext(final String uniqIdSrc, final Vertex2d source, final int i, final boolean delete,
			final U u, final String resourceId, final Class<? extends Node> class1, final String vimConnectionId) {
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

	private static String getUniqId(final Vertex2d source, final int i) {
		final StringBuilder sb = new StringBuilder(source.getName());
		if (null != source.getParent()) {
			sb.append("-").append(source.getParent());
		}
		sb.append("-").append(source.getType().getSimpleName());
		sb.append("-").append(String.format("%04d", i));
		return sb.toString();
	}

	private static <U> VirtualTaskV3<U> createTask(final String uniqId, final Vertex2d source, final int i, final boolean delete, final U params, final Function<U, VirtualTaskV3<U>> converter) {
		final VirtualTaskV3<U> vt = converter.apply(params);
		vt.setTemplateParameters(params);
		vt.setRank(i);
		vt.setName(source.getName());
		vt.setAlias(uniqId);
		vt.setDelete(delete);
		return vt;
	}

}
