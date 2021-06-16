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

import java.io.Serializable;

import jakarta.persistence.*;

/**
 * The persistent class for the JPA22_RETURNINSERT_MASTER_JOINED database table.
 * 
 */
@Entity
@Table(name = "JPA22_RETURNINSERT_MASTER_JOINED")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@SequenceGenerator(name = "AbstractEntityObjectSEQ_STORE", sequenceName = "TEST_RETURNINSERT_MASTER_JOINED_ID_SEQ", allocationSize = 1)
public class ReturnInsertMasterJoined implements Serializable {

	@Id
	private Long id;

	private String type;

	public ReturnInsertMasterJoined() {
	}

	public ReturnInsertMasterJoined(Long id, String type) {
		this.id = id;
		this.type = type;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
