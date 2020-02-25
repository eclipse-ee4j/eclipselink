/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation
package org.eclipse.persistence.jpa.returninsert.model;

import org.eclipse.persistence.annotations.ReturnInsert;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

/**
 * The primary key class for the TEST_RETURNINSERT_DETAIL database table.
 * 
 */

@Embeddable
public class ReturnInsertDetailPK implements Serializable {

	private static final long serialVersionUID = -7028778781258139822L;

	@ReturnInsert(returnOnly = true)
	@Column(name = "ID_VIRTUAL")
	private long idVirtual;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ID")
	private Date id;

	@Column(name = "COL1")
	private long col1;

	@Column(name = "COL2")
	private String col2;

	public ReturnInsertDetailPK() {
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

	public String getCol2() {
		return col2;
	}

	public void setCol2(String col2) {
		this.col2 = col2;
	}
}