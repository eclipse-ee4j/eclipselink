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
 *     rbarkhouse - 2010-03-04 12:22:11 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.dynamic;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.internal.dynamic.DynamicEntityImpl;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.jaxb.DynamicJAXBContext;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public abstract class AbstractDynamicJAXBTestCases extends JAXBTestCases {

    // TODO: Use qualified class names when bug 298984 is fixed
    private static final String DOCWRAPPER_CLASS_NAME = "DocWrapper";
        //"org.persistence.testing.jaxb.dynamic.xxx.DocWrapper";
    private static final String ROOT_CLASS_NAME = "Root";
        //"org.persistence.testing.jaxb.dynamic.xxx.Root";
    private static final String COMPOBJ_CLASS_NAME = "CompositeObjectTarget";
        //"org.persistence.testing.jaxb.dynamic.xxx.CompositeObjectTarget";
    private static final String COMPCOLL_CLASS_NAME = "CompositeCollectionTarget";
        //"org.persistence.testing.jaxb.dynamic.xxx.CompositeCollectionTarget";
    private static final String ANYOBJ_CLASS_NAME = "AnyObjectTarget";
        //"org.persistence.testing.jaxb.dynamic.xxx.AnyObjectTarget";
    private static final String ANYCOLL_CLASS_NAME = "AnyCollectionTarget";
        //"org.persistence.testing.jaxb.dynamic.xxx.AnyCollectionTarget";
    private static final String ANYATT_CLASS_NAME = "AnyAttributeTarget";
        //"org.persistence.testing.jaxb.dynamic.xxx.AnyAttributeTarget";
    private static final String OBJREFSUB_CLASS_NAME = "ObjectReferenceSubclassTarget";
        //"org.persistence.testing.jaxb.dynamic.xxx.ObjectReferenceSubclassTarget";
    private static final String COLLREF_CLASS_NAME = "CollectionReferenceTarget";
        //"org.persistence.testing.jaxb.dynamic.xxx.CollectionReferenceTarget";

    private static final String EMP_CLASS_NAME = "Employee";
        //"org.persistence.testing.jaxb.dynamic.zzz.Employee";

    protected ArrayList objectsAlreadyCheckedForEquality;

    public AbstractDynamicJAXBTestCases(String name) throws Exception {
        super(name);
    }

    protected Object getControlObject() {
        DynamicEntity docWrapper = ((DynamicJAXBContext) jaxbContext).newDynamicEntity(DOCWRAPPER_CLASS_NAME);

        DynamicEntity root = ((DynamicJAXBContext) jaxbContext).newDynamicEntity(ROOT_CLASS_NAME);

        DynamicEntity compObj = ((DynamicJAXBContext) jaxbContext).newDynamicEntity(COMPOBJ_CLASS_NAME);
        compObj.set("value", null);
        Vector anyColl = new Vector(2);
        DynamicEntity anyItem1 = ((DynamicJAXBContext) jaxbContext).newDynamicEntity(ANYCOLL_CLASS_NAME);
        anyItem1.set("value", "aNyOne");
        anyColl.add(anyItem1);
        DynamicEntity anyItem2 = ((DynamicJAXBContext) jaxbContext).newDynamicEntity(ANYCOLL_CLASS_NAME);
        anyItem2.set("value", "aNyTwo");
        anyColl.add(anyItem2);
        compObj.set("anyColl", anyColl);
        root.set("compObj", compObj);

        ArrayList compColl = new ArrayList();
        DynamicEntity item1 = ((DynamicJAXBContext) jaxbContext).newDynamicEntity(COMPCOLL_CLASS_NAME);
        item1.set("value", "123");
        DynamicEntity item2 = ((DynamicJAXBContext) jaxbContext).newDynamicEntity(COMPCOLL_CLASS_NAME);
        item2.set("value", "456");
        item1.set("invRef", root);
        item2.set("invRef", root);
        compColl.add(item1);
        compColl.add(item2);
        root.set("compColl", compColl);

        Vector comDirColl = new Vector(2);
        comDirColl.add("qWe");
        comDirColl.add("rTy");
        root.set("compDirColl", comDirColl);

        root.set("binData", new byte[] { 'a', 'b', 'c', 'd', 'e' });

        Vector binDataColl = new Vector();
        binDataColl.add(new byte[] { '1', '2', '3' });
        binDataColl.add(new byte[] { '4', '5', '6' });
        root.set("binDataColl", binDataColl);

        DynamicEntity anyObj = ((DynamicJAXBContext) jaxbContext).newDynamicEntity(ANYOBJ_CLASS_NAME);
        anyObj.set("value", "aNy");
        root.set("anyObj", anyObj);

        DynamicEntity anyAtt = ((DynamicJAXBContext) jaxbContext).newDynamicEntity(ANYATT_CLASS_NAME);
        Properties anyAttProps = new Properties();
        anyAttProps.put(new QName("", "anyAtt1"), "aNyaTT1");
        anyAttProps.put(new QName("", "anyAtt2"), "aNyAttTWO");
        anyAtt.set("value", anyAttProps);
        root.set("anyAtt", anyAtt);

        String[] transform = new String[2];
        transform[0] = "transformValue1";
        transform[1] = "transformValue2";
        root.set("transform", transform);

        Document doc;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.newDocument();
        } catch(ParserConfigurationException e) {
            throw new RuntimeException(e);
        }

        Element elem = doc.createElementNS("", "frag");
        Text text = doc.createTextNode("XML Fragment Text Content");
        elem.appendChild(text);
        root.set("frag", elem);

        Vector fragColl = new Vector(2);
        Element elem1 = doc.createElementNS("", "frag-coll");
        Text text1 = doc.createTextNode("XML Fragment Collection One");
        elem1.appendChild(text1);
        Element elem2 = doc.createElementNS("", "frag-coll");
        Text text2 = doc.createTextNode("XML Fragment Collection Two");
        elem2.appendChild(text2);
        fragColl.add(elem1);
        fragColl.add(elem2);
        root.set("fragColl", fragColl);

        DynamicEntity objRef = ((DynamicJAXBContext) jaxbContext).newDynamicEntity(OBJREFSUB_CLASS_NAME);
        objRef.set("id", "13579");
        objRef.set("superclassValue", "SUPER");
        objRef.set("subclassValue", "SUB");
        root.set("objRef", objRef);

        Vector collRef = new Vector(2);
        DynamicEntity collRef1 = ((DynamicJAXBContext) jaxbContext).newDynamicEntity(COLLREF_CLASS_NAME);
        collRef1.set("id", "112233");
        collRef1.set("value", "collRefVal1");
        DynamicEntity collRef2 = ((DynamicJAXBContext) jaxbContext).newDynamicEntity(COLLREF_CLASS_NAME);
        collRef2.set("id", "445566");
        collRef2.set("value", "collRefVal2");
        collRef.add(collRef1);
        collRef.add(collRef2);
        root.set("collRef", collRef);

        root.set("choice", new Integer(2112));

        Vector choiceColl = new Vector(3);
        choiceColl.add(new Double(3.14159));
        choiceColl.add("Pi");
        choiceColl.add(Boolean.TRUE);
        root.set("choiceColl", choiceColl);

        docWrapper.set("root", root);
        docWrapper.set("objRefTarget", objRef);
        docWrapper.set("collRefTarget", collRef);

        return docWrapper;
    }

    public void testSecondProject() throws Exception {
        // Ensure that the second project's descriptor is also available from this context

        DynamicEntity emp = ((DynamicJAXBContext) jaxbContext).newDynamicEntity(EMP_CLASS_NAME);
        assertNotNull("Could not instantiate Descriptor from second project.", emp);
    }

    public void testGetSetByXPath() throws Exception {
        NamespaceResolver nsResolver = new NamespaceResolver();
        nsResolver.put("ns0", "myNamespace");
        nsResolver.put("xsi", XMLConstants.SCHEMA_INSTANCE_URL);

        URL url = ClassLoader.getSystemResource(resourceName);
        DynamicEntity docWrapper = (DynamicEntity) jaxbUnmarshaller.unmarshal(url);

        DynamicEntity root = ((DynamicJAXBContext) jaxbContext).getValueByXPath(docWrapper, "ns0:root", nsResolver, DynamicEntity.class);
        assertNotNull(root);

        DynamicEntity anyAtt = root.get("anyAtt");
        DynamicEntity anyAttFromXPath = ((DynamicJAXBContext) jaxbContext).getValueByXPath(root, "any-att", nsResolver, DynamicEntity.class);
        assertEquals(anyAtt, anyAttFromXPath);

        DynamicEntity newAnyAtt = ((DynamicJAXBContext) jaxbContext).newDynamicEntity(ANYATT_CLASS_NAME);
        Properties anyAttProps = new Properties();
        anyAttProps.put(new QName("", "anyAtt1"), "333");
        anyAttProps.put(new QName("", "anyAtt2"), "fourfourfour");
        newAnyAtt.set("value", anyAttProps);

        ((DynamicJAXBContext) jaxbContext).setValueByXPath(root, "any-att", nsResolver, newAnyAtt);

        DynamicEntity newAnyAttFromXPath = ((DynamicJAXBContext) jaxbContext).getValueByXPath(root, "any-att", nsResolver, DynamicEntity.class);
        assertEquals(newAnyAtt, newAnyAttFromXPath);

        Object compColl = root.get("compColl");
        Object compCollFromXPath = ((DynamicJAXBContext) jaxbContext).getValueByXPath(root, "comp-coll/item", nsResolver, ArrayList.class);
        assertEquals(compColl, compCollFromXPath);
    }

    protected void compareObjects(Object controlObject, Object testObject) throws Exception {
        if (objectsAlreadyCheckedForEquality.contains(testObject)) {
            // To handle cyclic relationships, only check each pair of DynamicEntities once
            return;
        }

        if (controlObject == null && testObject == null) {
            // Nothing to check
            return;
        }

        if (controlObject == null && testObject != null) {
            fail("testObject was [" + testObject + "] but controlObject was [" + controlObject + "].");
        }

        if (controlObject != null && testObject == null) {
            fail("testObject was [" + testObject + "] but controlObject was [" + controlObject + "].");
        }

        if (testObject instanceof Element && controlObject instanceof Element) {
            Element testElement = (Element) testObject;
            Element controlElement = (Element) controlObject;

            boolean equal = true;
            try {
                if (testElement.getNodeType() == controlElement.ATTRIBUTE_NODE && controlElement.getNodeType() == controlElement.ATTRIBUTE_NODE) {
                    Attr att1 = (Attr) testElement;
                    Attr att2 = (Attr) controlElement;
                    equal = equal && att1.getNodeValue().equals(att2.getNodeValue());
                } else if (testElement.getNodeType() == controlElement.TEXT_NODE && controlElement.getNodeType() == controlElement.TEXT_NODE) {
                    Text text1 = (Text) testElement;
                    Text text2 = (Text) controlElement;
                    equal = equal && text1.getNodeValue().equals(text2.getNodeValue());
                } else if (testElement.getNodeType() == controlElement.ELEMENT_NODE && controlElement.getNodeType() == controlElement.ELEMENT_NODE) {
                    Element elem1 = (Element) testElement;
                    Element elem2 = (Element) controlElement;
                    equal = equal && elem1.getNodeName().equals(elem2.getNodeName());
                    equal = equal && (elem1.getChildNodes().getLength() == elem2.getChildNodes().getLength());
                    compareObjects(elem1.getFirstChild().getNodeValue(), elem2.getFirstChild().getNodeValue());
                }
                if (!equal) {
                    fail("testElement was [" + testElement + "] but controlElement was [" + controlElement + "].");
                }
            } catch (Exception e) {
                fail("Element comparison failed: " + e.getLocalizedMessage());
            }
            return;
        }

        if (testObject instanceof List && controlObject instanceof List) {
            if ((((List) testObject).size()) != (((List) controlObject).size())) {
                fail("testObject had [" + ((List) testObject).size() + "] elements but controlObject had [" + ((List) controlObject).size() + "].");
            }
            for (int i = 0; i < ((List) controlObject).size(); i++) {
                compareObjects(((List) controlObject).get(i), ((List) testObject).get(i));
            }
            // We've now checked all the elements so return
            return;
        }

        if (testObject.getClass().isArray() && controlObject.getClass().isArray()) {
            if (!Helper.comparePotentialArrays(testObject, controlObject)) {
                fail("controlObject [" + controlObject + "] is not equal to testObject [" + testObject + "].");
            }
            // We've now checked all the elements so return
            return;
        }

        if (controlObject instanceof DynamicEntityImpl && testObject instanceof DynamicEntityImpl) {
            DynamicEntityImpl dynamicControl = (DynamicEntityImpl) controlObject;
            DynamicEntityImpl dynamicTest = (DynamicEntityImpl) testObject;

            String controlClass = dynamicControl.getType().getClassName();
            String testClass = dynamicTest.getType().getClassName();
            if (!controlClass.equals(testClass)) {
                fail("testObject [" + testClass + "] and controlObject [" + controlClass + "] are not the same class.");
            }

            objectsAlreadyCheckedForEquality.add(testObject);

            if (dynamicControl.getType().getNumberOfProperties() != dynamicTest.getType().getNumberOfProperties()) {
                fail("testObject and controlObject did not have the same number of properties.");
            }

            List<String> propNames = dynamicControl.getType().getPropertiesNames();
            Iterator<String> it = propNames.iterator();
            while (it.hasNext()) {
                String propName = it.next();

                Object controlValue = dynamicControl.get(propName);
                Object testValue = dynamicTest.get(propName);

                compareObjects(controlValue, testValue);
            }
        } else {
            if (!testObject.equals(controlObject)) {
                fail("testObject was [" + testObject + "] but controlObject was [" + controlObject + "].");
            }
        }
    }

    public void xmlToObjectTest(Object testObject) throws Exception {
        log("\n**xmlToObjectTest**");
        log("Expected:");
        Object controlObject = getReadControlObject();
        if (null == controlObject) {
            log((String) null);
        } else {
            log(controlObject.toString());
        }
        log("Actual:");
        if (null == testObject) {
            log((String) null);
        } else {
            log(testObject.toString());
        }

        if ((controlObject instanceof XMLRoot) && (testObject instanceof XMLRoot)) {
            XMLRoot controlRoot = (XMLRoot) controlObject;
            XMLRoot testRoot = (XMLRoot) testObject;
            compareXMLRootObjects(controlRoot, testRoot);
        } else {
            objectsAlreadyCheckedForEquality = new ArrayList();
            compareObjects(controlObject, testObject);
        }
    }

}