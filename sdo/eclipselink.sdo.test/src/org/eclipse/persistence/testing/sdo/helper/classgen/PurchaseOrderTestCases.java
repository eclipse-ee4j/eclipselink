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
package org.eclipse.persistence.testing.sdo.helper.classgen;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import javax.xml.transform.stream.StreamSource;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.helper.DefaultSchemaResolver;
import org.eclipse.persistence.sdo.helper.FileCodeWriter;
import org.eclipse.persistence.sdo.helper.SDOClassGenerator;

public class PurchaseOrderTestCases extends SDOClassGenTestCases {
   
    public PurchaseOrderTestCases(String name) {
        super(name);
    }
    
    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.classgen.PurchaseOrderTestCases" };
        TestRunner.main(arguments);
    }   

    protected String getSchemaName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/PurchaseOrder.xsd";
    }

    protected String getSourceFolder() {
        return "./srcPO";
    }

    protected String getControlSourceFolder() {
        return "./org/eclipse/persistence/testing/sdo/helper/classgen/srcPO";
    }

    protected List getControlFileNames() {
        ArrayList list = new ArrayList();
        list.add("LineItemType.java");
        list.add("LineItemTypeImpl.java");
        list.add("Items.java");
        list.add("ItemsImpl.java");
        list.add("PurchaseOrderType.java");
        list.add("PurchaseOrderTypeImpl.java");
        list.add("AddressType.java");
        list.add("AddressTypeImpl.java");
        return list;
    }
    
     // The following test case is out of scope for ClassGenElements - we let it fail with a NPE that generates an empty xsdString for this suite
    public void testAllParameterMethod() throws Exception {
        //compileFiles(getControlSourceFolder() + "/" + getPackageDir());
        compileFiles();
        StringReader reader = new StringReader(xsdString);

        List types = xsdHelper.define(reader, null);
        
        FileCodeWriter codeWriter = new FileCodeWriter();
        codeWriter.setSourceDir(getSourceFolder());
        classGenerator.generate( codeWriter,  types);
        int numGenerated = classGenerator.getGeneratedBuffers().size();
        assertEquals(getControlFileNames().size() / 2, numGenerated);

        compareFiles(getControlFiles(), getGeneratedFiles(classGenerator.getGeneratedBuffers()));
    }
}
