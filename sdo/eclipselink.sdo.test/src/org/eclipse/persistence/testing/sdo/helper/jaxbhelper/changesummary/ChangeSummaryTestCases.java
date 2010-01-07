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
*     bdoughan - Mar 27/2009 - 2.0 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.sdo.helper.jaxbhelper.changesummary;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.sdo.helper.jaxb.JAXBHelperContext;
import org.eclipse.persistence.testing.sdo.SDOTestCase;

import commonj.sdo.ChangeSummary;
import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.XMLDocument;

public class ChangeSummaryTestCases extends SDOTestCase {

    private static final String XML_SCHEMA = "org/eclipse/persistence/testing/sdo/helper/jaxbhelper/changesummary/ChangeSummary.xsd";
    private static final String XML_DATA_TYPE_PROPERTY_WAS_SET = "org/eclipse/persistence/testing/sdo/helper/jaxbhelper/changesummary/DataTypePropertyWasSet.xml";
    private static final String XML_DATA_TYPE_PROPERTY_WAS_NOT_SET = "org/eclipse/persistence/testing/sdo/helper/jaxbhelper/changesummary/DataTypePropertyWasNotSet.xml";
    private static final String XML_DATA_TYPE_MANY_PROPERTY_WAS_SET = "org/eclipse/persistence/testing/sdo/helper/jaxbhelper/changesummary/DataTypeManyPropertyWasSet.xml";
    private static final String XML_DATA_TYPE_MANY_PROPERTY_WAS_NOT_SET = "org/eclipse/persistence/testing/sdo/helper/jaxbhelper/changesummary/DataTypeManyPropertyWasNotSet.xml";
    private static final String XML_NON_DATA_TYPE_PROPERTY_WAS_SET = "org/eclipse/persistence/testing/sdo/helper/jaxbhelper/changesummary/NonDataTypePropertyWasSet.xml";
    private static final String XML_NON_DATA_TYPE_PROPERTY_WAS_NOT_SET = "org/eclipse/persistence/testing/sdo/helper/jaxbhelper/changesummary/NonDataTypePropertyWasNotSet.xml";
    private static final String XML_NON_DATA_TYPE_MANY_PROPERTY_WAS_SET = "org/eclipse/persistence/testing/sdo/helper/jaxbhelper/changesummary/NonDataTypeManyPropertyWasSet.xml";
    private static final String XML_NON_DATA_TYPE_MANY_PROPERTY_WAS_NOT_SET = "org/eclipse/persistence/testing/sdo/helper/jaxbhelper/changesummary/NonDataTypeManyPropertyWasNotSet.xml";

    private static final String CONTROL_DATATYPE_PROPERTY = "name";
    private static final String CONTROL_DATATYPE_PROPERTY_OLD_VALUE = "OldValue";
    private static final String CONTROL_DATATYPE_PROPERTY_NEW_VALUE = "NewValue";

    private static final String CONTROL_DATATYPE_MANY_PROPERTY = "simple-list";
    private static final String CONTROL_DATATYPE_MANY_PROPERTY_OLD_VALUE = "OldValue";
    private static final String CONTROL_DATATYPE_MANY_PROPERTY_NEW_VALUE = "NewValue";

    private static final String CONTROL_NONDATATYPE_CONTAINMENT_PROPERTY = "child1";

    private static final String CONTROL_NONDATATYPE_CONTAINMENT_MANY_PROPERTY = "child2";

    private JAXBHelperContext jaxbHelperContext;

    public ChangeSummaryTestCases(String name) {
        super(name);
    }

    public void setUp() {
        ChangeSummaryProject project = new ChangeSummaryProject();
        XMLContext xmlContext = new XMLContext(project);
        JAXBContext jaxbContext = new JAXBContext(xmlContext);
        jaxbHelperContext = new JAXBHelperContext(jaxbContext);

        InputStream xsd = Thread.currentThread().getContextClassLoader().getResourceAsStream(XML_SCHEMA);
        jaxbHelperContext.getXSDHelper().define(xsd, null);
    }

