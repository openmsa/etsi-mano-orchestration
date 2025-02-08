package com.ubiqube.etsi.mano.orchestrator.v4;

import java.util.concurrent.ExecutorService;

/**
 * Interface representing a Q4 Workflow in the ETSI MANO orchestrator.
 */
public interface Q4Workflow {

	/**
	 * Runs the workflow using the provided executor service.
	 *
	 * @param executorService the executor service to run the workflow.
	 */
	Q4Results run(ExecutorService executorService);

	/**
	 * Adds a vertex to the graph. This is the way to add a single task to the
	 * graph.
	 *
	 * @param task the task to be added as a vertex.
	 */
	void addVertex(Q4Task task);

	/**
	 * Adds an edge between two vertices in the graph. There is no need to add the
	 * vertices first, as they will be added automatically if they do not exist
	 *
	 * @param from the starting task of the edge.
	 * @param to   the ending task of the edge.
	 */
	void addEdge(Q4Task from, Q4Task to);

}
