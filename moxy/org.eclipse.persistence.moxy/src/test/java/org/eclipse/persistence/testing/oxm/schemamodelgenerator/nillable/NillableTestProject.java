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
//     rbarkhouse - 2009-08-06 16:27:00 - initial implementation
package org.eclipse.persistence.testing.oxm.schemamodelgenerator.nillable;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeDirectCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.sessions.Project;

public class NillableTestProject extends Project {

    boolean shouldSetMappingsToNillable = false;

    public NillableTestProject() {
        addDescriptors();
    }

    public NillableTestProject(boolean nillable) {
        shouldSetMappingsToNillable = nillable;
        addDescriptors();

    }

    private void addDescriptors() {
        addDescriptor(getNillableTestObjectDescriptor());
        addDescriptor(getNillableTestSubObjectDescriptor());
    }

    private XMLDescriptor getNillableTestObjectDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(NillableTestObject.class);
        descriptor.setAlias("NillableTestObject");
        descriptor.setDefaultRootElement("nillable-test-object");

        XMLDirectMapping directMapping = new XMLDirectMapping();
        directMapping.setAttributeName("direct");
        directMapping.setXPath("direct/text()");
        directMapping.getNullPolicy().setNullRepresentedByXsiNil(shouldSetMappingsToNillable);
        descriptor.addMapping(directMapping);

        XMLCompositeDirectCollectionMapping directCollectionMapping = new XMLCompositeDirectCollectionMapping();
        directCollectionMapping.setAttributeName("directCollection");
        directCollectionMapping.setXPath("directCollection/text()");
        directCollectionMapping.getNullPolicy().setNullRepresentedByXsiNil(shouldSetMappingsToNillable);
        descriptor.addMapping(directCollectionMapping);

        XMLCompositeObjectMapping compositeObjectMapping = new XMLCompositeObjectMapping();
        compositeObjectMapping.setAttributeName("compositeObject");
        compositeObjectMapping.setXPath("compositeObject/text()");
        compositeObjectMapping.setReferenceClass(NillableTestSubObject.class);
        compositeObjectMapping.getNullPolicy().setNullRepresentedByXsiNil(shouldSetMappingsToNillable);
        descriptor.addMapping(compositeObjectMapping);

        XMLCompositeCollectionMapping compositeCollectionMapping = new XMLCompositeCollectionMapping();
        compositeCollectionMapping.setAttributeName("compositeCollection");
        compositeCollectionMapping.setXPath("compositeCollection/text()");
        compositeCollectionMapping.setReferenceClass(NillableTestSubObject.class);
        compositeCollectionMapping.getNullPolicy().setNullRepresentedByXsiNil(shouldSetMappingsToNillable);
        descriptor.addMapping(compositeCollectionMapping);

        return descriptor;
    }

    private XMLDescriptor getNillableTestSubObjectDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(NillableTestSubObject.class);
        descriptor.setAlias("NillableTestSubObject");

        XMLDirectMapping valueMapping = new XMLDirectMapping();
        valueMapping.setAttributeName("value");
        valueMapping.setXPath("value/text()");
        descriptor.addMapping(valueMapping);

        return descriptor;
    }

}
