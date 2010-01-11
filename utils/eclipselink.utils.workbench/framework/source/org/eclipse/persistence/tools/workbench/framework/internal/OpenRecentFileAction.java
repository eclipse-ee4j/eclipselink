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
******************************************************************************/
package org.eclipse.persistence.tools.workbench.framework.internal;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JOptionPane;

import org.eclipse.persistence.tools.workbench.uitools.swing.EmptyIcon;
import org.eclipse.persistence.tools.workbench.utility.io.FileTools;


/**
 * This is not really a "framework" action - it is only added to
 * the workbench window's File menu.
 */
final class OpenRecentFileAction
	extends AbstractAction
{
	private WorkbenchWindow workbenchWindow;
	private File recentFile;
	private static final Icon ICON = new EmptyIcon(16);

	OpenRecentFileAction(WorkbenchWindow workbenchWindow, int count, File recentFile) {
		super();
		this.initialize(workbenchWindow, count, recentFile);
	}

	void initialize(WorkbenchWindow workbenchWindow, int count, File recentFile) {
		this.workbenchWindow = workbenchWindow;
		this.recentFile = recentFile;

		String countString = Integer.toString(count);
		String shortFileName = FileTools.shortenFileName(recentFile.getAbsoluteFile());
		this.putValue(NAME, countString + " " + shortFileName);
		this.putValue(SHORT_DESCRIPTION, recentFile.getAbsolutePath());
		this.putValue(SMALL_ICON, ICON);
		if (count < 10) {
			this.putValue(MNEMONIC_KEY, new Integer(countString.charAt(0)));
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (recentFile.exists()) {
			workbenchWindow.nodeManager().open(recentFile, workbenchWindow.getContext());
		} else {
			JOptionPane.showMessageDialog(
							workbenchWindow,
							workbenchWindow.resourceRepository().getString("recentProjectFileNotFound.message", recentFile.getAbsolutePath()),
							workbenchWindow.resourceRepository().getString("recentProjectFileNotFound.title"),
							JOptionPane.ERROR_MESSAGE
			);
			workbenchWindow.getApplication().recentFilesManager().removeRecentFile(recentFile);
		}
	}
}
