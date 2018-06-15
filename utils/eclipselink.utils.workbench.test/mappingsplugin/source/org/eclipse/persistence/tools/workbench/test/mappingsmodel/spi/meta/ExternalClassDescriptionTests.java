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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.test.mappingsmodel.spi.meta;

import junit.framework.TestCase;

import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassDescription;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassRepository;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalMethod;
import org.eclipse.persistence.tools.workbench.test.utility.TestTools;




public abstract class ExternalClassDescriptionTests extends TestCase {
    private ExternalClassRepository repository;


    protected ExternalClassDescriptionTests(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        this.repository = this.buildRepository();
    }

    protected abstract ExternalClassRepository buildRepository();

    protected void tearDown() throws Exception {
        TestTools.clear(this);
        super.tearDown();
    }

    public void testAdditionalInfo() {
        ExternalClassDescription type = this.descriptionFor(java.lang.Object.class);
        String additionalInfo = type.getAdditionalInfo();
        assertTrue("bad additional info: " + additionalInfo, additionalInfo.indexOf("rt.jar") != -1);
    }

    public void testArrayDepth() throws Exception {
        ExternalClassDescription type = this.descriptionFor(java.lang.Object.class);
        assertEquals("array depth", 0, type.getArrayDepth());

        type = this.descriptionFor(java.lang.String.class);
        ExternalClass exClass = type.getExternalClass();
        ExternalMethod exMethod = SPIMetaTestTools.zeroArgumentMethodNamed(exClass, "getBytes");
        type = exMethod.getReturnType();
        assertEquals("array depth", 1, type.getArrayDepth());
    }

    public void testElementTypeName() throws Exception {
        ExternalClassDescription type = this.descriptionFor(java.lang.Object.class);
        assertEquals("element type name", java.lang.Object.class.getName(), type.getElementTypeName());

        type = this.descriptionFor(java.lang.String.class);
        ExternalClass exClass = type.getExternalClass();
        ExternalMethod exMethod = SPIMetaTestTools.zeroArgumentMethodNamed(exClass, "getBytes");
        type = exMethod.getReturnType();
        assertEquals("element type name", byte.class.getName(), type.getElementTypeName());
    }

    public void testExternalClass() throws Exception {
        ExternalClassDescription type = this.descriptionFor(java.lang.Object.class);
        ExternalClass exClass = type.getExternalClass();
        assertEquals("external class", java.lang.Object.class.getDeclaredMethods().length, exClass.getDeclaredMethods().length);

        type = this.descriptionFor(java.lang.String.class);
        exClass = type.getExternalClass();
        ExternalMethod exMethod = SPIMetaTestTools.zeroArgumentMethodNamed(exClass, "getBytes");
        type = exMethod.getReturnType();
        boolean exCaught = false;
        try {
            exClass = type.getExternalClass();
        } catch (IllegalStateException ex) {
            exCaught = true;
        }
        assertTrue("IllegalStateException not thrown", exCaught);
    }

    public void testSynthetic() throws Exception {
        ExternalClassDescription type = this.descriptionFor(java.lang.Object.class);
        assertTrue( ! type.isSynthetic());
        type = this.descriptionFor(java.util.Map.class);
        assertTrue( ! type.isSynthetic());
        type = this.descriptionFor(java.util.Map.Entry.class);
        assertTrue( ! type.isSynthetic());
        type = this.descriptionFor(Class.forName("java.util.Vector$1"));
        assertTrue(type.isSynthetic());
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
