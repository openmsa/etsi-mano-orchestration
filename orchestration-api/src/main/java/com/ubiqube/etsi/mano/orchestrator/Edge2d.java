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

import com.ubiqube.etsi.mano.orchestrator.exceptions.OrchestrationException;

import org.jspecify.annotations.NonNull;
import lombok.Getter;
import lombok.Setter;

/**
 * The representation of an edge between 2 vertex.
 *
 * @author Olivier Vignaud
 *
 */
@Getter
@Setter
public class Edge2d {
	/**
	 * Source vertex.
	 */
	@NonNull
	private Vertex2d source;
	/**
	 * Target vertex.
	 */
	@NonNull
	private Vertex2d target;
	/**
	 * Relation between the 2 vertex.
	 */
	@NonNull
	private Relation relation;

	@Override
	public String toString() {
		if (null == relation) {
			throw new OrchestrationException("Error: " + source + " <=> " + target);
		}
		return switch (relation) {
		case ONE_TO_ONE -> "1:1";
		case ONE_TO_MANY -> "1:*";
		case MANY_TO_ONE -> "*:1";
		case MULTI -> "***";
		case NONE -> "><";
		default -> throw new IllegalArgumentException("Unexpected value: " + relation);
		};
	}
}
