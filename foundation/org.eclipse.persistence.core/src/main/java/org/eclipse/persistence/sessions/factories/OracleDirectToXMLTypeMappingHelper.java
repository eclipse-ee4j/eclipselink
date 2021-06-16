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
package org.eclipse.persistence.sessions.factories;

import org.eclipse.persistence.internal.codegen.NonreflectiveMethodDefinition;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.internal.sessions.factories.DirectToXMLTypeMappingHelper;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.mappings.xdb.DirectToXMLTypeMapping;

/**
 * Helper class to abstract the XML mapping for DirectToXMLType.
 *
 * @author djclarke
 * @since EclipseLink 0.1
 */
public class OracleDirectToXMLTypeMappingHelper extends DirectToXMLTypeMappingHelper {

    protected String namespaceXPath;
    /**
     * Add the XMLType mapping indicator to the DatabaseMapping descriptor.
     */
    @Override
    public void addClassIndicator(XMLDescriptor descriptor, String namespaceXPath) {
        this.namespaceXPath = namespaceXPath;
        descriptor.getInheritancePolicy().addClassIndicator(DirectToXMLTypeMapping.class,
            namespaceXPath + "direct-xml-type-mapping");
    }

    /**
     * Write the Project.class code for the XMLType property.
     */
    @Override
    public void writeShouldreadWholeDocument(NonreflectiveMethodDefinition method, String mappingName, DatabaseMapping mapping) {
        method.addLine(mappingName + ".setShouldReadWholeDocument(" + ((DirectToXMLTypeMapping)mapping).shouldReadWholeDocument() + ");");
    }

    /**
     * Invoked from a descriptor is not found.
     */
    @Override
    public void addXDBDescriptors(String name, DatabaseSessionImpl session,
        NamespaceResolver namespaceResolver) {

        if (session.getDescriptorForAlias(name) == null){
            XMLDescriptor descriptor = new XMLDescriptor();

            descriptor.setJavaClass(DirectToXMLTypeMapping.class);
            descriptor.descriptorIsAggregate();
            descriptor.getInheritancePolicy().setParentClass(DirectToFieldMapping.class);

            XMLDirectMapping directtofieldmapping = new XMLDirectMapping();
            directtofieldmapping.setAttributeName("shouldReadWholeDocument");
            directtofieldmapping.setGetMethodName("shouldReadWholeDocument");
            directtofieldmapping.setSetMethodName("setShouldReadWholeDocument");
            directtofieldmapping.setXPath(namespaceXPath + "read-whole-document/text()");
            directtofieldmapping.setNullValue(Boolean.FALSE);
            descriptor.addMapping(directtofieldmapping);

            // Need to set the namespace resolver.
            descriptor.setNamespaceResolver(namespaceResolver);

            session.addDescriptor(descriptor);
        }
    }

}
