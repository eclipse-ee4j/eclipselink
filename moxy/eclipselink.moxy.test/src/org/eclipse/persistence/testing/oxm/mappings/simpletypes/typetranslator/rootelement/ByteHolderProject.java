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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings.simpletypes.typetranslator.rootelement;


// TopLink imports
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.sessions.Project;

public class ByteHolderProject extends Project {
    public ByteHolderProject() {
        super();
        this.addDescriptor(getByteHolderDescriptor());
    }

    XMLDescriptor getByteHolderDescriptor() {
        XMLDescriptor xmlDescriptor = new XMLDescriptor();
        xmlDescriptor.setJavaClass(ByteHolder.class);
        xmlDescriptor.setDefaultRootElement("byteHolder");
        // xmlDescriptor.setShouldPreserveDocument(true);

        XMLDirectMapping mapping = new XMLDirectMapping();
        XMLField tef = new XMLField();
        tef.setIsTypedTextField(true);
        tef.setXPath("text()");
        mapping.setField(tef);
        mapping.setAttributeName("bytes");
        xmlDescriptor.addMapping(mapping);

        NamespaceResolver resolver = new NamespaceResolver();
        resolver.put(XMLConstants.SCHEMA_INSTANCE_PREFIX, XMLConstants.SCHEMA_INSTANCE_URL);
        resolver.put(XMLConstants.SCHEMA_PREFIX, XMLConstants.SCHEMA_URL);
        xmlDescriptor.setNamespaceResolver(resolver);

        return xmlDescriptor;
    }
}
