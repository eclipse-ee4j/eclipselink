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
package org.eclipse.persistence.tools.workbench.test.platformsplugin.ui;

import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatform;
import org.eclipse.persistence.tools.workbench.platformsplugin.ui.repository.DatabasePlatformRepositoryNode;
import org.eclipse.persistence.tools.workbench.utility.events.ListChangeAdapter;

/**
 * 
 */
public class DatabasePlatformTabbedPropertiesPageTest extends AbstractPropertiesPageTest {

	public static void main(String[] args) throws Exception {
		new DatabasePlatformTabbedPropertiesPageTest().exec(args);
	}

	public DatabasePlatformTabbedPropertiesPageTest() {
		super();
	}

	protected ListValueModel nodesModel(DatabasePlatformRepositoryNode reposNode) {
		ListValueModel childrenModel = reposNode.getChildrenModel();
		// add a dummy listener so the models wake up
		childrenModel.addListChangeListener(ListValueModel.VALUE, new ListChangeAdapter());
		return childrenModel;
	}

	private DatabasePlatform currentPlatform() {
		return (DatabasePlatform) this.currentValue();
	}

	protected void print() {
		DatabasePlatform currentPlatform = this.currentPlatform();
		if (currentPlatform == null) {
			return;
		}
		System.out.println("current platform: " + currentPlatform);
		System.out.println("\tshort file name: " + currentPlatform.getShortFileName());
		System.out.println("\truntime platform class name: " + currentPlatform.getRuntimePlatformClassName());
		System.out.println("\tsupports native sequencing: " + currentPlatform.supportsNativeSequencing());
		System.out.println("\tcomment: " + currentPlatform.getComment());
	}

}
