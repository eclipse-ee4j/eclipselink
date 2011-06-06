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

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWDataField;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWXmlDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWFieldTransformerAssociation;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWMethod;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWSchemaContextComponent;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXmlField;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXmlNode;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXpathContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXpathSpec;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.SchemaChange;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;

public final class MWXmlFieldTransformerAssociation
	extends MWFieldTransformerAssociation
	implements MWXmlNode, MWXpathContext
{
	// **************** Variables *********************************************
	
	/** The xml field associated with the FieldTransformer class */
	private MWXmlField xmlField;
	
	
	// **************** Constructors ******************************************
	
	/** Default constructor - for TopLink use only */
	private MWXmlFieldTransformerAssociation() {
		super();
	}
	
	MWXmlFieldTransformerAssociation(MWXmlTransformationMapping parent) {
		super(parent);
	}
	
	MWXmlFieldTransformerAssociation(MWXmlTransformationMapping parent, String xpath, MWClass fieldTransformerClass) {
		super(parent, fieldTransformerClass);
		this.xmlField.setXpath(xpath);
	}
	
	MWXmlFieldTransformerAssociation(MWXmlTransformationMapping parent, String xpath, MWMethod transformationMethod) {
		super(parent, transformationMethod);
		this.xmlField.setXpath(xpath);
	}
	
	// **************** Initialization ****************************************
	
	protected void initialize(Node parent) {
		super.initialize(parent);
		this.xmlField = new MWXmlField(this);
	}
	
	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.xmlField);
	}
	
	
	// **************** Xml field ****************************************
	
	public MWXmlField getXmlField() {
		return this.xmlField;
	}
	
	public MWDataField getField() {
		return this.getXmlField();
	}
	
	
	// **************** MWXpathContext implementation  ************************
	
	public MWSchemaContextComponent schemaContext(MWXmlField xmlField) {
		return this.xmlDescriptor().getSchemaContext();
	}
	
	public MWXpathSpec xpathSpec(MWXmlField xmlField) {
		return this.buildXpathSpec();
	}
	
	protected MWXpathSpec buildXpathSpec() {
		return new MWXpathSpec() {
			public boolean mayUseCollectionData() {
				return false;
			}
			
			// transformation mappings should be able to use both 
			// direct and complex fields
			
			public boolean mayUseComplexData() {
				return true;
			}
			
			public boolean mayUseSimpleData() {
				return true;
			}
		};
	}
	
	
	// **************** Convenience *******************************************
	
	private MWXmlTransformationMapping xmlTransformationMapping() {
		return (MWXmlTransformationMapping) this.getParent();
	}
	
	private MWXmlDescriptor xmlDescriptor() {
		return this.xmlTransformationMapping().xmlDescriptor();
	}
	
	
	// **************** Model synchronization *********************************
	
	/** @see MWXmlNode#resolveXpaths */
	public void resolveXpaths() {
		this.xmlField.resolveXpaths();
	}
	
	/** @see MWXmlNode#schemaChanged(SchemaChange) */
	public void schemaChanged(SchemaChange change) {
		this.xmlField.schemaChanged(change);
	}
	
	
	// **************** Problem handling **************************************
	
	protected void addProblemsTo(List currentProblems) {
		super.addProblemsTo(currentProblems);
		this.addXpathMissingProblemTo(currentProblems);
	}
	
	private void addXpathMissingProblemTo(List newProblems) {
		if (! this.getXmlField().isSpecified()) {
			newProblems.add(this.buildProblem(ProblemConstants.MAPPING_FIELD_TRANSFORMER_XPATH_MISSING));
		}
	}
	
	/** Return true if the xpath specified by the xml field is used by another field transformer association */
	public boolean duplicateXpath(String xpath) {
		if (xpath.equals("")) {
			return false;
		}
		
		for (Iterator stream = this.xmlTransformationMapping().fieldTransformerAssociations(); stream.hasNext(); ) {
			MWXmlFieldTransformerAssociation association = (MWXmlFieldTransformerAssociation) stream.next();
			
			if (association != this && xpath.equals(association.getXmlField().getXpath())) {
				return true;
			}
		}
		
		return false;
	}
	
	
	// **************** TopLink Methods ***************************************
	
	public static XMLDescriptor buildDescriptor() {	
		XMLDescriptor descriptor = new XMLDescriptor();
		
		descriptor.setJavaClass(MWXmlFieldTransformerAssociation.class);
		descriptor.getInheritancePolicy().setParentClass(MWFieldTransformerAssociation.class);
		
		XMLCompositeObjectMapping xmlFieldMapping = new XMLCompositeObjectMapping();
		xmlFieldMapping.setReferenceClass(MWXmlField.class);
		xmlFieldMapping.setAttributeName("xmlField");
		xmlFieldMapping.setGetMethodName("getXmlFieldForTopLink");
		xmlFieldMapping.setSetMethodName("setXmlFieldForTopLink");
		xmlFieldMapping.setXPath("xml-field");
		descriptor.addMapping(xmlFieldMapping);
		
		return descriptor;
	}
	
	private MWXmlField getXmlFieldForTopLink() {
		return (this.xmlField.isSpecified()) ? this.xmlField : null;
	}
	
	private void setXmlFieldForTopLink(MWXmlField xmlField) {
		this.xmlField = ((xmlField == null) ? new MWXmlField(this) : xmlField);
	}
}
