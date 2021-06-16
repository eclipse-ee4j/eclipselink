/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
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
        resolver.put(XMLConstants.SCHEMA_INSTANCE_PREFIX, javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
        resolver.put(XMLConstants.SCHEMA_PREFIX, javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
        xmlDescriptor.setNamespaceResolver(resolver);

        return xmlDescriptor;
    }
}
