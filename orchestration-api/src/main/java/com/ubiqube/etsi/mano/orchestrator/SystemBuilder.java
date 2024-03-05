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

import com.ubiqube.etsi.mano.orchestrator.uow.UnitOfWorkV3;

import jakarta.annotation.Nullable;

/**
 * Helper class for building Systems.
 *
 * @author Olivier Vignaud {@literal <ovi@ubiqube.com>}
 *
 */
public interface SystemBuilder<U> {
	/**
	 * Return a single vertex.
	 *
	 * @return Return a single Vertex.
	 */
	@Nullable
	UnitOfWorkV3<U> getSingle();

	/**
	 * Add 2 vertex, and link them in the current system.
	 *
	 * @param src  Source vertex.
	 * @param dest Target vertex.
	 */
	void add(UnitOfWorkV3<U> src, UnitOfWorkV3<U> dest);

	/**
	 * Return all system vertexes.
	 *
	 * @return A list of vertex.
	 */
	List<UnitOfWorkV3<U>> getVertexV3();

}
