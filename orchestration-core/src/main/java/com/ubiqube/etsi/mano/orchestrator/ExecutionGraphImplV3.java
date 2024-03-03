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
package com.ubiqube.etsi.mano.orchestrator;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.jgrapht.ListenableGraph;

import com.ubiqube.etsi.mano.orchestrator.dump.Connection;
import com.ubiqube.etsi.mano.orchestrator.dump.ExecutionResult;
import com.ubiqube.etsi.mano.orchestrator.dump.Vertex;
import com.ubiqube.etsi.mano.orchestrator.exceptions.OrchestrationException;
import com.ubiqube.etsi.mano.orchestrator.nodes.ConnectivityEdge;
import com.ubiqube.etsi.mano.orchestrator.nodes.vnfm.Network;
import com.ubiqube.etsi.mano.orchestrator.scale.ContextVt;
import com.ubiqube.etsi.mano.orchestrator.uow.ContextUow;
import com.ubiqube.etsi.mano.orchestrator.uow.UnitOfWorkV3;
import com.ubiqube.etsi.mano.orchestrator.v4.api.Selector;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTaskV3;

import jakarta.annotation.Nonnull;

/**
 *
 * @author olivier
 *
 * @param <U>
 */
public class ExecutionGraphImplV3<U> implements ExecutionGraph {
	@Nonnull
	private final ListenableGraph<UnitOfWorkV3<U>, ConnectivityEdge<UnitOfWorkV3<U>>> g;
	@Nonnull
	private final List<ContextUow<U>> global = new ArrayList<>();

	public ExecutionGraphImplV3(final ListenableGraph<UnitOfWorkV3<U>, ConnectivityEdge<UnitOfWorkV3<U>>> g) {
		this.g = g;
	}

	public ListenableGraph<UnitOfWorkV3<U>, ConnectivityEdge<UnitOfWorkV3<U>>> getGraph() {
		return g;
	}

	@Override
	public void add(final Class<Network> type, final String toscaName, final String resourceId) {
		global.add(new ContextUow<>(new ContextVt<>(ManoSelector.of(type, toscaName), resourceId)));
	}

	public final List<ContextUow<U>> getGlobal() {
		return global;
	}

	@Override
	public ExecutionResult dump() {
		final List<Vertex> vertex = g.vertexSet().stream().map(this::map).toList();
		final List<Connection> links = g.edgeSet().stream().map(this::map).toList();
		return new ExecutionResult(vertex, links);
	}

	private Vertex map(final UnitOfWorkV3<U> v) {
		final VirtualTaskV3<U> vt = v.getParameters();
		return Vertex.builder()
				.id(makeKey(vt.getSelector()))
				.selector(vt.getSelector())
				.status(statusConvert(vt))
				.vimConnectionId(vt.getVimConnectionId())
				.vimResourceId(vt.getVimResourceId())
				.build();
	}

	private static String makeKey(final Selector selector) {
		final String tmp = "%s-%04d-%s".formatted(selector.getName(), selector.getRank(), selector.getType().getSimpleName());
		try {
			final MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(tmp.getBytes());
			return String.format("%032x", new BigInteger(1, md.digest()));
		} catch (final NoSuchAlgorithmException e) {
			throw new OrchestrationException(e);
		}
	}

	private String statusConvert(final VirtualTaskV3<U> vt) {
		if (vt.getVimResourceId() != null) {
			return "SUCCESS";
		}
		return "FAILED";
	}

	private Connection map(final ConnectivityEdge<UnitOfWorkV3<U>> conn) {
		final VirtualTaskV3<U> src = conn.getSource().getParameters();
		final VirtualTaskV3<U> tgt = conn.getTarget().getParameters();
		return new Connection(makeKey(src.getSelector()), makeKey(tgt.getSelector()));
	}
}
