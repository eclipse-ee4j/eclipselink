/*
 * Copyright (c) 2020, 2021 Oracle and/or its affiliates. All rights reserved.
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

import jakarta.persistence.*;

import org.eclipse.persistence.annotations.ReturnUpdate;
import org.eclipse.persistence.annotations.ReturnInsert;

/**
 * The parent persistent class for the JPA22_RETURNINSERT_DETAIL_JOINED database table.
 * 
 */
@Entity
@Table(name = "JPA22_RETURNINSERT_DETAIL_JOINED")
@PrimaryKeyJoinColumn(name = "id")
@DiscriminatorValue("A")
public class ReturnInsertDetailJoined extends ReturnInsertMasterJoined {

	@Column(name = "detail_nr")
	Long detailNumber = null;

	@Column(name = "detail_nr_virtual", nullable = false, unique = true, updatable = false)
	@ReturnInsert(returnOnly = true)
	@ReturnUpdate
	Long detailNumberVirtual = null;

	public ReturnInsertDetailJoined() {
	}

	public ReturnInsertDetailJoined(Long id, Long detailNumber, String type) {
		super(id, type);
		this.detailNumber = detailNumber;
	}

	public Long getDetailNumber() {
		return detailNumber;
	}

	public void setDetailNumber(Long detailNumber) {
		this.detailNumber = detailNumber;
	}

	public Long getDetailNumberVirtual() {
		return detailNumberVirtual;
	}

	public void setDetailNumberVirtual(Long detailNumber) {
		this.detailNumberVirtual = detailNumber;
	}
}