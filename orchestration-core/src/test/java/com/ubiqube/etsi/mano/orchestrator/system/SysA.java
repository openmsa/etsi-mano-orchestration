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
package com.ubiqube.etsi.mano.orchestrator.system;

import com.ubiqube.etsi.mano.orchestrator.OrchestrationServiceV3;
import com.ubiqube.etsi.mano.orchestrator.SystemBuilder;
import com.ubiqube.etsi.mano.orchestrator.SystemBuilderV3Impl;
import com.ubiqube.etsi.mano.orchestrator.entities.SystemConnections;
import com.ubiqube.etsi.mano.orchestrator.nodes.Node;
import com.ubiqube.etsi.mano.orchestrator.uow.UnitA;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTaskV3;
import com.ubiqube.etsi.mano.service.sys.SystemV3;

public class SysA implements SystemV3<Object> {

	@Override
	public SystemBuilder getImplementation(final OrchestrationServiceV3 orchestrationService, final VirtualTaskV3 virtualTask, final SystemConnections vim) {
		return SystemBuilderV3Impl.of(new UnitA(virtualTask.getName(), virtualTask.getType()));
	}

	@Override
	public Class<? extends Node> getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getVimType() {
		// TODO Auto-generated method stub
		return null;
	}

}
