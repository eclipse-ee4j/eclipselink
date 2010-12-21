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
package org.eclipse.persistence.tools.workbench.mappingsmodel.project.xml;

import java.util.Iterator;
import java.util.List;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWXmlSchema;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWXmlSchemaRepository;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.SPIManager;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXmlNode;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.SchemaChange;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;

public abstract class MWXmlProject 
	extends MWProject
	implements MWXmlNode
{
	/** A non-changing privately-owned repository for schemas */ 
	private MWXmlSchemaRepository schemaRepository;


	// **************** Static Methods ****************************************
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWXmlProject.class);
		descriptor.getInheritancePolicy().setParentClass(MWProject.class);
		
		XMLCompositeObjectMapping schemaRepositoryMapping = new XMLCompositeObjectMapping();
		schemaRepositoryMapping.setAttributeName("schemaRepository");
		schemaRepositoryMapping.setReferenceClass(MWXmlSchemaRepository.class);
		schemaRepositoryMapping.setXPath("xml-schema-repository");
		descriptor.addMapping(schemaRepositoryMapping);
		
		return descriptor;
	}
	
	
	// **************** Constructors ******************************************
	
	/** Default constructor - for TopLink use only. */
	protected MWXmlProject() {
		super();
	}
	
	protected MWXmlProject(String name, SPIManager spiManager) {
		super(name, spiManager);
	}
	
	
	// **************** Initialization ****************************************
	
	/** initialize persistent state */
	protected void initialize(Node parent) {
		super.initialize(parent);
		this.schemaRepository = new MWXmlSchemaRepository(this);
	}
	
	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.schemaRepository);
	}

	
	// **************** Accessors *********************************************
	
	public MWXmlSchemaRepository getSchemaRepository() {
		return this.schemaRepository;
	}
	
	/** This is used by the I/O manager. */
	public MWModel getMetaDataRepository() {
		return this.getSchemaRepository();
	}
	
	public MWXmlSchema schemaNamed(String schemaName) {
		return getSchemaRepository().getSchema(schemaName);
	}
	

	// ********** automap **********

	public boolean canAutomapDescriptors() {
		return false;
	}

	
	// **************** Model synchronization *********************************
	
	/** @see MWXmlNode#resolveXpaths */
	public void resolveXpaths() {
		for (Iterator stream = this.mappingDescriptors(); stream.hasNext(); ) {
			((MWXmlNode) stream.next()).resolveXpaths();
		}
	}
	
	/** @see MWXmlNode#schemaChanged(SchemaChange) */
	public void schemaChanged(SchemaChange change) {
		for (Iterator stream = this.mappingDescriptors(); stream.hasNext(); ) {
			((MWXmlNode) stream.next()).schemaChanged(change);
		}
	}
	
	
	// **************** TopLink methods ***************************************
	
	protected void resolveInternalReferences() {
		for (Iterator stream = this.schemaRepository.schemas(); stream.hasNext(); ) {
			((MWXmlSchema) stream.next()).resolveReferences();
		}
		super.resolveInternalReferences();
	}

}
