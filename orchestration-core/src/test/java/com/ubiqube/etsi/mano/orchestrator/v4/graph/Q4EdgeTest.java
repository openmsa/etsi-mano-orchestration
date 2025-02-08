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
