/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Denise Smith - January 6th, 2010 - 2.0.1
package org.eclipse.persistence.testing.oxm.mappings.binarydatacollection.identifiedbyname.withgroupingelement;
import javax.activation.DataHandler;

import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.XMLBinaryDataCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.testing.oxm.mappings.binarydatacollection.EmployeeWithByteArrayObject;

public class BinaryDataCollectionDataHandlerProject extends BinaryDataCollectionByteObjectArrayProject{

     public BinaryDataCollectionDataHandlerProject(NamespaceResolver namespaceResolver) {
        super(namespaceResolver);
    }

     protected XMLDescriptor getEmployeeDescriptor(NamespaceResolver aNSResolver) {
            XMLDescriptor descriptor = new XMLDescriptor();
            descriptor.setJavaClass(EmployeeWithByteArrayObject.class);
            descriptor.setDefaultRootElement("employee");

            XMLDirectMapping idMapping = new XMLDirectMapping();
            idMapping.setAttributeName("id");
            idMapping.setXPath("@id");
            descriptor.addMapping(idMapping);

            XMLBinaryDataCollectionMapping photosMapping = new XMLBinaryDataCollectionMapping();
            photosMapping.setAttributeName("photos");
            XMLField field = new XMLField("photos/list/photo");
            photosMapping.setField(field);

            descriptor.addMapping(photosMapping);
            if (aNSResolver != null) {
                descriptor.setNamespaceResolver(aNSResolver);
            }

            photosMapping.setShouldInlineBinaryData(false);
            photosMapping.setSwaRef(true);
            photosMapping.setMimeType("image");

            photosMapping.setAttributeElementClass(DataHandler.class);
            return descriptor;
        }
}
