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
package org.eclipse.persistence.tools.workbench.test.scplugin.app;

import java.util.ArrayList;

import org.eclipse.persistence.tools.workbench.framework.NodeManager;
import org.eclipse.persistence.tools.workbench.framework.Plugin;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.app.NavigatorSelectionModel;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.uitools.app.TreeNodeValueModel;

public class SCTestNodeManager implements NodeManager {

	private ArrayList projectNodes;
	
	public SCTestNodeManager( ApplicationNode projectNode) {
		super();
		initialize( projectNode);
	}

	public void initialize( ApplicationNode projectNode) {
		
		this.projectNodes = new ArrayList( 5);
		this.addProjectNode( projectNode);
	}

	public void addProjectNode( ApplicationNode node) {

		int i = this.projectNodes.size();
		
		this.projectNodes.add( i, node);
	}

	public ApplicationNode[] projectNodesFor( Plugin plugin) {

		return (ApplicationNode[]) this.projectNodes.toArray(new ApplicationNode[this.projectNodes.size()]);
	}

	public TreeNodeValueModel getRootNode() {
		// TODO Auto-generated method stub
		return null;
	}

	public NavigatorSelectionModel getTreeSelectionModel() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public boolean save(ApplicationNode node, WorkbenchContext workbenchContext) {
		return false;
	}


	public void removeProjectNode(ApplicationNode node) {
		// TODO Auto-generated method stub
		
	}
}
