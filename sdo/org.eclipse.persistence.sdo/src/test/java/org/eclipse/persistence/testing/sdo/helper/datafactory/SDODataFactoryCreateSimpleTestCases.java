/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.sdo.helper.datafactory;

import commonj.sdo.DataObject;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.helper.SDOTypeHelper;
import org.eclipse.persistence.testing.sdo.helper.xmlhelper.SDOXMLHelperTestCases;
import org.eclipse.persistence.exceptions.SDOException;

public class SDODataFactoryCreateSimpleTestCases extends SDOXMLHelperTestCases {
    protected SDOType simpleType;

    public SDODataFactoryCreateSimpleTestCases(String name) {
        super(name);
    }

    public void setUp() {
        super.setUp();
        simpleType = getSDOType();
    }

    public void testCreateByType() {
        DataObject dataObject = dataFactory.create(simpleType);
        assertEquals(simpleType, dataObject.getType());
    }

    public void testCreateByClass() {
        DataObject dataObject = dataFactory.create(USAddress.class);
        assertEquals(simpleType, dataObject.getType());
    }

    public void testCreateByInvalidClass() {
        Class oldClass = simpleType.getInstanceClass();
        simpleType.setInstanceClass(String.class);
        try {
            DataObject dataObject = dataFactory.create(simpleType);
            fail("An SDOException should have occurred but didn't");
        } catch (SDOException e) {
            assertEquals(SDOException.CLASS_NOT_FOUND ,e.getErrorCode());
        } finally {
            simpleType.setInstanceClass(oldClass);
        }
    }

    public void testCreateByNullClass() {
        Class oldClass = simpleType.getInstanceClass();
        String oldClassName = simpleType.getInstanceClassName();
        simpleType.setInstanceClass(null);
        simpleType.setInstanceClassName(null);
        try {
            DataObject dataObject = dataFactory.create(USAddress.class);
            fail("An exception should have occurred but didn't");
        } catch (IllegalArgumentException e) {

        } finally {
            simpleType.setInstanceClass(oldClass);
            simpleType.setInstanceClassName(oldClassName);
        }
    }

    private SDOType getSDOType() {
        SDOType type = new SDOType("http://testing", "USAddress");
        type.setAbstract(false);
        type.setInstanceClass(USAddress.class);
        SDOProperty prop = new SDOProperty(aHelperContext);
        prop.setName("name");
        prop.setType(SDOConstants.SDO_STRING);
        prop.setContainingType(type);
        type.addDeclaredProperty(prop);

        SDOProperty prop2 = new SDOProperty(aHelperContext);
        prop2.setName("street");
        prop2.setType(SDOConstants.SDO_STRING);
        prop2.setContainingType(type);
        type.addDeclaredProperty(prop2);

        ((SDOTypeHelper)typeHelper).addType(type);
        return type;
    }

     public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.datafactory.SDODataFactoryCreateSimpleTestCases" };
        TestRunner.main(arguments);
    }
}
