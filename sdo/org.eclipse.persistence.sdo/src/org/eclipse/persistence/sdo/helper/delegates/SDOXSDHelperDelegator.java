/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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
import java.util.WeakHashMap;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.helper.SDOXSDHelper;
import org.eclipse.persistence.sdo.helper.SchemaLocationResolver;
import org.eclipse.persistence.sdo.helper.SchemaResolver;
import org.eclipse.persistence.logging.AbstractSessionLog;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.impl.HelperProvider;

/**
 * <p><b>Purpose</b>: Provides access to additional information when the Type or Property is defined by an XML Schema (XSD)..
 * <p><b>Responsibilities</b>:<ul>
 * <li> Finds the appropriate SDOXSDHelperDelegate for the classLoader and delegates work to that implementation of SDOXSDHelper. 
 * <li> Define methods defines Types from an XSD.
 * <li> Generate methods an XSD from Types.
 * <li> Other Methods return null/false otherwise or if the information is unavailable.
 * </ul>
 */
public class SDOXSDHelperDelegator implements SDOXSDHelper {
    private Map sdoXSDHelperDelegates;

    // hold the context containing all helpers so that we can preserve inter-helper relationships
    private HelperContext aHelperContext;

    public SDOXSDHelperDelegator() {
        // TODO: JIRA129 - default to static global context - Do Not use this convenience constructor outside of JUnit testing
        //aHelperContext = HelperProvider.getDefaultContext();
        sdoXSDHelperDelegates = new WeakHashMap();
    }

    public SDOXSDHelperDelegator(HelperContext aContext) {
        super();
        aHelperContext = aContext;
        sdoXSDHelperDelegates = new WeakHashMap();
    }

    public String getLocalName(Type type) {
        return getSDOXSDHelperDelegate().getLocalName(type);
    }

    public String getLocalName(Property property) {
        return getSDOXSDHelperDelegate().getLocalName(property);
    }

    public String getNamespaceURI(Property property) {
        return getSDOXSDHelperDelegate().getNamespaceURI(property);
    }

    public boolean isAttribute(Property property) {
        return getSDOXSDHelperDelegate().isAttribute(property);
    }

    public boolean isElement(Property property) {
        return getSDOXSDHelperDelegate().isElement(property);
    }

    public boolean isMixed(Type type) {
        return getSDOXSDHelperDelegate().isMixed(type);
    }

    public boolean isXSD(Type type) {
        return getSDOXSDHelperDelegate().isXSD(type);
    }

    public Property getGlobalProperty(String uri, String propertyName, boolean isElement) {
        return getSDOXSDHelperDelegate().getGlobalProperty(uri, propertyName, isElement);
    }

    public Property getGlobalProperty(QName qname, boolean isElement) {
        return getSDOXSDHelperDelegate().getGlobalProperty(qname, isElement);
    }

    public String getAppinfo(Type type, String source) {
        return getSDOXSDHelperDelegate().getAppinfo(type, source);
    }

    public String getAppinfo(Property property, String source) {
        return getSDOXSDHelperDelegate().getAppinfo(property, source);
    }

    public List define(String xsd) {
        return getSDOXSDHelperDelegate().define(xsd);
    }

    public List define(Reader xsdReader, String schemaLocation) {
        return getSDOXSDHelperDelegate().define(xsdReader, schemaLocation);
    }

    public List define(Source xsdSource, SchemaResolver schemaResolver) {
        return getSDOXSDHelperDelegate().define(xsdSource, schemaResolver);
    }

    public List define(InputStream xsdInputStream, String schemaLocation) {
        return getSDOXSDHelperDelegate().define(xsdInputStream, schemaLocation);
    }

    public String generate(List types) {
        return getSDOXSDHelperDelegate().generate(types);
    }

    public String generate(List types, Map namespaceToSchemaLocation) {
        return getSDOXSDHelperDelegate().generate(types, namespaceToSchemaLocation);
    }

    public String generate(List types, SchemaLocationResolver schemaLocationResolver) {
        return getSDOXSDHelperDelegate().generate(types, schemaLocationResolver);
    }

    /**
     * INTERNAL:
     */	  
    public Map buildAppInfoMap(List appInfoElements) {
        return getSDOXSDHelperDelegate().buildAppInfoMap(appInfoElements);
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
        getSDOXSDHelperDelegate().addGlobalProperty(qname, prop, isElement);
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
            AbstractSessionLog.getLog().log(AbstractSessionLog.FINEST, "{0} matched classLoader: {1} to parent cl: {2}", //
            		new Object[] {getClass().getName(), classLoader.toString(), classLoader.getParent().toString()}, false);
            classLoader = classLoader.getParent();
            // check if we are running in an ejb container
        } else if ((classLoader.getParent() != null) && (classLoader.toString().indexOf(SDOConstants.CLASSLOADER_WEB_FRAGMENT) != -1)) {
        	// we are running in a local ejb container
            AbstractSessionLog.getLog().log(AbstractSessionLog.FINEST, "{0} matched classLoader: {1} to parent cl: {2}", //
            		new Object[] {getClass().getName(), classLoader.toString(), classLoader.getParent().toString()}, false);
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
    private SDOXSDHelperDelegate getSDOXSDHelperDelegate() {
        ClassLoader contextClassLoader = getContextClassLoader();
        SDOXSDHelperDelegate sdoXSDHelperDelegate = (SDOXSDHelperDelegate)sdoXSDHelperDelegates.get(contextClassLoader);
        if (null == sdoXSDHelperDelegate) {
            sdoXSDHelperDelegate = new SDOXSDHelperDelegate(getHelperContext());
            sdoXSDHelperDelegates.put(contextClassLoader, sdoXSDHelperDelegate);
            AbstractSessionLog.getLog().log(AbstractSessionLog.FINEST, "{0} creating new {1} keyed on classLoader: {2}", //
            		new Object[] {getClass().getName(), sdoXSDHelperDelegate, contextClassLoader.toString()}, false);
        }
        return sdoXSDHelperDelegate;
    }

    public HelperContext getHelperContext() {
        if(null == aHelperContext) {
            aHelperContext = HelperProvider.getDefaultContext();
        }
        return aHelperContext;
    }

    public void setHelperContext(HelperContext helperContext) {
        aHelperContext = helperContext;
    }

    public void reset() {
        getSDOXSDHelperDelegate().reset();
    }
   
}