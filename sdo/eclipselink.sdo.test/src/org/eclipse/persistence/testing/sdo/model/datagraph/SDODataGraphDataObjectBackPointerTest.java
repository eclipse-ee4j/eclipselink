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

package org.eclipse.persistence.testing.sdo.model.datagraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.namespace.QName;

import junit.textui.TestRunner;

import org.eclipse.persistence.sdo.SDODataGraph;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.helper.DefaultSchemaLocationResolver;
import org.eclipse.persistence.sdo.helper.ListWrapper;
import org.eclipse.persistence.sdo.helper.SDOXSDHelper;

import commonj.sdo.DataGraph;
import commonj.sdo.DataObject;
import commonj.sdo.Type;

public class SDODataGraphDataObjectBackPointerTest extends SDODataGraphDataObjectTestCases {
    /**
     * customer/item[] - containment = true 
     * porder/item[] - containment = false
     */

    public SDODataGraphDataObjectBackPointerTest(String name) {
        super(name);
    }

    public void setUp() {
        super.setUp();
        dataGraph = new SDODataGraph(aHelperContext);
    }

    // This function will exercise the updateDataGraph() function on SDODataObject.updateContainment()
    public void testSetDOFromOtherDataGraphWithMixedContainmentNonContainmentWithoutCStoDGwithoutCS() {
    	DefaultSchemaLocationResolver resolver = new DefaultSchemaLocationResolver(new HashMap<QName, String>());
    	registerTypes();
    	List<Type> types = getTypesToGenerateFrom();
        ((SDOXSDHelper)xsdHelper).generate(types, resolver);
        
        // Create target
    	DataObject aDataObjectTarget = createRootObject(types);
    	DataGraph aDataGraphTarget = dataGraph;
    	aDataGraphTarget.createRootObject(aDataObjectTarget.getType());
    	DataObject targetRoot = aDataGraphTarget.getRootObject();
    	// Check it
    	assertNotNull(targetRoot);
    	assertEquals(targetRoot.getType(), aDataGraphTarget.getType(URINAME, COMPANY_TYPENAME));    	
    	ListWrapper pOrdersTarget = (ListWrapper)targetRoot.getList(PO_PATH);
    	int poSize = pOrdersTarget.size();
    	assertEquals(0, poSize);
    	
        // Create source
    	DataObject aDataObjectSource = createRootObject(types);
    	DataGraph aDataGraphSource = new SDODataGraph(aHelperContext);
    	aDataGraphSource.createRootObject(aDataObjectSource.getType());
    	DataObject sourceRoot = aDataGraphSource.getRootObject();
    	// Check it
    	assertNotNull(sourceRoot);
    	assertEquals(sourceRoot.getType(), aDataGraphSource.getType(URINAME, COMPANY_TYPENAME));    	
    	ListWrapper pOrdersSource = (ListWrapper) aDataObjectSource.getList(PO_PATH);
    	int poSizeSource = pOrdersSource.size();
    	assertEquals(1, poSizeSource);

    	// Clear the target CS so that we enter only the DataGraph recursive loop
    	((SDODataObject)targetRoot)._setChangeSummary(null);
    	
    	// Get source DO
    	DataObject sourcePO = (DataObject)pOrdersSource.get(0);
    	
    	// DataGraph should be null before set
    	assertNull(sourcePO.getDataGraph());
    	
    	// Move the root object from one null data graph to the other
    	pOrdersTarget.add(sourcePO);

    	// DataGraph should be not be null before set
    	assertNotNull(sourcePO.getDataGraph());
    	// Target dataGraph should be set on all children
    	assertEquals(aDataGraphTarget, sourcePO.getDataGraph());

    	DataObject copyPOshipTo = (DataObject)sourcePO.get("shipTo");
    	DataObject copyPObillTo = (DataObject)sourcePO.get("billTo");
    	DataObject copyPOitem1 = (DataObject)sourcePO.get("item[1]");
    	DataObject copyPOitem2 = (DataObject)sourcePO.get("item[2]");
    	
    	// Check dataGraph back pointers recursively (containment only)
    	List<DataObject> copyPreOrderList = preOrderTraversalDataObjectList(sourcePO);
    	assertNotNull(copyPreOrderList);
    	assertEquals(5, copyPreOrderList.size());
    	copyPreOrderList.contains(copyPOshipTo);
    	copyPreOrderList.contains(copyPObillTo);
    	copyPreOrderList.contains(copyPOitem1);
    	copyPreOrderList.contains(copyPOitem2);

    	assertNotNull(copyPOshipTo.getDataGraph());
    	assertEquals(aDataGraphTarget, copyPOshipTo.getDataGraph());
    	assertNotNull(copyPObillTo.getDataGraph());
    	assertEquals(aDataGraphTarget, copyPObillTo.getDataGraph());
    	// item property is unidirectional or containment=false - still in the dataGraph in this case
    	assertNotNull(copyPOitem1.getDataGraph());
    	assertEquals(aDataGraphTarget, copyPOitem1.getDataGraph());
    	assertNotNull(copyPOitem2.getDataGraph());
    	assertEquals(aDataGraphTarget, copyPOitem2.getDataGraph());
    }

