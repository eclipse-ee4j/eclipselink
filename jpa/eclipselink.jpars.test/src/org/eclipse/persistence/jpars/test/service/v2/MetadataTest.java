/*******************************************************************************
 * Copyright (c) 2014 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * 		Dmitry Kornilov - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.jpars.test.service.v2;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.jpa.rs.PersistenceContext;
import org.eclipse.persistence.jpa.rs.PersistenceFactoryBase;
import org.eclipse.persistence.jpa.rs.resources.MetadataResource;
import org.eclipse.persistence.jpa.rs.resources.common.AbstractResource;
import org.eclipse.persistence.jpars.test.util.ExamplePropertiesLoader;
import org.eclipse.persistence.jpars.test.util.RestUtils;
import org.eclipse.persistence.jpars.test.util.TestHttpHeaders;
import org.eclipse.persistence.jpars.test.util.TestURIInfo;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.persistence.Persistence;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.xml.bind.JAXBException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * A set of metadata tests.
 *
 * @author Dmitry Kornilov
 */
public class MetadataTest {
    private static final String JPARS_VERSION = "v2.0";

    private static final Logger logger = Logger.getLogger("org.eclipse.persistence.jpars.test.service");
    private static final String DEFAULT_PU = "jpars_employee-static";

    private static PersistenceContext context;
    private static MetadataResource metadataResource;

    @BeforeClass
    public static void setup() throws Exception {
        final Map<String, Object> properties = new HashMap<String, Object>();
        ExamplePropertiesLoader.loadProperties(properties);
        properties.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, null);
        properties.put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.DROP_AND_CREATE);
        properties.put(PersistenceUnitProperties.CLASSLOADER, new DynamicClassLoader(Thread.currentThread().getContextClassLoader()));
        properties.put(PersistenceUnitProperties.WEAVING, "static");

        final PersistenceFactoryBase factory = new PersistenceFactoryBase();
        factory.get(DEFAULT_PU, RestUtils.getServerURI(), JPARS_VERSION, properties);
        context = factory.bootstrapPersistenceContext(DEFAULT_PU, Persistence.createEntityManagerFactory(DEFAULT_PU, properties), RestUtils.getServerURI(JPARS_VERSION), JPARS_VERSION, false);

        metadataResource = new MetadataResource();
        metadataResource.setPersistenceFactory(factory);
    }

    @Test
    public void testMetadataCatalog() throws URISyntaxException, JAXBException, UnsupportedEncodingException {
        final Response response = metadataResource.getMetadataCatalog(JPARS_VERSION, DEFAULT_PU,
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
        final Response response = metadataResource.getEntityResource(JPARS_VERSION, DEFAULT_PU, "Employee",
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
        final Response response = metadataResource.getQueryResource(JPARS_VERSION, DEFAULT_PU, "Employee.salaryMax",
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
        final Response response = metadataResource.getEntityResource(JPARS_VERSION, DEFAULT_PU, "Employee",
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
        final Response response = metadataResource.getQueryResource(JPARS_VERSION, DEFAULT_PU, "Employee.findAll",
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
        final Response response = metadataResource.getQueryResource(JPARS_VERSION, DEFAULT_PU, "Employee.getManager",
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
        checkLink(response, "describes", "/entity/" + entityName);
    }

    private void checkQueryMetadata(String response, String queryName) throws URISyntaxException {
        assertTrue(response.contains("\"name\":\"" + queryName + "\""));
        checkLinkWithMediaType(response, "alternate", "/metadata-catalog/query/" + queryName, "application/schema+json");
        checkLinkWithMediaType(response, "canonical", "/metadata-catalog/query/" + queryName, "application/json");

        final String describesLink = "{\"rel\":\"describes\",\"href\":\"" + RestUtils.getServerURI(context.getVersion()) + context.getName() + "/query/" + queryName;
        assertTrue(response.contains(describesLink));
    }

    private void checkLink(String response, String rel, String uri) throws URISyntaxException {
        final String link = "{\"rel\":\"" + rel + "\",\"href\":\"" + RestUtils.getServerURI(context.getVersion()) + context.getName() + uri + "\"}";
        assertTrue(response.contains(link));
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
