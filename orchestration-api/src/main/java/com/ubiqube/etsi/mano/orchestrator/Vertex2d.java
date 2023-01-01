/**
 *     Copyright (C) 2019-2020 Ubiqube.
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
package com.ubiqube.etsi.mano.orchestrator;

import java.util.Objects;

import com.ubiqube.etsi.mano.orchestrator.nodes.Node;

import lombok.Getter;

/**
 *
 * @author olivier
 *
 */
@Getter
public class Vertex2d {

	private Class<? extends Node> type;
	private String name;
	private Vertex2d parent;

	public Vertex2d() {
		//
	}

	public Vertex2d(final Class<? extends Node> class1, final String name, final Vertex2d parent) {
		this.type = class1;
		this.name = name;
		this.parent = parent;
	}

	public boolean match(final Class<? extends Node> inType, final String inName, final Vertex2d inParent) {
		final boolean ret = ((inType == type) && inName.equals(name));
		if ((null == inParent)) {
			return ret;
		}
		return ret && (inParent.equals(parent));
	}

	@Override
	public String toString() {
		if (parent != null) {
			return parent.name + "-" + name + "\n" + type.getSimpleName();
		}
		return name + "\n" + type.getSimpleName();
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, parent, type);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if ((obj == null) || (getClass() != obj.getClass())) {
			return false;
		}
		final Vertex2d other = (Vertex2d) obj;
		return Objects.equals(name, other.name) && Objects.equals(parent, other.parent) && Objects.equals(type, other.type);
	}
}
