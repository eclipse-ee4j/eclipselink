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
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.changesummary;

import org.eclipse.persistence.exceptions.SDOException;
import junit.textui.TestRunner;

public class ChangeSummaryRootCreateInvalidTestCases extends ChangeSummaryRootLoadAndSaveTestCases {
    public ChangeSummaryRootCreateInvalidTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.changesummary.ChangeSummaryRootCreateInvalidTestCases" };
        TestRunner.main(arguments);
    }

    protected String getControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/changesummary/team_csroot_create_error.xml");
    }


    public void testLoadFromAndSaveAfterDefineMultipleSchemas() throws Exception {
        try {
            super.testLoadFromAndSaveAfterDefineMultipleSchemas();
        } catch (SDOException e) {
            assertEquals(SDOException.ERROR_PROCESSING_XPATH, e.getErrorCode());
            return;            
        }
        fail("An SDOException should have occurred.");
    }
    
    public void testClassGenerationLoadAndSave() throws Exception {
        try {
            super.testClassGenerationLoadAndSave();
        } catch (SDOException e) {
            assertEquals(SDOException.ERROR_PROCESSING_XPATH, e.getErrorCode());
            return;            
        }
        fail("An SDOException should have occurred.");
    }

    public void testLoadFromDomSourceWithURIAndOptionsSaveDataObjectToStreamResult() throws Exception {
        try {
            super.testLoadFromDomSourceWithURIAndOptionsSaveDataObjectToStreamResult();
        } catch (SDOException e) {
            assertEquals(SDOException.ERROR_PROCESSING_XPATH, e.getErrorCode());
            return;            
        }
        fail("An SDOException should have occurred.");
    }

    public void testLoadFromFileReaderWithURIAndOptionsStreamSaveDataObjectToWriter() throws Exception {
        try {
            super.testLoadFromFileReaderWithURIAndOptionsStreamSaveDataObjectToWriter();
        } catch (SDOException e) {
            assertEquals(SDOException.ERROR_PROCESSING_XPATH, e.getErrorCode());
            return;            
        }
        fail("An SDOException should have occurred.");
    }

    public void testLoadFromInputStreamSaveDocumentToOutputStream() throws Exception {
        try {
            super.testLoadFromInputStreamSaveDocumentToOutputStream();
        } catch (SDOException e) {
            assertEquals(SDOException.ERROR_PROCESSING_XPATH, e.getErrorCode());
            return;            
        }
        fail("An SDOException should have occurred.");
    }

    public void testLoadFromInputStreamWithURIAndOptionsSaveDataObjectToOutputStream() throws Exception {
        try {
            super.testLoadFromInputStreamWithURIAndOptionsSaveDataObjectToOutputStream();
        } catch (SDOException e) {
            assertEquals(SDOException.ERROR_PROCESSING_XPATH, e.getErrorCode());
            return;            
        }
        fail("An SDOException should have occurred.");
    }

    public void testLoadFromInputStreamWithURIAndOptionsSaveDataObjectToStreamResult() throws Exception {
        try {
            super.testLoadFromInputStreamWithURIAndOptionsSaveDataObjectToStreamResult();
        } catch (SDOException e) {
            assertEquals(SDOException.ERROR_PROCESSING_XPATH, e.getErrorCode());
            return;            
        }
        fail("An SDOException should have occurred.");
    }

    public void testLoadFromSAXSourceWithURIAndOptionsSaveDataObjectToStreamResult() throws Exception {
        try {
            super.testLoadFromSAXSourceWithURIAndOptionsSaveDataObjectToStreamResult();
        } catch (SDOException e) {
            assertEquals(SDOException.ERROR_PROCESSING_XPATH, e.getErrorCode());
            return;            
        }
        fail("An SDOException should have occurred.");
    }

    public void testLoadFromStreamSourceWithURIAndOptionsSaveDataObjectToStreamResult() throws Exception {
        try {
            super.testLoadFromStreamSourceWithURIAndOptionsSaveDataObjectToStreamResult();
        } catch (SDOException e) {
            assertEquals(SDOException.ERROR_PROCESSING_XPATH, e.getErrorCode());
            return;            
        }
        fail("An SDOException should have occurred.");
    }

    public void testLoadFromStringSaveDocumentToWriter() throws Exception {
        try {
            super.testLoadFromStringSaveDocumentToWriter();
        } catch (SDOException e) {
            assertEquals(SDOException.ERROR_PROCESSING_XPATH, e.getErrorCode());
            return;            
        }
        fail("An SDOException should have occurred.");
    }

    public void testNoSchemaLoadFromInputStreamSaveDataObjectToString() throws Exception {
        try {
            super.testNoSchemaLoadFromInputStreamSaveDataObjectToString();
        } catch (SDOException e) {
            assertEquals(SDOException.ERROR_PROCESSING_XPATH, e.getErrorCode());
            return;            
        }
        fail("An SDOException should have occurred.");
    }
}
