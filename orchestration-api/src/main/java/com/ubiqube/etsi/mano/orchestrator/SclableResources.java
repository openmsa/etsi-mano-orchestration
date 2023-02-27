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
package com.ubiqube.etsi.mano.orchestrator;

import com.ubiqube.etsi.mano.orchestrator.nodes.Node;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author olivier
 *
 */
@AllArgsConstructor
@Getter
@Setter
public class SclableResources<U> {
	private Class<? extends Node> type;
	private String name;
	private int have;
	private int want;
	private U templateParameter;

	public static <U> SclableResources<U> of(final Class<? extends Node> type, final String name, final int have, final int want, final U param) {
		return new SclableResources<>(type, name, have, want, param);
	}

	@Override
	public String toString() {
		return "SR(type=" + type.getSimpleName() + ", name=" + name + ", have=" + have + ", want=" + want + ")";
	}

}
