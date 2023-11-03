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
 *     along with this program.  If not, see https://www.gnu.org/licenses/.
 */
package com.ubiqube.etsi.mano.orchestrator.uow;

import com.ubiqube.etsi.mano.orchestrator.Context3d;
import com.ubiqube.etsi.mano.orchestrator.nodes.Node;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTaskV3;

import jakarta.annotation.Nullable;

/**
 * A unit of work, is like a workflow task.
 *
 * @author Olivier Vignaud
 *
 */
public interface UnitOfWorkV3<U> {
	/**
	 * Return the associated virtual task.
	 *
	 * @return The associated virtual task.
	 */
	VirtualTaskV3<U> getVirtualTask();

	/**
	 * Execute the task.
	 *
	 * @param context The workflow context.
	 * @return The allocated resource.
	 */
	@Nullable
	String execute(Context3d context);

	/**
	 * Rollback/delete the resource.
	 *
	 * @param context The workflow context.
	 * @return Probably nothing.
	 */
	@Nullable
	String rollback(Context3d context);

	/**
	 * The type of the Unit of work
	 *
	 * @return A Node derived class.
	 */
	Class<? extends Node> getType();

	/**
	 * Set the resource id.
	 *
	 * @param res The resource Id.
	 */
	void setResource(String res);

}
