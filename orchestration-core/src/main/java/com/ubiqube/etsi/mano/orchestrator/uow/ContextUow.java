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
package com.ubiqube.etsi.mano.orchestrator.uow;

import com.ubiqube.etsi.mano.orchestrator.Context3d;
import com.ubiqube.etsi.mano.orchestrator.nodes.Node;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTaskV3;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class ContextUow<U> implements UnitOfWorkV3<U> {
	@NonNull
	private final VirtualTaskV3<U> vt;
	private String resource;

	public ContextUow(final VirtualTaskV3<U> vt) {
		this.vt = vt;
		this.resource = vt.getVimResourceId();
	}

	@Override
	@Nullable
	public String execute(final Context3d context) {
		// Nothing.
		return null;
	}

	@Override
	@Nullable
	public String rollback(final Context3d context) {
		// Nothing.
		return null;
	}

	@Override
	public Class<? extends Node> getType() {
		return vt.getType();
	}

	@Override
	public VirtualTaskV3<U> getVirtualTask() {
		return vt;
	}

	@Override
	public void setResource(final String resource) {
		this.resource = resource;
	}

	public String getResource() {
		return resource;
	}

}
