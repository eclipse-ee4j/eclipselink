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
package org.eclipse.persistence.testing.sdo;

import commonj.sdo.ChangeSummary;
import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Sequence;
import commonj.sdo.helper.CopyHelper;
import commonj.sdo.helper.DataFactory;
import commonj.sdo.helper.EqualityHelper;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.TypeHelper;
import commonj.sdo.helper.XMLDocument;
import commonj.sdo.helper.XMLHelper;
import commonj.sdo.helper.XSDHelper;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.eclipse.persistence.sdo.SDOChangeSummary;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOSequence;
import org.eclipse.persistence.sdo.SDOSetting;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.ValueStore;
import org.eclipse.persistence.sdo.helper.SDOClassLoader;
import org.eclipse.persistence.sdo.helper.SDODataHelper;
import org.eclipse.persistence.sdo.helper.SDOHelperContext;
import org.eclipse.persistence.sdo.helper.SDOTypeHelper;
import org.eclipse.persistence.sdo.helper.SDOXMLHelper;
import org.eclipse.persistence.sdo.helper.SDOXSDHelper;
import commonj.sdo.Type;
import commonj.sdo.impl.HelperProvider;
import org.eclipse.persistence.Version;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.platform.xml.XMLComparer;

import static org.eclipse.persistence.sdo.SDOConstants.*;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SDOTestCase extends junit.framework.TestCase {
    public boolean useLogging = false;
    public boolean ignoreCRLF = false;
    public boolean customContext = false;
    public boolean loggingLevelFinest = false;
    public HelperContext aHelperContext;
    public TypeHelper typeHelper;
    public XMLHelper xmlHelper;
    public XSDHelper xsdHelper;
    public EqualityHelper equalityHelper;
    public CopyHelper copyHelper;
    public DataFactory dataFactory;
    public SDODataHelper dataHelper;
    public DocumentBuilder parser;
    public String classgenCompilePath; 
    public String tempFileDir;
    private SDOXMLComparer xmlComparer;

    protected static final String USER_DIR = System.getProperty("user.dir").replace('\\', '/');
    protected static final String FILE_PROTOCOL = USER_DIR.startsWith("/")? "file:" : "file:/";    
    protected static final String HTTP_PROTOCOL = "http://";
    protected static final String NON_DEFAULT_JAVA_PACKAGE_DIR = "org/example";  
    protected static final String NON_DEFAULT_JAVA_PACKAGE_NAME = "org.example";
    protected static final String NON_DEFAULT_URI = "http://www.example.org";
    
    public SDOTestCase(String name) {
        super(name);
        useLogging = Boolean.getBoolean("useLogging");
        ignoreCRLF = Boolean.getBoolean("ignoreCRLF");
        customContext = Boolean.getBoolean("customContext");
        loggingLevelFinest = Boolean.getBoolean("loggingLevelFinest");
		classgenCompilePath = System.getProperty("sdo.classgen.compile.path");
		tempFileDir = System.getProperty("tempFileDir");
        if(null == tempFileDir || tempFileDir.length() < 1) {
			tempFileDir = ".";
		}
	
        if (loggingLevelFinest && AbstractSessionLog.getLog().getLevel() != AbstractSessionLog.FINEST) {
            // override default INFO logging level for static logs
            AbstractSessionLog.getLog().setLevel(AbstractSessionLog.FINEST);
            AbstractSessionLog.getLog().log(AbstractSessionLog.FINEST, "{0} {1}", //
                    new Object[] {Version.getProduct(), Version.getVersionString()}, false);
        }

        // reverse the flags so that a false(from the flag not found) will not
        // default to a static context
    }
    
    public void setUp() {
        xmlComparer = new SDOXMLComparer();
        if (customContext) {
            // default to instance of a HelperContext
            aHelperContext = new SDOHelperContext();
        } else {
            // default to static context (Global)
            aHelperContext = HelperProvider.getDefaultContext();
        }
        typeHelper = aHelperContext.getTypeHelper();
        xmlHelper = aHelperContext.getXMLHelper();
        xsdHelper = aHelperContext.getXSDHelper();
        equalityHelper = aHelperContext.getEqualityHelper();
        copyHelper = aHelperContext.getCopyHelper();
        dataFactory = aHelperContext.getDataFactory();
        // TODO: we should be using the DataHelper interface
        dataHelper = (SDODataHelper)aHelperContext.getDataHelper();

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);
        builderFactory.setIgnoringElementContentWhitespace(true);
        try {
            parser = builderFactory.newDocumentBuilder();
        } catch (Exception e) {
            fail("Could not create parser.");
            e.printStackTrace();
        }
        
        ((SDOTypeHelper) typeHelper).reset();
        ((SDOXMLHelper) xmlHelper).reset();
        ((SDOXSDHelper) xsdHelper).reset();
    }
    
    public void tearDown() throws Exception {
        
        ((SDOTypeHelper) typeHelper).reset();
        ((SDOXMLHelper) xmlHelper).reset();
        ((SDOXSDHelper) xsdHelper).reset();
        
        typeHelper = null;
        xmlHelper = null;
        xsdHelper = null;
        equalityHelper = null;
        copyHelper = null;
        dataFactory = null;
        parser = null;
        aHelperContext = null;
        
        
    }
        
    public void assertXMLIdentical(Document control, Document test) {
        boolean isEqual = xmlComparer.isNodeEqual(control, test);
        String controlString = "";
        String testString = "";
        
        if (!isEqual) {
            org.eclipse.persistence.platform.xml.XMLTransformer t = 
                org.eclipse.persistence.platform.xml.XMLPlatformFactory.getInstance().getXMLPlatform().newXMLTransformer();
            java.io.StringWriter controlWriter = new java.io.StringWriter();
            t.transform(control, controlWriter);
            
            t = org.eclipse.persistence.platform.xml.XMLPlatformFactory.getInstance().getXMLPlatform().newXMLTransformer();
            java.io.StringWriter testWriter = new java.io.StringWriter();
            t.transform(test, testWriter);

            controlString = controlWriter.toString();
            testString = testWriter.toString();
        }
        
        assertTrue("Documents are not equal.\nCONTROL:\n" + controlString + "\nTEST:\n" + testString, isEqual);
    }
    
    public void assertSchemaIdentical(Document control, Document test) {
        assertTrue("Node " + control + " is not equal to node " + test, xmlComparer.isSchemaEqual(control, test));
    }
    public String getName() {
        String longClassName = getClass().getName();
        String shortClassName = longClassName.substring(longClassName.lastIndexOf(".") + 1, longClassName.length() - 1);
        return shortClassName + SDO_XPATH_NS_SEPARATOR_FRAGMENT + super.getName();
    }

    protected void log(String string) {
        if (useLogging) {
            System.out.println(string);
        }
    }

    protected void log(List items) {
        if (useLogging) {
            for (int i = 0; i < items.size(); i++) {
                log(items.get(i));
            }
        }
    }

    protected void log(Object object) {
        if (useLogging) {
            if (object instanceof Type) {
                log((Type) object);
            } else {
                System.out.println(object.toString());
            }
        }
    }

    protected void log(Type type) {
        if (useLogging) {
            System.out.println(type.getURI());
            System.out.println(type.getName());
        }
    }

    public String getSchema(String fileName) {
        String xsdSchema = EMPTY_STRING;
        FileInputStream is = null;
        try {
            is = new FileInputStream(fileName);           
            return getSchema(is, fileName);
        } catch (Exception e) {
            log(getClass().toString() + ": Reading error for : " + fileName + " message: " + e.getClass() + " " + e.getMessage());
        } finally {
            try {
                if (null != is) {
                    is.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return xsdSchema;
    }

 public String getSchema(InputStream is, String fileName) {
        String xsdSchema = EMPTY_STRING;        
        try {            
            byte[] bytes = new byte[is.available()];
            is.read(bytes);
            xsdSchema = new String(bytes);
            log(xsdSchema);
            return xsdSchema;
        } catch (Exception e) {
            log(getClass().toString() + ": Reading error for : " + fileName + " message: " + e.getClass() + " " + e.getMessage());
        } finally {
            try {
                if (null != is) {
                    is.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return xsdSchema;
    }

    /* Tree specific algorithms to aide in testing */
    /**
     * Return the depth of this dataObject from it's root
     * 
     * @return int
     */
    public int depth(DataObject aChild) {
        if (null == aChild.getContainer()) {
            // base case
            return 0;
        } else {
            // recursive case
            return 1 + depth(aChild.getContainer());
        }
    }

    // diff function required

    /**
     * Return an ArrayList of all the DataObjects in the tree in a preOrder
     * fashion
     */
    public List<DataObject> preOrderTraversalDataObjectList(SDODataObject currentDO) {
        return preOrderTraversalDataObjectList(currentDO, new ArrayList<DataObject>(), false, true);
    }

    public List<DataObject> preOrderTraversalDataObjectList(DataObject currentDO) {
        return preOrderTraversalDataObjectList((SDODataObject) currentDO, new ArrayList<DataObject>(), false, true);
    }

    /**
     * Return an ArrayList of all the DataObjects (including unset ones) in the
     * tree in a preOrder fashion
     */
    public List<DataObject> preOrderTraversalDataObjectList(SDODataObject currentDO, boolean countNullObjects) {
        return preOrderTraversalDataObjectList(currentDO, new ArrayList<DataObject>(), countNullObjects, true);
    }

    private List<DataObject> preOrderTraversalDataObjectList(SDODataObject currentDO, ArrayList<DataObject> currentList, boolean countNullObjects, boolean recurse) {
        if (currentDO != null) {
            // add yourself
            currentList.add(currentDO);
            // check DO's recursively
            List instanceProperties = currentDO.getInstanceProperties();
            SDOProperty nextProperty = null;
            Object value = null;
            if (recurse) {
                for (int i = 0; i < instanceProperties.size(); i++) {
                    nextProperty = (SDOProperty) instanceProperties.get(i);
                    value = currentDO.get(nextProperty);
                    boolean recurseHopefullyNotToInfinityPlease = true;
                    if (!nextProperty.getType().isChangeSummaryType() && !nextProperty.getType().isDataType()) {
                        // don't traverse into opposite properties to avoid an
                        // infinite loop
                        if (null != nextProperty.getOpposite()) {
                            recurseHopefullyNotToInfinityPlease = false;
                        }

                        if (nextProperty.isMany()) {
                            // iterate list
                            Object manyItem;

                            // use of delete while using an iterator will
                            // generate a ConcurrentModificationException
                            for (int index = 0; index < ((List) value).size(); index++) {
                                manyItem = ((List) value).get(index);
                                if (manyItem != null && manyItem instanceof SDODataObject) {
                                    preOrderTraversalDataObjectList((SDODataObject) manyItem, currentList, countNullObjects, recurseHopefullyNotToInfinityPlease);
                                }
                            }
                        } else {
                            if (value != null) {
                                preOrderTraversalDataObjectList((SDODataObject) value, currentList, countNullObjects, recurseHopefullyNotToInfinityPlease);
                            }
                        }
                    }
                }
            }
        } else {
            if (countNullObjects) {
                currentList.add(currentDO);
            }
        }
        return currentList;
    }

    protected void assertCreated(DataObject dataObject, ChangeSummary changeSummary) {
        assertTrue(changeSummary.isCreated(dataObject));
        assertFalse(changeSummary.isModified(dataObject));
        assertFalse(changeSummary.isDeleted(dataObject));
        // if we are at the root then there is no parent to track
        if (null != dataObject.getContainer()) {
            // check that parent (if not the root) sequence has been saved in
            // originalSequences (if dataObject is not an attribute - which it
            // is not)
            if (dataObject.getContainer().getSequence() != null) {
                assertNotNull(changeSummary.getOldSequence(dataObject.getContainer()));
            } else {
                assertNull(changeSummary.getOldSequence(dataObject.getContainer()));
            }
        }
        assertEquals(0, changeSummary.getOldValues(dataObject).size());
    }

    protected void assertModified(DataObject dataObject, ChangeSummary changeSummary) {
        assertFalse(changeSummary.isCreated(dataObject));
        assertTrue(changeSummary.isModified(dataObject));
        assertFalse(changeSummary.isDeleted(dataObject));
    }

    protected void assertChildrenUnset(DataObject dataobject) {
        // verify all properties are unset and all many properties are size 0
        Iterator anIterator = dataobject.getType().getProperties().iterator();
        while (anIterator.hasNext()) {
            Property aProperty = (Property) anIterator.next();
            Object aPropertyValue = dataobject.get(aProperty);
            assertFalse(dataobject.isSet(aProperty));
            // all properties must be unset
            if (aProperty.isMany()) {
                assertSame(((List) aPropertyValue).size(), 0);
            } else {
            	if(!aProperty.getType().isDataType()) {
            		assertEquals(aProperty.getDefault(),aPropertyValue);
            		// assertSame(aProperty.getDefault(), dataobject.get(aProperty));
            	} else {
            		// JIRA-253: we return a wrapped numeric primitive when it has no default
            		Type aType = aProperty.getType();
                	if (aType.equals(SDO_BOOLEAN)) {
                		assertEquals(false, ((Boolean)aPropertyValue).booleanValue());
                	} else if (aType.equals(SDO_BYTE)) {
                		assertEquals(0, ((Byte)aPropertyValue).byteValue());
                	} else if (aType.equals(SDO_CHARACTER)) {
                		assertEquals(0, ((Character)aPropertyValue).charValue());
                	} else if (aType.equals(SDO_DOUBLE)) {
                		assertEquals(0, ((Double)aPropertyValue).doubleValue());
                	} else if (aType.equals(SDO_FLOAT)) {
                		assertEquals(0, ((Float)aPropertyValue).floatValue());
                	} else if (aType.equals(SDO_INT)) {
                		assertEquals(0, ((Integer)aPropertyValue).intValue());
                	} else if (aType.equals(SDO_SHORT)) {
                		assertEquals(0, ((Short)aPropertyValue).shortValue());
                	} else if (aType.equals(SDO_LONG)) {
                		assertEquals(0, ((Long)aPropertyValue).longValue());
                	}
            	}                                
            }
        }
    }

    protected void assertDeleted(DataObject dataobject, ChangeSummary changeSummary) {
        assertDeleted(dataobject, changeSummary, true, false, false);
    }

    protected void assertDeleted(DataObject dataobject, ChangeSummary changeSummary, boolean nullContainer) {
        assertDeleted(dataobject, changeSummary, nullContainer, false, false);
    }

    // for objects that were detached and then (re)set - their container and cs
    // will not be null but they will have oldSettings/deletedList items
    protected void assertDeleted(DataObject dataObject, ChangeSummary changeSummary, boolean nullContainer, boolean testLoadSave) {
        assertDeleted(dataObject, changeSummary, nullContainer, false, testLoadSave);
        assertChildrenUnset(dataObject);
    }

    protected void assertDeleted(DataObject dataObject, ChangeSummary changeSummary, boolean nullContainer, boolean isReAttached, boolean testLoadSave) {
        assertFalse(changeSummary.isCreated(dataObject));
        assertFalse(changeSummary.isModified(dataObject));
        if (!isReAttached) {
            assertTrue(((SDOChangeSummary) changeSummary).isDeleted(dataObject));
            if (dataObject.getSequence() != null) {
                assertNotNull(changeSummary.getOldSequence(dataObject));
            } else {
                assertNull(changeSummary.getOldSequence(dataObject));
            }
        } else {
            assertFalse(((SDOChangeSummary) changeSummary).isDeleted(dataObject));
        }

        int propertySize = dataObject.getType().getProperties().size();
        int oldValuesSize = changeSummary.getOldValues(dataObject).size();

        assertEquals(propertySize, oldValuesSize);

        // for objects that were detached and then (re)set - their container and
        // cs will not be null but they will have oldSettings/deletedList items
        if (nullContainer) {
            assertNull(dataObject.getContainer());
            // verify that the cs is not set on deleted/detached objects
            assertNull(dataObject.getChangeSummary());
        }

        assertChildrenUnset(dataObject);
    }

    // detached objects' properties are intact and not unset
    protected void assertDetached(DataObject dataobject, ChangeSummary changeSummary) {
        assertDetached(dataobject, changeSummary, true, false, false);
    }

    // we delete/detach and then (Re)set - deletedSet is cleared but oldSettings
    // remain (for now until we have smart undo code)
    protected void assertDetachedAndReset(DataObject dataobject, ChangeSummary changeSummary, boolean nullContainment) {
        assertDetached(dataobject, changeSummary, nullContainment, true, false);
    }

    protected void assertDetached(DataObject dataobject, ChangeSummary changeSummary, boolean nullContainment) {
        assertDetached(dataobject, changeSummary, nullContainment, false, false);
    }

    protected void assertDetached(DataObject dataobject, ChangeSummary changeSummary, boolean nullContainment, boolean isReAttached) {
        assertDetached(dataobject, changeSummary, nullContainment, isReAttached, false);
    }

    // isReAttached means that the property was detached and then (re)set - no
    // deletedSet entry but oldSettings remain
    protected void assertDetached(DataObject dataobject, ChangeSummary changeSummary, boolean nullContainment, boolean isReAttached, boolean testLoadSave) {
        assertFalse(changeSummary.isCreated(dataobject));
        assertFalse(changeSummary.isModified(dataobject));
        if (!isReAttached) {
            assertTrue(((SDOChangeSummary) changeSummary).isDeleted(dataobject));
        } else {
            assertFalse(((SDOChangeSummary) changeSummary).isDeleted(dataobject));
        }

        int propertySize = dataobject.getType().getProperties().size();
        int oldValuesSize = changeSummary.getOldValues(dataobject).size();

        assertEquals(propertySize, oldValuesSize);

        // for objects that were detached and then (re)set - their container and
        // cs will not be null but they will have oldSettings/deletedList items
        if (nullContainment) {
            assertNull(dataobject.getContainer());
            // verify that the cs is not set on deleted/detached objects
            assertNull(dataobject.getChangeSummary());
        }
    }

    protected void assertUnchanged(DataObject dataobject, ChangeSummary changeSummary) {
        assertFalse(changeSummary.isCreated(dataobject));
        assertFalse(changeSummary.isModified(dataobject));
        assertFalse(changeSummary.isDeleted(dataobject));

        assertEquals(0, changeSummary.getOldValues(dataobject).size());
    }

    // inOrderNodeList, preOrderNodeList, postOrderNodeList
    // function to monitor actual values inside the oldSetting HashMap
    protected void checkOldSettingsValues(String values, SDOChangeSummary aCS, List dataObjectList) {
        SDODataObject aDataObject = null;
        for (int i = 0; i < dataObjectList.size(); i++) {
            aDataObject = (SDODataObject) dataObjectList.get(i);
            assertEquals(Integer.parseInt(values.substring(i, i + 1)), aCS.getOldValues(aDataObject).size());
        }
    }

    protected void checkOldContainers(SDOChangeSummary aCS,//
    List dataObjectChildList, List dataObjectContainerList) {// we need
                                                                // generics here
        SDODataObject aChildDataObject = null;
        SDODataObject aContainerDataObject = null;
        for (int i = 0; i < dataObjectChildList.size(); i++) {
            aChildDataObject = (SDODataObject) dataObjectChildList.get(i);
            aContainerDataObject = (SDODataObject) dataObjectContainerList.get(i);
            assertEquals(aChildDataObject, aCS.getOldContainer(aContainerDataObject));
        }
    }

    /*
     * ChangeSummary specific functions for undoChanges
     */
    /**
     * 
     * @param currentDO
     * @param isCSonAncestor
     */
    public void assertChangeSummaryStatusIfClearedIfCSIsAncestor(DataObject currentDO, boolean isCSonAncestor) {
        if (currentDO != null) {
            // check current DO
            if (isCSonAncestor) {
                assertNull(((SDODataObject) currentDO).getChangeSummary());
            } else {
                assertNotNull(((SDODataObject) currentDO).getChangeSummary());
            }
            // check DO's recursively
            List instanceProperties = currentDO.getInstanceProperties();
            for (int i = 0; i < instanceProperties.size(); i++) {
                SDOProperty nextProperty = (SDOProperty) instanceProperties.get(i);
                Object value = currentDO.get(nextProperty);

                if (!nextProperty.getType().isChangeSummaryType() && !nextProperty.getType().isDataType() && value != null) {
                    if (nextProperty.isMany()) {
                        // iterate list
                        Object manyItem;

                        // use of delete while using an iterator will generate a
                        // ConcurrentModificationException
                        for (int index = 0; index < ((List) value).size(); index++) {
                            manyItem = ((List) value).get(index);
                            if (manyItem != null) {
                                assertChangeSummaryStatusIfClearedIfCSIsAncestor((SDODataObject) manyItem, isCSonAncestor);
                            }
                        }
                    } else {
                        assertChangeSummaryStatusIfClearedIfCSIsAncestor((SDODataObject) value, isCSonAncestor);
                    }
                }
            }
        }
    }

    /**
     * 
     * @param aChangeSummary
     * @param undoneDO
     * @param originalDO
     */
    protected void assertUndoChangesEqualToOriginal(ChangeSummary aChangeSummary,//
    DataObject undoneDO, DataObject originalDO) {
        // get logging flag before
        boolean loggingStateBefore = aChangeSummary.isLogging();
        aChangeSummary.undoChanges();
        assertTrue(loggingStateBefore == aChangeSummary.isLogging());

        // verify that the model has been returned to its original base state
        assertTrue(equalityHelper.equal(undoneDO, originalDO));
        // the objects are deep copies of each other but not the actual same
        // purchase order
        assertFalse(undoneDO == originalDO);
        // verify that CS is cleared but logging is unchanged
        assertTrue(aChangeSummary.getChangedDataObjects().size() == 0);
        assertTrue(aChangeSummary.getOldValues(undoneDO).size() == 0);
        assertTrue(aChangeSummary.getOldValues(originalDO).size() == 0);
    }

    /**
     * 
     * @param aRootObject
     */
    // test undo when logging is off (with previous changes)
    protected void assertValueStoresInitializedAfterLoggingOn(DataObject aRootObject) {
        // verify logging is on
        assertTrue(aRootObject.getChangeSummary().isLogging());
        // verify original VS is null and save a copy of current VS for object
        // identity testing after undo
        ValueStore aCurrentValueStore = ((SDODataObject) aRootObject)._getCurrentValueStore();
        assertNotNull(aCurrentValueStore);
        ValueStore anOriginalValueStore = (ValueStore) ((SDOChangeSummary) aRootObject.getChangeSummary()).getOriginalValueStores().get(aRootObject);
        assertNull(anOriginalValueStore);
    }

    /**
     * 
     * @param aRootObject
     * @param aCurrentValueStoreAfterLoggingFirstOnParam
     */
    protected void assertValueStoresCopiedAndSwappedAfterFirstModifyOperation(DataObject aRootObject, ValueStore aCurrentValueStoreAfterLoggingFirstOnParam) {
        // verify logging is on
        assertTrue(aRootObject.getChangeSummary().isLogging());
        assertNotNull(aCurrentValueStoreAfterLoggingFirstOnParam);
        ValueStore anOriginalValueStoreAfterOperation = (ValueStore) ((SDOChangeSummary) aRootObject.getChangeSummary()).getOriginalValueStores().get(aRootObject);
        ValueStore aCurrentValueStoreAfterOperation = ((SDODataObject) aRootObject)._getCurrentValueStore();
        assertNotNull(anOriginalValueStoreAfterOperation);
        assertNotNull(aCurrentValueStoreAfterOperation);
        assertTrue(anOriginalValueStoreAfterOperation == aCurrentValueStoreAfterLoggingFirstOnParam);
    }

    protected void assertSequencesCopiedAndSwappedAfterFirstModifyOperation(DataObject aRootObject, Sequence aCurrentSequenceAfterLoggingFirstOnParam) {
        // verify logging is on
        assertTrue(aRootObject.getChangeSummary().isLogging());
        assertNotNull(aCurrentSequenceAfterLoggingFirstOnParam);
        Sequence anOriginalSequenceAfterOperation = (Sequence) ((SDOChangeSummary) aRootObject.getChangeSummary()).getOriginalSequences().get(aRootObject);
        Sequence aCurrentSequenceAfterOperation = ((SDODataObject) aRootObject).getSequence();
        assertNotNull(anOriginalSequenceAfterOperation);
        assertNotNull(aCurrentSequenceAfterOperation);
        assertTrue(anOriginalSequenceAfterOperation == aCurrentSequenceAfterLoggingFirstOnParam);
    }

    /**
     * 
     * @param aRootObject
     * @param aCurrentValueStoreAfterLoggingFirstOnParam
     */
    protected void assertValueStoresReturnedToStartStateAfterUndoChanges(DataObject aRootObject, ValueStore aCurrentValueStoreAfterLoggingFirstOnParam) {
        // verify logging is on
        assertTrue(aRootObject.getChangeSummary().isLogging());
        ValueStore anOriginalValueStoreAfterUndo = (ValueStore) ((SDOChangeSummary) aRootObject.getChangeSummary()).getOriginalValueStores().get(aRootObject);
        ValueStore aCurrentValueStoreAfterUndo = ((SDODataObject) aRootObject)._getCurrentValueStore();
        assertNull(anOriginalValueStoreAfterUndo);
        assertNotNull(aCurrentValueStoreAfterUndo);
        // we return the original value store back to the current VS
        assertTrue(aCurrentValueStoreAfterUndo == aCurrentValueStoreAfterLoggingFirstOnParam);
    }

    /**
     * 
     * @param aRootObject
     * @param aCurrentSequenceAfterLoggingFirstOnParam
     */
    protected void assertSequencesReturnedToStartStateAfterUndoChanges(DataObject aRootObject, Sequence aCurrentSequenceAfterLoggingFirstOnParam) {
        // verify logging is on
        assertTrue(aRootObject.getChangeSummary().isLogging());
        SDOSequence anOriginalSequenceAfterUndo = (SDOSequence) ((SDOChangeSummary) aRootObject.getChangeSummary()).getOriginalSequences().get(aRootObject);
        SDOSequence aCurrentSequenceAfterUndo = (SDOSequence) ((SDODataObject) aRootObject).getSequence();
        assertNull(anOriginalSequenceAfterUndo);
        assertNotNull(aCurrentSequenceAfterUndo);
        // we return the sequence back to the current VS
        assertEquals(aCurrentSequenceAfterUndo.size(), aCurrentSequenceAfterLoggingFirstOnParam.size());
        assertTrue(compareSequences(aCurrentSequenceAfterUndo, (SDOSequence) aCurrentSequenceAfterLoggingFirstOnParam, true));
    }

    /**
     * INTERNAL: Return whether the 2 sequences are equal. Element properties
     * and unstructured text will be compared - attributes are out of scope.
     * <p>
     * For shallow equal - only dataType=true objects are compared, DataObject
     * values are ignored but should be defaults. Note: A setting object should
     * handle its own isEqual() behavior
     * 
     * @param aSequence
     * @param aSequenceCopy
     * @param isDeep
     * @return
     */
    public boolean compareSequences(SDOSequence aSequence, SDOSequence aSequenceCopy, boolean isDeep) {
        // corner case: isSequenced set to true after type definition had not
        // sequence
        if (null == aSequence && null == aSequenceCopy) {
            return true;
        }
        // both sequences must be null
        if (null == aSequence || null == aSequenceCopy) {
            return false;
        }
        // for shallow equal - match whether we skipped creating settings or set
        // value=null for shallow copies
        if (aSequence.size() != aSequenceCopy.size()) {
            return false;
        }

        // the settings inside the sequence must be new objects
        SDOSetting originalSetting = null;
        SDOSetting copySetting = null;
        List originalSettingsList = aSequence.getSettings();
        List copySettingsList = aSequenceCopy.getSettings();
        if (null == originalSettingsList || null == copySettingsList) {
            return false;
        }

        Property originalProperty = null;
        Property copyProperty = null;
        /**
         * For shallow equal when dataType is false we do not check this
         * setting, the value will be unset (default value) in the shallow copy.
         * orig v1=String v2=DataObject v3=String shallowcopy v1=String
         * v2=null(default) v3=String deepcopy v1=String v2=DataObject v3=String
         */
        for (int index = 0, size = aSequence.size(); index < size; index++) {
            originalSetting = (SDOSetting) originalSettingsList.get(index);
            copySetting = (SDOSetting) copySettingsList.get(index);

            originalProperty = originalSetting.getProperty();
            copyProperty = copySetting.getProperty();

            // we must handle null properties that represent unstructured text
            // both null = unstructured
            // one null = invalid state (return not equal)
            // both !null = valid state (check equality)
            if ((null == originalProperty && null != copyProperty) || (null != originalProperty && null == copyProperty)) {
                return false;
            }

            // the property field on the setting must point to the same property
            // instance as the original
            // handle both properties == null
            if (originalProperty != copyProperty) { 
                return false;
            }

            Object originalValue = originalSetting.getValue();
            Object copyValue = copySetting.getValue();

            // for unstructuredText (null property) and simple dataTypes we
            // check equality directly
            if (null == originalProperty || originalProperty.getType().isDataType()) {
                // if one of the values is null return false
                if (((null == originalValue) && (null != copyValue)) || //
                ((null != originalValue) && (null == copyValue))) {
                    return false;
                }
                // if both values are null - they are equal
                // we can also use !.equals
                if ((null != originalValue) && !originalValue.equals(copyValue)) {
                    return false;
                }
            } else {
                // For complex types
                // we do not need to check deep equality on dataObjects twice
                // here, just check instances
                // because the dataObject compare will iterate all the
                // properties of each dataObject
                // only compare DataObjects when in a deep equal
                if (isDeep) {
                    if (null != originalValue && null != copyValue) {
                        // setting.isSet is ignored for sequences
                        // perform a deep equal on the single item
                        // the values may not match their types - return false
                        // instead of a CCE
                        if (originalValue instanceof DataObject && copyValue instanceof DataObject) {
                            if (!equalityHelper.equal((DataObject) originalValue, (DataObject) copyValue)) {
                                return false;
                            }
                        } else {
                            return false;
                        }
                    } else {
                        // both values must be null to be equal
                        if ((null == originalValue && null != copyValue) || (null == copyValue && null != originalValue)) {
                            return false;
                        }
                    }
                } else {
                    /**
                     * For DataObjects in general anything that is deep equal is
                     * also shallow equal - but not the reverse. In the case of
                     * shallow equal on sequences. We can ignore the state of
                     * the 2 complex objects. UC1: if aSequenceCopy setting was
                     * from a shallowCopy then it will be unset. UC2: if
                     * aSequenceCopy setting was from a deepCopy or a reversed
                     * argument shallowCopy then it may be unset or set. We will
                     * not check for a default value on either sequence setting.
                     */
                }
            }
        }
        return true;
    }

    /**
     * Remove CR\LF=13\10 from source and target strings so that we can run the
     * suite on windows without assertEquals() failing. Use the system property
     * -DignoreCRLF=true
     * 
     * @param control
     * @param test
     * @param removeCRLF
     *            void
     * 
     */
    protected DataObject addProperty(DataObject parentType, String name, Type propType) {
        DataObject newProperty = parentType.createDataObject("property");
        SDOProperty prop = (SDOProperty) newProperty.getType().getProperty("name");
        newProperty.set(prop, name);
        prop = (SDOProperty) newProperty.getType().getProperty("type");
        newProperty.set(prop, propType);
        return newProperty;
    }

    /**
     * 
     * @param parentType
     * @param name
     * @param propType
     * @param isContainment
     * @return
     */
    protected DataObject addProperty(DataObject parentType, String name, Type propType, boolean isContainment) {
        DataObject newProperty = addProperty(parentType, name, propType);
        newProperty.setBoolean(CONTAINMENT, isContainment);
        return newProperty;
    }

    /**
     * 
     * @param parentType
     * @param name
     * @param propType
     * @param isContainment
     * @param isMany
     * @return
     */
    protected DataObject addProperty(DataObject parentType, String name, Type propType, boolean isContainment, boolean isMany) {
        DataObject newProperty = addProperty(parentType, name, propType, isContainment);
        newProperty.setBoolean(SDOXML_MANY, isMany);
        return newProperty;
    }

    protected DataObject addProperty(DataObject parentType, String name, Type propType, boolean isContainment, boolean isMany, boolean isElement) {
        DataObject newProperty = addProperty(parentType, name, propType, isContainment, isMany);
        if (isElement) {
            newProperty.set(XMLELEMENT_PROPERTY, true);

        }
        return newProperty;
    }
    
    protected DataObject addProperty(DataObject parentType, String name, DataObject propTypeDO, boolean isContainment, boolean isMany, boolean isElement) {
        DataObject newProperty = parentType.createDataObject("property");
        SDOProperty prop = (SDOProperty) newProperty.getType().getProperty("name");
        newProperty.set(prop, name);
        prop = (SDOProperty) newProperty.getType().getProperty("type");
        newProperty.set(prop, propTypeDO);
        
        newProperty.setBoolean(CONTAINMENT, isContainment);
        if (isElement) {
            newProperty.set(XMLELEMENT_PROPERTY, true);

        }
        return newProperty;
    }

    /**
     * Set the oc prop containing the idProp property - for unidirectional/bidirectional relationships
     */ 
    public void setIDPropForReferenceProperties(DataObject doType, Object idProp) {
    	// get the global property referencing the idProp property
        doType.set(ID_PROPERTY, idProp);
    }

    /**
     * INTERNAL:
     * Get the reference ID open content Property if it exists for this Type.
     * @return id Property or null
     */
    public SDOProperty getIDProp(Type aType) {
   		return (SDOProperty)aType.getProperty((String)aType.get(SDOConstants.ID_PROPERTY));
    }
    
    /**
     * 
     * @param uri
     * @param name
     * @return
     */
    protected DataObject defineType(String uri, String name) {
        DataObject newType = aHelperContext.getDataFactory().create(SDO_URL, TYPE);
        newType.set("uri", uri);
        newType.set("name", name);
        return newType;
    }

    public HelperContext getHelperContext() {
        return aHelperContext;
    }

    public void setHelperContext(HelperContext helperContext) {
        aHelperContext = helperContext;
    }

    protected void assertEqualsBytes(byte[] controlBytes, byte[] bytes) {
        if (controlBytes.length != bytes.length) {
            fail("Expected:" + log(controlBytes) + " but was:" + log(bytes));
        }

        for (int i = 0; i < controlBytes.length; i++) {
            if (controlBytes[i] != bytes[i]) {
                fail("Expected:" + log(controlBytes) + " but was:" + log(bytes));
            }
        }
    }

    protected String log(byte[] bytes) {
        String s = new String();
        for (int i = 0; i < bytes.length; i++) {
            s += bytes[i];
        }
        return s;
    }

    protected Document getDocument(String fileName) {
        FileInputStream inputStream = null;
        try{
          inputStream = new FileInputStream(fileName);
          return getDocument(inputStream);
          } catch (Exception e) {
            e.printStackTrace();
            fail("An error occurred loading the control document:" + fileName);
            return null;
        } finally {
            try {
                if (null != inputStream) {
                    inputStream.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }

    }
    
     protected Document getDocument(InputStream inputStream) {        
        try {
            
            Document document = parser.parse(inputStream);
            inputStream.close();
            return document;
        } catch (Exception e) {
            e.printStackTrace();
            fail("An error occurred loading the control document:"); // + inputStream.);
            return null;
        } finally {
            try {
                if (null != inputStream) {
                    inputStream.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    public void assertStringsEqual(String controlString, String generatedString) {
        String controlString2 = removeCRLFfromString(controlString);
        String generatedString2 = removeCRLFfromString(generatedString);
        // Allow Windows generated java code to pass comparison as well as Linux generated ones
//        if (ignoreCRLF) {
            assertEquals(controlString2, generatedString2);
//        } else {
//            assertEquals(controlString, generatedString);
//        }
    }

    protected void removeEmptyTextNodes(Node node) {
        NodeList nodeList = node.getChildNodes();
        Node childNode;
        for (int x = nodeList.getLength() - 1; x >= 0; x--) {
            childNode = nodeList.item(x);
            if (childNode.getNodeType() == Node.TEXT_NODE) {
                if (childNode.getNodeValue().trim().equals("")) {
                    node.removeChild(childNode);
                }
            } else if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                removeEmptyTextNodes(childNode);
            }
        }
    }

    protected String removeWhiteSpaceFromString(String s) {
        String returnString = s.replaceAll(" ", "");
        returnString = returnString.replaceAll("\n", "");
        returnString = returnString.replaceAll("\t", "");
        returnString = returnString.replaceAll("\r", "");

        return returnString;
    }

    protected String removeCRLFfromString(String s) {
        String returnString = s.replaceAll("\n", "");
        returnString = returnString.replaceAll("\t", "");
        returnString = returnString.replaceAll("\r", "");
        return returnString;
    }

    /**
     * Write to the output stream and return a success flag if no exceptions
     * thrown
     * 
     * @param anObject
     * @param uri
     * @param typename
     * @param aStream
     * @return success flag
     */
    public boolean writeXML(DataObject anObject, String uri, String typename, OutputStream aStream) {
        boolean success = true;
        // verify save
        if (useLogging) {
            try {
                xmlHelper.save(anObject, uri, typename, aStream);
            } catch (Exception e) {
                System.out.println("Exception: " + e.getMessage());
                e.printStackTrace();
                success = false;
            }
        }
        return success;
    }

    public DataObject loadXML(String filename, boolean resetChangeSummary) {
        DataObject anObject = null;
        try {
            XMLDocument document = xmlHelper.load(new FileInputStream(filename));
            anObject = document.getRootObject();
            // leave the cs alone?
            if (resetChangeSummary) {
                ChangeSummary aCS = anObject.getChangeSummary();
                if (aCS != null) {
                    aCS.endLogging();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail("_Error: SDOTestSuite.loadXML(): An error occurred loading the xml file " + filename);
        }
        return anObject;
    }
    
     protected void emptyAndDeleteDirectory(File f) {
        if (f.isDirectory()) {
            File[] files = f.listFiles();
            for (int i = 0; i < files.length; i++) {
                File next = files[i];
                if (next.isDirectory()) {
                    emptyAndDeleteDirectory(next);
                } else {
                    next.delete();
                }
            }
            f.delete();
        }
    }
    
     protected void deleteDirsOnExit(File f) {
        if (f.isDirectory()) {
            File[] files = f.listFiles();
            for (int i = 0; i < files.length; i++) {
                File next = files[i];
                if (next.isDirectory()) {
                    deleteDirsOnExit(next);
                } else {
                    next.deleteOnExit();
                }
            }
            f.deleteOnExit();
        }
    }
    
    protected String getClassPathForCompile(){
        return classgenCompilePath;
    }
 
}
