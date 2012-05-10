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
 *     rbarkhouse - 2009-10-14 11:21:57 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.converter;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeDirectCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.sessions.Project;

public class MyProject extends Project {

    public MyProject () {
        super();
        this.addDescriptor(getCustomerDescriptor());
        this.addDescriptor(getIntermediateValueDescriptor());
    }

    private XMLDescriptor getCustomerDescriptor() {
        XMLDescriptor xmlDescriptor = new XMLDescriptor();
        xmlDescriptor.setJavaClass(MyObject.class);
        xmlDescriptor.setDefaultRootElement("MyXML");

        XMLCompositeObjectMapping valueMapping = new XMLCompositeObjectMapping();
        valueMapping.setReferenceClass(IntermediateValue.class);
        valueMapping.setAttributeName("value");
        valueMapping.setXPath(".");
        valueMapping.setConverter(new ValueConverter());
        xmlDescriptor.addMapping(valueMapping);

        return xmlDescriptor;
    }

    private XMLDescriptor getIntermediateValueDescriptor() {
        XMLDescriptor xmlDescriptor = new XMLDescriptor();
        xmlDescriptor.setJavaClass(IntermediateValue.class);

        XMLDirectMapping partAMapping = new XMLDirectMapping();
        partAMapping.setAttributeName("partA");
        partAMapping.setXPath("valuePartA/text()");
        xmlDescriptor.addMapping(partAMapping);

        XMLCompositeDirectCollectionMapping partBMapping = new XMLCompositeDirectCollectionMapping();
        partBMapping.setAttributeName("partB");
        partBMapping.setXPath("valuePartB/text()");
        xmlDescriptor.addMapping(partBMapping);

        return xmlDescriptor;
    }

}