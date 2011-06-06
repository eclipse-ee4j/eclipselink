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
package org.eclipse.persistence.tools.workbench.test.models.employee;

import java.io.StringWriter;
import java.sql.Timestamp;

import org.eclipse.persistence.descriptors.DescriptorEvent;

/**  
 * <b>Purpose</b>: Larger scale projects within the Employee Demo
 * <p><b>Description</b>: 	LargeProject is a concrete subclass of Project. It is instantiated for Projects with type = 'L'. The additional
 *								information (budget, & milestoneVersion) are mapped from the LPROJECT table.
 *	@see Project
 */

public class LargeProject extends Project implements LargeProjectInterface
{
	public double budget;
	public Timestamp milestoneVersion;
public LargeProject()
{
	this.budget = 0.0;
}
public double getBudget()
{
	return budget;
}
public Timestamp getMilestoneVersion()
{
	return milestoneVersion;
}
public void postBuild(DescriptorEvent event)
{
}
public void postClone(DescriptorEvent event)
{
}
protected void postMerge(DescriptorEvent event)
{
}
public void preUpdate(DescriptorEvent event)
{
}
public void aboutToUpdate(DescriptorEvent event)
{
}
public void preInsert(DescriptorEvent event)
{
}
public void preWrite(DescriptorEvent event)
{
}
private void postDelete(DescriptorEvent event)
{
}
public void aboutToInsert(DescriptorEvent event)
{
}
public void setBudget(double budget)
{
	this.budget = budget;
}
public void setMilestoneVersion(Timestamp milestoneVersion)
{
	this.milestoneVersion = milestoneVersion;
}
/**
 * Print the project's data.
 */

@Override
public String toString()
{
	StringWriter writer = new StringWriter();
	
	writer.write("Large Project: ");	
	writer.write(getName());
	writer.write(" ");
	writer.write(getDescription());
	writer.write(" " + getBudget());
	writer.write(" ");
	writer.write(String.valueOf(getMilestoneVersion()));
	return writer.toString();
}
}
