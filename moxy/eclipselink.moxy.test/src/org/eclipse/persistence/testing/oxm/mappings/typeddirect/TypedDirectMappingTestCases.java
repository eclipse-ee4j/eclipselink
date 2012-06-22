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
package org.eclipse.persistence.testing.oxm.mappings.typeddirect;

import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import java.text.SimpleDateFormat;

import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class TypedDirectMappingTestCases extends XMLMappingTestCases {

	private static final String DATE_FORMAT = "yyyy-MM-dd";
	private static final String TIME_FORMAT = "HH:mm:ss";
	private static final String DATE_TIME_FORMAT = DATE_FORMAT + "'T'" + TIME_FORMAT;

	public TypedDirectMappingTestCases(String name) throws Exception {
		super(name);
		setControlDocument("org/eclipse/persistence/testing/oxm/mappings/typeddirect/testObject1.xml");
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

			parsedDate = new SimpleDateFormat(DATE_FORMAT).parse("1976-02-17");
			date = Calendar.getInstance();
			date.setTime(parsedDate); date.clear(Calendar.HOUR); date.clear(Calendar.MINUTE); date.clear(Calendar.SECOND); date.clear(Calendar.MILLISECOND); date.clear(Calendar.ZONE_OFFSET);
			parsedDate = new SimpleDateFormat(DATE_FORMAT).parse("1976-04-09");
			date2 = Calendar.getInstance();
			date2.setTime(parsedDate); date2.clear(Calendar.HOUR); date2.clear(Calendar.MINUTE); date2.clear(Calendar.SECOND); date2.clear(Calendar.MILLISECOND); date2.clear(Calendar.ZONE_OFFSET);
			parsedDate = new SimpleDateFormat(DATE_FORMAT).parse("1978-04-18");
			date3 = Calendar.getInstance();
			date3.setTime(parsedDate); date3.clear(Calendar.HOUR); date3.clear(Calendar.MINUTE); date3.clear(Calendar.SECOND); date3.clear(Calendar.MILLISECOND); date3.clear(Calendar.ZONE_OFFSET);

			parsedDate = new SimpleDateFormat(DATE_TIME_FORMAT).parse("1976-02-17T23:21:00");
			dateTime = Calendar.getInstance(); dateTime.clear();
			dateTime.setTime(parsedDate);
			dateTime.clear(Calendar.ZONE_OFFSET);
			parsedDate = new SimpleDateFormat(DATE_TIME_FORMAT).parse("1976-04-09T23:58:00");
			dateTime2 = Calendar.getInstance(); dateTime2.clear();
			dateTime2.setTime(parsedDate);
            dateTime2.clear(Calendar.ZONE_OFFSET);
			parsedDate = new SimpleDateFormat(DATE_TIME_FORMAT).parse("1978-04-18T06:00:00");
			dateTime3 = Calendar.getInstance(); dateTime3.clear();
			dateTime3.setTime(parsedDate);
            dateTime3.clear(Calendar.ZONE_OFFSET);

            typedDate = new SimpleDateFormat(DATE_FORMAT).parse("1978-08-02");
            untypedDate = new SimpleDateFormat(DATE_TIME_FORMAT).parse("1978-08-02T01:00:00");
            typedSqlDate = Helper.sqlDateFromUtilDate(typedDate);
            untypedSqlDate = Helper.sqlDateFromUtilDate(untypedDate);
            
            
            untypedTimestamp = Helper.timestampFromDate(untypedDate);
            typedTimestamp = Helper.timestampFromDate(new SimpleDateFormat(TIME_FORMAT).parse("01:00:00"));
            
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

}
