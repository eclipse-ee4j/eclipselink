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
package org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWNode;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryManager;
import org.eclipse.persistence.tools.workbench.utility.node.NodeModel;

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
