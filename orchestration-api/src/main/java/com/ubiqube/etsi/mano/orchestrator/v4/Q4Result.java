package com.ubiqube.etsi.mano.orchestrator.v4;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Q4Result {
	private Q4Task id;
	private String result;
	private Q4ResultStatus status;
	private String message;

	/**
	 * start time for the task
	 */
	private LocalDateTime startTime;
	/**
	 * End time for the task
	 */
	private LocalDateTime endTime;

}
