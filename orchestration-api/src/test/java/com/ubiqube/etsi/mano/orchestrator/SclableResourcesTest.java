/**
 *     Copyright (C) 2019-2023 Ubiqube.
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
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.ubiqube.etsi.mano.orchestrator.nodes.vnfm.Network;

class SclableResourcesTest {

	@Test
	void testContructor() throws Exception {
		final SclableResources<Object> obj = new SclableResources<>(Network.class, "name", 0, 1, getClass());
		assertNotNull(obj.toString());
	}

	@Test
	void testOf() throws Exception {
		final SclableResources<Object> obj = SclableResources.of(Network.class, "name", 0, 1, getClass());
		assertNotNull(obj.toString());
		assertEquals(Network.class, obj.getType());
		assertEquals("name", obj.getName());
		assertEquals(0, obj.getHave());
		assertEquals(1, obj.getWant());
		assertEquals(getClass(), obj.getTemplateParameter());
	}
}
