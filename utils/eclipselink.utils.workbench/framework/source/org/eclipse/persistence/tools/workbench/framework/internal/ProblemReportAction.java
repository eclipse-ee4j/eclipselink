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
package org.eclipse.persistence.tools.workbench.framework.internal;

import java.io.StringWriter;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationProblemContainer;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.TextAreaDialog;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.utility.io.IndentingPrintWriter;


/**
 * generate report on the problems currently in the problems view
 */
final class ProblemReportAction
	extends AbstractFrameworkAction
{
	private ValueModel appProblemContainerHolder;

	public ProblemReportAction(ValueModel appProblemContainerHolder, WorkbenchContext context) {
		super(context);
		this.appProblemContainerHolder = appProblemContainerHolder;
	}

	protected void initialize() {
		super.initialize();
		this.initializeTextAndMnemonic("PROBLEM_REPORT");
		this.initializeIcon("tools.problemReport");
		this.initializeAccelerator("PROBLEM_REPORT.ACCELERATOR");
	}

	protected void execute() {
		StringWriter sw = new StringWriter(10000);		// start big
		IndentingPrintWriter ipw = new IndentingPrintWriter(sw, "   ");
		this.getApplicationProblemContainer().printBranchApplicationProblemsOn(ipw);
		TextAreaDialog dialog = new TextAreaDialog(sw.toString(), "problemsPane", this.getWorkbenchContext());
		dialog.setTitle(resourceRepository().getString("PROBLEM_REPORT_DIALOG_TITLE"));
		dialog.show();
	}

	private ApplicationProblemContainer getApplicationProblemContainer() {
		return (ApplicationProblemContainer) this.appProblemContainerHolder.getValue();
	}

}
