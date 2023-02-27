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
import com.ubiqube.etsi.mano.orchestrator.nodes.Node;

/**
 *
 * @author olivier
 *
 * @param U Type of the parameter class, can be a Task parameter.
 */
public interface VirtualTaskV3<U> {
	boolean isDeleteTask();

	String getVimConnectionId();

	void setVimConnectionId(String conn);

	void setName(String name);

	String getName();

	Class<? extends Node> getType();

	void setAlias(String alias);

	String getAlias();

	int getRank();

	void setRank(int rank);

	U getTemplateParameters();

	void setTemplateParameters(U u);

	void setDelete(boolean del);

	void setSystemBuilder(SystemBuilder<U> db);

	SystemBuilder<U> getSystemBuilder();

	String getVimResourceId();

	void setVimResourceId(String res);

	void setRemovedLiveInstanceId(UUID liveInstanceId);

	String getToscaName();

	ResultType getStatus();
}
