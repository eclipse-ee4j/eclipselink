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
package org.eclipse.persistence.testing.sdo.helper.xsdhelper.define;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.testing.sdo.helper.xsdhelper.XSDHelperTestCases;

public class AttributeGroupTestCases  extends XSDHelperDefineTestCases {
    public AttributeGroupTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        TestRunner.run(AttributeGroupTestCases.class);
    }

    public String getSchemaToDefine() {    
        return "org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/soap_encoding_1.1.xsd";
    }
    
      public void testDefine() {
        //String xsdSchema = getSchema(getSchemaToDefine());
        InputStream is = getSchemaInputStream(getSchemaToDefine());
        List types = xsdHelper.define(is, getSchemaLocation());

        //List types = xsdHelper.define(xsdSchema, getSchemaLocation());
        log("\nExpected:\n");
        //List controlTypes = getControlTypes();
        //log(controlTypes);

        log("\nActual:\n");
        log(types);

        //compare(getControlTypes(), types);
    }
    
    public List getControlTypes()
    {
      return new ArrayList();
    }

}
