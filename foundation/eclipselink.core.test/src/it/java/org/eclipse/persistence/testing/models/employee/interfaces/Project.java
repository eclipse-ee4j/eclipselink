/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.employee.interfaces;

import java.math.BigDecimal;
import java.io.Serializable;

/**
 * <b>Purpose</b>:         Abstract superclass for Large & Small projects in Employee Demo
 *    <p><b>Description</b>:     Project is an example of an abstract superclass. It demonstrates how class inheritance can be mapped to database tables.
 * It's subclasses are concrete and may or may not add columns through additional tables. The PROJ_TYPE field in the
 * database table indicates which subclass to instantiate. Projects are involved in a M:M relationship with employees.
 * The Employee classs maintains the definition of the relation table.
 *
 *    @see LargeProject
 *    @see SmallProject
 */
public abstract interface Project extends Serializable {
    String getDescription();

    BigDecimal getId();

    String getName();

    org.eclipse.persistence.testing.models.employee.interfaces.Employee getTeamLeader();

    void setDescription(String description);

    void setName(String name);

    void setTeamLeader(org.eclipse.persistence.testing.models.employee.interfaces.Employee teamLeader);
}
