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

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.tools.schemaframework.*;
import oracle.sql.*;

/**
 * This class tests Direct-to-Field mapping to TIMESTAMP, TIMESTAMPTZ and TIMESTAMPLTZ
 */
public class TIMESTAMPDirectToFieldTester extends TIMESTAMPTester {

	public TIMESTAMPDirectToFieldTester() {
		super();
	}

	public TIMESTAMPDirectToFieldTester (String nameOfTest, int year, int month, int date, int hrs, int min, int sec, int nano, String zoneId) 
	{
		super(nameOfTest, year, month, date, hrs, min, sec, nano, zoneId);
	}

	public TIMESTAMPDirectToFieldTester (String nameOfTest, Calendar c) 
	{
		super(nameOfTest, c);
	}

	public TIMESTAMPDirectToFieldTester (String nameOfTest, long time) 
	{
		super(nameOfTest, time);
	}

	public static RelationalDescriptor descriptor()
	{
		RelationalDescriptor descriptor = new RelationalDescriptor();

		/* First define the class, table and descriptor properties. */
		descriptor.setJavaClass(TIMESTAMPDirectToFieldTester.class);
		descriptor.setTableName("TIMESTAMPTEST");
		descriptor.setPrimaryKeyFieldName("NAME");

		/* Next define the attribute mappings. */
		descriptor.addDirectMapping("testName", "getTestName", "setTestName", "NAME");
		descriptor.addDirectMapping("tsToTS", "TSTOTS");
		descriptor.addDirectMapping("tsToTSTZ", "TSTOTSTZ");
		descriptor.addDirectMapping("tsToTSLTZ", "TSTOTSLTZ");
		descriptor.addDirectMapping("calToTSTZ", "CALTOTSTZ");
		descriptor.addDirectMapping("calToTSLTZ", "CALTOTSLTZ");
		descriptor.addDirectMapping("utilDateToTS", "UTILDATETOTS");
		descriptor.addDirectMapping("utilDateToTSTZ", "UTILDATETOTSTZ");
		descriptor.addDirectMapping("utilDateToTSLTZ", "UTILDATETOTSLTZ");
		descriptor.addDirectMapping("dateToTS", "DATETOTS");
		descriptor.addDirectMapping("dateToTSTZ", "DATETOTSTZ");
		descriptor.addDirectMapping("dateToTSLTZ", "DATETOTSLTZ");
		descriptor.addDirectMapping("timeToTS", "TIMETOTS");
		descriptor.addDirectMapping("timeToTSTZ", "TIMETOTSTZ");
		descriptor.addDirectMapping("timeToTSLTZ", "TIMETOTSLTZ");
		descriptor.addDirectMapping("tsToDate", "TSTODATE");
//		descriptor.addDirectMapping("calToDate", "CALTODATE");
		return descriptor;
	}

