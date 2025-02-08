package com.ubiqube.etsi.mano.orchestrator.v4;

import java.util.List;

import lombok.Data;

@Data
public class Q4Results {

	private final List<Q4Result> results;

	public Q4Results(final List<Q4Result> results) {
		this.results = results;
	}

	public static Q4Results of(final List<Q4Result> results) {
		return new Q4Results(results);
	}

	public List<Q4Result> getErrors() {
		return results.stream().filter(x -> Q4ResultStatus.ERRORED.equals(x.getStatus())).toList();
	}

	public List<Q4Result> getSucess() {
		return results.stream().filter(x -> Q4ResultStatus.SUCCESS.equals(x.getStatus())).toList();
	}
}
