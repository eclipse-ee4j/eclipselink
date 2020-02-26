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

import javax.persistence.*;
import java.io.Serializable;

/**
 * The parent persistent class for the JPA22_RETURNINSERT_DETAIL database table.
 * 
 */
@MappedSuperclass
public class ReturnInsertDetailParent implements Serializable {

	private static final long serialVersionUID = 1712864132706165027L;

	@Column(name = "COL4")
	private String col4;

	@ReturnInsert(returnOnly = true)
	@Column(name = "COL4_VIRTUAL")
	private String col4Virtual;

	public String getCol4() {
		return col4;
	}

	public void setCol4(String col4) {
		this.col4 = col4;
	}

	public String getCol4Virtual() {
		return col4Virtual;
	}

	public void setCol4Virtual(String col4Virtual) {
		this.col4Virtual = col4Virtual;
	}
}