/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     rbarkhouse - 2009-08-06 16:27:00 - initial implementation
package org.eclipse.persistence.testing.oxm.schemamodelgenerator.nillable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.internal.oxm.mappings.Descriptor;
import org.eclipse.persistence.internal.oxm.schema.SchemaModelGeneratorProperties;
import org.eclipse.persistence.internal.oxm.schema.model.Schema;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.schemamodelgenerator.GenerateSchemaTestCases;
import org.eclipse.persistence.testing.oxm.schemamodelgenerator.TestProject;
import org.w3c.dom.Document;

public class NillableSchemaTestCases extends GenerateSchemaTestCases {

    protected static final String MYNS = "";
    protected static final String NILLABLE_RESOURCE = "org/eclipse/persistence/testing/oxm/schemamodelgenerator/nillable/nillable.xsd";
    protected static final String NON_NILLABLE_RESOURCE = "org/eclipse/persistence/testing/oxm/schemamodelgenerator/nillable/non-nillable.xsd";

    public NillableSchemaTestCases(String name) throws Exception {
        super(name);
    }

    public void testNillableElements() throws Exception {
        runTest(true);
    }

    public void testNonNillableElements() throws Exception {
        runTest(false);
    }

    public void runTest(boolean nillable) throws Exception {
        Schema generatedSchema = null;
        Document tDoc = null;
        Document cDoc = null;
        try {
            boolean setSchemaContext = true;
            boolean setDefaultRootElement = true;

            SchemaModelGeneratorProperties props = new SchemaModelGeneratorProperties();
            props.addProperty(MYNS, SchemaModelGeneratorProperties.ELEMENT_FORM_QUALIFIED_KEY, true);

            Project prj = new NillableTestProject(nillable);
            loginProject(prj);
            List<Descriptor> descriptorsToProcess = setupDescriptorList(prj);
            Map<String, Schema> generatedSchemas = sg.generateSchemas(descriptorsToProcess, props);
            generatedSchema = generatedSchemas.get(MYNS);

            // debugging
            // writeSchema(generatedSchema);

            assertNotNull("No schema was generated for namespace [" + MYNS + "]", generatedSchema);

            tDoc = getDocument(generatedSchema);
            if (nillable) {
                cDoc = getDocument(NILLABLE_RESOURCE);
            } else {
                cDoc = getDocument(NON_NILLABLE_RESOURCE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            fail();
        }
        assertNotNull("Schema to Document conversion failed", tDoc);
        assertNotNull("A problem occurred loading the control schema", cDoc);
        assertTrue("Schema comparsion failed", comparer.isSchemaEqual(cDoc, tDoc));
    }

    private List<Descriptor> setupDescriptorList(Project prj) {
        List<Descriptor> descriptorsToProcess = new ArrayList<Descriptor>();
        descriptorsToProcess.add((Descriptor) prj.getDescriptorForAlias("NillableTestObject"));
        descriptorsToProcess.add((Descriptor) prj.getDescriptorForAlias("NillableTestSubObject"));
        return descriptorsToProcess;
    }

}
