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
package com.ubiqube.etsi.mano.orchestrator.uow;

import java.util.Objects;

import org.jgrapht.event.GraphEdgeChangeEvent;
import org.jgrapht.event.GraphListener;
import org.jgrapht.event.GraphVertexChangeEvent;

import com.ubiqube.etsi.mano.orchestrator.nodes.ConnectivityEdge;

import org.jspecify.annotations.Nullable;

public class UnitOfWorkVertexListenerV3<U> implements GraphListener<UnitOfWorkV3<U>, ConnectivityEdge<UnitOfWorkV3<U>>> {

	@Override
	public void vertexAdded(final @Nullable GraphVertexChangeEvent<UnitOfWorkV3<U>> e) {
		// Nothing.
	}

	@Override
	public void vertexRemoved(final @Nullable GraphVertexChangeEvent<UnitOfWorkV3<U>> e) {
		// Nothing.

	}

	@Override
	public void edgeAdded(final @Nullable GraphEdgeChangeEvent<UnitOfWorkV3<U>, ConnectivityEdge<UnitOfWorkV3<U>>> e) {
		Objects.requireNonNull(e);
		final ConnectivityEdge<UnitOfWorkV3<U>> edge = e.getEdge();
		edge.setSource(e.getEdgeSource());
		edge.setTarget(e.getEdgeTarget());
	}

	@Override
	public void edgeRemoved(final @Nullable GraphEdgeChangeEvent<UnitOfWorkV3<U>, ConnectivityEdge<UnitOfWorkV3<U>>> e) {
		// Nothing.
	}

}
