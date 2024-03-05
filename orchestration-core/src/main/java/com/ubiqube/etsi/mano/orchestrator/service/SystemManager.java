/**
 *     Copyright (C) 2019-2024 Ubiqube.
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

import org.springframework.stereotype.Service;

import com.ubiqube.etsi.mano.orchestrator.entities.SystemConnections;
import com.ubiqube.etsi.mano.orchestrator.entities.Systems;
import com.ubiqube.etsi.mano.orchestrator.exceptions.OrchestrationException;
import com.ubiqube.etsi.mano.orchestrator.repository.SystemJpa;

/**
 *
 * @author Olivier Vignaud {@literal <ovi@ubiqube.com>}
 *
 */
@Service
public class SystemManager {
	private final SystemJpa systemsJpa;

	public SystemManager(final SystemJpa systemsJpa) {
		this.systemsJpa = systemsJpa;
	}

	public SystemConnections findVimByVimIdAndProviderId(final String systemConnectionId, final String vimType) {
		final Systems sys = systemsJpa.findByVimId(systemConnectionId);
		if (null == sys) {
			throw new OrchestrationException("Could not find " + systemConnectionId + " for vim type: " + vimType);
		}
		return sys.getSubSystems().stream()
				.filter(x -> x.getModuleName().equals(vimType))
				.findFirst()
				.orElseThrow(() -> new OrchestrationException("Could not find " + vimType + " for system: " + systemConnectionId));
	}

}
