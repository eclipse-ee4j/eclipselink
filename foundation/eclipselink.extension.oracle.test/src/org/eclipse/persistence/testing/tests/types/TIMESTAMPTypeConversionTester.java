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
 *     
 *     14/05/2009 ailitchev - Bug 267929: Oracle 11.1.0.7: TIMESTAMP test '100 Years from now -> 2109-03-10 13:22:28.5 EST5EDT EDT' began to fail after Daylight Saving Time started
 *     Changed the test "100 Years from now" to "Last DST year", see comment in TIMESTAMPTester.
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.types;

import java.util.*;
import oracle.sql.*;

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.mappings.converters.TypeConversionConverter;
import org.eclipse.persistence.tools.schemaframework.*;

/**
 * This class tests TypeConversion mapping to TIMESTAMP, TIMESTAMPTZ and TIMESTAMPLTZ
 */
public class TIMESTAMPTypeConversionTester extends TIMESTAMPTester {

	public TIMESTAMPTypeConversionTester() {
		super();
	}

	public TIMESTAMPTypeConversionTester (String nameOfTest, int year, int month, int date, int hrs, int min, int sec, int nano, String zoneId) 
	{
		super(nameOfTest, year, month, date, hrs, min, sec, nano, zoneId);
	}

	public TIMESTAMPTypeConversionTester (String nameOfTest, Calendar c) 
	{
		super(nameOfTest, c);
	}

	public TIMESTAMPTypeConversionTester (String nameOfTest, long time) 
	{
		super(nameOfTest, time);
	}

	/**
	 *Return a platform independant definition of the database table.
	 */
	public static TableDefinition tableDefinition(Session session)
	{
		TableDefinition definition= TIMESTAMPTester.tableDefinition();

		definition.setName("TIMESTAMPTCTEST");
		definition.addField("TSTOTS", TIMESTAMP.class);
		definition.addField("TSTOTSTZ", TIMESTAMPTZ.class);
		definition.addField("TSTOTSLTZ", TIMESTAMPLTZ.class);
		definition.addField("CALTOTSTZ", TIMESTAMPTZ.class);
		definition.addField("CALTOTSLTZ", TIMESTAMPLTZ.class);
		definition.addField("UTILDATETOTS", TIMESTAMP.class);
		definition.addField("UTILDATETOTSTZ", TIMESTAMPTZ.class);
		definition.addField("UTILDATETOTSLTZ", TIMESTAMPLTZ.class);
		definition.addField("DATETOTS", TIMESTAMP.class);
		definition.addField("DATETOTSTZ", TIMESTAMPTZ.class);
		definition.addField("DATETOTSLTZ", TIMESTAMPLTZ.class);
		definition.addField("TIMETOTS", TIMESTAMP.class);
		definition.addField("TIMETOTSTZ", TIMESTAMPTZ.class);
		definition.addField("TIMETOTSLTZ", TIMESTAMPLTZ.class);
		definition.addField("TSTODATE", java.sql.Date.class);
		
		return definition;
	}		
	
