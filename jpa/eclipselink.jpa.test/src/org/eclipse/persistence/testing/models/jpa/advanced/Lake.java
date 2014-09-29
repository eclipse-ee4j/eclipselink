/*******************************************************************************
 * Copyright (c) 1998, 2014 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     David Minsky - Oracle
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.advanced;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="CMP3_LAKE")
public class Lake {
	
	@Id
    @GeneratedValue
	protected int id;
	protected String name;
	
	public Lake() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String toString() {
		return getClass().getSimpleName() + " id:[" + id + "] name:[" + name + "] hashcode:[" + System.identityHashCode(this) + "]";
	}
	
}
