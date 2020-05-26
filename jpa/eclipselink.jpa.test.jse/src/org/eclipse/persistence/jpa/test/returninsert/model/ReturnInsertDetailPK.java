/*******************************************************************************
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 ******************************************************************************/
package org.eclipse.persistence.jpa.test.returninsert.model;

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