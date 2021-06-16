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
import org.eclipse.persistence.annotations.ReturnUpdate;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * ReturnInsertDetailEmbedded instance to be {@link Embedded} into other entities.
 */
@Embeddable
public class ReturnInsertDetailEmbedded implements Serializable {

	private static final long serialVersionUID = 1712864132706065027L;


	@ReturnInsert(returnOnly = true)
	@Column(name = "COL2_VIRTUAL")
	private String col2Virtual;

	@Column(name = "COL3")
	private String col3;

	@ReturnInsert(returnOnly = true)
	@ReturnUpdate
	@Column(name = "COL3_VIRTUAL")
	private String col3Virtual;

	@Embedded
	private ReturnInsertDetailEmbeddedEmbedded returnInsertDetailEmbeddedEmbedded;

	public ReturnInsertDetailEmbedded() {
	}

	public String getCol2Virtual() {
		return col2Virtual;
	}

	public void setCol2Virtual(String col2Virtual) {
		this.col2Virtual = col2Virtual;
	}

	public String getCol3() {
		return col3;
	}

	public void setCol3(String col3) {
		this.col3 = col3;
	}

	public String getCol3Virtual() {
		return col3Virtual;
	}

	public void setCol3Virtual(String col3Virtual) {
		this.col3Virtual = col3Virtual;
	}

	public ReturnInsertDetailEmbeddedEmbedded getReturnInsertDetailEmbeddedEmbedded() {
		return returnInsertDetailEmbeddedEmbedded;
	}

	public void setReturnInsertDetailEmbeddedEmbedded(ReturnInsertDetailEmbeddedEmbedded returnInsertDetailEmbeddedEmbedded) {
		this.returnInsertDetailEmbeddedEmbedded = returnInsertDetailEmbeddedEmbedded;
	}
}
