/**
 * Copyright (C) 2019-2025 Ubiqube.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.ubiqube.etsi.mano.orchestrator;

import org.jgrapht.ListenableGraph;

import com.ubiqube.etsi.mano.orchestrator.nodes.ConnectivityEdge;
import com.ubiqube.etsi.mano.orchestrator.uow.UnitOfWorkV3;

/**
 * Called after plan runner task, this is mainly for debugging dumping the
 * graph.
 *
 * @author Olivier Vignaud
 *
 */
public interface PostPlanRunner<U> {
	/**
	 * Called After the graph creation.
	 *
	 * @param createImplementation The graph.
	 */
	void runCreatePost(ListenableGraph<UnitOfWorkV3<U>, ConnectivityEdge<UnitOfWorkV3<U>>> createImplementation);

	/**
	 * Called after a delete action.
	 *
	 * @param deleteImplementation The delete graph.
	 */
	void runDeletePost(ListenableGraph<UnitOfWorkV3<U>, ConnectivityEdge<UnitOfWorkV3<U>>> deleteImplementation);

}
