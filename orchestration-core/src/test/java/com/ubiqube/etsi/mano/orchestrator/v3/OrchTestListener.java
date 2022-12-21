package com.ubiqube.etsi.mano.orchestrator.v3;

import com.ubiqube.etsi.mano.orchestrator.OrchExecutionListener;
import com.ubiqube.etsi.mano.orchestrator.uow.UnitOfWorkV3;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTaskV3;

public class OrchTestListener implements OrchExecutionListener<Object> {

	@Override
	public void onStart(final VirtualTaskV3<Object> virtualTask) {
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
