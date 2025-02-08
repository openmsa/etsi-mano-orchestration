package com.ubiqube.etsi.mano.orchestrator.v4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Q4TestTask implements Q4Task {
	/** Logger. */
	private static final Logger LOG = LoggerFactory.getLogger(Q4TestTask.class);
	private final String message;

	public Q4TestTask(final String message) {
		this.message = message;
	}

	@Override
	public String execute(final Q4Context context3d) {
		LOG.info("Executing task: {}", message);
		return null;
	}

	@Override
	public String rollback(final Q4Context context3d) {
		LOG.info("Rollback task execution: {}", message);
		return null;
	}

}