    public void testDataTypePropertyWasSet() throws IOException {
        Type rootType = jaxbHelperContext.getType(Root.class);
        Property controlDataTypeProperty = rootType.getProperty(CONTROL_DATATYPE_PROPERTY);

        DataObject rootDO = jaxbHelperContext.getDataFactory().create(rootType);
        rootDO.set(controlDataTypeProperty, CONTROL_DATATYPE_PROPERTY_OLD_VALUE);
        rootDO.getChangeSummary().beginLogging();
        rootDO.set(controlDataTypeProperty, CONTROL_DATATYPE_PROPERTY_NEW_VALUE);

        assertEquals(CONTROL_DATATYPE_PROPERTY_NEW_VALUE, rootDO.get(controlDataTypeProperty));

        ChangeSummary.Setting setting = rootDO.getChangeSummary().getOldValue(rootDO, rootType.getProperty(CONTROL_DATATYPE_PROPERTY));
        String testOldValue = (String) setting.getValue();
        assertEquals(CONTROL_DATATYPE_PROPERTY_OLD_VALUE, testOldValue);
        assertEquals(true, setting.isSet());
    }

    public void testDataTypePropertyWasSetXML() throws IOException {
        Type rootType = jaxbHelperContext.getType(Root.class);
        Property controlDataTypeProperty = rootType.getProperty(CONTROL_DATATYPE_PROPERTY);

        InputStream xml = Thread.currentThread().getContextClassLoader().getResourceAsStream(XML_DATA_TYPE_PROPERTY_WAS_SET);
        DataObject rootDO = jaxbHelperContext.getXMLHelper().load(xml).getRootObject();

        assertEquals(CONTROL_DATATYPE_PROPERTY_NEW_VALUE, rootDO.get(controlDataTypeProperty));

        ChangeSummary.Setting setting = rootDO.getChangeSummary().getOldValue(rootDO, controlDataTypeProperty);
        String testOldValue = (String) setting.getValue();
        assertEquals(CONTROL_DATATYPE_PROPERTY_OLD_VALUE, testOldValue);
        assertEquals(true, setting.isSet());
    }

    public void testDataTypePropertyWasNotSet() throws IOException {
        Type rootType = jaxbHelperContext.getType(Root.class);
        Property controlDataTypeProperty = rootType.getProperty(CONTROL_DATATYPE_PROPERTY);

        DataObject rootDO = jaxbHelperContext.getDataFactory().create(rootType);
        rootDO.getChangeSummary().beginLogging();
        rootDO.set(controlDataTypeProperty, CONTROL_DATATYPE_PROPERTY_NEW_VALUE);

        assertEquals(CONTROL_DATATYPE_PROPERTY_NEW_VALUE, rootDO.get(controlDataTypeProperty));

        ChangeSummary.Setting setting = rootDO.getChangeSummary().getOldValue(rootDO, controlDataTypeProperty);
        String testOldValue = (String) setting.getValue();
        assertEquals(null, testOldValue);
        assertEquals(false, setting.isSet());
    }

    public void testDataTypePropertyWasNotSetXML() throws IOException {
        Type rootType = jaxbHelperContext.getType(Root.class);
        Property controlDataTypeProperty = rootType.getProperty(CONTROL_DATATYPE_PROPERTY);

        InputStream xml = Thread.currentThread().getContextClassLoader().getResourceAsStream(XML_DATA_TYPE_PROPERTY_WAS_NOT_SET);
        DataObject rootDO = jaxbHelperContext.getXMLHelper().load(xml).getRootObject();

        assertEquals(CONTROL_DATATYPE_PROPERTY_NEW_VALUE, rootDO.get(controlDataTypeProperty));

        ChangeSummary.Setting setting = rootDO.getChangeSummary().getOldValue(rootDO, controlDataTypeProperty);
        String testOldValue = (String) setting.getValue();
        assertEquals(null, testOldValue);
        assertEquals(false, setting.isSet());
    }

