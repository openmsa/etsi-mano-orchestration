package com.ubiqube.etsi.mano.orchestrator.v4;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestTask implements Q4Task {
	/** Logger. */
	private static final Logger LOG = LoggerFactory.getLogger(TestTask.class);
	private final String message;

	public TestTask(final String message) {
		this.message = message;
	}

	@Override
	public String execute(final Q4Context context3d) {
		LOG.info("Executing task: {}", message);
		return UUID.randomUUID().toString();
	}

	@Override
	public String rollback(final Q4Context context3d) {
		// TODO Auto-generated method stub
		return null;
	}

}
