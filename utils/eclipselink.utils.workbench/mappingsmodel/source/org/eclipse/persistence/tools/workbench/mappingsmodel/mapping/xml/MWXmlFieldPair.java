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
package org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml;

import java.util.Iterator;
import java.util.List;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWEisDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWXmlDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWSchemaContextComponent;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXmlField;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXmlNode;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXpathContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXpathSpec;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.SchemaChange;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.eis.mappings.EISOneToManyMapping;
import org.eclipse.persistence.eis.mappings.EISOneToOneMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;

public final class MWXmlFieldPair 
	extends MWModel
	implements MWXmlNode, MWXpathContext
{
	// **************** Variables *********************************************
	
	private MWXmlField sourceXmlField;
	
	private MWXmlField targetXmlField;
	
	
	// **************** Constructors ***************

	/** Default constructor - for TopLink use only */
	protected MWXmlFieldPair() {
		super();
	}
	
	MWXmlFieldPair(MWXmlReferenceMapping parent) {
		super(parent);
	}
	
	MWXmlFieldPair(MWXmlReferenceMapping parent, String sourceXpath, String targetXpath) {
		this(parent);
		this.sourceXmlField.setXpath(sourceXpath);
		this.targetXmlField.setXpath(targetXpath);
	}
	
	
	// **************** Initialization ****************************************
	
	/** initialize persistent state */
	protected void initialize(Node parent) {
		super.initialize(parent);
		this.sourceXmlField = new MWXmlField(this);
		this.targetXmlField = new MWXmlField(this);
	}

	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.sourceXmlField);
		children.add(this.targetXmlField);
	}
	
	
	// **************** Source field ******************************************
	
	public MWXmlField getSourceXmlField() {
		return this.sourceXmlField;
	}
	
	
	// **************** Target field ******************************************
	
	public MWXmlField getTargetXmlField() {
		return this.targetXmlField;
	}
	
	
	// **************** MWXpathContext implementation  ************************
	
	public MWSchemaContextComponent schemaContext(MWXmlField xmlField) {
		if (xmlField == this.getTargetXmlField()) {
			MWXmlDescriptor xmlReferenceDescriptor = this.xmlReferenceDescriptor();
			return (xmlReferenceDescriptor == null) ? null: xmlReferenceDescriptor.getSchemaContext();
		}
		else {
			return this.xmlDescriptor().getSchemaContext();
		}
	}
	
	public MWXpathSpec xpathSpec(MWXmlField xmlField) {
		return this.buildXpathSpec(xmlField);
	}
	
	protected MWXpathSpec buildXpathSpec(final MWXmlField xmlField) {
		return new MWXpathSpec() {
			public boolean mayUseCollectionData() {
				return MWXmlFieldPair.this.mayUseCollectionXpath(xmlField);
			}
			
			public boolean mayUseComplexData() {
				return false;
			}
			
			public boolean mayUseSimpleData() {
				return true;
			}
		};
	}
	
	protected boolean mayUseCollectionXpath(MWXmlField xmlField) {
		if (xmlField == this.getSourceXmlField()) {
			return this.xmlRefMapping().sourceFieldMayUseCollectionXpath();
		}
		else {
			return false;
		}
	}
	
	
	// **************** Convenience *******************************************
	
	private MWXmlDescriptor xmlDescriptor() {
		return (MWXmlDescriptor) this.xmlRefMapping().getParent();
	}

	private MWXmlReferenceMapping xmlRefMapping() {
		return (MWXmlReferenceMapping) this.getParent();
	}
	
	private MWXmlDescriptor xmlReferenceDescriptor() {
		return this.xmlRefMapping().referenceDescriptor();
	}	
	
	// **************** Model synchronization *********************************
	
	/** @see MWXmlNode#resolveXpaths */
	public void resolveXpaths() {
		this.sourceXmlField.resolveXpaths();
		this.targetXmlField.resolveXpaths();
	}
	
	/** @see MWXmlNode#schemaChanged(SchemaChange) */
	public void schemaChanged(SchemaChange change) {
		this.sourceXmlField.schemaChanged(change);
		this.targetXmlField.schemaChanged(change);
	}
	
	
	// **************** Problem handling **************************************
	
	protected void addProblemsTo(List currentProblems) {
		super.addProblemsTo(currentProblems);
		this.addSourceFieldMissingProblemTo(currentProblems);
		this.addTargetFieldMissingProblemTo(currentProblems);
	}
	
	private void addSourceFieldMissingProblemTo(List newProblems) {
		if (! this.getSourceXmlField().isSpecified()) {
			newProblems.add(this.buildProblem(ProblemConstants.MAPPING_SOURCE_XPATH_MISSING));
		}
	}
	
	private void addTargetFieldMissingProblemTo(List newProblems) {
		if (! this.getTargetXmlField().isSpecified()) {
			newProblems.add(this.buildProblem(ProblemConstants.MAPPING_TARGET_XPATH_MISSING));
		}
	}
	
	/** Return true if the xpath is used by another source field */
	public boolean duplicateSourceXpath(String xpath) {
		if (xpath.equals("")) {
			return false;
		}
		
		for (Iterator stream = this.xmlRefMapping().xmlFieldPairs(); stream.hasNext(); ) {
			MWXmlFieldPair fieldPair = (MWXmlFieldPair) stream.next();
			
			if (fieldPair != this && xpath.equals(fieldPair.getSourceXmlField().getXpath())) {
				return true;
			}
		}
		
		return false;
	}
	
	/** Return true if the xpath specified by the xml field is used by another target field */
	public boolean duplicateTargetXpath(String xpath) {
		if (xpath.equals("")) {
			return false;
		}
		
		for (Iterator stream = this.xmlRefMapping().xmlFieldPairs(); stream.hasNext(); ) {
			MWXmlFieldPair fieldPair = (MWXmlFieldPair) stream.next();
			
			if (fieldPair != this && xpath.equals(fieldPair.getTargetXmlField().getXpath())) {
				return true;
			}
		}
		
		return false;
	}
	
	
	// **************** Runtime conversion ************************************
	
	public void addRuntimeForeignKeyField(EISOneToOneMapping mapping) {
		if (this.getSourceXmlField().isSpecified() && this.getTargetXmlField().isSpecified()) {
			mapping.addForeignKeyField(this.getSourceXmlField().runtimeField(), this.getTargetXmlField().runtimeField());
		}
	}
	
	public void addRuntimeForeignKeyField(EISOneToManyMapping mapping, MWXmlField groupingElement) {	
		if (this.getSourceXmlField().isSpecified()&& this.getTargetXmlField().isSpecified()) {
			mapping.addForeignKeyField(
				this.getSourceXmlField().runtimeField(groupingElement), 
				this.getTargetXmlField().runtimeField(groupingElement)
			);
		}
	}
	
	
	// **************** TopLink Only Methods **********************************
		
	public static XMLDescriptor buildDescriptor() {	
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWXmlFieldPair.class);
		
		XMLCompositeObjectMapping sourceFieldMapping = new XMLCompositeObjectMapping();
		sourceFieldMapping.setReferenceClass(MWXmlField.class);
		sourceFieldMapping.setAttributeName("sourceXmlField");
		sourceFieldMapping.setGetMethodName("getSourceXmlFieldForTopLink");
		sourceFieldMapping.setSetMethodName("setSourceXmlFieldForTopLink");
		sourceFieldMapping.setXPath("source-xml-field");
		descriptor.addMapping(sourceFieldMapping);
		
		XMLCompositeObjectMapping targetFieldMapping = new XMLCompositeObjectMapping();
		targetFieldMapping.setReferenceClass(MWXmlField.class);
		targetFieldMapping.setAttributeName("targetXmlDataFieldHandle");
		targetFieldMapping.setGetMethodName("getTargetXmlFieldForTopLink");
		targetFieldMapping.setSetMethodName("setTargetXmlFieldForTopLink");
		targetFieldMapping.setXPath("target-xml-field");
		descriptor.addMapping(targetFieldMapping);

		return descriptor;	
	}
	
	private MWXmlField getSourceXmlFieldForTopLink() {
		return (this.sourceXmlField.isSpecified()) ? this.sourceXmlField : null;
	}
	
	private void setSourceXmlFieldForTopLink(MWXmlField xmlField) {
		this.sourceXmlField = ((xmlField == null) ? new MWXmlField(this) : xmlField);
	}
	
	private MWXmlField getTargetXmlFieldForTopLink() {
		return (this.targetXmlField.isSpecified()) ? this.targetXmlField : null;
	}
	
	private void setTargetXmlFieldForTopLink(MWXmlField xmlField) {
		this.targetXmlField = ((xmlField == null) ? new MWXmlField(this) : xmlField);
	}
}
