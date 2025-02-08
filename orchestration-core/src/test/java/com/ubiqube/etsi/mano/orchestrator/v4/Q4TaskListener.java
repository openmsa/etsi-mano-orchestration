package com.ubiqube.etsi.mano.orchestrator.v4;

import org.jspecify.annotations.Nullable;

public interface Q4TaskListener {
	/**
	 * Called before task start.
	 *
	 * @param uaow A Unit of work.
	 */
	void onStart(Q4Task uaow);

	/**
	 * Called after successful task termination.
	 *
	 * @param uaow The unit of works.
	 * @param res  Result of the task, mainly the allocated resource.
	 */
	void onTerminate(Q4Task uaow, @Nullable String res);

	/**
	 * Called if task failed.
	 *
	 * @param uaow The unit of work.
	 * @param e    The exception thrown by the task.
	 */
	void onError(Q4Task uaow, RuntimeException e);

}
