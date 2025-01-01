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

import com.ubiqube.etsi.mano.orchestrator.uow.UnitOfWorkV3;

/**
 * The representation of one execution result.
 *
 * @param <U> The task parameters, this one is unique per task type (Network,
 *            compute, ...)
 * @author Olivier Vignaud {@literal <ovi@ubiqube.com>}
 *
 */
public interface OrchExecutionResult<U> {
	/**
	 * Return the task f the workflow.
	 *
	 * @return The associated task parameters
	 */
	UnitOfWorkV3<U> getTask();

	/**
	 * Return the execution result of the task.
	 *
	 * @return The result of the task. Cannot be null.
	 */
	ResultType getResult();

	/**
	 * The message ?
	 *
	 * @return A String containing the message.
	 */
	String getMessage();
}
