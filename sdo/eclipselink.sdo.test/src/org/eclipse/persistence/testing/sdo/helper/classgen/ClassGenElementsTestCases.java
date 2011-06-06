/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.sdo.helper.classgen;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.helper.SDOClassGenerator;
import org.eclipse.persistence.exceptions.SDOException;

public class ClassGenElementsTestCases extends SDOClassGenTestCases {
    private String srcFolder;
    private String controlSourceFolder;
    private List<String> controlFileNames;

    public ClassGenElementsTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.classgen.ClassGenElementsTestCases" };
        TestRunner.main(arguments);
    }

    public void setUp() {
        // schemaGen in the super will fail schema load  with a NPE that generates an empty xsdString for this suite - normal
    	super.setUp();
        classGenerator = new SDOClassGenerator(aHelperContext);
        controlFileNames = new ArrayList<String>();
    }

    public void testElementWithName() {
        String xsdSchemaName = "org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/elements/ElementWithName.xsd";
        srcFolder = "./elements/elementWithName";
        controlSourceFolder = "org/eclipse/persistence/testing/sdo/helper/classgen/elements/elementWithName/";

        controlFileNames.add("MyTestType.java");
        controlFileNames.add("MyTestTypeImpl.java");
        controlFileNames.add("ElementTest.java");
        controlFileNames.add("ElementTestImpl.java");
        runClassGenTest(xsdSchemaName, 2);

    }

    public void testElementWithSDO_Name() {
        String xsdSchemaName = "org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/elements/ElementWithSDO_Name.xsd";
        srcFolder = "./elements/elementWithSDO_Name";
        controlSourceFolder = "org/eclipse/persistence/testing/sdo/helper/classgen/elements/elementWithSDO_Name/";

        controlFileNames.add("MyTestType.java");
        controlFileNames.add("MyTestTypeImpl.java");
        controlFileNames.add("ElementTest.java");
        controlFileNames.add("ElementTestImpl.java");
        runClassGenTest(xsdSchemaName, 2);

    }

    public void testElementWithSDO_AliasName() {
        String xsdSchemaName = "org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/elements/ElementWithSDO_AlasName.xsd";
        srcFolder = "./elements/elementWithSDO_AliasName";
        controlSourceFolder = "org/eclipse/persistence/testing/sdo/helper/classgen/elements/elementWithSDO_AliasName/";
        controlFileNames.add("MyTestType.java");
        controlFileNames.add("MyTestTypeImpl.java");
        controlFileNames.add("ElementTest.java");
        controlFileNames.add("ElementTestImpl.java");
        runClassGenTest(xsdSchemaName, 2);

    }

    public void testElementWithReference() {
        String xsdSchemaName = "org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/elements/ElementWithReference.xsd";
        srcFolder = "./elements/elementWithReference";
        controlSourceFolder = "org/eclipse/persistence/testing/sdo/helper/classgen/elements/elementWithReference/";

        //controlFileNames.add("SDO_NAME.java");
        //controlFileNames.add("SDO_NAMEImpl.java");
        controlFileNames.add("MyTestType.java");
        controlFileNames.add("MyTestTypeImpl.java");
        runClassGenTest(xsdSchemaName, 1);

    }

    public void testElementWithMaxOccurance() {
        String xsdSchemaName = "org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/elements/ElementWithMaxOccurance.xsd";
        srcFolder = "./elements/elementWithMaxOccurance";
        controlSourceFolder = "org/eclipse/persistence/testing/sdo/helper/classgen/elements/elementWithMaxOccurance/";

        controlFileNames.add("MyTestType.java");
        controlFileNames.add("MyTestTypeImpl.java");
        controlFileNames.add("ElementTest.java");
        controlFileNames.add("ElementTestImpl.java");
        runClassGenTest(xsdSchemaName, 2);

    }

    public void testElementWithNillable() {
        String xsdSchemaName = "org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/elements/ElementWithNillable.xsd";
        srcFolder = "./elements/elementWithNillable";
        controlSourceFolder = "org/eclipse/persistence/testing/sdo/helper/classgen/elements/elementWithNillable/";

        controlFileNames.add("MyTestType.java");
        controlFileNames.add("MyTestTypeImpl.java");
        controlFileNames.add("ElementTest.java");
        controlFileNames.add("ElementTestImpl.java");
        runClassGenTest(xsdSchemaName, 2);

    }

    public void testElementWithSubstitution() {
        String xsdSchemaName = "org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/elements/ElementWithSubstitution.xsd";
        srcFolder = "./elements/elementWithSubstitution";
        controlSourceFolder = "org/eclipse/persistence/testing/sdo/helper/classgen/elements/elementWithSubstitution/";

        controlFileNames.add("MyTestType.java");
        controlFileNames.add("MyTestTypeImpl.java");
        controlFileNames.add("ElementTest.java");
        controlFileNames.add("ElementTestImpl.java");
        controlFileNames.add("MySubstitute.java");
        controlFileNames.add("MySubstituteImpl.java");
        runClassGenTest(xsdSchemaName, 3);

    }

    public void testElementWithName_Simple() {
        String xsdSchemaName = "org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/elements/ElementWithName_Simple.xsd";
        srcFolder = "./elements/elementWithName_Simple";
        controlSourceFolder = "org/eclipse/persistence/testing/sdo/helper/classgen/elements/elementWithName_Simple/";

        controlFileNames.add("MyTestType.java");
        controlFileNames.add("MyTestTypeImpl.java");
        runClassGenTest(xsdSchemaName, 1);

    }

    public void testElementWithDefault_Simple() {
        String xsdSchemaName = "org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/elements/ElementWithDefault_Simple.xsd";
        srcFolder = "./elements/elementWithDefault_Simple";
        controlSourceFolder = "org/eclipse/persistence/testing/sdo/helper/classgen/elements/elementWithDefault_Simple/";

        controlFileNames.add("MyTestType.java");
        controlFileNames.add("MyTestTypeImpl.java");
        runClassGenTest(xsdSchemaName, 1);

    }

    public void testElementWithFixed_Simple() {
        String xsdSchemaName = "org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/elements/ElementWithFixed_Simple.xsd";
        srcFolder = "./elements/elementWithFixed_Simple";
        controlSourceFolder = "org/eclipse/persistence/testing/sdo/helper/classgen/elements/elementWithFixed_Simple/";

        controlFileNames.add("MyTestType.java");
        controlFileNames.add("MyTestTypeImpl.java");
        runClassGenTest(xsdSchemaName, 1);

    }

    public void testElementWithSDOString_Simple() {
        String xsdSchemaName = "org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/elements/ElementWithSDOString_Simple.xsd";
        srcFolder = "./elements/elementWithSDOString_Simple";
        controlSourceFolder = "org/eclipse/persistence/testing/sdo/helper/classgen/elements/elementWithSDOString_Simple/";

        controlFileNames.add("MyTestType.java");
        controlFileNames.add("MyTestTypeImpl.java");
        runClassGenTest(xsdSchemaName, 1);

    }

    public void testElementWithSDOPropertyType_Simple() {
        String xsdSchemaName = "org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/elements/ElementWithSDOPropertyType_Simple.xsd";
        srcFolder = "./elements/elementWithSDOPropertyType_Simple";
        controlSourceFolder = "org/eclipse/persistence/testing/sdo/helper/classgen/elements/elementWithSDOPropertyType_Simple/";

        controlFileNames.add("MyTestType.java");
        controlFileNames.add("MyTestTypeImpl.java");
        controlFileNames.add("P_TYPE.java");
        controlFileNames.add("P_TYPEImpl.java");
        runClassGenTest(xsdSchemaName, 2);

    }

    public void testElementWithSDOOppositePro_Simple() {
        String xsdSchemaName = "org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/elements/ElementWithSDOOppositePro_Simple.xsd";
        srcFolder = "./elements/elementWithSDOOppositePro_Simple";
        controlSourceFolder = "org/eclipse/persistence/testing/sdo/helper/classgen/elements/elementWithSDOOppositePro_Simple/";

        controlFileNames.add("MyTestType.java");
        controlFileNames.add("MyTestTypeImpl.java");
        // JIRA-235: simple single type references: Spec sect 9.2 (1) oppositeType.dataType must be false
        try {            
            runClassGenTest(xsdSchemaName, 1);
            fail("An SDOException " + SDOException.CANNOT_SET_PROPERTY_TYPE_ANNOTATION_IF_TARGET_DATATYPE_TRUE //
            		+ " should have occurred but did not.");
        } catch (SDOException e) {            
            assertEquals(SDOException.CANNOT_SET_PROPERTY_TYPE_ANNOTATION_IF_TARGET_DATATYPE_TRUE ,e.getErrorCode());            
        }
    }

    public void testElementWithSDODataType() {
        String xsdSchemaName = "org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/elements/ElementWithSDODataType.xsd";
        srcFolder = "./elements/elementWithSDODataType";
        controlSourceFolder = "org/eclipse/persistence/testing/sdo/helper/classgen/elements/elementWithSDODataType/";

        controlFileNames.add("MyTestType.java");
        controlFileNames.add("MyTestTypeImpl.java");
        runClassGenTest(xsdSchemaName, 1);

    }

    public void testElementWithSDOChangeSummary() {
        String xsdSchemaName = "org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/elements/ElementWithSDOChangeSummary.xsd";
        srcFolder = "./elements/elementWithSDOChangeSummary";
        controlSourceFolder = "org/eclipse/persistence/testing/sdo/helper/classgen/elements/elementWithSDOChangeSummary/";

        controlFileNames.add("MyTestType.java");
        controlFileNames.add("MyTestTypeImpl.java");
        runClassGenTest(xsdSchemaName, 1);

    }

    protected String getSourceFolder() {
        return "elements";
    }

    protected String getControlSourceFolder() {
        return controlSourceFolder;
    }

    protected List<String> getControlFileNames() {
        return controlFileNames;
    }

    private void runClassGenTest(String xsdSchemaName, int expectedNumFiles) {
        String xsdSchema = getSchema(xsdSchemaName);
        xsdHelper.define(xsdSchema);

        StringReader reader = new StringReader(xsdSchema);
        classGenerator.generate(reader, srcFolder);

        int numGenerated = classGenerator.getGeneratedBuffers().size();
        assertEquals(expectedNumFiles, numGenerated);

        compareFiles(getControlFiles(), getGeneratedFiles(classGenerator.getGeneratedBuffers()));

    }

    // The following test case is out of scope for ClassGenElements - we let it fail with a NPE that generates an empty xsdString for this suite
    public void testClassGen() throws Exception {
    }

    protected String getSchemaName() {
        return null;
    }
}
