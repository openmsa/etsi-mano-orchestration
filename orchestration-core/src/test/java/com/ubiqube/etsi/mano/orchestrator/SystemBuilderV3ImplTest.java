package com.ubiqube.etsi.mano.orchestrator;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.ubiqube.etsi.mano.orchestrator.uow.ANode;
import com.ubiqube.etsi.mano.orchestrator.uow.BNode;
import com.ubiqube.etsi.mano.orchestrator.uow.UnitA;
import com.ubiqube.etsi.mano.orchestrator.uow.UnitB;

class SystemBuilderV3ImplTest {

	@Test
	void test() {
		SystemBuilderV3Impl<Object> sb = (SystemBuilderV3Impl<Object>) SystemBuilderV3Impl.of(new UnitB("unitB", BNode.class), new UnitA("unitA", ANode.class));
		assertNotNull(sb);
		sb.add(new UnitA("unitA", ANode.class), new UnitB("unitB", BNode.class));
		sb.edge(new UnitA("unitA", ANode.class), new UnitB("unitB", BNode.class));
		sb.getSingle();
	}

}
