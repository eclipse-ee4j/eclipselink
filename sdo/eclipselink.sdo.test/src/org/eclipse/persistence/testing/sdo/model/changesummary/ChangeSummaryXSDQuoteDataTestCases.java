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
* dmccann - 1.0M8 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.sdo.model.changesummary;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.testing.sdo.SDOTestCase;

import commonj.sdo.ChangeSummary;
import commonj.sdo.DataObject;
import commonj.sdo.helper.XMLDocument;

import org.eclipse.persistence.sdo.helper.ListWrapper;
import org.w3c.dom.Document;

public class ChangeSummaryXSDQuoteDataTestCases extends SDOTestCase {
    private DataObject quoteDataDO;
    private ChangeSummary cs;
    private static final String schemaFileName = "org/eclipse/persistence/testing/sdo/model/changesummary/quoteDataWithCS.xsd";
    private static final String inputDocumentFileName = "org/eclipse/persistence/testing/sdo/model/changesummary/quoteDataWithCS.xml";
    private static final String writeDocumentBeforeMoveFileName = "org/eclipse/persistence/testing/sdo/model/changesummary/quoteDataWithCSBeforeMove.xml";
    private static final String writeDocumentAfterMoveFileName = "org/eclipse/persistence/testing/sdo/model/changesummary/quoteDataWithCSAfterMove.xml";

    private String writeDocumentBeforeMoveAsString;
    private String writeDocumentAfterMoveAsString;
    
    public static final String ROOT_ELEMENT_URI = "www.example.com";
    public static final String ROOT_ELEMENT_NAME = "quoteType";

    public ChangeSummaryXSDQuoteDataTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.model.changesummary.ChangeSummaryXSDQuoteDataTestCases" };
        TestRunner.main(arguments);
    }

    public void setUp() {
        super.setUp();
        try {
            InputStream is = new FileInputStream(schemaFileName);
            xsdHelper.define(is, null);
            
            XMLDocument inputDocument = xmlHelper.load(new FileInputStream(inputDocumentFileName));
            quoteDataDO = inputDocument.getRootObject();
            cs = quoteDataDO.getChangeSummary();
            cs.endLogging();
            
            FileInputStream inputStream = new FileInputStream(writeDocumentBeforeMoveFileName);
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            writeDocumentBeforeMoveAsString = new String(bytes);
            
            inputStream.close();
            inputStream = new FileInputStream(writeDocumentAfterMoveFileName);
            bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            writeDocumentAfterMoveAsString = new String(bytes);

        } catch (Exception e) {
            e.printStackTrace();
            fail("An error occurred loading the xsd");
        }
    }

    /**
     * Tests change summary contents after:
     *   1) modify operation
     *   2) move modified data objects
     *   
     * The change summary should contain an entry for the initial modification, 
     * then an additional entry after the move.
     */
    public void testChangeSummaryModifyThenMove() throws Exception {
        List lineItemList = quoteDataDO.getList("lineItem");
        List relatedQuoteItem = quoteDataDO.getList("relatedQuoteItem");
        cs.beginLogging();

        // modify each related quote item (should be a modified entry in the CS)
        for (int i=0;i<relatedQuoteItem.size();i++){
            DataObject currentDO = (DataObject) relatedQuoteItem.get(i); 
            currentDO.set("cost", i+20);
        }

        String beforemovestr = xmlHelper.save(quoteDataDO, ROOT_ELEMENT_URI, ROOT_ELEMENT_NAME);

        Document doc1 = parser.parse(new ByteArrayInputStream(writeDocumentBeforeMoveAsString.getBytes()));
        Document doc2 = parser.parse(new ByteArrayInputStream(beforemovestr.getBytes()));
        
        assertXMLIdentical(doc1, doc2);
        
        // move the modified related quote items (should be a modified and a move entry in the CS)
        lineItemList.addAll(relatedQuoteItem);

        String aftermovestr = xmlHelper.save(quoteDataDO, ROOT_ELEMENT_URI, ROOT_ELEMENT_NAME);
        
        doc1 = parser.parse(new ByteArrayInputStream(writeDocumentAfterMoveAsString.getBytes()));
        doc2 = parser.parse(new ByteArrayInputStream(aftermovestr.getBytes()));
        
        assertXMLIdentical(doc1, doc2);
        
    }
    
    /**
     * Tests isModified and isDeleted states of a data object and its parent 
     * after:
     *   1) the data object is detached from the parent
     *   2) the data object is re-attached to the parent in the same position in
     *   the original list
     * 
     * This essentially tests an undo operation.
     */
    public void testUndoDetach() throws Exception {
        ListWrapper items = (ListWrapper) quoteDataDO.get("lineItem");
        DataObject item = (DataObject) items.get(0);
            
        cs.beginLogging();            
        
        // sanity check
        assertFalse(cs.isModified(quoteDataDO));
        assertFalse(cs.isDeleted(item));
        
        // detach item from quoteDataDO
        item.detach();

        // at this point item has been deleted, and the parent is modified
        assertTrue(cs.isModified(quoteDataDO));
        assertTrue(cs.isDeleted(item));

        // re-add the item back in the same position (undo detach)        
        items.add(0, item);

        // after the undo op, item is not deleted, and the parent has not been modified
        assertFalse(cs.isModified(quoteDataDO));
        assertFalse(cs.isDeleted(item));
    }
    
    /**
     * Tests isModified and isDeleted states of a data object and its parent 
     * after:
     *   1) the data object is detached from the parent
     *   2) the data object is re-attached to the parent in a different position
     *   in the original list
     *   
     * This essentially tests a move operation.
     */
    public void testMove() throws Exception {
        ListWrapper items = (ListWrapper) quoteDataDO.get("lineItem");
        DataObject item = (DataObject) items.get(0);
            
        cs.beginLogging();            

        // sanity check
        assertFalse(cs.isModified(quoteDataDO));
        assertFalse(cs.isDeleted(item));

        // detach item from quoteDataDO
        item.detach();

        // at this point item has been deleted, and the parent is modified
        assertTrue(cs.isModified(quoteDataDO));
        assertTrue(cs.isDeleted(item));

        // re-add item to the end of the list (move)
        items.add(item);

        // after the move op, item is not deleted, but the parent has been modified
        assertTrue(cs.isModified(quoteDataDO));
        assertFalse(cs.isDeleted(item));
    }

    /**
     * Tests isModified and isDeleted states of two data objects and their 
     * parent after:
     *   1) the data objects are detached from the parent
     *   2) one data object is re-attached to the parent in the same position in
     *   the original list
     */
    public void testUndoWithAdditionalModifications() throws Exception {
        ListWrapper items = (ListWrapper) quoteDataDO.get("lineItem");
        ListWrapper relatedQuoteItems = (ListWrapper) quoteDataDO.get("relatedQuoteItem");
            
        cs.beginLogging();            

        DataObject item = (DataObject) items.get(0);
        DataObject rqItem = (DataObject) relatedQuoteItems.get(0);

        // sanity check
        assertFalse(cs.isModified(quoteDataDO));
        assertFalse(cs.isDeleted(item));
        assertFalse(cs.isDeleted(rqItem));

        // detach item and rqItem from quoteDataDO
        item.detach();
        rqItem.detach();

        // re-add item back to the same position in quoteDataDO (undo detach)
        items.add(0, item);

        // after the undo op, item is not deleted, but the parent has been modified (rqItem was detached)
        assertTrue(cs.isModified(quoteDataDO));
        assertFalse(cs.isDeleted(item));
    }

    /**
     * Tests isModified and isDeleted states of two data objects and their 
     * parent after:
     *   1) the data objects are detached from the parent
     *   2) one data object is re-attached to the parent in the same position in
     *   the original list
     */
    public void testMultipleUndo() throws Exception {
        ListWrapper items = (ListWrapper) quoteDataDO.get("lineItem");
        ListWrapper relatedQuoteItems = (ListWrapper) quoteDataDO.get("relatedQuoteItem");
            
        cs.beginLogging();            

        DataObject item = (DataObject) items.get(0);
        DataObject rqItem = (DataObject) relatedQuoteItems.get(0);

        // sanity check
        assertFalse(cs.isModified(quoteDataDO));
        assertFalse(cs.isDeleted(item));
        assertFalse(cs.isDeleted(rqItem));

        // detach item and rqItem from quoteDataDO
        item.detach();
        rqItem.detach();

        // re-add item back to the same position in quoteDataDO (undo detach)
        items.add(0, item);

        // after the undo op, item is not deleted, but the parent has been modified (rqItem was detached)
        assertTrue(cs.isModified(quoteDataDO));
        assertFalse(cs.isDeleted(item));

        // re-add rqItem back to the same position in quoteDataDO (undo detach)
        relatedQuoteItems.add(0, rqItem);

        // after the undo op, rqItem is not deleted, and the parent is modified
        assertFalse(cs.isModified(quoteDataDO));
        assertFalse(cs.isDeleted(rqItem));
    }
}
