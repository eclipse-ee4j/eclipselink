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
package org.eclipse.persistence.tools.workbench.test.platformsplugin.ui;

import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatform;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatformRepository;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabaseType;
import org.eclipse.persistence.tools.workbench.platformsplugin.ui.platform.DatabasePlatformNode;
import org.eclipse.persistence.tools.workbench.platformsplugin.ui.repository.DatabasePlatformRepositoryNode;
import org.eclipse.persistence.tools.workbench.utility.events.ListChangeAdapter;

/**
 * 
 */
public class DatabaseTypePropertiesPageTest extends AbstractPropertiesPageTest {

	public static void main(String[] args) throws Exception {
		new DatabaseTypePropertiesPageTest().exec(args);
	}

	public DatabaseTypePropertiesPageTest() {
		super();
	}

	protected ListValueModel nodesModel(DatabasePlatformRepositoryNode reposNode) {
		ListValueModel childrenModel = this.defaultPlatformNode(this.platformNodesModel(reposNode), (DatabasePlatformRepository) reposNode.getValue()).getChildrenModel();
		// add a dummy listener so the models wake up
		childrenModel.addListChangeListener(ListValueModel.VALUE, new ListChangeAdapter());
		return childrenModel;
	}

	protected ListValueModel platformNodesModel(DatabasePlatformRepositoryNode reposNode) {
		ListValueModel childrenModel = reposNode.getChildrenModel();
		// add a dummy listener so the models wake up
		childrenModel.addListChangeListener(ListValueModel.VALUE, new ListChangeAdapter());
		return childrenModel;
	}

	private DatabasePlatformNode defaultPlatformNode(ListValueModel platformsHolder, DatabasePlatformRepository repos) {
		for (Iterator stream = (Iterator) platformsHolder.getValue(); stream.hasNext(); ) {
			DatabasePlatformNode node = (DatabasePlatformNode) stream.next();
			DatabasePlatform platform = (DatabasePlatform) node.getValue();
			if (platform == repos.getDefaultPlatform()) {
				return node;
			}
		}
		throw new IllegalStateException("miss node for default platform: " + repos.getDefaultPlatform());
	}

	private DatabaseType currentType() {
		return (DatabaseType) this.currentValue();
	}

	protected void print() {
		DatabaseType currentType = this.currentType();
		if (currentType == null) {
			return;
		}
		System.out.println("current type: " + currentType);
		System.out.println("\tJDBC type: " + currentType.getJDBCType());
		System.out.println("\tallows size: " + currentType.allowsSize());
		System.out.println("\trequires size: " + currentType.requiresSize());
		System.out.println("\tinitial size: " + currentType.getInitialSize());
		System.out.println("\tallows sub-size: " + currentType.allowsSubSize());
		System.out.println("\tallows null: " + currentType.allowsNull());
		System.out.println("\tcomment: " + currentType.getComment());
	}

}
