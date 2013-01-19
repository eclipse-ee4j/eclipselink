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

import org.eclipse.persistence.oxm.CharacterEscapeHandler;
import org.eclipse.persistence.oxm.XMLMarshalListener;
import org.eclipse.persistence.oxm.attachment.XMLAttachmentMarshaller;
import org.eclipse.persistence.platform.xml.XMLTransformer;

public abstract class Marshaller<
    CONTEXT extends Context,
    MEDIA_TYPE extends MediaType,
    NAMESPACE_PREFIX_MAPPER extends NamespacePrefixMapper> {

    public abstract XMLAttachmentMarshaller getAttachmentMarshaller();

    /**
     * Value that will be used to prefix attributes.  
     * Ignored marshalling XML.
     */
    public abstract String getAttributePrefix();

    /**
     * Return this Marshaller's CharacterEscapeHandler.
     */
    public abstract CharacterEscapeHandler getCharacterEscapeHandler();

    /**
     * Get the encoding set on this Marshaller
     * If the encoding has not been set the default UTF-8 will be used
     */
    public abstract String getEncoding();

    /**
     * Return the String that will be used to perform indenting in marshalled documents.
     * Default is &quot;   &quot; (three spaces).
     */
    public abstract String getIndentString();

    public abstract XMLMarshalListener getMarshalListener();

    /**
     * Get the MediaType for this xmlMarshaller.
     * See org.eclipse.persistence.oxm.MediaType for the media types supported by EclipseLink MOXy
     * If not set the default is MediaType.APPLICATION_XML
     * @return MediaType
     */
    public abstract MEDIA_TYPE getMediaType();

    /**
     * NamespacePrefixMapper that can be used during marshal (instead of those set in the project meta data)
     */
    public abstract NAMESPACE_PREFIX_MAPPER getNamespacePrefixMapper();

    /**
     * Return the property for a given key, if one exists.
     */
   public abstract Object getProperty(Object key);
    
    /**
     * INTERNAL
     * @return the transformer instance for this marshaller
     */
    public abstract XMLTransformer getTransformer();

    /**
     * Name of the property to marshal/unmarshal as a wrapper on the text() mappings
     */
    public abstract String getValueWrapper();

    /**
     * Return the instance of XMLContext that was used to create this instance
     * of XMLMarshaller.
     */
    public abstract CONTEXT getXMLContext();

    /**
     * Get this Marshaller's XML Header.
     */
    public abstract String getXmlHeader();

    /**
     * INTERNAL
     */
    public abstract boolean isEqualUsingIdenity();

    /**
     * Determine if the @XMLRootElement should be marshalled when present.  
     * Ignored marshalling XML.   
     */
    public abstract boolean isIncludeRoot();

    /**
     * Property to determine if size 1 any collections should be treated as collections
     * Ignored marshalling XML.
     */
    public abstract boolean isReduceAnyArrays();

    /**
     * Get the namespace separator used during marshal operations.
     * If mediaType is application/json '.' is the default
     * Ignored marshalling XML.
     */
    public abstract char getNamespaceSeparator();

    /**
     * Name of the property to determine if empty collections should be marshalled as []   
     * Ignored marshalling XML.
     */
    public abstract boolean isMarshalEmptyCollections();

}