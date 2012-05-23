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
 *     Denise Smith - December 15, 2009
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings.binarydata.identifiedbyname;

import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.XMLBinaryDataMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.binarydata.Employee;

public class BinaryDataByteObjectArrayProject extends Project {
    public BinaryDataByteObjectArrayProject(//
    NamespaceResolver namespaceResolver) {
        addDescriptor(getEmployeeDescriptor(namespaceResolver));
    }

    private XMLDescriptor getEmployeeDescriptor(NamespaceResolver aNSResolver) {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(EmployeeWithByteObjectArray.class);
        descriptor.setDefaultRootElement("employee");

        XMLDirectMapping idMapping = new XMLDirectMapping();
        idMapping.setAttributeName("id");
        idMapping.setXPath("@id");
        descriptor.addMapping(idMapping);

        XMLBinaryDataMapping photoMapping = new XMLBinaryDataMapping();
        photoMapping.setAttributeName("photo");
        //photoMapping.setXPath("photos/list/photo");///text()");
        XMLField field = new XMLField("photo");///text()");
        field.setSchemaType(XMLConstants.BASE_64_BINARY_QNAME);
        photoMapping.setField(field);

        descriptor.addMapping(photoMapping);
        if (aNSResolver != null) {
            descriptor.setNamespaceResolver(aNSResolver);
        }

        photoMapping.setShouldInlineBinaryData(false);
        photoMapping.setSwaRef(false);
        photoMapping.setMimeType("image");
        
        XMLBinaryDataMapping dataMapping = new XMLBinaryDataMapping();
        dataMapping.setAttributeName("data");
        field = new XMLField("data");
        field.setSchemaType(XMLConstants.BASE_64_BINARY_QNAME);
        dataMapping.setField(field);
        dataMapping.setShouldInlineBinaryData(false);
        dataMapping.setSwaRef(false);
        descriptor.addMapping(dataMapping);

        return descriptor;
    }
}
