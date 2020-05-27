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

import jakarta.persistence.*;

/**
 * The parent persistent class for the JPA22_RETURNINSERT_DETAIL_JOINED database table.
 * 
 */
@Entity
@Table(name = "JPA22_RETURNINSERT_DETAIL_JOINED")
@PrimaryKeyJoinColumn(name = "id")
@DiscriminatorValue("A")
public class ReturnInsertDetailJoined extends ReturnInsertMasterJoined {

	@Column(name = "detail_nr", nullable = false, unique = true, updatable = false)
	@ReturnInsert(returnOnly = true)
	Long detailNumber = null;

	public ReturnInsertDetailJoined() {
	}

	public ReturnInsertDetailJoined(Long id, String type) {
		super(id, type);
	}

	public Long getDetailNumber() {
		return detailNumber;
	}

	public void setDetailNumber(Long detailNumber) {
		this.detailNumber = detailNumber;
	}
}