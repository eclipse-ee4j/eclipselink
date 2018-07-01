/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.5 - initial implementation
//     Marcel Valovy - 2.6.0 - added case insensitive unmarshalling property
package org.eclipse.persistence.internal.oxm;

import javax.xml.validation.Schema;

import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.oxm.mappings.Descriptor;
import org.eclipse.persistence.internal.oxm.record.UnmarshalRecord;
import org.eclipse.persistence.oxm.attachment.XMLAttachmentUnmarshaller;
import org.xml.sax.ErrorHandler;

public abstract class Unmarshaller<
    ABSTRACT_SESSION extends CoreAbstractSession,
    CONTEXT extends Context,
    DESCRIPTOR extends Descriptor,
    ID_RESOLVER extends IDResolver,
    MEDIA_TYPE extends MediaType,
    ROOT extends Root,
    UNMARSHALLER_HANDLER extends UnmarshallerHandler,
    UNMARSHALLER_LISTENER extends Unmarshaller.Listener> {

    protected CONTEXT context;
    private UNMARSHALLER_LISTENER unmarshalListener;

    public Unmarshaller(CONTEXT context) {
        this.context = context;
    }

    /**
     * Copy constructor
     */
    protected Unmarshaller(Unmarshaller unmarshaller) {
        this.context = (CONTEXT) unmarshaller.getContext();
        this.unmarshalListener = (UNMARSHALLER_LISTENER) unmarshaller.getUnmarshalListener();
    }

    /**
     * INTERNAL
     */
    public abstract ROOT createRoot();

    /**
     * INTERNAL
     */
    public abstract UnmarshalRecord createRootUnmarshalRecord(Class clazz);

    /**
     * INTERNAL
     */
    public abstract UnmarshalRecord createUnmarshalRecord(DESCRIPTOR descriptor, ABSTRACT_SESSION session);

    public abstract XMLAttachmentUnmarshaller getAttachmentUnmarshaller();

    /**
     * Value that will be used to prefix attributes.
     * Ignored unmarshalling XML.
     * @return
     */
    public abstract String getAttributePrefix();

    /**
     * Return the instance of XMLContext that was used to create this instance
     * of Unmarshaller.
     */
    public CONTEXT getContext() {
        return context;
    }

    /**
     * Get the ErrorHandler set on this Unmarshaller
     * @return the ErrorHandler set on this Unmarshaller
     */
    public abstract ErrorHandler getErrorHandler();

    /**
     * Return this Unmarshaller's custom IDResolver.
     * @see IDResolver
     * @return the custom IDResolver, or null if one has not been specified.
     */
    public abstract ID_RESOLVER getIDResolver();

    /**
     * Get the MediaType for this unmarshaller.
     * See org.eclipse.persistence.oxm.MediaType for the media types supported by EclipseLink MOXy
     * If not set the default is MediaType.APPLICATION_XML
     * @return MediaType
     */
    public abstract MEDIA_TYPE getMediaType();

    /**
     * Name of the NamespaceResolver to be used during unmarshal
     * Ignored unmarshalling XML.
     */
    public abstract NamespaceResolver getNamespaceResolver();

    /**
     * Get the namespace separator used during unmarshal operations.
     * If mediaType is application/json '.' is the default
     * Ignored unmarshalling XML.
     */
    public abstract char getNamespaceSeparator();

    /**
     * Return the property for a given key, if one exists.
     *
     * @parm key
     * @return
     */
    public abstract Object getProperty(Object key);

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

    /**
     * INTERNAL:
     * Returns the AttributeGroup or the name of the AttributeGroup to be used to
     * unmarshal.
     */
    public abstract Object getUnmarshalAttributeGroup();

    public abstract UNMARSHALLER_HANDLER getUnmarshallerHandler();

    public UNMARSHALLER_LISTENER getUnmarshalListener() {
        return unmarshalListener;
    }

    /**
     * Name of the property to marshal/unmarshal as a wrapper on the text() mappings
     * Ignored unmarshalling XML.
     */
    public abstract String getValueWrapper();

    /**
     * INTERNAL
     * @return true if the media type is application/json, else false.
     * @since EclipseLink 2.6.0
     */
    public abstract boolean isApplicationJSON();

    /**
     * INTERNAL
     * @return true if the media type is application/xml, else false.
     * @since EclipseLink 2.6.0
     */
    public abstract boolean isApplicationXML();

    /**
     * Return if this Unmarshaller should try to automatically determine
     * the MediaType of the document (instead of using the MediaType set
     * by setMediaType)
     */
    public abstract boolean isAutoDetectMediaType();

    /**
     * Return whether this Unmarshaller should perform case insensitive unmarshalling.
     */
    public abstract boolean isCaseInsensitive();

    /**
     * Determine if the @XMLRootElement should be marshalled when present.
     * Ignored unmarshalling XML.
     * @return
     */
    public abstract boolean isIncludeRoot();

    public abstract boolean isResultAlwaysXMLRoot();

    public abstract boolean isWrapperAsCollectionName();

    public abstract void setIDResolver(ID_RESOLVER idResolver);

    public abstract boolean shouldWarnOnUnmappedElement();

    public void setUnmarshalListener(UNMARSHALLER_LISTENER unmarshalListener) {
        this.unmarshalListener = unmarshalListener;
    }

    /**
     * <p>An implementation of UnmarshalListener can be set on an Unmarshaller to
     * provide additional behaviour during unmarshal operations.</p>
     */
    public interface Listener {

        /**
         * Event that will be called after objects are unmarshalled.
         *
         * @param target the object that was unmarshalled.
         * @param parent the owning object of the object that was unmarshalled. This may be null.
         */
        public void afterUnmarshal(Object target, Object parent);


        /**
         * Event that will be called before objects are unmarshalled.
         *
         * @param target A newly created instance of the object to be unmarshalled.
         * @param parent the owning object of the object that will be unmarshalled. This may be null.
         */
        public void beforeUnmarshal(Object target, Object parent);

    }

    /**
     * Returns json type configuration.
     *
     * @return json type configuration
     * @since 2.6.0
     */
    public abstract JsonTypeConfiguration getJsonTypeConfiguration();

}
