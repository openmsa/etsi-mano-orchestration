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

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import com.ubiqube.etsi.mano.orchestrator.v4.ref.From;
import com.ubiqube.etsi.mano.orchestrator.v4.ref.FromRef;
import com.ubiqube.etsi.mano.orchestrator.v4.ref.Self;

/**
 * @author Olivier Vignaud
 * @param <U>
 */
public class DependencyBuilderImpl<U> implements DependencyBuilder<U> {

	private final Descriptor2dBuilderImpl<U> d2d;

	public DependencyBuilderImpl(final Descriptor2dBuilderImpl<U> descriptor2dBuilder) {
		this.d2d = descriptor2dBuilder;
	}

	@Override
	public FinalBuilder<U> fromRef(final Function<U, Collection<String>> func) {
		final FromRef<U> fromRef = new FromRef<>(func);
		return new FinalBuilderImpl<>(this, fromRef);
	}

	@Override
	public <V> FinalBuilder<U> from(final Function<U, Collection<V>> func) {
		final From<U, V> from = new From<>(func);
		return new FinalBuilderImpl<>(this, from);
	}

	@Override
	public FinalBuilder<U> from(final String name) {
		final Self from = new Self(name);
		return new FinalBuilderImpl<>(this, from);
	}

	// List<Dependency2d>
	@Override
	public List<Dependency> build() {
		return List.of();
	}

	public void addDependency(final Dependency dep) {
		d2d.addDependency(dep);
	}

	@Override
	public <V> FinalBuilder<U> fromProperty(final Function<U, String> func) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FinalBuilder<U> self() {
		// TODO Auto-generated method stub
		return null;
	}

}
