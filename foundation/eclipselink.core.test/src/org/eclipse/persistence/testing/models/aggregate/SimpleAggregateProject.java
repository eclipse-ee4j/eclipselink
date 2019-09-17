/*
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.testing.models.aggregate;

import org.eclipse.persistence.descriptors.ChangedFieldsLockingPolicy;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.mappings.AggregateObjectMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;

public class SimpleAggregateProject extends org.eclipse.persistence.sessions.Project {

    public SimpleAggregateProject() {
        applyPROJECT();
        applyLOGIN();

        addDescriptor(buildSimpleAggregateClassDescriptor());
        addDescriptor(buildSimpleEntityDescriptor());
    }

    protected void applyLOGIN() {
    }

    protected void applyPROJECT() {
        setName("SimpleAggregateSystem");
    }

    public ClassDescriptor buildSimpleAggregateClassDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.descriptorIsAggregate();
        descriptor.setJavaClass(SimpleAggregate.class);

        // ClassDescriptor Properties.
        descriptor.setAlias("SimpleAggregate");


        // Query Manager.


        // Event Manager.

        // Mappings.
        DirectToFieldMapping contentMapping = new DirectToFieldMapping();
        contentMapping.setAttributeName("content");
        contentMapping.setFieldName("content->DIRECT");
        descriptor.addMapping(contentMapping);

        return descriptor;
    }

    protected ClassDescriptor buildSimpleEntityDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        //descriptor.setCacheable(false);

        // SECTION: DESCRIPTOR
        descriptor.setJavaClass(SimpleEntity.class);
        descriptor.addTableName("SIMPLEENTITY");
        descriptor.addPrimaryKeyFieldName("SIMPLEENTITY.ID");

        ChangedFieldsLockingPolicy lockingPolicy = new ChangedFieldsLockingPolicy();
        descriptor.setOptimisticLockingPolicy(lockingPolicy);

        // SECTION: DIRECTTOFIELDMAPPING
        DirectToFieldMapping directtofieldmapping = new DirectToFieldMapping();
        directtofieldmapping.setAttributeName("id");
        directtofieldmapping.setIsReadOnly(false);
        directtofieldmapping.setFieldName("SIMPLEENTITY.ID");
        descriptor.addMapping(directtofieldmapping);

        DirectToFieldMapping fieldmapping = new DirectToFieldMapping();
        fieldmapping.setAttributeName("field");
        fieldmapping.setIsReadOnly(false);
        fieldmapping.setFieldName("SIMPLEENTITY.FIELD");
        descriptor.addMapping(fieldmapping);

        AggregateObjectMapping aggregateObjectMapping = new AggregateObjectMapping();
        aggregateObjectMapping.setAttributeName("simpleAggregate");
        aggregateObjectMapping.setReferenceClass(SimpleAggregate.class);
        aggregateObjectMapping.setIsNullAllowed(true);
        aggregateObjectMapping.addFieldNameTranslation("SIMPLEENTITY.CONTENT", "content->DIRECT");
        descriptor.addMapping(aggregateObjectMapping);

        return descriptor;
    }

}