    public void testDataTypeManyPropertyWasSet() throws IOException {
        Type rootType = jaxbHelperContext.getType(Root.class);
        Property controlDataTypeManyProperty = rootType.getProperty(CONTROL_DATATYPE_MANY_PROPERTY);

        DataObject rootDO = jaxbHelperContext.getDataFactory().create(rootType);
        rootDO.getList(controlDataTypeManyProperty).add(CONTROL_DATATYPE_MANY_PROPERTY_OLD_VALUE);
        rootDO.getChangeSummary().beginLogging();
        rootDO.getList(controlDataTypeManyProperty).add(CONTROL_DATATYPE_MANY_PROPERTY_NEW_VALUE);

        assertEquals(2, rootDO.getList(controlDataTypeManyProperty).size());
        assertEquals(CONTROL_DATATYPE_MANY_PROPERTY_OLD_VALUE, rootDO.getList(controlDataTypeManyProperty).get(0));
        assertEquals(CONTROL_DATATYPE_MANY_PROPERTY_NEW_VALUE, rootDO.getList(controlDataTypeManyProperty).get(1));

        ChangeSummary.Setting setting = rootDO.getChangeSummary().getOldValue(rootDO, controlDataTypeManyProperty);
        List testOldValue = (List) setting.getValue();
        assertEquals(1, testOldValue.size());
        assertEquals(CONTROL_DATATYPE_MANY_PROPERTY_OLD_VALUE, testOldValue.get(0));
        assertEquals(true, setting.isSet());
    }

    public void testDataTypeManyPropertyWasSetXML() throws IOException {
        Type rootType = jaxbHelperContext.getType(Root.class);
        Property controlDataTypeManyProperty = rootType.getProperty(CONTROL_DATATYPE_MANY_PROPERTY);

        InputStream xml = Thread.currentThread().getContextClassLoader().getResourceAsStream(XML_DATA_TYPE_MANY_PROPERTY_WAS_SET);
        DataObject rootDO = jaxbHelperContext.getXMLHelper().load(xml).getRootObject();

        assertEquals(2, rootDO.getList(controlDataTypeManyProperty).size());
        assertEquals(CONTROL_DATATYPE_MANY_PROPERTY_OLD_VALUE, rootDO.getList(controlDataTypeManyProperty).get(0));
        assertEquals(CONTROL_DATATYPE_MANY_PROPERTY_NEW_VALUE, rootDO.getList(controlDataTypeManyProperty).get(1));

        ChangeSummary.Setting setting = rootDO.getChangeSummary().getOldValue(rootDO, controlDataTypeManyProperty);
        List testOldValue = (List) setting.getValue();
        assertEquals(1, testOldValue.size());
        assertEquals(CONTROL_DATATYPE_MANY_PROPERTY_OLD_VALUE, testOldValue.get(0));
        assertEquals(true, setting.isSet());
    }

    public void testDataTypeManyPropertyWasNotSet() throws IOException {
        Type rootType = jaxbHelperContext.getType(Root.class);
        Property controlDataTypeManyProperty = rootType.getProperty(CONTROL_DATATYPE_MANY_PROPERTY);

        DataObject rootDO = jaxbHelperContext.getDataFactory().create(rootType);
        rootDO.getChangeSummary().beginLogging();
        rootDO.getList(controlDataTypeManyProperty).add(CONTROL_DATATYPE_MANY_PROPERTY_NEW_VALUE);

        assertEquals(1, rootDO.getList(controlDataTypeManyProperty).size());
        assertEquals(CONTROL_DATATYPE_MANY_PROPERTY_NEW_VALUE, rootDO.getList(controlDataTypeManyProperty).get(0));

        ChangeSummary.Setting setting = rootDO.getChangeSummary().getOldValue(rootDO, controlDataTypeManyProperty);
        List testOldValue = (List) setting.getValue();
        assertEquals(0, testOldValue.size());
        assertEquals(false, setting.isSet());
    }

    public void testDataTypeManyPropertyWasNotSetXML() throws IOException {
        Type rootType = jaxbHelperContext.getType(Root.class);
        Property controlDataTypeManyProperty = rootType.getProperty(CONTROL_DATATYPE_MANY_PROPERTY);

        InputStream xml = Thread.currentThread().getContextClassLoader().getResourceAsStream(XML_DATA_TYPE_MANY_PROPERTY_WAS_NOT_SET);
        DataObject rootDO = jaxbHelperContext.getXMLHelper().load(xml).getRootObject();

        assertEquals(1, rootDO.getList(controlDataTypeManyProperty).size());
        assertEquals(CONTROL_DATATYPE_MANY_PROPERTY_NEW_VALUE, rootDO.getList(controlDataTypeManyProperty).get(0));

        ChangeSummary.Setting setting = rootDO.getChangeSummary().getOldValue(rootDO, controlDataTypeManyProperty);
        List testOldValue = (List) setting.getValue();
        assertEquals(0, testOldValue.size());
        assertEquals(false, setting.isSet());
    }

