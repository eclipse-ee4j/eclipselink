/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.mapping;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.testing.models.mapping.BabyMonitor;
import org.eclipse.persistence.testing.tests.mapping.TwoLevelJoinedAttributeTest;

/**
 * Bug 3142898
 * This test expands uppon TwoLevelJoinedAttributeTest to ensure that if joining
 * is cyclical we still properly populate our objects.
 */
public class LoopingJoinedAttributeTest extends TwoLevelJoinedAttributeTest {
    protected int oldJoinFetch;

    public LoopingJoinedAttributeTest() {
        super();
        setDescription("Ensure objects that use joining in a cyclical manner work properly. (e.g. A joined to B joined to C joined to A))");
    }

    public void setup() {
        super.setup();
        // add a join between BabyMonitor and Baby what will complete a loop when combined with the superclass' joins
        ClassDescriptor descriptor = getSession().getClassDescriptor(BabyMonitor.class);
        oldJoinFetch = ((OneToOneMapping)descriptor.getMappingForAttributeName("baby")).getJoinFetch();
        ((OneToOneMapping)descriptor.getMappingForAttributeName("baby")).useInnerJoinFetch();
        descriptor.reInitializeJoinedAttributes();
    }

    public void reset() {
        super.reset();
        ClassDescriptor descriptor = getSession().getClassDescriptor(BabyMonitor.class);
        ((OneToOneMapping)descriptor.getMappingForAttributeName("baby")).setJoinFetch(oldJoinFetch);
        descriptor.reInitializeJoinedAttributes();
    }
}
