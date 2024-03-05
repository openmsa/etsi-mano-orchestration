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

import java.util.Arrays;
import java.util.stream.Collectors;

public class NamedResource {

	private final String baseName;
	private final String[] dim;

	public NamedResource(final String baseName, final String[] dim) {
		this.baseName = baseName;
		this.dim = dim;
	}

	public static NamedResource of(final String baseNAme, final String... dim) {
		return new NamedResource(baseNAme, dim);
	}

	@Override
	public String toString() {
		final String dimExpand = Arrays.stream(dim).collect(Collectors.joining("_"));
		if (dimExpand.isEmpty()) {
			return baseName;
		}
		return baseName + "_" + dimExpand;
	}

	public String toBaseName() {
		return baseName;
	}
}
