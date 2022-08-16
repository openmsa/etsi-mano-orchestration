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
package com.ubiqube.etsi.mano.orchestrator.scale;

import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTaskV3;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author olivier
 *
 * @param <U>
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ContextVt<U> implements VirtualTaskV3<U> {

	private String vimConnectionId;

	private String name;

	private Class<?> type;

	private String alias;

	private int rank;

	private U templateParameters;

	private boolean delete;

	private String resourceId;
	
	@Override
	public boolean isDeleteTask() {
		return delete;
	}

}
