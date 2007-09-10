/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.inheritance;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.queries.ReadAllQuery;

public abstract class Animal {
    protected int id;
    protected String name;

    public Animal() {
        super();
    }

    public static void addToDescriptor(ClassDescriptor descriptor) {
        org.eclipse.persistence.mappings.TransformationMapping mapping = new org.eclipse.persistence.mappings.TransformationMapping();
        mapping.addFieldTransformation("TYPE", "getType");
        descriptor.addMapping(mapping);
        
        ReadAllQuery readAll = new ReadAllQuery(Animal.class);
        descriptor.getQueryManager().addQuery("InheritanceReadAll", readAll);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public abstract String getType();

    public void setId(int this_id) {
        id = this_id;

    }

    public void setName(String this_name) {
        name = this_name;
    }
}