    // This function will exercise the updateChangeSummaryAndDataGraph() function on SDODataObject.updateContainment()
    public void testSetDOFromOtherDataGraphWithMixedContainmentNonContainmentWithoutCStoDGwithCS() {
    	DefaultSchemaLocationResolver resolver = new DefaultSchemaLocationResolver(new HashMap<QName, String>());
    	registerTypes();
    	List<Type> types = getTypesToGenerateFrom();
        ((SDOXSDHelper)xsdHelper).generate(types, resolver);
        
        // Create target
    	DataObject aDataObjectTarget = createRootObject(types);
    	DataGraph aDataGraphTarget = dataGraph;
    	aDataGraphTarget.createRootObject(aDataObjectTarget.getType());
    	DataObject targetRoot = aDataGraphTarget.getRootObject();
    	// Check it
    	assertNotNull(targetRoot);
    	assertEquals(targetRoot.getType(), aDataGraphTarget.getType(URINAME, COMPANY_TYPENAME));    	
    	ListWrapper pOrdersTarget = (ListWrapper)targetRoot.getList(PO_PATH);
    	int poSize = pOrdersTarget.size();
    	assertEquals(0, poSize);
    	
    	// Add a porder (null DG source)
    	
        // Create source
    	DataObject aDataObjectSource = createRootObject(types);
    	DataGraph aDataGraphSource = new SDODataGraph(aHelperContext);
    	aDataGraphSource.createRootObject(aDataObjectSource.getType());
    	DataObject sourceRoot = aDataGraphSource.getRootObject();
    	// Check it
    	assertNotNull(sourceRoot);
    	assertEquals(sourceRoot.getType(), aDataGraphSource.getType(URINAME, COMPANY_TYPENAME));    	
    	ListWrapper pOrdersSource = (ListWrapper) aDataObjectSource.getList(PO_PATH);
    	int poSizeSource = pOrdersSource.size();
    	assertEquals(1, poSizeSource);

    	// Get source DO
    	DataObject sourcePO = (DataObject)pOrdersSource.get(0);
    	
    	// DataGraph should be null before set
    	assertNull(sourcePO.getDataGraph());
    	
    	// Move the root object from one null data graph to the other
    	pOrdersTarget.add(sourcePO);

    	// DataGraph should be not be null before set
    	assertNotNull(sourcePO.getDataGraph());
    	// Target dataGraph should be set on all children    	
    	assertEquals(aDataGraphTarget, sourcePO.getDataGraph());

    	DataObject copyPOshipTo = (DataObject)sourcePO.get("shipTo");
    	DataObject copyPObillTo = (DataObject)sourcePO.get("billTo");
    	DataObject copyPOitem1 = (DataObject)sourcePO.get("item[1]");
    	DataObject copyPOitem2 = (DataObject)sourcePO.get("item[2]");
    	
    	// Check dataGraph back pointers recursively (containment only)
    	List<DataObject> copyPreOrderList = preOrderTraversalDataObjectList(sourcePO);
    	assertNotNull(copyPreOrderList);
    	assertEquals(5, copyPreOrderList.size());
    	copyPreOrderList.contains(copyPOshipTo);
    	copyPreOrderList.contains(copyPObillTo);
    	copyPreOrderList.contains(copyPOitem1);
    	copyPreOrderList.contains(copyPOitem2);

    	assertNotNull(copyPOshipTo.getDataGraph());
    	assertEquals(aDataGraphTarget, copyPOshipTo.getDataGraph());
    	assertNotNull(copyPObillTo.getDataGraph());
    	assertEquals(aDataGraphTarget, copyPObillTo.getDataGraph());
    	// item property is unidirectional or containment=false - still in the dataGraph in this case
    	assertNotNull(copyPOitem1.getDataGraph());
    	assertEquals(aDataGraphTarget, copyPOitem1.getDataGraph());
    	assertNotNull(copyPOitem2.getDataGraph());
    	assertEquals(aDataGraphTarget, copyPOitem2.getDataGraph());
    }
    
