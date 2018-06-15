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
package org.eclipse.persistence.tools.workbench.test.models.complexaggregate;

import java.io.Serializable;

import org.eclipse.persistence.indirection.ValueHolder;
import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class Responsibility implements Serializable {
    public Number id;
    public String responsibility;
    public ValueHolderInterface employee;
public Responsibility()
{
    employee = new ValueHolder();
}
public static Responsibility example1(Employee anEmployee)
{
    Responsibility example = new Responsibility();

    example.setResponsibility("Project Leader");
    example.getEmployee().setValue(anEmployee);
    return example;
}
public static Responsibility example2(Employee anEmployee)
{
    Responsibility example = new Responsibility();

    example.setResponsibility("Team Leader");
    example.getEmployee().setValue(anEmployee);
    return example;
}
public static Responsibility example3(Employee anEmployee)
{
    Responsibility example = new Responsibility();

    example.setResponsibility("Module Leader");
    example.getEmployee().setValue(anEmployee);
    return example;
}
public static Responsibility example4(Employee anEmployee)
{
    Responsibility example = new Responsibility();

    example.setResponsibility("Developer");
    example.getEmployee().setValue(anEmployee);
    return example;
}
public static Responsibility example5(Employee anEmployee)
{
    Responsibility example = new Responsibility();

    example.setResponsibility("Network Administrator");
    example.getEmployee().setValue(anEmployee);
    return example;
}
public static Responsibility example6(Employee anEmployee)
{
    Responsibility example = new Responsibility();

    example.setResponsibility("Integrator");
    example.getEmployee().setValue(anEmployee);
    return example;
}
public ValueHolderInterface getEmployee()
{
    return employee;
}
public String getResponsibility() {
    return responsibility;
}
public void setResponsibility(String aString)
{
    responsibility = aString;
}
/**
 * Return a platform independant definition of the database table.
 */

public static TableDefinition tableDefinition()
{
    TableDefinition definition = new TableDefinition();

    definition.setName("AGG_RES");

    definition.addIdentityField("ID", java.math.BigDecimal.class, 15);
    definition.addField("DUTY", String.class, 30);
    definition.addField("EMP_ID", java.math.BigDecimal.class, 15);
    return definition;
}
}
