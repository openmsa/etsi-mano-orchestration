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
package com.ubiqube.etsi.mano.orchestrator.system;

import com.ubiqube.etsi.mano.orchestrator.OrchestrationService;
import com.ubiqube.etsi.mano.orchestrator.OrchestrationServiceV3;
import com.ubiqube.etsi.mano.orchestrator.SystemBuilder;
import com.ubiqube.etsi.mano.orchestrator.SystemBuilderImpl;
import com.ubiqube.etsi.mano.orchestrator.entities.SystemConnections;
import com.ubiqube.etsi.mano.orchestrator.nodes.Node;
import com.ubiqube.etsi.mano.orchestrator.uow.UnitB;
import com.ubiqube.etsi.mano.orchestrator.uow.UnitC;
import com.ubiqube.etsi.mano.orchestrator.uow.UnitOfWorkV3;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTask;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTaskV3;
import com.ubiqube.etsi.mano.service.sys.System;

public class SysB implements System<Object> {

	@Override
	public String getProviderId() {
		return "PROVB";
	}

	@Override
	public SystemBuilder getImplementation(final OrchestrationService orchestrationService, final VirtualTask virtualTask, final SystemConnections vim) {
		return SystemBuilderImpl.of(new UnitB(), new UnitC());
	}

	@Override
	public Class<? extends Node> getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SystemBuilder<UnitOfWorkV3<Object>> getImplementation(final OrchestrationServiceV3<Object> orchestrationServicev3, final VirtualTaskV3<Object> virtualTask, final SystemConnections vim) {
		// TODO Auto-generated method stub
		return null;
	}

}
