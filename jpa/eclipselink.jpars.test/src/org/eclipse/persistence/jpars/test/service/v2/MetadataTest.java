/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//         Dmitry Kornilov - Initial implementation
package org.eclipse.persistence.jpars.test.service.v2;

import org.eclipse.persistence.jpa.rs.resources.EntityResource;
import org.eclipse.persistence.jpa.rs.resources.MetadataResource;
import org.eclipse.persistence.jpa.rs.resources.QueryResource;
import org.eclipse.persistence.jpa.rs.resources.common.AbstractResource;
import org.eclipse.persistence.jpars.test.BaseJparsTest;
import org.eclipse.persistence.jpars.test.util.RestUtils;
import org.eclipse.persistence.jpars.test.util.TestHttpHeaders;
import org.eclipse.persistence.jpars.test.util.TestURIInfo;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.xml.bind.JAXBException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * A set of metadata tests.
 *
 * @author Dmitry Kornilov
 * @since EclipseLink 2.6.0
 */
public class MetadataTest extends BaseJparsTest {
    protected static MetadataResource metadataResource;

    @BeforeClass
    public static void setup() throws Exception {
        initContext("jpars_employee-static", "v2.0");
        metadataResource = new MetadataResource();
        metadataResource.setPersistenceFactory(factory);
    }

    @Test
    public void testMetadataCatalog() throws URISyntaxException, JAXBException, UnsupportedEncodingException {
        final Response response = metadataResource.getMetadataCatalog(version, pu,
                TestHttpHeaders.generateHTTPHeader(MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON),
                new TestURIInfo());
        final String responseString = getResponseAsString(response);

        // Check entities
        checkEntityMetadata(responseString, "PhoneNumber");
        checkEntityMetadata(responseString, "SmallProject");
        checkEntityMetadata(responseString, "Employee");
        checkEntityMetadata(responseString, "LargeProject");
        checkEntityMetadata(responseString, "Expertise");
        checkEntityMetadata(responseString, "Project");
        checkEntityMetadata(responseString, "EmployeeAddress");
        checkEntityMetadata(responseString, "Office");

        // Check queries
        checkQueryMetadata(responseString, "Employee.salaryMax");
        checkQueryMetadata(responseString, "EmployeeAddress.updatePostalCode");
        checkQueryMetadata(responseString, "EmployeeAddress.getRegion");
        checkQueryMetadata(responseString, "Employee.getManager");
        checkQueryMetadata(responseString, "Employee.getPhoneNumbers");
        checkQueryMetadata(responseString, "Employee.count");
        checkQueryMetadata(responseString, "EmployeeAddress.getAll");
        checkQueryMetadata(responseString, "Employee.getManagerById");
        checkQueryMetadata(responseString, "EmployeeAddress.getById");
        checkQueryMetadata(responseString, "Employee.findAll");
        checkQueryMetadata(responseString, "Employee.deleteAll");
        checkQueryMetadata(responseString, "Employee.findAllPageable");
        checkQueryMetadata(responseString, "EmployeeAddress.getPicture");
    }

    @Test
    public void testEntityMetadata() throws URISyntaxException, JAXBException, UnsupportedEncodingException {
        final Response response = metadataResource.getEntityResource(version, pu, "Employee",
                TestHttpHeaders.generateHTTPHeader(MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON),
                new TestURIInfo());
        final String responseString = getResponseAsString(response);

        // Check ID
        assertTrue(responseString.contains("\"name\":\"Employee\""));

        // Check metadata
        checkEntityMetadata(responseString, "Employee");
    }

    @Test
    public void testQueryMetadata() throws URISyntaxException, JAXBException, UnsupportedEncodingException {
        final Response response = metadataResource.getQueryResource(version, pu, "Employee.salaryMax",
                TestHttpHeaders.generateHTTPHeader(MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON),
                new TestURIInfo());
        final String responseString = getResponseAsString(response);

        // Check ID
        assertTrue(responseString.contains("\"name\":\"Employee.salaryMax\""));

        // Check metadata
        checkQueryMetadata(responseString, "Employee.salaryMax");
    }

