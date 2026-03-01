/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     ailitchev - fixed bug 266555: Nested AggregateCollectionMapping may set wrong tables into reference descriptor.
package org.eclipse.persistence.testing.tests.aggregate;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.mappings.AggregateCollectionMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.aggregate.Agent;
import org.eclipse.persistence.testing.models.aggregate.Builder;

/*
 * Test verifies that correct tables are used by nested aggregate collection mappings.
 * The target classes are the same, but all the reference descriptors of
 * aggregate collection mappings from Builder class should have tables with names
 * starting with "BUILDER"; but none of the reference descriptors from Agent should
 * have tables named this way.
 */
public class AgentBuilderTablesTest extends TestCase {
    public AgentBuilderTablesTest() {
        super();
        this.setDescription("Test verifies that correct tables are used by nested aggregate collection mappings");
    }
    @Override
    public void verify() {
        String errorMsg = "";
        errorMsg += verifyDescriptorWithAggColMappingsAndChildren(getSession().getDescriptor(Agent.class), false, "Agent");
        errorMsg += verifyDescriptorWithAggColMappingsAndChildren(getSession().getDescriptor(Builder.class), true, "Builder");
        if(!errorMsg.isEmpty()) {
            throw new TestErrorException("\n"+errorMsg);
        }
    }
    String verifyDescriptorWithAggColMappingsAndChildren(ClassDescriptor desc, boolean tableNameShouldStartWithBuilder, String attributeName) {
        StringBuilder localErrorMsg = new StringBuilder(verifyDescriptorWithChildren(desc, tableNameShouldStartWithBuilder, attributeName));
        boolean hasAggregateCollectionMapping = false;
        for (DatabaseMapping mapping : desc.getMappings()) {
            if (mapping.isAggregateCollectionMapping()) {
                AggregateCollectionMapping acMapping = (AggregateCollectionMapping) mapping;
                localErrorMsg.append(verifyDescriptorWithAggColMappingsAndChildren(acMapping.getReferenceDescriptor(), tableNameShouldStartWithBuilder, attributeName + "." + acMapping.getAttributeName()));
            }
        }
        return localErrorMsg.toString();
    }
    String verifyDescriptorWithChildren(ClassDescriptor desc, boolean tableNameShouldStartWithBuilder, String attributeName) {
        StringBuilder localErrorMsg = new StringBuilder(verifyDescriptor(desc, tableNameShouldStartWithBuilder, attributeName));
        if(desc.hasInheritance() && desc.getInheritancePolicy().hasChildren()) {
            for (ClassDescriptor childDesc : desc.getInheritancePolicy().getChildDescriptors()) {
                localErrorMsg.append(verifyDescriptor(childDesc, tableNameShouldStartWithBuilder, attributeName));
            }
        }
        return localErrorMsg.toString();
    }
    String verifyDescriptor(ClassDescriptor desc, boolean tableNameShouldStartWithBuilder, String attributeName) {
        StringBuilder localErrorMsg = new StringBuilder();
        for (String tableName : desc.getTableNames()) {
            boolean startsWithBuilder = tableName.startsWith("BUILDER");
            if (tableNameShouldStartWithBuilder != startsWithBuilder) {
                localErrorMsg.append("Wrong table name ").append(tableName).append("; ");
            }
        }
        if(!localErrorMsg.isEmpty()) {
            localErrorMsg = new StringBuilder("Ref.descriptor class: " + desc.getJavaClass().getSimpleName() + "; mapping: " + attributeName + ": " + localErrorMsg + "\n");
        }
        return localErrorMsg.toString();
    }
}
