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
package org.eclipse.persistence.sdo.helper;

import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.XMLDocument;
import commonj.sdo.helper.XMLHelper;
import java.io.IOException;
import java.util.List;
import java.util.TimeZone;

import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.sessions.Project;
import org.xml.sax.InputSource;

/**
 * <p><b>Purpose</b>: Helper to transform XML documents into DataObects and DataObjects into XML documents.
 * <p><b>Responsibilities</b>:<ul>
 * <li> Load methods create commonj.sdo.XMLDocument objects from XML (unmarshal) 
 * <li> Save methods create XML from commonj.sdo.XMLDocument and commonj.sdo.DataObject objects (marshal) 
 * </ul>
 */
public interface SDOXMLHelper extends XMLHelper {

    /**
     * The specified TimeZone will be used for all String to date object
     * conversions.  By default the TimeZone from the JVM is used.
     */   
    public void setTimeZone(TimeZone timeZone);
    
    /**
     * By setting this flag to true the marshalled date objects marshalled to 
     * the XML schema types time and dateTime will be qualified by a time zone.  
     * By default time information is not time zone qualified.
     */
    public void setTimeZoneQualified(boolean timeZoneQualified); 

	/**
     * INTERNAL:
	 * 
	 * @param inputSource
	 * @param locationURI
	 * @param options
	 * @return
	 * @throws IOException
	 */
    public XMLDocument load(InputSource inputSource, String locationURI, Object options) throws IOException;

    /**
     * INTERNAL:
     * 
     * @param loader
     */
    public void setLoader(SDOClassLoader loader);

    /**
     * INTERNAL:
     * 
     * @return
     */
    public SDOClassLoader getLoader();

    /**
     * INTERNAL:
     * 
     * @param xmlContext
     */
    public void setXmlContext(XMLContext xmlContext);

    /**
     * INTERNAL:
     * 
     * @return
     */
    public XMLContext getXmlContext();    

    /**
     * INTERNAL:
     * 
     * @param descriptors
     */
    public void addDescriptors(List descriptors);

    /**
     * INTERNAL:
     * 
     * @param toplinkProject
     */
    public void setTopLinkProject(Project toplinkProject);

    /**
     * INTERNAL:
     * 
     * @return
     */
    public Project getTopLinkProject();

    /**
     * INTERNAL:
     * 
     * @param xmlMarshaller
     */
    public void setXmlMarshaller(XMLMarshaller xmlMarshaller);

    /**
     * INTERNAL:
     * 
     * @return
     */
    public XMLMarshaller getXmlMarshaller();

    /**
     * INTERNAL:
     * 
     * @param xmlUnmarshaller
     */
    public void setXmlUnmarshaller(XMLUnmarshaller xmlUnmarshaller);

    /**
     * INTERNAL:
     * 
     * @return
     */
    public XMLUnmarshaller getXmlUnmarshaller();

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

    public void initializeDescriptor(XMLDescriptor descriptor);

    /**
     * INTERNAL:
     * 
     */
    public XMLConversionManager getXmlConversionManager();
}
