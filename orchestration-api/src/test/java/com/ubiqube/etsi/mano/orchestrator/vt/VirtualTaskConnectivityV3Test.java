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
package com.ubiqube.etsi.mano.orchestrator.vt;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.ubiqube.etsi.mano.orchestrator.Relation;

class VirtualTaskConnectivityV3Test {

	@Test
	void test() {
		final VirtualTaskV3<Object> vts = new TestVt();
		final VirtualTaskV3<Object> vtt = new TestVt();
		VirtualTaskConnectivityV3<Object> srv = new VirtualTaskConnectivityV3<>(vts, vtt, Relation.MANY_TO_ONE);
		srv.setRelation(null);
		srv.setSource(vtt);
		srv.setTarget(vts);
		srv.getRelation();
		srv.getSource();
		srv.getTarget();
		assertTrue(true);
		assertNotNull(srv.toString());
		srv = new VirtualTaskConnectivityV3<>();
	}

}
