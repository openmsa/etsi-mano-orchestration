/**
 *     Copyright (C) 2019-2023 Ubiqube.
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
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.ubiqube.etsi.mano.orchestrator.scale;

import java.util.UUID;

import com.ubiqube.etsi.mano.orchestrator.ResultType;
import com.ubiqube.etsi.mano.orchestrator.SystemBuilder;
import com.ubiqube.etsi.mano.orchestrator.nodes.Node;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTaskV3;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author olivier
 *
 * @param <U>
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ContextVt<U> implements VirtualTaskV3<U> {

	private String vimConnectionId;
	@Nonnull
	private String name;

	private String alias;

	private int rank;

	private U templateParameters;

	private boolean delete;

	@Nullable
	private String vimResourceId;

	private Class<? extends Node> parent;

	private SystemBuilder<U> systemBuilder;

	private UUID removedLiveInstanceId;

	public ContextVt(Class<? extends Node> clazz, String name,@Nullable String vimResourceId) {
		this.parent = clazz;
		this.name = name;
		this.vimResourceId = vimResourceId;
	}
	@Override
	public boolean isDeleteTask() {
		return delete;
	}

	@Override
	public @Nullable Class<? extends Node> getType() {
		return parent;
	}

	@Override
	public void setRemovedLiveInstanceId(UUID liveInstanceId) {
		removedLiveInstanceId = liveInstanceId;
	}

	@Override
	public String getToscaName() {
		return name;
	}

	@Override
	public @Nullable String toString() {
		return alias;
	}
	@Override
	public ResultType getStatus() {
		return ResultType.NOT_STARTED;
	}
}