	public static RelationalDescriptor descriptor()
	{
		RelationalDescriptor descriptor = new RelationalDescriptor();

		/* First define the class, table and descriptor properties. */
		descriptor.setJavaClass(TIMESTAMPTypeConversionTester.class);
		descriptor.setTableName("TIMESTAMPTCTEST");
		descriptor.setPrimaryKeyFieldName("NAME");

		/* Next define the attribute mappings. */
		descriptor.addDirectMapping("testName", "getTestName", "setTestName", "NAME");
	
		DirectToFieldMapping tsToDateMapping = new DirectToFieldMapping();
		tsToDateMapping.setAttributeName("tsToDate");
		tsToDateMapping.setFieldName("TSTODATE");
		TypeConversionConverter converter = new TypeConversionConverter();
		converter.setObjectClass(java.sql.Timestamp.class);
		converter.setDataClass(java.sql.Timestamp.class);
                tsToDateMapping.setConverter(converter);
		descriptor.addMapping(tsToDateMapping);

		DirectToFieldMapping timestampMapping = new DirectToFieldMapping();
		timestampMapping.setAttributeName("tsToTS");
		timestampMapping.setFieldName("TSTOTS");
		converter = new TypeConversionConverter();
		converter.setObjectClass(java.sql.Timestamp.class);
		converter.setDataClass(oracle.sql.TIMESTAMP.class);
                timestampMapping.setConverter(converter);
		descriptor.addMapping(timestampMapping);

		DirectToFieldMapping timestampMapping2 = new DirectToFieldMapping();
		timestampMapping2.setAttributeName("tsToTSTZ");
		timestampMapping2.setFieldName("TSTOTSTZ");
		converter = new TypeConversionConverter();
		converter.setObjectClass(java.sql.Timestamp.class);
		converter.setDataClass(oracle.sql.TIMESTAMPTZ.class);
                timestampMapping2.setConverter(converter);
		descriptor.addMapping(timestampMapping2);

		DirectToFieldMapping timestampMapping3 = new DirectToFieldMapping();
		timestampMapping3.setAttributeName("tsToTSLTZ");
		timestampMapping3.setFieldName("TSTOTSLTZ");
		converter = new TypeConversionConverter();
		converter.setObjectClass(java.sql.Timestamp.class);
		converter.setDataClass(oracle.sql.TIMESTAMPLTZ.class);
                timestampMapping3.setConverter(converter);
		descriptor.addMapping(timestampMapping3);

		DirectToFieldMapping calendarMapping = new DirectToFieldMapping();
		calendarMapping.setAttributeName("calToTSTZ");
		calendarMapping.setFieldName("CALTOTSTZ");
		converter = new TypeConversionConverter();
		converter.setObjectClass(java.util.Calendar.class);
		converter.setDataClass(oracle.sql.TIMESTAMPTZ.class);
                calendarMapping.setConverter(converter);
		descriptor.addMapping(calendarMapping);
	
		DirectToFieldMapping calendarMapping2 = new DirectToFieldMapping();
		calendarMapping2.setAttributeName("calToTSLTZ");
		calendarMapping2.setFieldName("CALTOTSLTZ");
		converter = new TypeConversionConverter();
		converter.setObjectClass(java.util.Calendar.class);
		converter.setDataClass(oracle.sql.TIMESTAMPLTZ.class);
                calendarMapping2.setConverter(converter);
		descriptor.addMapping(calendarMapping2);

		DirectToFieldMapping utilDateToTSMapping = new DirectToFieldMapping();
		utilDateToTSMapping.setAttributeName("utilDateToTS");
		utilDateToTSMapping.setFieldName("UTILDATETOTS");
		converter = new TypeConversionConverter();
		converter.setObjectClass(java.util.Date.class);
		converter.setDataClass(oracle.sql.TIMESTAMP.class);
                utilDateToTSMapping.setConverter(converter);
		descriptor.addMapping(utilDateToTSMapping);

		DirectToFieldMapping utilDateToTSTZMapping = new DirectToFieldMapping();
		utilDateToTSTZMapping.setAttributeName("utilDateToTSTZ");
		utilDateToTSTZMapping.setFieldName("UTILDATETOTSTZ");
		converter = new TypeConversionConverter();
		converter.setObjectClass(java.util.Date.class);
		converter.setDataClass(oracle.sql.TIMESTAMPTZ.class);
                utilDateToTSTZMapping.setConverter(converter);
		descriptor.addMapping(utilDateToTSTZMapping);

		DirectToFieldMapping utilDateToTSLTZMapping = new DirectToFieldMapping();
		utilDateToTSLTZMapping.setAttributeName("utilDateToTSLTZ");
		utilDateToTSLTZMapping.setFieldName("UTILDATETOTSLTZ");
		converter = new TypeConversionConverter();
		converter.setObjectClass(java.util.Date.class);
		converter.setDataClass(oracle.sql.TIMESTAMPLTZ.class);
                utilDateToTSLTZMapping.setConverter(converter);
		descriptor.addMapping(utilDateToTSLTZMapping);

		DirectToFieldMapping dateToTSMapping = new DirectToFieldMapping();
		dateToTSMapping.setAttributeName("dateToTS");
		dateToTSMapping.setFieldName("DATETOTS");
		converter = new TypeConversionConverter();
		converter.setObjectClass(java.sql.Date.class);
		converter.setDataClass(oracle.sql.TIMESTAMP.class);
                dateToTSMapping.setConverter(converter);
		descriptor.addMapping(dateToTSMapping);

		DirectToFieldMapping dateToTSTZMapping = new DirectToFieldMapping();
		dateToTSTZMapping.setAttributeName("dateToTSTZ");
		dateToTSTZMapping.setFieldName("DATETOTSTZ");
		converter = new TypeConversionConverter();
		converter.setObjectClass(java.sql.Date.class);
		converter.setDataClass(oracle.sql.TIMESTAMPTZ.class);
                dateToTSTZMapping.setConverter(converter);
		descriptor.addMapping(dateToTSTZMapping);

		DirectToFieldMapping dateToTSLTZMapping = new DirectToFieldMapping();
		dateToTSLTZMapping.setAttributeName("dateToTSLTZ");
		dateToTSLTZMapping.setFieldName("DATETOTSLTZ");
		converter = new TypeConversionConverter();
		converter.setObjectClass(java.sql.Date.class);
		converter.setDataClass(oracle.sql.TIMESTAMPLTZ.class);
                dateToTSLTZMapping.setConverter(converter);
		descriptor.addMapping(dateToTSLTZMapping);

		DirectToFieldMapping timeToTSMapping = new DirectToFieldMapping();
		timeToTSMapping.setAttributeName("timeToTS");
		timeToTSMapping.setFieldName("TIMETOTS");
		converter = new TypeConversionConverter();
		converter.setObjectClass(java.sql.Time.class);
		converter.setDataClass(oracle.sql.TIMESTAMP.class);
                timeToTSMapping.setConverter(converter);
		descriptor.addMapping(timeToTSMapping);

		DirectToFieldMapping timeToTSTZMapping = new DirectToFieldMapping();
		timeToTSTZMapping.setAttributeName("timeToTSTZ");
		timeToTSTZMapping.setFieldName("TIMETOTSTZ");
		converter = new TypeConversionConverter();
		converter.setObjectClass(java.sql.Time.class);
		converter.setDataClass(oracle.sql.TIMESTAMPTZ.class);
                timeToTSTZMapping.setConverter(converter);
		descriptor.addMapping(timeToTSTZMapping);

		DirectToFieldMapping timeToTSLTZMapping = new DirectToFieldMapping();
		timeToTSLTZMapping.setAttributeName("timeToTSLTZ");
		timeToTSLTZMapping.setFieldName("TIMETOTSLTZ");
		converter = new TypeConversionConverter();
		converter.setObjectClass(java.sql.Time.class);
		converter.setDataClass(oracle.sql.TIMESTAMPLTZ.class);
                timeToTSLTZMapping.setConverter(converter);
		descriptor.addMapping(timeToTSLTZMapping);
		
		return descriptor;
	}

