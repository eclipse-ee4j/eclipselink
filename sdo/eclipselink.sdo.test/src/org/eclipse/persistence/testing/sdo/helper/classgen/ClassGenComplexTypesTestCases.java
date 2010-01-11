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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.sdo.helper.classgen;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import junit.textui.TestRunner;

import org.eclipse.persistence.sdo.helper.ClassBuffer;
import org.eclipse.persistence.sdo.helper.SDOClassGenerator;

public class ClassGenComplexTypesTestCases extends SDOClassGenTestCases {
    private String srcFolder;
    private String controlSourceFolder;
    private List<String> controlFileNames;

    public ClassGenComplexTypesTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.classgen.ClassGenComplexTypesTestCases" };
        TestRunner.main(arguments);
    }

    public void setUp() {
        super.setUp();
        classGenerator = new SDOClassGenerator(aHelperContext);
        controlFileNames = new ArrayList<String>();
    }

    public void testComplexTypeWithEmptyContent() {
        String xsdSchemaName = "org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/complextypes/ComplexTypeWithEmptyContent.xsd";
        srcFolder = "./complextypes/complexTypeWithEmptyContent";
        controlSourceFolder = "org/eclipse/persistence/testing/sdo/helper/classgen/complextypes/complexTypeWithEmptyContent/";

        controlFileNames.add("MyTestType.java");
        controlFileNames.add("MyTestTypeImpl.java");

        runClassGenTest(xsdSchemaName, 1);
    }

    public void testComplexTypeWithContent() {
        String xsdSchemaName = "org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/complextypes/ComplexTypeWithContent.xsd";
        srcFolder = "./complextypes/complexTypeWithContent";
        controlSourceFolder = "org/eclipse/persistence/testing/sdo/helper/classgen/complextypes/complexTypeWithContent/";

        controlFileNames.add("MyTestType.java");
        controlFileNames.add("MyTestTypeImpl.java");
        runClassGenTest(xsdSchemaName, 1);
    }

    public void testComplexTypeWithAnonymous() {
        String xsdSchemaName = "org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/complextypes/ComplexTypeWithAnonymous.xsd";
        srcFolder = "./complextypes/complexTypeWithAnonymous";
        controlSourceFolder = "org/eclipse/persistence/testing/sdo/helper/classgen/complextypes/complexTypeWithAnonymous/";

        controlFileNames.add("MyTestType.java");
        controlFileNames.add("MyTestTypeImpl.java");
        runClassGenTest(xsdSchemaName, 1);

    }

    public void testComplexTypeWithSDO_NAME() {
        String xsdSchemaName = "org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/complextypes/ComplexTypeWithSDO_NAME.xsd";

        srcFolder = "./complextypes/complexTypeWithSDO_NAME";
        controlSourceFolder = "org/eclipse/persistence/testing/sdo/helper/classgen/complextypes/complexTypeWithSDO_NAME/";

        controlFileNames.add("SDO_NAME.java");
        controlFileNames.add("SDO_NAMEImpl.java");
        runClassGenTest(xsdSchemaName, 1);
    }

    public void testComplexTypeWithAbstract() {
        String xsdSchemaName = "org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/complextypes/ComplexTypeWithAbstract.xsd";

        srcFolder = "./complextypes/complexTypeWithAbstract";
        controlSourceFolder = "org/eclipse/persistence/testing/sdo/helper/classgen/complextypes/complexTypeWithAbstract/";

        controlFileNames.add("MyTestType.java");
        controlFileNames.add("MyTestTypeImpl.java");
        runClassGenTest(xsdSchemaName, 1);
    }

    public void testComplexTypeWithAliasName() {
        String xsdSchemaName = "org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/complextypes/ComplexTypeWithAliasName.xsd";

        srcFolder = "./complextypes/complexTypeWithAliasName";
        controlSourceFolder = "org/eclipse/persistence/testing/sdo/helper/classgen/complextypes/complexTypeWithAliasName/";

        controlFileNames.add("MyTestType.java");
        controlFileNames.add("MyTestTypeImpl.java");
        runClassGenTest(xsdSchemaName, 1);
    }

    public void testComplexTypeExtendingComplexType() {
        String xsdSchemaName = "org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/complextypes/ComplexTypeEntendingComplexType.xsd";

        srcFolder = "./complextypes/complexTypeExtendingComplexType";
        controlSourceFolder = "org/eclipse/persistence/testing/sdo/helper/classgen/complextypes/complexTypeExtendingComplexType/";

        controlFileNames.add("MyTestType.java");
        controlFileNames.add("MyTestTypeImpl.java");
        controlFileNames.add("TestType.java");
        controlFileNames.add("TestTypeImpl.java");
        runClassGenTest(xsdSchemaName, 2);
    }

    public void testComplexTypeComplexContentRestrictingComplex() {
        String xsdSchemaName = "org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/complextypes/ComplexTypeComplexContentRestrictingComplex.xsd";

        srcFolder = "./complextypes/complexTypeComplexContentRestrictingComplex";
        controlSourceFolder = "org/eclipse/persistence/testing/sdo/helper/classgen/complextypes/complexTypeComplexContentRestrictingComplex/";

        controlFileNames.add("MyTestType.java");
        controlFileNames.add("MyTestTypeImpl.java");
        controlFileNames.add("TestType.java");
        controlFileNames.add("TestTypeImpl.java");
        runClassGenTest(xsdSchemaName, 2);
    }

    public void testComplexTypeSimpleContentRestrictingComplex() {
        String xsdSchemaName = "org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/complextypes/ComplexTypeSimpleContentRestrictingComplex.xsd";

        srcFolder = "./complextypes/complexTypeSimpleContentRestrictingComplex";
        controlSourceFolder = "org/eclipse/persistence/testing/sdo/helper/classgen/complextypes/complexTypeSimpleContentRestrictingComplex/";

        controlFileNames.add("MyTestType.java");
        controlFileNames.add("MyTestTypeImpl.java");
        controlFileNames.add("TestType.java");
        controlFileNames.add("TestTypeImpl.java");
        runClassGenTest(xsdSchemaName, 2);
    }

    public void testComplexTypeWithMixedContent() {
        String xsdSchemaName = "org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/complextypes/ComplexTypeWithMixedContent.xsd";

        srcFolder = "./complextypes/complexTypeWithMixedContent";
        controlSourceFolder = "org/eclipse/persistence/testing/sdo/helper/classgen/complextypes/complexTypeWithMixedContent/";

        controlFileNames.add("MyTestType.java");
        controlFileNames.add("MyTestTypeImpl.java");
        runClassGenTest(xsdSchemaName, 1);
    }

    public void testComplexTypeWithSDOSequence() {
        String xsdSchemaName = "org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/complextypes/ComplexTypeWithSDOSequence.xsd";

        srcFolder = "./complextypes/complexTypeWithSDOSequence";
        controlSourceFolder = "org/eclipse/persistence/testing/sdo/helper/classgen/complextypes/complexTypeWithSDOSequence/";

        controlFileNames.add("MyTestType.java");
        controlFileNames.add("MyTestTypeImpl.java");
        runClassGenTest(xsdSchemaName, 1);
    }

    public void testComplexTypeExtendingSimpleType() {
        String xsdSchemaName = "org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/complextypes/ComplexTypeEntendingSimpleType.xsd";

        srcFolder = "./complextypes/complexTypeExtendingSimpleType";
        controlSourceFolder = "org/eclipse/persistence/testing/sdo/helper/classgen/complextypes/complexTypeExtendingSimpleType/";

        controlFileNames.add("MyTestType.java");
        controlFileNames.add("MyTestTypeImpl.java");
        runClassGenTest(xsdSchemaName, 1);
    }

    public void testComplexTypeWithOpenContent() {
        String xsdSchemaName = "org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/complextypes/ComplexTypeWithOpenContent.xsd";

        srcFolder = "./complextypes/complexTypeWithOpenContent";
        controlSourceFolder = "org/eclipse/persistence/testing/sdo/helper/classgen/complextypes/complexTypeWithOpenContent/";

        controlFileNames.add("MyTestType.java");
        controlFileNames.add("MyTestTypeImpl.java");
        runClassGenTest(xsdSchemaName, 1);
    }

    public void testComplexTypeWithOpenAttributes() {
        String xsdSchemaName = "org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/complextypes/ComplexTypeWithOpenAttributes.xsd";

        srcFolder = "./complextypes/complexTypeWithOpenAttributes";
        controlSourceFolder = "org/eclipse/persistence/testing/sdo/helper/classgen/complextypes/complexTypeWithOpenAttributes/";

        controlFileNames.add("MyTestType.java");
        controlFileNames.add("MyTestTypeImpl.java");
        runClassGenTest(xsdSchemaName, 1);
    }

    protected String getControlSourceFolder() {
        return controlSourceFolder;
    }

    protected String getSourceFolder() {
        return "complextypes";
    }

    protected List<String> getControlFileNames() {
        return controlFileNames;
    }

    private void runClassGenTest(String xsdSchemaName, int expectedNumFiles) {
        String xsdSchema = getSchema(xsdSchemaName);
        xsdHelper.define(xsdSchema);

        StringReader reader = new StringReader(xsdSchema);
        Map<Object, ClassBuffer> generatedMap = classGenerator.generate(reader, srcFolder);

        int numGenerated = classGenerator.getGeneratedBuffers().size();
        assertEquals(expectedNumFiles, numGenerated);
        assertEquals(expectedNumFiles, generatedMap.size());

        compareFiles(getControlFiles(), getGeneratedFiles(classGenerator.getGeneratedBuffers()));

    }

    public void testClassGen() throws Exception {
    }

    protected String getSchemaName() {
        return null;
    }
}
