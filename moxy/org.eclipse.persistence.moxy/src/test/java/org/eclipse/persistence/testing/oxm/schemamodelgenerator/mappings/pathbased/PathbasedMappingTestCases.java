/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
// dmccann - April 30/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.oxm.schemamodelgenerator.mappings.pathbased;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.eclipse.persistence.internal.oxm.mappings.Descriptor;
import org.eclipse.persistence.internal.oxm.schema.SchemaModelGeneratorProperties;
import org.eclipse.persistence.internal.oxm.schema.model.Schema;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.schemamodelgenerator.GenerateSchemaTestCases;
import org.w3c.dom.Document;

public class PathbasedMappingTestCases extends GenerateSchemaTestCases {
    protected static final String MYNS = "http://www.example.org/customer-example";
    protected static final String XSD_RESOURCE = "org/eclipse/persistence/testing/oxm/schemamodelgenerator/mappings/pathbased/Customer.xsd";
    protected static final String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/schemamodelgenerator/mappings/pathbased/Customer-data.xml";
    protected static final String INVALID_XML_RESOURCE = "org/eclipse/persistence/testing/oxm/schemamodelgenerator/mappings/pathbased/Invalid-customer-data.xml";

    public PathbasedMappingTestCases(String name) throws Exception {
        super(name);
    }

    public void testSchemaGen() throws Exception {
        Schema generatedSchema = null;
        Document tDoc = null;
        Document cDoc = null;
        boolean exception = false;
        String msg = null;
        try {
            SchemaModelGeneratorProperties props = new SchemaModelGeneratorProperties();
            props.addProperty(MYNS, SchemaModelGeneratorProperties.ELEMENT_FORM_QUALIFIED_KEY, false);

            XMLContext xCtx = new XMLContext("org.eclipse.persistence.testing.oxm.schemamodelgenerator.mappings.pathbased", Thread.currentThread().getContextClassLoader());
            Project prj = xCtx.getSession(0).getProject();
            loginProject(prj);

            List<Descriptor> descriptorsToProcess = setupDescriptorList(prj);
            Map<String, Schema> generatedSchemas = sg.generateSchemas(descriptorsToProcess, props);

            generatedSchema = generatedSchemas.get(MYNS);
            assertNotNull("No schema was generated for namespace ["+MYNS+"]", generatedSchema);

            writeSchema(generatedSchema);

            tDoc = getDocument(generatedSchema);
            cDoc = getDocument(XSD_RESOURCE);

            assertNotNull("Schema to Document conversion failed", tDoc);
            assertNotNull("A problem occurred loading the control schema", cDoc);
            assertTrue("Schema comparsion failed", comparer.isSchemaEqual(cDoc, tDoc));

            SchemaFactory sFact = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);

            File schemaFile = new File(TMP_DIR, "generatedSchema.xsd");

            javax.xml.validation.Schema theSchema = sFact.newSchema(schemaFile);
            Validator validator = theSchema.newValidator();
            StreamSource ss = new StreamSource(new File(Thread.currentThread().getContextClassLoader().getResource(XML_RESOURCE).toURI()));
            validator.validate(ss);
    } catch (Exception ex) {
            exception = true;
            msg = ex.toString();
            ex.printStackTrace();
        }
        assertTrue("Schema validation failed unexpectedly: " + msg, exception==false);
    }

    public void testSchemaGenValidationFailure() throws Exception {
        Schema generatedSchema = null;
        Document tDoc = null;
        Document cDoc = null;
        boolean exception = false;
        try {
            SchemaModelGeneratorProperties props = new SchemaModelGeneratorProperties();
            props.addProperty(MYNS, SchemaModelGeneratorProperties.ELEMENT_FORM_QUALIFIED_KEY, false);

            XMLContext xCtx = new XMLContext("org.eclipse.persistence.testing.oxm.schemamodelgenerator.mappings.pathbased");
            Project prj = xCtx.getSession(0).getProject();
            loginProject(prj);

            List<Descriptor> descriptorsToProcess = setupDescriptorList(prj);
            Map<String, Schema> generatedSchemas = sg.generateSchemas(descriptorsToProcess, props);

            generatedSchema = generatedSchemas.get(MYNS);
            assertNotNull("No schema was generated for namespace ["+MYNS+"]", generatedSchema);

            writeSchema(generatedSchema);

            tDoc = getDocument(generatedSchema);
            cDoc = getDocument(XSD_RESOURCE);

            assertNotNull("Schema to Document conversion failed", tDoc);
            assertNotNull("A problem occurred loading the control schema", cDoc);
            assertTrue("Schema comparsion failed", comparer.isSchemaEqual(cDoc, tDoc));

            SchemaFactory sFact = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
            javax.xml.validation.Schema theSchema = sFact.newSchema(new File(TMP_DIR, "generatedSchema.xsd"));
            Validator validator = theSchema.newValidator();
            StreamSource ss = new StreamSource(new File(INVALID_XML_RESOURCE));
            validator.validate(ss);
        } catch (Exception ex) {
            exception = true;
        }
        assertTrue("Schema did not fail as expected: ", exception==true);
    }

    /**
     * Add the Customer, Address and PhoneNumber descriptors to the List of descriptors
     * to be processed by the schema model generator.
     *
     * @param prj
     * @return
     */
    private List<Descriptor> setupDescriptorList(Project prj) {
        List<Descriptor> descriptorsToProcess = new ArrayList<Descriptor>();
        descriptorsToProcess.add((Descriptor) prj.getDescriptorForAlias("Customer"));
        descriptorsToProcess.add((Descriptor) prj.getDescriptorForAlias("Address"));
        descriptorsToProcess.add((Descriptor) prj.getDescriptorForAlias("PhoneNumber"));
        return descriptorsToProcess;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.schemamodelgenerator.mappings.pathbased.PathbasedMappingTestCases" };
        junit.textui.TestRunner.main(arguments);
    }
}
