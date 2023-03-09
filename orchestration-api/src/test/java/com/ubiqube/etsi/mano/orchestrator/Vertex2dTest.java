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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.ubiqube.etsi.mano.orchestrator.nodes.vnfm.Compute;
import com.ubiqube.etsi.mano.orchestrator.nodes.vnfm.Network;

@SuppressWarnings("static-method")
class Vertex2dTest {

	@Test
	void testDefaultConstructor() throws Exception {
		final Vertex2d v = new Vertex2d();
		assertNull(v.getName());
		assertNull(v.getParent());
		assertNull(v.getType());
	}

	@Test
	void testConstructor() throws Exception {
		final Vertex2d v = new Vertex2d(Network.class, "name", null);
		assertEquals("name", v.getName());
		assertNull(v.getParent());
		assertEquals(Network.class, v.getType());
		assertNotNull(v.toString());
		v.hashCode();
	}

	@Test
	void testEqual() throws Exception {
		final Vertex2d v = new Vertex2d(Network.class, "name", null);
		v.equals(null);
		v.equals(v);
		v.equals("");
		final Vertex2d v2 = new Vertex2d(Network.class, "name", null);
		assertTrue(v.equals(v2));
		assertTrue(true);
	}

	@Test
	void testEqualName() throws Exception {
		final Vertex2d v = new Vertex2d(Network.class, "name", null);
		final Vertex2d v2 = new Vertex2d(Network.class, "name2", null);
		assertFalse(v.equals(v2));
	}

	@Test
	void testEqualType() throws Exception {
		final Vertex2d v = new Vertex2d(Network.class, "name", null);
		final Vertex2d v2 = new Vertex2d(Compute.class, "name", null);
		assertFalse(v.equals(v2));
	}

	@Test
	void testEqualParent() throws Exception {
		final Vertex2d v = new Vertex2d(Network.class, "name", null);
		final Vertex2d v2 = new Vertex2d(Compute.class, "name", v);
		assertNotNull(v2.toString());
		assertFalse(v.equals(v2));
	}

	@Test
	void testMatch() throws Exception {
		final Vertex2d v = new Vertex2d(Network.class, "name", null);
		assertTrue(v.match(Network.class, "name", null));
		assertFalse(v.match(Compute.class, "name", null));
		assertFalse(v.match(Network.class, "name2", null));
		assertFalse(v.match(Network.class, "name", v));
	}

}
