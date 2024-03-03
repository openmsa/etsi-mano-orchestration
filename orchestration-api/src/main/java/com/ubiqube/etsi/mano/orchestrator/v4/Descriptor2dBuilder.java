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
package com.ubiqube.etsi.mano.orchestrator.v4;

import java.util.List;
import java.util.function.Function;

/**
 * @author Olivier Vignaud
 * @param <U>
 */
public interface Descriptor2dBuilder<U> {

	Descriptor2dBuilder<U> type(final String name);

	Descriptor2dBuilder<U> scalableWith(String type);

	Descriptor2dBuilder<U> dependencies(Function<DependencyBuilder<U>, List<Dependency>> func);

	Descriptor2dBuilder<U> identifiedBy(final Function<U, String> func);

	Vertex2dDescriptor build();

}
