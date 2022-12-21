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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.jgrapht.ListenableGraph;
import org.jgrapht.graph.DefaultListenableGraph;
import org.jgrapht.graph.DirectedAcyclicGraph;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ubiqube.etsi.mano.controllers.VertexResult;
import com.ubiqube.etsi.mano.orchestrator.exceptions.OrchestrationException;
import com.ubiqube.etsi.mano.service.graph.Edge2d;
import com.ubiqube.etsi.mano.service.graph.GraphListener2d;
import com.ubiqube.etsi.mano.service.graph.Vertex2d;

public class OrchTestFactory {

	public static ListenableGraph<Vertex2d, Edge2d> createSr(final String path) throws StreamReadException, DatabindException, IOException {
		final ObjectMapper mapper = new ObjectMapper();
		final VertexResult r;
		try (InputStream is = OrchTestFactory.class.getClassLoader().getResourceAsStream("/ns-vnffg.json")) {
			r = mapper.readValue(is, VertexResult.class);
		}
		final ListenableGraph<Vertex2d, Edge2d> g = new DefaultListenableGraph<>(new DirectedAcyclicGraph<>(Edge2d.class));
		g.addGraphListener(new GraphListener2d());
		r.getVertices().forEach(x -> g.addVertex(x));
		r.getEdges().forEach(x -> {
			final Vertex2d src = find(g.vertexSet(), x.getSource());
			final Vertex2d tgt = find(g.vertexSet(), x.getTarget());
			Optional.ofNullable(g.addEdge(src, tgt)).ifPresent(y -> y.setRelation(x.getRelation()));
		});
		return g;
	}

	private static Vertex2d find(final Set<Vertex2d> vertexSet, final String source) {
		final List<Vertex2d> l = vertexSet.stream().filter(x -> x.toString().equals(source)).toList();
		if (l.size() != 1) {
			throw new OrchestrationException("source have " + l);
		}
		return l.get(0);
	}

}
