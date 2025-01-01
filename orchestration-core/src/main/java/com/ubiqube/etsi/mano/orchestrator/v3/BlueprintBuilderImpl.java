/**
 * Copyright (C) 2019-2025 Ubiqube.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.ubiqube.etsi.mano.orchestrator.v3;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.jgrapht.ListenableGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ubiqube.etsi.mano.orchestrator.ContextHolder;
import com.ubiqube.etsi.mano.orchestrator.Edge2d;
import com.ubiqube.etsi.mano.orchestrator.SclableResources;
import com.ubiqube.etsi.mano.orchestrator.Vertex2d;
import com.ubiqube.etsi.mano.orchestrator.nodes.Node;
import com.ubiqube.etsi.mano.orchestrator.scale.PlanMerger;
import com.ubiqube.etsi.mano.orchestrator.scale.PlanMultiplier;
import com.ubiqube.etsi.mano.orchestrator.scale.ScalingEngine;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTaskConnectivityV3;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTaskV3;

import jakarta.annotation.Nonnull;

/**
 *
 * @author Olivier Vignaud
 *
 */
@Service
public class BlueprintBuilderImpl implements BlueprintBuilder {
	private static final Logger LOG = LoggerFactory.getLogger(BlueprintBuilderImpl.class);
	@Nonnull
	private final ScalingEngine se = new ScalingEngine();
	@Nonnull
	private final PlanMerger pMerge = new PlanMerger();

	@Override
	public <U> PreExecutionGraphV3<U> buildPlan(final List<SclableResources<U>> scaleResources, final ListenableGraph<Vertex2d, Edge2d> g,
			final Function<U, VirtualTaskV3<U>> converter, final List<ContextHolder> liveItems, final List<Class<? extends Node>> masterVertex) {
		final PlanMultiplier<U> pm = new PlanMultiplier<>(scaleResources, converter, liveItems);
		final List<SclableResources<U>> ttd = masterVertex.stream()
				.map(x -> toThingsToDo(x, scaleResources))
				.flatMap(List::stream)
				.toList();
		final List<ListenableGraph<VirtualTaskV3<U>, VirtualTaskConnectivityV3<U>>> plans = new ArrayList<>();
		ttd.forEach(x -> {
			LOG.trace("SR = {}/{}", x.getType().getSimpleName(), x.getName());
			final ListenableGraph<Vertex2d, Edge2d> s = se.scale(g, x.getType(), x.getName());
			final ListenableGraph<VirtualTaskV3<U>, VirtualTaskConnectivityV3<U>> np = pm.multiply(s, x);
			plans.add(np);
		});
		final ListenableGraph<VirtualTaskV3<U>, VirtualTaskConnectivityV3<U>> ret = pMerge.merge(g, plans);
		return new PreExecutionGraphV3Impl<>(ret);
	}

	/**
	 * Extract tasks to do.
	 *
	 * @param <U>
	 * @param inNode         Node type to filter.
	 * @param scaleResources List of scale resources.
	 * @return A list of resources. or an empty list.
	 */
	private static <U> List<SclableResources<U>> toThingsToDo(final Class<? extends Node> inNode, final List<SclableResources<U>> scaleResources) {
		return scaleResources.stream()
				.filter(x -> x.getType() == inNode)
				// .filter(x -> (x.getHave() != x.getWant()))
				.toList();
	}

}
