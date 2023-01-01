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
package com.ubiqube.etsi.mano.orchestrator.v3;

import java.util.List;
import java.util.function.Function;

import org.jgrapht.ListenableGraph;

import com.ubiqube.etsi.mano.orchestrator.ContextHolder;
import com.ubiqube.etsi.mano.orchestrator.Edge2d;
import com.ubiqube.etsi.mano.orchestrator.SclableResources;
import com.ubiqube.etsi.mano.orchestrator.Vertex2d;
import com.ubiqube.etsi.mano.orchestrator.nodes.Node;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTaskV3;

/**
 *
 * @author olivier
 *
 */
public interface BlueprintBuilder {

	<U> PreExecutionGraphV3<U> buildPlan(final List<SclableResources<U>> scaleResources, final ListenableGraph<Vertex2d, Edge2d> g,
			Function<U, VirtualTaskV3<U>> converter, List<ContextHolder> liveItems, List<Class<? extends Node>> masterVertex);

}
