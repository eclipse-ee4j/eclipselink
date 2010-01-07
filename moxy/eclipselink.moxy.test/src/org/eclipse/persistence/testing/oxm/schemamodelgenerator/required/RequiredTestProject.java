/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     rbarkhouse - 2009-08-13 13:49:00 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.oxm.schemamodelgenerator.required;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeDirectCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.sessions.Project;

public class RequiredTestProject extends Project {

    boolean shouldSetMappingsToRequired = false;

    public RequiredTestProject() {
        addDescriptors();
    }

    public RequiredTestProject(boolean required) {
        shouldSetMappingsToRequired = required;
        addDescriptors();
    }

    private void addDescriptors() {
        addDescriptor(getRequiredTestObjectDescriptor());
        addDescriptor(getRequiredTestSubObjectDescriptor());
    }

    private XMLDescriptor getRequiredTestObjectDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(RequiredTestObject.class);
        descriptor.setAlias("RequiredTestObject");
        descriptor.setDefaultRootElement("required-test-object");

        XMLDirectMapping directMapping = new XMLDirectMapping();
        directMapping.setAttributeName("direct");
        directMapping.setXPath("direct/text()");
        ((XMLField) directMapping.getField()).setRequired(shouldSetMappingsToRequired);
        descriptor.addMapping(directMapping);

        XMLDirectMapping directAttributeMapping = new XMLDirectMapping();
        directAttributeMapping.setAttributeName("directAttribute");
        directAttributeMapping.setXPath("@directAttribute");
        ((XMLField) directAttributeMapping.getField()).setRequired(shouldSetMappingsToRequired);
        descriptor.addMapping(directAttributeMapping);        
        
        XMLCompositeDirectCollectionMapping directCollectionMapping = new XMLCompositeDirectCollectionMapping(); 
        directCollectionMapping.setAttributeName("directCollection");
        directCollectionMapping.setXPath("directCollection/text()");
        ((XMLField) directCollectionMapping.getField()).setRequired(shouldSetMappingsToRequired);
        descriptor.addMapping(directCollectionMapping);

        XMLCompositeObjectMapping compositeObjectMapping = new XMLCompositeObjectMapping();
        compositeObjectMapping.setAttributeName("compositeObject");
        compositeObjectMapping.setXPath("compositeObject/text()");
        compositeObjectMapping.setReferenceClass(RequiredTestSubObject.class);
        ((XMLField) compositeObjectMapping.getField()).setRequired(shouldSetMappingsToRequired);        
        descriptor.addMapping(compositeObjectMapping);

        XMLCompositeCollectionMapping compositeCollectionMapping = new XMLCompositeCollectionMapping();
        compositeCollectionMapping.setAttributeName("compositeCollection");
        compositeCollectionMapping.setXPath("compositeCollection/text()");
        compositeCollectionMapping.setReferenceClass(RequiredTestSubObject.class);
        ((XMLField) compositeCollectionMapping.getField()).setRequired(shouldSetMappingsToRequired);        
        descriptor.addMapping(compositeCollectionMapping);

        return descriptor;
    }

    private XMLDescriptor getRequiredTestSubObjectDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(RequiredTestSubObject.class);
        descriptor.setAlias("RequiredTestSubObject");

        XMLDirectMapping valueMapping = new XMLDirectMapping();
        valueMapping.setAttributeName("value");
        valueMapping.setXPath("value/text()");
        descriptor.addMapping(valueMapping);

        return descriptor;
    }

}