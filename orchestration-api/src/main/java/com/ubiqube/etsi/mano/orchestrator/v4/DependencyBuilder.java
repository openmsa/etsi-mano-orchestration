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
package com.ubiqube.etsi.mano.orchestrator.v4;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public interface DependencyBuilder<U> {

	FinalBuilder<U> fromRef(final Function<U, Collection<String>> func);

	<V> FinalBuilder<U> from(final Function<U, Collection<V>> func);

	<V> FinalBuilder<U> fromProperty(final Function<U, String> func);

	FinalBuilder<U> from(final String name);

	List<Dependency> build();

	FinalBuilder<U> self();

}