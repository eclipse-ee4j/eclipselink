/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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

    public String getLocalName(Type type) {
        return getXSDHelperDelegate().getLocalName(type);
    }

    public String getLocalName(Property property) {
        return getXSDHelperDelegate().getLocalName(property);
    }

    public String getNamespaceURI(Type type) {
        return getXSDHelperDelegate().getNamespaceURI(type);
    }

    public String getNamespaceURI(Property property) {
        return getXSDHelperDelegate().getNamespaceURI(property);
    }

    public boolean isAttribute(Property property) {
        return getXSDHelperDelegate().isAttribute(property);
    }

    public boolean isElement(Property property) {
        return getXSDHelperDelegate().isElement(property);
    }

    public boolean isMixed(Type type) {
        return getXSDHelperDelegate().isMixed(type);
    }

    public boolean isXSD(Type type) {
        return getXSDHelperDelegate().isXSD(type);
    }

    public Property getGlobalProperty(String uri, String propertyName, boolean isElement) {
        return getXSDHelperDelegate().getGlobalProperty(uri, propertyName, isElement);
    }

    public Property getGlobalProperty(QName qname, boolean isElement) {
        return getXSDHelperDelegate().getGlobalProperty(qname, isElement);
    }

    public String getAppinfo(Type type, String source) {
        return getXSDHelperDelegate().getAppinfo(type, source);
    }

    public String getAppinfo(Property property, String source) {
        return getXSDHelperDelegate().getAppinfo(property, source);
    }

    public List define(String xsd) {
        return getXSDHelperDelegate().define(xsd);
    }

    public List define(Reader xsdReader, String schemaLocation) {
        return getXSDHelperDelegate().define(xsdReader, schemaLocation);
    }

    public List define(Source xsdSource, SchemaResolver schemaResolver) {
        return getXSDHelperDelegate().define(xsdSource, schemaResolver);
    }

    public List define(InputStream xsdInputStream, String schemaLocation) {
        return getXSDHelperDelegate().define(xsdInputStream, schemaLocation);
    }

    public String generate(List types) {
        return getXSDHelperDelegate().generate(types);
    }

    public String generate(List types, Map namespaceToSchemaLocation) {
        return getXSDHelperDelegate().generate(types, namespaceToSchemaLocation);
    }

    public String generate(List types, SchemaLocationResolver schemaLocationResolver) {
        return getXSDHelperDelegate().generate(types, schemaLocationResolver);
    }

    /**
     * INTERNAL:
     */
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
    public void addGlobalProperty(QName qname, Property prop, boolean isElement) {
        getXSDHelperDelegate().addGlobalProperty(qname, prop, isElement);
    }

    public SDOXSDHelperDelegate getXSDHelperDelegate() {
        return (SDOXSDHelperDelegate) SDOHelperContext.getHelperContext().getXSDHelper();
    }

    public void reset() {
        getXSDHelperDelegate().reset();
    }

}
