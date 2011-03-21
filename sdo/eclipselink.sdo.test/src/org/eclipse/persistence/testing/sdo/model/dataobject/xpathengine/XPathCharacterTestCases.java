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
 *     Blaise Doughan - 2.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.sdo.model.dataobject.xpathengine;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.sdo.helper.SDOHelperContext;

import commonj.sdo.DataObject;
import commonj.sdo.helper.DataFactory;

import junit.framework.TestCase;

public class XPathCharacterTestCases extends TestCase {

    private static final String CONTROL_PIPE = "100|1";
    private static final String CONTROL_PERIOD = "100.1";
    private static final String CONTROL_NUMBER_SIGN = "100#1";
    private static final String CONTROL_UNDERSCORE = "100_1";
    private static final String CONTROL_SLASH = "100/1";
    private static final String CONTROL_EXCLAMATION_POINT = "100!1";
    private static final String CONTROL_AMPERSAND = "100&1";
    private static final String CONTROL_PERCENT = "100%1";
    private static final String CONTROL_AT_SIGN = "100@1";
    private static final String CONTROL_DOLLAR_SIGN = "100$1";
    private static final String CONTROL_SQUARE_BRACKET = "100[1]";
    private static final String CONTROL_COLON = "100:1";
    private static final String CONTROL_TILDA = "100~1";
    private static final String CONTROL_DASH = "100-1";
    private static final String XSD_RESOURCE = "org/eclipse/persistence/testing/sdo/model/dataobject/xpathengine/XPathCharacter.xsd";
    private static final String TEST_SDO_NS = "http://xmlns.oracle.com/test/xpath";

    private DataObject fooSdo;

    public XPathCharacterTestCases(String name) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
        SDOHelperContext hc = new SDOHelperContext();

        InputStream xsd = XPathCharacterTestCases.class.getClassLoader().getResourceAsStream(XSD_RESOURCE);
        hc.getXSDHelper().define(xsd, null);
        xsd.close();

        DataFactory df = hc.getDataFactory();
        fooSdo = df.create(TEST_SDO_NS, "fooType");

        List barList = new ArrayList(14);

        barList.add(consBar(df, CONTROL_DASH, "bar1"));
        barList.add(consBar(df, CONTROL_TILDA, "bar2"));
        barList.add(consBar(df, CONTROL_COLON, "bar3"));
        barList.add(consBar(df, CONTROL_SQUARE_BRACKET, "bar4"));
        barList.add(consBar(df, CONTROL_DOLLAR_SIGN, "bar5"));
        barList.add(consBar(df, CONTROL_AT_SIGN, "bar6"));
        barList.add(consBar(df, CONTROL_PERCENT, "bar7"));
        barList.add(consBar(df, CONTROL_AMPERSAND, "bar8"));
        barList.add(consBar(df, CONTROL_EXCLAMATION_POINT, "bar9"));
        barList.add(consBar(df, CONTROL_SLASH, "bar10"));
        barList.add(consBar(df, CONTROL_UNDERSCORE, "bar11"));
        barList.add(consBar(df, CONTROL_NUMBER_SIGN, "bar12"));
        barList.add(consBar(df, CONTROL_PERIOD, "bar13"));
        barList.add(consBar(df, CONTROL_PIPE, "bar14"));

