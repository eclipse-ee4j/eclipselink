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
package org.eclipse.persistence.tools.workbench.mappingsmodel.query.xml;

import java.util.List;
import java.util.ListIterator;

import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWEisTransactionalPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryManager;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWReadAllQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWReadObjectQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXmlNode;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.SchemaChange;
import org.eclipse.persistence.tools.workbench.utility.iterators.ArrayListIterator;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorQueryManager;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;

public final class MWEisQueryManager
	extends MWQueryManager
	implements MWXmlNode
{
	// **************** Variables *********************************************
	
	private volatile MWEisInteraction insertInteraction;		
	private volatile MWEisInteraction updateInteraction;		
	private volatile MWEisInteraction deleteInteraction;		
	private volatile MWEisInteraction readObjectInteraction;		
	private volatile MWEisInteraction readAllInteraction;		
	private volatile MWEisInteraction doesExistInteraction;
	
	
	// **************** Constructors ******************************************
	
	//Toplink persistence use only please	
	private MWEisQueryManager() {
		super();
	}
	
	public MWEisQueryManager(MWEisTransactionalPolicy descriptor) {
		super(descriptor);
	}
	
	
	// **************** Initialization ****************************************
	
	protected void initialize(Node model) {
		super.initialize(model);
		this.deleteInteraction = new MWEisInteraction(this);
		this.doesExistInteraction = new MWEisInteraction(this);
		this.insertInteraction = new MWEisInteraction(this);
		this.readAllInteraction = new MWEisInteraction(this);
		this.readObjectInteraction = new MWEisInteraction(this);
		this.updateInteraction = new MWEisInteraction(this);
	}
	
	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.deleteInteraction);
		children.add(this.doesExistInteraction);
		children.add(this.insertInteraction);
		children.add(this.readAllInteraction);
		children.add(this.readObjectInteraction);
		children.add(this.updateInteraction);
	}
	
	public MWReadAllQuery buildReadAllQuery(String queryName) {
		return new MWEisReadAllQuery(this, queryName);
	}
	
	public MWReadObjectQuery buildReadObjectQuery(String queryName) {
		return new MWEisReadObjectQuery(this, queryName);
	}
	
	public boolean supportsReportQueries() {
		return false;
	}
	
	public MWEisInteraction getDeleteInteraction() {
		return this.deleteInteraction;
	}
	
	public MWEisInteraction getDoesExistInteraction() {
		return this.doesExistInteraction;
	}
	
	public MWEisInteraction getInsertInteraction() {
		return this.insertInteraction;
	}

	public MWEisInteraction getReadAllInteraction() {
		return this.readAllInteraction;
	}

	public MWEisInteraction getReadObjectInteraction() {
		return this.readObjectInteraction;
	}

	public MWEisInteraction getUpdateInteraction() {
		return this.updateInteraction;
	}
	
	public ListIterator interactions() {
		Object[] interactions = new Object[] {
			this.insertInteraction,
			this.updateInteraction,
			this.deleteInteraction, 
			this.readObjectInteraction,
			this.readAllInteraction,
			this.doesExistInteraction
		};
		return new ArrayListIterator(interactions);
	}
	
	
	// **************** Model synchronization *********************************
	
	/** @see MWXmlNode#resolveXpaths() */
	public void resolveXpaths() {
		// TODO
	}
	
	/** @see MWXmlNode#schemaChanged(SchemaChange) */
	public void schemaChanged(SchemaChange change) {
		// TODO
	}
	
	
	// **************** Runtime conversion ************************************
	
	public void adjustRuntimeDescriptor(ClassDescriptor runtimeDescriptor) {		
		super.adjustRuntimeDescriptor(runtimeDescriptor);

		DescriptorQueryManager rtQueryManager = (DescriptorQueryManager) runtimeDescriptor.getQueryManager();
		
		// Custom Calls
		rtQueryManager.setDeleteCall(getDeleteInteraction().runtimeInteraction());
		rtQueryManager.setInsertCall(getInsertInteraction().runtimeInteraction());
		rtQueryManager.setUpdateCall(getUpdateInteraction().runtimeInteraction());
		rtQueryManager.setReadAllCall(getReadAllInteraction().runtimeInteraction());
		rtQueryManager.setReadObjectCall(getReadObjectInteraction().runtimeInteraction());
		rtQueryManager.setDoesExistCall(getDoesExistInteraction().runtimeInteraction());
	}
	
	
	// **************** TopLink methods ***************************************
	
	public static XMLDescriptor buildDescriptor() {		
		XMLDescriptor descriptor = new XMLDescriptor();
		
		descriptor.setJavaClass(MWEisQueryManager.class);
		descriptor.getInheritancePolicy().setParentClass(MWQueryManager.class);
		
		// composite object mapping - delete interaction
		XMLCompositeObjectMapping deleteMapping = new XMLCompositeObjectMapping();
		deleteMapping.setAttributeName("deleteInteraction");
		deleteMapping.setReferenceClass(MWEisInteraction.class);
		deleteMapping.setXPath("delete-interaction");
		descriptor.addMapping(deleteMapping);

		// composite object mapping - does exist interaction
		XMLCompositeObjectMapping doesExistInteractionMapping = new XMLCompositeObjectMapping();
		doesExistInteractionMapping.setAttributeName("doesExistInteraction");
		doesExistInteractionMapping.setReferenceClass(MWEisInteraction.class);
		doesExistInteractionMapping.setXPath("does-exist-interaction");
		descriptor.addMapping(doesExistInteractionMapping);

		// composite object mapping - insert interaction
		XMLCompositeObjectMapping insertInteractionMapping = new XMLCompositeObjectMapping();
		insertInteractionMapping.setAttributeName("insertInteraction");
		insertInteractionMapping.setReferenceClass(MWEisInteraction.class);
		insertInteractionMapping.setXPath("insert-interaction");
		descriptor.addMapping(insertInteractionMapping);

		// composite object mapping - read all interaction
		XMLCompositeObjectMapping readAllInteractionMapping = new XMLCompositeObjectMapping();
		readAllInteractionMapping.setAttributeName("readAllInteraction");
		readAllInteractionMapping.setReferenceClass(MWEisInteraction.class);
		readAllInteractionMapping.setXPath("read-all-interaction");
		descriptor.addMapping(readAllInteractionMapping);

		// composite object mapping - read object interaction
		XMLCompositeObjectMapping readObjectInteractionMapping = new XMLCompositeObjectMapping();
		readObjectInteractionMapping.setAttributeName("readObjectInteraction");
		readObjectInteractionMapping.setReferenceClass(MWEisInteraction.class);
		readObjectInteractionMapping.setXPath("read-object-interaction");
		descriptor.addMapping(readObjectInteractionMapping);

		// composite object mapping - update interaction
		XMLCompositeObjectMapping updateInteractionMapping = new XMLCompositeObjectMapping();
		updateInteractionMapping.setAttributeName("updateInteraction");
		updateInteractionMapping.setReferenceClass(MWEisInteraction.class);
		updateInteractionMapping.setXPath("update-interaction");
		descriptor.addMapping(updateInteractionMapping);
				
		return descriptor;
	}
}
