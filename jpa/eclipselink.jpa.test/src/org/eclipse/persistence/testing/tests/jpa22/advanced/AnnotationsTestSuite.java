/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.persistence.testing.tests.jpa22.advanced;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Convert;
import javax.persistence.Converts;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.MapKeyJoinColumns;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.NamedStoredProcedureQueries;
import javax.persistence.NamedStoredProcedureQuery;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContexts;
import javax.persistence.PersistenceUnit;
import javax.persistence.PersistenceUnits;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.PrimaryKeyJoinColumns;
import javax.persistence.SecondaryTable;
import javax.persistence.SecondaryTables;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotatedElement;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAsmFactory;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa22.advanced.Department;
import org.eclipse.persistence.testing.models.jpa22.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa22.advanced.PhoneNumber;
import org.eclipse.persistence.testing.models.jpa22.advanced.Race;
import org.eclipse.persistence.testing.models.jpa22.advanced.Runner;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;

import static org.eclipse.persistence.testing.tests.jpa22.advanced.AnnotationsTestSuite.Access.FIELD;
import static org.eclipse.persistence.testing.tests.jpa22.advanced.AnnotationsTestSuite.Access.PROPERTY;

@PersistenceContext(name = "MulitPU-1", unitName = "MulitPU-1")
@PersistenceContext(name = "MulitPU-2", unitName = "MulitPU-2")
@PersistenceContext(name = "MulitPU-3", unitName = "MulitPU-3")
@PersistenceUnit(name = "MulitPU-4", unitName = "MulitPU-4")
@PersistenceUnit(name = "MulitPU-5", unitName = "MulitPU-5")
public class AnnotationsTestSuite extends JUnitTestCase {

    public AnnotationsTestSuite() {
    }

    public AnnotationsTestSuite(String name) {
        super(name);
        setPuName("MulitPU-1");
    }

    /**
     * Return the the persistence unit name for this test suite.
     */
    @Override
    public String getPersistenceUnitName() {
        return "MulitPU-1";
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("AnnotationsTestSuite");

        suite.addTest(new AnnotationsTestSuite("testRepeatingAnnotations"));

        return suite;
    }

    /**
     * Verify that repeating annotations work as expected and are backwards
     * compatible.
     */
    public void testRepeatingAnnotations() throws Exception {

        // @AssociationOverride
        verifyContainerAnnotation(Employee.class, PROPERTY, "getPeriod", AssociationOverrides.class, 2);
        verifyAnnotation(Employee.class, PROPERTY, "getPeriod", AssociationOverride.class, 2);
        verifyMetadata(Employee.class, PROPERTY, "period", "javax.persistence.AssociationOverrides", 2);


        // @AttributeOverride
        verifyContainerAnnotation(Employee.class, PROPERTY, "getPeriod", AttributeOverrides.class, 2);
        verifyAnnotation(Employee.class, PROPERTY, "getPeriod", AttributeOverride.class, 2);
        verifyMetadata(Employee.class, PROPERTY, "period", "javax.persistence.AttributeOverrides", 2);

        // @Convert
        verifyContainerAnnotation(Race.class, FIELD, "organizers", Converts.class, 2);
        verifyAnnotation(Race.class, FIELD, "organizers", Convert.class, 2);
        verifyMetadata(Race.class, FIELD, "organizers", "javax.persistence.Converts", 2);

        // @JoinColumn
        verifyContainerAnnotation(Employee.class, PROPERTY, "getPhoneNumbers", JoinColumns.class, 2);
        verifyAnnotation(Employee.class, PROPERTY, "getPhoneNumbers", JoinColumn.class, 2);
        verifyMetadata(Employee.class, PROPERTY, "phoneNumbers", "javax.persistence.JoinColumns", 2);

        // @MapKeyJoinColumn
        verifyContainerAnnotation(Department.class, PROPERTY, "getEmployeesByPhoneNumber", MapKeyJoinColumns.class, 2);
        verifyAnnotation(Department.class, PROPERTY, "getEmployeesByPhoneNumber", MapKeyJoinColumn.class, 2);
        verifyMetadata(Department.class, PROPERTY, "employeesByPhoneNumber", "javax.persistence.MapKeyJoinColumns", 2);

        // @NamedEntityGraph
        verifyContainerAnnotation(Employee.class, NamedEntityGraphs.class, 2);
        verifyAnnotation(Employee.class, NamedEntityGraph.class, 2);
        verifyMetadata(Employee.class, "javax.persistence.NamedEntityGraphs", 2);

        // @NamedNativeQuery
        verifyContainerAnnotation(Runner.class, NamedNativeQueries.class, 2);
        verifyAnnotation(Runner.class, NamedNativeQuery.class, 2);
        verifyMetadata(Runner.class, "javax.persistence.NamedNativeQueries", 2);

        // @NamedQuery
        verifyContainerAnnotation(Employee.class, NamedQueries.class, 2);
        verifyAnnotation(Employee.class, NamedQuery.class, 2);
        verifyMetadata(Employee.class, "javax.persistence.NamedQueries", 2);

        // @NamedStoredProcedureQuery
        verifyContainerAnnotation(Employee.class, NamedStoredProcedureQueries.class, 4);
        verifyAnnotation(Employee.class, NamedStoredProcedureQuery.class, 4);
        verifyMetadata(Employee.class, "javax.persistence.NamedStoredProcedureQueries", 4);

        // @PersistenceContext
        verifyContainerAnnotation(AnnotationsTestSuite.class, PersistenceContexts.class, 3);
        verifyAnnotation(AnnotationsTestSuite.class, PersistenceContext.class, 3);
        verifyMetadata(AnnotationsTestSuite.class, "javax.persistence.PersistenceContexts", 3);

        // @PersistenceUnit
        verifyContainerAnnotation(AnnotationsTestSuite.class, PersistenceUnits.class, 2);
        verifyAnnotation(AnnotationsTestSuite.class, PersistenceUnit.class, 2);
        verifyMetadata(AnnotationsTestSuite.class, "javax.persistence.PersistenceUnits", 2);

        // @PrimaryKeyJoinColumn
        verifyContainerAnnotation(PhoneNumber.class, PROPERTY, "getPhoneNumberDetails", PrimaryKeyJoinColumns.class, 2);
        verifyAnnotation(PhoneNumber.class, PROPERTY, "getPhoneNumberDetails", PrimaryKeyJoinColumn.class, 2);
        verifyMetadata(PhoneNumber.class, PROPERTY, "phoneNumberDetails", "javax.persistence.PrimaryKeyJoinColumns", 2);

        // @SecondaryTable
        verifyContainerAnnotation(Employee.class, SecondaryTables.class, 2);
        verifyAnnotation(Employee.class, SecondaryTable.class, 2);
        verifyMetadata(Employee.class, "javax.persistence.SecondaryTables", 2);

        // @SqlResultSetMapping
        verifyContainerAnnotation(Employee.class, SqlResultSetMappings.class, 2);
        verifyAnnotation(Employee.class, SqlResultSetMapping.class, 2);
        verifyMetadata(Employee.class, "javax.persistence.SqlResultSetMappings", 2);
    }

