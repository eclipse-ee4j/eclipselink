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

import java.io.Serializable;

import javax.persistence.*;

/**
 * The persistent class for the TEST_RETURNINSERT_MASTER database table.
 * 
 */
@Entity
@Table(name = "JPA22_RETURNINSERT_MASTER")
@Cacheable(false)
@NamedQueries({ 
	@NamedQuery(name = "ReturnInsertMaster.findAll", query = "SELECT t FROM ReturnInsertMaster t"),
})
public class ReturnInsertMaster implements Serializable {

	private static final long serialVersionUID = 1712864032706065027L;

	@EmbeddedId
	private ReturnInsertMasterPK id;

	@ReturnInsert(returnOnly = true)
	@Column(name = "COL1_VIRTUAL")
	private long col1Virtual;

	public ReturnInsertMaster() {
	}

	public ReturnInsertMasterPK getId() {
		return this.id;
	}

	public void setId(ReturnInsertMasterPK id) {
		this.id = id;
	}

	public long getCol1Virtual() {
		return col1Virtual;
	}

	public void setCol1Virtual(long col1Virtual) {
		this.col1Virtual = col1Virtual;
	}

}