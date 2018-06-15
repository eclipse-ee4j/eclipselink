/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2018 IBM and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     05/10/2018-master Joe Grassel
//       - Github#93: Bug with bulk update processing involving version field update parameter

package org.eclipse.persistence.jpa.test.version.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;

@Entity
public class TemporalVersionedEntity {
	@Id
	private long id;
	
	@Version
	private java.sql.Timestamp updatetimestamp;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public java.sql.Timestamp getUpdatetimestamp() {
		return updatetimestamp;
	}

	public void setUpdatetimestamp(java.sql.Timestamp version) {
		this.updatetimestamp = version;
	}

	@Override
	public String toString() {
		return "TemporalVersionedEntity [id=" + id + ", version=" + updatetimestamp + "]";
	}
	
	
}
