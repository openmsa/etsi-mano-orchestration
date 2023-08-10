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

import org.junit.jupiter.api.Test;

import com.ubiqube.etsi.mano.orchestrator.nodes.vnfm.Network;

@SuppressWarnings("static-method")
class Edge2dTest {

	@Test
	void testName() throws Exception {
		final Edge2d obj = new Edge2d();
		obj.setRelation(Relation.MANY_TO_ONE);
		obj.setSource(new Vertex2d(Network.class, "name", null));
		obj.setTarget(new Vertex2d(Network.class, "name2", null));
		obj.toString();
		assertNotNull(obj.getRelation());
		obj.getSource();
		obj.getTarget();
	}

	@Test
	void testRelation001() throws Exception {
		final Edge2d obj = new Edge2d();
		obj.setRelation(Relation.MULTI);
		obj.setSource(new Vertex2d(Network.class, "name", null));
		obj.setTarget(new Vertex2d(Network.class, "name2", null));
		assertNotNull(obj.toString());
	}

	@Test
	void testRelation002() throws Exception {
		final Edge2d obj = new Edge2d();
		obj.setRelation(Relation.NONE);
		obj.setSource(new Vertex2d(Network.class, "name", null));
		obj.setTarget(new Vertex2d(Network.class, "name2", null));
		assertNotNull(obj.toString());
	}

	@Test
	void testRelation003() throws Exception {
		final Edge2d obj = new Edge2d();
		obj.setRelation(Relation.ONE_TO_MANY);
		obj.setSource(new Vertex2d(Network.class, "name", null));
		obj.setTarget(new Vertex2d(Network.class, "name2", null));
		assertNotNull(obj.toString());
	}

	@Test
	void testRelation004() throws Exception {
		final Edge2d obj = new Edge2d();
		obj.setRelation(Relation.ONE_TO_ONE);
		obj.setSource(new Vertex2d(Network.class, "name", null));
		obj.setTarget(new Vertex2d(Network.class, "name2", null));
		assertNotNull(obj.toString());
	}
}
