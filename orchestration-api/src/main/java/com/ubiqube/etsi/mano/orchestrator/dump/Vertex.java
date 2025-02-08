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
package com.ubiqube.etsi.mano.orchestrator.dump;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a vertex in the orchestration graph.
 */
@Getter
@Setter
@Builder
public class Vertex {
	/**
	 * The unique identifier of the vertex.
	 */
	private String id;

	/**
	 * The name of the vertex.
	 */
	private String name;

	/**
	 * The VIM connection ID associated with the vertex.
	 */
	private String vimConnectionId;

	/**
	 * The VIM resource ID associated with the vertex.
	 */
	private String vimResourceId;

	/**
	 * The type of the vertex.
	 */
	private String type;

	/**
	 * The alias of the vertex.
	 */
	private String alias;

	/**
	 * The rank of the vertex.
	 */
	private int rank;

	/**
	 * The status of the vertex.
	 */
	private String status;
}
