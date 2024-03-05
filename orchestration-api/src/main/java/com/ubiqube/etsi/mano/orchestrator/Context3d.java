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
package com.ubiqube.etsi.mano.orchestrator;

import java.util.List;

import com.ubiqube.etsi.mano.orchestrator.nodes.Node;

import jakarta.annotation.Nullable;

/**
 * Context 3D old a traditional workflow context, but organized in a tree. Main
 * benefit is you can address only parent resources.
 *
 * @author Olivier Vignaud
 *
 */
public interface Context3d {
	/**
	 * For the given class, name return the resource.
	 *
	 * @param class1    The node type.
	 * @param toscaName The name of the element.
	 * @return The resource, or throw exception.
	 */
	String get(Class<? extends Node> class1, String toscaName);

	/**
	 * Return parent from the given class, name.
	 *
	 * @param class1    The node type.
	 * @param toscaName The name.
	 * @return A list of node/name combination.
	 */
	List<String> getParent(Class<? extends Node> class1, String toscaName);

	/**
	 * Add a resource to the context.
	 *
	 * @param class1     Node type.
	 * @param name       Name of the element.
	 * @param resourceId The resource id.
	 */
	void add(Class<? extends Node> class1, String name, String resourceId);

	/**
	 * Get all resource for a given Node.
	 *
	 * @param class1 The node type.
	 * @return A List of encountered nodes, empty list otherwise.
	 */
	List<String> get(Class<? extends Node> class1);

	/**
	 * Get a given resource using class / name. Will not crash if not found.
	 *
	 * @param class1          The type of the node.
	 * @param parentToscaName The name of the node.
	 * @return Null if not found.
	 */
	@Nullable
	String getOptional(Class<? extends Node> class1, String parentToscaName);

}
