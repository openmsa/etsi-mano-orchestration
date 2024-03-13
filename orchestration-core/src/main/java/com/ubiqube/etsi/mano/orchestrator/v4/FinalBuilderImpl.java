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

import com.ubiqube.etsi.mano.orchestrator.v4.ref.OrchReference;

public class FinalBuilderImpl<U> implements FinalBuilder<U> {

	private final DependencyBuilderImpl<U> parent;
	private final OrchReference ref;

	public FinalBuilderImpl(final DependencyBuilderImpl<U> parent, final OrchReference ref) {
		this.parent = parent;
		this.ref = ref;
	}

	@Override
	public DependencyBuilder<U> haveOne(final String name) {
		final Dependency dep = new Dependency(ref, Cardinality.ONE, name);
		parent.addDependency(dep);
		return parent;
	}

	@Override
	public DependencyBuilder<U> haveMany(final String name) {
		final Dependency dep = new Dependency(ref, Cardinality.MANY, name);
		parent.addDependency(dep);
		return parent;
	}

}