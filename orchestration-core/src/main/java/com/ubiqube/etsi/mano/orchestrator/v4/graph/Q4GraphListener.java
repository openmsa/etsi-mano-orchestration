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
