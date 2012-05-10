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
package org.eclipse.persistence.tools.workbench.mappingsmodel;

import java.util.Collection;

import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassRepository;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.utility.node.Node;


public interface MWNode 
	extends Node 
{
	// ********** convenience methods **********
	
	MWProject getProject();
	
	MWNode getMWParent();

	MWClassRepository getRepository();

	MWClass typeNamed(String typeName);
	
	MWClass typeFor(Class javaClass);


	// ********** model synchronization support **********
	
	void mappingReplaced(MWMapping oldMapping, MWMapping newMapping);
	
	void descriptorReplaced(MWDescriptor oldDescriptor, MWDescriptor newDescriptor);

	/**
	 * Used as a performance tuning when unmapping descriptors instead
	 * of calling #nodeRemoved(Node) for every mapping in the descriptor.
	 */
	void descriptorUnmapped(Collection mappings);
	
	
	// ********** I/O event methods **********
	
	void resolveClassHandles();
	
	void resolveDescriptorHandles();
	
	void resolveMetadataHandles();
	
	void resolveColumnHandles();
	
	void resolveReferenceHandles();
	
	void resolveMethodHandles();

	void postProjectBuild();
	
	
	// ********** legacy methods **********



}
