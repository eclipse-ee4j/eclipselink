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
* dmccann - Mar 2/2009 - 2.0 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.oxm.schemamodelgenerator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.descriptors.Namespace;
import org.eclipse.persistence.internal.oxm.schema.SchemaModelGenerator;
import org.eclipse.persistence.internal.oxm.schema.SchemaModelGeneratorProperties;
import org.eclipse.persistence.internal.oxm.schema.SchemaModelProject;
import org.eclipse.persistence.internal.oxm.schema.model.Schema;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLLogin;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.platform.SAXPlatform;
import org.eclipse.persistence.oxm.platform.XMLPlatform;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.jaxb.JAXBXMLComparer;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import junit.framework.TestCase;

/**
 * These tests are designed to test basic schema model generation functionality.  The
 * associated project (TestProject) has three descriptors - Employee, Address and 
 * PhoneNumber - that contain XMLDirect, XMLCompositeDirectCollection, 
 * XMLCompositeObject and XMLCompositeCollection mappings.  Different combinations of
 * element form default, schema context and default root element settings are tested.
 * Note that these tests will set the schema context and/or default root elements on
 * every descriptor, and NOT on a per descriptor basis.
 * 
 */
public class GenerateSingleSchemaTestCases extends GenerateSchemaTestCases {
    protected static final String MYNS = "myns:examplenamespace";
    protected static final String MYEMPTYNS = "";
    protected static final String ELEMENT_FORM_QUALIFIED_RESOURCE = "org/eclipse/persistence/testing/oxm/schemamodelgenerator/eltFrmQualified.xsd";
    protected static final String ELEMENT_FORM_QUALIFIED_NO_CTX_RESOURCE = "org/eclipse/persistence/testing/oxm/schemamodelgenerator/eltFrmQualifiedNoCtx.xsd";
    protected static final String ELEMENT_FORM_QUALIFIED_NO_DRE_RESOURCE = "org/eclipse/persistence/testing/oxm/schemamodelgenerator/eltFrmQualifiedNoDRE.xsd";
    protected static final String ELEMENT_FORM_UNQUALIFIED_RESOURCE = "org/eclipse/persistence/testing/oxm/schemamodelgenerator/eltFrmUnqualified.xsd";
    protected static final String ELEMENT_FORM_UNQUALIFIED_NO_CTX_RESOURCE = "org/eclipse/persistence/testing/oxm/schemamodelgenerator/eltFrmUnqualifiedNoCtx.xsd";
    protected static final String ELEMENT_FORM_UNQUALIFIED_NO_DRE_RESOURCE = "org/eclipse/persistence/testing/oxm/schemamodelgenerator/eltFrmUnqualifiedNoDRE.xsd";

    public GenerateSingleSchemaTestCases(String name) throws Exception {
        super(name);
    }

    /**
     * Test global complex type and global element generation.  Element form default
     * is qualified.
     * 
     * @throws Exception
     */
    public void testElementFormQualified() throws Exception {
        Schema generatedSchema = null;
        Document tDoc = null;
        Document cDoc = null;
        try {
            boolean setSchemaContext = true;
            boolean setDefaultRootElement = true;
            
            SchemaModelGeneratorProperties props = new SchemaModelGeneratorProperties();
            props.addProperty(MYNS, SchemaModelGeneratorProperties.ELEMENT_FORM_QUALIFIED_KEY, true);

            Project prj = new TestProject(setSchemaContext, setDefaultRootElement, MYNS);
            loginProject(prj);
            List<XMLDescriptor> descriptorsToProcess = setupDescriptorList(prj);
            Map<String, Schema> generatedSchemas = sg.generateSchemas(descriptorsToProcess, props);
            generatedSchema = generatedSchemas.get(MYNS);
            
            // debugging
            //writeSchema(generatedSchema);
            
            assertNotNull("No schema was generated for namespace ["+MYNS+"]", generatedSchema);

            tDoc = getDocument(generatedSchema);
            cDoc = getDocument(ELEMENT_FORM_QUALIFIED_RESOURCE);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail();
        }
        assertNotNull("Schema to Document conversion failed", tDoc);
        assertNotNull("A problem occurred loading the control schema", cDoc);
        assertTrue("Schema comparsion failed", comparer.isSchemaEqual(cDoc, tDoc));
    }
    
