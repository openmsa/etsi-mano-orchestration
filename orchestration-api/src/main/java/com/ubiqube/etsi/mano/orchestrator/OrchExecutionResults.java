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

import java.util.List;

/**
 * Hold a collection of execution results.
 *
 * @param <U> The task parameters, this one is unique per task type (Network,
 *            compute, ...)
 * @author Olivier Vignaud {@literal <ovi@ubiqube.com>}
 *
 */
public interface OrchExecutionResults<U> {
	/**
	 * Get all success results.
	 *
	 * @return A list of success results.
	 */
	List<OrchExecutionResult<U>> getSuccess();

	/**
	 * Get all error results.
	 *
	 * @return A list of all failed results.
	 */
	List<OrchExecutionResult<U>> getErrored();

	/**
	 * Add all results.
	 *
	 * @param results An execution results object {@link OrchExecutionResults}.
	 */
	void addAll(OrchExecutionResults<U> results);

}
