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
package org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWNode;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWQueryKey;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.InterfaceDescriptorCreationException;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.utility.filters.Filter;



public interface MWRelationalDescriptor extends MWNode {

	boolean isTableDescriptor();	
	boolean isInterfaceDescriptor();
	boolean isAggregateDescriptor();	
	
	
	// ************* Morphing Support ***********
		
	MWAggregateDescriptor asMWAggregateDescriptor();
	MWTableDescriptor asMWTableDescriptor() throws InterfaceDescriptorCreationException;
	MWInterfaceDescriptor asMWInterfaceDescriptor() throws InterfaceDescriptorCreationException;
	
	void initializeFromMWAggregateDescriptor(MWAggregateDescriptor oldDescriptor);	
	void initializeFromMWRelationalClassDescriptor(MWRelationalClassDescriptor oldDescriptor);	
	void initializeFromMWTableDescriptor(MWTableDescriptor oldDescriptor);
	void initializeFromMWInterfaceDescriptor(MWInterfaceDescriptor oldDescriptor);


	// ************* Query Keys ***********
	
	Iterator allQueryKeys();	
	Iterator allQueryKeyNames();	
	MWQueryKey queryKeyNamed(String name);
	Iterator allQueryKeysIncludingInherited();	
	MWQueryKey queryKeyNamedIncludingInherited(String name);
		
	// ************* InterfaceDesc and Var 1-1 mapping ***********
	
	Iterator implementors();


	// ************* Expression support ***********
	
	void notifyExpressionsToRecalculateQueryables();
	
	List getQueryables(Filter queryableFilter);
		
	
	// ************* Tables ***********

	Iterator associatedTables();	
	int associatedTablesSize();
	
	Iterator associatedTablesIncludingInherited();
	int associatedTablesIncludingInheritedSize();
	
	MWTable getPrimaryTable();
	
	/**
	 * In certain situations we support "candidate" tables,
	 * ie when mapping reference mappings within aggregate descriptors
	 * Candidate tables are not supported when mapping direct mappings, or other situations
	 * where the user should not be able to select a field from within an aggregate descriptor.
	 * Then associatedTables should be called, aggregate descs will return a nullIterator
	 */
	Iterator candidateTables();	
	int candidateTablesSize();
	
	Iterator candidateTablesIncludingInherited();
	int candidateTablesIncludingInheritedSize();

	// ************* Aggregate Mapping ***********
	
	Collection buildAggregateFieldNameGenerators();


	
	//These really belong in a MWDescriptor interface, but i don't think we need that yet
	MWClass getMWClass();	
	boolean isActive();
	void setActive(boolean active);		
	String getName();
	Iterator mappingsIncludingInherited();
	Iterator mappings();	
	int mappingsSize();
	MWMapping mappingNamed(String name);	

}
