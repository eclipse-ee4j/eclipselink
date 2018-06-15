/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//         dclarke/tware - Initial implementation
//      09-01-2014-2.6.0 Dmitry Kornilov
//        - Fields filtering (projection), application/schema+json media type handling
package org.eclipse.persistence.jpa.rs.util;

import java.beans.PropertyChangeListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.internal.dynamic.DynamicEntityImpl;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jpa.rs.PersistenceContext;
import org.eclipse.persistence.jpa.rs.exceptions.JPARSException;
import org.eclipse.persistence.jpa.rs.features.fieldsfiltering.FieldsFilter;
import org.eclipse.persistence.jpa.rs.resources.common.AbstractResource;
import org.eclipse.persistence.jpa.rs.util.list.ReportQueryResultList;
import org.eclipse.persistence.jpa.rs.util.xmladapters.LinkAdapter;

/**
 * Simple {@link StreamingOutput} implementation that uses the provided
 * {@link JAXBContext} to marshal the result when requested to either XML or
 * JSON based on the accept media provided.
 *
 * @author dclarke
 * @since EclipseLink 2.4.0
 */
public class StreamingOutputMarshaller implements StreamingOutput {
    private final PersistenceContext context;
    private final Object result;
    private final MediaType mediaType;
    private FieldsFilter filter;

    public StreamingOutputMarshaller(PersistenceContext context, Object result, MediaType acceptedType) {
        this.context = context;
        this.result = result;
        this.mediaType = acceptedType;
    }

    /**
     * This constructor is used for fields filtering. Only attributes included in fields parameter are marshalled.
     *
     * @param context persistence context.
     * @param result entity to process.
     * @param acceptedTypes accepted media types.
     * @param filter containing a list of fields to filter out from the response.
     */
    public StreamingOutputMarshaller(PersistenceContext context, Object result, List<MediaType> acceptedTypes, FieldsFilter filter) {
        this(context, result, acceptedTypes);
        this.filter = filter;
    }

    /**
     * Creates a new StreamingOutputMarshaller.
     *
     * @param context persistence context.
     * @param result entity to process.
     * @param acceptedTypes accepted media types.
     */
    public StreamingOutputMarshaller(PersistenceContext context, Object result, List<MediaType> acceptedTypes) {
        this(context, result, mediaType(acceptedTypes));
    }

    @Override
    public void write(OutputStream output) throws IOException, WebApplicationException {
        if (result instanceof byte[] && this.mediaType.equals(MediaType.APPLICATION_OCTET_STREAM_TYPE)) {
            output.write((byte[]) result);
            output.flush();
            output.close();
        } else if (result instanceof String) {
            OutputStreamWriter writer = new OutputStreamWriter(output, StandardCharsets.UTF_8);
            writer.write((String) result);
            writer.flush();
            writer.close();
        } else {
            if ((this.context != null && this.context.getJAXBContext() != null && this.result != null) &&
                    (this.mediaType.equals(MediaType.APPLICATION_JSON_TYPE) || this.mediaType.equals(MediaType.APPLICATION_XML_TYPE))) {
                try {
                    if (result instanceof ReportQueryResultList) {
                        if (mediaType == MediaType.APPLICATION_JSON_TYPE) {
                            // avoid outer QueryResultList class (outer grouping name) in JSON responses
                            context.marshallEntity(((ReportQueryResultList) result).getItems(), mediaType, output);
                        } else {
                            context.marshallEntity(result, mediaType, output);
                        }
                    } else {
                        if (filter != null) {
                            context.marshallEntity(result, filter, mediaType, output);
                        } else {
                            context.marshallEntity(result, mediaType, output);
                        }
                    }
                    return;
                } catch (Exception ex) {
                    JPARSLogger.exception(context.getSessionLog(), "jpars_caught_exception", new Object[] {}, ex);
                    throw JPARSException.exceptionOccurred(ex);
                }
            }

            if (this.mediaType.equals(MediaType.APPLICATION_OCTET_STREAM_TYPE)) {
                // could not marshall, try serializing
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(result);
                oos.flush();
                oos.close();
                output.write(baos.toByteArray());
            } else {
                if (context != null) {
                    JPARSLogger.error(context.getSessionLog(), "jpars_could_not_marshal_requested_result_to_requested_type", new Object[] { result });
                } else {
                    JPARSLogger.error("jpars_could_not_marshal_requested_result_to_requested_type", new Object[] { result });
                }
                throw new WebApplicationException();
            }
        }
    }

    /**
     * Identify the preferred {@link MediaType} from the list provided. This
     * will check for JSON string or {@link MediaType} first then XML.
     *
     * @param types
     *            List of {@link MediaType} values;
     * @return selected {@link MediaType}
     */
    public static MediaType mediaType(List<MediaType> types) {
        MediaType aMediaType;
        if (types != null) {
            for (MediaType type : types) {
                aMediaType = type;
                if (aMediaType.isCompatible(MediaType.APPLICATION_JSON_TYPE)) {
                    return MediaType.APPLICATION_JSON_TYPE;
                }
                if (aMediaType.isCompatible(MediaType.APPLICATION_XML_TYPE)) {
                    return MediaType.APPLICATION_XML_TYPE;
                }
                if (aMediaType.isCompatible(MediaType.APPLICATION_OCTET_STREAM_TYPE)) {
                    return MediaType.APPLICATION_OCTET_STREAM_TYPE;
                }
                if (aMediaType.isCompatible(AbstractResource.APPLICATION_SCHEMA_JSON_TYPE)) {
                    return AbstractResource.APPLICATION_SCHEMA_JSON_TYPE;
                }
            }
        }
        // An unsupported media type never comes to resource, no need to throw exception here.
        return MediaType.APPLICATION_JSON_TYPE;
    }

    public static Marshaller createMarshaller(PersistenceContext context, MediaType mediaType) throws JAXBException {
        Marshaller marshaller = context.getJAXBContext().createMarshaller();
        marshaller.setProperty(MarshallerProperties.MEDIA_TYPE, mediaType.toString());
        marshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
        marshaller.setAdapter(new LinkAdapter(context.getBaseURI().toString(), context));
        marshaller.setListener(new Marshaller.Listener() {
            @Override
            public void beforeMarshal(Object source) {
                if (source instanceof DynamicEntity) {
                    DynamicEntityImpl sourceImpl = (DynamicEntityImpl) source;
                    PropertyChangeListener listener = sourceImpl._persistence_getPropertyChangeListener();
                    sourceImpl._persistence_setPropertyChangeListener(null);
                    ((DynamicEntity) source).set("self", source);
                    sourceImpl._persistence_setPropertyChangeListener(listener);
                }
            }
        });
        return marshaller;
    }

    public static MediaType getResponseMediaType(HttpHeaders headers) {
        MediaType mediaType = MediaType.TEXT_PLAIN_TYPE;
        if (headers != null) {
            List<MediaType> accepts = headers.getAcceptableMediaTypes();
            if (accepts != null && accepts.size() > 0) {
                try {
                    mediaType = StreamingOutputMarshaller.mediaType(accepts);
                } catch (Exception ex) {
                    JPARSLogger.exception("Exception in getResponseMediaType", new Object[]{headers}, ex);
                }
            }
        }
        return mediaType;
    }
}
