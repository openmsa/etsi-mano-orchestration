package com.ubiqube.etsi.mano.orchestrator.v4;

import java.util.List;
import java.util.concurrent.ExecutorService;

import org.jgrapht.ListenableGraph;
import org.jgrapht.graph.DefaultListenableGraph;
import org.jgrapht.graph.DirectedAcyclicGraph;

import com.github.dexecutor.core.DefaultDexecutor;
import com.github.dexecutor.core.DexecutorConfig;
import com.github.dexecutor.core.ExecutionConfig;
import com.github.dexecutor.core.task.ExecutionResult;
import com.github.dexecutor.core.task.ExecutionResults;
import com.ubiqube.etsi.mano.orchestrator.v4.graph.Q4Edge;
import com.ubiqube.etsi.mano.orchestrator.v4.graph.Q4GraphListener;

public class Q4WorkflowImpl implements Q4Workflow {
	private final ListenableGraph<Q4Task, Q4Edge> graph;

	public Q4WorkflowImpl() {
		graph = new DefaultListenableGraph<>(new DirectedAcyclicGraph<>(Q4Edge.class));
		graph.addGraphListener(new Q4GraphListener());
	}

	@Override
	public Q4Results run(final ExecutorService executorService) {
		Q4TaskProviderImpl taskProvider = new Q4TaskProviderImpl(null);
		DexecutorConfig<Q4Task, String> config = new DexecutorConfig<>(executorService, taskProvider);
		final DefaultDexecutor<Q4Task, String> executor = new DefaultDexecutor<>(config);
		addRoot(executor);
		graph.edgeSet().forEach(x -> executor.addDependency(x.getSource(), x.getTarget()));
		final ExecutionResults<Q4Task, String> res = executor.execute(ExecutionConfig.TERMINATING);
		return map(res);
	}

	private Q4Results map(final ExecutionResults<Q4Task, String> res) {
		List<Q4Result> lst = res.getAll().stream().map(this::map).toList();
		return Q4Results.of(lst);
	}

	private Q4Result map(final ExecutionResult<Q4Task, String> x) {
		Q4Result result = new Q4Result();
		result.setId(x.getId());
		result.setResult(x.getResult());
		result.setStatus(Q4ResultStatus.valueOf(x.getStatus().toString()));
		result.setMessage(x.getMessage());
		result.setStartTime(x.getStartTime());
		result.setEndTime(x.getEndTime());
		return result;
	}

	private void addRoot(final DefaultDexecutor<Q4Task, String> executor) {
		graph.vertexSet().forEach(x -> {
			if (graph.incomingEdgesOf(x).isEmpty() && graph.outgoingEdgesOf(x).isEmpty()) {
				executor.addIndependent(x);
			}
		});
	}

	@Override
	public void addVertex(final Q4Task task) {
		graph.addVertex(task);

	}

	@Override
	public void addEdge(final Q4Task from, final Q4Task to) {
		graph.addVertex(from);
		graph.addVertex(to);
		graph.addEdge(from, to);
	}

}
