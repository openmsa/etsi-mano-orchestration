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

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.jgrapht.ListenableGraph;
import org.jgrapht.graph.DefaultListenableGraph;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ubiqube.etsi.mano.orchestrator.exceptions.OrchestrationException;
import com.ubiqube.etsi.mano.orchestrator.nodes.Node;
import com.ubiqube.etsi.mano.service.graph.Edge2d;
import com.ubiqube.etsi.mano.service.graph.GraphListener2d;
import com.ubiqube.etsi.mano.service.graph.Relation;
import com.ubiqube.etsi.mano.service.graph.Vertex2d;

/**
 *
 * @author olivier
 *
 */
public class ScalingEngine {
	private static final Logger LOG = LoggerFactory.getLogger(ScalingEngine.class);

	public ListenableGraph<Vertex2d, Edge2d> scale(final ListenableGraph<Vertex2d, Edge2d> g, final Class<? extends Node> clazz, final String name) {
		final List<Vertex2d> found = g.vertexSet().stream().filter(x -> (x.getType() == clazz) && (x.getName().equals(name))).toList();
		if (found.size() != 1) {
			throw new OrchestrationException("Bad number of match " + found.size() + ", when looking for :" + clazz.getSimpleName() + "/" + name);
		}
		final Vertex2d orig = found.get(0);
		final ListenableGraph<Vertex2d, Edge2d> d = new DefaultListenableGraph<>(new DirectedAcyclicGraph<>(Edge2d.class));
		d.addGraphListener(new GraphListener2d());
		d.addVertex(orig);
		handleVertex(orig, g, d, new HashSet<>());
		return d;
	}

	private void handleVertex(final Vertex2d orig, final ListenableGraph<Vertex2d, Edge2d> g, final ListenableGraph<Vertex2d, Edge2d> d, final HashSet<Vertex2d> cache) {
		if (cache.contains(orig)) {
			return;
		}
		cache.add(orig);
		g.outgoingEdgesOf(orig).forEach(x -> {
			final Vertex2d t = x.getTarget();
			if ((x.getRelation() == Relation.ONE_TO_ONE) || (x.getRelation() == Relation.MULTI)) {
				d.addVertex(t);
				Optional.ofNullable(d.addEdge(orig, t)).ifPresent(y -> y.setRelation(x.getRelation()));
				handleVertex(t, g, d, cache);
				cache.add(t);
			} else {
				LOG.trace("Ignored: {} => {}", vertex2Log(t), x.getRelation());
			}
		});
		g.incomingEdgesOf(orig).forEach(x -> {
			final Vertex2d s = x.getSource();
			if ((x.getRelation() == Relation.ONE_TO_ONE) || (x.getRelation() == Relation.MULTI)) {
				d.addVertex(s);
				Optional.ofNullable(d.addEdge(s, orig)).ifPresent(y -> y.setRelation(x.getRelation()));
				handleVertex(s, g, d, cache);
				cache.add(s);
			} else {
				LOG.trace("Ignored: {} => {}", vertex2Log(s), x.getRelation());
			}
		});
	}

	private static String vertex2Log(final Vertex2d v) {
		return v.getName() + " " + v.getType().getSimpleName();
	}
}
