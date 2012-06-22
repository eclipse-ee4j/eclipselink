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
package org.eclipse.persistence.testing.oxm.mappings.binarydatacollection.identifiedbyname.withgroupingelement;

import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.XMLBinaryDataCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.testing.oxm.mappings.binarydatacollection.Employee;
import org.eclipse.persistence.testing.oxm.mappings.binarydatacollection.EmployeeWithByteArrayObject;

public class BinaryDataCollectionByteObjectArrayProject extends Project {
    public BinaryDataCollectionByteObjectArrayProject(//
    NamespaceResolver namespaceResolver) {
        addDescriptor(getEmployeeDescriptor(namespaceResolver));
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
        field.setSchemaType(XMLConstants.BASE_64_BINARY_QNAME);
        photosMapping.setField(field);

        descriptor.addMapping(photosMapping);
        if (aNSResolver != null) {
            descriptor.setNamespaceResolver(aNSResolver);
        }

        photosMapping.setShouldInlineBinaryData(false);
        photosMapping.setSwaRef(false);
        photosMapping.setMimeType("image");
        //photosMapping.setCollectionContentType(ClassConstants.ABYTE);
        Converter valueConverter = new MyConverter();
        photosMapping.setValueConverter(valueConverter);
        
        photosMapping.setAttributeElementClass(Byte[].class);
        return descriptor;
    }
    
    public class MyConverter implements Converter{
    	
		public Object convertDataValueToObjectValue(Object dataValue, Session session) { 
			return dataValue;
		}
	
		public Object convertObjectValueToDataValue(Object objectValue, Session session) {
			return objectValue;
		}
	
		public void initialize(DatabaseMapping mapping, Session session) {
			
		}
	
		public boolean isMutable() {
			return false;
		}
    }

}
