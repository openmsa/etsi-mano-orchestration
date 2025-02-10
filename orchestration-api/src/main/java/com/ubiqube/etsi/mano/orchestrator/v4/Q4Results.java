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
