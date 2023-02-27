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
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.ubiqube.etsi.mano.orchestrator;

import org.jgrapht.ListenableGraph;
import org.jgrapht.graph.DefaultListenableGraph;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ubiqube.etsi.mano.orchestrator.nodes.ConnectivityEdge;
import com.ubiqube.etsi.mano.orchestrator.uow.UnitOfWorkV3;
import com.ubiqube.etsi.mano.orchestrator.uow.UnitOfWorkVertexListenerV3;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTaskV3;

/**
 *
 * @author Olivier Vignaud <ovi@ubiqube.com>
 *
 */
public class GraphTools {

	private static final Logger LOG = LoggerFactory.getLogger(GraphTools.class);

	private GraphTools() {
		// Nothing.
	}

	public static <U> ListenableGraph<UnitOfWorkV3<U>, ConnectivityEdge<UnitOfWorkV3<U>>> createGraph() {
		// Vertex everyThing
		final ListenableGraph<UnitOfWorkV3<U>, ConnectivityEdge<UnitOfWorkV3<U>>> g = (ListenableGraph) (Object) new DefaultListenableGraph<>(new DirectedAcyclicGraph<>(ConnectivityEdge.class));
		g.addGraphListener(new UnitOfWorkVertexListenerV3<>());
		return g;
	}

	public static <U> ListenableGraph<UnitOfWorkV3<U>, ConnectivityEdge<UnitOfWorkV3<U>>> createGraphV3() {
		// Vertex everyThing
		final ListenableGraph<UnitOfWorkV3<U>, ConnectivityEdge<UnitOfWorkV3<U>>> g = (ListenableGraph) (Object) new DefaultListenableGraph<>(new DirectedAcyclicGraph<>(ConnectivityEdge.class));
		g.addGraphListener(new UnitOfWorkVertexListenerV3<>());
		return g;
	}

	public static <U> ListenableGraph<UnitOfWorkV3<U>, ConnectivityEdge<UnitOfWorkV3<U>>> revert(final ListenableGraph<UnitOfWorkV3<U>, ConnectivityEdge<UnitOfWorkV3<U>>> g) {
		final ListenableGraph<UnitOfWorkV3<U>, ConnectivityEdge<UnitOfWorkV3<U>>> gNew = createGraph();
		g.vertexSet().forEach(gNew::addVertex);
		g.edgeSet().forEach(x -> gNew.addEdge(x.getTarget(), x.getSource()));
		return gNew;
	}

	public static <U> void dumpV3(final ListenableGraph<UnitOfWorkV3<U>, ConnectivityEdge<UnitOfWorkV3<U>>> g) {
		g.vertexSet().forEach(x -> LOG.debug("v: {} of {}", x.getTask().getAlias(), x.getType().getSimpleName()));
		g.edgeSet().forEach(x -> LOG.debug("e: {} => {}", x.getSource().getTask().getAlias(), x.getTarget().getTask().getAlias()));
	}

	public static String toDotName(final UnitOfWorkV3<?> task) {
		final String base = task.getType().getSimpleName() + "_" + task.getTask().getName() + "_" + String.format("%04d", task.getTask().getRank());
		return cleanup(base);
	}

	private static String cleanup(final String base) {
		return base.replace("/", "_").replace("-", "_").replace("\n", "_").replace(",", "_").replace("(", "_").replace(")", "_").replace(" ", "_");
	}

	public static String toDotName(final VirtualTaskV3<?> task) {
		final String base = task.getType().getSimpleName() + "_" + task.getName() + "_" + String.format("%04d", task.getRank());
		return cleanup(base);
	}
}
