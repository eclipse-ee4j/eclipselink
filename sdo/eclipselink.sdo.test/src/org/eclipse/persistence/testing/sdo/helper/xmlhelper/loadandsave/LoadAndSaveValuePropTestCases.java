/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.XMLDocument;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;

/* #  6067502 22-MAY-07 TOPLINK 4 15 N 1339 SDO 11.1.1.0.0 NO RELEASE 
 * SDO: JAVA CODE GENERATION REQUIRES SDO RESERVED WORD NAME COLLISION HANDLING 
 * Mangled class name collision will occur here resulting in the wrong Address property being used 
 * causing an isMany=true failure check
 * see  testLoadFromAndSaveAfterDefineMultipleSchemas()
 */

public class LoadAndSaveValuePropTestCases extends LoadAndSaveTestCases {
    public LoadAndSaveValuePropTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.LoadAndSaveValuePropTestCases" };
        TestRunner.main(arguments);
    }

    protected String getNoSchemaControlFileName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/valuePropNoSchema.xml";
    }

    protected String getControlDataObjectFileName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/valueProp.xml";
    }

    protected String getControlFileName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/valueProp.xml";
    }

    protected String getSchemaName() {
        return "org/eclipse/persistence/testing/sdo/helper/xmlhelper/ValueProp.xsd";
    }

    protected String getControlRootURI() {
        return "urn:customer-example";
    }
    
    protected String getUnrelatedSchemaName(){
      return "org/eclipse/persistence/testing/sdo/schemas/PurchaseOrderWithInstanceClass.xsd";
    }

    protected String getControlRootName() {
        return "customer";
    }
    
    protected String getRootInterfaceName() {
        return "CustomerType";
    }

    // Override package generation based on the JAXB 2.0 algorithm in SDOUtil.java
    protected List<String> getPackages() {
        List<String> packages = new ArrayList<String>();       
        packages.add("customer_example");
        return packages;
    }
    
    public void testNoSchemaLoadFromInputStreamSaveDataObjectToString() throws Exception {
      //do nothing, this test doesn't apply
      //we would have to do the following to make this work in current code
    }
    
    public void registerTypes(){
      //do nothing because testNoSchemaLoadFromInputStreamSaveDataObjectToString is not run for this model
    }
            
}
