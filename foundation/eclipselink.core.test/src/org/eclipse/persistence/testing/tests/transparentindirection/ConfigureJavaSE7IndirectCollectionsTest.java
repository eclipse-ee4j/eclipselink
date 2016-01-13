/*******************************************************************************
 * Copyright (c) 1998, 2016 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     dminsky - initial API and implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.transparentindirection;

import org.eclipse.persistence.config.SystemProperties;
import org.eclipse.persistence.indirection.IndirectCollectionsFactory;
import org.eclipse.persistence.testing.framework.TestCase;;

/**
 * Bug 485585 - Configure instantiation of Java SE 7 indirect collection classes in Java SE 8
 * 
 * This test validates the expected behavior of setting the following system property to true: 
 * eclipselink.indirection.javase7-indirect-collections property
 */
public class ConfigureJavaSE7IndirectCollectionsTest extends TestCase {
    
    public ConfigureJavaSE7IndirectCollectionsTest() {
        super();
        setDescription("Test the configuration of Java SE 7 indirect collection classes in Java SE 8");
    }
    
    @Override
    public void setup() throws Throwable {
        // this tests depends on the eclipselink.indirection.javase7-indirect-collections property being set to true
        String propertyName = SystemProperties.JAVASE7_INDIRECT_COLLECTIONS;
        String propertyValue = System.getProperty(propertyName);
        // simply warn on inability to run, do not fail
        if (propertyValue == null) {
            throwWarning("System property: [" + propertyName + "] was not configured");
        } else if (Boolean.valueOf(propertyValue) == false) {
            throwWarning("System property: [" + propertyName + "] value: [" + propertyValue + "] was false or is invalid");
        }
    }
    
    @Override
    public void test() throws Throwable {
        assertSame("IndirectCollectionsFactory IndirectList class should be org.eclipse.persistence.indirection.IndirectList",
                org.eclipse.persistence.indirection.IndirectList.class, 
                IndirectCollectionsFactory.IndirectList_Class);
        assertSame("IndirectCollectionsFactory IndirectSet class should be org.eclipse.persistence.indirection.IndirectSet", 
                org.eclipse.persistence.indirection.IndirectSet.class, 
                IndirectCollectionsFactory.IndirectSet_Class);
        assertSame("IndirectCollectionsFactory IndirectMap class should be org.eclipse.persistence.indirection.IndirectMap", 
                org.eclipse.persistence.indirection.IndirectMap.class, 
                IndirectCollectionsFactory.IndirectMap_Class);
    }

}