    public void testDetachNonContainmentDOwithMixedContainmentNonContainmentWithoutCStoDGwithCS() {
    	DefaultSchemaLocationResolver resolver = new DefaultSchemaLocationResolver(new HashMap<QName, String>());
    	registerTypes();
    	List<Type> types = getTypesToGenerateFrom();
        ((SDOXSDHelper)xsdHelper).generate(types, resolver);
        //log(generatedSchema);
    	dataGraph.createRootObject(URINAME, COMPANY_TYPENAME);//aDataObject.getType());
    	DataObject aWrappedDO = dataGraph.getRootObject();
    	assertNotNull(aWrappedDO);
    	// TODO
    	assertEquals(aWrappedDO.getType(), dataGraph.getType(URINAME, COMPANY_TYPENAME));

    	DataObject dgDO = populateRootDataGraphDataObject(dataGraph.getRootObject(), types);
    	ListWrapper pOrders = (ListWrapper)dgDO.getList(PO_PATH);
    	int poSize = pOrders.size();
    	assertEquals(1, poSize);

    	// Detach DO    	
    	DataObject porder1DO = (DataObject)pOrders.get(0);
    	int porder1ItemsSize = ((ListWrapper)porder1DO.get("item")).size();
    	DataObject ncItem1DO = (DataObject)(porder1DO).get("item[1]");
    	DataObject detachedDO = ncItem1DO;
    	ncItem1DO.detach();

    	
    	ListWrapper pOrdersDGList = (ListWrapper)dgDO.getList(PO_PATH); 
    	int poSizeDG =  pOrdersDGList.size();
    	assertEquals(porder1ItemsSize - 1, poSizeDG);
    	
    	assertEquals(null, detachedDO.getDataGraph());
    	DataObject copyPOitem2 = (DataObject)porder1DO.get("item[1]"); // Remaining item
    	/**
    	 * item property is unidirectional or containment=false - therefore not in the dataGraph.
    	 * However, if the item also has a containment=true reference to itself then it does reference the dataGraph
    	 */    	
    	assertNull(ncItem1DO.getDataGraph());
    	assertNotNull(copyPOitem2);
    	assertNull(copyPOitem2.getDataGraph());

    	// Check that the CS is not set on all nodes including non-containment ones
    	assertNull(ncItem1DO.getChangeSummary());
    	
    }