	public static RelationalDescriptor descriptorWithAccessors()
	{
		RelationalDescriptor descriptor = new RelationalDescriptor();

		/* First define the class, table and descriptor properties. */
		descriptor.setJavaClass(TIMESTAMPDirectToFieldTester.class);
		descriptor.setTableName("TIMESTAMPTEST");
		descriptor.setPrimaryKeyFieldName("NAME");

		/* Next define the attribute mappings. */
		descriptor.addDirectMapping("testName", "getTestName", "setTestName", "NAME");
		descriptor.addDirectMapping("tsToTS", "getTsToTS", "setTsToTS", "TSTOTS");
		descriptor.addDirectMapping("tsToTSTZ", "getTsToTSTZ", "setTsToTSTZ", "TSTOTSTZ");
		descriptor.addDirectMapping("tsToTSLTZ", "getTsToTSLTZ", "setTsToTSLTZ", "TSTOTSLTZ");
		descriptor.addDirectMapping("calToTSTZ", "getCalToTSTZ", "setCalToTSTZ", "CALTOTSTZ");
		descriptor.addDirectMapping("calToTSLTZ", "getCalToTSLTZ", "setCalToTSLTZ", "CALTOTSLTZ");
		descriptor.addDirectMapping("utilDateToTS", "getUtilDateToTS", "setUtilDateToTS", "UTILDATETOTS");
		descriptor.addDirectMapping("utilDateToTSTZ", "getUtilDateToTSTZ", "setUtilDateToTSTZ", "UTILDATETOTSTZ");
		descriptor.addDirectMapping("utilDateToTSLTZ", "getUtilDateToTSLTZ", "setUtilDateToTSLTZ", "UTILDATETOTSLTZ");
		descriptor.addDirectMapping("dateToTS", "getDateToTS", "setDateToTS", "DATETOTS");
		descriptor.addDirectMapping("dateToTSTZ", "getDateToTSTZ", "setDateToTSTZ", "DATETOTSTZ");
		descriptor.addDirectMapping("dateToTSLTZ", "getDateToTSLTZ", "setDateToTSLTZ", "DATETOTSLTZ");
		descriptor.addDirectMapping("timeToTS", "getTimeToTS", "setTimeToTS", "TIMETOTS");
		descriptor.addDirectMapping("timeToTSTZ", "getTimeToTSTZ", "setTimeToTSTZ", "TIMETOTSTZ");
		descriptor.addDirectMapping("timeToTSLTZ", "getTimeToTSLTZ", "setTimeToTSLTZ", "TIMETOTSLTZ");
		descriptor.addDirectMapping("tsToDate", "getTsToDate", "setTsToDate", "TSTODATE");
//		descriptor.addDirectMapping("calToDate", "getCalToDate", "setCalToDate", "CALTODATE");
		return descriptor;
	}

	/**
	 *Return a platform independant definition of the database table.
	 */
	public static TableDefinition tableDefinition(Session session)
	{
		TableDefinition definition= TIMESTAMPTester.tableDefinition();

		definition.setName("TIMESTAMPTEST");

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
//		definition.addField("CALTODATE", java.sql.Date.class);
		return definition;
	}

	static public Vector testInstances () 
	{
		Vector tests = new Vector();	 
		tests.addElement(new TIMESTAMPDirectToFieldTester("NOW", Calendar.getInstance()));	
		tests.addElement(new TIMESTAMPDirectToFieldTester("ZERO", 1900,0,1,0,0,0,0,"GMT"));							
		tests.addElement(new TIMESTAMPDirectToFieldTester("GMT New Years Eve", 1997, 11, 31, 23, 59, 59, 900000000, "GMT"));	
		tests.addElement(new TIMESTAMPDirectToFieldTester("PST New Years Eve", 1997, 11, 31, 23, 59, 59, 900000000, "America/Los_Angeles"));	
		tests.addElement(new TIMESTAMPDirectToFieldTester("EST New Years Day 2000", 2000, 1, 1, 0, 0, 1, 0, "America/Montreal"));	
		tests.addElement(new TIMESTAMPDirectToFieldTester("London summer", 1998, 7, 1, 14, 37, 53, 365789894, "Europe/London"));	
		tests.addElement(new TIMESTAMPDirectToFieldTester("LA summer", 1999, 5, 31, 12, 59, 59, 565764356, "America/Los_Angeles"));	
		Calendar yearsBack100 = Calendar.getInstance();
		yearsBack100.set(Calendar.YEAR, (yearsBack100.get(Calendar.YEAR) - 100));
		tests.addElement(new TIMESTAMPDirectToFieldTester("100 Years ago", yearsBack100));
		Calendar lastDSTYearCal = Calendar.getInstance();
		lastDSTYearCal.set(Calendar.YEAR, lastDSTYear);
		tests.addElement(new TIMESTAMPDirectToFieldTester("Last DST year", lastDSTYearCal));
		tests.addElement(new TIMESTAMPDirectToFieldTester("GMT Before 1970", 1902, 2, 13, 16, 34, 25, 900000000, "GMT"));
		//Nanos starting with one zero
		tests.addElement(new TIMESTAMPDirectToFieldTester("Nano with one zero", 465346464057L));
		//Nanos starting with two zeros
		tests.addElement(new TIMESTAMPDirectToFieldTester("Nano with two zeros", 465346464007L));
		return tests;
	}

