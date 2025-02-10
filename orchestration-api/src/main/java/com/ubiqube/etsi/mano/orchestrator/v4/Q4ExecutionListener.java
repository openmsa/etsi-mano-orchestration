package com.ubiqube.etsi.mano.orchestrator.v4;

import org.jspecify.annotations.Nullable;

import com.ubiqube.etsi.mano.orchestrator.uow.UnitOfWorkV3;

public interface Q4ExecutionListener<U extends Q4Task> {
	/**
	 * Called before task start.
	 *
	 * @param uaow A Unit of work.
	 */
	void onStart(UnitOfWorkV3<U> uaow);

	/**
	 * Called after successful task termination.
	 *
	 * @param uaow The unit of works.
	 * @param res  Result of the task, mainly the allocated resource.
	 */
	void onTerminate(UnitOfWorkV3<U> uaow, @Nullable String res);

	/**
	 * Called if task failed.
	 *
	 * @param uaow The unit of work.
	 * @param e    The exception thrown by the task.
	 */
	void onError(UnitOfWorkV3<U> uaow, RuntimeException e);

}