    public void testAddDOwithMixedContainmentNonContainmentWithoutCStoDGwithCS() {
    	DefaultSchemaLocationResolver resolver = new DefaultSchemaLocationResolver(new HashMap<QName, String>());
    	registerTypes();
    	List<Type> types = getTypesToGenerateFrom();
        ((SDOXSDHelper)xsdHelper).generate(types, resolver);
        //log(generatedSchema);
    	DataObject aDataObject = createRootObject(types);
    	dataGraph.createRootObject(aDataObject.getType());
    	DataObject aWrappedDO = dataGraph.getRootObject();
    	assertNotNull(aWrappedDO);
    	assertEquals(aWrappedDO.getType(), dataGraph.getType(URINAME, COMPANY_TYPENAME));
    	
    	DataObject dgDO = dataGraph.getRootObject();
    	ListWrapper pOrders = (ListWrapper)aDataObject.getList(PO_PATH);
    	int poSize = pOrders.size();
    	assertEquals(1, poSize);
    	
    	// Create detached DO
    	DataObject copyPO = (DataObject)pOrders.get(0);
    	
    	// Create a new ListWrapper and set it on the DG DO
    	List<DataObject> aList = new ArrayList<DataObject>();
    	aList.add(copyPO);
    	// Add DO to DG-DO - there is no existing elements
    	dgDO.set("porder", aList);
    	ListWrapper pOrdersDGList = (ListWrapper)dgDO.getList(PO_PATH); 
    	int poSizeDG =  pOrdersDGList.size();
    	// or
    	//pOrdersDGList.add(copyPO);
    	assertEquals(poSize, poSizeDG);
    	
    	assertEquals(dataGraph, copyPO.getDataGraph());
    	DataObject copyPOshipTo = (DataObject)copyPO.get("shipTo");
    	DataObject copyPObillTo = (DataObject)copyPO.get("billTo");
    	DataObject copyPOitem1 = (DataObject)copyPO.get("item[1]");
    	DataObject copyPOitem2 = (DataObject)copyPO.get("item[2]");
    	
    	// Check dataGraph back pointers recursively (containment only)
    	List<DataObject> copyPreOrderList = preOrderTraversalDataObjectList(copyPO);
    	assertNotNull(copyPreOrderList);
    	assertEquals(5, copyPreOrderList.size());
    	copyPreOrderList.contains(copyPOshipTo);
    	copyPreOrderList.contains(copyPObillTo);
    	copyPreOrderList.contains(copyPOitem1);
    	copyPreOrderList.contains(copyPOitem2);

//    	for(DataObject aDO : copyPreOrderList) {
//    		assertEquals(dataGraph, aDO.getDataGraph()); // Check containment=true|false nodes
//    	}
    	
    	assertNotNull(copyPOshipTo.getDataGraph());
    	assertNotNull(copyPObillTo.getDataGraph());
    	// item property is unidirectional or containment=false - still in the dataGraph in this case
    	assertNotNull(copyPOitem1.getDataGraph());
    	assertNotNull(copyPOitem2.getDataGraph());
    	
    	// Check that the CS is set on all nodes including non-containment ones
    	assertNotNull(copyPOitem1.getChangeSummary());
    	
    }

    public void testDetachDOwithMixedContainmentNonContainmentWithoutCStoDGwithCS() {
    	DefaultSchemaLocationResolver resolver = new DefaultSchemaLocationResolver(new HashMap<QName, String>());
    	registerTypes();
    	List<Type> types = getTypesToGenerateFrom();
        String generatedSchema = ((SDOXSDHelper)xsdHelper).generate(types, resolver);
        //log(generatedSchema);
    	dataGraph.createRootObject(URINAME, COMPANY_TYPENAME);//aDataObject.getType());
    	DataObject aWrappedDO = dataGraph.getRootObject();
    	assertNotNull(aWrappedDO);
    	// TODO
    	assertEquals(aWrappedDO.getType(), dataGraph.getType(URINAME, COMPANY_TYPENAME));

    	DataObject dgDO = populateRootDataGraphDataObject(dataGraph.getRootObject(), types);
    	ListWrapper pOrders = (ListWrapper)dgDO.getList(PO_PATH);
    	int poSize = pOrders.size();
    	assertEquals(1, poSize);

    	// Detach DO    	
    	DataObject detachedDO = (DataObject)pOrders.get(0);
    	detachedDO.detach();

    	
    	ListWrapper pOrdersDGList = (ListWrapper)dgDO.getList(PO_PATH); 
    	int poSizeDG =  pOrdersDGList.size();
    	assertEquals(poSize - 1, poSizeDG);
    	
    	assertEquals(null, detachedDO.getDataGraph());
    	DataObject copyPOshipTo = (DataObject)detachedDO.get("shipTo");
    	DataObject copyPObillTo = (DataObject)detachedDO.get("billTo");
    	DataObject copyPOitem1 = (DataObject)detachedDO.get("item[1]");
    	DataObject copyPOitem2 = (DataObject)detachedDO.get("item[2]");
/*    	
    	// Check dataGraph back pointers recursively (containment only)
    	List<DataObject> copyPreOrderList = preOrderTraversalDataObjectList(detachedDO);
    	assertNotNull(copyPreOrderList);
    	assertEquals(5, copyPreOrderList.size());
    	copyPreOrderList.contains(copyPOshipTo);
    	copyPreOrderList.contains(copyPObillTo);
    	copyPreOrderList.contains(copyPOitem1);
    	copyPreOrderList.contains(copyPOitem2);
*/
    	assertNull(copyPOshipTo.getDataGraph());
    	assertNull(copyPObillTo.getDataGraph());
    	/**
    	 * item property is unidirectional or containment=false - therefore not in the dataGraph.
    	 * However, if the item also has a containment=true reference to itself then it does reference the dataGraph
    	 */
    	assertNull(copyPOitem1.getDataGraph());
    	assertNull(copyPOitem2.getDataGraph());

    	// Check that the CS is not set on all nodes including non-containment ones
    	assertNull(copyPOitem1.getChangeSummary());
    	
    }

