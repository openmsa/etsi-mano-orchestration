package com.ubiqube.etsi.mano.orchestrator.v4.graph;

import java.util.Objects;

import org.jspecify.annotations.NonNull;

import com.ubiqube.etsi.mano.orchestrator.v4.Q4Task;

public class Q4Edge {
	@NonNull
	private Q4Task source;
	@NonNull
	private Q4Task target;

	public Q4Edge() {
		// Need constructor for graph.
	}

	public Q4Edge(final Q4Task source, final Q4Task target) {
		this.source = Objects.requireNonNull(source);
		this.target = Objects.requireNonNull(target);
	}

	public Q4Task getSource() {
		return source;
	}

	public void setSource(final Q4Task source) {
		this.source = source;
	}

	public Q4Task getTarget() {
		return target;
	}

	public void setTarget(final Q4Task target) {
		this.target = target;
	}

	@Override
	public String toString() {
		return "Q4Edge [source=" + source + ", target=" + target + "]";
	}

}
