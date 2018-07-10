/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.mappingsmodel.schema;

import java.util.Iterator;
import java.util.ListIterator;

import javax.xml.XMLConstants;

import org.eclipse.persistence.oxm.XMLDescriptor;

public final class ReferencedSimpleTypeDefinition
    extends ReferencedSchemaTypeDefinition
    implements MWSimpleTypeDefinition
{
    private volatile ExplicitSimpleTypeDefinition simpleType;


    // **************** Static methods ****************************************

    static ReferencedSimpleTypeDefinition simpleUrType(AbstractSchemaModel parent) {
        return new ReferencedSimpleTypeDefinition(parent, "anySimpleType", XMLConstants.W3C_XML_SCHEMA_NS_URI);
    }


    // **************** Constructors ******************************************

    /** Toplink use only */
    private ReferencedSimpleTypeDefinition() {
        super();
    }

    ReferencedSimpleTypeDefinition(AbstractSchemaModel parent, String typeName, String typeNamespace) {
        super(parent, typeName, typeNamespace);
    }


    // **************** MWSimpleTypeDefinition contract ***********************

    public String getVariety() {
        return this.simpleType.getVariety();
    }

    public MWSimpleTypeDefinition getItemType() {
        return this.simpleType.getItemType();
    }

    public ListIterator memberTypes() {
        return this.simpleType.memberTypes();
    }


    // **************** MWSchemaTypeDefinition contract ***********************

    public MWSchemaTypeDefinition getBaseType() {
        return this.simpleType.getBaseType();
    }

    public boolean isComplex() {
        return false;
    }

    public Iterator baseBuiltInTypes() {
        return this.simpleType.baseBuiltInTypes();
    }


    // **************** SchemaComponentReference contract *********************

    protected MWNamedSchemaComponent getReferencedComponent() {
        return this.simpleType;
    }

    protected void resolveReference(String simpleTypeNamespace, String simpleTypeName) {
        this.simpleType = (ExplicitSimpleTypeDefinition) this.getSchema().simpleType(simpleTypeNamespace, simpleTypeName);
    }


    // **************** SchemaModel contract **********************************

    protected void reloadName(String name, String namespace) {
        if (name == null) {
            name = "anySimpleType";
            namespace = XMLConstants.W3C_XML_SCHEMA_NS_URI;
        }

        super.reloadName(name, namespace);
    }


    // **************** TopLink methods ***************************************

    public static XMLDescriptor buildDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(ReferencedSimpleTypeDefinition.class);
        descriptor.getInheritancePolicy().setParentClass(ReferencedSchemaTypeDefinition.class);
        return descriptor;
    }
}
