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

import javax.xml.transform.Result;

import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.oxm.NamespacePrefixMapper;
import org.eclipse.persistence.oxm.attachment.XMLAttachmentMarshaller;
import org.eclipse.persistence.platform.xml.XMLTransformer;

public abstract class Marshaller<
    CONTEXT extends Context,
    MEDIA_TYPE extends MediaType> {

    public abstract XMLAttachmentMarshaller getAttachmentMarshaller();
    
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
    public abstract NamespacePrefixMapper getNamespacePrefixMapper();

    /**
     * INTERNAL
     * @return the transformer instance for this marshaller
     */
    public abstract XMLTransformer getTransformer();

    /**
     * Return the instance of XMLContext that was used to create this instance
     * of XMLMarshaller.
     */
    public abstract CONTEXT getXMLContext();

    /**
     * Determine if the @XMLRootElement should be marshalled when present.  
     * Ignored marshalling XML.   
     */
    public abstract boolean isIncludeRoot();
    
    /**
     * PUBLIC:
     * Convert the given object to XML and update the given result with that XML Document
     * @param object the object to marshal
     * @param result the result to marshal the object to
     * @throws XMLMarshalException if an error occurred during marshalling
     */
     public abstract void marshal(Object object, Result result) throws XMLMarshalException;

}