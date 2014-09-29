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

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="CMP3_CANOE")
public class Canoe {

	@Id
    @GeneratedValue
	protected int id;
	
	protected String color;
	
	@OneToOne(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinColumn(name="LAKE_ID")
	protected Lake lake;
	
	public Canoe() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public Lake getLake() {
		return lake;
	}

	public void setLake(Lake lake) {
		this.lake = lake;
	}
	
	public String toString() {
		return getClass().getSimpleName() + " id:[" + id + "] color:[" + color + "] hashcode:[" + System.identityHashCode(this) + "]";
	}
	
}
