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
package com.ubiqube.etsi.mano.orchestrator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.dexecutor.core.task.Task;
import com.github.dexecutor.core.task.TaskProvider;
import com.ubiqube.etsi.mano.orchestrator.uow.UnitOfWorkV3;

/**
 * An implementation for task delete.
 *
 * @author Olivier Vignaud {@literal <ovi@ubiqube.com>}
 *
 */
public class DeleteTaskProvider<U> implements TaskProvider<UnitOfWorkV3<U>, String> {

	private static final Logger LOG = LoggerFactory.getLogger(DeleteTaskProvider.class);
	private final Context3dNetFlow<U> context;
	private final OrchExecutionListener<U> listener;

	public DeleteTaskProvider(final Context3dNetFlow<U> context, final OrchExecutionListener<U> listener) {
		this.context = context;
		this.listener = listener;
	}

	@Override
	public Task<UnitOfWorkV3<U>, String> provideTask(final UnitOfWorkV3<U> uaow) {
		LOG.debug("Called with: {} {}", uaow.getVirtualTask().getClass(), uaow.getVirtualTask().getAlias());
		return new UowExecDeleteTask<>(listener, uaow, context);
	}

}
