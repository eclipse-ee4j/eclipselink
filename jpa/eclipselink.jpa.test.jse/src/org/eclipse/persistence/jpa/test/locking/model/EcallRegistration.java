/*
 * Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2022 IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.jpa.test.locking.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Version;

@Entity
@NamedQuery(name="updateActiveEcallAvailableFlag", query="UPDATE EcallRegistration e SET e.ecallAvailableFlag = :flag WHERE e.pk = :pk")
public class EcallRegistration {

	@Id private String pk;

	private int ecallAvailableFlag;

	@Version @Column(name = "sys_update_timestamp")
	private Timestamp sysUpdateTimestamp;

	public EcallRegistration() { }
	
	public EcallRegistration(String pk, int ecallAvailableFlag) {
		this.pk = pk;
		this.ecallAvailableFlag = ecallAvailableFlag;
	}

	public String getPk() {
		return pk;
	}

	public void setPk(String pk) {
		this.pk = pk;
	}

	public int getEcallAvailableFlag() {
		return ecallAvailableFlag;
	}

	public void setEcallAvailableFlag(int ecallAvailableFlag) {
		this.ecallAvailableFlag = ecallAvailableFlag;
	}

	public Timestamp getSysUpdateTimestamp() {
		return this.sysUpdateTimestamp;
	}
	
	public void setSysUpdateTimestamp(Timestamp sysUpdateTimestamp) {
		this.sysUpdateTimestamp = sysUpdateTimestamp;
	}
}
