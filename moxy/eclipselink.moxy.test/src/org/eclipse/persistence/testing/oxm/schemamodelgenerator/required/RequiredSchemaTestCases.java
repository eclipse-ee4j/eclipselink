/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     rbarkhouse - 2009-08-13 13:49:00 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.oxm.schemamodelgenerator.required;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.internal.oxm.schema.SchemaModelGeneratorProperties;
import org.eclipse.persistence.internal.oxm.schema.model.Schema;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.schemamodelgenerator.GenerateSchemaTestCases;
import org.eclipse.persistence.testing.oxm.schemamodelgenerator.TestProject;
import org.w3c.dom.Document;

public class RequiredSchemaTestCases extends GenerateSchemaTestCases {

    protected static final String MYNS = "";
    protected static final String REQUIRED_RESOURCE = "org/eclipse/persistence/testing/oxm/schemamodelgenerator/required/required.xsd";
    protected static final String NON_REQUIRED_RESOURCE = "org/eclipse/persistence/testing/oxm/schemamodelgenerator/required/non-required.xsd";    

    public RequiredSchemaTestCases(String name) throws Exception {
        super(name);
    }

    public void testRequiredElements() throws Exception {
        runTest(true);
    }

    public void testNonRequiredElements() throws Exception {
        runTest(false);
    }

    public void runTest(boolean required) throws Exception {
        Schema generatedSchema = null;
        Document tDoc = null;
        Document cDoc = null;
        try {
            boolean setSchemaContext = true;
            boolean setDefaultRootElement = true;
            
            SchemaModelGeneratorProperties props = new SchemaModelGeneratorProperties();
            props.addProperty(MYNS, SchemaModelGeneratorProperties.ELEMENT_FORM_QUALIFIED_KEY, true);

            Project prj = new RequiredTestProject(required);
            loginProject(prj);
            List<XMLDescriptor> descriptorsToProcess = setupDescriptorList(prj);
            Map<String, Schema> generatedSchemas = sg.generateSchemas(descriptorsToProcess, props);
            generatedSchema = generatedSchemas.get(MYNS);

            // debugging
            // writeSchema(generatedSchema);

            assertNotNull("No schema was generated for namespace [" + MYNS + "]", generatedSchema);

            tDoc = getDocument(generatedSchema);
            if (required) {
                cDoc = getDocument(REQUIRED_RESOURCE);
            } else {
                cDoc = getDocument(NON_REQUIRED_RESOURCE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            fail();
        }
        assertNotNull("Schema to Document conversion failed", tDoc);
        assertNotNull("A problem occurred loading the control schema", cDoc);
        assertTrue("Schema comparsion failed", comparer.isSchemaEqual(cDoc, tDoc));        
    }
    
    private List<XMLDescriptor> setupDescriptorList(Project prj) {
        List<XMLDescriptor> descriptorsToProcess = new ArrayList<XMLDescriptor>();
        descriptorsToProcess.add((XMLDescriptor) prj.getDescriptorForAlias("RequiredTestObject"));
        descriptorsToProcess.add((XMLDescriptor) prj.getDescriptorForAlias("RequiredTestSubObject"));
        return descriptorsToProcess;
    }

}