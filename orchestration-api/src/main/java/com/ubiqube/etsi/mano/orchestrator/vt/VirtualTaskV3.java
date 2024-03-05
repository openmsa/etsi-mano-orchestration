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
import com.ubiqube.etsi.mano.orchestrator.nodes.Node;

import jakarta.annotation.Nullable;

/**
 * Representation of a virtual task.
 *
 * @author Olivier Vignaud
 *
 *         <U> Type of the parameter class, can be a Task parameter.
 */
public interface VirtualTaskV3<U> {
	/**
	 * True if the task is a delete task.
	 *
	 * @return A boolean.
	 */
	boolean isDeleteTask();

	/**
	 * Return vim connection Id.
	 *
	 * @return A Sting.
	 */
	@Nullable
	String getVimConnectionId();

	/**
	 * Set the vim connection Id.
	 *
	 * @param conn The vim connection Id
	 */
	void setVimConnectionId(String id);

	/**
	 * Set the name of the task.
	 *
	 * @param name The name of the task.
	 */
	void setName(String name);

	/**
	 * Return the name of the task.
	 *
	 * @return The string.
	 */
	String getName();

	/**
	 * Return the task type.
	 *
	 * @return The task type.
	 */
	Class<? extends Node> getType();

	/**
	 * Set the alias name.
	 *
	 * @param alias The alias name.
	 */
	void setAlias(String alias);

	/**
	 * Return the alias name.
	 *
	 * @return The string.
	 */
	String getAlias();

	/**
	 * Return the rank of the task. This is a dimensional parameter that helps to
	 * select a task in multiple dimensions.
	 *
	 * @return An int.
	 */
	int getRank();

	/**
	 * Set the rank of this task.
	 *
	 * @param rank The rank.
	 */
	void setRank(int rank);

	/**
	 * Return parameter of the task.
	 *
	 * @return The parameter.
	 */
	U getTemplateParameters();

	/**
	 * Set task paramter.
	 *
	 * @param u The parameter object.
	 */
	void setTemplateParameters(U u);

	/**
	 * Set delete mode.
	 *
	 * @param del True for calling delete action, true will call execute method.
	 */
	void setDelete(boolean del);

	/**
	 * System builder associated with this task.
	 *
	 * @param db The system builder instance.
	 */
	void setSystemBuilder(SystemBuilder<U> db);

	/**
	 * Return the associated system builder.
	 *
	 * @return A system builder instance. Null otherwise.
	 */
	SystemBuilder<U> getSystemBuilder();

	/**
	 * Return the vim resource id.
	 *
	 * @return The vimResourceId.
	 */
	@Nullable
	String getVimResourceId();

	/**
	 * Set the vim resource id.
	 *
	 * @param res
	 */
	void setVimResourceId(String res);

	/**
	 * The live instance to remove on success.
	 *
	 * @param liveInstanceId A live instance Id.
	 */
	void setRemovedLiveInstanceId(UUID liveInstanceId);

	/**
	 * The tosca name.
	 *
	 * @return The tosca name.
	 */
	String getToscaName();

	/**
	 * The task status.
	 *
	 * @return The task status.
	 */
	ResultType getStatus();
}
