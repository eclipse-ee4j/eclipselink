/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.models.employee;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <b>Purpose</b>: 		Abstract superclass for Large & Small projects in Employee Demo
 *	<p><b>Description</b>: 	Project is an example of an abstract superclass. It demonstrates how class inheritance can be mapped to database tables.
 * It's subclasses are concrete and may or may not add columns through additional tables. The PROJ_TYPE field in the
 * database table indicates which subclass to instantiate. Projects are involved in a M:M relationship with employees. 
 * The Employee classs maintains the definition of the relation table.
 *
 *	@see LargeProject
 *	@see SmallProject
 */

public abstract interface ProjectInterface extends Serializable {
String getDescription();
BigDecimal getId();
String getName();
EmployeeInterface getTeamLeader();
void setDescription(String description);
void setName(String name);
void setTeamLeader(EmployeeInterface teamLeader);
}
