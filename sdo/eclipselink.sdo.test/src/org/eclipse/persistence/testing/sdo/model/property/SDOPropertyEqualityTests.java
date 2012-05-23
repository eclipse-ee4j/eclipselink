package org.eclipse.persistence.testing.sdo.model.property;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.sdo.SDOType;

import commonj.sdo.DataObject;
import commonj.sdo.helper.DataFactory;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.TypeHelper;
import commonj.sdo.helper.XMLDocument;
import commonj.sdo.helper.XMLHelper;

import junit.framework.TestCase;

/**
 * Test the hashCode and equality methods on SDOProperty.
 */
public class SDOPropertyEqualityTests extends TestCase {
    /**
     * This test does the following:
     *   1) Define a DataObject with a single String property, 'name'
     *   2) Create two instances of this DataObject, each with a different 'name'
     *   3) Add the two DataObjects to a list named 'result'
     *   4) Create a wrapper DataObject
     *   5) Set the 'result' list on the wrapper DataObject
     *   6) Create an XML document from the wrapper DataObject
     *   7) Save the document to a StringWriter
     *   8) Load the document
     *   9) Get the root DataObject from the document
     *  10) Verify that the root DataObject has a 'result' list containing two entries
     *  
     * If the hashCode and equals method on SDOProperty do not work properly, the root DataObject
     * will have return a 'result' list with only one entry.
     */
    public void testSameNameProperty() {
        boolean exception = false;
        String msg = "";
        List resultList = null;
        
        try {
            setUpDO();
    
            List<DataObject> result = queryDO();
            // The HelperContext corresponding to the DataObjects in the query result.
            HelperContext helperContext = ((SDOType) result.get(0).getType()).getHelperContext();
    
            DataObject wrapperDO = helperContext.getDataFactory().create("org.eclipse.persistence.sdo", "OpenSequencedType");
            wrapperDO.setList("result", result);
    
            XMLDocument xmlDocument = helperContext.getXMLHelper().createDocument(wrapperDO, "http://www.example.org", "ROOT");
            StringWriter stringWriter = new StringWriter();
            XMLHelper.INSTANCE.save(xmlDocument, stringWriter, null);
    
            StringReader stringReader = new StringReader(stringWriter.toString());
            XMLDocument resultDocument = helperContext.getXMLHelper().load(stringReader, null, null);
            DataObject resultDO = resultDocument.getRootObject();
    
            resultList = resultDO.getList("result");
        } catch (Exception x) {
            exception = true;
            msg = x.getMessage();
        }
        assertFalse("An unexpected exception occurred: " + msg, exception);
        assertTrue("Expected 2 list entries, but was " + resultList.size() , resultList.size() == 2);
    }
    
    private void setUpDO() {
        DataObject itemTypeDO = DataFactory.INSTANCE.create("commonj.sdo", "Type");
        itemTypeDO.set("name", "Item");
        itemTypeDO.set("uri", "org.example");
        itemTypeDO.setBoolean("dataType", false);

        DataObject namePropertyDO = DataFactory.INSTANCE.create("commonj.sdo", "Property");
        namePropertyDO.set("name", "name");
        namePropertyDO.set("type", TypeHelper.INSTANCE.getType("commonj.sdo", "String"));
        itemTypeDO.getList("property").add(namePropertyDO);

        TypeHelper.INSTANCE.define(itemTypeDO);
    }

    private List<DataObject> queryDO() {
        List result = new ArrayList<DataObject>();

        DataObject item1DO = DataFactory.INSTANCE.create("org.example", "Item");
        item1DO.set("name", "Telephone");
        result.add(item1DO);

        DataObject item2DO = DataFactory.INSTANCE.create("org.example", "Item");
        item2DO.set("name", "Extension Cord");
        result.add(item2DO);

        return result;
    }
}