        fooSdo.setList("bar", barList);
    }

    private DataObject consBar(DataFactory df, String id, String value) {
        DataObject barSdo = df.create(TEST_SDO_NS, "barType");
        barSdo.set("id", id);
        barSdo.set("value", value);
        return barSdo;
      }

    public void testDash() {
        DataObject barSdo = fooSdo.getDataObject("bar[id=100-1]");
        assertEquals(CONTROL_DASH, barSdo.getString("id"));
        assertEquals(CONTROL_DASH, fooSdo.getString("bar[id=100-1]/id"));
    }

    public void testTilda() {
        DataObject barSdo = fooSdo.getDataObject("bar[id=100~1]");
        assertEquals(CONTROL_TILDA, barSdo.getString("id"));
        assertEquals(CONTROL_TILDA, fooSdo.getString("bar[id=100~1]/id"));
    }

    public void testColon() {
        DataObject barSdo = fooSdo.getDataObject("bar[id=100:1]");
        assertEquals(CONTROL_COLON, barSdo.getString("id"));
        assertEquals(CONTROL_COLON, fooSdo.getString("bar[id=100:1]/id"));
    }

    public void testSquareBracket() {
        DataObject barSdo = fooSdo.getDataObject("bar[id=100[1]]");
        assertEquals(CONTROL_SQUARE_BRACKET, barSdo.getString("id"));
        assertEquals(CONTROL_SQUARE_BRACKET, fooSdo.getString("bar[id=100[1]]/id"));
    }

    public void testDollarSign() {
        DataObject barSdo = fooSdo.getDataObject("bar[id=100$1]");
        assertEquals(CONTROL_DOLLAR_SIGN, barSdo.getString("id"));
        assertEquals(CONTROL_DOLLAR_SIGN, fooSdo.getString("bar[id=100$1]/id"));
    }

    public void testAtSign() {
        DataObject barSdo = fooSdo.getDataObject("bar[id=100@1]");
        assertEquals(CONTROL_AT_SIGN, barSdo.getString("id"));
        assertEquals(CONTROL_AT_SIGN, fooSdo.getString("bar[id=100@1]/id"));
    }

    public void testPercent() {
        DataObject barSdo = fooSdo.getDataObject("bar[id=100%1]");
        assertEquals(CONTROL_PERCENT, barSdo.getString("id"));
        assertEquals(CONTROL_PERCENT, fooSdo.getString("bar[id=100%1]/id"));
    }

    public void testAmpersand() {
        DataObject barSdo = fooSdo.getDataObject("bar[id=100&1]");
        assertEquals(CONTROL_AMPERSAND, barSdo.getString("id"));
        assertEquals(CONTROL_AMPERSAND, fooSdo.getString("bar[id=100&1]/id"));
    }

    public void testExclamationPoint() {
        DataObject barSdo = fooSdo.getDataObject("bar[id=100!1]");
        assertEquals(CONTROL_EXCLAMATION_POINT, barSdo.getString("id"));
        assertEquals(CONTROL_EXCLAMATION_POINT, fooSdo.getString("bar[id=100!1]/id"));
    }

    public void testSlash() {
        DataObject barSdo = fooSdo.getDataObject("bar[id=100/1]");
        assertEquals(CONTROL_SLASH, barSdo.getString("id"));
        assertEquals(CONTROL_SLASH, fooSdo.getString("bar[id=100/1]/id"));
    }

    public void testUnderscore() {
        DataObject barSdo = fooSdo.getDataObject("bar[id=100_1]");
        assertEquals(CONTROL_UNDERSCORE, barSdo.getString("id"));
        assertEquals(CONTROL_UNDERSCORE, fooSdo.getString("bar[id=100_1]/id"));
    }

    public void testNumberSign() {
        DataObject barSdo = fooSdo.getDataObject("bar[id=100#1]");
        assertEquals(CONTROL_NUMBER_SIGN, barSdo.getString("id"));
        assertEquals(CONTROL_NUMBER_SIGN, fooSdo.getString("bar[id=100#1]/id"));
    }

    public void testPeriod() {
        DataObject barSdo = fooSdo.getDataObject("bar[id=100.1]");
        assertEquals(CONTROL_PERIOD, barSdo.getString("id"));
        assertEquals(CONTROL_PERIOD, fooSdo.getString("bar[id=100.1]/id"));
    }

    public void testPipe() {
        DataObject barSdo = fooSdo.getDataObject("bar[id=100|1]");
        assertEquals(CONTROL_PIPE, barSdo.getString("id"));
        assertEquals(CONTROL_PIPE, fooSdo.getString("bar[id=100|1]/id"));
    }

}