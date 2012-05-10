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
package org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational;

import java.util.List;

import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.mappings.xdb.DirectToXMLTypeMapping;

public final class MWDirectToXmlTypeMapping 
	extends MWRelationalDirectMapping 
{
	// **************** Variables *********************************************
	
	private volatile boolean readWholeDocument;
		public final static String READ_WHOLE_DOCUMENT_PROPERTY = "readWholeDocument";


	// **************** Static methods *****************
	
	public static XMLDescriptor buildDescriptor() {	
		XMLDescriptor descriptor = new XMLDescriptor();
		
		descriptor.setJavaClass(MWDirectToXmlTypeMapping.class);
		descriptor.getInheritancePolicy().setParentClass(MWRelationalDirectMapping.class);
				
		XMLDirectMapping readWholeDocumentMapping = (XMLDirectMapping) descriptor.addDirectMapping("readWholeDocument", "read-whole-document/text()");
		readWholeDocumentMapping.setNullValue(Boolean.FALSE);
		
		return descriptor;	
	}


	// **************** Constructors ***************

	/** Default constructor - for TopLink use only */
	private MWDirectToXmlTypeMapping() {
		super();
	}

	MWDirectToXmlTypeMapping(MWMappingDescriptor parent, MWClassAttribute attribute, String name) {
		super(parent, attribute, name);
	}
	
	public String iconKey() {
		return "mapping.directToXmlType";
	}


	//************** Read Whole Document *************
	
	public boolean isReadWholeDocument() {
		return this.readWholeDocument;
	}
	
	public void setReadWholeDocument(boolean readWholeDocument) {
		boolean oldValue = this.readWholeDocument;
		this.readWholeDocument = readWholeDocument;
		firePropertyChanged(READ_WHOLE_DOCUMENT_PROPERTY, oldValue, readWholeDocument);
	}


	// **************** Morphing ****************
	
	protected void initializeOn(MWMapping newMapping) {
		newMapping.initializeFromMWDirectToXmlTypeMapping(this);
	}

	public MWDirectToXmlTypeMapping asMWDirectToXmlTypeMapping() {
		return this;
	}


	// **************** Runtime Conversion ****************
	
	public DatabaseMapping runtimeMapping() {
		DirectToXMLTypeMapping mapping = (DirectToXMLTypeMapping) super.runtimeMapping();
		mapping.setShouldReadWholeDocument(isReadWholeDocument());

		return mapping;
	}

	protected DatabaseMapping buildRuntimeMapping() {
		return new DirectToXMLTypeMapping();
	}


	//************* Problem Handling ************
	
	protected void addProblemsTo(List newProblems) {
		super.addProblemsTo(newProblems);
		this.checkDatabasePlatform(newProblems);
		this.checkAttributeType(newProblems);
		this.checkColumnType(newProblems);
	}
	
	private void checkDatabasePlatform(List newProblems) {
		if ( ! this.getDatabase().getDatabasePlatform().containsDatabaseTypeNamed("XMLTYPE")) {
			newProblems.add(this.buildProblem(ProblemConstants.MAPPING_XML_TYPE_ON_NON_ORACLE_9i_PLATFORM));
		}
	}
	
	private void checkAttributeType(List newProblems) {
		MWClass attributeType = this.getInstanceVariable().getType();
		if ( ! attributeType.mightBeAssignableTo(this.typeFor(String.class))
				&& ! attributeType.mightBeAssignableTo(this.typeFor(org.w3c.dom.Document.class))
				&& ! attributeType.mightBeAssignableTo(this.typeFor(org.w3c.dom.Node.class)))
		{
			newProblems.add(this.buildProblem(ProblemConstants.MAPPING_XML_TYPE_WITH_INCORRECT_ATTRIBUTE_TYPE));		
		}
	}
	
	
	private void checkColumnType(List newProblems) {
		if (this.getColumn() == null) {
			return;
		}
		if (!this.getColumn().getDatabaseType().getName().equals("XMLTYPE")) {
			newProblems.add(this.buildProblem(ProblemConstants.MAPPING_XML_TYPE_WITH_INCORRECT_DATABASE_TYPE));		
		}
	}
}
