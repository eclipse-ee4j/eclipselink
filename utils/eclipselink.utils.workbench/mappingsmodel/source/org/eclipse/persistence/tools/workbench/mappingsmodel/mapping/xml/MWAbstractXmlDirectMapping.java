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

import java.util.Collection;
import java.util.List;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWDataField;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWXmlDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWDirectMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWTypeConversionConverter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWSchemaContextComponent;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXmlField;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXpathContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXpathSpec;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.SchemaChange;
import org.eclipse.persistence.tools.workbench.utility.node.Node;
import org.eclipse.persistence.tools.workbench.utility.node.Problem;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.foundation.AbstractDirectMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;

@SuppressWarnings("nls")
public abstract class MWAbstractXmlDirectMapping 
	extends MWDirectMapping
	implements MWXmlMapping, MWXpathedMapping, MWXpathContext
{
	// **************** Variables *********************************************
	
	/** Aggregately mapped, so no property change */
	private MWXmlField xmlField;
	
	
	// **************** Constructors ******************************************

	/** Default constructor - for TopLink use only */
	protected MWAbstractXmlDirectMapping() {
		super();
	}
	
	MWAbstractXmlDirectMapping(MWXmlDescriptor parent, MWClassAttribute attribute, String name) {
		super(parent, attribute, name);
	}
	
	
	// **************** Initialization ****************************************
	
	@Override
	protected void initialize(Node parent) {
		super.initialize(parent);
		this.xmlField = new MWXmlField(this);
	}
	
	@Override
	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.xmlField);
	}
	
	
	// **************** MWDirectMapping implementation ************************
	
	@Override
	protected MWTypeConversionConverter buildTypeConversionConverter() {
		return new MWXmlTypeConversionConverter(this);
	}


	// **************** MWXpathedMapping implementation  **********************
	
	public MWXmlField getXmlField() {
		return this.xmlField;
	}
	
	
	// **************** MWXmlMapping contract *********************************
	
	public MWSchemaContextComponent schemaContext() {
		return this.xmlDescriptor().getSchemaContext();
	}
	
	public MWXmlField firstMappedXmlField() {
		if (this.getXmlField().isResolved()) {
			return this.getXmlField();
		}
		else {
			return null;
		}
	}
	
	@Override
	public void addWrittenFieldsTo(Collection writtenXpaths) {
		if (! this.isReadOnly() && ! this.getXmlField().getXpath().equals("")) {
			writtenXpaths.add(this.getXmlField());
		}
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
			
			public boolean mayUseComplexData() {
				return false;
			}
			
			public boolean mayUseSimpleData() {
				return true;
			}
		};
	}
	
	
	// **************** Convenience *******************************************
	
	private MWXmlDescriptor xmlDescriptor() {
		return (MWXmlDescriptor) this.getParent();
	}
	
	
	// **************** Morphing **********************************************
	
	@Override
	protected void initializeFromMWXpathedMapping(MWXpathedMapping oldMapping) {
		super.initializeFromMWXpathedMapping(oldMapping);
		this.getXmlField().setXpath(oldMapping.getXmlField().getXpath());
		this.getXmlField().setTyped(oldMapping.getXmlField().isTyped());
	}
	
	
	// **************** Problem handling **************************************
	
	@Override
	protected void addProblemsTo(List newProblems) {
		// would like to add xpath problems first
		this.addXpathNotSpecifiedProblemTo(newProblems);
		this.addXmlFieldNotDirectProblemTo(newProblems);
		this.addXmlFieldNotSingularProblemTo(newProblems);
		super.addProblemsTo(newProblems);
	}
	
	private void addXpathNotSpecifiedProblemTo(List<Problem> newProblems) {
		if (! this.getXmlField().isSpecified()) {
			newProblems.add(this.buildProblem(ProblemConstants.XPATH_NOT_SPECIFIED));
		}
	}
	
	private void addXmlFieldNotDirectProblemTo(List<Problem> newProblems) {
		if (this.getXmlField().isValid() && ! this.getXmlField().isDirect()) {
			newProblems.add(this.buildProblem(ProblemConstants.XPATH_NOT_DIRECT, this.getXmlField().getXpath()));
		}
	}
	
	private void addXmlFieldNotSingularProblemTo(List<Problem> newProblems) {
		if (this.getXmlField().isValid() && ! this.getXmlField().isSingular()) {
			newProblems.add(this.buildProblem(ProblemConstants.XPATH_NOT_SINGULAR, this.getXmlField().getXpath()));
		}
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
	
	
	// **************** Runtime Conversion ************************************
	
	@Override
	public DatabaseMapping buildRuntimeMapping() {
		return this.xmlDescriptor().buildDefaultRuntimeDirectMapping();
	}
	
	@Override
	public DatabaseMapping runtimeMapping() {
		AbstractDirectMapping runtimeMapping = (AbstractDirectMapping) super.runtimeMapping();
		runtimeMapping.setField(this.getXmlField().runtimeField());
		return runtimeMapping;
	}
	
	
	// **************** TopLink methods ***************************************
	@SuppressWarnings("deprecation")
	public static XMLDescriptor buildDescriptor() {	
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWAbstractXmlDirectMapping.class);
		descriptor.getInheritancePolicy().setParentClass(MWDirectMapping.class);
		
		XMLCompositeObjectMapping xmlFieldMapping = new XMLCompositeObjectMapping();
		xmlFieldMapping.setReferenceClass(MWXmlField.class);
		xmlFieldMapping.setAttributeName("xmlField");
		xmlFieldMapping.setGetMethodName("getXmlFieldForTopLink");
		xmlFieldMapping.setSetMethodName("setXmlFieldForTopLink");
		xmlFieldMapping.setXPath("xml-field");
		descriptor.addMapping(xmlFieldMapping);
		
		return descriptor;	
	}
	
	@SuppressWarnings("unused")
	private MWXmlField getXmlFieldForTopLink() {
		return (this.xmlField.isSpecified()) ? this.xmlField : null;
	}
	
	@SuppressWarnings("unused")
	private void setXmlFieldForTopLink(MWXmlField xmlField) {
		this.xmlField = ((xmlField == null) ? new MWXmlField(this) : xmlField);
	}
}
