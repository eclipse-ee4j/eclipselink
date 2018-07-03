/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.test.models.complexaggregate;

import java.io.Serializable;
import java.util.Vector;

import org.eclipse.persistence.indirection.ValueHolder;
import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.tools.schemaframework.PopulationManager;

public class ProjectDescription implements Serializable {
    public Number id;
    public String description;
    public ValueHolderInterface computer;
    public ValueHolderInterface languages;
    public ValueHolderInterface responsibilities;
public ProjectDescription()
{
    computer    = new ValueHolder();
    responsibilities = new ValueHolder();
    languages = new ValueHolder();
}
public static ProjectDescription example1(Employee anEmployee)
{
    ProjectDescription example = new ProjectDescription();
    Vector responsibilities = new Vector(),
           languages = new Vector();
    PopulationManager manger = PopulationManager.getDefaultManager();

    responsibilities.addElement(Responsibility.example1(anEmployee));
    responsibilities.addElement(Responsibility.example2(anEmployee));

    languages.addElement(manger.getObject((new Language()).getClass(), "example1"));
    languages.addElement(manger.getObject((new Language()).getClass(), "example2"));
    languages.addElement(manger.getObject((new Language()).getClass(), "example3"));
    languages.addElement(manger.getObject((new Language()).getClass(), "example4"));

    example.setDescription("TOPLink");
    example.getComputer().setValue(Computer.example1());
    example.getResponsibilities().setValue(responsibilities);
    example.getLanguages().setValue(languages);

    return example;
}
public static ProjectDescription example2(Employee anEmployee)
{
    ProjectDescription example = new ProjectDescription();
    Vector responsibilities = new Vector(),
           languages = new Vector();
    PopulationManager manager = PopulationManager.getDefaultManager();

    responsibilities.addElement(Responsibility.example3(anEmployee));
    responsibilities.addElement(Responsibility.example4(anEmployee));

    languages.addElement(manager.getObject((new Language()).getClass(), "example3"));
    languages.addElement(manager.getObject((new Language()).getClass(), "example4"));

    example.setDescription("Course Development");
    example.getComputer().setValue(Computer.example2());
    example.getResponsibilities().setValue(responsibilities);
    example.getLanguages().setValue(languages);

    return example;
}
public static ProjectDescription example3(Employee anEmployee)
{
    ProjectDescription example = new ProjectDescription();
    Vector responsibilities = new Vector(),
           languages = new Vector();
    PopulationManager manger = PopulationManager.getDefaultManager();

    responsibilities.addElement(Responsibility.example5(anEmployee));
    responsibilities.addElement(Responsibility.example6(anEmployee));

    languages.addElement(manger.getObject((new Language()).getClass(), "example5"));
    languages.addElement(manger.getObject((new Language()).getClass(), "example6"));

    example.setDescription("Network Administration");
    example.getComputer().setValue(Computer.example3());
    example.getResponsibilities().setValue(responsibilities);
    example.getLanguages().setValue(languages);

    return example;
}
public ValueHolderInterface getComputer()
{
    return computer;
}
public String getDescription()
{
    return description;
}
public ValueHolderInterface getLanguages()
{
    return languages;
}
public ValueHolderInterface getResponsibilities()
{
    return responsibilities;
}
public void setDescription(String aDescription)
{
    description = aDescription;
}
}
