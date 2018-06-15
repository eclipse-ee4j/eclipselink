/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.test.models.complexmapping;

import java.io.Serializable;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class Hardware implements Serializable {
    public java.math.BigDecimal id;
    public String distibuted;
    public Employee employee;
    public static void addToDescriptor(ClassDescriptor des){
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression exp = builder.get("distibuted").equal("false");

        des.getQueryManager().setAdditionalJoinExpression(exp);
    }
    public String getDist(){
        return this.distibuted;
    }
    public void setDist(String dist){
        this.distibuted = dist;
    }
public void setEmployee(Employee anEmployee)
{
    employee = anEmployee;
}
public static TableDefinition tableDefinition() {
    TableDefinition definition = new TableDefinition();

    definition.setName("MAP_HRW");
    definition.addPrimaryKeyField("ID", java.math.BigDecimal.class, 15);
    definition.addField("DIST", String.class, 5);
    definition.addField("TYPE",String.class, 5);
    definition.addField("EMP_LNAME",String.class, 25);
    definition.addField("EMP_FNAME",String.class, 25);

    return definition;
}
}
