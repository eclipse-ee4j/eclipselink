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
package org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml;

import java.util.Iterator;
import java.util.List;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWDataField;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWClassIndicatorFieldPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWClassIndicatorPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWClassIndicatorValue;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWSchemaContextComponent;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXmlField;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXmlNode;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXpathContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXpathSpec;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.SchemaChange;
import org.eclipse.persistence.tools.workbench.utility.iterators.NullIterator;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.descriptors.InheritancePolicy;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;

/**
 *  Class indicator policy that holds an xml field
 */
public final class MWXmlClassIndicatorFieldPolicy
	extends MWClassIndicatorFieldPolicy
	implements MWXpathContext
{	
	private MWXmlField xmlField;
	
	private volatile boolean useXSIType;
		public static String USE_XSITYPE_PROPERTY = "useXSIType";
	
	
	// **************** Constructors ******************************************
	
	/** Toplink use only */
	private MWXmlClassIndicatorFieldPolicy() {
		super();
	}
	
	protected MWXmlClassIndicatorFieldPolicy(MWClassIndicatorPolicy.Parent parent) {
		this(parent, NullIterator.instance());
	}
	
	protected MWXmlClassIndicatorFieldPolicy(MWClassIndicatorPolicy.Parent parent, Iterator descriptorsAvailableForIndication) {
		super(parent);
		setDescriptorsAvailableForIndicatorDictionary(descriptorsAvailableForIndication);
	}
	
		
	// **************** Initialization ****************************************	
	
	protected void initialize(Node parent) {
		super.initialize(parent);
		this.xmlField = new MWXmlField(this);
		this.useXSIType = false;
	}
	
	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.xmlField);
	}
	
	
	// **************** Xml field *********************************************
	
	public MWXmlField getXmlField() {
		return this.xmlField;
	}
	
	public MWDataField getField() {
		return this.getXmlField();
	}

	
	// **************** Use xsi type ******************************************
	
	public boolean isUseXSIType() {
		return this.useXSIType;
	}

	public void setUseXSIType(boolean newValue) {
		boolean oldValue = this.useXSIType;
		this.useXSIType = newValue;
		this.firePropertyChanged(USE_XSITYPE_PROPERTY, oldValue, newValue);

        if (newValue) {
        	this.setClassNameIsIndicator(false);
        	this.generateIndicatorValueValues();
        } 
 	}
	
	private void generateIndicatorValueValues() {
		Iterator indicatorValues = classIndicatorValues();
		while(indicatorValues.hasNext()) {
			MWClassIndicatorValue value = (MWClassIndicatorValue)indicatorValues.next();
			MWXmlDescriptor descriptor = (MWXmlDescriptor)value.getDescriptorValue();
			if (descriptor.getSchemaContext() != null) {
				String name = descriptor.getSchemaContext().contextTypeQname();
				value.setIndicatorValue(name);	
			}
		}
	}
	
	private void clearIndicatorValuesValues() {
		Iterator indicatorValues = classIndicatorValues();
		while(indicatorValues.hasNext()) {
			MWClassIndicatorValue value = (MWClassIndicatorValue)indicatorValues.next();
			value.setIndicatorValue(null);
		}
	}
	
	
	// **************** MWXpathContext implementation *************************
	
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
			
			public boolean mayUseComplexData() {
				return false;
			}
			
			public boolean mayUseSimpleData() {
				return true;
			}
		};
	}
	
	
	// **************** Convenience *******************************************
	
	private MWClassIndicatorFieldPolicy.Parent classIndicatorFieldPolicyParent() {
		return (MWClassIndicatorFieldPolicy.Parent) this.getParent();
	}
	
	/** Return the xml descriptor that contains this class indicator field policy */
	private MWXmlDescriptor xmlDescriptor() {
		return (MWXmlDescriptor) this.classIndicatorFieldPolicyParent().getContainingDescriptor();
	}

	
	// **************** Problem support ***************************************
	protected void addProblemsTo(List newProblems) {
		super.addProblemsTo(newProblems);
		this.checkClassIndicatorField(newProblems);
	}
	
	protected boolean fieldSpecified() {
		if (!isUseXSIType()) {
			return getXmlField().isResolved();
		}
		return true;
	}
	
	
	// **************** Model synchronization *********************************
	
	/** @see MWXmlNode#resolveXpaths() */
	public void resolveXpaths() {
		this.xmlField.resolveXpaths();
	}
	
	/** @see MWXmlNode.schemaChanged(SchemaChange) */
	public void schemaChanged(SchemaChange change) {
		this.xmlField.schemaChanged(change);
	}
	
	
	// **************** Runtime conversion ************************************
	
	public void adjustRuntimeInheritancePolicy(InheritancePolicy runtimeInheritancePolicy) {
		super.adjustRuntimeInheritancePolicy(runtimeInheritancePolicy);
		
		if (this.useXSIType) {
			runtimeInheritancePolicy.setClassIndicatorField(new XMLField("@xsi:type"));		
		} 
		else {
			runtimeInheritancePolicy.setClassIndicatorFieldName(this.getXmlField().getXpath());
		}
	}
	
	
	// **************** TopLink methods ***************************************
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWXmlClassIndicatorFieldPolicy.class);	
		descriptor.getInheritancePolicy().setParentClass(MWClassIndicatorFieldPolicy.class);
		
		XMLDirectMapping useXSITypeMapping = (XMLDirectMapping)descriptor.addDirectMapping("useXSIType", "@use-xsitype");
		useXSITypeMapping.setNullValue(Boolean.FALSE);
		
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
