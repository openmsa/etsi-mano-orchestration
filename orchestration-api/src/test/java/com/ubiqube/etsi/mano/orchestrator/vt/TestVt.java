/**
 *     Copyright (C) 2019-2024 Ubiqube.
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
 *     along with this program.  If not, see https://www.gnu.org/licenses/.
 */
package com.ubiqube.etsi.mano.orchestrator.vt;

import java.util.UUID;

import com.ubiqube.etsi.mano.orchestrator.ResultType;
import com.ubiqube.etsi.mano.orchestrator.SystemBuilder;
import com.ubiqube.etsi.mano.orchestrator.v4.api.Selector;

public class TestVt implements VirtualTaskV3<Object> {

	@Override
	public boolean isDeleteTask() {
		return false;
	}

	@Override
	public void setDelete(final boolean del) {
		//

	}

	@Override
	public String getVimConnectionId() {
		return null;
	}

	@Override
	public void setVimConnectionId(final String id) {
		//

	}

	@Override
	public String getVimResourceId() {
		return null;
	}

	@Override
	public void setVimResourceId(final String res) {
		//

	}

	@Override
	public Selector getSelector() {
		return null;
	}

	@Override
	public Object getTemplateParameters() {
		return null;
	}

	@Override
	public void setTemplateParameters(final Object u) {
		//

	}

	@Override
	public void setSystemBuilder(final SystemBuilder<Object> db) {
		//

	}

	@Override
	public SystemBuilder<Object> getSystemBuilder() {
		return null;
	}

	@Override
	public void setRemovedLiveInstanceId(final UUID liveInstanceId) {
		//

	}

	@Override
	public ResultType getStatus() {
		return null;
	}

}
