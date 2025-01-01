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
package com.ubiqube.etsi.mano.orchestrator.nodes;

import java.util.Objects;

import org.jspecify.annotations.NonNull;

/**
 * Represent an edge between 2 vertexes.
 *
 * @param <U>
 */
public class ConnectivityEdge<U> {
	@NonNull
	private U source;
	@NonNull
	private U target;

	public ConnectivityEdge() {
		// Need constructor for graph.
	}

	public ConnectivityEdge(final U source, final U target) {
		this.source = Objects.requireNonNull(source);
		this.target = Objects.requireNonNull(target);
	}

	public U getSource() {
		return source;
	}

	public void setSource(final U source) {
		this.source = source;
	}

	public U getTarget() {
		return target;
	}

	public void setTarget(final U target) {
		this.target = target;
	}

	@Override
	public String toString() {
		return "ConnectivityEdge [source=" + source + ", target=" + target + "]";
	}

}
