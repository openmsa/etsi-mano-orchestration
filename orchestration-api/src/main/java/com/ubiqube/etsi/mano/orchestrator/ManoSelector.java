/**
 *     Copyright (C) 2019-2024 Ubiqube.
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

import com.ubiqube.etsi.mano.orchestrator.v4.api.Selector;

import jakarta.annotation.Nullable;

public class ManoSelector implements Selector {

	private String name;
	private final Class<?> type;
	private int rank = 0;

	public ManoSelector(final Class<?> type, final String toscaName) {
		this.name = toscaName;
		this.type = type;
	}

	public static ManoSelector of(final Class<?> type, final String toscaName) {
		return new ManoSelector(type, toscaName);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Class<?> getType() {
		return type;
	}

	@Override
	public void setAlias(final String alias) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getAlias() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getRank() {
		return rank;
	}

	@Override
	public void setRank(final int rank) {
		this.rank = rank;
	}

	@Override
	public String getToscaName() {
		return name;
	}

	@Override
	public void setName(final String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return type.getSimpleName() + "_" + name + "_" + String.format("%04d", rank);
	}

	@Override
	public boolean equalsNoRank(@Nullable final Selector other) {
		if (null == other) {
			return false;
		}
		return (other.getType() == type)
				&& (other.getName().equals(name));
	}

	@Override
	public boolean equalsFully(@Nullable final Selector other) {
		if (null == other) {
			return false;
		}
		return equalsNoRank(other)
				&& (other.getRank() == rank);
	}
}
