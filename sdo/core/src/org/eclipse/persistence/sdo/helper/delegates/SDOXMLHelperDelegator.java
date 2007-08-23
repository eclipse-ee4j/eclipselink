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
import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.XMLDocument;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.WeakHashMap;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.helper.SDOClassLoader;
import org.eclipse.persistence.sdo.helper.SDOXMLHelper;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.sessions.Project;
import org.xml.sax.InputSource;

/**
 * <p><b>Purpose</b>: Helper to XML documents into DataObects and DataObjects into XML documents.
 * <p><b>Responsibilities</b>:<ul>
 * <li> Finds the appropriate SDOXMLHelperDelegate for the classLoader and delegates work to that 
 * <li> Load methods create commonj.sdo.XMLDocument objects from XML (unmarshal) 
 * <li> Save methods create XML from commonj.sdo.XMLDocument and commonj.sdo.DataObject objects (marshal) 
 * </ul>
 */
public class SDOXMLHelperDelegator implements SDOXMLHelper {
    private Map sdoXMLHelperDelegates;

    // hold the context containing all helpers so that we can preserve inter-helper relationships
    private HelperContext aHelperContext;

    public SDOXMLHelperDelegator() {
        //aHelperContext = HelperProvider.getDefaultContext();
        sdoXMLHelperDelegates = new WeakHashMap();
    }

    public SDOXMLHelperDelegator(HelperContext aContext) {
        super();
        aHelperContext = aContext;
        sdoXMLHelperDelegates = new WeakHashMap();
    }

    /**
     * The specified TimeZone will be used for all String to date object
     * conversions.  By default the TimeZone from the JVM is used.
     */   
    public void setTimeZone(TimeZone timeZone) {
    	getSDOXMLHelperDelegate().setTimeZone(timeZone);	
    }
    
    /**
     * By setting this flag to true the marshalled date objects marshalled to 
     * the XML schema types time and dateTime will be qualified by a time zone.  
     * By default time information is not time zone qualified.
     */
    public void setTimeZoneQualified(boolean timeZoneQualified) {
    	getSDOXMLHelperDelegate().setTimeZoneQualified(timeZoneQualified);	    	
    }

    public XMLDocument load(String inputString) {
        return getSDOXMLHelperDelegate().load(inputString);
    }

    public XMLDocument load(InputStream inputStream) throws IOException {
        return getSDOXMLHelperDelegate().load(inputStream);
    }

    public XMLDocument load(InputStream inputStream, String locationURI, Object options) throws IOException {
        return getSDOXMLHelperDelegate().load(inputStream, locationURI, options);
    }

    public XMLDocument load(InputSource inputSource, String locationURI, Object options) throws IOException {
        return getSDOXMLHelperDelegate().load(inputSource, locationURI, options);
    }

    public XMLDocument load(Reader inputReader, String locationURI, Object options) throws IOException {
        return getSDOXMLHelperDelegate().load(inputReader, locationURI, options);
    }

    public XMLDocument load(Source source, String locationURI, Object options) throws IOException {
        return getSDOXMLHelperDelegate().load(source, locationURI, options);
    }

    public String save(DataObject dataObject, String rootElementURI, String rootElementName) {
        return getSDOXMLHelperDelegate().save(dataObject, rootElementURI, rootElementName);
    }

    public void save(DataObject dataObject, String rootElementURI, String rootElementName, OutputStream outputStream) throws IOException {
        getSDOXMLHelperDelegate().save(dataObject, rootElementURI, rootElementName, outputStream);
    }

    public void save(XMLDocument xmlDocument, OutputStream outputStream, Object options) throws IOException {
        getSDOXMLHelperDelegate().save(xmlDocument, outputStream, options);
    }

    public void save(XMLDocument xmlDocument, Writer outputWriter, Object options) throws IOException {
        getSDOXMLHelperDelegate().save(xmlDocument, outputWriter, options);
    }

    public void save(XMLDocument xmlDocument, Result result, Object options) throws IOException {
        getSDOXMLHelperDelegate().save(xmlDocument, result, options);
    }

    public XMLDocument createDocument(DataObject dataObject, String rootElementURI, String rootElementName) {
        return getSDOXMLHelperDelegate().createDocument(dataObject, rootElementURI, rootElementName);
    }

    public void setLoader(SDOClassLoader loader) {
        getSDOXMLHelperDelegate().setLoader(loader);
    }

    public SDOClassLoader getLoader() {
        return getSDOXMLHelperDelegate().getLoader();
    }

    public void setXmlContext(XMLContext xmlContext) {
        getSDOXMLHelperDelegate().setXmlContext(xmlContext);
    }

    public XMLContext getXmlContext() {
        return getSDOXMLHelperDelegate().getXmlContext();
    }

    public void addDescriptor(XMLDescriptor descriptor) {
        getSDOXMLHelperDelegate().addDescriptor(descriptor);
    }

    public void addDescriptors(List descriptors) {
        getSDOXMLHelperDelegate().addDescriptors(descriptors);
    }

    public void setTopLinkProject(Project toplinkProject) {
        getSDOXMLHelperDelegate().setTopLinkProject(toplinkProject);
    }

    public Project getTopLinkProject() {
        return getSDOXMLHelperDelegate().getTopLinkProject();
    }

    public void setXmlMarshaller(XMLMarshaller xmlMarshaller) {
        getSDOXMLHelperDelegate().setXmlMarshaller(xmlMarshaller);
    }

    public XMLMarshaller getXmlMarshaller() {
        return getSDOXMLHelperDelegate().getXmlMarshaller();
    }

    public void setXmlUnmarshaller(XMLUnmarshaller xmlUnmarshaller) {
        getSDOXMLHelperDelegate().setXmlUnmarshaller(xmlUnmarshaller);
    }

    public XMLUnmarshaller getXmlUnmarshaller() {
        return getSDOXMLHelperDelegate().getXmlUnmarshaller();
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
    private SDOXMLHelperDelegate getSDOXMLHelperDelegate() {
        ClassLoader contextClassLoader = getContextClassLoader();
        SDOXMLHelperDelegate sdoXMLHelperDelegate = (SDOXMLHelperDelegate)sdoXMLHelperDelegates.get(contextClassLoader);
        if (null == sdoXMLHelperDelegate) {
            sdoXMLHelperDelegate = new SDOXMLHelperDelegate(aHelperContext);
            sdoXMLHelperDelegates.put(contextClassLoader, sdoXMLHelperDelegate);
            AbstractSessionLog.getLog().log(AbstractSessionLog.FINEST, "{0} creating new {1} keyed on classLoader: {2}", //
            		new Object[] {getClass().getName(), sdoXMLHelperDelegate, contextClassLoader.toString()}, false);
        }
        return sdoXMLHelperDelegate;
    }

    public HelperContext getHelperContext() {
        return aHelperContext;
    }

    public void setHelperContext(HelperContext helperContext) {
        aHelperContext = helperContext;
    }

    public void reset() {
        getSDOXMLHelperDelegate().reset();
    }
}