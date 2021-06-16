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
package org.eclipse.persistence.testing.tests.workbenchintegration;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.testing.models.employee.domain.Employee;


public class BuildDescriptorAddMultipleTableForeignKeyFieldNameTest extends ProjectClassGeneratorResultFileTest {
    ClassDescriptor descriptorToModify;

    public BuildDescriptorAddMultipleTableForeignKeyFieldNameTest() {
        super(new org.eclipse.persistence.testing.models.employee.relational.EmployeeProject(),
              "descriptor.addForeignKeyFieldNameForMultipleTable(\"SOURCE_TABLE.Test\", \"TARGET_TABLE.Test\");");
        setDescription("Test buildDescriptor() -> getAdditionalTablePK()");
    }

    protected void setup() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        descriptorToModify = project.getDescriptors().get(Employee.class);
        descriptorToModify.addForeignKeyFieldNameForMultipleTable("SOURCE_TABLE.Test", "TARGET_TABLE.Test");
    }
}
