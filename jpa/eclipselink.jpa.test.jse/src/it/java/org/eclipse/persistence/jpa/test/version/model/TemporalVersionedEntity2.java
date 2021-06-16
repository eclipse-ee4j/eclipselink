/*
 * Copyright (c) 2018, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     05/10/2018-master Joe Grassel
//       - Github#93: Bug with bulk update processing involving version field update parameter

package org.eclipse.persistence.jpa.test.version.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Version;

@Entity
public class TemporalVersionedEntity2 {
	@Id
	private long id;
	
	@Version
	private java.sql.Timestamp version;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public java.sql.Timestamp getVersion() {
		return version;
	}

	public void setVersion(java.sql.Timestamp version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return "TemporalVersionedEntity [id=" + id + ", version=" + version + "]";
	}
	
	
}
