/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.sdo.helper;

import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.XMLDocument;
import commonj.sdo.helper.XMLHelper;
import java.io.IOException;
import java.io.OutputStream;
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

    void serialize(XMLDocument xmlDocument, OutputStream outputStream, Object options) throws IOException;

    /**
     * The specified TimeZone will be used for all String to date object
     * conversions.  By default the TimeZone from the JVM is used.
     */
    void setTimeZone(TimeZone timeZone);

    /**
     * By setting this flag to true the marshalled date objects marshalled to
     * the XML schema types time and dateTime will be qualified by a time zone.
     * By default time information is not time zone qualified.
     */
    void setTimeZoneQualified(boolean timeZoneQualified);

    /**
     * INTERNAL:
     *
     * @param inputSource
     * @param locationURI
     * @param options
     * @return
     * @throws IOException
     */
    XMLDocument load(InputSource inputSource, String locationURI, Object options) throws IOException;

    /**
     * INTERNAL:
     *
     * @param loader
     */
    void setLoader(SDOClassLoader loader);

    /**
     * INTERNAL:
     *
     * @return
     */
    SDOClassLoader getLoader();

    /**
     * INTERNAL:
     *
     * @param xmlContext
     */
    void setXmlContext(XMLContext xmlContext);

    /**
     * INTERNAL:
     *
     * @return
     */
    XMLContext getXmlContext();

    /**
     * INTERNAL:
     *
     * @param descriptors
     */
    void addDescriptors(List descriptors);

    /**
     * INTERNAL:
     *
     * @param toplinkProject
     */
    void setTopLinkProject(Project toplinkProject);

    /**
     * INTERNAL:
     *
     * @return
     */
    Project getTopLinkProject();

    /**
     * INTERNAL:
     *
     * @param xmlMarshaller
     */
    void setXmlMarshaller(XMLMarshaller xmlMarshaller);

    /**
     * INTERNAL:
     *
     * @return
     */
    XMLMarshaller getXmlMarshaller();

    /**
     * INTERNAL:
     *
     * @param xmlUnmarshaller
     */
    void setXmlUnmarshaller(XMLUnmarshaller xmlUnmarshaller);

    /**
     * INTERNAL:
     *
     * @return
     */
    XMLUnmarshaller getXmlUnmarshaller();

    /**
     * INTERNAL:
     * Return the helperContext that this instance is associated with.
     *
     * @return
     */
    HelperContext getHelperContext();

    /**
     * INTERNAL:
     * Set the helperContext that this instance is associated with.
     *
     * @param helperContext
     */
    void setHelperContext(HelperContext helperContext);

    /**
     * INTERNAL:
     *
     */
    void reset();

    void initializeDescriptor(XMLDescriptor descriptor);

    /**
     * INTERNAL:
     *
     */
    XMLConversionManager getXmlConversionManager();
}
