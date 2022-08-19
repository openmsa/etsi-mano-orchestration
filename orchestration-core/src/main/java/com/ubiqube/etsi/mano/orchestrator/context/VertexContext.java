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
package com.ubiqube.etsi.mano.orchestrator.context;

import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTaskV3;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author olivier
 *
 */
@Getter
@Setter
public class VertexContext<U> {
	/**
	 * Origin plan vertex.
	 */
	private VirtualTaskV3<U> orig;

	/**
	 * The VIM resource.
	 */
	private String resource;

	public VertexContext(final VirtualTaskV3<U> orig) {
		this.orig = orig;
	}
}
