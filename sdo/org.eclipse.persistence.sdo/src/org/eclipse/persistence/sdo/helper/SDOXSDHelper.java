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
package org.eclipse.persistence.sdo.helper;

import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import commonj.sdo.Property;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.XSDHelper;

/**
 * <p><b>Purpose</b>: Provides access to additional information when the Type or Property is defined by an XML Schema (XSD).
 * <p><b>Responsibilities</b>:<ul>
 * <li> Define methods define Types from an XSD.
 * <li> Generate methods generate an XSD from Types.
 * <li> Other Methods return null/false for exception conditions.
 * </ul>
 */
public interface SDOXSDHelper extends XSDHelper {

    /**
     * INTERNAL:
     *
     * @param qname
     * @param isElement
     * @return
     */
    public Property getGlobalProperty(QName qname, boolean isElement);

    /**
     * INTERNAL:
     *
     * @param xsdSource
     * @param schemaResolver
     * @return
     */
    public List define(Source xsdSource, SchemaResolver schemaResolver);

    /**
     * INTERNAL:
     *
     * @param types
     * @param schemaLocationResolver
     * @return
     */
    public String generate(List types, SchemaLocationResolver schemaLocationResolver);

    /**
     * INTERNAL:
     *
     * @param appInfoElements
     * @return
     */
    public Map buildAppInfoMap(List appInfoElements);

    /**
     * INTERNAL:
     * Return the helperContext that this instance is associated with.
     *
     * @return
     */
    public HelperContext getHelperContext();

    /**
     * INTERNAL:
     * Set the helperContext that this instance is associated with.
     *
     * @param helperContext
     */
    public void setHelperContext(HelperContext helperContext);

    /**
     * INTERNAL:
     *
     */
    public void reset();

    /**
     * INTERNAL:
     *
     * @param qname
     * @param prop
     * @param isElement
     * Register the given property with the given qname.
     */
    public void addGlobalProperty(QName qname, Property prop, boolean isElement);
}