    /**
     * Test global element and anonymous complex type generation. Element form 
     * default is qualified.
     * 
     * @throws Exception
     */
    public void testElementFormQualifiedNoSchemaContext() throws Exception {
        Schema generatedSchema = null;
        Document tDoc = null;
        Document cDoc = null;
        try {
            boolean setSchemaContext = false;
            boolean setDefaultRootElement = true;
            
            SchemaModelGeneratorProperties props = new SchemaModelGeneratorProperties();
            props.addProperty(MYNS, SchemaModelGeneratorProperties.ELEMENT_FORM_QUALIFIED_KEY, true);

            Project prj = new TestProject(setSchemaContext, setDefaultRootElement, MYNS);
            loginProject(prj);
            List<XMLDescriptor> descriptorsToProcess = setupDescriptorList(prj);
            Map<String, Schema> generatedSchemas = sg.generateSchemas(descriptorsToProcess, props);
            generatedSchema = generatedSchemas.get(MYNS);
            assertNotNull("No schema was generated for namespace ["+MYNS+"]", generatedSchema);
            
            // debugging
            //writeSchema(generatedSchema);

            tDoc = getDocument(generatedSchema);
            cDoc = getDocument(ELEMENT_FORM_QUALIFIED_NO_CTX_RESOURCE);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail();
        }
        assertNotNull("Schema to Document conversion failed", tDoc);
        assertNotNull("A problem occurred loading the control schema", cDoc);
        assertTrue("Schema comparsion failed", comparer.isSchemaEqual(cDoc, tDoc));
    }

    /**
     * Test global complex type generation.  Element form default is qualified.
     * 
     * @throws Exception
     */
    public void testElementFormQualifiedNoDefaultRootElement() throws Exception {
        Schema generatedSchema = null;
        Document tDoc = null;
        Document cDoc = null;
        try {
            boolean setSchemaContext = true;
            boolean setDefaultRootElement = false;

            SchemaModelGeneratorProperties props = new SchemaModelGeneratorProperties();
            props.addProperty(MYNS, SchemaModelGeneratorProperties.ELEMENT_FORM_QUALIFIED_KEY, true);

            Project prj = new TestProject(setSchemaContext, setDefaultRootElement, MYNS);
            loginProject(prj);
            List<XMLDescriptor> descriptorsToProcess = setupDescriptorList(prj);
            Map<String, Schema> generatedSchemas = sg.generateSchemas(descriptorsToProcess, props);
            generatedSchema = generatedSchemas.get(MYNS);
            assertNotNull("No schema was generated for namespace ["+MYNS+"]", generatedSchema);
            
            // debugging
            //writeSchema(generatedSchema);

            tDoc = getDocument(generatedSchema);
            cDoc = getDocument(ELEMENT_FORM_QUALIFIED_NO_DRE_RESOURCE);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail();
        }
        assertNotNull("Schema to Document conversion failed", tDoc);
        assertNotNull("A problem occurred loading the control schema", cDoc);
        assertTrue("Schema comparsion failed", comparer.isSchemaEqual(cDoc, tDoc));
    }

    /**
     * No schema should be generated since there is no default root element
     * or schema context set on any descriptors
     */
    public void testElementFormQualifiedNoCtxNoDRE() throws Exception {
        Schema generatedSchema = null;
        try {
            boolean setSchemaContext = false;
            boolean setDefaultRootElement = false;

            SchemaModelGeneratorProperties props = new SchemaModelGeneratorProperties();
            props.addProperty(MYNS, SchemaModelGeneratorProperties.ELEMENT_FORM_QUALIFIED_KEY, false);
            
            Project prj = new TestProject(setSchemaContext, setDefaultRootElement, MYNS);
            loginProject(prj);
            List<XMLDescriptor> descriptorsToProcess = setupDescriptorList(prj);
            Map<String, Schema> generatedSchemas = sg.generateSchemas(descriptorsToProcess, props);
            generatedSchema = generatedSchemas.get(MYNS);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail();
        }
        assertNull("A schema should not have been generated", generatedSchema);
    }

    /**
     * Test global complex type and global element generation.  Element form default
     * is unqualified.  Element refs will be generated for local elements whose 
     * type is that of a global complex type.
     * 
     * @throws Exception
     */
    public void testElementFormUnqualified() throws Exception {
        Schema generatedSchema = null;
        Document tDoc = null;
        Document cDoc = null;
        try {
            boolean setSchemaContext = true;
            boolean setDefaultRootElement = true;
            Project prj = new TestProject(setSchemaContext, setDefaultRootElement);
            loginProject(prj);
            List<XMLDescriptor> descriptorsToProcess = setupDescriptorList(prj);

            SchemaModelGeneratorProperties props = new SchemaModelGeneratorProperties();
            props.addProperty(MYEMPTYNS, SchemaModelGeneratorProperties.ELEMENT_FORM_QUALIFIED_KEY, false);
            
            Map<String, Schema> generatedSchemas = sg.generateSchemas(descriptorsToProcess, props);
            generatedSchema = generatedSchemas.get(MYEMPTYNS);
            assertNotNull("No schema was generated for namespace ["+MYEMPTYNS+"]", generatedSchema);
            
            // debugging
            //writeSchema(generatedSchema);

            tDoc = getDocument(generatedSchema);
            cDoc = getDocument(ELEMENT_FORM_UNQUALIFIED_RESOURCE);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail();
        }
        assertNotNull("Schema to Document conversion failed", tDoc);
        assertNotNull("A problem occurred loading the control schema", cDoc);
        assertTrue("Schema comparsion failed", comparer.isSchemaEqual(cDoc, tDoc));
    }
    
