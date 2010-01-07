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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  

/* $Header: BinaryDataCollectionWithGroupingElementIdentifiedByNameProject.java 02-nov-2006.10:57:28 gyorke Exp $ */
/* Copyright (c) 2006, Oracle. All rights reserved.  */
/*
   DESCRIPTION

   MODIFIED    (MM/DD/YY)
    gyorke      11/02/06 - 
    mfobrien    10/19/06 - Creation
 */

/**
 *  @version $Header: BinaryDataCollectionWithGroupingElementIdentifiedByNameProject.java 02-nov-2006.10:57:28 gyorke Exp $
 *  @author  mfobrien
 *  @since   11.1
 */
package org.eclipse.persistence.testing.oxm.mappings.binarydatacollection.identifiedbyname.withgroupingelement;

import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.XMLBinaryDataCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.binarydatacollection.Employee;

public class BinaryDataCollectionWithGroupingElementIdentifiedByNameProject extends Project {
    //public NamespaceResolver namespaceResolver;
    public BinaryDataCollectionWithGroupingElementIdentifiedByNameProject(//
    NamespaceResolver namespaceResolver) {
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
        //photosMapping.setXPath("photos/list/photo");///text()");   
        XMLField field = new XMLField("photos/list/photo");///text()");
        field.setSchemaType(XMLConstants.BASE_64_BINARY_QNAME);
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
