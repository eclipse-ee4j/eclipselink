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
import java.util.List;

import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWOXDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWXmlDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWSchemaContextComponent;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXmlField;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXmlNode;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXpathContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXpathSpec;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.SchemaChange;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;

public abstract class MWAbstractAnyMapping 
	extends MWMapping 
	implements MWXmlMapping, MWXpathedMapping, MWXpathContext
{
	// **************** Variables *********************************************
	
	private MWXmlField xmlField;
	
	private volatile boolean wildcardMapping;
		public static final String WILDCARD_PROPERTY = "wildcardMapping";
	
	
	// **************** Constructors ******************************************
	
	/** Default constructor - for TopLink use only */
	protected MWAbstractAnyMapping() {
		super();
	}
	
	protected MWAbstractAnyMapping(MWXmlDescriptor descriptor, MWClassAttribute attribute, String name) {
		super(descriptor, attribute, name);
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
	
	
	// **************** MWXpathedMapping implementation  **********************
	
	public MWXmlField getXmlField() {
		return this.xmlField;
	}
	
	
	// **************** Wildcard mapping **************************************
	
	public boolean isWildcardMapping() {
		return this.wildcardMapping;
	}
	
	public void setWildcardMapping(boolean newValue) {
		boolean oldValue = this.wildcardMapping;
		this.wildcardMapping = newValue;
		this.firePropertyChanged(WILDCARD_PROPERTY, oldValue, newValue);
	}
	
	
	// **************** MWXmlMapping contract *********************************
	
	public MWSchemaContextComponent schemaContext() {
		return this.oxDescriptor().getSchemaContext();
	}
	
	public MWXmlField firstMappedXmlField() {
		if (this.getXmlField().isResolved()) {
			return this.getXmlField();
		}
		else {
			return null;
		}
	}
	
	public void addWrittenFieldsTo(Collection writtenFields) {
		if (! this.isReadOnly() && ! this.getXmlField().getXpath().equals("")) {
			writtenFields.add(this.getXmlField());
		}
	}
	
	
	// **************** MWXpathContext implementation  ************************
	
	public MWSchemaContextComponent schemaContext(MWXmlField xmlField) {
		return this.schemaContext();
	}
	
	public MWXpathSpec xpathSpec(MWXmlField xmlField) {
		return this.buildXpathSpec();
	}
	
	protected MWXpathSpec buildXpathSpec() {
		return new MWXpathSpec() {
			public boolean mayUseCollectionData() {
				return MWAbstractAnyMapping.this.mayUseCollectionData();
			}
			
			public boolean mayUseComplexData() {
				return true;
			}
			
			public boolean mayUseSimpleData() {
				return false;
			}
		};
	}
	
	protected abstract boolean mayUseCollectionData();
	
	
	// **************** Convenience *******************************************
	
	protected MWOXDescriptor oxDescriptor() {
		return (MWOXDescriptor) this.getParent();
	}
	
	// **************** Morphing **********************************************
	
	protected void initializeFromMWXpathedMapping(MWXpathedMapping oldMapping) {
		super.initializeFromMWXpathedMapping(oldMapping);
		
		this.getXmlField().setXpath(oldMapping.getXmlField().getXpath());
		// can't use typed, so don't set that
	}
	
	
	// **************** Problem handling **************************************
	
	protected void addProblemsTo(List newProblems) {
		// would like to add xpath and reference descriptor problems first
		this.addXmlFieldProblemsTo(newProblems);
		this.addWildcardProblemsTo(newProblems);
		super.addProblemsTo(newProblems);
	}
	
	protected void addXmlFieldProblemsTo(List newProblems) {
		if (! this.oxDescriptor().isAnyTypeDescriptor()) {
			this.addXpathNotSpecifiedProblemTo(newProblems);
		}
	}
	
	private void addXpathNotSpecifiedProblemTo(List newProblems) {
		if (!this.oxDescriptor().isAnyTypeDescriptor() && !this.getXmlField().isSpecified()) {
			newProblems.add(this.buildProblem(ProblemConstants.XPATH_NOT_SPECIFIED));
		}
	}
	
	private void addWildcardProblemsTo(List newProblems) {
		this.addWildcardSpecifiedProblemTo(newProblems);
		this.addNoWildcardInSchemaContextProblemTo(newProblems);
		this.addMapsToNonAttributesProblemTo(newProblems);
	}
	
	private void addWildcardSpecifiedProblemTo(List newProblems) {
		// if this is in an anyType descriptor, it cannot be a wildcard mapping
		if (this.oxDescriptor().isAnyTypeDescriptor() && this.isWildcardMapping()) {
			newProblems.add(this.buildProblem(ProblemConstants.MAPPING_WILDCARD_SPECIFIED_IN_ANY_TYPE_DESCRIPTOR));
		}
	}
	
	private void addNoWildcardInSchemaContextProblemTo(List newProblems) {
		if (this.isWildcardMapping()) {
			MWSchemaContextComponent context = this.schemaContext();
			
			if (context != null && ! context.containsWildcard()) {
				newProblems.add(this.buildProblem(ProblemConstants.MAPPING_NO_WILDCARD_IN_SCHEMA_CONTEXT));
			}
		}
	}
	
	private void addMapsToNonAttributesProblemTo(List newProblems) {
		// TODO !!!
		
		// The descriptor that owns this mapping may not map to any xml fields that 
		// conflict with the element content of this mapping.
		// 
		//	If this mapping has no xpath, then its context is also the context of
		// the owning descriptor, and the owning descriptor may not map to any xml
		// fields that are not attributes.
		// 
		//	If this mapping has an xpath, then its context is the context of that 
		// xpath, and the descriptor may not map to any xml fields within that context
		// that are not attributes.
		
		// (Will likely need to have an iterator on all xml fields mapped to by
		// the owning descriptor)
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
	
	
	// **************** TopLink methods ***************************************
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWAbstractAnyMapping.class);
		descriptor.descriptorIsAggregate();
		
		descriptor.getInheritancePolicy().setParentClass(MWMapping.class);
		
		XMLCompositeObjectMapping xmlFieldMapping = new XMLCompositeObjectMapping();
		xmlFieldMapping.setReferenceClass(MWXmlField.class);
		xmlFieldMapping.setAttributeName("xmlField");
		xmlFieldMapping.setGetMethodName("getXmlFieldForTopLink");
		xmlFieldMapping.setSetMethodName("setXmlFieldForTopLink");
		xmlFieldMapping.setXPath("xpath");
		descriptor.addMapping(xmlFieldMapping);
		
		((XMLDirectMapping) descriptor.addDirectMapping("wildcardMapping", "wildcard-mapping/text()")).setNullValue(Boolean.FALSE);
		
		return descriptor;
	}

	private MWXmlField getXmlFieldForTopLink() {
		return (this.xmlField.isSpecified()) ? this.xmlField: null;
	}
	
	private void setXmlFieldForTopLink(MWXmlField xmlField) {
		this.xmlField = ((xmlField == null) ? new MWXmlField(this) : xmlField);
	}
}
