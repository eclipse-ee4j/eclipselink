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
//      gonural - Initial implementation
//      Dmitry Kornilov - JPARS 2.0 related changes
package org.eclipse.persistence.jpa.rs.resources.common;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
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
import org.eclipse.persistence.internal.jpa.rs.metadata.model.v2.ContextsCatalog;
import org.eclipse.persistence.internal.jpa.rs.metadata.model.v2.Resource;
import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.jpa.rs.PersistenceContext;
import org.eclipse.persistence.jpa.rs.exceptions.JPARSException;
import org.eclipse.persistence.jpa.rs.features.ItemLinksBuilder;
import org.eclipse.persistence.jpa.rs.features.ServiceVersion;
import org.eclipse.persistence.jpa.rs.util.HrefHelper;
import org.eclipse.persistence.jpa.rs.util.JPARSLogger;
import org.eclipse.persistence.jpa.rs.util.StreamingOutputMarshaller;
import org.eclipse.persistence.jpa.rs.util.list.LinkList;

/**
 * Base class for persistent unit resources.
 *
 * @author gonural
 */
public abstract class AbstractPersistenceResource extends AbstractResource {
    private static final String CLASS_NAME = AbstractPersistenceResource.class.getName();

    /**
     * Produces a response containing a list of available persistence contexts.
     * Returns different responses depending version.
     *
     * @param version the service version (null, "v1.0", "v2.0", "latest")
     * @param headers the HTTP headers
     * @param uriInfo the URL
     * @return response containing a list of persistence contexts.
     */
    protected Response getContextsInternal(String version, HttpHeaders headers, UriInfo uriInfo) {
        JPARSLogger.entering(CLASS_NAME, "getContextsInternal", new Object[] { "GET", version, uriInfo.getRequestUri().toASCIIString() });
        if (!isValidVersion(version)) {
            JPARSLogger.error("unsupported_service_version_in_the_request", new Object[] { version });
            throw JPARSException.invalidServiceVersion(version);
        }

        if (ServiceVersion.fromCode(version).compareTo(ServiceVersion.VERSION_2_0) >= 0) {
            return getContextsV2(version, headers, uriInfo);
        } else {
            return getContextsV1(version, headers, uriInfo);
        }
    }

    @SuppressWarnings("rawtypes")
    protected Response callSessionBeanInternal(String version, HttpHeaders headers, UriInfo uriInfo, InputStream is) {
        JPARSLogger.entering(CLASS_NAME, "callSessionBeanInternal", new Object[] { "POST", headers.getMediaType(), version, uriInfo.getRequestUri().toASCIIString() });
        try {
            if (!isValidVersion(version)) {
                JPARSLogger.error("unsupported_service_version_in_the_request", new Object[] { version });
                throw JPARSException.invalidServiceVersion(version);
            }

            SessionBeanCall call = unmarshallSessionBeanCall(is);

            String jndiName = call.getJndiName();
            if (!isValid(jndiName)) {
                JPARSLogger.error("jpars_invalid_jndi_name", new Object[] { jndiName });
                throw JPARSException.jndiNamePassedIsInvalid(jndiName);
            }
            
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
                Object parameterValue;
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
        } catch (JAXBException | NamingException | ReflectiveOperationException | RuntimeException e) {
            JPARSLogger.exception("exception_in_callSessionBeanInternal", new Object[]{version, headers.getMediaType(), uriInfo.getRequestUri().toASCIIString()}, e);
            throw JPARSException.exceptionOccurred(e);
        }
    }

    private boolean isValid(String jndiName) {
        String protocol = null;
        int colon = jndiName.indexOf(':');
        int slash = jndiName.indexOf('/');
        if (colon > 0 && (slash == -1 || colon < slash)) {
            protocol = jndiName.substring(0, colon);
        }
        return protocol == null || protocol.isEmpty() || protocol.equalsIgnoreCase("java") || protocol.equalsIgnoreCase("ejb");
    }

