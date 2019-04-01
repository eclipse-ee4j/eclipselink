/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.models.aggregate;

import java.io.*;
import java.util.Collection;
import java.util.Vector;
import org.eclipse.persistence.indirection.*;
import org.eclipse.persistence.tools.schemaframework.*;

public class ProjectDescription implements Serializable {
    public Number id;
    public String description;
    public ValueHolderInterface<Computer> computer;
    public ValueHolderInterface<Collection<Language>> languages;
    public ValueHolderInterface<Collection<Responsibility>> responsibilities;

    public ProjectDescription() {
        computer = new ValueHolder<>();
        responsibilities = new ValueHolder<>();
        languages = new ValueHolder<>();
    }

    public static ProjectDescription example1(Employee anEmployee) {
        ProjectDescription example = new ProjectDescription();
        Vector<Responsibility> responsibilities = new Vector<>();
        Vector<Language> languages = new Vector<>();
        PopulationManager manger = PopulationManager.getDefaultManager();

        responsibilities.addElement(Responsibility.example1(anEmployee));
        responsibilities.addElement(Responsibility.example2(anEmployee));

        languages.addElement((Language) manger.getObject((new Language()).getClass(), "example1"));
        languages.addElement((Language) manger.getObject((new Language()).getClass(), "example2"));
        languages.addElement((Language) manger.getObject((new Language()).getClass(), "example3"));
        languages.addElement((Language) manger.getObject((new Language()).getClass(), "example4"));

        example.setDescription("TOPLink");
        example.getComputer().setValue(Computer.example1());
        example.getResponsibilities().setValue(responsibilities);
        example.getLanguages().setValue(languages);

        return example;
    }

    public static ProjectDescription example2(Employee anEmployee) {
        ProjectDescription example = new ProjectDescription();
        Vector<Responsibility> responsibilities = new Vector<Responsibility>();
        Vector<Language> languages = new Vector<>();
        PopulationManager manager = PopulationManager.getDefaultManager();

        responsibilities.addElement(Responsibility.example3(anEmployee));
        responsibilities.addElement(Responsibility.example4(anEmployee));

        languages.addElement((Language) manager.getObject((new Language()).getClass(), "example3"));
        languages.addElement((Language) manager.getObject((new Language()).getClass(), "example4"));

        example.setDescription("Course Development");
        example.getComputer().setValue(Computer.example2());
        example.getResponsibilities().setValue(responsibilities);
        example.getLanguages().setValue(languages);

        return example;
    }

    public static ProjectDescription example3(Employee anEmployee) {
        ProjectDescription example = new ProjectDescription();
        Vector<Responsibility> responsibilities = new Vector<Responsibility>();
        Vector<Language>  languages= new Vector<>();
        PopulationManager manger = PopulationManager.getDefaultManager();

        responsibilities.addElement(Responsibility.example5(anEmployee));
        responsibilities.addElement(Responsibility.example6(anEmployee));

        languages.addElement((Language) manger.getObject((new Language()).getClass(), "example5"));
        languages.addElement((Language) manger.getObject((new Language()).getClass(), "example6"));

        example.setDescription("Network Administration");
        example.getComputer().setValue(Computer.example3());
        example.getResponsibilities().setValue(responsibilities);
        example.getLanguages().setValue(languages);

        return example;
    }

    public ValueHolderInterface<Computer> getComputer() {
        return computer;
    }

    public String getDescription() {
        return description;
    }

    public ValueHolderInterface<Collection<Language>> getLanguages() {
        return languages;
    }

    public ValueHolderInterface<Collection<Responsibility>> getResponsibilities() {
        return responsibilities;
    }

    public void setDescription(String aDescription) {
        description = aDescription;
    }
}
