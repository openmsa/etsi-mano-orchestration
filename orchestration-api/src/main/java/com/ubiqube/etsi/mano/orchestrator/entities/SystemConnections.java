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

import java.util.Map;

import com.ubiqube.etsi.mano.dao.mano.AccessInfo;
import com.ubiqube.etsi.mano.dao.mano.Connection;
import com.ubiqube.etsi.mano.dao.mano.InterfaceInfo;

import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

/**
 * Describe a connection to VIM system.
 * <P>
 * <b>WARNING</b>: This is not a full vim, it can represent just the access to
 * an openstack for the neutron part.
 *
 * @author Olivier Vignaud {@literal <ovi@ubiqube.com>}
 *
 */
@Getter
@Setter
@Entity
public class SystemConnections<I extends InterfaceInfo, A extends AccessInfo> extends Connection<I, A> {

	private static final long serialVersionUID = 1L;

	/**
	 * Identifier of the VIM.
	 */
	private String vimId;

	/**
	 * Type of the VIM.
	 */
	private String vimType;

	/**
	 * Name of the module.
	 */
	private String moduleName;

	/**
	 * Information about the interface.
	 */
	@OneToOne(cascade = CascadeType.ALL, targetEntity = InterfaceInfo.class)
	private I interfaceInfo;

	/**
	 * Information about the access.
	 */
	@OneToOne(cascade = CascadeType.ALL, targetEntity = AccessInfo.class)
	private A accessInfo;

	/**
	 * Additional properties.
	 */
	@ElementCollection(fetch = FetchType.EAGER)
	private Map<String, String> extra;

}