    public void testDeleteDOwithMixedContainmentNonContainmentWithoutCStoDGwithCS() {
    	DefaultSchemaLocationResolver resolver = new DefaultSchemaLocationResolver(new HashMap<QName, String>());
    	registerTypes();
    	List<Type> types = getTypesToGenerateFrom();
        ((SDOXSDHelper)xsdHelper).generate(types, resolver);
        //log(generatedSchema);
    	dataGraph.createRootObject(URINAME, COMPANY_TYPENAME);//aDataObject.getType());
    	DataObject aWrappedDO = dataGraph.getRootObject();
    	assertNotNull(aWrappedDO);
    	// TODO
    	assertEquals(aWrappedDO.getType(), dataGraph.getType(URINAME, COMPANY_TYPENAME));

    	DataObject dgDO = populateRootDataGraphDataObject(dataGraph.getRootObject(), types);
    	ListWrapper pOrders = (ListWrapper)dgDO.getList(PO_PATH);
    	int poSize = pOrders.size();
    	assertEquals(1, poSize);
    	
    	// save objects before they are split apart
    	DataObject detachedDO = (DataObject)pOrders.get(0);
    	DataObject copyPOshipTo = (DataObject)detachedDO.get("shipTo");
    	DataObject copyPObillTo = (DataObject)detachedDO.get("billTo");
    	DataObject copyPOitem1 = (DataObject)detachedDO.get("item[1]");
    	DataObject copyPOitem2 = (DataObject)detachedDO.get("item[2]");

    	// Delete DO    	
    	detachedDO.delete();
    	
    	ListWrapper pOrdersDGList = (ListWrapper)dgDO.getList(PO_PATH); 
    	int poSizeDG =  pOrdersDGList.size();
    	assertEquals(poSize - 1, poSizeDG);
    	
    	assertEquals(null, detachedDO.getDataGraph());
/*    	
    	// Check dataGraph back pointers recursively (containment only)
    	List<DataObject> copyPreOrderList = preOrderTraversalDataObjectList(detachedDO);
    	assertNotNull(copyPreOrderList);
    	assertEquals(5, copyPreOrderList.size());
    	copyPreOrderList.contains(copyPOshipTo);
    	copyPreOrderList.contains(copyPObillTo);
    	copyPreOrderList.contains(copyPOitem1);
    	copyPreOrderList.contains(copyPOitem2);
*/
    	assertNull(copyPOshipTo.getDataGraph());
    	assertNull(copyPObillTo.getDataGraph());
    	/**
    	 * item property is unidirectional or containment=false - therefore not in the dataGraph.
    	 * However, if the item also has a containment=true reference to itself then it does reference the dataGraph
    	 */
    	assertNull(copyPOitem1.getDataGraph());
    	assertNull(copyPOitem2.getDataGraph());

    	// Check that the CS is not set on all nodes including non-containment ones
    	assertNull(copyPOitem1.getChangeSummary());
    	
    }

