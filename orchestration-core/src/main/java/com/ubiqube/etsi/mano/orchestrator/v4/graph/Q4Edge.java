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
package com.ubiqube.etsi.mano.orchestrator.v4.graph;

import java.util.Objects;

import org.jspecify.annotations.NonNull;

import com.ubiqube.etsi.mano.orchestrator.v4.Q4Task;

public class Q4Edge {
	@NonNull
	private Q4Task source;
	@NonNull
	private Q4Task target;

	public Q4Edge() {
		// Need constructor for graph.
	}

	public Q4Edge(final Q4Task source, final Q4Task target) {
		this.source = Objects.requireNonNull(source);
		this.target = Objects.requireNonNull(target);
	}

	public Q4Task getSource() {
		return source;
	}

	public void setSource(final Q4Task source) {
		this.source = source;
	}

	public Q4Task getTarget() {
		return target;
	}

	public void setTarget(final Q4Task target) {
		this.target = target;
	}

	@Override
	public String toString() {
		return "Q4Edge [source=" + source + ", target=" + target + "]";
	}

}
