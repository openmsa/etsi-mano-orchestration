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

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestTask implements Q4Task {
	/** Logger. */
	private static final Logger LOG = LoggerFactory.getLogger(TestTask.class);
	private final String message;

	public TestTask(final String message) {
		this.message = message;
	}

	@Override
	public String execute(final Q4Context context3d) {
		LOG.info("Executing task: {}", message);
		return UUID.randomUUID().toString();
	}

	@Override
	public String rollback(final Q4Context context3d) {
		// TODO Auto-generated method stub
		return null;
	}

}