    public void testUnsetDOwithMixedContainmentNonContainmentWithoutCStoDGwithCS() {
    	DefaultSchemaLocationResolver resolver = new DefaultSchemaLocationResolver(new HashMap<QName, String>());
    	registerTypes();
    	List<Type> types = getTypesToGenerateFrom();
        String generatedSchema = ((SDOXSDHelper)xsdHelper).generate(types, resolver);
        //log(generatedSchema);
    	dataGraph.createRootObject(URINAME, COMPANY_TYPENAME);//aDataObject.getType());
    	DataObject aWrappedDO = dataGraph.getRootObject();
    	assertNotNull(aWrappedDO);
    	// TODO
    	assertEquals(aWrappedDO.getType(), dataGraph.getType(URINAME, COMPANY_TYPENAME));

    	DataObject dgDO = populateRootDataGraphDataObject(dataGraph.getRootObject(), types);
    	ListWrapper pOrders = (ListWrapper)dgDO.getList(PO_PATH);
    	int poSize = pOrders.size();
    	assertEquals(1, poSize);

    	// Delete DO    	
    	DataObject detachedDO = (DataObject)pOrders.get(0);
    	dgDO.unset(PO_PATH + "[1]");
    	
    	ListWrapper pOrdersDGList = (ListWrapper)dgDO.getList(PO_PATH); 
    	int poSizeDG =  pOrdersDGList.size();
    	assertEquals(poSize - 1, poSizeDG);
    	
    	assertEquals(null, detachedDO.getDataGraph());
    	DataObject copyPOshipTo = (DataObject)detachedDO.get("shipTo");
    	DataObject copyPObillTo = (DataObject)detachedDO.get("billTo");
    	DataObject copyPOitem1 = (DataObject)detachedDO.get("item[1]");
    	DataObject copyPOitem2 = (DataObject)detachedDO.get("item[2]");
/*    	
    	// Check dataGraph back pointers recursively (containment only)
    	List<DataObject> copyPreOrderList = preOrderTraversalDataObjectList(detachedDO);
    	assertNotNull(copyPreOrderList);
    	assertEquals(5, copyPreOrderList.size());
    	copyPreOrderList.contains(copyPOshipTo);
    	copyPreOrderList.contains(copyPObillTo);
    	copyPreOrderList.contains(copyPOitem1);
    	copyPreOrderList.contains(copyPOitem2);
*/
    	assertNull(copyPOshipTo.getDataGraph());
    	assertNull(copyPObillTo.getDataGraph());
    	/**
    	 * item property is unidirectional or containment=false - therefore not in the dataGraph.
    	 * However, if the item also has a containment=true reference to itself then it does reference the dataGraph
    	 */
    	assertNull(copyPOitem1.getDataGraph());
    	assertNull(copyPOitem2.getDataGraph());

    	// Check that the CS is not set on all nodes including non-containment ones
    	assertNull(copyPOitem1.getChangeSummary());
    }
     
    //public void testDeleteFromDataGraph() {    }
    //public void testDetachFromDataGraph() {    }
    //public void testUnsetFromDataGraph() {    }
    //public void testMoveBetweenDataGraphsUsingSet() {    }
  
    
    /**
     * Adding a child dataObject to an existing rootDataObject on a dataGraph (with/without changeSummary(s))
     * 
     * Start: (add DO2 as a child of DO1 - adding DO2 to the DG)
     * (target DG and DO1)
     *   DG
     *   	--> DO1
     *   		--> (CS1) - variable below
     *   
     *   (single CS on source)
     *   DO2
     *   	---> (CS2) - variable below
     *   or (multiple CS)
     *   DO2
     *   	---> DO3
     *   		---> (CS3) - variable below
     *   	---> DO4
     *   		---> (CS4) - variable below
     *   
     * End: (any CS2,3,4 are not merged into the single CS1 on the DG - exception occurs)
     *   DG
     *   	--> DO1
     *   		--> DO2    
     *   	--> (CS1) - variable below
     * 
     * Use Cases:
     * Target DG:DO1	Source DO2:		Operation:															Results:
     * --------------------------------------------------------------------------------------------------------------------------------------
     * cs=0						cs=0					: add DO (no cs) child to DG (no cs)					: no cs operations 
     * cs=0						cs=1					: add DO (with 1 cs) child to DG (no cs)			: cs is not moved to DG - exception
     * cs=0						cs=n (multiple)	: add DO (with multi-cs) child to DG (no cs)		: cs(s) not merged, not moved to DG - exception
     * cs=1						cs=0					: add DO (no cs) child to DG (with cs)				: no cs operations
     * cs=1						cs=1					: add DO (with 1 cs) child to DG (with cs)			: cs not merged with DG cs - exception
     * cs=1						cs=n (multiple)	: add DO (with multi-cs) child to DG (with cs)	: cs(s) not merged with DG cs - exception
     * 
     */
    //public void testAddChildDOwithoutCStoDGwithoutCS() {   }   	
    //public void testAddChildDOwith1CStoDGwithoutCS() {    }
    //public void testAddChildDOwithMulitpleCStoDGwithoutCS() {    }
    //public void testAddChildDOwithoutCStoDGwithCS() {    }
    //public void testAddChildDOwith1CStoDGwithCS() {    }
    //public void testAddChildDOwithMulipleCStoDGwithCS() {    }

    /**
     * Perform all operations where isContainment() check is performed in SDODataObject - but with the DO inside a DG 
     * so that non-containment DO's are treated as containment while in the DataGraph scope.
     */
    
    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.datagraph.SDODataGraphDataObjectBackPointerTest" };
        TestRunner.main(arguments);
    }
    
}
