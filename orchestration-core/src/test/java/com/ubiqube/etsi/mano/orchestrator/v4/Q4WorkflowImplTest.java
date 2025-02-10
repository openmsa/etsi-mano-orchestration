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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

class Q4WorkflowImplTest {

	private Q4WorkflowImpl workflow;

	private final ExecutorService executorService = Executors.newFixedThreadPool(10);

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		workflow = new Q4WorkflowImpl();
	}

	@Test
	void testAddVertex() {
		Q4Task task = new Q4TestTask("task1");
		workflow.addVertex(task);
//			assertTrue(workflow.graph.containsVertex(task));
	}

	@Test
	void testAddEdge() {
		Q4Task task1 = new Q4TestTask("task1");
		Q4Task task2 = new Q4TestTask("task2");
		workflow.addVertex(task1);
		workflow.addVertex(task2);
		workflow.addEdge(task1, task2);
//			assertTrue(workflow.graph.containsEdge(task1, task2));
	}

	@Test
	void testRun() {
		Q4Task task1 = new Q4TestTask("task1");
		Q4Task task2 = new Q4TestTask("task2");
		workflow.addEdge(task1, task2);

		Q4Results results = workflow.run(executorService);
		assertNotNull(results);
		assertEquals(2, results.getResults().size());
		assertEquals(2, results.getSucess().size());
		assertTrue(results.getErrors().isEmpty());
	}

	@Test
	void testProvider() {
		Q4TaskProviderImpl provider = new Q4TaskProviderImpl(null);
		Q4Task task = new Q4TestTask("task1");
		provider.provideTask(task);
	}

	@Test
	void testQ4TaskProvider() {
		Q4Task task = new Q4TestTask("task1");
		Q4Context context = null;
		new Q4DexecutorTask(task, context);
	}
}
