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

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.dexecutor.core.task.Task;
import com.github.dexecutor.core.task.TaskProvider;
import com.ubiqube.etsi.mano.orchestrator.uow.UnitOfWorkV3;

import org.jspecify.annotations.NonNull;

/**
 *
 * @author Olivier Vignaud {@literal <ovi@ubiqube.com>}
 *
 */
public class CreateTaskProvider<U> implements TaskProvider<UnitOfWorkV3<U>, String> {

	private static final Logger LOG = LoggerFactory.getLogger(CreateTaskProvider.class);
	@NonNull
	private final Context3dNetFlow<U> context;
	@NonNull
	private final OrchExecutionListener<U> listener;

	public CreateTaskProvider(final Context3dNetFlow<U> context, final OrchExecutionListener<U> listener) {
		this.context = Objects.requireNonNull(context);
		this.listener = Objects.requireNonNull(listener);
	}

	@Override
	public Task<UnitOfWorkV3<U>, String> provideTask(final UnitOfWorkV3<U> uaow) {
		LOG.debug("Called with: {}", uaow);
		return new UowExecCreateTask<>(listener, uaow, context);
	}

}
