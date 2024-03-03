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

import java.util.Set;
import java.util.function.Function;

public interface RelationBuilder<U> {

	RelationBuilder<U> haveManyRef(Function<U, Set<String>> func, Class<?> clazz);

	RelationBuilder<U> haveManyParentRef(Function<U, Set<String>> func, Class<?> clazz);

	RelationBuilder<U> haveManyChild(Function<U, Set<?>> func, Class<?> clazz);
}
