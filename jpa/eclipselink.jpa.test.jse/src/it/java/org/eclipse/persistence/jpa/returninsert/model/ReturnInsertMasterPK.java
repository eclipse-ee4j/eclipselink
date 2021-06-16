/*
 * Copyright (c) 2020, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
package org.eclipse.persistence.jpa.returninsert.model;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.*;

import org.eclipse.persistence.annotations.ReturnInsert;

/**
 * The primary key class for the TEST_RETURNINSERT_MASTER database table.
 * 
 */

@Embeddable
public class ReturnInsertMasterPK implements Serializable {

	private static final long serialVersionUID = -7028778781258939822L;

	@ReturnInsert(returnOnly = true)
	@Column(name = "ID_VIRTUAL")
	private long idVirtual;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ID")
	private java.util.Date id;

	@Column(name = "COL1")
	private long col1;

	public ReturnInsertMasterPK() {
	}

	public long getIdVirtual() {
		return idVirtual;
	}

	public void setIdVirtual(long idVirtual) {
		this.idVirtual = idVirtual;
	}

	public Date getId() {
		return id;
	}

	public void setId(Date id) {
		this.id = id;
	}

	public long getCol1() {
		return col1;
	}

	public void setCol1(long col1) {
		this.col1 = col1;
	}
}
