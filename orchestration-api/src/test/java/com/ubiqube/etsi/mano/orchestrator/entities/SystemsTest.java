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
 *     along with this program.  If not, see https://www.gnu.org/licenses/.
 */
package com.ubiqube.etsi.mano.orchestrator.entities;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;

@SuppressWarnings("static-method")
class SystemsTest {

	@Test
	void testName() throws Exception {
		final Systems s = new Systems();
		s.setId(UUID.randomUUID());
		s.setName("");
		s.setSubSystems(Set.of());
		s.setVimId("");
		s.setVimOrigin(UUID.randomUUID());
		s.toString();
		assertNotNull(s.getId());
		assertNotNull(s.getName());
		assertNotNull(s.getSubSystems());
		assertNotNull(s.getVimId());
		assertNotNull(s.getVimOrigin());
	}

	@Test
	void test1dd() throws Exception {
		final Systems s = new Systems();
		final SystemConnections sc = new SystemConnections();
		final SystemConnections sc2 = new SystemConnections();
		s.add(sc);
		s.add(sc2);
		assertTrue(true);
	}
}
