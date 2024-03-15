/*
 * Copyright (c) 2015, 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.testing.tests.advanced2;

import jakarta.persistence.AssociationOverride;
import jakarta.persistence.AssociationOverrides;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Convert;
import jakarta.persistence.Converts;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.MapKeyJoinColumn;
import jakarta.persistence.MapKeyJoinColumns;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedEntityGraphs;
import jakarta.persistence.NamedNativeQueries;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.NamedStoredProcedureQueries;
import jakarta.persistence.NamedStoredProcedureQuery;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceContexts;
import jakarta.persistence.PersistenceUnit;
import jakarta.persistence.PersistenceUnits;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.PrimaryKeyJoinColumns;
import jakarta.persistence.SecondaryTable;
import jakarta.persistence.SecondaryTables;
import jakarta.persistence.SqlResultSetMapping;
import jakarta.persistence.SqlResultSetMappings;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotatedElement;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAsmFactory;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa21.advanced.Department;
import org.eclipse.persistence.testing.models.jpa21.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa21.advanced.PhoneNumber;
import org.eclipse.persistence.testing.models.jpa21.advanced.Race;
import org.eclipse.persistence.testing.models.jpa21.advanced.Runner;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;

import static org.eclipse.persistence.testing.tests.advanced2.AnnotationsTest.Access.FIELD;
import static org.eclipse.persistence.testing.tests.advanced2.AnnotationsTest.Access.PROPERTY;

@PersistenceContext(name = "advanced2x", unitName = "advanced2x")
@PersistenceContext(name = "advanced2x-ddl", unitName = "advanced2x-ddl")
@PersistenceContext(name = "advanced2x-xml-ddl", unitName = "advanced2x-xml-ddl")
@PersistenceUnit(name = "advanced2x-xml", unitName = "advanced2x-xml")
@PersistenceUnit(name = "advanced2x-ddl-template", unitName = "advanced2x-ddl-template")
public class AnnotationsTest extends JUnitTestCase {

    public AnnotationsTest() {
    }

    public AnnotationsTest(String name) {
        super(name);
        setPuName(getPersistenceUnitName());
    }

    /**
     * Return the the persistence unit name for this test suite.
     */
    @Override
    public String getPersistenceUnitName() {
        return "advanced2x";
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("AnnotationsTest");

        suite.addTest(new AnnotationsTest("testRepeatingAnnotations"));

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
        verifyMetadata(Employee.class, PROPERTY, "period", "jakarta.persistence.AssociationOverrides", 2);


        // @AttributeOverride
        verifyContainerAnnotation(Employee.class, PROPERTY, "getPeriod", AttributeOverrides.class, 2);
        verifyAnnotation(Employee.class, PROPERTY, "getPeriod", AttributeOverride.class, 2);
        verifyMetadata(Employee.class, PROPERTY, "period", "jakarta.persistence.AttributeOverrides", 2);

        // @Convert
        verifyContainerAnnotation(Race.class, FIELD, "organizers", Converts.class, 2);
        verifyAnnotation(Race.class, FIELD, "organizers", Convert.class, 2);
        verifyMetadata(Race.class, FIELD, "organizers", "jakarta.persistence.Converts", 2);

        // @JoinColumn
        verifyContainerAnnotation(Employee.class, PROPERTY, "getPhoneNumbers", JoinColumns.class, 2);
        verifyAnnotation(Employee.class, PROPERTY, "getPhoneNumbers", JoinColumn.class, 2);
        verifyMetadata(Employee.class, PROPERTY, "phoneNumbers", "jakarta.persistence.JoinColumns", 2);

        // @MapKeyJoinColumn
        verifyContainerAnnotation(Department.class, PROPERTY, "getEmployeesByPhoneNumber", MapKeyJoinColumns.class, 2);
        verifyAnnotation(Department.class, PROPERTY, "getEmployeesByPhoneNumber", MapKeyJoinColumn.class, 2);
        verifyMetadata(Department.class, PROPERTY, "employeesByPhoneNumber", "jakarta.persistence.MapKeyJoinColumns", 2);

        // @NamedEntityGraph
        verifyContainerAnnotation(Employee.class, NamedEntityGraphs.class, 2);
        verifyAnnotation(Employee.class, NamedEntityGraph.class, 2);
        verifyMetadata(Employee.class, "jakarta.persistence.NamedEntityGraphs", 2);

        // @NamedNativeQuery
        verifyContainerAnnotation(Runner.class, NamedNativeQueries.class, 2);
        verifyAnnotation(Runner.class, NamedNativeQuery.class, 2);
        verifyMetadata(Runner.class, "jakarta.persistence.NamedNativeQueries", 2);

        // @NamedQuery
        verifyContainerAnnotation(Employee.class, NamedQueries.class, 2);
        verifyAnnotation(Employee.class, NamedQuery.class, 2);
        verifyMetadata(Employee.class, "jakarta.persistence.NamedQueries", 2);

        // @NamedStoredProcedureQuery
        verifyContainerAnnotation(Employee.class, NamedStoredProcedureQueries.class, 5);
        verifyAnnotation(Employee.class, NamedStoredProcedureQuery.class, 5);
        verifyMetadata(Employee.class, "jakarta.persistence.NamedStoredProcedureQueries", 5);

        // @PersistenceContext
        verifyContainerAnnotation(AnnotationsTest.class, PersistenceContexts.class, 3);
        verifyAnnotation(AnnotationsTest.class, PersistenceContext.class, 3);
        verifyMetadata(AnnotationsTest.class, "jakarta.persistence.PersistenceContexts", 3);

        // @PersistenceUnit
        verifyContainerAnnotation(AnnotationsTest.class, PersistenceUnits.class, 2);
        verifyAnnotation(AnnotationsTest.class, PersistenceUnit.class, 2);
        verifyMetadata(AnnotationsTest.class, "jakarta.persistence.PersistenceUnits", 2);

        // @PrimaryKeyJoinColumn
        verifyContainerAnnotation(PhoneNumber.class, PROPERTY, "getPhoneNumberDetails", PrimaryKeyJoinColumns.class, 2);
        verifyAnnotation(PhoneNumber.class, PROPERTY, "getPhoneNumberDetails", PrimaryKeyJoinColumn.class, 2);
        verifyMetadata(PhoneNumber.class, PROPERTY, "phoneNumberDetails", "jakarta.persistence.PrimaryKeyJoinColumns", 2);

        // @SecondaryTable
        verifyContainerAnnotation(Employee.class, SecondaryTables.class, 2);
        verifyAnnotation(Employee.class, SecondaryTable.class, 2);
        verifyMetadata(Employee.class, "jakarta.persistence.SecondaryTables", 2);

        // @SqlResultSetMapping
        verifyContainerAnnotation(Employee.class, SqlResultSetMappings.class, 2);
        verifyAnnotation(Employee.class, SqlResultSetMapping.class, 2);
        verifyMetadata(Employee.class, "jakarta.persistence.SqlResultSetMappings", 2);
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

    private <T, A extends Annotation> void verifyAnnotation(Class<T> classToCheck, Class<A> annotationClass, int occurrences) {
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
