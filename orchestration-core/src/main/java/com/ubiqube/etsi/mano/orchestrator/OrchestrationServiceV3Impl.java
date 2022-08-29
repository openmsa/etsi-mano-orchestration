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

import org.springframework.stereotype.Service;

import com.ubiqube.etsi.mano.orchestrator.uow.UnitOfWorkV3;

@Service
public class OrchestrationServiceV3Impl<U> implements OrchestrationServiceV3<U> {

	@Override
	public SystemBuilder<UnitOfWorkV3<U>> systemBuilderOf(final UnitOfWorkV3<U> uow) {
		return (SystemBuilder<UnitOfWorkV3<U>>) SystemBuilderV3Impl.of(uow);
	}

	@Override
	public SystemBuilder<UnitOfWorkV3<U>> systemBuilderOf(final UnitOfWorkV3<U> left, final UnitOfWorkV3<U> right) {
		return null;
		// SystemBuilderImpl.of(left, right)
	}

	@Override
	public SystemBuilder<UnitOfWorkV3<U>> createEmptySystemBuilder() {
		return new SystemBuilderV3Impl<>();
	}

}
