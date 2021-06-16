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

import java.util.Enumeration;
import org.eclipse.persistence.descriptors.*;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.expressions.*;

public class PC extends Computer {
    public static void addToDescriptor(ClassDescriptor descriptor) {
        descriptor.getInheritancePolicy().setWithAllSubclassesExpression(new ExpressionBuilder().getField("INH_COMP.CTYPE").equal("PC"));

        ExpressionBuilder builder = new ExpressionBuilder();
        descriptor.getInheritancePolicy().setOnlyInstancesExpression((builder.getField("INH_COMP.CTYPE").equal("PC")).and(builder.getField("INH_COMP.PCTYPE").equal("PC")));

        // In order for this domain model to work with all of our tests, it must be set
        // up so that the transformation mapping below is not added twice.
        // As a result, we check for the mapping before adding it.
        // The reason this mapping is not added in the project is that some Mapping Workbench
        // tests rely on the ammendment method.
        Enumeration mappings = descriptor.getMappings().elements();
        while (mappings.hasMoreElements()) {
            DatabaseMapping mapping = (DatabaseMapping)mappings.nextElement();
            if (mapping.isTransformationMapping()) {
                Object pctype = ((TransformationMapping)mapping).getFieldNameToMethodNames().get("PCTYPE");
                if (pctype != null) {
                    return;
                }
                ;
            }
        }
        TransformationMapping typeMapping = new TransformationMapping();
        typeMapping.addFieldTransformation("PCTYPE", "getPCType");
        descriptor.addMapping(typeMapping);

    }

    public String getComputerType() {
        return "PC";
    }

    public String getPCType() {
        return "PC";
    }
}
