/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWNode;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryManager;
import org.eclipse.persistence.tools.workbench.utility.node.NodeModel;

import org.eclipse.persistence.descriptors.ClassDescriptor;

public interface MWTransactionalPolicy 
	extends MWNode, NodeModel
{

	MWQueryManager getQueryManager();
	MWRefreshCachePolicy getRefreshCachePolicy();
	MWCachingPolicy getCachingPolicy();
	
    MWLockingPolicy getLockingPolicy();

	boolean isConformResultsInUnitOfWork();
	void setConformResultsInUnitOfWork(boolean conform);
		String CONFORM_RESULTS_IN_UNIT_OF_WORK_PROPERTY = "conformResultsInUnitOfWork";
	
	boolean isReadOnly();
	void setReadOnly(boolean newValue);
		String READ_ONLY_PROPERTY = "readOnly";
	
    String getDescriptorAlias();
    void setDescriptorAlias(String descriptorAlias);
        public final static String DESCRIPTOR_ALIAS_PROPERTY = "descriptorAlias";


    /** Used to keep up to date with inheritance changes */
	void descriptorInheritanceChanged();
	
	
	// ************ runtime conversion ***********

	void adjustRuntimeDescriptor(ClassDescriptor runtimeDescriptor);

	
	// ************ TopLink Methods ***********
	
	MWAbstractTransactionalPolicy getValueForTopLink(); 
    
}
