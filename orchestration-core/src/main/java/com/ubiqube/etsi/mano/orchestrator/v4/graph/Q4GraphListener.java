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
package com.ubiqube.etsi.mano.orchestrator.v4.graph;

import java.util.Objects;

import org.jgrapht.event.GraphEdgeChangeEvent;
import org.jgrapht.event.GraphListener;
import org.jgrapht.event.GraphVertexChangeEvent;

import com.ubiqube.etsi.mano.orchestrator.v4.Q4Task;

public class Q4GraphListener implements GraphListener<Q4Task, Q4Edge> {

	@Override
	public void vertexAdded(final GraphVertexChangeEvent<Q4Task> e) {
		//
	}

	@Override
	public void vertexRemoved(final GraphVertexChangeEvent<Q4Task> e) {
		//
	}

	@Override
	public void edgeAdded(final GraphEdgeChangeEvent<Q4Task, Q4Edge> e) {
		Objects.requireNonNull(e);
		final Q4Edge edge = e.getEdge();
		edge.setSource(e.getEdgeSource());
		edge.setTarget(e.getEdgeTarget());
	}

	@Override
	public void edgeRemoved(final GraphEdgeChangeEvent<Q4Task, Q4Edge> e) {
		//
	}

}
