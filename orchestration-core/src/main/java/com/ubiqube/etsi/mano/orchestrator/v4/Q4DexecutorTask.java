package com.ubiqube.etsi.mano.orchestrator.v4;

import com.github.dexecutor.core.task.Task;

public class Q4DexecutorTask extends Task<Q4Task, String> {

	private static final long serialVersionUID = 1L;
	private final transient Q4Task task;
	private final transient Q4Context context;

	public Q4DexecutorTask(final Q4Task task, final Q4Context context) {
		this.task = task;
		this.context = context;
	}

	@Override
	public String execute() {
		return task.execute(context);
	}

}
