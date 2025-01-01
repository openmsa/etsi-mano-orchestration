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
import java.util.function.Function;

import com.github.dexecutor.core.task.Task;
import com.ubiqube.etsi.mano.orchestrator.context.SimplifiedContextImpl;
import com.ubiqube.etsi.mano.orchestrator.uow.ContextUow;
import com.ubiqube.etsi.mano.orchestrator.uow.UnitOfWorkV3;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

public class DexecutorTask<P> extends Task<UnitOfWorkV3<P>, String> {
	/** Serial. */
	private static final long serialVersionUID = 1L;
	@Nonnull
	private final transient UnitOfWorkV3<P> uaow;
	@Nonnull
	private final transient Function<Context3d, String> function;
	@Nonnull
	private transient OrchExecutionListener<P> listener;
	@Nonnull
	private transient Context3dNetFlow<P> context;

	public DexecutorTask(final OrchExecutionListener<P> listener, final UnitOfWorkV3<P> uaow, final Context3dNetFlow<P> context, final boolean create) {
		this.uaow = Objects.requireNonNull(uaow);
		this.listener = Objects.requireNonNull(listener);
		this.context = Objects.requireNonNull(context);
		if (!(uaow.getVirtualTask().isDeleteTask() ^ create)) {
			function = x -> null;
		} else if (create) {
			function = x -> {
				final String res = uaow.execute(x);
				if (null != res) {
					uaow.setResource(res);
				}
				return res;
			};
		} else {
			function = uaow::rollback;
		}
	}

	@Override
	public @Nullable String execute() {
		if (uaow instanceof ContextUow) {
			return null;
		}
		listener.onStart(uaow);
		final Context3d ctx = new SimplifiedContextImpl<>(uaow, context);
		final String res;
		try {
			res = function.apply(ctx);
			listener.onTerminate(uaow, res);
			return res;
		} catch (final RuntimeException e) {
			listener.onError(uaow, e);
			throw e;
		}

	}

}
