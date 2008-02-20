/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.sdo.helper.xsdhelper.define;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.exceptions.SDOException;
import org.eclipse.persistence.exceptions.XMLMarshalException;

public class LoadFailsAfterDefineFailsTestCases extends XSDHelperDefineTestCases {
    public LoadFailsAfterDefineFailsTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        TestRunner.run(LoadFailsAfterDefineFailsTestCases.class);
    }

    public String getSchemaToDefine() {
		return "./org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/SimpleWithError.xsd";
    }

    public void testDefine() {
        try {
            super.testDefine();
        } catch (SDOException e) {            
            //do nothing expected exception
        }

        String xmlFileName = "./org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/simpleWithError.xml";					        

        try {
            FileInputStream inputStream = new FileInputStream(xmlFileName);
            xmlHelper.load(inputStream);
        } catch (FileNotFoundException fnfException) {
            fnfException.printStackTrace();
            fail("FileNotFoundException occurred");
        } catch (IOException ioException) {
            ioException.printStackTrace();
            fail("ioException occurred");
        } catch (XMLMarshalException xmlException) {
            xmlException.printStackTrace();
            fail("XMLMarshalException occurred");
        } catch (SDOException sdoException) {
          assertEquals(SDOException.GLOBAL_PROPERTY_NOT_FOUND, sdoException.getErrorCode());
          return;
        }
        fail("An SDOException should have occurred but did not");
    }

    public List getControlTypes() {
        List types = new ArrayList();
        return types;
    }
}