package com.ubiqube.etsi.mano.orchestrator.v4;

import com.github.dexecutor.core.task.Task;
import com.github.dexecutor.core.task.TaskProvider;

public class Q4TaskProvider implements TaskProvider<Q4Task, String> {

	private final Q4Context context;

	Q4TaskProvider(final Q4Context context) {
		this.context = context;
	}

	@Override
	public Task<Q4Task, String> provideTask(final Q4Task id) {
		return new Q4DexecutorTask(id, context);
	}

}
