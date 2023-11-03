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
package com.ubiqube.etsi.mano.orchestrator.vt;

import com.ubiqube.etsi.mano.orchestrator.Relation;

import jakarta.annotation.Nonnull;

/**
 * An edge specialized in Virtual tasks.
 *
 * @author Olivier Vignaud
 * @param <U> Task parameters.
 */
public class VirtualTaskConnectivityV3<U> {
	@Nonnull
	private VirtualTaskV3<U> source;
	@Nonnull
	private VirtualTaskV3<U> target;
	@Nonnull
	private Relation relation;

	public VirtualTaskConnectivityV3() {
		// Need default constructor for graph.
	}

	public VirtualTaskConnectivityV3(final VirtualTaskV3<U> source, final VirtualTaskV3<U> target, final Relation relation) {
		this.source = source;
		this.target = target;
		this.relation = relation;
	}

	public VirtualTaskV3<U> getSource() {
		return source;
	}

	public void setSource(final VirtualTaskV3<U> source) {
		this.source = source;
	}

	public VirtualTaskV3<U> getTarget() {
		return target;
	}

	public void setTarget(final VirtualTaskV3<U> target) {
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
