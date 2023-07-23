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
import com.ubiqube.etsi.mano.orchestrator.entities.SystemConnections;
import com.ubiqube.etsi.mano.orchestrator.exceptions.OrchestrationException;
import com.ubiqube.etsi.mano.orchestrator.uow.UnitOfWorkV3;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTaskV3;
import com.ubiqube.etsi.mano.service.sys.SystemV3;

import jakarta.annotation.Nonnull;

/**
 *
 * @author olivier
 *
 */
@Service
public class ImplementationServiceV3<U> {
	@Nonnull
	private final SystemManager vimManager;
	@Nonnull
	private final Map<String, List<SystemV3<U>>> systems;
	@Nonnull
	private final OrchestrationServiceV3<U> orchestrationService;

	public ImplementationServiceV3(final List<SystemV3<U>> systems, final SystemManager vimManager, final OrchestrationServiceV3<U> orchestrationService) {
		this.vimManager = vimManager;
		this.systems = systems.stream().collect(Collectors.groupingBy(SystemV3::getVimType, Collectors.toList()));
		this.orchestrationService = orchestrationService;
	}

	public SystemBuilder<UnitOfWorkV3<U>> getTargetSystem(final VirtualTaskV3<U> virtualTask) {
		final String connectionId = virtualTask.getVimConnectionId();
		if (null == connectionId) {
			throw new OrchestrationException("Unable to find VimId: " + virtualTask.getVimConnectionId() + ", for task: " + virtualTask.getName());
		}
		final SystemConnections vim = vimManager.findVimByVimIdAndProviderId(connectionId, virtualTask.getType().getSimpleName());
		vim.getVimType();
		final List<SystemV3<U>> sysList = systems.get(virtualTask.getType().getSimpleName());
		final SystemV3<U> sys = getSimpleOrCrash(sysList, vim.getVimType());
		return sys.getImplementation(orchestrationService, virtualTask, vim);
	}

	private SystemV3<U> getSimpleOrCrash(final List<SystemV3<U>> sysList, final String vimType) {
		if (sysList.isEmpty()) {
			throw new OrchestrationException("No system for the given vim Type " + vimType);
		}
		return sysList.stream().filter(x -> x.getVimType().equals(vimType)).findFirst().orElseThrow(() -> new OrchestrationException("Unable to find " + vimType + "in " + sysList));
	}
}
