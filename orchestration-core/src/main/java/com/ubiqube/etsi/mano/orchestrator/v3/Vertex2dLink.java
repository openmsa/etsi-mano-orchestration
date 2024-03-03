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
package com.ubiqube.etsi.mano.orchestrator.v3;

import com.ubiqube.etsi.mano.orchestrator.Relation;
import com.ubiqube.etsi.mano.orchestrator.v4.Vertex2dDescriptor;

import jakarta.annotation.Nonnull;

public class Vertex2dLink {
	@Nonnull
	private Vertex2dDescriptor source;
	@Nonnull
	private Vertex2dDescriptor target;
	@Nonnull
	private Relation relation;

	public Vertex2dLink() {
		// Need default constructor for graph.
	}

	public Vertex2dLink(final Vertex2dDescriptor source, final Vertex2dDescriptor target, final Relation relation) {
		this.source = source;
		this.target = target;
		this.relation = relation;
	}

	public Vertex2dDescriptor getSource() {
		return source;
	}

	public void setSource(final Vertex2dDescriptor source) {
		this.source = source;
	}

	public Vertex2dDescriptor getTarget() {
		return target;
	}

	public void setTarget(final Vertex2dDescriptor target) {
		this.target = target;
	}

	public Relation getRelation() {
		return relation;
	}

	public void setRelation(final Relation relation) {
		this.relation = relation;
	}

	@Override
	public String toString() {
		return "ConnectivityEdge [source=" + source + ", target=" + target + "]";
	}

}
