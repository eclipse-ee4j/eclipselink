/*******************************************************************************
 * Copyright (c) 2012, 2013 Oracle and/or its affiliates. All rights reserved.
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

import java.util.Properties;
import java.util.Map.Entry;

import org.eclipse.persistence.oxm.CharacterEscapeHandler;
import org.eclipse.persistence.oxm.XMLMarshalListener;
import org.eclipse.persistence.oxm.attachment.XMLAttachmentMarshaller;
import org.eclipse.persistence.platform.xml.XMLTransformer;
import org.xml.sax.ErrorHandler;

/**
 *
 */
public abstract class Marshaller<
    CONTEXT extends Context,
    MEDIA_TYPE extends MediaType,
    NAMESPACE_PREFIX_MAPPER extends NamespacePrefixMapper> {

    private static String DEFAULT_INDENT = "   "; // default indent is three spaces;

    private CharacterEscapeHandler charEscapeHandler;
    protected CONTEXT context;
    private String encoding;
    private boolean equalUsingIdenity;
    private ErrorHandler errorHandler;
    private boolean formattedOutput;
    private String indentString;
    protected NAMESPACE_PREFIX_MAPPER mapper;
    private XMLMarshalListener marshalListener;
    protected Properties marshalProperties;

    public Marshaller(CONTEXT context) {
        this.context = context;
        this.encoding = Constants.DEFAULT_XML_ENCODING;
        this.equalUsingIdenity = true;
        this.formattedOutput = true;
        this.indentString = DEFAULT_INDENT;
    }

    /**
     * Copy constructor
     */
    protected Marshaller(Marshaller marshaller) {
        this.charEscapeHandler = marshaller.getCharacterEscapeHandler();
        this.context = (CONTEXT) marshaller.getContext();
        this.encoding = marshaller.getEncoding();
        this.equalUsingIdenity = marshaller.isEqualUsingIdenity();
        this.errorHandler = marshaller.getErrorHandler();
        this.formattedOutput = marshaller.isFormattedOutput();
        this.indentString = marshaller.getIndentString();
        this.mapper = (NAMESPACE_PREFIX_MAPPER) marshaller.getNamespacePrefixMapper();
        this.marshalListener = marshaller.getMarshalListener();
        if(marshaller.marshalProperties != null) {
            marshalProperties = new Properties();
            for(Entry entry : marshalProperties.entrySet()) {
                marshalProperties.put(entry.getKey(), entry.getValue());
            }
        }
    }

    public abstract XMLAttachmentMarshaller getAttachmentMarshaller();

    /**
     * Return this Marshaller's CharacterEscapeHandler.
     * @since 2.3.3
     */
    public CharacterEscapeHandler getCharacterEscapeHandler() {
        return this.charEscapeHandler;
    }

    /**
     * Return the instance of Context that was used to create this instance
     * of Marshaller.
     */
    public CONTEXT getContext() {
        return context;
    }

    /**
     * Get the encoding set on this Marshaller.  If the encoding has not been 
     * set the default UTF-8 will be used
     */
    public String getEncoding() {
        return encoding;
    }

    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }

    /**
     * Return the String that will be used to perform indenting in marshalled
     * documents.  Default is &quot;   &quot; (three spaces).
     */
    public String getIndentString() {
        return indentString;
    }

    public XMLMarshalListener getMarshalListener() {
        return this.marshalListener;
    }

    /**
     * Get the media type for this Marshaller.
     */
    public abstract MEDIA_TYPE getMediaType();

    /**
     * NamespacePrefixMapper that can be used during marshal (instead of those set in the project meta data)
     */
    public NAMESPACE_PREFIX_MAPPER getNamespacePrefixMapper() {
        return mapper;
    }

    /**
     * Return the property for a given key, if one exists.
     */
   public Object getProperty(Object key) {
       if(null == marshalProperties) {
           return null;
       }
       return marshalProperties.get(key);
   }
    
    /**
     * INTERNAL
     * @return the transformer instance for this Marshaller
     */
    public abstract XMLTransformer getTransformer();

    /**
     * INTERNAL
     */
    public boolean isEqualUsingIdenity() {
        return equalUsingIdenity;
    }

    /**
    * Returns if this Marshaller should format the output.  By default this is 
    * set to true and the marshalled output will be formatted.
    * @return if this Marshaller should format the output
    */
    public boolean isFormattedOutput() {
        return formattedOutput;
    }

    /**
     * Determine if the root not should be marshalled.  This property may 
     * ignored for media types that require a root node such as XML. 
     */
    public abstract boolean isIncludeRoot();

    /**
     * Property to determine if size 1 any collections should be treated as 
     * collections.
     */
    public abstract boolean isReduceAnyArrays();

    public abstract boolean isWrapperAsCollectionName();

    /**
     * Set this Marshaller's CharacterEscapeHandler.
     * @since 2.3.3
     */
    public void setCharacterEscapeHandler(CharacterEscapeHandler c) {
        this.charEscapeHandler = c;
    }

    /**
     * Set the encoding on this Marshaller.  If the encoding is not set the 
     * default UTF-8 will be used.
     * @param newEncoding the encoding to set on this Marshaller
     */
    public void setEncoding(String newEncoding) {
        this.encoding = newEncoding;
    }

    /**
     * INTERNAL
     */
    public void setEqualUsingIdenity(boolean equalUsingIdenity) {
        this.equalUsingIdenity = equalUsingIdenity;
    }

    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    /**
     * Set if this XMLMarshaller should format the XML
     * By default this is set to true and the XML marshalled will be formatted.
     * @param shouldFormat if this XMLMarshaller should format the XML
     */
    public void setFormattedOutput(boolean shouldFormat) {
        this.formattedOutput = shouldFormat;
    }

    /**
     * Set the String that will be used to perform indenting in marshalled 
     * documents.
     * @since 2.3.3
     */
    public void setIndentString(String s) {
        this.indentString = s;
    }

    public void setMarshalListener(XMLMarshalListener listener) {
        this.marshalListener = listener;
    }

    /**
     * NamespacePrefixMapper that can be used during marshal (instead of those 
     * set in the project meta data)
     */
    public void setNamespacePrefixMapper(NAMESPACE_PREFIX_MAPPER mapper) {
        this.mapper = mapper;
    }

}