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
package org.eclipse.persistence.jaxb.compiler;

import java.util.ArrayList;

import javax.xml.namespace.QName;

/**
 * INTERNAL:
 * <p><b>Purpose:</b>Provide additional information about JAXB 2.0 Generated Schemas to
 * callers.
 * <p><b>Responsibilities:</b><ul>
 * <li>Store information about a schema type generated for a specific class</li>
 * <li>Store information about any globalElementDeclarations that were generated for a specific class</li>
 * <li>Act as an integration point with WebServices</li>
 * </ul>
 * <p>This class was created as a means to return specific information about generated schema
 * artifacts for a particular java class. A Map of SchemaTypeInfo is returned from schema generation
 * operations on TopLinkJAXB20Generator.
 *
 * @author mmacivor
 * @since Oracle TopLink 11.1.1.0.0
 * @see org.eclipse.persistence.jaxb.compiler.Generator
 */
public class SchemaTypeInfo {
    private QName schemaTypeName;
    private ArrayList<QName> globalElementDeclarations;

    public QName getSchemaTypeName() {
        return schemaTypeName;
    }

    public void setSchemaTypeName(QName typeName) {
        this.schemaTypeName = typeName;
    }

    public ArrayList<QName> getGlobalElementDeclarations() {
        if(globalElementDeclarations == null) {
            globalElementDeclarations = new ArrayList();
        }
        return globalElementDeclarations;
    }

    public void setGlobalElementDeclarations(ArrayList<QName> elementDeclarations) {
        this.globalElementDeclarations = elementDeclarations;
    }
}

