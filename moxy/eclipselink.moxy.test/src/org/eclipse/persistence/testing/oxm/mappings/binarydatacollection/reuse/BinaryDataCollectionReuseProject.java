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
 *     rbarkhouse - 2009-10-09 14:17:31 - initial implementation
 ******************************************************************************/

package org.eclipse.persistence.testing.oxm.mappings.binarydatacollection.reuse;

import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.XMLBinaryDataCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.sessions.Project;

public class BinaryDataCollectionReuseProject extends Project {

    public BinaryDataCollectionReuseProject(NamespaceResolver namespaceResolver) {
        addDescriptor(getEmployeeDescriptor(namespaceResolver));
    }

    private XMLDescriptor getEmployeeDescriptor(NamespaceResolver aNSResolver) {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Employee.class);
        descriptor.setDefaultRootElement("employee");

        XMLDirectMapping idMapping = new XMLDirectMapping();
        idMapping.setAttributeName("id");
        idMapping.setXPath("@id");
        descriptor.addMapping(idMapping);

        XMLBinaryDataCollectionMapping photosMapping = new XMLBinaryDataCollectionMapping();
        photosMapping.setAttributeName("photos");
        XMLField field = new XMLField("photos/list/photo");
        field.setSchemaType(XMLConstants.BASE_64_BINARY_QNAME);
        photosMapping.setReuseContainer(true);
        photosMapping.setField(field);

        descriptor.addMapping(photosMapping);
        if (aNSResolver != null) {
            descriptor.setNamespaceResolver(aNSResolver);
        }

        photosMapping.setShouldInlineBinaryData(false);
        photosMapping.setSwaRef(false);
        photosMapping.setMimeType("image");
        photosMapping.setCollectionContentType(ClassConstants.APBYTE);
        return descriptor;
    }

}