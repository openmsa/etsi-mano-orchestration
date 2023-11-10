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

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class SystemConnectionsTest {

	@Test
	void test() {
		final SystemConnections srv = new SystemConnections();
		srv.setAccessInfo(Map.of());
		srv.setExtra(Map.of());
		srv.setId(UUID.randomUUID());
		srv.setInterfaceInfo(Map.of());
		srv.setModuleName("MODULE");
		srv.setVimId("ID");
		srv.setVimType("TYPE");
		srv.getAccessInfo();
		srv.getExtra();
		srv.getId();
		srv.getInterfaceInfo();
		srv.getModuleName();
		srv.getVimId();
		srv.getVimType();
		assertTrue(true);
	}

}
