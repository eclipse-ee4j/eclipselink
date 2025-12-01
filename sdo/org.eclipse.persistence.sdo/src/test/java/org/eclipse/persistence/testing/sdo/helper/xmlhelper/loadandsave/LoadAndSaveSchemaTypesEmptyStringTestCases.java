/*
 * Copyright (c) 2011, 2025 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Denise Smith - 2.3 - initial implementation
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave;

import org.eclipse.persistence.internal.core.helper.CoreClassConstants;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.sdo.SDOConstants;

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

    @Override
    protected String getControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/schemaTypesEmpty.xml");
    }

    @Override
    protected String getNoSchemaControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/schemaTypesEmptyNoSchema.xml");
    }

    @Override
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
            assertEquals(0, dataHelper.convertFromStringValue(SDOConstants.EMPTY_STRING, CoreClassConstants.PINT));
            assertEquals(0l, dataHelper.convertFromStringValue(SDOConstants.EMPTY_STRING, CoreClassConstants.PLONG));
            assertEquals(0.0, dataHelper.convertFromStringValue(SDOConstants.EMPTY_STRING, CoreClassConstants.PDOUBLE));
            assertEquals(0f, dataHelper.convertFromStringValue(SDOConstants.EMPTY_STRING, CoreClassConstants.PFLOAT));
            short testShort = 0;
            assertEquals(testShort, dataHelper.convertFromStringValue(SDOConstants.EMPTY_STRING, CoreClassConstants.PSHORT));
            char testChar =0;
            assertEquals(testChar, dataHelper.convertFromStringValue(SDOConstants.EMPTY_STRING, CoreClassConstants.PCHAR));
            assertEquals(false, dataHelper.convertFromStringValue(SDOConstants.EMPTY_STRING, CoreClassConstants.PBOOLEAN));


            //wrappers
            assertEquals(testShort, document.getRootObject().getLong("myShortWrapperTest"));
            assertEquals(testShort, document.getRootObject().getByte("myByteWrapperTest"));
            assertEquals(testChar, document.getRootObject().getChar("myCharacterWrapperTest"));
            assertEquals(0f, document.getRootObject().getFloat("myFloatWrapperTest"));
            assertEquals(0.0, document.getRootObject().getDouble("myDoubleWrapperTest"));
            assertEquals(0, document.getRootObject().getInt("myIntegerWrapperTest"));


            assertNull(dataHelper.convertFromStringValue(SDOConstants.EMPTY_STRING, CoreClassConstants.BIGINTEGER));
            assertNull(dataHelper.convertFromStringValue(SDOConstants.EMPTY_STRING, CoreClassConstants.BIGDECIMAL));
            assertNull(dataHelper.convertFromStringValue(SDOConstants.EMPTY_STRING, CoreClassConstants.INTEGER));
            assertNull(dataHelper.convertFromStringValue(SDOConstants.EMPTY_STRING, CoreClassConstants.LONG));
            assertNull(dataHelper.convertFromStringValue(SDOConstants.EMPTY_STRING, CoreClassConstants.SHORT));
            assertNull(dataHelper.convertFromStringValue(SDOConstants.EMPTY_STRING, CoreClassConstants.FLOAT));
            assertNull(dataHelper.convertFromStringValue(SDOConstants.EMPTY_STRING, CoreClassConstants.DOUBLE));
            assertNull(dataHelper.convertFromStringValue(SDOConstants.EMPTY_STRING, CoreClassConstants.BOOLEAN));

            assertNull(dataHelper.convertFromStringValue(SDOConstants.EMPTY_STRING, ClassConstants.QNAME));
            assertNull(dataHelper.convertFromStringValue(SDOConstants.EMPTY_STRING, CoreClassConstants.CHAR));

        }

}
