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
package org.eclipse.persistence.testing.tests.types;

import java.util.*;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.tools.schemaframework.*;
import oracle.sql.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.internal.platform.database.oracle.TIMESTAMPHelper;

//Bug#3950634  Prevent java.lang.ArrayIndexOutOfBoundsException from occuring
public class CalendarToTSTZWithoutSessionTZTest extends TypeTester {

	public Calendar calToTSTZ;

	public CalendarToTSTZWithoutSessionTZTest () {
		super("NEW");
	}
   
	public CalendarToTSTZWithoutSessionTZTest (String nameOfTest, int year, int month, int date, int hrs, int min, int sec, int nano, String zoneId) 
	{
		super(nameOfTest);		
		calToTSTZ = Calendar.getInstance(TimeZone.getTimeZone(zoneId));		
		calToTSTZ.set(year, month, date, hrs, min, sec);
		calToTSTZ.set(Calendar.MILLISECOND, nano / 1000000);
	}

	public CalendarToTSTZWithoutSessionTZTest (String nameOfTest, Calendar c) 
	{
		super(nameOfTest);
		calToTSTZ = c;
	}

	public CalendarToTSTZWithoutSessionTZTest (String nameOfTest, long time) 
	{
		super(nameOfTest);
		calToTSTZ = Calendar.getInstance();
		calToTSTZ.setTime(new java.util.Date(time));
	}

	public Calendar getCalToTSTZ() {
		return calToTSTZ;
	}

	public void setCalToTSTZ(Calendar calToTSTZ) {
		this.calToTSTZ = calToTSTZ;
	}

    public static RelationalDescriptor descriptor()
	{
		RelationalDescriptor descriptor = new RelationalDescriptor();

		/* First define the class, table and descriptor properties. */
		descriptor.setJavaClass(CalendarToTSTZWithoutSessionTZTest.class);
		descriptor.setTableName("CALENDARTOTSTZ");
		descriptor.setPrimaryKeyFieldName("NAME");

		/* Next define the attribute mappings. */
		descriptor.addDirectMapping("testName", "getTestName", "setTestName", "NAME");
		descriptor.addDirectMapping("calToTSTZ", "CALTOTSTZ");
		return descriptor;
    }

	public static TableDefinition tableDefinition(Session session)
	{
		TableDefinition definition= TypeTester.tableDefinition();

		definition.setName("CALENDARTOTSTZ");

		definition.addField("CALTOTSTZ", TIMESTAMPTZ.class);
		return definition;
	}

	public String toString()
	{
		return getTestName() + " -> " +  TIMESTAMPHelper.printCalendar(calToTSTZ);
	}


	static public Vector testInstances () 
	{
		Vector tests = new Vector();	 
		tests.addElement(new CalendarToTSTZWithoutSessionTZTest("NOW", Calendar.getInstance()));	
		tests.addElement(new CalendarToTSTZWithoutSessionTZTest("ZERO", 1900,0,1,0,0,0,0,"GMT"));							
		return tests;
    }

	protected void verify(WriteTypeObjectTest testCase) throws TestException
	{
		try { 
			super.verify(testCase);
        } catch (java.lang.ArrayIndexOutOfBoundsException e0) {
            throw new TestErrorException("The test throws java.lang.ArrayIndexOutOfBoundsException");
		} catch (TestException e) {
			// JConnect in non native mode
			if (caughtException != null && 
				caughtException.toString().equals("EXCEPTION: org.eclipse.persistence.exceptions.DatabaseException \n"+
																"DESCRIPTION: null \n"+
																"INTERNAL EXCEPTION: java.sql.SQLException: JZ0S8: An escape sequence in a SQL Query was malformed.\n"+
																"ERROR CODE: 0\n\n")) {
					throw new TestProblemException("JConnect does not do dates in non-native SQL:\n" + caughtException.getInternalException());
			}

			if (testCase.getObjectFromDatabase() == null) {
				throw new TestErrorException("TIMESTAMPTester throws exception: " + e);
			}
        }
    }

}
