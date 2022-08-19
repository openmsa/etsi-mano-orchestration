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
package com.ubiqube.etsi.mano.orchestrator.context;

import java.util.List;

import org.jgrapht.ListenableGraph;

import com.ubiqube.etsi.mano.orchestrator.Context3d;
import com.ubiqube.etsi.mano.orchestrator.Context3dNetFlow;
import com.ubiqube.etsi.mano.orchestrator.nodes.ConnectivityEdge;
import com.ubiqube.etsi.mano.orchestrator.nodes.Node;
import com.ubiqube.etsi.mano.orchestrator.uow.UnitOfWorkV3;

public class SimplifiedContextImpl<U> implements Context3d<U> {

	private final Context3dNetFlow<U> flow;
	private final UnitOfWorkV3<U> actual;

	public SimplifiedContextImpl(final UnitOfWorkV3<U> actual, final ListenableGraph<UnitOfWorkV3<U>, ConnectivityEdge<UnitOfWorkV3<U>>> d) {
		this.flow = new Context3dNetFlow<>(d);
		this.actual = actual;
	}

	public SimplifiedContextImpl(final UnitOfWorkV3<U> actual, final Context3dNetFlow flow) {
		this.flow = flow;
		this.actual = actual;
	}

	@Override
	public String get(final Class<? extends Node> class1, final String toscaName) {
		return flow.get(actual, class1, toscaName);
	}

	@Override
	public List<String> getParent(final Class<? extends Node> class1, final String toscaName) {
		return flow.getParent(actual, class1, toscaName);
	}

	@Override
	public void add(final Class<? extends Node> class1, final String name, final String resourceId) {
		flow.add(actual, class1, name, resourceId);
	}

}