    /**
     * Test global element generation.  Element form default is unqualified.   
     * Element refs will be generated for local elements - the global 
     * elements they refer to will have anonymous complex types generated.
     *  
     * @throws Exception
     */
    public void testElementFormUnqualifiedNoSchemaContext() throws Exception {
        Schema generatedSchema = null;
        Document tDoc = null;
        Document cDoc = null;
        try {
            boolean setSchemaContext = false;
            boolean setDefaultRootElement = true;

            SchemaModelGeneratorProperties props = new SchemaModelGeneratorProperties();
            props.addProperty(MYEMPTYNS, SchemaModelGeneratorProperties.ELEMENT_FORM_QUALIFIED_KEY, false);
            
            Project prj = new TestProject(setSchemaContext, setDefaultRootElement);
            loginProject(prj);
            List<XMLDescriptor> descriptorsToProcess = setupDescriptorList(prj);
            Map<String, Schema> generatedSchemas = sg.generateSchemas(descriptorsToProcess, props);
            generatedSchema = generatedSchemas.get(MYEMPTYNS);
            assertNotNull("No schema was generated for namespace ["+MYEMPTYNS+"]", generatedSchema);

            // debugging
            //writeSchema(generatedSchema);

            tDoc = getDocument(generatedSchema);
            cDoc = getDocument(ELEMENT_FORM_UNQUALIFIED_NO_CTX_RESOURCE);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail();
        }
        assertNotNull("Schema to Document conversion failed", tDoc);
        assertNotNull("A problem occurred loading the control schema", cDoc);
        assertTrue("Schema comparsion failed", comparer.isSchemaEqual(cDoc, tDoc));
    }

    /**
     * Test global complex type generation.  Element form default is unqualified.  
     * Element refs will be generated for local elements whose type is that of 
     * a global complex type.  In these cases, global elements will be 
     * generated for the refs.
     * 
     * @throws Exception
     */
    public void testElementFormUnqualifiedNoDefaultRootElement() throws Exception {
        Schema generatedSchema = null;
        Document tDoc = null;
        Document cDoc = null;
        try {
            boolean setSchemaContext = true;
            boolean setDefaultRootElement = false;

            SchemaModelGeneratorProperties props = new SchemaModelGeneratorProperties();
            props.addProperty(MYEMPTYNS, SchemaModelGeneratorProperties.ELEMENT_FORM_QUALIFIED_KEY, false);
            
            Project prj = new TestProject(setSchemaContext, setDefaultRootElement);
            loginProject(prj);
            List<XMLDescriptor> descriptorsToProcess = setupDescriptorList(prj);
            Map<String, Schema> generatedSchemas = sg.generateSchemas(descriptorsToProcess, props);
            generatedSchema = generatedSchemas.get(MYEMPTYNS);
            assertNotNull("No schema was generated for namespace ["+MYEMPTYNS+"]", generatedSchema);

            // debugging
            //writeSchema(generatedSchema);

            tDoc = getDocument(generatedSchema);
            cDoc = getDocument(ELEMENT_FORM_UNQUALIFIED_NO_DRE_RESOURCE);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail();
        }
        assertNotNull("Schema to Document conversion failed", tDoc);
        assertNotNull("A problem occurred loading the control schema", cDoc);
        assertTrue("Schema comparsion failed", comparer.isSchemaEqual(cDoc, tDoc));
    }

    /**
     * No schema should be generated since there is no default root element
     * or schema context set on any descriptors
     */
    public void testElementFormUnqualifiedNoCtxNoDRE() throws Exception {
        Schema generatedSchema = null;
        try {
            boolean setSchemaContext = false;
            boolean setDefaultRootElement = false;
            Project prj = new TestProject(setSchemaContext, setDefaultRootElement);
            loginProject(prj);
            List<XMLDescriptor> descriptorsToProcess = setupDescriptorList(prj);

            SchemaModelGeneratorProperties props = new SchemaModelGeneratorProperties();
            props.addProperty(MYEMPTYNS, SchemaModelGeneratorProperties.ELEMENT_FORM_QUALIFIED_KEY, false);
            
            Map<String, Schema> generatedSchemas = sg.generateSchemas(descriptorsToProcess, props);
            generatedSchema = generatedSchemas.get(MYEMPTYNS);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail();
        }
        assertNull("A schema should not have been generated", generatedSchema);
    }

    /**
     * Add the Employee, Address and PhoneNumber descriptors to the List of descriptors
     * to be processed by the schema model generator.
     * 
     * @param prj
     * @return
     */
    private List<XMLDescriptor> setupDescriptorList(Project prj) {
        List<XMLDescriptor> descriptorsToProcess = new ArrayList<XMLDescriptor>();
        descriptorsToProcess.add((XMLDescriptor) prj.getDescriptorForAlias("Employee"));
        descriptorsToProcess.add((XMLDescriptor) prj.getDescriptorForAlias("Address"));
        descriptorsToProcess.add((XMLDescriptor) prj.getDescriptorForAlias("PhoneNumber"));
        return descriptorsToProcess;
    }
}
