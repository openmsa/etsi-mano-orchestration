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
package com.ubiqube.etsi.mano.orchestrator.entities;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Olivier Vignaud {@literal <ovi@ubiqube.com>}
 *
 */

/**
 * Represents a system entity with a unique identifier, name, VIM origin, VIM ID, and a set of subsystem connections.
 */
@Getter
@Setter
@Entity
public class Systems {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID id = null;

	private String name;

	private UUID vimOrigin;

	private String vimId;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private Set<SystemConnections> subSystems;

	/**
	 * Adds a subsystem connection to the set of subsystem connections.
	 *
	 * @param sc the subsystem connection to add
	 */
	public void add(final SystemConnections sc) {
		if (null == subSystems) {
			subSystems = new LinkedHashSet<>();
		}
		subSystems.add(sc);
	}
}
