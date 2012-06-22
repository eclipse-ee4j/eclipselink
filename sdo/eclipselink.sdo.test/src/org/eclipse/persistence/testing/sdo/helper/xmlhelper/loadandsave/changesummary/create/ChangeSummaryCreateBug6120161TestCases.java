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
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.changesummary.create;

import commonj.sdo.ChangeSummary;
import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.helper.XMLDocument;
import java.io.FileInputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import javax.xml.transform.stream.StreamSource;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.helper.DefaultSchemaResolver;
import org.eclipse.persistence.sdo.helper.FileCodeWriter;
import org.eclipse.persistence.sdo.helper.SDOClassGenerator;
import org.eclipse.persistence.sdo.helper.SDOXSDHelper;
import org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.LoadAndSaveTestCases;

public class ChangeSummaryCreateBug6120161TestCases extends LoadAndSaveTestCases {
    public ChangeSummaryCreateBug6120161TestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.changesummary.create.ChangeSummaryCreateBug6120161TestCases" };
        TestRunner.main(arguments);
    }

    protected String getSchemaName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/changesummary/bug6120161/HRAppService.xsd";
    }

    protected String getControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/changesummary/bug6120161/bug6120161.xml");
    }
    
    protected String getControlWriteFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/changesummary/bug6120161/bug6120161Write.xml");
    }


    protected String getSchemaLocation() {
        return (FILE_PROTOCOL + USER_DIR + "/org/eclipse/persistence/testing/sdo/helper/xmlhelper/changesummary/bug6120161/");
    }

    protected String getControlRootName() {
        return "processCSDeptElement";
    }

    protected String getControlRootURI() {
        return "http://example.com/app/";
    }

    protected String getRootInterfaceName() {
        return "ProcessCSDeptElement";
    }

    protected List<String> getPackages() {
        List<String> packages = new ArrayList<String>();
        packages.add("com/example/app");
        packages.add("dept");
        packages.add("com/mypackage");
        return packages;
    }

    protected void generateClasses(String tmpDirName) throws Exception {
        String xsdString = getSchema(getSchemaName());
        StringReader reader = new StringReader(xsdString);

        SDOClassGenerator classGenerator = new SDOClassGenerator(aHelperContext);
        DefaultSchemaResolver sr = new DefaultSchemaResolver();
        sr.setBaseSchemaLocation(getSchemaLocation());
        FileCodeWriter cw = new FileCodeWriter();
        cw.setSourceDir(tmpDirName);
        classGenerator.generate(reader, cw, sr, true);
    }

    protected void verifyAfterLoad(XMLDocument doc) {
        super.verifyAfterLoad(doc);
        DataObject root = doc.getRootObject();
        DataObject data = (DataObject)root.getDataObject("processData");

        List dataObjects = data.getList("Value");
        ChangeSummary changeSummary = data.getChangeSummary();
        DataObject dept = (DataObject)dataObjects.get(0);
        List emps = dept.getList("Emp");
        assertEquals(7, emps.size());

        assertContainsEmpAtIndex(emps, 0, 7566, "JONES");
        assertContainsEmpAtIndex(emps, 1, 7788, "SCOTT");
        assertContainsEmpAtIndex(emps, 2, 7876, "ADAMS");
        assertContainsEmpAtIndex(emps, 3, 7902, "FORD");
        assertContainsEmpAtIndex(emps, 4, 8082, "ADAMS");
        assertContainsEmpAtIndex(emps, 5, 8083, "ADAMS");
        assertContainsEmpAtIndex(emps, 6, 0, "ADAMS_TEST");

        ChangeSummary.Setting oldSetting = changeSummary.getOldValue(dept, dept.getInstanceProperty("Emp"));
        Object value = oldSetting.getValue();
        assertTrue(value instanceof List);
        assertEquals(7, ((List)value).size());

        assertContainsEmpAtIndex((List)value, 0, 7566, "JONES");
        assertContainsEmpAtIndex((List)value, 1, 7788, "SCOTT");
        assertContainsEmpAtIndex((List)value, 2, 7876, "ADAMS");
        assertContainsEmpAtIndex((List)value, 3, 7902, "FORD");
        assertContainsEmpAtIndex((List)value, 4, 8081, "ADAMS");//deleted
        assertContainsEmpAtIndex((List)value, 5, 8082, "ADAMS");
        assertContainsEmpAtIndex((List)value, 6, 8083, "ADAMS");

        DataObject adams8081 = ((DataObject)((List)value).get(4));

        /*
                for (int i = 0, size = emps.size(); i < size; i++) {
                    DataObject nextEmp = (DataObject)emps.get(i);
                    System.out.println("NUM:" + nextEmp.get("Empno"));
                    System.out.println("NAME:" + nextEmp.get("Ename"));
                }

                System.out.println("\n*** First pass ***\n");
                for (int i = 0, size = dataObjects.size(); i < size; i++) {
                    DataObject dataObject = (DataObject)dataObjects.get(i);
                    printDataObject(dataObject, changeSummary, "", null);
                }

                System.out.println("\n*** Second pass ***\n");
                List changedObjects = changeSummary.getChangedDataObjects();
                for (int i = 0, size = changedObjects.size(); i < size; i++) {
                    DataObject dataObject = (DataObject)changedObjects.get(i);
                    printDataObject(dataObject, changeSummary, "", changeSummary.getOldContainer(dataObject));
                }

                */
    }

    public void registerTypes() {
    }

    private void assertContainsEmpAtIndex(List emps, int index, int empNo, String empName) {
        DataObject itemAtIndex = (DataObject)emps.get(index);
        assertEquals(empNo, itemAtIndex.getInt("Empno"));
        assertEquals(empName, itemAtIndex.getString("Ename"));
    }

    private void assertContainsEmp(List emps, int empNo, String empName) {
        for (int i = 0, size = emps.size(); i < size; i++) {
            DataObject nextEmp = (DataObject)emps.get(i);
            int nextEmpNo = nextEmp.getInt("Empno");
            String nextEmpName = nextEmp.getString("Ename");
            if (nextEmpName.equals(empName)) {
                if (empNo == nextEmpNo) {
                    return;//found match              
                }
            }
        }
        fail("DOESNT CONTAIN: " + empName);
    }

    protected List defineTypes() {
        try {
            FileInputStream inputStream = new FileInputStream(getSchemaName());
            StreamSource ss = new StreamSource(inputStream);
            DefaultSchemaResolver dsr = new DefaultSchemaResolver();
            dsr.setBaseSchemaLocation(getSchemaLocation());
            return ((SDOXSDHelper)xsdHelper).define(ss, dsr);
        } catch (Exception e) {
            e.printStackTrace();
            fail("An error occurred during defineTypes");
        }
        return null;
    }

    protected static void printDataObject(DataObject dataObject, ChangeSummary changeSummary, String indent, DataObject container) {
        if (changeSummary.isCreated(dataObject)) {
            System.out.print(indent + "Created\t" + dataObject);
        } else if (changeSummary.isModified(dataObject)) {
            System.out.print(indent + "Modified\t" + dataObject);
        } else if (changeSummary.isDeleted(dataObject)) {
            System.out.print(indent + "Deleted\t" + dataObject);
        } else {
            System.out.print(indent + "Unchanged\t" + dataObject);
        }

        if (container == null) {
            System.out.println();
        } else {
            System.out.println("\tOldContainer:\t" + container);
        }

        List properties = dataObject.getType().getProperties();
        for (int p = 0, size = properties.size(); p < size; p++) {
            if (dataObject.isSet(p)) {
                Property property = (Property)properties.get(p);
                if (property.isContainment()) {
                    List children = dataObject.getList(property);
                    for (int c = 0, csize = children.size(); c < csize; c++) {
                        DataObject child = (DataObject)children.get(c);
                        printDataObject(child, changeSummary, " " + (c + 1) + " ", null);
                    }
                }
            }
        }
    }

    public void testNoSchemaLoadFromInputStreamSaveDataObjectToString() {
        //do nothing 
    }
}
