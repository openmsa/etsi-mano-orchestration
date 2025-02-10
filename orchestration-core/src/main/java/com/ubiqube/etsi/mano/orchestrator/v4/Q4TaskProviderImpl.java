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
package com.ubiqube.etsi.mano.orchestrator.v4;

import com.github.dexecutor.core.task.Task;
import com.github.dexecutor.core.task.TaskProvider;

public class Q4TaskProviderImpl implements TaskProvider<Q4Task, String> {

	private final Q4Context context;

	Q4TaskProviderImpl(final Q4Context context) {
		this.context = context;
	}

	@Override
	public Task<Q4Task, String> provideTask(final Q4Task id) {
		return new Q4DexecutorTask(id, context);
	}

}
