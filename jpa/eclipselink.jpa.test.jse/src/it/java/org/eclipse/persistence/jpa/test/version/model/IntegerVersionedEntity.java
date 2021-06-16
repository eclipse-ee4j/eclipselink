/*
 * Copyright (c) 2018, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     05/10/2018-master Joe Grassel
//       - Github#93: Bug with bulk update processing involving version field update parameter

package org.eclipse.persistence.jpa.test.version.model;

import jakarta.persistence.Basic;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Version;

@Entity
public class IntegerVersionedEntity {
	@Id
	private long id;
	
	@Basic
	private String data;
	
	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	@Version
	private int theVersion;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getTheVersion() {
		return theVersion;
	}

	public void setTheVersion(int theVersion) {
		this.theVersion = theVersion;
	}

	@Override
	public String toString() {
		return "IntegerVersionedEntity [id=" + id + ", data=" + data + ", theVersion=" + theVersion + "]";
	}

	
	
}