    private SessionBeanCall unmarshallSessionBeanCall(InputStream data) throws JAXBException {
        Class<?>[] jaxbClasses = new Class[] { SessionBeanCall.class };
        JAXBContext context = (JAXBContext) JAXBContextFactory.createContext(jaxbClasses, null);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        unmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, Boolean.FALSE);
        unmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_JSON);
        StreamSource ss = new StreamSource(data);
        return unmarshaller.unmarshal(ss, SessionBeanCall.class).getValue();
    }

    /**
     * Returns a response object containing the available contexts list. Used in JPARS 1.0 and below.
     *
     * @param version the version (null or "v1.0")
     * @param headers the HTTP headers
     * @param uriInfo the UTL info
     * @return Response object
     */
    private Response getContextsV1(String version, HttpHeaders headers, UriInfo uriInfo) {
        JPARSLogger.entering(CLASS_NAME, "getContextsV1", new Object[] { "GET", version, uriInfo.getRequestUri().toASCIIString() });
        try {
            final Set<String> contexts = getPersistenceFactory().getPersistenceContextNames();
            final String mediaType = StreamingOutputMarshaller.mediaType(headers.getAcceptableMediaTypes()).toString();
            final URI baseURI = uriInfo.getBaseUri();
            final List<Link> links = new ArrayList<>();

            for (String context : contexts) {
                if (version != null) {
                    links.add(new Link(context, mediaType, baseURI + version + "/" + context + "/metadata"));
                } else {
                    links.add(new Link(context, mediaType, baseURI + context + "/metadata"));
                }
            }

            final LinkList linkList = new LinkList();
            linkList.setList(links);

            final String result;
            if (mediaType.equals(MediaType.APPLICATION_JSON)) {
                result = marshallMetadata(linkList.getList(), mediaType);
            } else {
                result = marshallMetadata(linkList, mediaType);
            }
            return Response.ok(new StreamingOutputMarshaller(null, result, headers.getAcceptableMediaTypes())).build();
        } catch (JAXBException ex) {
            JPARSLogger.exception("exception_in_getContextsV1", new Object[] {version, headers.getMediaType(), uriInfo.getRequestUri().toASCIIString()}, ex);
            throw JPARSException.exceptionOccurred(ex);
        }
    }

    /**
     * Returns a response object containing the available contexts list. Used in JPARS 2.0 or above.
     *
     * @param version the version ("v2.0" or higher)
     * @param headers the HTTP headers
     * @param uriInfo the UTL info
     * @return Response object
     */
    private Response getContextsV2(String version, HttpHeaders headers, UriInfo uriInfo) {
        JPARSLogger.entering(CLASS_NAME, "getContextsV2", new Object[] { "GET", version, uriInfo.getRequestUri().toASCIIString() });
        try {
            final ContextsCatalog result = new ContextsCatalog();

            final Set<String> contexts = getPersistenceFactory().getPersistenceContextNames();
            for (String context : contexts) {
                final Resource contextResource = new Resource();
                contextResource.setName(context);

                final String href = HrefHelper.getRoot(uriInfo.getBaseUri().toString(), version, context).append("/metadata-catalog").toString();
                contextResource.setLinks((new ItemLinksBuilder())
                        .addCanonical(href)
                        .getList());

                result.addContext(contextResource);
            }

            final String mediaType = StreamingOutputMarshaller.mediaType(headers.getAcceptableMediaTypes()).toString();
            final String marshalled = marshallMetadata(result, mediaType);

            return Response.ok(new StreamingOutputMarshaller(null, marshalled, headers.getAcceptableMediaTypes())).build();
        } catch (JAXBException ex) {
            JPARSLogger.exception("exception_in_getContextsV2", new Object[] {version, headers.getMediaType(), uriInfo.getRequestUri().toASCIIString()}, ex);
            throw JPARSException.exceptionOccurred(ex);
        }
    }
}
