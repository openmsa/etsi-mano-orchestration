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

import java.util.UUID;

import com.ubiqube.etsi.mano.orchestrator.ResultType;
import com.ubiqube.etsi.mano.orchestrator.SystemBuilder;
import com.ubiqube.etsi.mano.orchestrator.nodes.Node;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTaskV3;

public class NamedTestTask implements VirtualTaskV3<Object> {

	private final String name;
	private final Class<? extends Node> type;
	private int rank;
	private String alias;

	public NamedTestTask(final String name, final Class<? extends Node> type) {
		this.name = name;
		this.type = type;
	}

	@Override
	public boolean isDeleteTask() {
		return false;
	}

	@Override
	public String getVimConnectionId() {
		throw new IllegalArgumentException();
	}

	@Override
	public void setVimConnectionId(final String conn) {
		throw new IllegalArgumentException();
	}

	@Override
	public void setName(final String name) {
		throw new IllegalArgumentException();
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public Class<? extends Node> getType() {
		return this.type;
	}

	@Override
	public void setAlias(final String alias) {
		this.alias = alias;
	}

	@Override
	public String getAlias() {
		return alias;
	}

	@Override
	public int getRank() {
		return this.rank;
	}

	@Override
	public void setRank(final int rank) {
		this.rank = rank;
	}

	@Override
	public Object getTemplateParameters() {
		throw new IllegalArgumentException();
	}

	@Override
	public void setTemplateParameters(final Object u) {
		throw new IllegalArgumentException();
	}

	@Override
	public void setDelete(final boolean del) {
		throw new IllegalArgumentException();
	}

	@Override
	public void setSystemBuilder(final SystemBuilder<Object> db) {
		throw new IllegalArgumentException();
	}

	@Override
	public SystemBuilder<Object> getSystemBuilder() {
		throw new IllegalArgumentException();
	}

	@Override
	public String getVimResourceId() {
		return name;
	}

	@Override
	public void setVimResourceId(final String res) {
		throw new IllegalArgumentException();
	}

	@Override
	public void setRemovedLiveInstanceId(final UUID liveInstanceId) {
		throw new IllegalArgumentException();
	}

	@Override
	public String getToscaName() {
		return name;
	}

	@Override
	public ResultType getStatus() {
		throw new IllegalArgumentException();
	}

}
