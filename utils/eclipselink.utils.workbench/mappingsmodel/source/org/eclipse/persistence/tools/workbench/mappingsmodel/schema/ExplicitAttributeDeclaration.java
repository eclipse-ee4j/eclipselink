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
package org.eclipse.persistence.tools.workbench.mappingsmodel.schema;

import java.util.Iterator;
import java.util.List;

import javax.xml.XMLConstants;

import org.apache.xerces.impl.dv.xs.XSSimpleTypeDecl;
import org.apache.xerces.impl.xs.XSAttributeDecl;
import org.apache.xerces.impl.xs.XSAttributeUseImpl;
import org.apache.xerces.xs.XSConstants;
import org.apache.xerces.xs.XSObject;
import org.eclipse.persistence.mappings.converters.ObjectTypeConverter;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.utility.iterators.NullIterator;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

public final class ExplicitAttributeDeclaration
	extends AbstractNamedSchemaComponent
	implements MWAttributeDeclaration
{
	private volatile MWSimpleTypeDefinition type;
	
	private volatile String use;
	
	private volatile String defaultValue;
	
	private volatile String fixedValue;
	
	
	// **************** Constructors ******************************************
	
	/** For TopLink use only */
	private ExplicitAttributeDeclaration() {
		super();
	}
	
	ExplicitAttributeDeclaration(MWModel parent, String name) {
		super(parent, name);
	}
	
	
	// **************** Initialization ****************************************
	
	protected /* private-protected */ void initialize(Node parent) {
		super.initialize(parent);
		this.type = new ReferencedSimpleTypeDefinition(this, "anySimpleType", XMLConstants.W3C_XML_SCHEMA_NS_URI);
	}
	
	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.type);
	}
	
	
	// **************** MWAttributeDeclaration contract ***********************
	
	public MWSimpleTypeDefinition getType() {
		return this.type;
	}
	
	public String getDefaultValue() {
		return this.defaultValue;
	}
	
	public String getFixedValue() {
		return this.fixedValue;
	}
	
	public String getUse() {
		return this.use;
	}
	
	
	// **************** MWXpathableSchemaComponent contract *******************
	
	public int getMaxOccurs() {
		return 1;
	}
	
	public Iterator baseBuiltInTypes() {
		return this.type.baseBuiltInTypes();
	}
	
	
	// **************** MWSchemaContextComponent contract *********************
	
	public boolean hasType() {
		return true;
	}
	
	public String contextTypeQname() {
		return this.type.contextTypeQname();
	}
	
	public boolean containsText() {
		return false;
	}
	
	public boolean containsWildcard() {
		return false;
	}
	
	public int compareSchemaOrder(MWElementDeclaration element1, MWElementDeclaration element2) {
		// no elements in an attribute
		return 0;
	}
	
	
	// **************** MWNamedSchemaComponent contract ***********************
	
	public String componentTypeName() {
		return "attribute";
	}
	
	
	// **************** MWSchemaComponent contract ****************************
	
	/** Attribute declarations have no subcomponents. */
	public Iterator structuralComponents() {
		return NullIterator.instance();
	}
	
	
	// **************** SchemaModel contract **********************************
	
	protected void reloadInternal(XSObject xsAttrUse) {
		XSAttributeUseImpl attributeUseImpl = null;
		XSAttributeDecl attributeDecl = null;
		if (xsAttrUse instanceof XSAttributeUseImpl) {
			attributeUseImpl = (XSAttributeUseImpl)xsAttrUse;
			attributeDecl = (XSAttributeDecl)attributeUseImpl.getAttrDeclaration();
			super.reloadInternal(attributeDecl);
			this.reloadUse(attributeUseImpl);
		} else {
			attributeDecl = (XSAttributeDecl)xsAttrUse;
			super.reloadInternal(attributeDecl);
		}
				
		this.reloadType(attributeDecl);
		
		if (attributeDecl.getConstraintType() != XSConstants.VC_NONE) {
			if (attributeDecl.getConstraintType() == XSConstants.VC_DEFAULT) {
				this.defaultValue = attributeDecl.getConstraintValue();
			} else if (attributeDecl.getConstraintType() == XSConstants.VC_FIXED) {
				this.fixedValue = attributeDecl.getConstraintValue();
			}
		}
	}
	
	private void reloadType(XSAttributeDecl attributeDecl) {
		MWSimpleTypeDefinition oldType = this.type;
		XSSimpleTypeDecl simpleTypeDecl = (XSSimpleTypeDecl) attributeDecl.getTypeDefinition();
		this.type = MWSimpleTypeDefinition.Reloader.reloadedSimpleType(this, oldType, simpleTypeDecl);
		
		if (oldType != this.type) {
			this.getProject().nodeRemoved(oldType);
		}
	}
	
	private void reloadUse(XSAttributeUseImpl attributeUseImpl) {
		if (attributeUseImpl.getRequired()) {
			this.use = REQUIRED;
		}
		else {
			this.use = OPTIONAL;
		}
	}	
	
	public void resolveReferences() {
		super.resolveReferences();
		this.type.resolveReferences();
	}
	
	
	// **************** TopLink methods ***************************************
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(ExplicitAttributeDeclaration.class);
		descriptor.getInheritancePolicy().setParentClass(AbstractNamedSchemaComponent.class);
		
		descriptor.addDirectMapping("defaultValue", "default-value/text()");
		descriptor.addDirectMapping("fixedValue", "fixed-value/text()");
		
		ObjectTypeConverter useMappingConverter = new ObjectTypeConverter();
		useMappingConverter.addConversionValue(ExplicitAttributeDeclaration.OPTIONAL, ExplicitAttributeDeclaration.OPTIONAL);
		useMappingConverter.addConversionValue(ExplicitAttributeDeclaration.REQUIRED, ExplicitAttributeDeclaration.REQUIRED);
		useMappingConverter.addConversionValue(ExplicitAttributeDeclaration.PROHIBITED, ExplicitAttributeDeclaration.PROHIBITED);
		XMLDirectMapping useMapping = new XMLDirectMapping();
		useMapping.setAttributeName("use");
		useMapping.setXPath("use/text()");
		useMapping.setConverter(useMappingConverter);
		descriptor.addMapping(useMapping);
		
		XMLCompositeObjectMapping typeMapping = new XMLCompositeObjectMapping();
		typeMapping.setAttributeName("type");
		typeMapping.setXPath("type");
		typeMapping.setReferenceClass(AbstractSchemaComponent.class);
		descriptor.addMapping(typeMapping);

		return descriptor; 
	}
}
