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
package org.eclipse.persistence.sdo.helper.delegates;

import java.io.InputStream;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;

import org.eclipse.persistence.sdo.helper.SDOHelperContext;
import org.eclipse.persistence.sdo.helper.SDOXSDHelper;
import org.eclipse.persistence.sdo.helper.SchemaLocationResolver;
import org.eclipse.persistence.sdo.helper.SchemaResolver;

import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.HelperContext;

/**
 * <p><b>Purpose</b>: Provides access to additional information when the Type or Property is defined by an XML Schema (XSD)..
 * <p><b>Responsibilities</b>:<ul>
 * <li> Finds the appropriate SDOXSDHelperDelegate for the classLoader/application name and delegates work to that implementation of SDOXSDHelper.
 * <li> Define methods defines Types from an XSD.
 * <li> Generate methods an XSD from Types.
 * <li> Other Methods return null/false otherwise or if the information is unavailable.
 * </ul>
 */
public class SDOXSDHelperDelegator extends AbstractHelperDelegator implements SDOXSDHelper {

    public SDOXSDHelperDelegator() {
    }

    public SDOXSDHelperDelegator(HelperContext aContext) {
        super();
        aHelperContext = aContext;
    }

    @Override
    public String getLocalName(Type type) {
        return getXSDHelperDelegate().getLocalName(type);
    }

    @Override
    public String getLocalName(Property property) {
        return getXSDHelperDelegate().getLocalName(property);
    }

    @Override
    public String getNamespaceURI(Type type) {
        return getXSDHelperDelegate().getNamespaceURI(type);
    }

    @Override
    public String getNamespaceURI(Property property) {
        return getXSDHelperDelegate().getNamespaceURI(property);
    }

    @Override
    public boolean isAttribute(Property property) {
        return getXSDHelperDelegate().isAttribute(property);
    }

    @Override
    public boolean isElement(Property property) {
        return getXSDHelperDelegate().isElement(property);
    }

    @Override
    public boolean isMixed(Type type) {
        return getXSDHelperDelegate().isMixed(type);
    }

    @Override
    public boolean isXSD(Type type) {
        return getXSDHelperDelegate().isXSD(type);
    }

    @Override
    public Property getGlobalProperty(String uri, String propertyName, boolean isElement) {
        return getXSDHelperDelegate().getGlobalProperty(uri, propertyName, isElement);
    }

    @Override
    public Property getGlobalProperty(QName qname, boolean isElement) {
        return getXSDHelperDelegate().getGlobalProperty(qname, isElement);
    }

    @Override
    public String getAppinfo(Type type, String source) {
        return getXSDHelperDelegate().getAppinfo(type, source);
    }

    @Override
    public String getAppinfo(Property property, String source) {
        return getXSDHelperDelegate().getAppinfo(property, source);
    }

    @Override
    public List define(String xsd) {
        return getXSDHelperDelegate().define(xsd);
    }

    @Override
    public List define(Reader xsdReader, String schemaLocation) {
        return getXSDHelperDelegate().define(xsdReader, schemaLocation);
    }

    @Override
    public List define(Source xsdSource, SchemaResolver schemaResolver) {
        return getXSDHelperDelegate().define(xsdSource, schemaResolver);
    }

    @Override
    public List define(InputStream xsdInputStream, String schemaLocation) {
        return getXSDHelperDelegate().define(xsdInputStream, schemaLocation);
    }

    @Override
    public String generate(List types) {
        return getXSDHelperDelegate().generate(types);
    }

    @Override
    public String generate(List types, Map namespaceToSchemaLocation) {
        return getXSDHelperDelegate().generate(types, namespaceToSchemaLocation);
    }

    @Override
    public String generate(List types, SchemaLocationResolver schemaLocationResolver) {
        return getXSDHelperDelegate().generate(types, schemaLocationResolver);
    }

    /**
     * INTERNAL:
     */
    @Override
    public Map buildAppInfoMap(List appInfoElements) {
        return getXSDHelperDelegate().buildAppInfoMap(appInfoElements);
    }

    /**
     * INTERNAL:
     *
     * @param qname
     * @param prop
     * @param isElement
     * Register the given property with the given qname.
     */
    @Override
    public void addGlobalProperty(QName qname, Property prop, boolean isElement) {
        getXSDHelperDelegate().addGlobalProperty(qname, prop, isElement);
    }

    public SDOXSDHelperDelegate getXSDHelperDelegate() {
        return (SDOXSDHelperDelegate) SDOHelperContext.getHelperContext().getXSDHelper();
    }

    @Override
    public void reset() {
        getXSDHelperDelegate().reset();
    }

}
