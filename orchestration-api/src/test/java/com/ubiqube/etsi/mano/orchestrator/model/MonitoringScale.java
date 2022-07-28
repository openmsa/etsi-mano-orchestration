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
package com.ubiqube.etsi.mano.orchestrator.model;

import java.util.List;

import com.ubiqube.etsi.mano.orchestrator.NamedDependency;

public class MonitoringScale implements ScaleModel {
	private final String name;

	public MonitoringScale(final String name) {
		this.name = name;
	}

	@Override
	public boolean isSingleNode() {
		return false;
	}

	@Override
	public List<NamedDependency> getDependencies() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int wantedInstance() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public ScaleModel clone(final int i) {
		return new MonitoringScale(name + i);
	}

}
