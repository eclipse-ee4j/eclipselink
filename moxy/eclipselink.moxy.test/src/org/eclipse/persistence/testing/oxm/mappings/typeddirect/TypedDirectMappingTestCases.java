/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.mappings.typeddirect;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.Vector;
import java.io.StringReader;
import java.text.SimpleDateFormat;

import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class TypedDirectMappingTestCases extends XMLMappingTestCases {

    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String TIME_FORMAT = "HH:mm:ss";
    private static final String DATE_TIME_FORMAT = DATE_FORMAT + "'T'" + TIME_FORMAT;

    private static final String CONTROL_XML_DOCUMENT;

    static {
        CONTROL_XML_DOCUMENT = "<?xml version = '1.0' encoding = 'UTF-8'?>" +
                "<test-object>" +
                "<binary>MTIzNDU2Nzg5</binary>" +
                "<binary>313233343536373839</binary>" +
                "<date>2013-02-17</date>" +
                "<time>23:21:00</time>" +
                "<date-time>2013-02-17T23:21:00</date-time>" +
                "<date-vector>" +
                "<date-element>2013-02-17</date-element>" +
                "<date-element>2013-04-09</date-element>" +
                "<date-element>2013-04-18</date-element>" +
                "</date-vector>" +
                "<time-vector>" +
                "<time-element>23:21:00</time-element>" +
                "<time-element>23:58:00</time-element>" +
                "<time-element>06:00:00</time-element>" +
                "</time-vector>" +
                "<date-time-vector>" +
                "<date-time-element>2013-02-17T23:21:00</date-time-element>" +
                "<date-time-element>2013-04-09T23:58:00</date-time-element>" +
                "<date-time-element>2013-04-18T06:00:00</date-time-element>" +
                "</date-time-vector>" +
                "<base-64-vector>" +
                "<base-64-element>MTEx</base-64-element>" +
                "<base-64-element>MjIy</base-64-element>" +
                "<base-64-element>MzMz</base-64-element>" +
                "</base-64-vector>" +
                "<hex-vector>" +
                "<hex-element>313131</hex-element>" +
                "<hex-element>323232</hex-element>" +
                "<hex-element>333333</hex-element>" +
                "</hex-vector>" +
                "<untyped-date>2013-02-20T01:00:00"+TIMEZONE_OFFSET+"</untyped-date>" +
                "<typed-date>2013-02-20</typed-date>" +
                "<untyped-sql-date>2013-02-20</untyped-sql-date>" +
                "<typed-sql-date>2013-02-20</typed-sql-date>" +
                "<untyped-timestamp>2013-02-20T01:00:00"+TIMEZONE_OFFSET+"</untyped-timestamp>" +
                "<typed-timestamp>01:00:00"+(TIMEZONE_OFFSET.equals("Z") ? "+01:00" : TIMEZONE_OFFSET)
                +"</typed-timestamp>" +
                "</test-object>";
    }

    public TypedDirectMappingTestCases(String name) throws Exception {
        super(name);
        // Bug #454154 - TypedDirectMappingTestCases were failing on test farm which run oxms tests in order strange
        // to default eclipselink oxm tests repo order. -> The following line ensures the same time zone is always
        // used for these test cases and that these test cases will pass on farm when they pass on eclipselink repo.
        TimeZone.setDefault(TimeZone.getTimeZone("America/Los_Angeles"));

        setProject(new TypedDirectMappingTestProject());
    }

    public Object getControlObject() {
        try {
            String base64 = "123456789";
            String hex = "123456789";
            Calendar time, time2, time3, date, date2, date3, dateTime, dateTime2, dateTime3;
            Vector hexAndBase64Vector, timeVector, dateVector, dateTimeVector;
            Date typedDate, untypedDate;
            java.sql.Date typedSqlDate, untypedSqlDate;
            java.sql.Timestamp typedTimestamp, untypedTimestamp;

            hexAndBase64Vector = new Vector();
            hexAndBase64Vector.addElement(new String("111").getBytes());
            hexAndBase64Vector.addElement(new String("222").getBytes());
            hexAndBase64Vector.addElement(new String("333").getBytes());

            Date parsedDate = new SimpleDateFormat(TIME_FORMAT).parse("23:21:00");
            time = Calendar.getInstance();
            time.setTime(parsedDate); time.clear(Calendar.YEAR); time.clear(Calendar.MONTH); time.clear(Calendar.DATE); time.clear(Calendar.ZONE_OFFSET);
            parsedDate = new SimpleDateFormat(TIME_FORMAT).parse("23:58:00");
            time2 = Calendar.getInstance();
            time2.setTime(parsedDate); time2.clear(Calendar.YEAR); time2.clear(Calendar.MONTH); time2.clear(Calendar.DATE); time2.clear(Calendar.ZONE_OFFSET);
            parsedDate = new SimpleDateFormat(TIME_FORMAT).parse("06:00:00");
            time3 = Calendar.getInstance();
            time3.setTime(parsedDate); time3.clear(Calendar.YEAR); time3.clear(Calendar.MONTH); time3.clear(Calendar.DATE); time3.clear(Calendar.ZONE_OFFSET);

            parsedDate = new SimpleDateFormat(DATE_FORMAT).parse("2013-02-17");
            date = Calendar.getInstance();
            date.setTime(parsedDate); date.clear(Calendar.HOUR); date.clear(Calendar.MINUTE); date.clear(Calendar.SECOND); date.clear(Calendar.MILLISECOND); date.clear(Calendar.ZONE_OFFSET);
            parsedDate = new SimpleDateFormat(DATE_FORMAT).parse("2013-04-09");
            date2 = Calendar.getInstance();
            date2.setTime(parsedDate); date2.clear(Calendar.HOUR); date2.clear(Calendar.MINUTE); date2.clear(Calendar.SECOND); date2.clear(Calendar.MILLISECOND); date2.clear(Calendar.ZONE_OFFSET);
            parsedDate = new SimpleDateFormat(DATE_FORMAT).parse("2013-04-18");
            date3 = Calendar.getInstance();
            date3.setTime(parsedDate); date3.clear(Calendar.HOUR); date3.clear(Calendar.MINUTE); date3.clear(Calendar.SECOND); date3.clear(Calendar.MILLISECOND); date3.clear(Calendar.ZONE_OFFSET);

            parsedDate = new SimpleDateFormat(DATE_TIME_FORMAT).parse("2013-02-17T23:21:00");
            dateTime = Calendar.getInstance(); dateTime.clear();
            dateTime.setTime(parsedDate);
            dateTime.clear(Calendar.ZONE_OFFSET);
            parsedDate = new SimpleDateFormat(DATE_TIME_FORMAT).parse("2013-04-09T23:58:00");
            dateTime2 = Calendar.getInstance(); dateTime2.clear();
            dateTime2.setTime(parsedDate);
            dateTime2.clear(Calendar.ZONE_OFFSET);
            parsedDate = new SimpleDateFormat(DATE_TIME_FORMAT).parse("2013-04-18T06:00:00");
            dateTime3 = Calendar.getInstance(); dateTime3.clear();
            dateTime3.setTime(parsedDate);
            dateTime3.clear(Calendar.ZONE_OFFSET);

            typedDate = new SimpleDateFormat(DATE_FORMAT).parse("2013-02-20");
            untypedDate = new SimpleDateFormat(DATE_TIME_FORMAT).parse("2013-02-20T01:00:00");
            typedSqlDate = Helper.sqlDateFromUtilDate(typedDate);
            untypedSqlDate = Helper.sqlDateFromUtilDate(untypedDate);


            untypedTimestamp = Helper.timestampFromDate(untypedDate);
            // use DATE_TIME_FORMAT to account for daylight savings
            typedTimestamp = Helper.timestampFromDate(new SimpleDateFormat(DATE_TIME_FORMAT).parse("2013-02-20T01:00:00"));

            dateVector = new Vector();
            dateVector.addElement(date);
            dateVector.addElement(date2);
            dateVector.addElement(date3);

            timeVector = new Vector();
            timeVector.addElement(time);
            timeVector.addElement(time2);
            timeVector.addElement(time3);

            dateTimeVector = new Vector();
            dateTimeVector.addElement(dateTime);
            dateTimeVector.addElement(dateTime2);
            dateTimeVector.addElement(dateTime3);

            TestObject testObject = new TestObject();
            testObject.setBase64(base64.getBytes());
            testObject.setHex(hex.getBytes());
            testObject.setDate(date);
            testObject.setTime(time);
            testObject.setDateTime(dateTime);
            testObject.setBase64Vector(hexAndBase64Vector);
            testObject.setHexVector(hexAndBase64Vector);
            testObject.setDateVector(dateVector);
            testObject.setTimeVector(timeVector);
            testObject.setDateTimeVector(dateTimeVector);
            testObject.setUntypedDate(untypedDate);
            testObject.setTypedDate(typedDate);
            testObject.setUntypedSqlDate(untypedSqlDate);
            testObject.setTypedSqlDate(typedSqlDate);
            testObject.setUntypedTimestamp(untypedTimestamp);
            testObject.setTypedTimestamp(typedTimestamp);

            return testObject;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected Document getControlDocument() {
        StringReader reader = new StringReader(CONTROL_XML_DOCUMENT);
        InputSource is = new InputSource(reader);
        Document doc = null;
        try {
            doc = parser.parse(is);
        } catch (Exception e) {
            fail("An error occurred setting up the control document");
        }
        return doc;
    }

    public boolean isUnmarshalTest() {
        return false;
    }

}
