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
package com.ubiqube.etsi.mano.orchestrator;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.jgrapht.GraphPath;
import org.jgrapht.ListenableGraph;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.graph.DefaultListenableGraph;
import org.jgrapht.graph.DirectedAcyclicGraph;

import com.ubiqube.etsi.mano.orchestrator.context.VertexContext;
import com.ubiqube.etsi.mano.orchestrator.context.VertexContextEdge;
import com.ubiqube.etsi.mano.orchestrator.nodes.Node;
import com.ubiqube.etsi.mano.orchestrator.uow.UnitOfWork;
import com.ubiqube.etsi.mano.service.graph.Edge2d;
import com.ubiqube.etsi.mano.service.graph.Vertex2d;

/**
 *
 * @author olivier
 *
 */
public class Context3dImpl implements Context3d {
	private final ListenableGraph<VertexContext, VertexContextEdge> d = new DefaultListenableGraph<>(new DirectedAcyclicGraph<>(VertexContextEdge.class));

	private VertexContext actual;

	private VertexContext source;

	public Context3dImpl(final ListenableGraph<Vertex2d, Edge2d> origin) {
		final Set<Vertex2d> cache = new HashSet<>();
		origin.edgeSet().forEach(x -> {
			final Vertex2d src = x.getSource();
			final VertexContext vc;
			if (!cache.contains(src)) {
				cache.add(src);
				vc = new VertexContext(src);
			} else {
				vc = findVertex(src);
			}

			final Vertex2d dst = x.getTarget();
			final VertexContext vcTgt;
			if (!cache.contains(dst)) {
				cache.add(dst);
				vcTgt = new VertexContext(dst);
			} else {
				vcTgt = findVertex(dst);
			}
			d.addEdge(vc, vcTgt);
		});
		makeStartNode();
	}

	public Context3dImpl() {
		//
	}

	private void makeStartNode() {
		final List<VertexContext> lv = d.vertexSet().stream().filter(x -> d.incomingEdgesOf(x).isEmpty()).toList();
		actual = new VertexContext(new Vertex2d(Node.class, "start", null));
		lv.forEach(x -> d.addEdge(actual, x));
	}

	public void setActual(final VertexContext actual) {
		this.actual = actual;
	}

	private VertexContext findVertex(final Vertex2d src) {
		return d.vertexSet().stream().filter(x -> x.getOrig() == src).findFirst().orElseThrow();
	}

	@Override
	public List<String> getParent(final Class<? extends Node> class1, final String toscaName) {
		Objects.requireNonNull(actual, "actual could not be null");
		final AllDirectedPaths<VertexContext, VertexContextEdge> path = new AllDirectedPaths<>(d);
		final List<GraphPath<VertexContext, VertexContextEdge>> paths = path.getAllPaths(source, actual, true, 1000);
		return paths.stream()
				.flatMap(x -> x.getVertexList().stream())
				.filter(x -> x.getOrig().getType() == class1)
				.filter(x -> x.getOrig().getName().equals(toscaName))
				.map(VertexContext::getResource)
				.toList();
	}

	@Override
	public void add(final Class<? extends Node> class1, final String name, final String resourceId) {
		Objects.requireNonNull(actual, "Actual must not be null");
		final VertexContext v = new VertexContext(new Vertex2d(class1, name, null));
		v.setResource(resourceId);
		d.addVertex(v);
		d.incomingEdgesOf(actual).forEach(x -> {
			d.addEdge(x.getSource(), v);
		});
		d.outgoingEdgesOf(v).forEach(x -> {
			d.addEdge(v, x.getTarget());
		});
	}

	@Override
	public <U> void add(final UnitOfWork<U> uaow, final String res) {
		// TODO Auto-generated method stub

	}

	@Override
	public String get(final Class<? extends Node> class1, final String toscaName) {
		// TODO Auto-generated method stub
		return null;
	}
}
