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
 *     along with this program.  If not, see https://www.gnu.org/licenses/.
 */
package com.ubiqube.etsi.mano.orchestrator.v3;

import com.ubiqube.etsi.mano.orchestrator.OrchExecutionListener;
import com.ubiqube.etsi.mano.orchestrator.uow.UnitOfWorkV3;

public class OrchTestListener implements OrchExecutionListener<Object> {

	@Override
	public void onStart(final UnitOfWorkV3<Object> virtualTask) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTerminate(final UnitOfWorkV3<Object> uaow, final String res) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onError(final UnitOfWorkV3<Object> uaow, final RuntimeException e) {
		// TODO Auto-generated method stub

	}

}
