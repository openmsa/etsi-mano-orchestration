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

import java.util.List;

import com.ubiqube.etsi.mano.orchestrator.nodes.ConnectivityEdge;
import com.ubiqube.etsi.mano.orchestrator.uow.UnitOfWork;
import com.ubiqube.etsi.mano.orchestrator.uow.UnitOfWorkV3;

/**
 *
 * @author Olivier Vignaud <ovi@ubiqube.com>
 *
 */
public interface SystemBuilder<U> {
	List<ConnectivityEdge<UnitOfWork<U>>> getEdges();

	UnitOfWork<U> getSingle();

	List<UnitOfWork<U>> getIncomingVertex();

	List<UnitOfWork<U>> getOutgoingVertex();

	void add(UnitOfWork<U> src, UnitOfWork<U> dest);

	void add(UnitOfWorkV3<U> src, UnitOfWorkV3<U> dest);

	List<UnitOfWork<U>> getVertex();

}
