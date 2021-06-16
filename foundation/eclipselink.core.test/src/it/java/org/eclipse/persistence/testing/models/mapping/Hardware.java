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
package org.eclipse.persistence.testing.models.mapping;

import java.io.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;
import org.eclipse.persistence.expressions.*;

public class Hardware implements Serializable {
    public java.math.BigDecimal id;
    public String distibuted;
    public Employee employee;

    public static void addToDescriptor(ClassDescriptor des) {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression exp = builder.get("distibuted").equal("false");

        des.getQueryManager().setAdditionalJoinExpression(exp);
    }

    public String getDist() {
        return this.distibuted;
    }

    public void setDist(String dist) {
        this.distibuted = dist;
    }

    public void setEmployee(Employee anEmployee) {
        employee = anEmployee;
    }

    public static TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("MAP_HRW");
        definition.addIdentityField("ID", java.math.BigDecimal.class, 15);
        definition.addField("DIST", String.class, 5);
        definition.addField("TYPE", String.class, 5);
        definition.addField("EMP_LNAME", String.class, 25);
        definition.addField("EMP_FNAME", String.class, 25);

        definition.addForeignKeyConstraint("MAP_HRW_MAP_EMP", "EMP_FNAME,EMP_LNAME", "FNAME,LNAME", "MAP_EMP");

        return definition;
    }
}