	static public Vector testInstances1 () 
	{
		Vector tests = new Vector();	 
		tests.addElement(new TIMESTAMPDirectToFieldTester("NOW 1", Calendar.getInstance()));	
		tests.addElement(new TIMESTAMPDirectToFieldTester("ZERO 1", 1900,0,1,0,0,0,0,"GMT"));							
		tests.addElement(new TIMESTAMPDirectToFieldTester("GMT New Years Eve 1", 1997, 11, 31, 23, 59, 59, 900000000, "GMT"));	
		tests.addElement(new TIMESTAMPDirectToFieldTester("PST New Years Eve 1", 1997, 11, 31, 23, 59, 59, 900000000, "America/Los_Angeles"));	
		tests.addElement(new TIMESTAMPDirectToFieldTester("EST New Years Day 2000 1", 2000, 1, 1, 0, 0, 1, 0, "America/Montreal"));	
		tests.addElement(new TIMESTAMPDirectToFieldTester("London summer 1", 1998, 7, 1, 14, 37, 53, 365789894, "Europe/London"));	
		tests.addElement(new TIMESTAMPDirectToFieldTester("LA summer 1", 1999, 5, 31, 12, 59, 59, 565764356, "America/Los_Angeles"));	
		Calendar yearsBack100 = Calendar.getInstance();
		yearsBack100.set(Calendar.YEAR, (yearsBack100.get(Calendar.YEAR) - 100));
		tests.addElement(new TIMESTAMPDirectToFieldTester("100 Years ago 1", yearsBack100));
		Calendar lastDSTYearCal = Calendar.getInstance();
		lastDSTYearCal.set(Calendar.YEAR, lastDSTYear);
		tests.addElement(new TIMESTAMPDirectToFieldTester("Last DST year 1", lastDSTYearCal));
		tests.addElement(new TIMESTAMPDirectToFieldTester("GMT Before 1970 1", 1902, 2, 13, 16, 34, 25, 900000000, "GMT"));
		tests.addElement(new TIMESTAMPDirectToFieldTester("Nano with one zero 1", 465346464057L));
		tests.addElement(new TIMESTAMPDirectToFieldTester("Nano with two zeros 1", 465346464007L));
		return tests;
	}

	static public Vector testInstances2 () 
	{
		Vector tests = new Vector();	 
		tests.addElement(new TIMESTAMPDirectToFieldTester("NOW 2", Calendar.getInstance()));	
		tests.addElement(new TIMESTAMPDirectToFieldTester("ZERO 2", 1900,0,1,0,0,0,0,"GMT"));							
		tests.addElement(new TIMESTAMPDirectToFieldTester("GMT New Years Eve 2", 1997, 11, 31, 23, 59, 59, 900000000, "GMT"));	
		tests.addElement(new TIMESTAMPDirectToFieldTester("PST New Years Eve 2", 1997, 11, 31, 23, 59, 59, 900000000, "America/Los_Angeles"));	
		tests.addElement(new TIMESTAMPDirectToFieldTester("EST New Years Day 2000 2", 2000, 1, 1, 0, 0, 1, 0, "America/Montreal"));	
		tests.addElement(new TIMESTAMPDirectToFieldTester("London summer 2", 1998, 7, 1, 14, 37, 53, 365789894, "Europe/London"));	
		tests.addElement(new TIMESTAMPDirectToFieldTester("LA summer 2", 1999, 5, 31, 12, 59, 59, 565764356, "America/Los_Angeles"));	
		Calendar yearsBack100 = Calendar.getInstance();
		yearsBack100.set(Calendar.YEAR, (yearsBack100.get(Calendar.YEAR) - 100));
		tests.addElement(new TIMESTAMPDirectToFieldTester("100 Years ago 2", yearsBack100));
		Calendar lastDSTYearCal = Calendar.getInstance();
		lastDSTYearCal.set(Calendar.YEAR, lastDSTYear);
		tests.addElement(new TIMESTAMPDirectToFieldTester("Last DST year 2", lastDSTYearCal));
		tests.addElement(new TIMESTAMPDirectToFieldTester("GMT Before 1970 2", 1902, 2, 13, 16, 34, 25, 900000000, "GMT"));
		tests.addElement(new TIMESTAMPDirectToFieldTester("Nano with one zero 2", 465346464057L));
		tests.addElement(new TIMESTAMPDirectToFieldTester("Nano with two zeros 2", 465346464007L));
		return tests;
	}
}
