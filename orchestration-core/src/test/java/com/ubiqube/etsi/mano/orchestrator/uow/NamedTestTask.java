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
package com.ubiqube.etsi.mano.orchestrator.uow;

import java.util.UUID;

import com.ubiqube.etsi.mano.orchestrator.ResultType;
import com.ubiqube.etsi.mano.orchestrator.SystemBuilder;
import com.ubiqube.etsi.mano.orchestrator.v4.api.Selector;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTaskV3;

public class NamedTestTask implements VirtualTaskV3<Object> {

	private final Selector selector;

	public NamedTestTask(final Selector selector) {
		this.selector = selector;
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
		return "";
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
	public ResultType getStatus() {
		throw new IllegalArgumentException();
	}

	@Override
	public Selector getSelector() {
		return selector;
	}

}
