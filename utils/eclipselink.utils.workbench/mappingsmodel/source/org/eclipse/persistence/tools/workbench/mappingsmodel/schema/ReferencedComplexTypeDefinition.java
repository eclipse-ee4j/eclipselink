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

import javax.xml.XMLConstants;

import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.QName;

import org.eclipse.persistence.oxm.XMLDescriptor;

public final class ReferencedComplexTypeDefinition
    extends ReferencedSchemaTypeDefinition
    implements MWComplexTypeDefinition
{
    private volatile ExplicitComplexTypeDefinition complexType;


    // **************** Constructors ******************************************

    /** Toplink use only */
    private ReferencedComplexTypeDefinition() {
        super();
    }

    ReferencedComplexTypeDefinition(AbstractSchemaModel parent, String typeName, String typeNamespace) {
        super(parent, typeName, typeNamespace);
    }


    // **************** SchemaComponentReference contract *********************

    protected MWNamedSchemaComponent getReferencedComponent() {
        return this.complexType;
    }

    protected void resolveReference(String complexTypeNamespace, String complexTypeName) {
        this.complexType = (ExplicitComplexTypeDefinition) this.getSchema().complexType(complexTypeNamespace, complexTypeName);
    }


    // **************** MWComplexTypeDefinition contract **********************

    public boolean isAbstract() {
        return this.complexType.isAbstract();
    }

    public String getDerivationMethod() {
        return this.complexType.getDerivationMethod();
    }

    public int attributeCount() {
        return this.complexType.attributeCount();
    }


    // **************** MWSchemaTypeDefiniton contract ************************

    public MWSchemaTypeDefinition getBaseType() {
        return this.complexType.getBaseType();
    }

    public boolean isComplex() {
        return true;
    }

    public Iterator baseBuiltInTypes() {
        return this.complexType.baseBuiltInTypes();
    }


    // **************** MWSchemaModel contract ********************************

    public MWNamedSchemaComponent nestedNamedComponent(QName qName) {
        return this.complexType.nestedNamedComponent(qName);
    }

    public int totalElementCount() {
        return this.complexType.totalElementCount();
    }

    protected void reloadName(String name, String namespace) {
        if (name == null) {
            name = "anyType";
            namespace = XMLConstants.W3C_XML_SCHEMA_NS_URI;
        }

        super.reloadName(name, namespace);
    }


    // **************** TopLink methods ***************************************

    public static XMLDescriptor buildDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(ReferencedComplexTypeDefinition.class);
        descriptor.getInheritancePolicy().setParentClass(ReferencedSchemaTypeDefinition.class);
        return descriptor;
    }
}
