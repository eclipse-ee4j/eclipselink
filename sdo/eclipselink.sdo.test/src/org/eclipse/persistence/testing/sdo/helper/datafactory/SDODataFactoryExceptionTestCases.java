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
package org.eclipse.persistence.testing.sdo.helper.datafactory;

import commonj.sdo.Type;
import commonj.sdo.impl.HelperProvider;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.helper.SDOTypeHelper;
import org.eclipse.persistence.sdo.types.SDODataType;
import org.eclipse.persistence.testing.sdo.helper.xmlhelper.SDOXMLHelperTestCases;

public class SDODataFactoryExceptionTestCases extends SDOXMLHelperTestCases {
    public SDODataFactoryExceptionTestCases(String name) {
        super(name);
    }
    
    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.datafactory.SDODataFactoryExceptionTestCases" };
        TestRunner.main(arguments);
    }

    public void testAbstractTypeException() throws Exception {
        SDOType type = new SDOType("uri", "name");
        type.setAbstract(true);
        try {
            dataFactory.create(type);
            fail("An IllegalArugmentException should have occurred");
        } catch (Exception e) {
        }
    }

    public void testOpenTypeException() throws Exception {
        SDOType type = new SDODataType("uri", "name", (SDOTypeHelper) HelperProvider.getDefaultContext().getTypeHelper());
        try {
            dataFactory.create(type);
            fail("An IllegalArugmentException should have occurred");
        } catch (Exception e) {
        }
    }

    public void testNullInterfaceClass() throws Exception {
        Class theClass = null;
        try {
            dataFactory.create(theClass);
            fail("An IllegalArugmentException should have occurred");
        } catch (Exception e) {
        }
    }
    
    public void testNullType() throws Exception {
        Type theType = null;
        try {
            dataFactory.create(theType);
            fail("An IllegalArugmentException should have occurred");
        } catch (Exception e) {
        }
    }

     public void testCreateWithNullType() throws Exception {
         Type type = null;
         try {
             dataFactory.create(type);
             fail("An IllegalArugmentException should have occurred");
         } catch (Exception e) {}
     }

     public void testCreateWithNullInterfaceClass() throws Exception {
         Class theClass = null;
         try {
             dataFactory.create(theClass);
             fail("An IllegalArugmentException should have occurred");
         } catch (Exception e) {}
     }

     public void testInvalidInterfaceClass() throws Exception {
         Class theClass = this.getClass();
         try {
             dataFactory.create(theClass);
             fail("An IllegalArugmentException should have occurred");
         } catch (Exception e) {}
     }
}
