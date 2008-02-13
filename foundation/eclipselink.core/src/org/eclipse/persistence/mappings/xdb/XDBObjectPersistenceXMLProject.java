/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.mappings.xdb;

import java.util.*;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.sessions.factories.DirectToXMLTypeMappingHelper;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.NamespaceResolver;

/**
 * INTERNAL:
 * Define the TopLink project and descriptor information to read a TopLink project from an XML file.
 * The XDB meta-data must be defined separately as it has separate jar dependency that must not be required if not using XDB.
 */
public class XDBObjectPersistenceXMLProject extends Project {

    /**
     * INTERNAL:
     * Return a new descriptor project.
     */
    public XDBObjectPersistenceXMLProject() {

        DirectToXMLTypeMappingHelper.getInstance().addXDBDescriptors("org.eclipse.persistence.mappings.xdb.DirectToXMLTypeMapping" ,this);

        // Set the namespaces on all descriptors.
        NamespaceResolver namespaceResolver = new NamespaceResolver();
        namespaceResolver.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        namespaceResolver.put("xsd", "http://www.w3.org/2001/XMLSchema");
        namespaceResolver.put("opm", "http://xmlns.oracle.com/ias/xsds/opm");
        namespaceResolver.put("toplink", "http://xmlns.oracle.com/ias/xsds/toplink");

        for (Iterator descriptors = getDescriptors().values().iterator(); descriptors.hasNext();) {
            XMLDescriptor descriptor = (XMLDescriptor)descriptors.next();
            descriptor.setNamespaceResolver(namespaceResolver);
        }
    }

}