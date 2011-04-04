/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith - 2.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.helper.SDODataHelper;

import commonj.sdo.Property;
import commonj.sdo.helper.XMLDocument;

import junit.textui.TestRunner;

public class LoadAndSaveSchemaTypesEmptyStringTestCases extends LoadAndSaveSchemaTypesTestCases {
    public LoadAndSaveSchemaTypesEmptyStringTestCases(String name) {
        super(name);
    }

	public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.LoadAndSaveSchemaTypesEmptyStringTestCases" };
        TestRunner.main(arguments);
    }

	protected String getControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/schemaTypesEmpty.xml");
    }
	
	protected String getNoSchemaControlFileName() {        
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/schemaTypesEmptyNoSchema.xml");
    }
	
	protected void verifyAfterLoad(XMLDocument document) {
	        assertNotNull(document);
	        assertNotNull(document.getRootObject());
	        assertNull(document.getRootObject().getContainer());
	        
	        //check dataobject.get values
	        assertEquals(null, document.getRootObject().get("myLongTest"));
	        assertEquals(null, document.getRootObject().get("myLongWrapperTest"));	        
	        assertEquals(null, document.getRootObject().get("myBooleanTest"));
	        assertEquals(null, document.getRootObject().get("myBooleanWrapperTest"));	        
	        assertEquals(null, document.getRootObject().get("myAnyURITest"));
	        assertEquals(null, document.getRootObject().get("myByteTest"));
	        assertEquals(null, document.getRootObject().get("myDateTest"));
	        assertEquals(null, document.getRootObject().get("myDateTimeTest"));
	        assertEquals(null, document.getRootObject().get("myDecimalTest"));
	        assertEquals(null, document.getRootObject().get("myDoubleTest"));
	        assertEquals(null, document.getRootObject().get("myDurationTest"));
	        assertEquals(null, document.getRootObject().get("myENTITIESTest"));
	        assertEquals(null, document.getRootObject().get("myENTITYTest"));
	        assertEquals(null, document.getRootObject().get("myGDayTest"));
	        assertEquals(null, document.getRootObject().get("myGMonthTest"));
	        assertEquals(null, document.getRootObject().get("myGMonthDayTest"));
	        assertEquals(null, document.getRootObject().get("myGYearTest"));
	        assertEquals(null, document.getRootObject().get("myGYearMonthTest"));
	        assertEquals(null, document.getRootObject().get("myHexBinaryTest"));
	        assertEquals(null, document.getRootObject().get("myIDTest"));
	        assertEquals(null, document.getRootObject().get("myIDREFTest"));
	        assertEquals(null, document.getRootObject().get("myIDREFSTest"));
	        assertEquals(null, document.getRootObject().get("myIntTest"));
	        assertEquals(null, document.getRootObject().get("myIntegerTest"));
	        assertEquals(null, document.getRootObject().get("myLanguageTest"));	        
	        assertEquals(null, document.getRootObject().get("myLongTest"));
	        assertEquals(null, document.getRootObject().get("myNameTest"));	        
	        assertEquals(null, document.getRootObject().get("myNCNameTest"));
	        assertEquals(null, document.getRootObject().get("myNegativeIntegerTest"));
	        assertEquals(null, document.getRootObject().get("myNonNegativeIntegerTest"));
	        assertEquals(null, document.getRootObject().get("myNMTOKENTest"));
	        assertEquals(null, document.getRootObject().get("myNMTOKENSTest"));
	        assertEquals(null, document.getRootObject().get("myNOTATIONTest"));
	        assertEquals(null, document.getRootObject().get("myNormalizedStringTest"));
	        assertEquals(null, document.getRootObject().get("myPositiveIntegerTest"));
	        assertEquals(null, document.getRootObject().get("myNonPositiveIntegerTest"));	        
	        assertEquals(null, document.getRootObject().get("myShort"));
	        assertEquals(null, document.getRootObject().get("myStringTest"));
	        assertEquals(null, document.getRootObject().get("myTimeTest"));
	        assertEquals(null, document.getRootObject().get("myTokenTest"));
	        assertEquals(null, document.getRootObject().get("myUnsignedByteTest"));
	        assertEquals(null, document.getRootObject().get("myUnsignedIntTest"));	        
	        assertEquals(null, document.getRootObject().get("myUnsignedLongTest"));
	        assertEquals(null, document.getRootObject().get("myUnsignedShortTest"));
	        	    
	        assertEquals(false, document.getRootObject().getBoolean("myBooleanTest"));
	        assertEquals(false, document.getRootObject().getBoolean("myBooleanWrapperTest"));
	        
	        assertEquals(0l, document.getRootObject().getLong("myLongTest"));
	        assertEquals(0l, document.getRootObject().getLong("myLongWrapperTest"));
	     
	        //primitive types
	        assertEquals(0, dataHelper.convertFromStringValue(SDOConstants.EMPTY_STRING, ClassConstants.PINT));
	        assertEquals(0l, dataHelper.convertFromStringValue(SDOConstants.EMPTY_STRING, ClassConstants.PLONG));
	        assertEquals(0.0, dataHelper.convertFromStringValue(SDOConstants.EMPTY_STRING, ClassConstants.PDOUBLE));
	        assertEquals(0f, dataHelper.convertFromStringValue(SDOConstants.EMPTY_STRING, ClassConstants.PFLOAT));
	        short testShort = 0;
	        assertEquals(testShort, dataHelper.convertFromStringValue(SDOConstants.EMPTY_STRING, ClassConstants.PSHORT));	        	        
	        char testChar =0;
	        assertEquals(testChar, dataHelper.convertFromStringValue(SDOConstants.EMPTY_STRING, ClassConstants.PCHAR));
	        assertEquals(false, dataHelper.convertFromStringValue(SDOConstants.EMPTY_STRING, ClassConstants.PBOOLEAN)); 
	        
	        assertNull(dataHelper.convertFromStringValue(SDOConstants.EMPTY_STRING, ClassConstants.BIGINTEGER));
	        assertNull(dataHelper.convertFromStringValue(SDOConstants.EMPTY_STRING, ClassConstants.BIGDECIMAL));
	        assertNull(dataHelper.convertFromStringValue(SDOConstants.EMPTY_STRING, ClassConstants.INTEGER));
	        assertNull(dataHelper.convertFromStringValue(SDOConstants.EMPTY_STRING, ClassConstants.LONG));
	        assertNull(dataHelper.convertFromStringValue(SDOConstants.EMPTY_STRING, ClassConstants.SHORT));	        
	        assertNull(dataHelper.convertFromStringValue(SDOConstants.EMPTY_STRING, ClassConstants.FLOAT));
	        assertNull(dataHelper.convertFromStringValue(SDOConstants.EMPTY_STRING, ClassConstants.DOUBLE));
            assertNull(dataHelper.convertFromStringValue(SDOConstants.EMPTY_STRING, ClassConstants.BOOLEAN)); 	        
	        
	        assertNull(dataHelper.convertFromStringValue(SDOConstants.EMPTY_STRING, ClassConstants.QNAME));	        
	        assertNull(dataHelper.convertFromStringValue(SDOConstants.EMPTY_STRING, ClassConstants.CHAR));
	                  
	    }
	
}
