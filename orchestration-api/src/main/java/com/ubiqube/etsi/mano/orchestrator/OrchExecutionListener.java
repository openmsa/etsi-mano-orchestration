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
package com.ubiqube.etsi.mano.orchestrator;

import com.ubiqube.etsi.mano.orchestrator.uow.UnitOfWorkV3;

import jakarta.annotation.Nullable;

/**
 * Execution callback, called every start/stop/error of tasks.
 *
 * @author Olivier Vignaud {@literal <ovi@ubiqube.com>}
 *
 */
public interface OrchExecutionListener<U> {
	/**
	 * Called before task start.
	 *
	 * @param uaow A Unit of work.
	 */
	void onStart(UnitOfWorkV3<U> uaow);

	/**
	 * Called after successful task termination.
	 *
	 * @param uaow The unit of works.
	 * @param res  Result of the task, mainly the allocated resource.
	 */
	void onTerminate(UnitOfWorkV3<U> uaow, @Nullable String res);

	/**
	 * Called if task failed.
	 *
	 * @param uaow The unit of work.
	 * @param e    The exception thrown by the task.
	 */
	void onError(UnitOfWorkV3<U> uaow, RuntimeException e);
}
