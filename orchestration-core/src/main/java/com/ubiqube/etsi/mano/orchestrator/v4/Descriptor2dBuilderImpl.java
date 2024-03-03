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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * @author Olivier Vignaud
 * @param <U>
 */
public class Descriptor2dBuilderImpl<U> implements Descriptor2dBuilder<U> {
	private final Class<U> me;
	private String type;
	private boolean scalable;
	private Function<U, String> funcIdentity;
	private final List<Dependency> dependencies = new ArrayList<>();
	private Function<DependencyBuilder<U>, List<Dependency>> funcDeps;

	private Descriptor2dBuilderImpl(final Class<U> cls) {
		this.me = cls;
	}

	public static <U> Descriptor2dBuilderImpl<U> of(final Class<U> cls) {
		return new Descriptor2dBuilderImpl<>(cls);
	}

	@Override
	public Descriptor2dBuilderImpl<U> type(final String name) {
		this.type = name;
		return this;
	}

	@Override
	public Descriptor2dBuilderImpl<U> scalableWith(final String typeb) {
		this.scalable = true;
		return this;
	}

	@Override
	public Descriptor2dBuilder<U> dependencies(final Function<DependencyBuilder<U>, List<Dependency>> func) {
		this.funcDeps = func;
		return this;
	}

	@Override
	public Descriptor2dBuilderImpl<U> identifiedBy(final Function<U, String> func) {
		this.funcIdentity = func;
		return this;
	}

	public void addDependency(final Dependency dep) {
		dependencies.add(dep);
	}

	@Override
	public Vertex2dDescriptor build() {
		return new Vertex2dDescriptor(me, type, scalable, dependencies);
	}

}
