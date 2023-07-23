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
package com.ubiqube.etsi.mano.orchestrator;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.ubiqube.etsi.mano.orchestrator.nodes.vnfm.Network;

@SuppressWarnings("static-method")
class ContextHolderTest {

	@Test
	void testEmptyConstructor() throws Exception {
		final ContextHolder ch = new ContextHolder();
		assertNull(ch.getLiveInstanceId());
		assertNull(ch.getName());
		assertNull(ch.getResourceId());
		assertNull(ch.getType());
		assertNull(ch.getVimConnectionId());
		ch.setLiveInstanceId(UUID.randomUUID());
		ch.setName("");
		ch.setRank(123);
		ch.setResourceId("");
		ch.setType(Network.class);
		ch.setVimConnectionId("");
	}

	@Test
	void testConstructor() throws Exception {
		final ContextHolder ch = new ContextHolder(null, null, null, 0, null, null);
		assertNotNull(ch);
	}
}
