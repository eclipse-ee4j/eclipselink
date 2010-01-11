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
package org.eclipse.persistence.testing.oxm.mappings.serializedobject;

import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.mappings.converters.*;
import org.eclipse.persistence.oxm.*;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.sessions.Project;
import javax.xml.namespace.QName;

public class SerializedObjectProject extends Project {
    public SerializedObjectProject() {
        super();
        buildEmployeeDescriptor();
    }

    public void buildEmployeeDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Employee.class);
        descriptor.setDefaultRootElement("employee");

        XMLDirectMapping hexAddress = new XMLDirectMapping();
        hexAddress.setAttributeName("hexAddress");
        hexAddress.setGetMethodName("getHexAddress");
        hexAddress.setSetMethodName("setHexAddress");
        hexAddress.setConverter(new SerializedObjectConverter());
        XMLField field = new XMLField("hex-address/text()");
        field.setIsTypedTextField(true);
        hexAddress.setField(field);
        descriptor.addMapping(hexAddress);

        XMLDirectMapping base64Address = new XMLDirectMapping();
        base64Address.setAttributeName("base64Address");
        base64Address.setGetMethodName("getBase64Address");
        base64Address.setSetMethodName("setBase64Address");
        base64Address.setConverter(new SerializedObjectConverter());
        XMLField field2 = new XMLField("base64-address/text()");
        field2.setIsTypedTextField(true);
        field2.addConversion(XMLConstants.BASE_64_BINARY_QNAME, ClassConstants.APBYTE);
        base64Address.setField(field2);
        descriptor.addMapping(base64Address);

        this.addDescriptor(descriptor);
    }
}
