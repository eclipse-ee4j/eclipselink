/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     ailitchev - fixed bug 266555: Nested AggregateCollectionMapping may set wrong tables into reference descriptor. 
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.aggregate;

import java.util.Iterator;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.helper.Helper;
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
    public void verify() {
        String errorMsg = "";
        errorMsg += verifyDescriptorWithAggColMappingsAndChildren(getSession().getDescriptor(Agent.class), false, "Agent");
        errorMsg += verifyDescriptorWithAggColMappingsAndChildren(getSession().getDescriptor(Builder.class), true, "Builder");
        if(errorMsg.length() > 0) {
            throw new TestErrorException("\n"+errorMsg);
        }
    }
    String verifyDescriptorWithAggColMappingsAndChildren(ClassDescriptor desc, boolean tableNameShouldStartWithBuilder, String attributeName) {
        String localErrorMsg = verifyDescriptorWithChildren(desc, tableNameShouldStartWithBuilder, attributeName);
        boolean hasAggregateCollectionMapping = false;
        Iterator<DatabaseMapping> itMappings = desc.getMappings().iterator();
        while(itMappings.hasNext()) {
            DatabaseMapping mapping = itMappings.next();
            if(mapping.isAggregateCollectionMapping()) {
                AggregateCollectionMapping acMapping = (AggregateCollectionMapping)mapping;
                localErrorMsg += verifyDescriptorWithAggColMappingsAndChildren(acMapping.getReferenceDescriptor(), tableNameShouldStartWithBuilder, attributeName + "." + acMapping.getAttributeName());
            }
        }
        return localErrorMsg;
    }
    String verifyDescriptorWithChildren(ClassDescriptor desc, boolean tableNameShouldStartWithBuilder, String attributeName) {
        String localErrorMsg = verifyDescriptor(desc, tableNameShouldStartWithBuilder, attributeName);
        if(desc.hasInheritance() && desc.getInheritancePolicy().hasChildren()) {
            Iterator<ClassDescriptor> itChildren = desc.getInheritancePolicy().getChildDescriptors().iterator();
            while(itChildren.hasNext()) {
                ClassDescriptor childDesc = itChildren.next();
                localErrorMsg += verifyDescriptor(childDesc, tableNameShouldStartWithBuilder, attributeName);
            }
        }
        return localErrorMsg;
    }
    String verifyDescriptor(ClassDescriptor desc, boolean tableNameShouldStartWithBuilder, String attributeName) {
        String localErrorMsg = "";
        Iterator<String> it = desc.getTableNames().iterator();
        while(it.hasNext()) {
            String tableName = it.next();
            boolean startsWithBuilder = tableName.startsWith("BUILDER");
            if(tableNameShouldStartWithBuilder != startsWithBuilder) {
                localErrorMsg += "Wrong table name " + tableName + "; ";
            }
        }
        if(localErrorMsg.length() > 0) {
            localErrorMsg = "Ref.descriptor class: " + Helper.getShortClassName(desc.getJavaClass()) +"; mapping: "+ attributeName + ": " + localErrorMsg + "\n";
        }
        return localErrorMsg;
    }
}
