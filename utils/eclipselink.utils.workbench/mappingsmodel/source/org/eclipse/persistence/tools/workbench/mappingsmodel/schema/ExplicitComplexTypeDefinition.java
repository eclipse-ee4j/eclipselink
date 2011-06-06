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
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.xerces.impl.xs.XSAttributeDecl;
import org.apache.xerces.impl.xs.XSAttributeUseImpl;
import org.apache.xerces.impl.xs.XSComplexTypeDecl;
import org.apache.xerces.xs.XSConstants;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSTypeDefinition;
import org.eclipse.persistence.mappings.converters.ObjectTypeConverter;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.CompositeIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.NullIterator;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

public final class ExplicitComplexTypeDefinition
	extends ExplicitSchemaTypeDefinition
	implements MWComplexTypeDefinition
{
	/** The type from which this type is derived.                                       *
	 *  If not derived from an explicit type, it is derived from the ur-type or "anyType". */
	private volatile ReferencedSchemaTypeDefinition baseType;
	
	/** The complex type is derived from the base type by restriction or extension. */
	private volatile String derivationMethod;
	
	/** Abstract types are useful primarily as derivation points for other types. */
	private volatile boolean abstractFlag;
	
	/** We have no support for attribute groups from the XDK, so all attributes just get plopped here. *
	 *  Key -> name; Value -> MWSchemaAttribute 													   */
	private Map attributes;
	
	/** Content may be empty, simple, complex: element only, or complex: mixed */
	private volatile Content content;
	
	
	// **************** Constructors ******************************************
	
	/** toplink use only */
	protected ExplicitComplexTypeDefinition() {
		super();
	}	
	
	ExplicitComplexTypeDefinition(MWModel parent, String name) {
		super(parent, name);
	}
	
	ExplicitComplexTypeDefinition(MWModel parent, String name, String namespace) {
		super(parent, name, namespace);
	}
	
	ExplicitComplexTypeDefinition(MWModel parent, String name, String namespace, boolean builtIn) {
		super(parent, name, namespace, builtIn);
	}
	
	
	// **************** Initialization ****************************************
	
	protected void initialize() {
		super.initialize();
		this.attributes = new Hashtable();
	}
	
	protected void initialize(Node parent) {
		super.initialize(parent);
		this.baseType = ReferencedSchemaTypeDefinition.urType(this);
		this.content = new EmptyContent(this);
	}
	
	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.baseType);
		synchronized (this.attributes) { children.addAll(this.attributes.values()); }
		
		if (this.content != null) {
			children.add(this.content);
		}
	}
	
	
	// **************** Public ************************************************
	
	public MWAttributeDeclaration attribute(String namespaceUrl, String name) {
		for (Iterator stream = this.attributes(); stream.hasNext(); ) {
			MWAttributeDeclaration attribute = (MWAttributeDeclaration) stream.next();
			
			if (namespaceUrl.equals(attribute.getNamespaceUrl())
				&& name.equals(attribute.getName())) {
					return attribute;
			}
		}
		
		return null;
	}
	
	
	// **************** Attributes ********************************************
	
	Iterator attributes() {
		return this.attributes.values().iterator();
	}
	
	private Iterator attributeNames() {
		return this.attributes.keySet().iterator();
	}
	
	private void addAttribute(String attributeName, MWAttributeDeclaration attribute) {
		this.attributes.put(attributeName, attribute);
	}
	
	private ReferencedAttributeDeclaration addReferencedAttribute(String attributeName, String attributeNamespace) {
		ReferencedAttributeDeclaration attributeRef = new ReferencedAttributeDeclaration(this, attributeName, attributeNamespace);
		this.addAttribute(attributeName, attributeRef);
		return attributeRef;
	}
	
	private ExplicitAttributeDeclaration addExplicitAttribute(String attributeName) {
		ExplicitAttributeDeclaration attributeDef = new ExplicitAttributeDeclaration(this, attributeName);
		this.addAttribute(attributeName, attributeDef);
		return attributeDef;
	}
	
	private void removeAttribute(String attributeName) {
		MWAttributeDeclaration attribute = (MWAttributeDeclaration) this.attributes.remove(attributeName);
		this.getProject().nodeRemoved(attribute);
	}
	
	
	// **************** MWComplexTypeDefinition contract **********************
	
	public String getDerivationMethod() {
		return this.derivationMethod;
	}
	
	public boolean isAbstract() {
		return this.abstractFlag;
	}
	
	public int totalElementCount() {
		return this.content.totalElementCount();
	}
	
	public int attributeCount() {
		return this.attributes.size();
	}
	
	
	// **************** MWSchemaTypeDefinition contract ***********************
	
	public MWSchemaTypeDefinition getBaseType() {
		return this.baseType;
	}
	
	public boolean isComplex() {
		return true;
	}
	
	public Iterator baseBuiltInTypes() {
		if (this.builtIn) {
			return NullIterator.instance();
		}
		else {
			return this.baseType.baseBuiltInTypes();
		}
	}
	
	
	// **************** MWSchemaContextComponent contract *********************
	
	public boolean containsText() {
		return this.content.hasTextContent();
	}
	
	public boolean containsWildcard() {
		return this.content.containsWildcard();
	}
	
	public int compareSchemaOrder(MWElementDeclaration element1, MWElementDeclaration element2) {
		if (element1.isDescendantOf(this.baseType)) {
			if (element2.isDescendantOf(this.baseType)) {
				return this.baseType.compareSchemaOrder(element1, element2);	
			}
			else {
				return -1;
			}
		}
		else if (element2.isDescendantOf(this.baseType)) {
			return +1;
		}
		else {
			return this.content.compareSchemaOrder(element1, element2);
		}
	}
	
	
	// **************** MWNamedSchemaComponent contract ***********************
	
	public String componentTypeName() {
		return "complexType";
	}
	
	public void addDirectlyOwnedComponentsTo(Collection directlyOwnedComponents) {
		directlyOwnedComponents.addAll(this.attributes.values());
		this.content.addDirectlyOwnedComponentsTo(directlyOwnedComponents);
	}
	
	
	// **************** MWSchemaModel contract ********************************
	
	public Iterator structuralComponents() {
		return new CompositeIterator(this.content.structuralComponents(), this.attributes());
	}
	
	public Iterator descriptorContextComponents() {
		return this.content.descriptorContextComponents();
	}
	
	public Iterator xpathComponents() {
		return new CompositeIterator(this.attributes(), this.content.xpathComponents());
	}
	
	public MWAttributeDeclaration nestedAttribute(String namespaceUrl, String attributeName) {
		return this.attribute(namespaceUrl, attributeName);
	}
	
	public MWElementDeclaration nestedElement(String namespaceUrl, String elementName) {
		return this.content.nestedElement(namespaceUrl, elementName);
	}
	
	protected void reloadInternal(XSObject xsObject) {
		super.reloadInternal(xsObject);
		
		XSComplexTypeDecl complexTypeDecl = (XSComplexTypeDecl) xsObject;
		
		this.reloadBaseType(complexTypeDecl.getBaseType());
		this.abstractFlag = complexTypeDecl.getAbstract();
		
		// derivation method
		if (complexTypeDecl.getDerivationMethod() == XSConstants.DERIVATION_RESTRICTION) {
			this.derivationMethod = RESTRICTION;
		}
		else {
			this.derivationMethod = EXTENSION;
		}
		
		this.reloadContent(complexTypeDecl);
		this.reloadAttributes(complexTypeDecl.getAttributeUses());
	}
	
	private void reloadBaseType(XSTypeDefinition complexTypeDecl) {
		ReferencedSchemaTypeDefinition oldBaseType = this.baseType;
		String baseTypeName = null;
		String baseTypeNamespace = null;
		
		baseTypeName = complexTypeDecl.getName();
		baseTypeNamespace = complexTypeDecl.getNamespace();
		
		this.baseType = ReferencedSchemaTypeDefinition.reloadedReferencedType(this, oldBaseType, baseTypeName, baseTypeNamespace, complexTypeDecl);
		
		if (oldBaseType != this.baseType && oldBaseType != null) {
			this.getProject().nodeRemoved(oldBaseType);
		}
	}
	
	private void reloadContent(XSComplexTypeDecl complexTypeDecl) {
		Content oldContent = this.content;
		this.content = Content.reloadedContent(this, this.content, complexTypeDecl);
		
		if (oldContent != this.content && oldContent != null) {
			this.getProject().nodeRemoved(oldContent);
		}
	}
	
	private void reloadAttributes(XSObjectList complexTypeAttributes) {
		Collection removedAttributeNames = CollectionTools.collection(this.attributeNames());
		
		for (int i = complexTypeAttributes.getLength() - 1; i >= 0; i --) {
			MWAttributeDeclaration attribute = this.reloadAttribute((XSAttributeUseImpl)complexTypeAttributes.item(i));
			removedAttributeNames.remove(attribute.getName());
		}
		
		for (Iterator stream = removedAttributeNames.iterator(); stream.hasNext(); ) {
			this.removeAttribute((String) stream.next());
		}
	}
	
	private MWAttributeDeclaration reloadAttribute(XSAttributeUseImpl attributeUse) {
		String attributeName = attributeUse.getAttrDeclaration().getName();
		MWAttributeDeclaration attribute = (MWAttributeDeclaration) this.attributes.get(attributeName);
		
		if (attributeUse.getAttrDeclaration().getScope() == XSAttributeDecl.SCOPE_GLOBAL) {
			 //attribute is actually a ref to a global attribute
			if (attribute == null || ! attribute.isReference()) {
				attribute = this.addReferencedAttribute(attributeName, attributeUse.getAttrDeclaration().getNamespace());
			}
		}
		else {
			// attribute is defined locally
			if (attribute == null || attribute.isReference()) {
				attribute = this.addExplicitAttribute(attributeName);
			}	
		}
		if (attribute.isReference()) {
			attribute.reload(attributeUse.getAttrDeclaration());
		} else {
			attribute.reload(attributeUse);
		}
		return attribute;
	}
	
	public void resolveReferences() {
		super.resolveReferences();
		
		this.baseType.resolveReferences();
		this.content.resolveReferences();
		
		for (Iterator stream = this.attributes.values().iterator(); stream.hasNext(); ) {
			((MWAttributeDeclaration) stream.next()).resolveReferences();
		}
	}
	
	
	// **************** Toplink methods ***************************************
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(ExplicitComplexTypeDefinition.class);
		
		descriptor.getInheritancePolicy().setParentClass(ExplicitSchemaTypeDefinition.class);
		
		descriptor.addDirectMapping("abstractFlag", "abstract/text()");
		
		ObjectTypeConverter derivationMethodConverter = new ObjectTypeConverter();
		derivationMethodConverter.addConversionValue(ExplicitComplexTypeDefinition.RESTRICTION, ExplicitComplexTypeDefinition.RESTRICTION);
		derivationMethodConverter.addConversionValue(ExplicitComplexTypeDefinition.EXTENSION, ExplicitComplexTypeDefinition.EXTENSION);
		XMLDirectMapping derivationMethodMapping = new XMLDirectMapping();
		derivationMethodMapping.setAttributeName("derivationMethod");
		derivationMethodMapping.setXPath("derivation-method/text()");
		derivationMethodMapping.setConverter(derivationMethodConverter);
		descriptor.addMapping(derivationMethodMapping);
		
		XMLCompositeObjectMapping baseTypeMapping = new XMLCompositeObjectMapping();
		baseTypeMapping.setAttributeName("baseType");
		baseTypeMapping.setReferenceClass(ReferencedSchemaTypeDefinition.class);
		baseTypeMapping.setXPath("base-type");
		descriptor.addMapping(baseTypeMapping);
		
		XMLCompositeCollectionMapping attributeDeclarationsMapping = new XMLCompositeCollectionMapping();
		attributeDeclarationsMapping.setAttributeName("attributes");
		attributeDeclarationsMapping.setGetMethodName("getAttributesForToplink");
		attributeDeclarationsMapping.setSetMethodName("setAttributesForToplink");
		attributeDeclarationsMapping.setXPath("attributes/attribute");
		attributeDeclarationsMapping.setReferenceClass(AbstractNamedSchemaComponent.class);
		attributeDeclarationsMapping.useMapClass(TreeMap.class, "getName");
		descriptor.addMapping(attributeDeclarationsMapping);
		
		XMLCompositeObjectMapping contentMapping = new XMLCompositeObjectMapping();
		contentMapping.setAttributeName("content");
		contentMapping.setXPath("content");
		contentMapping.setReferenceClass(Content.class);
		descriptor.addMapping(contentMapping);
		
		return descriptor;
	}
	
	private Map getAttributesForToplink() {
		return new TreeMap(attributes);
	}
	
	private void setAttributesForToplink(Map map) {
		Map elementMap = new HashMap();
		
		for (Iterator iter = map.values().iterator(); iter.hasNext(); ) {
			AbstractNamedSchemaComponent element = (AbstractNamedSchemaComponent)iter.next();
			elementMap.put(element.getName(), element);
		}
		
		this.attributes = elementMap;
	}
}
