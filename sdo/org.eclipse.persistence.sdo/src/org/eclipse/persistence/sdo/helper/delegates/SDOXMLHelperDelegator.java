/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.XMLDocument;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.List;
import java.util.TimeZone;
import javax.xml.transform.Result;
import javax.xml.transform.Source;

import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.sdo.helper.SDOClassLoader;
import org.eclipse.persistence.sdo.helper.SDOHelperContext;
import org.eclipse.persistence.sdo.helper.SDOXMLHelper;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.sessions.Project;
import org.xml.sax.InputSource;

/**
 * <p><b>Purpose</b>: Helper to XML documents into DataObects and DataObjects into XML documents.
 * <p><b>Responsibilities</b>:<ul>
 * <li> Finds the appropriate SDOXMLHelperDelegate for the classLoader/application name and delegates work to that 
 * <li> Load methods create commonj.sdo.XMLDocument objects from XML (unmarshal) 
 * <li> Save methods create XML from commonj.sdo.XMLDocument and commonj.sdo.DataObject objects (marshal) 
 * </ul>
 */
public class SDOXMLHelperDelegator extends AbstractHelperDelegator implements SDOXMLHelper {

    public SDOXMLHelperDelegator() {
    }

    public SDOXMLHelperDelegator(HelperContext aContext) {
        super();
        aHelperContext = aContext;
    }

    /**
     * The specified TimeZone will be used for all String to date object
     * conversions.  By default the TimeZone from the JVM is used.
     */   
    public void setTimeZone(TimeZone timeZone) {
        getXMLHelperDelegate().setTimeZone(timeZone);
    }

    /**
     * By setting this flag to true the marshalled date objects marshalled to 
     * the XML schema types time and dateTime will be qualified by a time zone.  
     * By default time information is not time zone qualified.
     */
    public void setTimeZoneQualified(boolean timeZoneQualified) {
        getXMLHelperDelegate().setTimeZoneQualified(timeZoneQualified);
    }

    public XMLDocument load(String inputString) {
        return getXMLHelperDelegate().load(inputString);
    }

    public XMLDocument load(InputStream inputStream) throws IOException {
        return getXMLHelperDelegate().load(inputStream);
    }

    public XMLDocument load(InputStream inputStream, String locationURI, Object options) throws IOException {
        return getXMLHelperDelegate().load(inputStream, locationURI, options);
    }

    public XMLDocument load(InputSource inputSource, String locationURI, Object options) throws IOException {
        return getXMLHelperDelegate().load(inputSource, locationURI, options);
    }

    public XMLDocument load(Reader inputReader, String locationURI, Object options) throws IOException {
        return getXMLHelperDelegate().load(inputReader, locationURI, options);
    }

    public XMLDocument load(Source source, String locationURI, Object options) throws IOException {
        return getXMLHelperDelegate().load(source, locationURI, options);
    }

    public String save(DataObject dataObject, String rootElementURI, String rootElementName) {
        return getXMLHelperDelegate().save(dataObject, rootElementURI, rootElementName);
    }

    public void save(DataObject dataObject, String rootElementURI, String rootElementName, OutputStream outputStream) throws IOException {
        getXMLHelperDelegate().save(dataObject, rootElementURI, rootElementName, outputStream);
    }

    public void save(XMLDocument xmlDocument, OutputStream outputStream, Object options) throws IOException {
        getXMLHelperDelegate().save(xmlDocument, outputStream, options);
    }

    public void save(XMLDocument xmlDocument, Writer outputWriter, Object options) throws IOException {
        getXMLHelperDelegate().save(xmlDocument, outputWriter, options);
    }

    public void save(XMLDocument xmlDocument, Result result, Object options) throws IOException {
        getXMLHelperDelegate().save(xmlDocument, result, options);
    }

    public void serialize(XMLDocument xmlDocument, OutputStream outputStream, Object options) throws IOException {
        getXMLHelperDelegate().serialize(xmlDocument, outputStream, options);
    }

    public XMLDocument createDocument(DataObject dataObject, String rootElementURI, String rootElementName) {
        return getXMLHelperDelegate().createDocument(dataObject, rootElementURI, rootElementName);
    }

    public void setLoader(SDOClassLoader loader) {
        getXMLHelperDelegate().setLoader(loader);
    }

    public SDOClassLoader getLoader() {
        return getXMLHelperDelegate().getLoader();
    }

    public void setXmlContext(XMLContext xmlContext) {
        getXMLHelperDelegate().setXmlContext(xmlContext);
    }

    public XMLContext getXmlContext() {
        return getXMLHelperDelegate().getXmlContext();
    }

    public void addDescriptors(List descriptors) {
        getXMLHelperDelegate().addDescriptors(descriptors);
    }

    public void setTopLinkProject(Project toplinkProject) {
        getXMLHelperDelegate().setTopLinkProject(toplinkProject);
    }

    public void initializeDescriptor(XMLDescriptor descriptor) {
    	getXMLHelperDelegate().initializeDescriptor(descriptor);
    }
    
    public Project getTopLinkProject() {
        return getXMLHelperDelegate().getTopLinkProject();
    }

    public void setXmlMarshaller(XMLMarshaller xmlMarshaller) {
        getXMLHelperDelegate().setXmlMarshaller(xmlMarshaller);
    }

    public XMLMarshaller getXmlMarshaller() {
        return getXMLHelperDelegate().getXmlMarshaller();
    }

    public void setXmlUnmarshaller(XMLUnmarshaller xmlUnmarshaller) {
        getXMLHelperDelegate().setXmlUnmarshaller(xmlUnmarshaller);
    }

    public XMLUnmarshaller getXmlUnmarshaller() {
        return getXMLHelperDelegate().getXmlUnmarshaller();
    }

    public SDOXMLHelperDelegate getXMLHelperDelegate() {
        return (SDOXMLHelperDelegate) SDOHelperContext.getHelperContext().getXMLHelper();
    }

    public void reset() {
        getXMLHelperDelegate().reset();
    }

    public XMLConversionManager getXmlConversionManager() {
        return getXMLHelperDelegate().getXmlConversionManager();
    }

}
