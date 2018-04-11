/*******************************************************************************
 * Copyright (c) 2018 IBM Corporation. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     04/11/2018 - Will Dazey
 *       - 533148 : Add the eclipselink.jpa.sql-call-deferral property
 ******************************************************************************/
package org.eclipse.persistence.jpa.test.property.model;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "PARENT")
@PrimaryKeyJoinColumn(name="PARENT_PK", referencedColumnName="ABSTRACTPARENT_PK")
public class Parent extends AbstractParent {
}
