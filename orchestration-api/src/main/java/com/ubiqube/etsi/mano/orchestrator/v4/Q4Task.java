package com.ubiqube.etsi.mano.orchestrator.v4;

public interface Q4Task {
	/**
	 * Run the task.
	 *
	 * @return Resource allocated.
	 */
	String execute(Q4Context context3d);

	/**
	 * Rollback the task.
	 *
	 * @return Nothing.
	 */
	String rollback(Q4Context context3d);
}
