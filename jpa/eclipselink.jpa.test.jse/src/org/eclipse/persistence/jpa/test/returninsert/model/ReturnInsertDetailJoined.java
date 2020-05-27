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

import javax.persistence.*;

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