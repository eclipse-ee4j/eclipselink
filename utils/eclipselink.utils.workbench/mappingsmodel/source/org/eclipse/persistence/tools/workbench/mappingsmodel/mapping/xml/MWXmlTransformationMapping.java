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
package org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWXmlDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWTransformationMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWMethod;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWSchemaContextComponent;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXmlField;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXmlNode;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.SchemaChange;
import org.eclipse.persistence.tools.workbench.utility.node.Problem;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;

public final class MWXmlTransformationMapping
	extends MWTransformationMapping
	implements MWXmlMapping
{
	// **************** Constructors ******************************************
	
	/** Default constructor - for TopLink use only */
	private MWXmlTransformationMapping() {
		super();
	}
	
	MWXmlTransformationMapping(MWXmlDescriptor parent, MWClassAttribute attribute, String name) {
		super(parent, attribute, name);
	}
	
	// **************** Field Transformer Associations API ********************
	
	public MWXmlFieldTransformerAssociation 
		addFieldTransformerAssociation(String xpath, MWClass fieldTransformer) 
	{
		MWXmlFieldTransformerAssociation association = 
			new MWXmlFieldTransformerAssociation(this, xpath, fieldTransformer);
		this.addFieldTransformerAssociation(association);
		return association;
	}
		
	public MWXmlFieldTransformerAssociation 
		addFieldTransformerAssociation(String xpath, MWMethod transformationMethod)
	{
		MWXmlFieldTransformerAssociation association = 
			new MWXmlFieldTransformerAssociation(this, xpath, transformationMethod);
		this.addFieldTransformerAssociation(association);
		return association;
	}
	
	/** Builds an empty field transformer association, but does not add it */
	public MWXmlFieldTransformerAssociation buildEmptyFieldTransformerAssociation() {
		MWXmlFieldTransformerAssociation fieldTransformerAssociation = 
			new MWXmlFieldTransformerAssociation(this);
		return fieldTransformerAssociation;
	}
	
	
	// **************** MWXmlMapping contract *********************************
	
	public MWSchemaContextComponent schemaContext() {
		return this.xmlDescriptor().getSchemaContext();
	}
	
	public MWXmlField firstMappedXmlField() {
		for (Iterator stream = this.fieldTransformerAssociations(); stream.hasNext(); ) {
			MWXmlField xmlField = ((MWXmlFieldTransformerAssociation) stream.next()).getXmlField();
			
			if (xmlField.isSpecified()) {
				return xmlField;
			}
		}
		
		return null;
	}
	
	public void addWrittenFieldsTo(Collection writtenFields) {
		if (this.isReadOnly()) {
			return;
		}
		else {
			for (Iterator stream = this.fieldTransformerAssociations(); stream.hasNext(); ) {
				MWXmlField xmlField = ((MWXmlFieldTransformerAssociation) stream.next()).getXmlField();
				
				if (xmlField.isSpecified()) {
					writtenFields.add(xmlField);
				}
			}
		}
	}
	
	
	// **************** Convenience *******************************************
	
	public MWXmlDescriptor xmlDescriptor() {
		return (MWXmlDescriptor) this.getParent();
	}
	
	
	// **************** Model synchronization *********************************
	
	/** @see MWXmlNode#resolveXpaths */
	public void resolveXpaths() {
		for (Iterator stream = this.fieldTransformerAssociations(); stream.hasNext(); ) {
			((MWXmlNode) stream.next()).resolveXpaths();
		}
	}
	
	/** @see MWXmlNode#schemaChanged(SchemaChange) */
	public void schemaChanged(SchemaChange change) {
		for (Iterator stream = this.fieldTransformerAssociations(); stream.hasNext(); ) {
			((MWXmlNode) stream.next()).schemaChanged(change);
		}
	}
	
	
	// **************** Problem handling **************************************
	
	protected void addProblemsTo(List newProblems) {
		super.addProblemsTo(newProblems);
		this.addDuplicateXpathProblemsTo(newProblems);
	}
	
	private void addDuplicateXpathProblemsTo(List newProblems) {
		for (Iterator stream = this.fieldTransformerAssociations(); stream.hasNext(); ) {
			MWXmlFieldTransformerAssociation association = (MWXmlFieldTransformerAssociation) stream.next();
			String xpath = association.getXmlField().getXpath();
			
			if (association.duplicateXpath(xpath)) {
				Problem problem = this.buildProblem(ProblemConstants.MAPPING_FIELD_TRANSFORMER_XPATH_DUPLICATE, xpath);
				
				if (! newProblems.contains(problem)) {
					newProblems.add(problem);
				}
			}
		}
	}
	
	
	// **************** Runtime conversion ************************************
	
	protected DatabaseMapping buildRuntimeMapping() {
		return this.xmlDescriptor().buildDefaultRuntimeTransformationMapping();
	}
	
	
	// **************** TopLink methods ****************************************
	
	public static XMLDescriptor buildDescriptor() {	
		XMLDescriptor descriptor = new XMLDescriptor();
		
		descriptor.setJavaClass(MWXmlTransformationMapping.class);
		descriptor.getInheritancePolicy().setParentClass(MWTransformationMapping.class);
		
		return descriptor;	
	}
		}
