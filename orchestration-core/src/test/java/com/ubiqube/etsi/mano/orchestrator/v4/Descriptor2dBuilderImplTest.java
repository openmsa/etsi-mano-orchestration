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
package com.ubiqube.etsi.mano.orchestrator.v4;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jgrapht.ListenableGraph;
import org.jgrapht.graph.DefaultListenableGraph;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ubiqube.etsi.mano.mapper.BeanWalker;
import com.ubiqube.etsi.mano.orchestrator.Relation;
import com.ubiqube.etsi.mano.orchestrator.exceptions.OrchestrationException;
import com.ubiqube.etsi.mano.orchestrator.v3.Vertex2dLink;
import com.ubiqube.etsi.mano.orchestrator.v4.model.Compute;
import com.ubiqube.etsi.mano.orchestrator.v4.model.Monitoring;
import com.ubiqube.etsi.mano.orchestrator.v4.model.Network;
import com.ubiqube.etsi.mano.orchestrator.v4.model.PackageModel;
import com.ubiqube.etsi.mano.orchestrator.v4.model.Port;
import com.ubiqube.etsi.mano.orchestrator.v4.model.Storage;
import com.ubiqube.etsi.mano.orchestrator.v4.model.SubNetwork;
import com.ubiqube.etsi.mano.orchestrator.v4.ref.OrchReference;

class Descriptor2dBuilderImplTest {
	private static final Logger LOG = LoggerFactory.getLogger(Descriptor2dBuilderImplTest.class);

	@Test
	void test() {
		final Vertex2dDescriptor networks = Descriptor2dBuilderImpl.of(Network.class)
				.identifiedBy(x -> x.getName())
				.type("network")
				.build();
		final Vertex2dDescriptor subNetworks = Descriptor2dBuilderImpl.of(SubNetwork.class)
				.identifiedBy(x -> x.getName())
				.dependencies(x -> x.self().haveOne("network").build())
				.type("network")
				.build();
		final Vertex2dDescriptor ports = Descriptor2dBuilderImpl.of(Port.class)
				.identifiedBy(x -> x.getName())
				.type("port")
				.scalableWith("compute")
				.dependencies(x -> x.self().haveOne("compute")
						.fromProperty(y -> y.getVirtualBinding()).haveOne("network")
						.fromProperty(y -> y.getVirtualLink()).haveOne("network")
						.build())
				.build();
		final Vertex2dDescriptor comp = Descriptor2dBuilderImpl.of(Compute.class)
				.identifiedBy(x -> x.getName())
				.type("compute")
				.dependencies(x -> x
						.fromRef(y -> y.getStorage()).haveMany("storage")
						.from(y -> y.getMonitorings()).haveMany("monitoring")
						.from(y -> y.getPorts()).haveMany("ports")
						.build())
				.build();
		final Vertex2dDescriptor mon = Descriptor2dBuilderImpl.of(Monitoring.class)
				.identifiedBy(x -> x.getName())
				.scalableWith("compute")
				.type("monitoring")
				.dependencies(x -> x.from("").haveMany("compute").build())
				.build();
		System.out.println(comp);
		final Vertex2dDescriptor storage = Descriptor2dBuilderImpl.of(Storage.class)
				.identifiedBy(x -> x.getName())
				.scalableWith("compute")
				.type("storage")
				.dependencies(x -> x.self().haveOne("compute").build())
				.build();
		check(PackageModel.class, List.of(comp, storage));
		getGraph(PackageModel.class, List.of(comp, storage));
	}

	private <U> void getGraph(final Class<U> clazz, final List<Vertex2dDescriptor> v2) {
		final Map<String, Vertex2dDescriptor> types = v2.stream().collect(Collectors.toMap(x -> x.type(), x -> x));
		final ListenableGraph<Vertex2dDescriptor, Vertex2dLink> g = new DefaultListenableGraph(new DirectedAcyclicGraph<>(Vertex2dLink.class));
		g.addGraphListener(new Vertex2dDescriptorListener());
		v2.forEach(g::addVertex);
		v2.forEach(x -> {
			x.dependencies().forEach(y -> {
				final Vertex2dDescriptor target = types.get(y.name());
				final Vertex2dLink edge = g.getEdge(target, x);
				LOG.debug("Edge {} => {}", x, target);
				if (null == edge) {
					g.addEdge(x, target).setRelation(Relation.ONE_TO_MANY);
				} else {
					edge.setRelation(null);
				}
			});
		});
	}

	private Relation toRealtional(final OrchReference ref) {
		// TODO Auto-generated method stub
		return null;
	}

	private void checkModel(final Class<PackageModel> class1, final List<Vertex2dDescriptor> v2) {
		final Map<Class<?>, Vertex2dDescriptor> types = v2.stream().collect(Collectors.toMap(x -> x.claz(), x -> x));
		final BeanWalker bw = new BeanWalker();
		bw.walk(bw, null);
	}

	private static void check(final Class<PackageModel> class1, final List<Vertex2dDescriptor> v2) {
		final Map<String, Vertex2dDescriptor> types = v2.stream().collect(Collectors.toMap(x -> x.type(), x -> x));

		v2.stream().forEach(x -> {
			x.dependencies().stream().forEach(y -> {
				if (types.get(y.name()) == null) {
					throw new OrchestrationException("");
				}
			});
		});

	}

}
