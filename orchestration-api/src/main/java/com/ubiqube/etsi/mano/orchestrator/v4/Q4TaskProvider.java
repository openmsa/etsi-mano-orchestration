package com.ubiqube.etsi.mano.orchestrator.v4;

public interface Q4TaskProvider<U extends Q4Task> {

	Q4Task provideTask(final U id, Q4Context context);
}
