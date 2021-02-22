/*******************************************************************************
 * Copyright (c) 2020, 2021 Oracle and/or its affiliates. All rights reserved.
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

import javax.persistence.*;

import org.eclipse.persistence.annotations.ReturnInsert;
import org.eclipse.persistence.annotations.ReturnUpdate;

/**
 * The parent persistent class for the JPA22_RETURNINSERT_DETAIL_JOINED database table.
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
