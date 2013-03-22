/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.sessions.factories;

import org.eclipse.persistence.internal.security.PrivilegedClassForName;
import org.eclipse.persistence.internal.security.PrivilegedNewInstanceFromClass;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.internal.codegen.NonreflectiveMethodDefinition;

/**
 * Helper class to abstract the XML mapping for DirectToXMLType.
 * 
 * @author djclarke
 * @since EclipseLink 0.1
 */
public class DirectToXMLTypeMappingHelper {

    private static DirectToXMLTypeMappingHelper singleton = null;

    public static DirectToXMLTypeMappingHelper getInstance() {

        if (singleton == null) {
            Class helperClass = null;

            try {
                helperClass = (Class) new PrivilegedClassForName("org.eclipse.persistence.sessions.factories.OracleDirectToXMLTypeMappingHelper").run();
            } catch (Throwable cnfe) {
                helperClass = DirectToXMLTypeMappingHelper.class;
            }
            try {
                singleton = (DirectToXMLTypeMappingHelper) new PrivilegedNewInstanceFromClass(helperClass).run();
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Helper create failed: " + helperClass);
            } catch (InstantiationException e) {
                throw new RuntimeException("Helper create failed: " + helperClass);
            }
        }
        return singleton;
    }

    /**
     * Add the XMLType mapping indicator to the DatabaseMapping descriptor.
     */
    public void addClassIndicator(XMLDescriptor descriptor, String namespaceXPath) {
    }

    /**
     * Write the Project.class code for the XMLType property.
     */
    public void writeShouldreadWholeDocument(NonreflectiveMethodDefinition method, String mappingName, DatabaseMapping mapping) {
    }

    /**
     * Invoked from a descriptor add the descriptor for DirectToXMLTypeMapping
     * @param namespacePrefix 
     */
    public void addXDBDescriptors(String name, DatabaseSessionImpl session,
        NamespaceResolver namespaceResolver) {
    }
}
