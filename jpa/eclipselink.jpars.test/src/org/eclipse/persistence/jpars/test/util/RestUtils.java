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
//      Dmitry Kornilov - Fixes related to JPARS service versions
//      Dmitry Kornilov - Upgrade to Jersey 2.x
package org.eclipse.persistence.jpars.test.util;

import javax.ws.rs.client.Client;
import org.eclipse.persistence.jpa.rs.MatrixParameters;
import org.eclipse.persistence.jpa.rs.PersistenceContext;
import org.eclipse.persistence.jpa.rs.QueryParameters;
import org.eclipse.persistence.jpa.rs.exceptions.ErrorResponse;
import org.eclipse.persistence.jpa.rs.features.ServiceVersion;
import org.eclipse.persistence.jpa.rs.util.list.ReadAllQueryResultCollection;
import org.eclipse.persistence.jpa.rs.util.list.SimpleHomogeneousList;
import org.eclipse.persistence.jpars.test.server.RestCallFailedException;

import javax.imageio.ImageIO;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static org.junit.Assert.fail;

/**
 * Helper class with static utility methods used for REST service testing.
 *
 * @author  gonural - initial implementation
 *          Dmitry Kornilov - JAXRS 2.x, Jersey 2.x
 */
public class RestUtils {
    public static final String JPA_RS_VERSION_STRING = "jpars.version.string";

    private static final String APPLICATION_LOCATION = "/eclipselink.jpars.test/persistence/";
    private static final String SERVER_URI_BASE = "server.uri.base";
    private static final String DEFAULT_SERVER_URI_BASE = "http://localhost:8080";
    private static final String JSON_REST_MESSAGE_FOLDER = "org/eclipse/persistence/jpars/test/restmessage/json/";
    private static final String XML_REST_MESSAGE_FOLDER = "org/eclipse/persistence/jpars/test/restmessage/xml/";
    private static final String IMAGE_FOLDER = "org/eclipse/persistence/jpars/test/image/";

    private static final Logger logger = Logger.getLogger("org.eclipse.persistence.jpars.test.server");
    private static final Client client = ClientBuilder.newClient();

    /**
     * Gets the server uri.
     *
     * @return the server uri
     * @throws URISyntaxException the URI syntax exception
     */
    public static URI getServerURI() throws URISyntaxException {
        String serverURIBase = System.getProperty(SERVER_URI_BASE, DEFAULT_SERVER_URI_BASE);
        return new URI(serverURIBase + APPLICATION_LOCATION);
    }

    /**
     * Gets the server uri.
     *
     * @return the server uri
     */
    public static URI getServerURI(String versionString) throws URISyntaxException {
        String serverURIBase = System.getProperty(SERVER_URI_BASE, DEFAULT_SERVER_URI_BASE);
        String versionStringUrlFragment = versionString == null || versionString.equals("") ? "" : versionString + "/";
        return new URI(serverURIBase + APPLICATION_LOCATION + versionStringUrlFragment);
    }

    /**
     * Marshals given object.
     *
     * @param context persistence context
     * @param object object to marshal
     * @param mediaType media type to use (XML or JSON)
     * @return string containing XML or JSON representation of given object
     */
    public static String marshal(PersistenceContext context, Object object, MediaType mediaType)
            throws JAXBException, UnsupportedEncodingException {
        return marshal(context, object, mediaType, true);
    }

