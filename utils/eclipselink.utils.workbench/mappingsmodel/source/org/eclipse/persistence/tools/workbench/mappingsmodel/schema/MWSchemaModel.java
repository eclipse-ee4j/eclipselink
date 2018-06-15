/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.mappingsmodel.schema;

import java.util.Iterator;

import org.apache.xerces.xs.XSObject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWNode;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.QName;
import org.eclipse.persistence.tools.workbench.utility.Model;


public interface MWSchemaModel
    extends MWNode, Model
{
    MWNamespace getParentNamespace();

    MWXmlSchema getSchema();

    /**
     * Return the child structural components (elements/ attributes/ groups)
     * of this schema model object
     */
    Iterator structuralComponents();

    /**
     * Return the child schema components of this schema model object
     * which may serve as the context for a descriptor
     */
    Iterator descriptorContextComponents();

    /**
     * Return the child schema components (attributes and elements) used for xpaths
     * of this schema model object
     */
    Iterator xpathComponents();

    /**
     * Return the named component immediately parenting
     * (no interposed named components) this schema model object.
     */
    MWNamedSchemaComponent parentNamedComponent();

    /**
     * Return the appropriate named component immediately nested
     * (no interposed named components) within this schema model object.
     *
     * NOTE: This is used predominantly by TopLink methods
     */
    MWNamedSchemaComponent nestedNamedComponent(QName qName);

    /**
     * Return the attribute immediately nested
     * (no interposed named components) within this schema model object.
     */
    MWAttributeDeclaration nestedAttribute(String namespaceUrl, String attributeName);

    /**
     * Return the element immediately nested
     * (no interposed elements) within this schema model object.
     */
    MWElementDeclaration nestedElement(String namespaceUrl, String elementName);

    /**
     * Return the number of elements immediately nested
     * (no interposed elements) within this schema model object.
     */
    int totalElementCount();

    // **************** Used internally only **********************************

    void reload(XSObject schemaObject);

    void resolveReferences();
}
