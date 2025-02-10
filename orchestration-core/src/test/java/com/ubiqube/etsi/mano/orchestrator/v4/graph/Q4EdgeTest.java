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
package com.ubiqube.etsi.mano.orchestrator.v4.graph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.ubiqube.etsi.mano.orchestrator.v4.Q4Task;
import com.ubiqube.etsi.mano.orchestrator.v4.Q4TestTask;

class Q4EdgeTest {

	@Test
	void constructorInitializesSourceAndTarget() {
		Q4Task source = new Q4TestTask("");
		Q4Task target = new Q4TestTask("");
		Q4Edge edge = new Q4Edge(source, target);

		assertEquals(source, edge.getSource());
		assertEquals(target, edge.getTarget());
	}

	@Test
	void setSourceUpdatesSource() {
		Q4Task source = new Q4TestTask("");
		Q4Edge edge = new Q4Edge();
		edge.setSource(source);

		assertEquals(source, edge.getSource());
	}

	@Test
	void setTargetUpdatesTarget() {
		Q4Task target = new Q4TestTask("");
		Q4Edge edge = new Q4Edge();
		edge.setTarget(target);

		assertEquals(target, edge.getTarget());
	}

	@Test
	void toStringReturnsCorrectFormat() {
		Q4Task source = new Q4TestTask("");
		Q4Task target = new Q4TestTask("");
		Q4Edge edge = new Q4Edge(source, target);

		String expected = "Q4Edge [source=" + source + ", target=" + target + "]";
		assertEquals(expected, edge.toString());
	}

	@Test
	void constructorThrowsExceptionWhenSourceIsNull() {
		Q4Task target = new Q4TestTask("");
		assertThrows(NullPointerException.class, () -> new Q4Edge(null, target));
	}

	@Test
	void constructorThrowsExceptionWhenTargetIsNull() {
		Q4Task source = new Q4TestTask("");
		assertThrows(NullPointerException.class, () -> new Q4Edge(source, null));
	}
}
