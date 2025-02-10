/**
 * Copyright (C) 2019-2025 Ubiqube.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
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