	static public Vector testInstances () 
	{
		Vector tests = new Vector();	 
		tests.addElement(new TIMESTAMPTypeConversionTester("NOW", Calendar.getInstance()));	
		tests.addElement(new TIMESTAMPTypeConversionTester("ZERO", 1900,0,1,0,0,0,0,"GMT"));							
		tests.addElement(new TIMESTAMPTypeConversionTester("GMT New Years Eve", 1997, 11, 31, 23, 59, 59, 900000000, "GMT"));	
		tests.addElement(new TIMESTAMPTypeConversionTester("PST New Years Eve", 1997, 11, 31, 23, 59, 59, 900000000, "America/Los_Angeles"));	
		tests.addElement(new TIMESTAMPTypeConversionTester("EST New Years Day 2000", 2000, 1, 1, 0, 0, 1, 0, "America/Montreal"));	
		tests.addElement(new TIMESTAMPTypeConversionTester("London summer", 1998, 7, 1, 14, 37, 53, 365789894, "Europe/London"));	
		tests.addElement(new TIMESTAMPTypeConversionTester("LA summer", 1999, 5, 31, 12, 59, 59, 565764356, "America/Los_Angeles"));	
		Calendar yearsBack100 = Calendar.getInstance();
		yearsBack100.set(Calendar.YEAR, (yearsBack100.get(Calendar.YEAR) - 100));
		tests.addElement(new TIMESTAMPTypeConversionTester("100 Years ago", yearsBack100));
		Calendar lastDSTYearCal = Calendar.getInstance();
		lastDSTYearCal.set(Calendar.YEAR, lastDSTYear);
		tests.addElement(new TIMESTAMPDirectToFieldTester("Last DST year", lastDSTYearCal));
		tests.addElement(new TIMESTAMPTypeConversionTester("GMT Before 1970", 1902, 2, 13, 16, 34, 25, 900000000, "GMT"));
		//Nanos starting with one zero
		tests.addElement(new TIMESTAMPTypeConversionTester("Nano with one zero", 465346464057L));
		//Nanos starting with two zeros
		tests.addElement(new TIMESTAMPTypeConversionTester("Nano with two zeros", 465346464007L));
		return tests;
	}

