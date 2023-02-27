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
package com.ubiqube.etsi.mano.orchestrator.vt;

import java.util.UUID;

import com.ubiqube.etsi.mano.orchestrator.ResultType;
import com.ubiqube.etsi.mano.orchestrator.SystemBuilder;
import com.ubiqube.etsi.mano.orchestrator.TestParameters;
import com.ubiqube.etsi.mano.orchestrator.nodes.Node;

public class ProvAVt implements VirtualTaskV3<TestParameters> {

	private SystemBuilder<TestParameters> sys;

	@Override
	public boolean isDeleteTask() {
		return false;
	}

	@Override
	public String getVimConnectionId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAlias() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setName(final String name) {
		// TODO Auto-generated method stub

	}

	@Override
	public Class<? extends Node> getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAlias(final String alias) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getRank() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setRank(final int rank) {
		// TODO Auto-generated method stub

	}

	@Override
	public TestParameters getTemplateParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setTemplateParameters(final TestParameters u) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDelete(final boolean del) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSystemBuilder(final SystemBuilder<TestParameters> db) {
		this.sys = db;
	}

	@Override
	public SystemBuilder<TestParameters> getSystemBuilder() {
		return sys;
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
	public String getToscaName() {
		// TODO Auto-generated method stub
		return null;
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
