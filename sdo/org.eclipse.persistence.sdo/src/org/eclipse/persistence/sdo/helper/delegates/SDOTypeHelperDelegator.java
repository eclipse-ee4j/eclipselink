/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.HelperContext;

import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.helper.SDOHelperContext;
import org.eclipse.persistence.sdo.helper.SDOTypeHelper;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.oxm.NamespaceResolver;

/**
 * <p><b>Purpose</b>: Helper to provide access to declared SDO Types.
 * <p><b>Responsibilities</b>:<ul>
 * <li> Finds the appropriate SDOTypeHelperDelegate for the classLoader/application name and delegates work to that
 * <li> Look up a Type given the uri and typeName or interfaceClass.
 * <li> SDO Types are available through the getType("commonj.sdo", typeName) method.
 * <li> Defines Types from DataObjects.
 * </ul>
 */
public class SDOTypeHelperDelegator extends AbstractHelperDelegator implements SDOTypeHelper {

    public SDOTypeHelperDelegator() { 
    }

    public SDOTypeHelperDelegator(HelperContext aContext) {
        aHelperContext = aContext;
    }

    public Class getJavaWrapperTypeForSDOType(Type sdoType) {
        return getTypeHelperDelegate().getJavaWrapperTypeForSDOType(sdoType);
    }

    public Type getType(String uri, String typeName) {
        return getTypeHelperDelegate().getType(uri, typeName);
    }

    public Type getTypeForSimpleJavaType(Class implClass) {
        return getTypeHelperDelegate().getTypeForSimpleJavaType(implClass);
    }

    public void addType(SDOType newType) {
        getTypeHelperDelegate().addType(newType);
    }

    public Type getType(Class interfaceClass) {
        return getTypeHelperDelegate().getType(interfaceClass);
    }

    public SDOType getTypeForImplClass(Class implClass) {
        return getTypeHelperDelegate().getTypeForImplClass(implClass);
    }

    public Type define(DataObject dataObject) {
        return getTypeHelperDelegate().define(dataObject);
    }

    public List define(List types) {
        return getTypeHelperDelegate().define(types);
    }

    public QName getXSDTypeFromSDOType(Type aType) {
        return getTypeHelperDelegate().getXSDTypeFromSDOType(aType);
    }

    public SDOType getSDOTypeFromXSDType(QName aName) {
        return getTypeHelperDelegate().getSDOTypeFromXSDType(aName);
    }

    public void setTypesHashMap(Map typesHashMap) {
        getTypeHelperDelegate().setTypesHashMap(typesHashMap);
    }

    public Map getTypesHashMap() {
        return getTypeHelperDelegate().getTypesHashMap();
    }

    /**
     * INTERNAL:
     * Return the map of Wrapper objects (SDOWrapperTypes that wrap a primitive document).
     * @return a HashMap of SDOWrapperTypes, keyed on the XSD type that it wraps.
     */
    public Map getWrappersHashMap() {
        return getTypeHelperDelegate().getWrappersHashMap();
    }

    /**
     * INTERNAL:
     * Set the map of Wrapper objects (SDOWrapperTypes that wrap a primitive document).
     * @param   aMap        a HashMap of SDOWrapperTypes, keyed on the XSD type that it wraps.
     */
    public void setWrappersHashMap(Map aMap) {
        getTypeHelperDelegate().setWrappersHashMap(aMap);
    }

    public void reset() {
        getTypeHelperDelegate().reset();
    }

    public Property defineOpenContentProperty(String uri, DataObject property) {
        return getTypeHelperDelegate().defineOpenContentProperty(uri, property);
    }

    public Property getOpenContentProperty(String uri, String propertyName) {
        return getTypeHelperDelegate().getOpenContentProperty(uri, propertyName);
    }

    public SDOTypeHelperDelegate getTypeHelperDelegate() {
        return (SDOTypeHelperDelegate) SDOHelperContext.getHelperContext().getTypeHelper();
    }

    /**
      * INTERNAL:
      * Add the given namespace uri and prefix to the global namespace resolver.
      */
    public String addNamespace(String prefix, String uri) {
        return getTypeHelperDelegate().addNamespace(prefix, uri);
    }

    /**
      * INTERNAL:
      * Return the prefix for the given uri, or generate a new one if necessary
      */
    public String getPrefix(String uri) {
        return getTypeHelperDelegate().getPrefix(uri);
    }

    /**
      * INTERNAL:
      * Return the NamespaceResolver
      */
    public NamespaceResolver getNamespaceResolver() {
        return getTypeHelperDelegate().getNamespaceResolver();
    }

    /**
    * INTERNAL:
    * Return the Map of Open Content Properties
    */
    public Map getOpenContentProperties() {
        return getTypeHelperDelegate().getOpenContentProperties();
    }

    public void addWrappersToProject(Project toplinkProject) {
        getTypeHelperDelegate().addWrappersToProject(toplinkProject);
    }

    public Map getInterfacesToSDOTypeHashMap() {
        return getTypeHelperDelegate().getInterfacesToSDOTypeHashMap();
    }

    public Map<Class, SDOType> getImplClassesToSDOType() {
        return getTypeHelperDelegate().getImplClassesToSDOType();
    }

    public List getAnonymousTypes() {
        return getTypeHelperDelegate().getAnonymousTypes();
    }

}
