/*
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle
package org.eclipse.persistence.testing.sdo.helper.typehelper.define;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.print.attribute.standard.JobHoldUntil;

import junit.textui.TestRunner;

import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.helper.SDOHelperContext;
import org.eclipse.persistence.testing.sdo.SDOTestCase;
import org.eclipse.persistence.exceptions.SDOException;

/**
 * Tests validation of XML names when defining types using type helper.
 */
public class SDOTypeHelperDefineInvalidTestCases extends SDOTestCase {
    private static final String VALIDATION_PROPERTY_NAME = "eclipselink.sdo.dynamic.type.names.validation";

    // Lists of (some) valid characters (depending on position) valid for NCName.
    //
    // For reference, valid NCName is production of the following:
    // Char          ::=      #x9 | #xA | #xD | [#x20-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]
    // NameStartChar ::=      ":" | [A-Z] | "_" | [a-z] | [#xC0-#xD6] | [#xD8-#xF6] | [#xF8-#x2FF] | [#x370-#x37D] | [#x37F-#x1FFF] | [#x200C-#x200D] | [#x2070-#x218F] | [#x2C00-#x2FEF] | [#x3001-#xD7FF] | [#xF900-#xFDCF] | [#xFDF0-#xFFFD] | [#x10000-#xEFFFF]
    // NameChar      ::=      NameStartChar | "-" | "." | [0-9] | #xB7 | [#x0300-#x036F] | [#x203F-#x2040]
    // Name          ::=      NameStartChar (NameChar)*
    // NCName        ::=      Name - (Char* ':' Char*)
    //
    // Note that we need to represent characters by Strings because there are some code points
    // which encode to 2 characters (surrogate pairs).
    private static List<String> VALID_FIRST_CHARS = initValidFirstChars();
    private static List<String> VALID_NEXT_CHARS = initValidNextChars(VALID_FIRST_CHARS);

    private String systemPropertyBackup;

    public SDOTypeHelperDefineInvalidTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.typehelper.define.SDOTypeHelperDefineInvalidTestCases" };
        TestRunner.main(arguments);
    }

    @Override
    public void setUp()
    {
        super.setUp();
        systemPropertyBackup = System.getProperty(VALIDATION_PROPERTY_NAME);
        if (systemPropertyBackup != null) {
            System.clearProperty(VALIDATION_PROPERTY_NAME);
        }
    }

    public void tearDown() throws Exception {
        super.tearDown();
        if (systemPropertyBackup == null) {
            System.clearProperty(VALIDATION_PROPERTY_NAME);
        } else {
            System.setProperty(VALIDATION_PROPERTY_NAME, systemPropertyBackup);
        }
    }

    public void testDefineInvalidTypeName() {
        SDOHelperContext ctx = new SDOHelperContext();
        DataObject typeDataObtject = ctx.getDataFactory().create(SDOConstants.SDO_URL, SDOConstants.TYPE);
        typeDataObtject.set("name", "invalid due to spaces");

        try {
            ctx.getTypeHelper().define(typeDataObtject);
            fail("IllegalArgumentException exception expected but did not occur.");
        } catch (IllegalArgumentException e) {
            assertNotNull("Not null cause expected.", e.getCause());
            assertTrue("SDOException expected as cause.", e.getCause() instanceof SDOException);
            assertEquals(SDOException.ERROR_DEFINING_TYPE_INVALID_NAME, ((SDOException)e.getCause()).getErrorCode());
        }
    }

    public void testDefineEmptyTypeName() {
        SDOHelperContext ctx = new SDOHelperContext();
        DataObject typeDataObtject = ctx.getDataFactory().create(SDOConstants.SDO_URL, SDOConstants.TYPE);
        typeDataObtject.set("name", "");

        try {
            ctx.getTypeHelper().define(typeDataObtject);
            fail("IllegalArgumentException exception expected but did not occur.");
        } catch (IllegalArgumentException e) {
            assertNotNull("Not null cause expected.", e.getCause());
            assertTrue("SDOException expected as cause.", e.getCause() instanceof SDOException);
            assertEquals(SDOException.ERROR_DEFINING_TYPE_INVALID_NAME, ((SDOException)e.getCause()).getErrorCode());
        }
    }

    public void testDefineInvalidPropertyName() {
        SDOHelperContext ctx = new SDOHelperContext();
        DataObject typeDataObject = ctx.getDataFactory().create(SDOConstants.SDO_URL, SDOConstants.TYPE);
        typeDataObject.set("name", "ValidTypeName");
        DataObject propertyDataObject = typeDataObject.createDataObject("property");
        propertyDataObject.set("name", "N\u00BANF"); // invalid code point \u00BA
        propertyDataObject.set("type", ctx.getTypeHelper().getType(SDOConstants.SDO_URL, SDOConstants.STRING));

        try {
            ctx.getTypeHelper().define(typeDataObject);
            fail("IllegalArgumentException exception expected but did not occur.");
        } catch (IllegalArgumentException e) {
            assertNotNull("Not null cause expected.", e.getCause());
            assertTrue("SDOException expected as cause.", e.getCause() instanceof SDOException);
            assertEquals(SDOException.ERROR_DEFINING_PROPERTY_INVALID_NAME, ((SDOException)e.getCause()).getErrorCode());
        }
    }

    public void testDefineInvalidPropertyTypeName() {
        SDOHelperContext ctx = new SDOHelperContext();
        DataObject typeDataObject = ctx.getDataFactory().create(SDOConstants.SDO_URL, SDOConstants.TYPE);
        typeDataObject.set("name", "ValidPropertyName");
        DataObject propertyTypeDataObject = ctx.getDataFactory().create(SDOConstants.SDO_URL, SDOConstants.TYPE);
        propertyTypeDataObject.set("name", "\u036FInvalidPropertyTypeName"); // invalid code point 0x36F at index 0
        DataObject propertyDataObject = typeDataObject.createDataObject("property");
        propertyDataObject.set("name", "ValidPropertyName");
        propertyDataObject.set("type", propertyTypeDataObject);

        try {
            ctx.getTypeHelper().define(typeDataObject);
            fail("IllegalArgumentException exception expected but did not occur.");
        } catch (IllegalArgumentException e) {
            assertNotNull("Not null cause expected.", e.getCause());
            assertTrue("SDOException expected as cause.", e.getCause() instanceof SDOException);
            assertEquals(SDOException.ERROR_DEFINING_TYPE_INVALID_NAME, ((SDOException)e.getCause()).getErrorCode());
        }
    }

    public void testDefineInvalidNamesWithValidationDisabled() {
        System.setProperty(VALIDATION_PROPERTY_NAME, "false");
        SDOHelperContext ctx = new SDOHelperContext();
        DataObject typeDataObject = ctx.getDataFactory().create(SDOConstants.SDO_URL, SDOConstants.TYPE);
        typeDataObject.set("name", ""); // empty is invalid
        DataObject propertyTypeDataObject = ctx.getDataFactory().create(SDOConstants.SDO_URL, SDOConstants.TYPE);
        propertyTypeDataObject.set("name", "\u036FInvalid\u00BAPropertyTypeName");
        DataObject propertyDataObject = typeDataObject.createDataObject("property");
        propertyDataObject.set("name", "Invalid Property Name");
        propertyDataObject.set("type", propertyTypeDataObject);

        Type type = ctx.getTypeHelper().define(typeDataObject);
        assertNotNull(type);
    }

    public void testDefineValidNames() {
        SDOHelperContext ctx = new SDOHelperContext();
        DataObject typeDataObject = ctx.getDataFactory().create(SDOConstants.SDO_URL, SDOConstants.TYPE);
        typeDataObject.set("name", VALID_FIRST_CHARS.get(0) + joinStrings(VALID_NEXT_CHARS));

        // each valid 1st char on first position:
        for (String validFirstChar : VALID_FIRST_CHARS) {
            DataObject propertyDataObject = typeDataObject.createDataObject("property");
            propertyDataObject.set("name", validFirstChar);
            propertyDataObject.set("type", ctx.getTypeHelper().getType(SDOConstants.SDO_URL, SDOConstants.STRING));
        }

        Type type = ctx.getTypeHelper().define(typeDataObject);
        assertNotNull(type);
    }

    private static List<String> initValidFirstChars() {
        return Arrays.asList(
                "A", "H", "Z", "_", "a", "q", "z",
                "\u00C0", "\u00D0", "\u00D6",
                "\u00D8", "\u00EF", "\u00F6",
                "\u00F8", "\u01FF", "\u02FF",
                "\u0370", "\u037A", "\u037D",
                "\u037F", "\u10FF", "\u1FFF",
                "\u200C", "\u200D",
                "\u2070", "\u210F", "\u218F",
                "\u2C00", "\u2EEE", "\u2FEF",
                "\u3001", "\uD000", "\uD7FF",
                "\uF900", "\uFD00", "\uFDCF",
                "\uFDF0", "\uFEEE", "\uFFFD",
                new StringBuilder().appendCodePoint(0x10000).toString(),
                new StringBuilder().appendCodePoint(0xA0000).toString(),
                new StringBuilder().appendCodePoint(0xEFFFF).toString());
    }

    private static List<String> initValidNextChars(List<String> validFirstChars) {
        List<String> extension = Arrays.asList(
                "-", ".", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
                "\u00B7", "\u0300", "\u0350", "\u036F", "\u203F", "\u2040");
        ArrayList<String> result = new ArrayList<>(validFirstChars.size() + extension.size());
        result.addAll(validFirstChars);
        result.addAll(extension);
        return result;
    }

    private static String joinStrings(List<String> strings) {
        StringBuilder sb = new StringBuilder();
        for(String s : strings) {
            sb.append(s);
        }
        return sb.toString();
    }
}
