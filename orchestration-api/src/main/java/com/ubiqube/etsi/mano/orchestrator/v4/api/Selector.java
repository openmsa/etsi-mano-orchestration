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
package com.ubiqube.etsi.mano.orchestrator.v4.api;

public interface Selector {
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
	Class<?> getType();

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
//	void setRank(int rank);

	/**
	 * The tosca name.
	 *
	 * @return The tosca name.
	 */
	String getToscaName();

	/**
	 * Set the name of the task.
	 *
	 * @param name The name of the task.
	 */
	void setName(String name);

	boolean equalsNoRank(Selector other);

	boolean equalsFully(Selector other);
}
