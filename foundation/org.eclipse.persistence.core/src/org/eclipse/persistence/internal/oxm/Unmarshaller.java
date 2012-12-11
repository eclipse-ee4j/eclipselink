/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.5 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.internal.oxm;

import javax.xml.validation.Schema;

import org.eclipse.persistence.oxm.IDResolver;
import org.eclipse.persistence.oxm.MediaType;
import org.eclipse.persistence.oxm.XMLUnmarshallerHandler;
import org.eclipse.persistence.oxm.attachment.XMLAttachmentUnmarshaller;
import org.xml.sax.ErrorHandler;

public abstract class Unmarshaller<CONTEXT extends Context> {

    public abstract XMLAttachmentUnmarshaller getAttachmentUnmarshaller();

    /**
     * Value that will be used to prefix attributes.  
     * Ignored unmarshalling XML.   
     * @return
     * @since 2.4
     */
    public abstract String getAttributePrefix();

    /**
     * Get the ErrorHandler set on this XMLUnmarshaller
     * @return the ErrorHandler set on this XMLUnmarshaller
     */
    public abstract ErrorHandler getErrorHandler();

    /**
     * Return this Unmarshaller's custom IDResolver.
     * @see IDResolver
     * @since 2.3.3
     * @return the custom IDResolver, or null if one has not been specified.
     */
    public abstract IDResolver getIDResolver();

    /**
     * Get the MediaType for this xmlUnmarshaller.
     * See org.eclipse.persistence.oxm.MediaType for the media types supported by EclipseLink MOXy
     * If not set the default is MediaType.APPLICATION_XML
     * @since 2.4
     * @return MediaType
     */
    public abstract MediaType getMediaType();

    /**
     * Name of the NamespaceResolver to be used during unmarshal
     * Ignored unmarshalling XML.  
     * @since 2.4    
     */ 
    public abstract NamespaceResolver getNamespaceResolver();

    /**
     * Get the namespace separator used during unmarshal operations.
     * If mediaType is application/json '.' is the default
     * Ignored unmarshalling XML.   
     * @since 2.4
     */
    public abstract char getNamespaceSeparator();

    public abstract Schema getSchema();

    /**
     * INTERNAL:
     * This is the text handler during unmarshal operations.
     */
    public abstract StrBuffer getStringBuffer();

    /**
     * Get the class that will be instantiated to handled unmapped content
     * Class must implement the org.eclipse.persistence.oxm.unmapped.UnmappedContentHandler interface
     */
    public abstract Class getUnmappedContentHandlerClass();

    public abstract XMLUnmarshallerHandler getUnmarshallerHandler();

    /**
     * Name of the property to marshal/unmarshal as a wrapper on the text() mappings   
     * Ignored unmarshalling XML.  
     * @since 2.4    
     */ 
    public abstract String getValueWrapper();

    /**
     * Return the instance of XMLContext that was used to create this instance
     * of XMLUnmarshaller.
     */
    public abstract CONTEXT getXMLContext();

    /**
     * Return if this XMLUnmarshaller should try to automatically determine
     * the MediaType of the document (instead of using the MediaType set
     * by setMediaType)
     */
    public abstract boolean isAutoDetectMediaType();

    /**
     * Determine if the @XMLRootElement should be marshalled when present.  
     * Ignored unmarshalling XML.   
     * @return
     * @since 2.4
     */
    public abstract boolean isIncludeRoot();

    public abstract boolean isResultAlwaysXMLRoot();

}
