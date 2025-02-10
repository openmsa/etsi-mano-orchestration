package com.ubiqube.etsi.mano.orchestrator.v4;

import com.github.dexecutor.core.task.Task;
import com.github.dexecutor.core.task.TaskProvider;

/**
 * Wrapper class for providing tasks in the Q4 context.
 */
public class Q4ProviderWrapper implements TaskProvider<Q4Task, String> {

	private final Q4Context context;
	private final Q4TaskProvider<Q4Task> provider;

	/**
	 * Constructs a Q4ProviderWrapper with the specified context and task provider.
	 *
	 * @param context  the Q4 context
	 * @param provider the task provider
	 */
	Q4ProviderWrapper(final Q4Context context, final Q4TaskProvider<Q4Task> provider) {
		this.context = context;
		this.provider = provider;
	}

	/**
	 * Provides a task for the given Q4 task ID.
	 *
	 * @param id the Q4 task ID
	 * @return the provided task
	 */
	@Override
	public Task<Q4Task, String> provideTask(final Q4Task id) {
		Q4Task tsk = provider.provideTask(id, context);
		return new Q4DexecutorTask(tsk, context);
	}

}
