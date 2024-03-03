/**
 *     Copyright (C) 2019-2024 Ubiqube.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.ubiqube.etsi.mano.orchestrator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.ubiqube.etsi.mano.orchestrator.uow.UnitA;
import com.ubiqube.etsi.mano.orchestrator.uow.UnitB;
import com.ubiqube.etsi.mano.orchestrator.uow.UnitC;
import com.ubiqube.etsi.mano.orchestrator.uow.UnitOfWorkV3;

class SystemBuilderV3ImplTest {

	@Test
	void test() {
		final SystemBuilderV3Impl sb3 = new SystemBuilderV3Impl<>();
		final UnitA a = new UnitA(ManoSelector.of(getClass(), "A"));
		final UnitB b = new UnitB(ManoSelector.of(getClass(), "B"));
		final UnitC c = new UnitC(ManoSelector.of(getClass(), "C"));
		sb3.add(a, b);
		sb3.edge(b, c);
		final List v = sb3.getVertexV3();
		assertEquals(3, v.size());
	}

	@Test
	void test2() {
		final UnitA a = new UnitA(ManoSelector.of(getClass(), "A"));
		final UnitB b = new UnitB(ManoSelector.of(getClass(), "B"));
		final SystemBuilder<Object> sb3 = SystemBuilderV3Impl.of(a, b);
		final List<UnitOfWorkV3<Object>> v = sb3.getVertexV3();
		assertEquals(2, v.size());
	}

	@Test
	void test3() {
		final UnitA a = new UnitA(ManoSelector.of(getClass(), "A"));
		final SystemBuilder<Object> sb3 = SystemBuilderV3Impl.of(a);
		final List<UnitOfWorkV3<Object>> v = sb3.getVertexV3();
		assertEquals(1, v.size());
		assertEquals(a, sb3.getSingle());
	}
}
