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

import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.XMLConstants;

import org.apache.xerces.impl.xs.XSComplexTypeDecl;
import org.apache.xerces.impl.xs.XSElementDecl;
import org.apache.xerces.impl.xs.XSParticleDecl;
import org.apache.xerces.xs.XSConstants;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.xs.XSTypeDefinition;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.QName;
import org.eclipse.persistence.tools.workbench.utility.iterators.CompositeIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.NullIterator;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

public final class ExplicitElementDeclaration
	extends AbstractNamedSchemaComponent
	implements MWElementDeclaration
{
	// **************** Variables *********************************************
	
	private volatile MWSchemaTypeDefinition type;
	
	private volatile boolean nillable;
	
	/** 
	 * This is non-null only when nillable=true.
	 * Also, not persisted.  Built solely based on state of nillable.
	 */
	private transient volatile MWAttributeDeclaration nilAttribute;
	
	private volatile String defaultValue;
	
	private volatile String fixedValue;
	
	private Map identityConstraints;
	
	private volatile ReferencedElementDeclaration substitutionGroup;
	
	private volatile boolean abstractFlag;
	
	
	//
	//	These two variables are only used when the element is used as a particle
	//
	
	/** The minimum number of times the element occurs in a document */
	private volatile int minOccurs;
	
	/** The maximum number of times the element occurs in a document */
	private volatile int maxOccurs;
	
	
	// **************** Constructors ******************************************
	
	/** Toplink Use Only */
	private ExplicitElementDeclaration() {
		super();
	}
	
	ExplicitElementDeclaration(MWModel parent, String name) {
		super(parent, name);
	}
	
	
	// **************** Initialization ****************************************
	
	protected void initialize(Node parent) {
		super.initialize(parent);
		this.identityConstraints = new Hashtable();
	}
	
	protected void initialize() {
		super.initialize();
		this.minOccurs = 1;
		this.maxOccurs = 1;
	}
	
	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		
		if (this.type != null) {
			children.add(this.type);
		}
		
		synchronized (this.identityConstraints) { children.addAll(this.identityConstraints.values()); }
		
		if (this.substitutionGroup != null) {
			children.add(this.substitutionGroup);
		}
	}
	
	private MWAttributeDeclaration buildNilAttribute() {
		return new ReferencedAttributeDeclaration(this, "nil", XMLConstants.W3C_XML_SCHEMA_NS_URI);
	}
	
	
	// **************** MWElementDeclaration contract *************************
	
	public MWSchemaTypeDefinition getType() {
		return this.type;
	}
	
	public MWElementDeclaration getSubstitutionGroup() {
		return this.substitutionGroup;
	}
	
	public boolean isAbstract() {
		return this.abstractFlag;
	}
	
	public String getDefaultValue() {
		return this.defaultValue;
	}
	
	public String getFixedValue() {
		return this.fixedValue;
	}
	
	public boolean isNillable() {
		return this.nillable;
	}
	
	
	// **************** MWParticle contract ***********************************
	
	public int getMinOccurs() {
		return this.minOccurs;
	}
	
	public int getMaxOccurs() {
		return this.maxOccurs;
	}
	
	public boolean isDescriptorContextComponent() {
		return ! this.type.isReference();
	}
	
	public boolean isEquivalentTo(XSParticleDecl xsParticle) {
		if (xsParticle.getTerm() instanceof XSElementDecl) {
			XSElementDecl elementNode = (XSElementDecl) xsParticle.getTerm();
			return this.getName().equals(elementNode.getName());
		}
		else {
			return false;
		}
	}
	
	
	// **************** MWXpathableSchemaComponent contract *******************
	
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
		return this.type.containsText();
	}
	
	public boolean containsWildcard() {
		return this.type.containsWildcard();
	}
	
	public int compareSchemaOrder(MWElementDeclaration element1, MWElementDeclaration element2) {
		return this.type.compareSchemaOrder(element1, element2);
	}
	
	
	// **************** MWNamedSchemaComponent contract ***********************
	
	public String componentTypeName() {
		return "element";
	}
	
	public void addDirectlyOwnedComponentsTo(Collection directlyOwnedComponents) {
		this.type.addDirectlyOwnedComponentsTo(directlyOwnedComponents);
	}
	
	
	// **************** MWSchemaModel contract ********************************
	
	public Iterator structuralComponents() {
		return this.type.structuralComponents();
	}
	
	public Iterator descriptorContextComponents() {
		if (! this.type.isReference()) {
			return this.type.descriptorContextComponents();
		}
		else {
			return NullIterator.instance();
		}
	}
	
	public Iterator xpathComponents() {
		Iterator typeXpathComponents = this.type.xpathComponents();
		
		if (this.nilAttribute == null) {
			return typeXpathComponents;
		}
		else {
			return new CompositeIterator(this.nilAttribute, this.type.xpathComponents());
		}
	}
	
	public MWNamedSchemaComponent nestedNamedComponent(QName qName) {
		return this.type.nestedNamedComponent(qName);
	}
	
	public MWAttributeDeclaration nestedAttribute(String namespaceUrl, String attributeName) {
		if (this.nilAttribute != null 
			&& namespaceUrl.equals(this.nilAttribute.getNamespaceUrl())
			&& attributeName.equals(this.nilAttribute.getName())
		) {
			return this.nilAttribute;
		}
		else {
			return this.type.nestedAttribute(namespaceUrl, attributeName);
		}
	}
	
	public MWElementDeclaration nestedElement(String namespaceUrl, String elementName) {
		return this.type.nestedElement(namespaceUrl, elementName);
	}
	
	public int totalElementCount() {
		return 1;
	}
	
	
	// **************** SchemaModel contract  *********************************
	
	protected void reloadInternal(XSObject xsObject) {
		
		XSElementDecl elemenDecl = null;
		if (xsObject instanceof XSParticleDecl) {
			elemenDecl = (XSElementDecl)((XSParticleDecl)xsObject).getTerm();
			super.reloadInternal(elemenDecl);
		} else {
			elemenDecl = (XSElementDecl)xsObject;
			super.reloadInternal(xsObject);
		}
		
		
		
		this.reloadType(elemenDecl);
		this.reloadNillable(elemenDecl);
		
		if (elemenDecl.getConstraintType() != XSConstants.VC_NONE) {
			if (elemenDecl.getConstraintType() == XSConstants.VC_DEFAULT) {
				this.defaultValue = elemenDecl.getConstraintValue();
			} else if (elemenDecl.getConstraintType() == XSConstants.VC_FIXED) {
				this.fixedValue = elemenDecl.getConstraintValue();
			}
		}
		this.abstractFlag = elemenDecl.getAbstract();
		
		if (xsObject instanceof XSParticleDecl) {
			this.minOccurs = ((XSParticleDecl)xsObject).getMinOccurs();
			this.maxOccurs = ((XSParticleDecl)xsObject).getMaxOccurs();
			if (((XSParticleDecl)xsObject).getMaxOccursUnbounded()) {
				this.maxOccurs = MWXmlSchema.INFINITY;
			}
		}
		
		this.reloadSubstitutionGroup(elemenDecl);
		
		this.reloadIdentityConstraints(elemenDecl);
	}
	
	private void reloadType(XSElementDecl elementDecl) {
		MWSchemaTypeDefinition oldType = this.type;
		XSTypeDefinition typeDef = elementDecl.getTypeDefinition();
		String typeName = typeDef.getName();
		String typeNamespace = typeDef.getNamespace();	
		
		// This is a short circuit to avoid infinitely nested elements.
		// Basically, if an element contains itself, we snip the chain at three nested elements.
		if (! "".equals(this.getName())
			&& this.parentNamedComponent() instanceof ExplicitElementDeclaration
			&& this.getName().equals(this.parentNamedComponent().getName())
			&& this.parentNamedComponent().parentNamedComponent() instanceof ExplicitElementDeclaration
			&& this.getName().equals(this.parentNamedComponent().parentNamedComponent().getName())
		) {
			this.type = ReferencedSchemaTypeDefinition.urType(this);
		}
		// And from here, we reload the type normally ...
		else if (typeName == null && ! (typeDef.getTypeCategory() == XSTypeDefinition.COMPLEX_TYPE && ((XSComplexTypeDecl) typeDef).getBaseType() == null)) {
			// *** Type is unnamed and based off of another type - create an explicit type ***
			this.type = ExplicitSchemaTypeDefinition.reloadedExplicitType(this, this.type, elementDecl);
		}
		else {
			// *** Type is named or unnamed with no base type (ur type) - create a referenced type ***
			this.type = ReferencedSchemaTypeDefinition.reloadedReferencedType(this, oldType,
																		  	  typeName, typeNamespace,
																			  typeDef);
		}
		
		if (oldType != this.type && oldType != null) {
			this.getProject().nodeRemoved(oldType);
		}
	}
	
	private void reloadNillable(XSElementDecl elemenDecl) {
		this.nillable = elemenDecl.getNillable();
		if (this.nillable) {
			this.nilAttribute = this.buildNilAttribute();
		}
		else {
			this.nilAttribute = null;
		}
	}
	
	private void reloadSubstitutionGroup(XSElementDecl elementDecl) {
		if (elementDecl.getSubstitutionGroupAffiliation() != null) {
			String substitutionGroupName = elementDecl.getSubstitutionGroupAffiliation().getName();
			String substitutionGroupNamespace = elementDecl.getSubstitutionGroupAffiliation().getNamespace();
			this.substitutionGroup = new ReferencedElementDeclaration(this, substitutionGroupName, substitutionGroupNamespace);
		}
		else {
			this.substitutionGroup = null;
		}
	}
	
	private void reloadIdentityConstraints(XSElementDecl elementDecl) {
		org.apache.xerces.impl.xs.identity.IdentityConstraint[] identityNodes = elementDecl.getIDConstraints();
		for (int i = 0; identityNodes != null && i < identityNodes.length; i ++ ) {
			org.apache.xerces.impl.xs.identity.IdentityConstraint identityNode = identityNodes[i];
			String identityName = identityNode.getName();
			IdentityConstraintDefinition identityConstraint = (IdentityConstraintDefinition) this.identityConstraints.get(identityName);
			
			if (identityConstraint == null) {
				identityConstraint = new IdentityConstraintDefinition(this, identityName);
				this.identityConstraints.put(identityName, identityConstraint);
			}
			
			identityConstraint.reload(identityNode);
		}
	}
	
	public void resolveReferences() {
		super.resolveReferences();
		
		this.type.resolveReferences();
		
		if (this.nilAttribute != null) {
			this.nilAttribute.resolveReferences();
		}
		
		if (this.substitutionGroup != null) {
			this.substitutionGroup.resolveReferences();
		}
	}
	
	
	//*********************************** Toplink persistence use ****************
	
	private Map getIdentityConstraintsForToplink() {
		return new TreeMap(this.identityConstraints);
	}
	
	private void setIdentityConstraintsForToplink(Map map) {
		Iterator iter = map.values().iterator();
		Map elementMap = new Hashtable();
		while(iter.hasNext()) {
			IdentityConstraintDefinition icd = (IdentityConstraintDefinition)iter.next();
			elementMap.put(icd.getName(), icd);
		}
		this.identityConstraints = elementMap;
	}
	
	
	// **************** TopLink methods ***************************************
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(ExplicitElementDeclaration.class);
		descriptor.getInheritancePolicy().setParentClass(AbstractNamedSchemaComponent.class);
		
		XMLCompositeObjectMapping typeMapping = new XMLCompositeObjectMapping();
		typeMapping.setAttributeName("type");
		typeMapping.setReferenceClass(AbstractSchemaComponent.class);
		typeMapping.setXPath("type");
		descriptor.addMapping(typeMapping);
		
		XMLDirectMapping nillableMapping = 
			(XMLDirectMapping) descriptor.addDirectMapping("nillable", "nillable/text()");
		nillableMapping.setGetMethodName("getNillableForTopLink");
		nillableMapping.setSetMethodName("setNillableForTopLink");
		nillableMapping.setNullValue(Boolean.FALSE);
		
		descriptor.addDirectMapping("defaultValue", "default-value/text()");
		descriptor.addDirectMapping("fixedValue", "fixed-value/text()");
		
		XMLDirectMapping abstractMapping = 
			(XMLDirectMapping) descriptor.addDirectMapping("abstractFlag", "abstract/text()");
		abstractMapping.setNullValue(Boolean.FALSE);
		
		((XMLDirectMapping) descriptor.addDirectMapping("minOccurs", "min-occurs/text()")).setNullValue(new Integer(1));
		((XMLDirectMapping) descriptor.addDirectMapping("maxOccurs", "max-occurs/text()")).setNullValue(new Integer(1));
		
		XMLCompositeObjectMapping substitutionGroupMapping = new XMLCompositeObjectMapping();
		substitutionGroupMapping.setAttributeName("substitutionGroup");
		substitutionGroupMapping.setReferenceClass(ReferencedElementDeclaration.class);
		substitutionGroupMapping.setXPath("substitution-group");
		descriptor.addMapping(substitutionGroupMapping);
		
		XMLCompositeCollectionMapping indentityConstraintsMapping = new XMLCompositeCollectionMapping();
		indentityConstraintsMapping.setAttributeName("identityConstraints");
		indentityConstraintsMapping.setGetMethodName("getIdentityConstraintsForToplink");
		indentityConstraintsMapping.setSetMethodName("setIdentityConstraintsForToplink");
		indentityConstraintsMapping.setXPath("identity-constraints/identity-constraint");
		indentityConstraintsMapping.setReferenceClass(IdentityConstraintDefinition.class);
		indentityConstraintsMapping.useMapClass(TreeMap.class, "getName");
		descriptor.addMapping(indentityConstraintsMapping);
		
		return descriptor;
	}
	
	private boolean getNillableForTopLink() {
		return this.nillable;
	}
	
	private void setNillableForTopLink(boolean newValue) {
		this.nillable = newValue;
		
		if (newValue) {
			this.nilAttribute = this.buildNilAttribute();
		}
	}
}
