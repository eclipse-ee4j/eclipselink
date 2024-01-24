/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.jpa.xml.merge.advanced;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.InheritancePolicy;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.sequencing.NativeSequence;
import org.eclipse.persistence.sequencing.Sequence;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.xml.merge.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa.xml.merge.advanced.Project;

import java.util.List;
import java.util.Map;

/**
 * JUnit test case(s) for merging xml with metadata-complete="true" and annotations.
 * See orm-annotation-merge-advanced-entity-mappings.xml
 * Values defined in xml often (but not always) prefixed with "XML_MERGE_"
 * Values defined in annotations often (but not always) prefixed with "ANN_MERGE_"
 * Currently no tables are created, so the testing is limited to examining descriptors.
 */
public class XmlMergeAdvancedTest extends JUnitTestCase {

    static String packageName = "org.eclipse.persistence.testing.models.jpa.xml.merge.advanced.";
    static String packageToCompareName = "org.eclipse.persistence.testing.models.jpa.advanced.";
    static String[] classNames = {"Address", "Employee", "EmploymentPeriod", "LargeProject", "PhoneNumber", "Project", "SmallProject"};

    public XmlMergeAdvancedTest() {
        super();
    }

    public XmlMergeAdvancedTest(String name) {
        super(name);
        setPuName(getPersistenceUnitName());
    }

    @Override
    public String getPersistenceUnitName() {
        return "xml-merge-advanced";
    }

    public static Test suite() {
        return new TestSuite(XmlMergeAdvancedTest.class, "Advanced Model");
    }

    public void testInheritanceDiscriminatorFieldValue() {
        InheritancePolicy projectInheritancePolicy = getPersistenceUnitServerSession().getDescriptor(Project.class).getInheritancePolicy();

        // defined in xml
        DatabaseField classIndicatorField = projectInheritancePolicy.getClassIndicatorField();
        String classIndicatorFieldName = classIndicatorField.getName();
        if(!classIndicatorFieldName.equals("XML_MERGE_PROJ_TYPE")) {
            fail("Wrong classIndicatorField name '"+classIndicatorFieldName+"'");
        }
        Class<?> classIndicatorFieldType = classIndicatorField.getType();
        if(!classIndicatorFieldType.equals(String.class)) {
            fail("Wrong classIndicatorField type '"+classIndicatorFieldType.getName()+"'");
        }
        // defined in xml
        String classIndicatorFieldTableName = classIndicatorField.getTableName();
        if(!classIndicatorFieldTableName.equals("CMP3_XML_MERGE_PROJECT")) {
            fail("Wrong classIndicatorField table '"+classIndicatorFieldTableName+"'");
        }

        @SuppressWarnings({"unchecked"})
        Map<String, String> classNameIndicators = projectInheritancePolicy.getClassNameIndicatorMapping();
        // defined in xml
        String projectIndicator = classNameIndicators.get(packageName + "Project");
        if(!projectIndicator.equals("XML_MERGE_P")) {
            fail("Wrong classIndicatorField value for Project '"+projectIndicator+"'");
        }
        // defaulted in annotations
        String smallProjectIndicator = classNameIndicators.get(packageName + "SmallProject");
        if(!smallProjectIndicator.equals("XMLMergeSmallProject")) {
            fail("Wrong classIndicatorField value for SmallProject '"+smallProjectIndicator+"'");
        }
        String largeProjectIndicator = classNameIndicators.get(packageName + "LargeProject");
        if(!largeProjectIndicator.equals("XMLMergeLargeProject")) {
            fail("Wrong classIndicatorField value for LargeProject '"+largeProjectIndicator+"'");
        }
    }

    // The test compares the mappings's types for each class with the corresponding class from packageToCompare.
    // The test should be altered accordingly in case the two classes are no longer use the same mappings types.
    public void testMappingsTypes() throws ClassNotFoundException {
        Map<Class<?>, ClassDescriptor> descriptors = getPersistenceUnitServerSession().getDescriptors();
        String errorMsg = "";
        for (int i=0; i<classNames.length; i++) {
            String classErrorMsg = "";
            String className = packageName + classNames[i];
            String classToCompareName = packageToCompareName + classNames[i];
            Class<?> cls = Class.forName(className);
            Class<?> clsToCompare = Class.forName(classToCompareName);
            ClassDescriptor desc = descriptors.get(cls);
            ClassDescriptor descToCompare = descriptors.get(clsToCompare);
            List<DatabaseMapping> mappings = desc.getMappings();
            List<DatabaseMapping> mappingsToCompare = descToCompare.getMappings();
            if(mappings.size() != mappingsToCompare.size()) {
                classErrorMsg = classErrorMsg +  "Number of mappings is different; ";
                continue;
            }
            for(int j=0; j<mappings.size(); j++) {
                DatabaseMapping mapping = mappings.get(j);
                String attributeName = mapping.getAttributeName();
                DatabaseMapping mappingToCompare = descToCompare.getMappingForAttributeName(attributeName);
                if(!mapping.getClass().equals(mappingToCompare.getClass())) {
                    classErrorMsg = classErrorMsg + "attribute "+attributeName+" - mappings of different types; ";
                }
            }
            if(classErrorMsg.length() > 0) {
                errorMsg = errorMsg + "class " + classNames[i] +": " + classErrorMsg;
            }
        }
        if(errorMsg.length() > 0) {
            fail(errorMsg);
        }
    }

    public void testIgnoredTransientAnnotation(){
        Map<Class<?>, ClassDescriptor> descriptors = getPersistenceUnitServerSession().getDescriptors();
        ClassDescriptor descriptor = descriptors.get(Employee.class);
        DatabaseMapping mapping = descriptor.getMappingForAttributeName("lastName");
        assertNotNull("No mapping for attribute that was set as @Transient, in a metadata complete entity.", mapping);
        assertTrue("Incorrect mapping for attribute that was set as @Transient, in a metadata complete entity.", mapping.isDirectToFieldMapping());
    }

    public void testSequenceGenerator(){
        Sequence sequence = getPersistenceUnitServerSession().getLogin().getSequence("ANN_MERGE_ADDRESS_SEQ");
        assertTrue("ANN_MERGE_ADDRESS_SEQ sequence incorrect.", sequence instanceof NativeSequence);
        assertEquals("ANN_MERGE_ADDRESS_SEQ incorrect allocation size.", 1, sequence.getPreallocationSize());
    }

    public static void main(String[] args) {
         junit.textui.TestRunner.run(XmlMergeAdvancedTest.suite());
    }
}
