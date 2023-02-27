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
package com.ubiqube.etsi.mano.orchestrator.v3;

import java.util.UUID;

import com.ubiqube.etsi.mano.orchestrator.ResultType;
import com.ubiqube.etsi.mano.orchestrator.SystemBuilder;
import com.ubiqube.etsi.mano.orchestrator.nodes.Node;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTaskV3;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestVirtualTask implements VirtualTaskV3<Object> {

	private String name;
	private final Class<? extends Node> type;
	private String alias;
	private final int rank;
	private String toscaName;

	public TestVirtualTask(final Class<? extends Node> type, final String name, final String taskAlias, final int i) {
		this.type = type;
		this.name = name;
		this.alias = taskAlias;
		this.rank = i;
		this.toscaName = name;
	}

	@Override
	public boolean isDeleteTask() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getVimConnectionId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setRank(final int rank) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object getTemplateParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setTemplateParameters(final Object u) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDelete(final boolean del) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSystemBuilder(final SystemBuilder<Object> db) {
		// TODO Auto-generated method stub

	}

	@Override
	public SystemBuilder<Object> getSystemBuilder() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getVimResourceId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setVimResourceId(final String res) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setRemovedLiveInstanceId(final UUID liveInstanceId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setVimConnectionId(final String conn) {
		// TODO Auto-generated method stub

	}

	@Override
	public ResultType getStatus() {
		// TODO Auto-generated method stub
		return null;
	}

}
