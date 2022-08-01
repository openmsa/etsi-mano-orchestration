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

import java.util.List;

import org.jgrapht.ListenableGraph;
import org.jgrapht.graph.DefaultListenableGraph;
import org.jgrapht.graph.DirectedAcyclicGraph;

import com.ubiqube.etsi.mano.orchestrator.exceptions.OrchestrationException;
import com.ubiqube.etsi.mano.orchestrator.nodes.Node;
import com.ubiqube.etsi.mano.orchestrator.uow.Relation;
import com.ubiqube.etsi.mano.service.graph.Edge2d;
import com.ubiqube.etsi.mano.service.graph.Vertex2d;

/**
 *
 * @author olivier
 *
 */
public class ScalingEngine {

	public ListenableGraph<Vertex2d, Edge2d> scale(final ListenableGraph<Vertex2d, Edge2d> g, final Class<? extends Node> clazz, final String name) {
		final List<Vertex2d> found = g.vertexSet().stream().filter(x -> (x.getType() == clazz) && (x.getName().equals(name))).toList();
		if (found.size() != 1) {
			throw new OrchestrationException("Bad number of match " + found.size() + ", when looking for :" + clazz.getSimpleName() + "/" + name);
		}
		final Vertex2d orig = found.get(0);
		final ListenableGraph<Vertex2d, Edge2d> d = new DefaultListenableGraph<>(new DirectedAcyclicGraph<>(Edge2d.class));
		d.addVertex(orig);
		handleVertex(orig, g, d);
		return d;
	}

	private void handleVertex(final Vertex2d orig, final ListenableGraph<Vertex2d, Edge2d> g, final ListenableGraph<Vertex2d, Edge2d> d) {
		g.outgoingEdgesOf(orig).forEach(x -> {
			final Vertex2d t = x.getTarget();
			if ((t.getRelation() == Relation.ONE_TO_ONE) || (t.getRelation() == Relation.MULTI)) {
				d.addVertex(t);
				d.addEdge(orig, t);
			} else {
				System.out.println("Ignored: " + t + "=> " + t.getRelation());
			}
		});
		g.incomingEdgesOf(orig).forEach(x -> {
			final Vertex2d s = x.getSource();
			if ((s.getRelation() == Relation.ONE_TO_ONE) || (s.getRelation() == Relation.MULTI)) {
				d.addVertex(s);
				d.addEdge(s, orig);
			} else {
				System.out.println("Ignored: " + s + "=> " + s.getRelation());
			}
		});
	}
}
