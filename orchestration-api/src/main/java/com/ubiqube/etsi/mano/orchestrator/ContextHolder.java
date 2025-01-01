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
package com.ubiqube.etsi.mano.orchestrator;

import java.util.UUID;

import com.ubiqube.etsi.mano.orchestrator.nodes.Node;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Hold a context element.
 *
 * @author Olivier Vignaud
 *
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ContextHolder {
	/**
	 * Live instance ID
	 */
	private UUID liveInstanceId;

	/**
	 * Node type.
	 */
	private Class<? extends Node> type;

	/**
	 * Element name.
	 */
	private String name;

	/**
	 * Rank of the element, maybe a dimension.
	 */
	private int rank;

	/**
	 * The resource id in the vim.
	 */
	private String resourceId;

	/**
	 * The vim associated with the resourceId.
	 */
	private String vimConnectionId;

}
