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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.test.mappingsmodel.spi.meta;

import java.lang.reflect.Field;

import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassDescription;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassRepository;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalField;
import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;

import junit.framework.TestCase;



public abstract class ExternalFieldTests  extends TestCase {
    private ExternalClassRepository repository;


    private static final boolean JDK15 = jdkIsVersion("1.5");
    private static final boolean JDK16 = jdkIsVersion("1.6");

    private static boolean jdkIsVersion(String version) {
        return System.getProperty("java.version").indexOf(version) != -1;
    }

    protected ExternalFieldTests(String name) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.repository = this.buildRepository();
    }

    protected abstract ExternalClassRepository buildRepository();

    @Override
    protected void tearDown() throws Exception {
        TestTools.clear(this);
        super.tearDown();
    }

    public void testDeclaringClass() throws Exception {
        this.verifyDeclaringClass(java.util.Vector.class, "elementData");
    }

    private void verifyDeclaringClass(Class javaClass, String fieldName) throws Exception {
        Field javaField = SPIMetaTestTools.fieldNamed(javaClass, fieldName);
        ExternalField exField = SPIMetaTestTools.fieldNamed(this.exClassFor(javaClass), fieldName);
        assertEquals(javaField.getDeclaringClass().getName(), exField.getDeclaringClass().getName());
    }

    public void testModifiers() throws Exception {
        this.verifyModifiers(java.util.Vector.class, "elementData");
        this.verifyModifiers(java.util.Vector.class, "elementCount");
    }

    private void verifyModifiers(Class javaClass, String fieldName) throws Exception {
        Field javaField = SPIMetaTestTools.fieldNamed(javaClass, fieldName);
        ExternalField exField = SPIMetaTestTools.fieldNamed(this.exClassFor(javaClass), fieldName);
        assertEquals(javaField.getModifiers(), exField.getModifiers());
    }

    public void testType() throws Exception {
        this.verifyType(java.util.Vector.class, "elementData");
        this.verifyType(java.util.Vector.class, "elementCount");
    }

    private void verifyType(Class javaClass, String fieldName) throws Exception {
        Field javaField = SPIMetaTestTools.fieldNamed(javaClass, fieldName);
        ExternalField exField = SPIMetaTestTools.fieldNamed(this.exClassFor(javaClass), fieldName);
        assertEquals(javaField.getType().getName(), exField.getType().getName());
        assertEquals(ClassTools.arrayDepthFor(javaField.getType()), exField.getType().getArrayDepth());
    }

    public void testSynthetic() throws Exception {
        this.verifySynthetic(java.util.Vector.class, "elementData", false);

        // typically the Level class will have a synthetic variable, to hold the .class stuff;
        // the test used to look like this:
//        this.verifySynthetic(java.util.logging.Level.class, "class$0", true);
        // but now we do the following because the compiler-generated name is different
        // between the jdk compiler and the eclipse compiler  ~bjv
        // as of jdk 1.5 these are not generated as synthetic
    }

    private void verifySynthetic(Class javaClass, String fieldName, boolean expected) throws Exception {
        ExternalField exField = SPIMetaTestTools.fieldNamed(this.exClassFor(javaClass), fieldName);
        assertEquals(expected, exField.isSynthetic());
    }

    private ExternalClass exClassFor(Class javaClass) throws Exception {
        return this.descriptionFor(javaClass).getExternalClass();
    }

    private ExternalClassDescription descriptionFor(Class javaClass) {
        return this.descriptionForClassNamed(javaClass.getName());
    }

    private ExternalClassDescription descriptionForClassNamed(String className) {
        ExternalClassDescription[] descriptions = this.repository.getClassDescriptions();
        for (int i = descriptions.length; i-- > 0; ) {
            if (descriptions[i].getName().equals(className)) {
                return descriptions[i];
            }
        }
        throw new IllegalArgumentException(className);
    }

}
