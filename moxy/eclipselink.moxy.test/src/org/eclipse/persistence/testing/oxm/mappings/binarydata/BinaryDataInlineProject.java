/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith -February 2010 - 2.1 
 ******************************************************************************/
package org.eclipse.persistence.testing.oxm.mappings.binarydata;

import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.XMLBinaryDataMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.sessions.Project;

public class BinaryDataInlineProject extends Project {

    public BinaryDataInlineProject(){
        addDescriptor(getEmployeeDescriptor());
    }

    private XMLDescriptor getEmployeeDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Employee.class);
        descriptor.setDefaultRootElement("employee");

        XMLDirectMapping idMapping = new XMLDirectMapping();
        idMapping.setAttributeName("id");
        idMapping.setXPath("@id");
        descriptor.addMapping(idMapping);

        XMLBinaryDataMapping photoMapping = new XMLBinaryDataMapping();
        photoMapping.setAttributeName("photo");
        XMLField field = new XMLField("photo");
        field.setSchemaType(XMLConstants.BASE_64_BINARY_QNAME);
        photoMapping.setField(field);
        photoMapping.setShouldInlineBinaryData(true);
        descriptor.addMapping(photoMapping);        

        XMLBinaryDataMapping photoAttrMapping = new XMLBinaryDataMapping();
        photoAttrMapping.setAttributeName("extraPhoto");
        XMLField fieldAttr = new XMLField("@photoAttr");
        fieldAttr.setSchemaType(XMLConstants.BASE_64_BINARY_QNAME);
        photoAttrMapping.setField(fieldAttr);
        photoAttrMapping.setShouldInlineBinaryData(true);
        descriptor.addMapping(photoAttrMapping);
        
        return descriptor;
    }
 }
