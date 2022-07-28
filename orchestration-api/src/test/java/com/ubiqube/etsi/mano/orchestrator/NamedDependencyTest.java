/**
 *     Copyright (C) 2019-2020 Ubiqube.
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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.ubiqube.etsi.mano.orchestrator.NamedDependency;
import com.ubiqube.etsi.mano.orchestrator.nodes.vnfm.Compute;
import com.ubiqube.etsi.mano.orchestrator.nodes.vnfm.Network;

/**
 *
 * @author olivier
 *
 */
class NamedDependencyTest {

	private static final NamedDependency n1 = new NamedDependency(Compute.class, "name");

	@Test
	void test01() {
		final NamedDependency n2 = new NamedDependency(Network.class, "name");
		assertFalse(n1.match(n2));
	}

	@Test
	void test02() {
		final NamedDependency n2 = new NamedDependency(Compute.class, "namebad");
		assertFalse(n1.match(n2));
	}

	@Test
	void test03() {
		final NamedDependency n2 = new NamedDependency(Compute.class, "name");
		assertTrue(n1.match(n2));
		assertNotNull(n2.toString());
	}
}
