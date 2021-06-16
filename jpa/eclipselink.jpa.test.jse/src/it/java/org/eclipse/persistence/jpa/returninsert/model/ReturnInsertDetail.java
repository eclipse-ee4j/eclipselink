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

import org.eclipse.persistence.annotations.ReturnInsert;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * The persistent class for the JPA22_RETURNINSERT_DETAIL database table.
 * 
 */
@Entity
@Table(name = "JPA22_RETURNINSERT_DETAIL")
@Cacheable(false)
@NamedQueries({
	@NamedQuery(name = "ReturnInsertDetail.findAll", query = "SELECT t FROM ReturnInsertDetail t"),
})
public class ReturnInsertDetail extends ReturnInsertDetailParent implements Serializable {

	private static final long serialVersionUID = 1712864132706065027L;

	@EmbeddedId
	private ReturnInsertDetailPK id;

	@ReturnInsert(returnOnly = true)
	@Column(name = "COL1_VIRTUAL")
	private long col1Virtual;

	@Embedded
	private ReturnInsertDetailEmbedded returnInsertDetailEmbedded;

	@OneToOne(cascade = { CascadeType.MERGE, CascadeType.PERSIST })
	@JoinColumns({
			@JoinColumn(name = "ID_VIRTUAL", referencedColumnName = "ID_VIRTUAL", insertable = false, updatable = false),
			@JoinColumn(name = "ID", referencedColumnName = "ID", insertable = false, updatable = false),
			@JoinColumn(name = "COL1", referencedColumnName = "COL1", insertable = false, updatable = false)
	})
	private ReturnInsertMaster returnInsertMaster;

	public ReturnInsertDetail() {
	}

	public ReturnInsertDetailPK getId() {
		return this.id;
	}

	public void setId(ReturnInsertDetailPK id) {
		this.id = id;
	}

	public long getCol1Virtual() {
		return col1Virtual;
	}

	public void setCol1Virtual(long col1Virtual) {
		this.col1Virtual = col1Virtual;
	}

	public ReturnInsertDetailEmbedded getReturnInsertDetailEmbedded() {
		return returnInsertDetailEmbedded;
	}

	public void setReturnInsertDetailEmbedded(ReturnInsertDetailEmbedded returnInsertDetailEmbedded) {
		this.returnInsertDetailEmbedded = returnInsertDetailEmbedded;
	}

	public ReturnInsertMaster getReturnInsertMaster() {
		return returnInsertMaster;
	}

	public void setReturnInsertMaster(ReturnInsertMaster returnInsertMaster) {
		this.returnInsertMaster = returnInsertMaster;
	}
}
