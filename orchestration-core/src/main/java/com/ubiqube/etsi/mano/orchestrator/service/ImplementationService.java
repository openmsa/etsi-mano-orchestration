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
package com.ubiqube.etsi.mano.orchestrator.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ubiqube.etsi.mano.orchestrator.OrchestrationServiceV3;
import com.ubiqube.etsi.mano.orchestrator.SystemBuilder;
import com.ubiqube.etsi.mano.orchestrator.SystemBuilderV3Impl;
import com.ubiqube.etsi.mano.orchestrator.entities.SystemConnections;
import com.ubiqube.etsi.mano.orchestrator.exceptions.OrchestrationException;
import com.ubiqube.etsi.mano.orchestrator.scale.ContextVt;
import com.ubiqube.etsi.mano.orchestrator.uow.ContextUow;
import com.ubiqube.etsi.mano.orchestrator.uow.UnitOfWorkV3;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTaskV3;
import com.ubiqube.etsi.mano.service.sys.SystemV3;

/**
 *
 * @author Olivier Vignaud {@literal <ovi@ubiqube.com>}
 *
 */
@Service
public class ImplementationService<U> {
	private final Map<String, List<SystemV3<U>>> systemsV3;
	private final SystemManager vimManager;
	private final OrchestrationServiceV3<U> orchestrationServicev3;

	public ImplementationService(final SystemManager vimManager, final OrchestrationServiceV3<U> orchestrationServicev3, final List<SystemV3<U>> systemsv3) {
		systemsV3 = systemsv3.stream().collect(Collectors.groupingBy(this::buildKey, Collectors.toList()));
		this.vimManager = vimManager;
		this.orchestrationServicev3 = orchestrationServicev3;
	}

	public SystemBuilder<UnitOfWorkV3<U>> getTargetSystem(final VirtualTaskV3<U> virtualTask) {
		if (virtualTask instanceof final ContextVt<U> cvt) {
			return SystemBuilderV3Impl.of(new ContextUow(cvt));
		}
		final String connectionId = virtualTask.getVimConnectionId();
		if (null == connectionId) {
			throw new OrchestrationException("Unable to find VimId: " + virtualTask.getVimConnectionId() + ", for task: " + virtualTask.getName());
		}
		final SystemConnections vim = vimManager.findVimByVimIdAndProviderId(connectionId, virtualTask.getType().getSimpleName());
		final List<SystemV3<U>> sys = systemsV3.get(buildKey(virtualTask));
		if (null == sys) {
			throw new OrchestrationException("Unable to find system matching: " + vim.getVimType() + "/" + connectionId + " / " + virtualTask.getType().getSimpleName());
		}
		final List<SystemV3<U>> usys = sys.stream().filter(x -> x.getVimType().equals(vim.getVimType())).toList();
//		if (usys.size() != 1) {
//			throw new OrchestrationException("Unique system of " + vim.getVimId() + "/" + virtualTask.getType() + ", must be uniq but was " + usys.size());
//		}
		return usys.get(0).getImplementation(orchestrationServicev3, virtualTask, vim);
	}

	private String buildKey(final VirtualTaskV3<U> virtualTask) {
		return virtualTask.getType().getSimpleName();
	}

	private String buildKey(final SystemV3<U> sv) {
		return sv.getType().getSimpleName();
	}

}
