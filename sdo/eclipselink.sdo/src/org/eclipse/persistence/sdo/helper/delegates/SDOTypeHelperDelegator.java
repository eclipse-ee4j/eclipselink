/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.sdo.helper.delegates;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.impl.HelperProvider;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import javax.xml.namespace.QName;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.helper.SDOTypeHelper;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.oxm.NamespaceResolver;

/**
 * <p><b>Purpose</b>: Helper to provide access to declared SDO Types.
 * <p><b>Responsibilities</b>:<ul>
 * <li> Finds the appropriate SDOTypeHelperDelegate for the classLoader and delegates work to that
 * <li> Look up a Type given the uri and typeName or interfaceClass.
 * <li> SDO Types are available through the getType("commonj.sdo", typeName) method.
 * <li> Defines Types from DataObjects.
 * </ul>
 */
public class SDOTypeHelperDelegator implements SDOTypeHelper {
    private Map sdoTypeHelperDelegates;

    // hold the context containing all helpers so that we can preserve inter-helper relationships
    private HelperContext aHelperContext;

    public SDOTypeHelperDelegator() {
        super();
        // TODO: JIRA129 - default to static global context - Do Not use this convenience constructor outside of JUnit testing
        //aHelperContext = HelperProvider.getDefaultContext();
        sdoTypeHelperDelegates = new WeakHashMap();
    }

    public SDOTypeHelperDelegator(HelperContext aContext) {
        super();
        aHelperContext = aContext;
        sdoTypeHelperDelegates = new WeakHashMap();
    }

    public Class getJavaWrapperTypeForSDOType(Type sdoType) {
        return getSDOTypeHelperDelegate().getJavaWrapperTypeForSDOType(sdoType);
    }

    public Type getType(String uri, String typeName) {
        return getSDOTypeHelperDelegate().getType(uri, typeName);
    }

    public Type getTypeForSimpleJavaType(Class implClass) {
        return getSDOTypeHelperDelegate().getTypeForSimpleJavaType(implClass);
    }

    public Type getOrCreateType(String uri, String typeName, String xsdLocalName) {
        return getSDOTypeHelperDelegate().getOrCreateType(uri, typeName, xsdLocalName);
    }

    public Type getType(Class interfaceClass) {
        return getSDOTypeHelperDelegate().getType(interfaceClass);
    }

    public Type getOrCreateType(Type next) {
        return getSDOTypeHelperDelegate().getOrCreateType(next);
    }

    public Type define(DataObject dataObject) {
        return getSDOTypeHelperDelegate().define(dataObject);
    }

    public List define(List types) {
        return getSDOTypeHelperDelegate().define(types);
    }

    public QName getXSDTypeFromSDOType(Type aType) {
        return getSDOTypeHelperDelegate().getXSDTypeFromSDOType(aType);
    }

    public SDOType getSDOTypeFromXSDType(QName aName) {
        return getSDOTypeHelperDelegate().getSDOTypeFromXSDType(aName);
    }

    public void setTypesHashMap(Map typesHashMap) {
        getSDOTypeHelperDelegate().setTypesHashMap(typesHashMap);
    }

    public Map getTypesHashMap() {
        return getSDOTypeHelperDelegate().getTypesHashMap();
    }

    public void reset() {
        getSDOTypeHelperDelegate().reset();
    }

    public Property defineOpenContentProperty(String uri, DataObject property) {
        return getSDOTypeHelperDelegate().defineOpenContentProperty(uri, property);
    }

    public Property getOpenContentProperty(String uri, String propertyName) {
        return getSDOTypeHelperDelegate().getOpenContentProperty(uri, propertyName);
    }

