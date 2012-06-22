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
package org.eclipse.persistence.testing.oxm.mappings.anycollection.withoutgroupingelement;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLAnyCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.sessions.Project;

public class AnyCollectionWithGroupingElementNonRootProject extends Project {
    public AnyCollectionWithGroupingElementNonRootProject() {
        this.addDescriptor(buildWrapperDescriptor());
        this.addDescriptor(buildRootDescriptor());
        this.addDescriptor(buildChildDescriptor());
    }

    public ClassDescriptor buildWrapperDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Wrapper.class);
        descriptor.setDefaultRootElement("wrapper");

        XMLCompositeCollectionMapping mapping = new XMLCompositeCollectionMapping();
        mapping.setReferenceClass(Root.class);
        mapping.setXPath("root");
        mapping.setAttributeName("roots");
        descriptor.addMapping(mapping);

        return descriptor;
    }

    public ClassDescriptor buildRootDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Root.class);
        descriptor.setDefaultRootElement("root");

        XMLAnyCollectionMapping mapping = new XMLAnyCollectionMapping();
        mapping.setAttributeName("any");
        mapping.setGetMethodName("getAny");
        mapping.setSetMethodName("setAny");
        descriptor.addMapping(mapping);

        return descriptor;
    }

    public ClassDescriptor buildChildDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Child.class);
        descriptor.setDefaultRootElement("child");

        XMLDirectMapping mapping = new XMLDirectMapping();
        mapping.setAttributeName("content");
        mapping.setGetMethodName("getContent");
        mapping.setSetMethodName("setContent");
        mapping.setXPath("text()");
        descriptor.addMapping(mapping);

        return descriptor;
    }
}
