/*******************************************************************************
 * Copyright (c) 2015 IBM Corporation. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     12/19/2014 - Dalia Abo Sheasha
 *       - 454917 : Added a test to use the IDENTITY strategy to generate values
 ******************************************************************************/

package org.eclipse.persistence.jpa.test.basic.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name="oepjtbmEmployee")
@NamedQuery(name="Employee.findAll", query="SELECT e FROM Employee e")
public class Employee {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	int id;

	@Version
	int version;

	public Employee() {

	}

	public int getId() {
		return id;
	}
}