    public void testNonDataTypePropertyWasSet() throws IOException {
        Type rootType = jaxbHelperContext.getType(Root.class);
        Property controlNonDataTypeProperty = rootType.getProperty(CONTROL_NONDATATYPE_CONTAINMENT_PROPERTY);

        DataObject rootDO = jaxbHelperContext.getDataFactory().create(rootType);
        DataObject oldChild1DO = rootDO.createDataObject(controlNonDataTypeProperty);
        oldChild1DO.set("id", 1);
        rootDO.getChangeSummary().beginLogging();
        DataObject newChild1DO = rootDO.createDataObject(controlNonDataTypeProperty);
        newChild1DO.set("id", 2);

        assertEquals(newChild1DO, rootDO.getDataObject(controlNonDataTypeProperty));

        ChangeSummary.Setting setting = rootDO.getChangeSummary().getOldValue(rootDO, controlNonDataTypeProperty);
        DataObject testOldValue = (DataObject) setting.getValue();
        assertEquals(1, testOldValue.get("id"));
        assertEquals(true, setting.isSet());
    }

    public void testNonDataTypePropertyWasSetXML() throws IOException {
        Type rootType = jaxbHelperContext.getType(Root.class);
        Property controlNonDataTypeProperty = rootType.getProperty(CONTROL_NONDATATYPE_CONTAINMENT_PROPERTY);

        InputStream xml = Thread.currentThread().getContextClassLoader().getResourceAsStream(XML_NON_DATA_TYPE_PROPERTY_WAS_SET);
        DataObject rootDO = jaxbHelperContext.getXMLHelper().load(xml).getRootObject();

        ChangeSummary.Setting setting = rootDO.getChangeSummary().getOldValue(rootDO, controlNonDataTypeProperty);
        DataObject testOldValue = (DataObject) setting.getValue();
        assertEquals(1, testOldValue.get("id"));
        assertEquals(true, setting.isSet());
    }

    public void testNonDataTypePropertyWasNotSet() throws IOException {
        Type rootType = jaxbHelperContext.getType(Root.class);
        Property controlNonDataTypeProperty = rootType.getProperty(CONTROL_NONDATATYPE_CONTAINMENT_PROPERTY);

        DataObject rootDO = jaxbHelperContext.getDataFactory().create(rootType);
        rootDO.getChangeSummary().beginLogging();
        DataObject newChild1DO = rootDO.createDataObject(controlNonDataTypeProperty);
        newChild1DO.set("id", 2);

        assertEquals(newChild1DO, rootDO.getDataObject(controlNonDataTypeProperty));

        ChangeSummary.Setting setting = rootDO.getChangeSummary().getOldValue(rootDO, controlNonDataTypeProperty);
        DataObject testOldValue = (DataObject) setting.getValue();
        assertEquals(null, testOldValue);
        assertEquals(false, setting.isSet());
    }

    public void testNonDataTypePropertyWasNotSetXML() throws IOException {
        Type rootType = jaxbHelperContext.getType(Root.class);
        Property controlNonDataTypeProperty = rootType.getProperty(CONTROL_NONDATATYPE_CONTAINMENT_PROPERTY);

        InputStream xml = Thread.currentThread().getContextClassLoader().getResourceAsStream(XML_NON_DATA_TYPE_PROPERTY_WAS_NOT_SET);
        DataObject rootDO = jaxbHelperContext.getXMLHelper().load(xml).getRootObject();

        ChangeSummary.Setting setting = rootDO.getChangeSummary().getOldValue(rootDO, controlNonDataTypeProperty);
        DataObject testOldValue = (DataObject) setting.getValue();
        assertEquals(null, testOldValue);
        assertEquals(false, setting.isSet());
    }

