/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.models.complexaggregate;

import java.io.Serializable;
import java.sql.Date;
public class Period implements Serializable {
	public Date startDate;
	public Date endDate;


public static Period example1()
{
	Period example = new Period();
	
	example.setStartDate(new Date(93, 1, 1));
	example.setEndDate(new Date(93, 2, 1));

	return example;
}  
public static Period example2()
{
	Period example = new Period();
	
	example.setStartDate(new Date(93, 1, 1));
	example.setEndDate(new Date(94, 2, 1));

	return example;
}  
public static Period example3()
{
	Period example = new Period();
	
	example.setStartDate(new Date(93, 1, 1));
	example.setEndDate(new Date(95, 2, 1));

	return example;
}  
public static Period example4()
{
	Period example = new Period();
	
	example.setStartDate(new Date(91, 1, 1));
	example.setEndDate(new Date(95, 2, 1));

	return example;
}  
public static Period example5()
{
	Period example = new Period();
	
	example.setStartDate(new Date(90, 1, 1));
	example.setEndDate(new Date(95, 2, 1));

	return example;
}  
public static Period example6()
{
	Period example = new Period();
	
	example.setStartDate(new Date(94, 1, 1));
	example.setEndDate(new Date(95, 2, 1));

	return example;
}  
public static Period example7()
{
	Period example = new Period();
	
	example.setStartDate(new Date(99, 1, 1));
	example.setEndDate(new Date(99, 2, 2));

	return example;
}  
public void setEndDate(Date aDate)
{
	endDate = aDate;
}	
public void setStartDate(Date aDate)
{
	startDate = aDate;
}	
}