    @Test
    public void testEntitySchema() throws URISyntaxException, JAXBException, UnsupportedEncodingException {
        final Response response = metadataResource.getEntityResource(version, pu, "Employee",
                TestHttpHeaders.generateHTTPHeader(AbstractResource.APPLICATION_SCHEMA_JSON_TYPE, AbstractResource.APPLICATION_SCHEMA_JSON),
                new TestURIInfo());
        final String responseString = getResponseAsString(response);

        // Check id
        assertTrue(responseString.contains("\"$schema\":\"" + RestUtils.getServerURI(context.getVersion()) + context.getName() + "/metadata-catalog/entity/Employee#"));

        // Check Title
        assertTrue(responseString.contains("\"title\":\"Employee\""));

        // Check properties
        checkProperty(responseString, "id", "integer");
        checkProperty(responseString, "firstName", "string");
        checkProperty(responseString, "gender", "object");
        checkProperty(responseString, "lastName", "string");
        checkProperty(responseString, "salary", "number");
        checkProperty(responseString, "version", "number");
        checkProperty(responseString, "period", "object");
        checkPropertyWithRef(responseString, "manager", "Employee");
        checkPropertyWithRef(responseString, "office", "Office");
        checkPropertyWithRef(responseString, "address", "EmployeeAddress");
        checkArrayProperty(responseString, "certifications", "object");
        checkArrayProperty(responseString, "responsibilities", "string");
        checkArrayPropertyWithEntityRef(responseString, "projects", "Project");
        checkArrayPropertyWithEntityRef(responseString, "expertiseAreas", "Expertise");
        checkArrayPropertyWithEntityRef(responseString, "managedEmployees", "Employee");
        checkArrayPropertyWithEntityRef(responseString, "phoneNumbers", "PhoneNumber");

        // Check links
        checkLinkWithMethod(responseString, "find", "/entity/Employee/{primaryKey}", "GET");
        checkLinkWithMethod(responseString, "create", "/entity/Employee", "PUT");
        checkLinkWithMethod(responseString, "update", "/entity/Employee", "POST");
        checkLinkWithMethod(responseString, "delete", "/entity/Employee/{primaryKey}", "DELETE");
    }

    @Test
    public void testReadAllQuerySchema() throws URISyntaxException, JAXBException, UnsupportedEncodingException {
        final Response response = metadataResource.getQueryResource(version, pu, "Employee.findAll",
                TestHttpHeaders.generateHTTPHeader(AbstractResource.APPLICATION_SCHEMA_JSON_TYPE, AbstractResource.APPLICATION_SCHEMA_JSON),
                new TestURIInfo());
        final String responseString = getResponseAsString(response);

        // Check id
        assertTrue(responseString.contains("\"$schema\":\"" + RestUtils.getServerURI(context.getVersion()) + context.getName() + "/metadata-catalog/query/Employee.findAll#\""));

        // Check Title
        assertTrue(responseString.contains("\"title\":\"Employee.findAll\""));

        // Check properties
        checkArrayPropertyWithEntityRef(responseString, "items", "Employee");

        // Check links
        checkLinkWithMethod(responseString, "execute", "/query/Employee.findAll", "GET");
    }

    @Test
    public void testReportQuerySchema() throws URISyntaxException, JAXBException, UnsupportedEncodingException {
        final Response response = metadataResource.getQueryResource(version, pu, "Employee.getManager",
                TestHttpHeaders.generateHTTPHeader(AbstractResource.APPLICATION_SCHEMA_JSON_TYPE, AbstractResource.APPLICATION_SCHEMA_JSON),
                new TestURIInfo());
        final String responseString = getResponseAsString(response);

        // Check id
        assertTrue(responseString.contains("\"$schema\":\"" + RestUtils.getServerURI(context.getVersion()) + context.getName() + "/metadata-catalog/query/Employee.getManager#\""));

        // Check Title
        assertTrue(responseString.contains("\"title\":\"Employee.getManager\""));

        // Check properties
        checkArrayPropertyWithRef(responseString, "items", "#/definitions/result");

        // Check definitions
        final String definitions = "\"definitions\":{\"result\":{\"properties\":{" +
                "\"firstName\":{\"type\":\"string\"}," +
                "\"lastName\":{\"type\":\"string\"}," +
                "\"manager\":{\"$ref\":\""  + RestUtils.getServerURI(context.getVersion()) + context.getName() + "/metadata-catalog/entity/Employee#\"}" +
                "}}}";
        assertTrue(responseString.contains(definitions));

        // Check links
        checkLinkWithMethod(responseString, "execute", "/query/Employee.getManager", "GET");
    }

