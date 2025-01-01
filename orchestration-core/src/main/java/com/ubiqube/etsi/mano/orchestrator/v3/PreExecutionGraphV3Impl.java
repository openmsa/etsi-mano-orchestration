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
package com.ubiqube.etsi.mano.orchestrator.v3;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.jgrapht.ListenableGraph;
import org.jgrapht.nio.dot.DOTExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ubiqube.etsi.mano.orchestrator.GraphTools;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTaskConnectivityV3;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTaskV3;

import org.jspecify.annotations.NonNull;

/**
 *
 * @author olivier
 *
 * @param <U>
 */
public class PreExecutionGraphV3Impl<U> implements PreExecutionGraphV3<U> {
	private static final Logger LOG = LoggerFactory.getLogger(PreExecutionGraphV3Impl.class);
	@NonNull
	private final ListenableGraph<VirtualTaskV3<U>, VirtualTaskConnectivityV3<U>> network;

	public PreExecutionGraphV3Impl(final ListenableGraph<VirtualTaskV3<U>, VirtualTaskConnectivityV3<U>> network) {
		this.network = network;
	}

	public ListenableGraph<VirtualTaskV3<U>, VirtualTaskConnectivityV3<U>> getGraph() {
		return network;
	}

	@Override
	public List<VirtualTaskV3<U>> getPreTasks() {
		return network.vertexSet().stream().toList();
	}

	@Override
	public void toDotFile(final String filename) {
		final DOTExporter<VirtualTaskV3<U>, VirtualTaskConnectivityV3<U>> exporter = new DOTExporter<>(GraphTools::toDotName);
		try (final FileOutputStream out = new FileOutputStream(filename)) {
			exporter.exportGraph(network, out);
		} catch (final IOException e) {
			LOG.trace("Error in graph export", e);
		}
	}
}