    /**
     * INTERNAL:
     * This function returns the current or parent ClassLoader.
     * We return the parent application ClassLoader when running in a J2EE client either in a
     * web or ejb container to match a weak reference to a particular helpercontext.
     */
    private ClassLoader getContextClassLoader() {

        /**
         * Classloader levels: (oc4j specific
         *  0 - APP.web (servlet/jsp) or APP.wrapper (ejb) or
         *  1 - APP.root (parent for helperContext)
         *  2 - default.root
         *  3 - system.root
         *  4 - oc4j.10.1.3 (remote EJB) or org.eclipse.persistence:11.1.1.0.0
         *  5 - api:1.4.0
         *  6 - jre.extension:0.0.0
         *  7 - jre.bootstrap:1.5.0_07 (with various J2SE versions)
         *  */

        // Kludge for running in OC4J - from WebServices group
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if ((classLoader.getParent() != null) && (classLoader.toString().indexOf(SDOConstants.CLASSLOADER_EJB_FRAGMENT) != -1)) {
            // we are running in a servlet container
            AbstractSessionLog.getLog().log(AbstractSessionLog.FINEST, "{0} matched classLoader: {1} to parent cl: {2}",//
                                            new Object[] { getClass().getName(), classLoader.toString(), classLoader.getParent().toString() }, false);
            classLoader = classLoader.getParent();
            // check if we are running in an ejb container
        } else if ((classLoader.getParent() != null) && (classLoader.toString().indexOf(SDOConstants.CLASSLOADER_WEB_FRAGMENT) != -1)) {
            // we are running in a local ejb container
            AbstractSessionLog.getLog().log(AbstractSessionLog.FINEST, "{0} matched classLoader: {1} to parent cl: {2}",//
                                            new Object[] { getClass().getName(), classLoader.toString(), classLoader.getParent().toString() }, false);
            classLoader = classLoader.getParent();
        } else {
            // we are running in a J2SE client (toString() contains a JVM hash) or an unmatched container level
        }
        return classLoader;
    }

    /**
     * INTERNAL:
     * @return
     */
    private SDOTypeHelperDelegate getSDOTypeHelperDelegate() {
        ClassLoader contextClassLoader = getContextClassLoader();
        SDOTypeHelperDelegate sdoTypeHelperDelegate = (SDOTypeHelperDelegate)sdoTypeHelperDelegates.get(contextClassLoader);
        if (null == sdoTypeHelperDelegate) {
            sdoTypeHelperDelegate = new SDOTypeHelperDelegate(getHelperContext());
            sdoTypeHelperDelegates.put(contextClassLoader, sdoTypeHelperDelegate);
            AbstractSessionLog.getLog().log(AbstractSessionLog.FINEST, "{0} creating new {1} keyed on classLoader: {2}",//
                                            new Object[] { getClass().getName(), sdoTypeHelperDelegate, contextClassLoader.toString() }, false);
        }
        return sdoTypeHelperDelegate;
    }

    public HelperContext getHelperContext() {
        if (null == aHelperContext) {
            aHelperContext = HelperProvider.getDefaultContext();
        }
        return aHelperContext;
    }

    public void setHelperContext(HelperContext helperContext) {
        aHelperContext = helperContext;
    }

    /**
      * INTERNAL:
      * Add the given namespace uri and prefix to the global namespace resolver.
      */
    public String addNamespace(String prefix, String uri) {
        return getSDOTypeHelperDelegate().addNamespace(prefix, uri);
    }

    /**
      * INTERNAL:
      * Return the prefix for the given uri, or generate a new one if necessary
      */
    public String getPrefix(String uri) {
        return getSDOTypeHelperDelegate().getPrefix(uri);
    }

    /**
      * INTERNAL:
      * Return the NamespaceResolver
      */
    public NamespaceResolver getNamespaceResolver() {
        return getSDOTypeHelperDelegate().getNamespaceResolver();
    }

    /**
    * INTERNAL:
    * Return the Map of Open Content Properties
    */
    public Map getOpenContentProperties() {
        return getSDOTypeHelperDelegate().getOpenContentProperties();
    }
}