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
 *     along with this program.  If not, see https://www.gnu.org/licenses/.
 */
package com.ubiqube.etsi.mano.orchestrator.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

@SuppressWarnings("static-method")
class SystemTypeTest {

	@Test
	void testFromNull() throws Exception {
		final SystemType st = SystemType.fromValue(null);
		assertNull(st);
	}

	@Test
	void testFromBad() throws Exception {
		final SystemType st = SystemType.fromValue("Bad");
		assertNull(st);
	}

	@Test
	void testFromNetwork() throws Exception {
		final SystemType st = SystemType.fromValue("NETWORK");
		assertNotNull(st);
		assertEquals(SystemType.NETWORK, st);
		st.toString();
	}
}
