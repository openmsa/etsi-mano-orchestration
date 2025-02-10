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

/**
 * Wrapper class for providing tasks in the Q4 context.
 */
public class Q4ProviderWrapper implements TaskProvider<Q4Task, String> {

	private final Q4Context context;
	private final Q4TaskProvider<Q4Task> provider;

	/**
	 * Constructs a Q4ProviderWrapper with the specified context and task provider.
	 *
	 * @param context  the Q4 context
	 * @param provider the task provider
	 */
	Q4ProviderWrapper(final Q4Context context, final Q4TaskProvider<Q4Task> provider) {
		this.context = context;
		this.provider = provider;
	}

	/**
	 * Provides a task for the given Q4 task ID.
	 *
	 * @param id the Q4 task ID
	 * @return the provided task
	 */
	@Override
	public Task<Q4Task, String> provideTask(final Q4Task id) {
		Q4Task tsk = provider.provideTask(id, context);
		return new Q4DexecutorTask(tsk, context);
	}

}
