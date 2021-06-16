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
