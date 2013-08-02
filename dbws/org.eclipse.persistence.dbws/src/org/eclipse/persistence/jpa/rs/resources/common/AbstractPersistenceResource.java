/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 *
 ******************************************************************************/
package org.eclipse.persistence.jpa.rs.resources.common;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.internal.jpa.rs.metadata.model.Link;
import org.eclipse.persistence.internal.jpa.rs.metadata.model.Parameter;
import org.eclipse.persistence.internal.jpa.rs.metadata.model.SessionBeanCall;
import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.jpa.rs.PersistenceContext;
import org.eclipse.persistence.jpa.rs.exceptions.JPARSException;
import org.eclipse.persistence.jpa.rs.util.JPARSLogger;
import org.eclipse.persistence.jpa.rs.util.StreamingOutputMarshaller;
import org.eclipse.persistence.jpa.rs.util.list.LinkList;

/**
 * @author gonural
 *
 */
public abstract class AbstractPersistenceResource extends AbstractResource {

    protected Response getContexts(String version, HttpHeaders headers, URI baseURI) throws JAXBException {
        try {
            if (!isValidVersion(version)) {
                JPARSLogger.error("unsupported_service_version_in_the_request", new Object[] { version });
                JPARSException.invalidServiceVersion(version);
            }

            Set<String> contexts = getPersistenceFactory().getPersistenceContextNames();
            Iterator<String> contextIterator = contexts.iterator();
            List<Link> links = new ArrayList<Link>();
            String mediaType = StreamingOutputMarshaller.mediaType(headers.getAcceptableMediaTypes()).toString();
            while (contextIterator.hasNext()) {
                String context = contextIterator.next();
                if (version != null) {
                    links.add(new Link(context, mediaType, baseURI + version + "/" + context + "/metadata"));
                } else {
                    links.add(new Link(context, mediaType, baseURI + context + "/metadata"));
                }
            }
            LinkList linkList = new LinkList();
            linkList.setList(links);
            String result = null;
            if (mediaType.equals(MediaType.APPLICATION_JSON)) {
                result = marshallMetadata(linkList.getList(), mediaType);
            } else {
                result = marshallMetadata(linkList, mediaType);
            }
            return Response.ok(new StreamingOutputMarshaller(null, result, headers.getAcceptableMediaTypes())).build();
        } catch (Exception ex) {
            throw JPARSException.exceptionOccurred(ex);
        }
    }

    @SuppressWarnings("rawtypes")
    protected Response callSessionBeanInternal(String version, HttpHeaders headers, UriInfo uriInfo, InputStream is) throws JAXBException, ClassNotFoundException, NamingException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        try {
            if (!isValidVersion(version)) {
                JPARSLogger.error("unsupported_service_version_in_the_request", new Object[] { version });
                JPARSException.invalidServiceVersion(version);
            }

            SessionBeanCall call = null;
            call = unmarshallSessionBeanCall(is);

            String jndiName = call.getJndiName();
            javax.naming.Context ctx = new InitialContext();
            Object ans = ctx.lookup(jndiName);
            if (ans == null) {
                JPARSLogger.error("jpars_could_not_find_session_bean", new Object[] { jndiName });
                throw JPARSException.sessionBeanCouldNotBeFound(jndiName);
            }

            PersistenceContext context = null;
            if (call.getContext() != null) {
                context = getPersistenceFactory().get(call.getContext(), uriInfo.getBaseUri(), version, null);
                if (context == null) {
                    JPARSLogger.error("jpars_could_not_find_persistence_context", new Object[] { call.getContext() });
                    throw JPARSException.persistenceContextCouldNotBeBootstrapped(call.getContext());
                }
            }

            Class[] parameters = new Class[call.getParameters().size()];
            Object[] args = new Object[call.getParameters().size()];
            int i = 0;
            for (Parameter param : call.getParameters()) {
                Class parameterClass = null;
                Object parameterValue = null;
                if (context != null) {
                    parameterClass = context.getClass(param.getTypeName());
                }
                if (parameterClass != null) {
                    parameterValue = context.unmarshalEntity(param.getTypeName(), headers.getMediaType(), is);
                } else {
                    parameterClass = Thread.currentThread().getContextClassLoader().loadClass(param.getTypeName());
                    parameterValue = ConversionManager.getDefaultManager().convertObject(param.getValue(), parameterClass);
                }
                parameters[i] = parameterClass;
                args[i] = parameterValue;
                i++;
            }
            Method method = ans.getClass().getMethod(call.getMethodName(), parameters);
            Object returnValue = method.invoke(ans, args);
            return Response.ok(new StreamingOutputMarshaller(null, returnValue, headers.getAcceptableMediaTypes())).build();
        } catch (Exception ex) {
            throw JPARSException.exceptionOccurred(ex);
        }
    }

    protected SessionBeanCall unmarshallSessionBeanCall(InputStream data) throws JAXBException {
        Class<?>[] jaxbClasses = new Class[] { SessionBeanCall.class };
        JAXBContext context = (JAXBContext) JAXBContextFactory.createContext(jaxbClasses, null);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        unmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, Boolean.FALSE);
        unmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_JSON);
        StreamSource ss = new StreamSource(data);
        return unmarshaller.unmarshal(ss, SessionBeanCall.class).getValue();
    }
}
