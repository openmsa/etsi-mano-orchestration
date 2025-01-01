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

import java.util.Arrays;
import java.util.List;

import org.jgrapht.ListenableGraph;

import com.ubiqube.etsi.mano.orchestrator.nodes.ConnectivityEdge;
import com.ubiqube.etsi.mano.orchestrator.uow.UnitOfWorkV3;

import jakarta.annotation.Nullable;

/**
 * A simple builder for {@link SystemBuilder}.
 *
 * @author Olivier Vignaud
 * @param <U>
 */
public class SystemBuilderV3Impl<U> implements SystemBuilder<U> {
	private final ListenableGraph<UnitOfWorkV3<U>, ConnectivityEdge<UnitOfWorkV3<U>>> g = GraphTools.createGraphV3();
	private UnitOfWorkV3<U> single = null;

	public static <U> SystemBuilder<U> of(final UnitOfWorkV3<U> vt) {
		final SystemBuilderV3Impl<U> ib = new SystemBuilderV3Impl<>();
		ib.single = vt;
		return ib;
	}

	public static <U> SystemBuilder<U> of(final UnitOfWorkV3<U> left, final UnitOfWorkV3<U> right) {
		final SystemBuilderV3Impl<U> ib = new SystemBuilderV3Impl<>();
		ib.g.addVertex(left);
		ib.g.addVertex(right);
		ib.g.addEdge(left, right);
		return ib;
	}

	public SystemBuilder<U> edge(final UnitOfWorkV3<U> left, final UnitOfWorkV3<U> right) {
		g.addVertex(left);
		g.addVertex(right);
		g.addEdge(left, right);
		return this;
	}

	@Override
	@Nullable
	public UnitOfWorkV3<U> getSingle() {
		return single;
	}

	@Override
	public void add(final UnitOfWorkV3<U> src, final UnitOfWorkV3<U> dest) {
		g.addVertex(src);
		g.addVertex(dest);
		g.addEdge(src, dest);
	}

	@Override
	public List<UnitOfWorkV3<U>> getVertexV3() {
		if (null != single) {
			return Arrays.asList(single);
		}
		return g.vertexSet().stream().toList();
	}

}