	static public Vector testInstances1 () 
	{
		Vector tests = new Vector();	 
		tests.addElement(new TIMESTAMPTypeConversionTester("NOW 1", Calendar.getInstance()));	
		tests.addElement(new TIMESTAMPTypeConversionTester("ZERO 1", 1900,0,1,0,0,0,0,"GMT"));							
		tests.addElement(new TIMESTAMPTypeConversionTester("GMT New Years Eve 1", 1997, 11, 31, 23, 59, 59, 900000000, "GMT"));	
		tests.addElement(new TIMESTAMPTypeConversionTester("PST New Years Eve 1", 1997, 11, 31, 23, 59, 59, 900000000, "America/Los_Angeles"));	
		tests.addElement(new TIMESTAMPTypeConversionTester("EST New Years Day 2000 1", 2000, 1, 1, 0, 0, 1, 0, "America/Montreal"));	
		tests.addElement(new TIMESTAMPTypeConversionTester("London summer 1", 1998, 7, 1, 14, 37, 53, 365789894, "Europe/London"));	
		tests.addElement(new TIMESTAMPTypeConversionTester("LA summer 1", 1999, 5, 31, 12, 59, 59, 565764356, "America/Los_Angeles"));	
		Calendar yearsBack100 = Calendar.getInstance();
		yearsBack100.set(Calendar.YEAR, (yearsBack100.get(Calendar.YEAR) - 100));
		tests.addElement(new TIMESTAMPTypeConversionTester("100 Years ago 1", yearsBack100));
		Calendar lastDSTYearCal = Calendar.getInstance();
		lastDSTYearCal.set(Calendar.YEAR, lastDSTYear);
		tests.addElement(new TIMESTAMPDirectToFieldTester("Last DST year 1", lastDSTYearCal));
		tests.addElement(new TIMESTAMPTypeConversionTester("GMT Before 1970 1", 1902, 2, 13, 16, 34, 25, 900000000, "GMT"));
		tests.addElement(new TIMESTAMPTypeConversionTester("Nano with one zero 1", 465346464057L));
		tests.addElement(new TIMESTAMPTypeConversionTester("Nano with two zeros 1", 465346464007L));
		return tests;
	}

	static public Vector testInstances2 () 
	{
		Vector tests = new Vector();	 
		tests.addElement(new TIMESTAMPTypeConversionTester("NOW 2", Calendar.getInstance()));	
		tests.addElement(new TIMESTAMPTypeConversionTester("ZERO 2", 1900,0,1,0,0,0,0,"GMT"));							
		tests.addElement(new TIMESTAMPTypeConversionTester("GMT New Years Eve 2", 1997, 11, 31, 23, 59, 59, 900000000, "GMT"));	
		tests.addElement(new TIMESTAMPTypeConversionTester("PST New Years Eve 2", 1997, 11, 31, 23, 59, 59, 900000000, "America/Los_Angeles"));	
		tests.addElement(new TIMESTAMPTypeConversionTester("EST New Years Day 2000 2", 2000, 1, 1, 0, 0, 1, 0, "America/Montreal"));	
		tests.addElement(new TIMESTAMPTypeConversionTester("London summer 2", 1998, 7, 1, 14, 37, 53, 365789894, "Europe/London"));	
		tests.addElement(new TIMESTAMPTypeConversionTester("LA summer 2", 1999, 5, 31, 12, 59, 59, 565764356, "America/Los_Angeles"));	
		Calendar yearsBack100 = Calendar.getInstance();
		yearsBack100.set(Calendar.YEAR, (yearsBack100.get(Calendar.YEAR) - 100));
		tests.addElement(new TIMESTAMPTypeConversionTester("100 Years ago 2", yearsBack100));
		Calendar lastDSTYearCal = Calendar.getInstance();
		lastDSTYearCal.set(Calendar.YEAR, lastDSTYear);
		tests.addElement(new TIMESTAMPDirectToFieldTester("Last DST year 2", lastDSTYearCal));
		tests.addElement(new TIMESTAMPTypeConversionTester("GMT Before 1970 2", 1902, 2, 13, 16, 34, 25, 900000000, "GMT"));
		tests.addElement(new TIMESTAMPTypeConversionTester("Nano with one zero 2", 465346464057L));
		tests.addElement(new TIMESTAMPTypeConversionTester("Nano with two zeros 2", 465346464007L));
		return tests;
	}
			
    // As opposed to TIMESTAMPTester
    // tsToTSTZ, utilDateToTSTZ and timeToTSTZ work no matter what session time zone is.
	// Converter makes a difference - Timestamp (Date, Time) object is converted to Calendar first,
	// which finally converted to TIMESTAMPTZ.
    boolean doesTimestampTZWork() {
        return true; 
    }
}