    /**
     * Marshals given object.
     *
     * @param context persistence context
     * @param object object to marshal
     * @param mediaType media type to use (XML or JSON)
     * @param sendRelationships if this is set to true, relationships will be sent as links instead of sending
     *                          the actual objects in the relationships
     * @return string containing XML or JSON representation of given object
     */
    public static String marshal(PersistenceContext context, Object object, MediaType mediaType, boolean sendRelationships)
            throws JAXBException, UnsupportedEncodingException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        context.marshall(object, mediaType, os, sendRelationships);
        return os.toString("UTF-8");
    }

    /**
     * Unmarshals given string to an object of given type.
     *
     * @param context persistence context
     * @param msg string to unmarshal
     * @param resultClass resulting object type
     * @param mediaType media type to use (XML or JSON)
     * @return unmarshaled object
     */
    @SuppressWarnings("unchecked")
    public static <T> T unmarshal(PersistenceContext context, String msg, Class<T> resultClass, MediaType mediaType) throws JAXBException {
        return (T) context.unmarshalEntity(resultClass.getSimpleName(), mediaType, new ByteArrayInputStream(msg.getBytes()));
    }

    /**
     * Reads object of given type with given ID.
     *
     * @param context persistence context
     * @param id id of the obejct to retrieve
     * @param resultClass object type
     * @param outputMediaType media type to use (XML or JSON)
     * @param tenantId tenantId or null if no tenant
     * @return retrieved object
     */
    public static <T> T restReadObject(PersistenceContext context,
                                       Object id,
                                       Class<T> resultClass,
                                       MediaType outputMediaType,
                                       Map<String, String> tenantId)
            throws RestCallFailedException, URISyntaxException, JAXBException {
        final String result = restRead(context, id, resultClass, outputMediaType, tenantId);
        return unmarshal(context, result, resultClass, outputMediaType);
    }

    /**
     * Reads object of given type with given ID.
     *
     * @param context persistent context
     * @param id id of the object to retrieve
     * @param resultClass object type
     * @param outputMediaType media type to use (XML or JSON)
     * @return retrieved object
     */
    public static <T> T restReadObject(PersistenceContext context,
                                       Object id,
                                       Class<T> resultClass,
                                       MediaType outputMediaType)
            throws RestCallFailedException, URISyntaxException, JAXBException {
        return restReadObject(context, id, resultClass, outputMediaType, null);
    }

    /**
     * Reads object of given type with given ID.
     *
     * @param context persistent context
     * @param id id of the object to retrieve
     * @param resultClass object type
     * @return retrieved object
     */
    public static <T> T restReadObject(PersistenceContext context,
                                       Object id,
                                       Class<T> resultClass) throws RestCallFailedException, URISyntaxException, JAXBException {
        return restReadObject(context, id, resultClass, MediaType.APPLICATION_JSON_TYPE);
    }

    /**
     * Retrieves and returns a string representation (not unmarshalled) of an object with given id and type.
     *
     * @param context persistence context
     * @param id id of the object to retrieve
     * @param resultClass type of the object to retrieve
     * @param outputMediaType media type to use (XML or JSON)
     * @param tenantId tenantId or null if no tenant
     * @return XML or JSON representation of retrieved object
     */
    public static String restRead(PersistenceContext context,
                                  Object id,
                                  Class resultClass,
                                  MediaType outputMediaType,
                                  Map<String, String> tenantId) throws RestCallFailedException, URISyntaxException {
        final String uri = new UriBuilder(context).addTenantInfo(tenantId).addEntity(resultClass.getSimpleName(), id).toString();
        final Response response = doGet(context, uri, outputMediaType);
        return response.readEntity(String.class);
    }

    /**
     * Retrieves and returns a string representation (not unmarshalled) of an object with given id and type.
     *
     * @param context persistence context
     * @param id id of the object to retrieve
     * @param resultClass type of the object to retrieve
     * @param outputMediaType media type to use (XML or JSON)
     * @return XML or JSON representation of retrieved object
     */
    public static String restRead(PersistenceContext context, Object id, Class resultClass, MediaType outputMediaType)
            throws RestCallFailedException, URISyntaxException {
        return restRead(context, id, resultClass, outputMediaType, null);
    }

    /**
     * Retrieves and returns a string representation (not unmarshalled) of an object with given id and type.
     *
     * @param context persistence context
     * @param id id of the object to retrieve
     * @param resultClass type of the object to retrieve
     * @return XML or JSON representation of retrieved object
     */
    public static String restRead(PersistenceContext context, Object id, Class resultClass)
            throws RestCallFailedException, URISyntaxException {
        return restRead(context, id, resultClass, MediaType.APPLICATION_JSON_TYPE);
    }

    /**
     * Retrieved object referenced by given link.
     *
     * @param context persistence context
     * @param href link to object to retrieve
     * @param resultClass type of the object to retrieve
     * @return retrieved object
     */
    @SuppressWarnings("unchecked")
    public static <T> T restReadByHref(PersistenceContext context, String href, Class<T> resultClass)
            throws RestCallFailedException, JAXBException {
        final Response response = doGet(context, href, MediaType.APPLICATION_JSON_TYPE);
        final String result = response.readEntity(String.class);
        return unmarshal(context, result, resultClass, MediaType.APPLICATION_JSON_TYPE);
    }

    /**
     * REST GET entity with hints.
     *
     * @param context persistent context
     * @param id entity ID
     * @param type entity type
     * @param hints hints list
     * @param outputMediaType media type to use (XML or JSON)
     * @return response in string
     */
    public static String restReadWithHints(PersistenceContext context,
                                           Object id,
                                           String type,
                                           Map<String, String> hints,
                                           MediaType outputMediaType) throws RestCallFailedException, URISyntaxException {
        final String uri = new UriBuilder(context).addEntity(type, id).addHints(hints).toString();
        final Response response = doGet(context, uri, outputMediaType);
        return response.readEntity(String.class);
    }

    /**
     * Updates given object.
     *
     * @param <T> the generic type
     * @param context persistence context
     * @param object object to update
     * @param resultClass the result class
     * @param tenantId the tenant id
     * @param mediaType media type to use (XML or JSON)
     * @param sendLinks if true links will be marshalled
     * @return updated object
     */
    @SuppressWarnings("unchecked")
    public static <T> T restUpdate(PersistenceContext context,
                                   Object object,
                                   Class<T> resultClass,
                                   Map<String, String> tenantId,
                                   MediaType mediaType,
                                   boolean sendLinks)
            throws RestCallFailedException, URISyntaxException, JAXBException, UnsupportedEncodingException {
        String marshalledObj = marshal(context, object, mediaType, sendLinks);
        String uri = new UriBuilder(context).addTenantInfo(tenantId).addEntity(resultClass.getSimpleName()).toString();
        Response response = doPost(context, uri, Entity.entity(marshalledObj, mediaType), mediaType);
        return unmarshal(context, response.readEntity(String.class), resultClass, mediaType);
    }

    /**
     * Updates given object.
     *
     * @param context persistence context
     * @param object object to update
     * @param resultClass the result class
     * @return updated object
     */
    public static <T> T restUpdate(PersistenceContext context, Object object, Class<T> resultClass)
            throws RestCallFailedException, URISyntaxException, JAXBException, UnsupportedEncodingException {
        return restUpdate(context, object, resultClass, null, MediaType.APPLICATION_JSON_TYPE, true);
    }

    /**
     * Updates given object.
     *
     * @param context persistence context
     * @param object object to update
     * @param resultClass the result class
     * @param sendLinks if true links will be marshalled
     * @return updated object
     */
    public static <T> T restUpdate(PersistenceContext context, Object object, Class<T> resultClass, boolean sendLinks)
            throws RestCallFailedException, URISyntaxException, JAXBException, UnsupportedEncodingException {
        return restUpdate(context, object, resultClass, null, MediaType.APPLICATION_JSON_TYPE, sendLinks);
    }

    /**
     * Posts a given message to update an object. Returns XML or JSON representation of updated object.
     *
     * @param context persistence context
     * @param message XML or JSON representation of an object to update
     * @param resultClass the result object type
     * @param tenantId the tenant id
     * @param mediaType media type to use (XML or JSON)
     * @return updated object in XMK or JSON depending on passed media type
     */
    public static String restUpdateStr(PersistenceContext context,
                                       String message,
                                       Class resultClass,
                                       Map<String, String> tenantId,
                                       MediaType mediaType) throws RestCallFailedException, URISyntaxException {
        final String uri = new UriBuilder(context).addTenantInfo(tenantId).addEntity(resultClass.getSimpleName()).toString();
        final Response response = doPost(context, uri, Entity.entity(message, mediaType), mediaType);
        return response.readEntity(String.class);
    }

    /**
     * Rest create.
     *
     * @param context persistence context
     * @param object the object to create
     * @param resultClass the result object type
     * @param tenantId the tenant id
     * @param mediaType media type to use (XML or JSON)
     * @param sendLinks if this is set to true, relationships will be sent as links instead of sending
     *                  the actual objects in the relationships
     * @return created object
     */
    @SuppressWarnings("unchecked")
    public static <T> T restCreate(PersistenceContext context,
                                   Object object,
                                   Class<T> resultClass,
                                   Map<String, String> tenantId,
                                   MediaType mediaType, boolean sendLinks)
            throws RestCallFailedException, URISyntaxException, JAXBException, UnsupportedEncodingException {
        final String marshalledObj = marshal(context, object, mediaType, sendLinks);
        final String uri = new UriBuilder(context).addTenantInfo(tenantId).addEntity(resultClass.getSimpleName()).toString();
        final Response response = doPut(context, uri, Entity.entity(marshalledObj, mediaType), mediaType);
        return unmarshal(context, response.readEntity(String.class), resultClass, mediaType);
    }

    /**
     * Creates an object.
     *
     * @param context persistence context
     * @param object the object to create
     * @param resultClass the result object type
     * @param mediaType media type to use (XML or JSON)
     * @return created object
     */
    public static <T> T restCreate(PersistenceContext context,
                                   Object object,
                                   Class<T> resultClass,
                                   MediaType mediaType)
            throws RestCallFailedException, URISyntaxException, JAXBException, UnsupportedEncodingException {
        return restCreate(context, object, resultClass, null, mediaType, false);
    }

    /**
     * Creates an object.
     *
     * @param context persistence context
     * @param object the object to create
     * @param resultClass the result object type
     * @return created object
     */
    public static <T> T restCreate(PersistenceContext context, Object object, Class<T> resultClass)
            throws RestCallFailedException, URISyntaxException, JAXBException, UnsupportedEncodingException {
        return restCreate(context, object, resultClass, MediaType.APPLICATION_JSON_TYPE);
    }

    /**
     * Rest create an object from string message.
     *
     * @param context persistence context
     * @param msg XML or JSON object representation
     * @param resultClass the result object type
     * @param mediaType media type to use (XML or JSON)
     * @return created object
     */
    @SuppressWarnings("unchecked")
    public static <T> T restCreateStr(PersistenceContext context,
                                      String msg,
                                      Class<T> resultClass,
                                      MediaType mediaType) throws URISyntaxException, JAXBException {
        final String uri = new UriBuilder(context).addEntity(resultClass.getSimpleName()).toString();
        final Response response = doPut(context, uri, Entity.entity(msg, mediaType), mediaType);
        return unmarshal(context, response.readEntity(String.class), resultClass, mediaType);
    }

    /**
     * Rest create with sequence.
     *
     * @param context persistence context
     * @param msg XML or JSON object representation
     * @param resultClass the result object type
     * @param mediaType media type to use (XML or JSON)
     */
    public static void restCreateWithSequence(PersistenceContext context,
                                              String msg,
                                              Class resultClass,
                                              MediaType mediaType) throws URISyntaxException {
        final String uri = new UriBuilder(context).addEntity(resultClass.getSimpleName()).toString();
        doPut(context, uri, Entity.entity(msg, mediaType), mediaType);
    }

    /**
     * Rest delete.
     *
     * @param context persistence context
     * @param id ID of object to delete
     * @param resultClass the result object type
     * @param tenantId the tenant id
     * @param mediaType media type to use (XML or JSON)
     */
    public static void restDelete(PersistenceContext context,
                                  Object id,
                                  Class resultClass,
                                  Map<String, String> tenantId,
                                  MediaType mediaType) throws RestCallFailedException, URISyntaxException {
        final String uri = new UriBuilder(context).addTenantInfo(tenantId).addEntity(resultClass.getSimpleName(), id).toString();
        doDelete(context, uri, mediaType);
    }

    /**
     * Rest delete.
     *
     * @param context persistence context
     * @param id ID of object to delete
     * @param resultClass the result object type
     * @param mediaType media type to use (XML or JSON)
     */
    public static void restDelete(PersistenceContext context,
                                  Object id,
                                  Class resultClass,
                                  MediaType mediaType) throws RestCallFailedException, URISyntaxException {
        restDelete(context, id, resultClass, null, mediaType);
    }

    /**
     * Rest delete.
     *
     * @param context persistence context
     * @param id ID of object to delete
     * @param resultClass the result object type
     */
    public static void restDelete(PersistenceContext context, Object id, Class resultClass)
            throws RestCallFailedException, URISyntaxException {
        restDelete(context, id, resultClass, null, MediaType.APPLICATION_JSON_TYPE);
    }

    /**
     * Rest named multi result query.
     *
     * @param context persistence context
     * @param queryName the query name
     * @param parameters the parameters
     * @param hints the hints
     * @param mediaType media type to use (XML or JSON)
     * @return query result in string
     */
    public static String restNamedMultiResultQueryResult(PersistenceContext context,
                                                         String queryName,
                                                         Map<String, Object> parameters,
                                                         Map<String, String> hints,
                                                         MediaType mediaType) throws URISyntaxException {
        final String uri = new UriBuilder(context).addQuery(queryName).addParameters(parameters).addHints(hints).toString();
        final Response response = doGet(context, uri, mediaType);
        return response.readEntity(String.class);
    }

    /**
     * Rest named multi result query.
     *
     * @param context persistence context
     * @param queryName the query name
     * @param returnType the result object type
     * @param parameters the parameters
     * @param hints the hints
     * @param mediaType media type to use (XML or JSON)
     * @return list of objects
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> restNamedMultiResultQuery(PersistenceContext context,
                                                        String queryName,
                                                        Class<T> returnType,
                                                        Map<String, Object> parameters,
                                                        Map<String, String> hints,
                                                        MediaType mediaType) throws URISyntaxException, JAXBException {
        final String result = restNamedMultiResultQueryResult(context, queryName, parameters, hints, mediaType);

        if (context.getServiceVersion().compareTo(ServiceVersion.VERSION_2_0) >= 0) {
            // 2.0 or higher
            final Object obj = context.unmarshalEntity("ReadAllQueryResultCollection", mediaType, new ByteArrayInputStream(result.getBytes()));
            if (obj instanceof ReadAllQueryResultCollection) {
                final ReadAllQueryResultCollection c = (ReadAllQueryResultCollection) obj;
                return (List<T>)c.getItems();
            } else {
                return Collections.emptyList();
            }
        } else {
            // 1.0 or no version
            final Object obj = context.unmarshalEntity(returnType.getSimpleName(), mediaType, new ByteArrayInputStream(result.getBytes()));
            if (obj instanceof SimpleHomogeneousList) {
                return Collections.emptyList();
            } else {
                return (List<T>)obj;
            }
        }
    }

    /**
     * Rest named multi result query.
     *
     * @param context persistence context
     * @param queryName the query name
     * @param returnType the result object type
     * @param parameters the parameters
     * @param hints the hints
     * @return list of objects
     */
    public static <T> List<T> restNamedMultiResultQuery(PersistenceContext context,
                                                        String queryName,
                                                        Class<T> returnType,
                                                        Map<String, Object> parameters,
                                                        Map<String, String> hints) throws URISyntaxException, JAXBException {
        return restNamedMultiResultQuery(context, queryName, returnType, parameters, hints, MediaType.APPLICATION_JSON_TYPE);
    }

    /**
     * Rest named single result query.
     *
     * @param context persistence context
     * @param queryName the query name
     * @param parameters the parameters
     * @param hints the hints
     * @param mediaType media type to use (XML or JSON)
     * @return query result in string
     */
    public static String restNamedSingleResultQueryResult(PersistenceContext context,
                                                          String queryName,
                                                          Map<String, Object> parameters,
                                                          Map<String, String> hints,
                                                          MediaType mediaType) throws URISyntaxException {
        final String uri = new UriBuilder(context).addSingleResultQuery(queryName).addParameters(parameters).addHints(hints).toString();
        final Response response = doGet(context, uri, mediaType);
        return response.readEntity(String.class);
    }

    /**
     * Rest named single result query.
     *
     * @param context persistence context
     * @param queryName the query name
     * @param returnType the result object type
     * @param parameters the parameters
     * @param hints the hints
     * @param mediaType media type to use (XML or JSON)
     * @return object
     */
    public static Object restNamedSingleResultQuery(PersistenceContext context,
                                                    String queryName,
                                                    String returnType,
                                                    Map<String, Object> parameters,
                                                    Map<String, String> hints,
                                                    MediaType mediaType) throws URISyntaxException, JAXBException {
        final String result = restNamedSingleResultQueryResult(context, queryName, parameters, hints, mediaType);
        return context.unmarshalEntity(returnType, mediaType, new ByteArrayInputStream(result.getBytes()));
    }

    /**
     * Rest named single result query.
     *
     * @param context persistence context
     * @param queryName the query name
     * @param returnType the result object type
     * @param parameters the parameters
     * @param hints the hints
     * @return object
     */
    public static Object restNamedSingleResultQuery(PersistenceContext context,
                                                    String queryName,
                                                    String returnType,
                                                    Map<String, Object> parameters,
                                                    Map<String, String> hints) throws URISyntaxException, JAXBException {
        return restNamedSingleResultQuery(context, queryName, returnType, parameters, hints, MediaType.APPLICATION_JSON_TYPE);
    }

    /**
     * Rest named single result query in byte array.
     *
     * @param context the persistent context
     * @param queryName the query name
     * @param parameters the parameters
     * @param hints the hints
     * @param outputMediaType the output media type
     * @return the byte[]
     * @throws URISyntaxException the URI syntax exception
     */
    public static byte[] restNamedSingleResultQueryInByteArray(PersistenceContext context,
                                                               String queryName,
                                                               Map<String, Object> parameters,
                                                               Map<String, String> hints,
                                                               MediaType outputMediaType) throws URISyntaxException {
        final String uri = new UriBuilder(context).addSingleResultQuery(queryName).addParameters(parameters).addHints(hints).toString();
        final Response response = doGet(context, uri, outputMediaType);

        ByteArrayOutputStream baos = null;
        if (outputMediaType == MediaType.APPLICATION_OCTET_STREAM_TYPE) {
            try {
                //BufferedImage image = ImageIO.read(response.getEntityInputStream());
                BufferedImage image = ImageIO.read(response.readEntity(InputStream.class));
                baos = new ByteArrayOutputStream();
                ImageIO.write(image, "png", baos);
                return baos.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (baos != null) {
                    try {
                        baos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }

    /**
     * Rest update relationship.
     *
     * @param context persistence context
     * @param objectId the object id
     * @param relationshipName the relationship name
     * @param newValue the new value
     * @param mediaType media type to use (XML or JSON)
     * @return updated object
     */
    @SuppressWarnings("unchecked")
    public static <T> T restUpdateRelationship(PersistenceContext context,
                                               String objectId,
                                               String relationshipName,
                                               Object newValue,
                                               Class<T> resultClass,
                                               MediaType mediaType)
            throws RestCallFailedException, URISyntaxException, JAXBException, UnsupportedEncodingException {
        final String newValueStr = marshal(context, newValue, mediaType);
        final String uri = new UriBuilder(context).addEntity(resultClass.getSimpleName(), objectId, relationshipName).toString();
        final Response response = doPost(context, uri, Entity.entity(newValueStr, mediaType), mediaType);
        return unmarshal(context, response.readEntity(String.class), resultClass, mediaType);
    }

    /**
     * Rest update bidirectional relationship.
     *
     * @param context persistence context
     * @param objectId the object id
     * @param objectType the object type
     * @param relationshipName the relationship name
     * @param newValue the new value
     * @param mediaType the media type
     * @param partner the partner
     * @param sendLinks if this is set to true, relationships will be sent as links instead of sending
     *                  the actual objects in the relationships
     * @return the string
     */
    public static String restUpdateBidirectionalRelationship(PersistenceContext context,
                                                             String objectId,
                                                             Class objectType,
                                                             String relationshipName,
                                                             Object newValue,
                                                             MediaType mediaType,
                                                             String partner,
                                                             boolean sendLinks)
            throws RestCallFailedException, URISyntaxException, JAXBException, UnsupportedEncodingException {
        // Construct parameters
        final Map<String, Object> params = new HashMap<>(1);
        if (partner != null) {
            params.put(MatrixParameters.JPARS_RELATIONSHIP_PARTNER, partner);
        }

        final String newValueStr = marshal(context, newValue, mediaType, sendLinks);
        final String uri = new UriBuilder(context).addEntity(objectType.getSimpleName(), objectId, relationshipName).addParameters(params).toString();
        final Response response = doPost(context, uri, Entity.entity(newValueStr, mediaType), mediaType);
        return response.readEntity(String.class);
    }

    /**
     * Rest remove bidirectional relationship.
     *
     * @param context persistence context
     * @param objectId the object id
     * @param objectType the object type
     * @param relationshipName the relationship name
     * @param mediaType the media type
     * @param partner the partner
     * @param listItemId the list item id
     * @return the string
     */
    public static String restRemoveBidirectionalRelationship(PersistenceContext context,
                                                             String objectId,
                                                             Class objectType,
                                                             String relationshipName,
                                                             MediaType mediaType,
                                                             String partner,
                                                             String listItemId)
            throws RestCallFailedException, URISyntaxException, JAXBException {
        // Construct hints
        final Map<String, String> hints = new HashMap<>();
        if (partner != null) {
            hints.put(QueryParameters.JPARS_RELATIONSHIP_PARTNER, partner);
        }
        if (listItemId != null) {
            hints.put(QueryParameters.JPARS_LIST_ITEM_ID, listItemId);
        }

        final String uri = new UriBuilder(context).addEntity(objectType.getSimpleName(), objectId, relationshipName).addHints(hints).toString();
        final Response response = doDelete(context, uri, mediaType);
        return response.readEntity(String.class);
    }

    /**
     * Gets the JSON message.
     *
     * @param inputFile the input file
     * @return the JSON message
     */
    public static String getJSONMessage(String inputFile) {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(JSON_REST_MESSAGE_FOLDER + inputFile);
        return convertStreamToString(is);
    }

    /**
     * Gets the XML message.
     *
     * @param inputFile the input file
     * @return the XML message
     */
    public static String getXMLMessage(String inputFile) {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(XML_REST_MESSAGE_FOLDER + inputFile);
        return convertStreamToString(is);
    }

    /**
     * Convert image to byte array.
     *
     * @param imageName the image name
     * @return the byte[]
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static byte[] convertImageToByteArray(String imageName) throws IOException {
        final InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(IMAGE_FOLDER + imageName);
        final BufferedImage originalImage = ImageIO.read(is);
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(originalImage, getExtension(new File(imageName)), baos);
        return baos.toByteArray();
    }

    /**
     * Rest find attribute.
     *
     * @param context the context
     * @param id the id
     * @param objectType the object type
     * @param attribute the attribute
     * @param outputMediaType the output media type
     * @return attribute
     */
    public static String restFindAttribute(PersistenceContext context,
                                           Object id,
                                           Class objectType,
                                           String attribute,
                                           MediaType outputMediaType) throws RestCallFailedException, URISyntaxException {
        final String uri = new UriBuilder(context).addEntity(objectType.getSimpleName(), id, attribute).toString();
        final Response response = doGet(context, uri, outputMediaType);
        return response.readEntity(String.class);
    }

    /**
     * Rest find attribute query.
     *
     * @param context the context
     * @param id the id
     * @param objectType the object type
     * @param attribute the attribute
     * @param parameters the parameters
     * @param hints the hints
     * @param outputMediaType the output media type
     * @return attribute
     */
    public static String restFindAttribute(PersistenceContext context,
                                           Object id,
                                           Class objectType,
                                           String attribute,
                                           Map<String, Object> parameters,
                                           Map<String, String> hints,
                                           MediaType outputMediaType) throws RestCallFailedException, URISyntaxException {
        final String uri = new UriBuilder(context).addEntity(objectType.getSimpleName(), id, attribute).addParameters(parameters).addHints(hints).toString();
        final Response response = doGet(context, uri, outputMediaType);
        return response.readEntity(String.class);
    }

    /**
     * Rest update query.
     *
     * @param context the context
     * @param queryName the query name
     * @param parameters the parameters
     * @param hints the hints
     * @param mediaType the media type
     * @return the string
     */
    public static String restUpdateQuery(PersistenceContext context,
                                         String queryName,
                                         Map<String, Object> parameters,
                                         Map<String, String> hints,
                                         MediaType mediaType) throws URISyntaxException {
        final String uri = new UriBuilder(context).addQuery(queryName).addParameters(parameters).addHints(hints).toString();
        final Response response = doPost(context, uri, Entity.entity(null, mediaType), mediaType);
        return response.readEntity(String.class);
    }

    /**
     * Executes GET request to the server.
     *
     * @param context persistence context
     * @param uri uri
     * @param outputMediaType media type to use
     * @return response
     */
    public static Response doGet(PersistenceContext context, String uri, MediaType outputMediaType) {
        final Response response = client.target(uri).request(outputMediaType).get();
        checkResponse(Response.Status.OK, context, outputMediaType, response);
        return response;
    }

    /**
     * Executes POST request to the server.
     *
     * @param context persistence context
     * @param uri uri
     * @param entity entity to post
     * @param outputMediaType media type to use
     * @return response
     */
    public static Response doPost(PersistenceContext context, String uri, Entity entity, MediaType outputMediaType) {
        final Response response = client.target(uri).request(outputMediaType).accept(outputMediaType).post(entity);
        checkResponse(Response.Status.OK, context, outputMediaType, response);
        return response;
    }

    /**
     * Executes PUT request to the server.
     *
     * @param context persistence context
     * @param uri uri
     * @param entity entity to create
     * @param outputMediaType media type to use
     * @return response
     */
    public static Response doPut(PersistenceContext context, String uri, Entity entity, MediaType outputMediaType) {
        final Response response = client.target(uri).request(outputMediaType).put(entity);
        checkResponse(Response.Status.CREATED, context, outputMediaType, response);
        return response;
    }

    /**
     * Executes DELETE request to the server.
     *
     * @param context persistence context
     * @param uri uri
     * @param outputMediaType media type to use
     * @return response
     */
    public static Response doDelete(PersistenceContext context, String uri, MediaType outputMediaType) {
        final Response response = client.target(uri).request(outputMediaType).delete();
        checkResponse(Response.Status.OK, context, outputMediaType, response);
        return response;
    }

    private static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;
    }

    @SuppressWarnings("unused")
    private static void writeToFile(String data) {
        BufferedWriter writer = null;
        try {
            String fileName = (System.currentTimeMillis() + ".txt");
            writer = new BufferedWriter(new FileWriter(fileName));
            writer.write(data);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static String convertStreamToString(InputStream is) {
        try {
            return new java.util.Scanner(is).useDelimiter("\\A").next();
        } catch (Exception e) {
            return null;
        }
    }

    private static void checkResponse(Response.Status status, PersistenceContext context, MediaType outputMediaType, Response response) {
        if (response.getStatus() != status.getStatusCode()) {
            restCallFailed(context, response, outputMediaType);
        }
    }

    private static void restCallFailed(PersistenceContext context, Response response, MediaType mediaType) {
        final String entity = response.readEntity(String.class);
        if ((entity != null) && (!entity.isEmpty())) {
            if ((entity.contains("problemType")) && (entity.contains("title") && (entity.contains("httpStatus")))) {
                try {
                    ErrorResponse errorDetail = (ErrorResponse) context.unmarshalEntity(ErrorResponse.class.getSimpleName(), mediaType, new ByteArrayInputStream(entity.getBytes()));
                    throw new RestCallFailedException(response.getStatus(), errorDetail);
                } catch (JAXBException e) {
                    fail("Exception thrown unmarshalling: " + e);
                }
            }
        }
        throw new RestCallFailedException(response.getStatus());
    }
}
