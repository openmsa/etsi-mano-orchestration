package com.ubiqube.etsi.mano.orchestrator.v4;

import com.github.dexecutor.core.task.Task;

public class Q4DexecutorTaskListener extends Task<Q4Task, String> {

	private static final long serialVersionUID = 1L;
	private final transient Q4Task task;
	private final transient Q4Context context;
	private final Q4ExecutionListener<Q4Task> listener;

	public Q4DexecutorTaskListener(final Q4Task task, final Q4Context context, final Q4ExecutionListener<Q4Task> listener) {
		this.task = task;
		this.context = context;
		this.listener = listener;
	}

	@Override
	public String execute() {
		return task.execute(context);
	}

}
