/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * 		dclarke/tware - initial 
 *      tware
 ******************************************************************************/
package org.eclipse.persistence.jpa.rs.util;

import java.beans.PropertyChangeListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.internal.dynamic.DynamicEntityImpl;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jpa.rs.PersistenceContext;

/**
 * Simple {@link StreamingOutput} implementation that uses the provided
 * {@link JAXBContext} to marshal the result when requested to either XML or
 * JSON based on the accept media provided.
 * 
 * @author dclarke
 * @since EclipseLink 2.4.0
 */
public class StreamingOutputMarshaller implements StreamingOutput {
    private PersistenceContext context;
    private Object result;
    private MediaType mediaType;

    public StreamingOutputMarshaller(PersistenceContext context, Object result, MediaType acceptedType) {
        this.context = context;
        this.result = result;
        this.mediaType = acceptedType;
    }

    public StreamingOutputMarshaller(PersistenceContext context, Object result, List<MediaType> acceptedTypes) {
        this(context, result, mediaType(acceptedTypes));
    }

    public void write(OutputStream output) throws IOException, WebApplicationException {
        if (result instanceof byte[] && this.mediaType.equals(MediaType.APPLICATION_OCTET_STREAM_TYPE)){
            output.write((byte[])result);
        } else if (result instanceof String){
            OutputStreamWriter writer = new OutputStreamWriter(output);
            writer.write((String)result);
            writer.flush();
            writer.close();
        } else {
            if (this.context != null && this.context.getJAXBContext() != null && this.result != null && (this.mediaType.equals(MediaType.APPLICATION_JSON_TYPE) || this.mediaType.equals(MediaType.APPLICATION_XML_TYPE))) {
                try {
                    context.marshallEntity(result, mediaType, output);
                    return;
                } catch (JAXBException e) {
                    JAXBException initialException = e;
                    if (this.result instanceof List){
                        List<JAXBElement<Object>> returnList = new ArrayList<JAXBElement<Object>>(((List)this.result).size());
                        List resultAsList = (List)this.result;
                        try{
                            for (Object object: resultAsList){
                                JAXBElement jaxbResult = new JAXBElement(new QName("result"), object.getClass(), object);
                                returnList.add(jaxbResult);
                            }
    
                            context.marshallEntity(returnList, mediaType, output);
                            return;
                        } catch (JAXBException ex){
                            JPARSLogger.fine("jaxb_exception_while_marshalling", new Object[]{ex.toString()});
                        }
                    }
                    JPARSLogger.fine("jpars_could_not_marshal_serializing", new Object[]{initialException});
                }
            }
            if (this.mediaType.equals(MediaType.APPLICATION_OCTET_STREAM_TYPE)){
                // could not marshall, try serializing
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(result);
                oos.flush();
                oos.close();
                output.write(baos.toByteArray());
            } else {
                JPARSLogger.fine("jpars_could_marshal_requested_result_to_requested_type", new Object[]{result});
            }
        }
    }
    
    /**
     * Identify the preferred {@link MediaType} from the list provided. This
     * will check for JSON string or {@link MediaType} first then XML.
     * 
     * @param types
     *            List of {@link String} or {@link MediaType} values;
     * @return selected {@link MediaType}
     * @throws WebApplicationException
     *             with Status.UNSUPPORTED_MEDIA_TYPE if neither the JSON or XML
     *             values are found.
     */
    public static MediaType mediaType(List<?> types) {
        JPARSLogger.fine("jpars_requested_type", new Object[]{types});
        for (int i=0;i<types.size();i++){
            if (isCompatible(types.get(i), new MediaType[]{MediaType.APPLICATION_JSON_TYPE})){
                return MediaType.APPLICATION_JSON_TYPE;
            }
            if (isCompatible(types.get(i), new MediaType[]{MediaType.APPLICATION_XML_TYPE})){
                return MediaType.APPLICATION_XML_TYPE;
            }
            if (isCompatible(types.get(i), new MediaType[]{MediaType.APPLICATION_OCTET_STREAM_TYPE})){
                return MediaType.APPLICATION_OCTET_STREAM_TYPE;
            }
        }
        throw new WebApplicationException(Status.UNSUPPORTED_MEDIA_TYPE);
    }
    
    protected static boolean isCompatible(Object type, MediaType[] types){
        if (type instanceof String){
            for (MediaType comparisonType:types){
                if (comparisonType.toString().equals((String)type)){
                    return true;
                }
            }
        } else {
            for (MediaType comparisonType:types){
                if (comparisonType.isCompatible((MediaType)type)){
                    return true;
                }
            }
        }
        return false;
    }
    
    private static boolean contains(List<?> types, MediaType type) {
        for (Object mt : types) {
            if (mt instanceof String) {
                if (((String) mt).contains(type.toString())) {
                    return true;
                }
            } else if (((MediaType) mt).isCompatible(type)) {
                return true;
            }
        }
        return false;
    }
    
    public static Marshaller createMarshaller(PersistenceContext context, MediaType mediaType) throws JAXBException{
        Marshaller marshaller = context.getJAXBContext().createMarshaller();
        marshaller.setProperty(MarshallerProperties.MEDIA_TYPE, mediaType.toString());
        marshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
        marshaller.setAdapter(new LinkAdapter(context.getBaseURI().toString(), context));
        marshaller.setListener(new Marshaller.Listener() {
            @Override
            public void beforeMarshal(Object source) {   
                if (source instanceof DynamicEntity){
                    DynamicEntityImpl sourceImpl = (DynamicEntityImpl)source;
                    PropertyChangeListener listener = sourceImpl._persistence_getPropertyChangeListener();
                    sourceImpl._persistence_setPropertyChangeListener(null);
                    ((DynamicEntity)source).set("self", source);
                    sourceImpl._persistence_setPropertyChangeListener(listener);
                }
            }
        });
        return marshaller;
    }
}
