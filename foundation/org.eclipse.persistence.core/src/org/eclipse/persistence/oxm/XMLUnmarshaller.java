/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.oxm;

import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.persistence.core.sessions.CoreSession;
import org.eclipse.persistence.internal.oxm.record.UnmarshalRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.oxm.schema.XMLSchemaReference;

/**
 * <p>Class used to unmarshal XML {@literal &} JSON to objects.
 *
 * <p>Create an XMLUnmarshaller from an XMLContext.<br>
 *  <em>Code Sample</em><br>
 *  <code>
 *  XMLContext context = new XMLContext("mySessionName");<br>
 *  XMLUnmarshaller unmarshaller = context.createUnmarshaller();<br>
 *  </code>
 *
 * <p>XML can be unmarshalled from the following inputs:<ul>
 * <li>java.io.File</li>
 * <li>java.io.InputStream</li>
 * <li>java.io.Reader</li>
 * <li>java.net.URL</li>
 * <li>javax.xml.transform.Source</li>
 * <li>org.w3c.dom.Node</li>
 * <li>org.xml.sax.InputSource</li>
 * </ul>
 *
 * <p>XML that can be unmarshalled is XML which has a root tag that corresponds
 * to a default root element on an XMLDescriptor in the TopLink project associated
 * with the XMLContext.
 *
 * @see org.eclipse.persistence.oxm.XMLContext
 */
public class XMLUnmarshaller extends org.eclipse.persistence.internal.oxm.XMLUnmarshaller<AbstractSession, XMLContext, XMLDescriptor, IDResolver, MediaType, XMLRoot, XMLUnmarshallerHandler, XMLUnmarshalListener> implements Cloneable {

    public static final int NONVALIDATING = org.eclipse.persistence.internal.oxm.XMLUnmarshaller.NONVALIDATING;
    public static final int SCHEMA_VALIDATION = org.eclipse.persistence.internal.oxm.XMLUnmarshaller.SCHEMA_VALIDATION;
    public static final int DTD_VALIDATION = org.eclipse.persistence.internal.oxm.XMLUnmarshaller.DTD_VALIDATION;

    protected XMLUnmarshaller(XMLContext xmlContext) {
        this(xmlContext, null);
        setMediaType(MediaType.APPLICATION_XML);
    }

    protected XMLUnmarshaller(XMLContext xmlContext, Map<String, Boolean> parserFeatures) {
        super(xmlContext, parserFeatures);
        setMediaType(MediaType.APPLICATION_XML);
    }

    // Copy Constructor
    protected XMLUnmarshaller(XMLUnmarshaller xmlUnmarshaller) {
        super(xmlUnmarshaller);
        setValidationMode(xmlUnmarshaller.getValidationMode());
    }

    @Override
    public XMLUnmarshaller clone() {
        return new XMLUnmarshaller(this);
    }

    /**
     * INTERNAL
     * @since 2.5.0
     */
    @Override
    public XMLRoot createRoot() {
        return new XMLRoot();
    }

    /**
     * INTERNAL
     * @since 2.5.0
     */
    @Override
    public UnmarshalRecord createUnmarshalRecord(XMLDescriptor xmlDescriptor, AbstractSession session) {
        org.eclipse.persistence.oxm.record.UnmarshalRecord wrapper = (org.eclipse.persistence.oxm.record.UnmarshalRecord) xmlDescriptor.getObjectBuilder().createRecord((AbstractSession) session);
        return wrapper.getUnmarshalRecord();
    }

    /**
     * Get the MediaType for this xmlUnmarshaller.
     * See org.eclipse.persistence.oxm.MediaType for the media types supported by EclipseLink MOXy
     * If not set the default is MediaType.APPLICATION_XML
     * @since 2.4
     * @return MediaType
     */
    @Override
    public MediaType getMediaType(){
        return super.getMediaType();
    }

    @Override
    public XMLUnmarshalListener getUnmarshalListener() {
        return super.getUnmarshalListener();
    }

    @Override
    public void setUnmarshalListener(XMLUnmarshalListener listener) {
        super.setUnmarshalListener(listener);
    }

    @Override
    public XMLUnmarshallerHandler getUnmarshallerHandler() {
        if (null == xmlUnmarshallerHandler) {
            xmlUnmarshallerHandler = new XMLUnmarshallerHandler(this);
        }
        return xmlUnmarshallerHandler;
    }

    /**
     * Return the instance of XMLContext that was used to create this instance
     * of XMLUnmarshaller.
     */
    @Override
    public XMLContext getXMLContext() {
        return getContext();
    }

    /**
     * Set the MediaType for this xmlUnmarshaller.
     * See org.eclipse.persistence.oxm.MediaType for the media types supported by EclipseLink MOXy
     * @since 2.4
     * @param mediaType
     */
    public void setMediaType(MediaType mediaType) {
        super.setMediaType(mediaType);
    }

    /**
    * Set the validation mode.
    * This method sets the validation mode of the parser to one of the 3 types:
    * NONVALIDATING, DTD_VALIDATION and SCHEMA_VALIDATION.
    * By default, the unmarshaller is set to be NONVALIDATING
    * @param validationMode sets the type of the validation mode to be used
    **/
    public void setValidationMode(int validationMode) {
        if (validationMode == SCHEMA_VALIDATION) {
            initializeSchemas();
        }
        platformUnmarshaller.setValidationMode(validationMode);
    }

    /**
     * Set the XMLContext used by this instance of XMLUnmarshaller.
     */
    @Override
    public void setXMLContext(XMLContext value) {
        context =  value;
    }

    /**
     * Return this Unmarshaller's custom IDResolver.
     * @see IDResolver
     * @since 2.3.3
     * @return the custom IDResolver, or null if one has not been specified.
     */
    @Override
    public IDResolver getIDResolver() {
        return super.getIDResolver();
    }

    /**
     * Set this Unmarshaller's custom IDResolver.
     * @see IDResolver
     * @since 2.3.3
     */
    @Override
    public void setIDResolver(IDResolver idResolver) {
        super.setIDResolver(idResolver);
    }

    protected void initialize(Map<String, Boolean> parserFeatures) {
        super.initialize(parserFeatures);

        // Waiting on XDK to fix bug #3697940 to enable this code
        //initializeSchemas();
        setValidationMode(NONVALIDATING);
    }

    private void initializeSchemas() {
        if (!schemasAreInitialized) {
            HashSet schemas = new HashSet();
            Iterator xmlDescriptors;
            XMLDescriptor xmlDescriptor;
            XMLSchemaReference xmlSchemaReference;
            int numberOfSessions = ((XMLContext) context).getSessions().size();
            for (int x = 0; x < numberOfSessions; x++) {
                xmlDescriptors = ((CoreSession)context.getSessions().get(x)).getDescriptors().values().iterator();
                URL schemaURL;
                while (xmlDescriptors.hasNext()) {
                    xmlDescriptor = (XMLDescriptor)xmlDescriptors.next();
                    xmlSchemaReference = xmlDescriptor.getSchemaReference();
                    if (null != xmlSchemaReference) {
                        schemaURL = xmlSchemaReference.getURL();
                        if (null != schemaURL) {
                            schemas.add(schemaURL.toString());
                        }
                    }
                }
            }
            schemas.remove(null);
            platformUnmarshaller.setSchemas(schemas.toArray());
            schemasAreInitialized = true;
        }
    }

}