    public void testNonDataTypeManyPropertyWasSet() throws IOException {
        Type rootType = jaxbHelperContext.getType(Root.class);
        Property controlNonDataTypeContainmentManyProperty = rootType.getProperty(CONTROL_NONDATATYPE_CONTAINMENT_MANY_PROPERTY);

        DataObject rootDO = jaxbHelperContext.getDataFactory().create(rootType);
        DataObject oldChild2DO = rootDO.createDataObject(controlNonDataTypeContainmentManyProperty);
        oldChild2DO.set("id", 1);
        rootDO.getChangeSummary().beginLogging();
        DataObject newChild2DO = rootDO.createDataObject(controlNonDataTypeContainmentManyProperty);
        newChild2DO.set("id", 2);

        assertEquals(2, rootDO.getList(controlNonDataTypeContainmentManyProperty).size());

        ChangeSummary.Setting setting = rootDO.getChangeSummary().getOldValue(rootDO, controlNonDataTypeContainmentManyProperty);
        List testOldValue = (List) setting.getValue();
        assertEquals(1, testOldValue.size());
        assertEquals(true, setting.isSet());
    }

    public void testNonDataTypeManyPropertyWasSetXML() throws IOException {
        Type rootType = jaxbHelperContext.getType(Root.class);
        Property controlNonDataTypeContainmentManyProperty = rootType.getProperty(CONTROL_NONDATATYPE_CONTAINMENT_MANY_PROPERTY);

        InputStream xml = Thread.currentThread().getContextClassLoader().getResourceAsStream(XML_NON_DATA_TYPE_MANY_PROPERTY_WAS_SET);
        DataObject rootDO = jaxbHelperContext.getXMLHelper().load(xml).getRootObject();

        assertEquals(2, rootDO.getList(controlNonDataTypeContainmentManyProperty).size());

        ChangeSummary.Setting setting = rootDO.getChangeSummary().getOldValue(rootDO, controlNonDataTypeContainmentManyProperty);
        List testOldValue = (List) setting.getValue();
        assertEquals(1, testOldValue.size());
        assertEquals(true, setting.isSet());
    }

    public void testNonDataTypeManyPropertyWasNotSet() throws IOException {
        Type rootType = jaxbHelperContext.getType(Root.class);
        Property controlNonDataTypeContainmentManyProperty = rootType.getProperty(CONTROL_NONDATATYPE_CONTAINMENT_MANY_PROPERTY);

        DataObject rootDO = jaxbHelperContext.getDataFactory().create(rootType);
        rootDO.getChangeSummary().beginLogging();
        DataObject newChild2DO = rootDO.createDataObject(controlNonDataTypeContainmentManyProperty);
        newChild2DO.set("id", 2);

        List testList = rootDO.getList(controlNonDataTypeContainmentManyProperty);
        assertEquals(1, rootDO.getList(controlNonDataTypeContainmentManyProperty).size());

        ChangeSummary.Setting setting = rootDO.getChangeSummary().getOldValue(rootDO, controlNonDataTypeContainmentManyProperty);
        List testOldValue = (List) setting.getValue();
        assertEquals(0, testOldValue.size());
        assertEquals(false, setting.isSet());
    }

    public void testNonDataTypeManyPropertyWasNotSetXML() throws IOException {
        Type rootType = jaxbHelperContext.getType(Root.class);
        Property controlNonDataTypeContainmentManyProperty = rootType.getProperty(CONTROL_NONDATATYPE_CONTAINMENT_MANY_PROPERTY);

        InputStream xml = Thread.currentThread().getContextClassLoader().getResourceAsStream(XML_NON_DATA_TYPE_MANY_PROPERTY_WAS_NOT_SET);
        DataObject rootDO = jaxbHelperContext.getXMLHelper().load(xml).getRootObject();

        assertEquals(1, rootDO.getList(controlNonDataTypeContainmentManyProperty).size());

        ChangeSummary.Setting setting = rootDO.getChangeSummary().getOldValue(rootDO, controlNonDataTypeContainmentManyProperty);
        List testOldValue = (List) setting.getValue();
        assertEquals(0, testOldValue.size());
        assertEquals(false, setting.isSet());
    }

    public void tearDown() {
    }

}