    @Test
    public void testEntityOptions() throws URISyntaxException {
        final EntityResource entityResource = new EntityResource();
        entityResource.setPersistenceFactory(factory);

        final Response response = entityResource.getEntityOptions(version, pu, "Employee",
                TestHttpHeaders.generateHTTPHeader(MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON),
                new TestURIInfo());
        assertTrue(response.getMetadata().containsKey("Link"));

        final String link = "<" + RestUtils.getServerURI(context.getVersion()) + context.getName() + "/metadata-catalog/entity/Employee>; rel=describedby";
        assertTrue(response.getMetadata().get("Link").get(0).equals(link));
    }

    @Test
    public void testQueryOptions() throws URISyntaxException {
        final QueryResource queryResource = new QueryResource();
        queryResource.setPersistenceFactory(factory);

        final Response response = queryResource.getQueryOptions(version, pu, "Employee.getManager",
                TestHttpHeaders.generateHTTPHeader(MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON),
                new TestURIInfo());
        assertTrue(response.getMetadata().containsKey("Link"));

        final String link = "<" + RestUtils.getServerURI(context.getVersion()) + context.getName() + "/metadata-catalog/query/Employee.getManager>; rel=describedby";
        assertTrue(response.getMetadata().get("Link").get(0).equals(link));
    }

    private String getResponseAsString(Response response) {
        StreamingOutput output = (StreamingOutput)response.getEntity();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            output.write(outputStream);
        } catch (IOException ex) {
            fail(ex.toString());
        }
        return outputStream.toString();
    }

    private void checkProperty(String response, String name, String type) {
        final String propertyStr = "\"" + name + "\":{\"type\":\"" + type + "\"}";
        assertTrue(response.contains(propertyStr));
    }

    private void checkPropertyWithRef(String response, String name, String refEntityName) throws URISyntaxException {
        final String propertyStr = "\"" + name + "\":{\"$ref\":\"" +
                RestUtils.getServerURI(context.getVersion()) + context.getName() + "/metadata-catalog/entity/" + refEntityName + "#\"}";
        assertTrue(response.contains(propertyStr));
    }

    private void checkArrayProperty(String response, String name, String itemType) {
        final String propertyStr = "\"" + name + "\":{\"type\":\"array\",\"items\":{\"type\":\"" + itemType + "\"}}";
        assertTrue(response.contains(propertyStr));
    }

    private void checkArrayPropertyWithEntityRef(String response, String name, String refEntityName) throws URISyntaxException {
        final String ref = RestUtils.getServerURI(context.getVersion()) + context.getName() + "/metadata-catalog/entity/" + refEntityName + "#";
        checkArrayPropertyWithRef(response, name, ref);
    }

    private void checkArrayPropertyWithRef(String response, String name, String ref) {
        final String propertyStr = "\"" + name + "\":{\"type\":\"array\",\"items\":{\"$ref\":\"" + ref + "\"}}";
        assertTrue(response.contains(propertyStr));
    }

    private void checkEntityMetadata(String response, String entityName) throws URISyntaxException {
        assertTrue(response.contains("\"name\":\"" + entityName + "\""));
        checkLinkWithMediaType(response, "alternate", "/metadata-catalog/entity/" + entityName, "application/schema+json");
        checkLinkWithMediaType(response, "canonical", "/metadata-catalog/entity/" + entityName, "application/json");
        checkLinkJson(response, "describes", "/entity/" + entityName);
    }

    private void checkQueryMetadata(String response, String queryName) throws URISyntaxException {
        assertTrue(response.contains("\"name\":\"" + queryName + "\""));
        checkLinkWithMediaType(response, "alternate", "/metadata-catalog/query/" + queryName, "application/schema+json");
        checkLinkWithMediaType(response, "canonical", "/metadata-catalog/query/" + queryName, "application/json");

        final String describesLink = "{\"rel\":\"describes\",\"href\":\"" + RestUtils.getServerURI(context.getVersion()) + context.getName() + "/query/" + queryName;
        assertTrue(response.contains(describesLink));
    }

    private void checkLinkWithMediaType(String response, String rel, String uri, String mediaType) throws URISyntaxException {
        final String link = "{\"rel\":\"" + rel + "\",\"href\":\"" + RestUtils.getServerURI(context.getVersion()) + context.getName() + uri + "\",\"mediaType\":\"" + mediaType + "\"}";
        assertTrue(response.contains(link));
    }

    private void checkLinkWithMethod(String response, String rel, String uri, String method) throws URISyntaxException {
        final String link = "{\"rel\":\"" + rel + "\",\"href\":\"" + RestUtils.getServerURI(context.getVersion()) + context.getName() + uri + "\",\"method\":\"" + method + "\"}";
        assertTrue(response.contains(link));
    }
}