    private <T, A extends Annotation> void verifyContainerAnnotation(Class<T> classToCheck, Access accessType, String elementName, Class<A> annotationClass, int occurrences) throws Exception {
        AccessibleObject element = accessType == FIELD ? classToCheck.getDeclaredField(elementName) : classToCheck.getDeclaredMethod(elementName);
        A annotation = element.getAnnotation(annotationClass);
        assertNotNull("No " + annotationClass.getName() + " found for " + (accessType == FIELD ? "field" : "method") + " " + elementName + " of " + classToCheck.getName() + " class", annotation);
        assertEquals("Wrong " + annotationClass.getName() + " number found for " + (accessType == FIELD ? "field" : "method") + " " + elementName + " of " + classToCheck.getName() + " class", occurrences, ((Annotation[]) annotationClass.getMethod("value").invoke(annotation)).length);
    }

    private <T, A extends Annotation> void verifyContainerAnnotation(Class<T> classToCheck, Class<A> annotationClass, int occurrences) throws Exception {
        A annotation = classToCheck.getAnnotation(annotationClass);
        assertNotNull("No " + annotationClass.getName() + " found for " + classToCheck.getName() + " class", annotation);
        assertEquals("Wrong " + annotationClass.getName() + " number found for " + classToCheck.getName() + " class", occurrences, ((Annotation[]) annotationClass.getMethod("value").invoke(annotation)).length);
    }

    private <T, A extends Annotation> void verifyAnnotation(Class<T> classToCheck, Access accessType, String elementName, Class<A> annotationClass, int occurrences) throws Exception {
        AccessibleObject element = accessType == FIELD ? classToCheck.getDeclaredField(elementName) : classToCheck.getDeclaredMethod(elementName);
        A[] annotations = element.getAnnotationsByType(annotationClass);
        assertEquals("Wrong " + annotationClass.getName() + " annotations number found for " + (accessType == FIELD ? "field" : "method") + " " + elementName + " of " + classToCheck.getName() + " class", occurrences, annotations.length);
    }

    private <T, A extends Annotation> void verifyAnnotation(Class<T> classToCheck, Class<A> annotationClass, int occurrences) throws Exception {
        A[] annotations = classToCheck.getAnnotationsByType(annotationClass);
        assertEquals("Wrong " + annotationClass.getName() + " annotations number found for " + classToCheck.getName() + " class", occurrences, annotations.length);
    }

    private <T> void verifyMetadata(Class<T> classToCheck, Access accessType, String fieldName, String annotationToVerify, int occurrences) {
        MetadataAsmFactory fact = new MetadataAsmFactory(new MetadataLogger(null), this.getClass().getClassLoader());
        MetadataClass metadataClass = fact.getMetadataClass(classToCheck.getName());
        MetadataAnnotatedElement annotatedElement = accessType == FIELD ? metadataClass.getField(fieldName) : metadataClass.getMethodForPropertyName(fieldName);
        MetadataAnnotation annotation = annotatedElement.getAnnotation(annotationToVerify);
        assertNotNull("Could not get " + annotationToVerify + " metadata for " + annotatedElement.getName() + (accessType == FIELD ? "field" : "method") + " of " + classToCheck + " class", annotation);
        assertEquals("Wrong " + annotationToVerify + " metadata values number for " + annotatedElement.getName() + (accessType == FIELD ? "field" : "method") + " of " + classToCheck + " class", occurrences, annotation.getAttributeArray("value").length);
    }

    private <T> void verifyMetadata(Class<T> classToCheck, String annotationToVerify, int occurrences) {
        MetadataAsmFactory fact = new MetadataAsmFactory(new MetadataLogger(null), this.getClass().getClassLoader());
        MetadataClass metadataClass = fact.getMetadataClass(classToCheck.getName());
        MetadataAnnotation annotation = metadataClass.getAnnotation(annotationToVerify);
        assertNotNull("Could not get " + annotationToVerify + " metadata for " + classToCheck + " class", annotation);
        assertEquals("Wrong " + annotationToVerify + " metadata values number for " + classToCheck + " class", occurrences, annotation.getAttributeArray("value").length);
    }

    protected enum Access {FIELD, PROPERTY, CLASS}

}
