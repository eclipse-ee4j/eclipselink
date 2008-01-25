/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.models.eis.employee;

public class LargeProject extends Project {
	public double budget;
	public String milestoneVersion;

	public LargeProject() {
		this.budget = 0.0;
	}
	
	public double getBudget() {
		return budget;
	}
	
	public String getMilestoneVersion() {
		return milestoneVersion;
	}

	public void setBudget(double budget) {
		this.budget = budget;
	}
	
	public void setMilestoneVersion(String milestoneVersion) {
		this.milestoneVersion = milestoneVersion;
	}
	
}
