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
package org.eclipse.persistence.tools.workbench.mappingsmodel.schema;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import javax.xml.XMLConstants;

import org.apache.xerces.impl.dv.xs.XSSimpleTypeDecl;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.xs.XSTypeDefinition;
import org.eclipse.persistence.mappings.converters.ObjectTypeConverter;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.utility.iterators.CompositeIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.NullIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.SingleElementIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

public final class ExplicitSimpleTypeDefinition
	extends ExplicitSchemaTypeDefinition
	implements MWSimpleTypeDefinition
{
	// **************** Variables *********************************************
	
	/** The variety of this simple type: one of ATOMIC, LIST, or UNION */
	private volatile String variety;
	
	/** If this simple type is a restriction of another type, this is that type */
	private volatile ReferencedSimpleTypeDefinition baseType;
	
	/** If this simple type is a list of items of another type, this is that type */
	private volatile MWSimpleTypeDefinition itemType;
	
	/** If this simple type is a union of types, these are those types */
	private volatile List<MWSimpleTypeDefinition> memberTypes;
	
	
	// **************** Constructors ******************************************
	
	/** Toplink use only */
	protected ExplicitSimpleTypeDefinition() {
		super();
	}
	
	ExplicitSimpleTypeDefinition(MWModel parent, String name) {
		super(parent, name);
	}
	
	ExplicitSimpleTypeDefinition(MWModel parent, String name, String namespace) {
		super(parent, name, namespace);
	}
	
	ExplicitSimpleTypeDefinition(MWModel parent, String name, String namespace, boolean builtIn) {
		super(parent, name, namespace, builtIn);
	}
	
	
	// **************** Initialization ****************************************
	
	protected void initialize(Node parent) {
		super.initialize(parent);
		this.variety = ATOMIC;
		this.baseType = ReferencedSimpleTypeDefinition.simpleUrType(this);
		this.memberTypes = new Vector();
	}
	
	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.baseType);
		
		if (this.itemType != null) {
			children.add(this.itemType);
		}
		
		children.addAll(this.memberTypes);
	}
	
	
	// **************** MWSimpleTypeDefinition contract ***********************
	
	public String getVariety() {
		return this.variety;
	}
	
	public boolean isAtomic() {
		return this.variety == ATOMIC;
	}
	
	public boolean isList() {
		return this.variety == LIST;
	}
	
	public boolean isUnion() {
		return this.variety == UNION;
	}
	
	public MWSimpleTypeDefinition getItemType() {
		return this.itemType;
	}
	
	public ListIterator memberTypes() {
		return this.memberTypes.listIterator();
	}
	
	
	// **************** MWSchemaTypeDefinition contract ***********************
	
	public MWSchemaTypeDefinition getBaseType() {
		return this.baseType;
	}
	
	public boolean isComplex() {
		return false;
	}
	
	public Iterator baseBuiltInTypes() {
		if (this.builtIn) {
			return new SingleElementIterator(this);
		}
		else if (this.isList()) {
			return this.itemType.baseBuiltInTypes();
		}
		else if (this.isUnion()) {
			return new CompositeIterator(this.memberTypeBaseBuiltInTypes());
		}
		else /* this.isAtomic() */ {
			return this.baseType.baseBuiltInTypes();
		}
	}
	
	private Iterator memberTypeBaseBuiltInTypes() {
		return new TransformationIterator(this.memberTypes.iterator()) {
			protected Object transform(Object next) {
				return ((MWSimpleTypeDefinition) next).baseBuiltInTypes();
			}
		};
	}
	
	
	// **************** MWSchemaContextComponent contract *********************
	
	public boolean containsText() {
		return true;
	}
	
	public boolean containsWildcard() {
		return false;
	}
	
	public int compareSchemaOrder(MWElementDeclaration element1, MWElementDeclaration element2) {
		// no elements in a simple type
		return 0;
	}
	
	
	// **************** MWNamedSchemaComponent contract ***********************
	
	public String componentTypeName() {
		return "simpleType";
	}
	
	
	// **************** MWSchemaComponent contract ****************************
	
	/**
	 * Simple types have no structural subcomponents.
	 */
	public Iterator structuralComponents() {
		return NullIterator.instance();
	}
	
	
	// **************** SchemaModel contract **********************************
	
	protected void reloadInternal(XSObject xsObject) {
		super.reloadInternal(xsObject);
		
		XSSimpleTypeDecl simpleTypeNode = (XSSimpleTypeDecl) xsObject;
		this.reloadVariety(simpleTypeNode);
		if (getNamespaceUrl() != null) {
			this.builtIn = (getNamespaceUrl().equals(XMLConstants.W3C_XML_SCHEMA_NS_URI) || getNamespaceUrl().equals(XMLConstants.W3C_XPATH_DATATYPE_NS_URI));
		}
	}
	
	private void reloadVariety(XSSimpleTypeDecl xsSimpleTypeDecl) {
		if (xsSimpleTypeDecl.getVariety() == XSSimpleTypeDefinition.VARIETY_LIST) {
			this.variety = LIST;
		}
		else if (xsSimpleTypeDecl.getVariety() == XSSimpleTypeDefinition.VARIETY_UNION) {
			this.variety = UNION;
		}
		else if (xsSimpleTypeDecl.getVariety() == XSSimpleTypeDefinition.VARIETY_ATOMIC) {
			this.variety = ATOMIC;
		}
		else if (xsSimpleTypeDecl.getVariety() == XSSimpleTypeDefinition.VARIETY_ABSENT){
			this.variety = ABSENT;
		} else {
			throw new IllegalArgumentException();
		}
		
		this.reloadBaseType(xsSimpleTypeDecl);
		this.reloadItemType(xsSimpleTypeDecl);
		this.reloadMemberTypes(xsSimpleTypeDecl);
	}
	
	private void reloadBaseType(XSSimpleTypeDecl xsSimpleTypeDecl) {
		XSTypeDefinition baseTypeNode = xsSimpleTypeDecl.getBaseType();
		
		if (baseTypeNode != null) {
			if (this.baseType == null) {
				this.baseType = new ReferencedSimpleTypeDefinition(this, baseTypeNode.getName(), baseTypeNode.getNamespace());
			}
			
			this.baseType.reload(baseTypeNode);
		}
		else {
			this.baseType = ReferencedSimpleTypeDefinition.simpleUrType(this);
		}
	}
	
	private void reloadItemType(XSSimpleTypeDecl xsSimpleTypeDecl) {
		MWSimpleTypeDefinition oldItemType = this.itemType;
		XSSimpleTypeDefinition itemTypeNode = xsSimpleTypeDecl.getItemType();
		
		if (this.isList()) {
			// itemTypeNode will not be null if this is a list
			this.itemType = 
				MWSimpleTypeDefinition.Reloader.reloadedSimpleType(this, oldItemType, itemTypeNode);
		}
		else {
			this.itemType = null;
		}
		
		if (oldItemType != null && oldItemType != this.itemType) {
			this.getProject().nodeRemoved(oldItemType);
		}
	}
	
	private void reloadMemberTypes(XSSimpleTypeDecl xsSimpleTypeDecl) {
		if (this.isUnion()) {
			// vector will not be empty if this is a union
			ListIterator<MWSimpleTypeDefinition> memberTypes = this.memberTypes.listIterator();
			ListIterator<XSObject> memberTypeNodes = XercesTools.listIteratorFromXSObjectList(xsSimpleTypeDecl.getMemberTypes());
			
			// refresh member types that have corresponding nodes
			while (memberTypes.hasNext() && memberTypeNodes.hasNext() ) {
				MWSimpleTypeDefinition oldMemberType = (MWSimpleTypeDefinition) memberTypes.next();
				XSSimpleTypeDefinition memberTypeNode = (XSSimpleTypeDefinition) memberTypeNodes.next();
				
				MWSimpleTypeDefinition newMemberType =
					MWSimpleTypeDefinition.Reloader.reloadedSimpleType(this, oldMemberType, memberTypeNode);
				
				if (oldMemberType != newMemberType) {
					memberTypes.set(newMemberType);
					this.getProject().nodeRemoved(oldMemberType);
				}
			}
			
			// add member types for new nodes
			while (memberTypeNodes.hasNext()) {
				XSSimpleTypeDefinition memberTypeNode = (XSSimpleTypeDefinition) memberTypeNodes.next();
				MWSimpleTypeDefinition newMemberType = 
					MWSimpleTypeDefinition.Reloader.reloadedSimpleType(this, null, memberTypeNode);
				
				memberTypes.add(newMemberType);
			}
			
			// remove member types for those with no corresponding nodes
			while (memberTypes.hasNext()) {
				MWSimpleTypeDefinition oldMemberType = (MWSimpleTypeDefinition) memberTypes.next();
				memberTypes.remove();
				this.getProject().nodeRemoved(oldMemberType);
			}
		}
		else {
			for (Iterator stream = this.memberTypes.iterator(); stream.hasNext(); ) {
				MWSimpleTypeDefinition oldMemberType = (MWSimpleTypeDefinition) stream.next();
				stream.remove();
				this.getProject().nodeRemoved(oldMemberType);
			}
		}
	}
	
	public void resolveReferences() {
		super.resolveReferences();
		
		this.baseType.resolveReferences();
		
		if (this.itemType != null) {
			this.itemType.resolveReferences();
		}
		
		for (Iterator stream = this.memberTypes.iterator(); stream.hasNext(); ) {
			((MWSimpleTypeDefinition) stream.next()).resolveReferences();
		}
	}
	
	
	// **************** TopLink methods ***************************************
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(ExplicitSimpleTypeDefinition.class);
		
		descriptor.getInheritancePolicy().setParentClass(ExplicitSchemaTypeDefinition.class);
		
		XMLDirectMapping varietyMapping = (XMLDirectMapping) descriptor.addDirectMapping("variety", "variety/text()");
		ObjectTypeConverter varietyConverter = new ObjectTypeConverter();
		// the first one here is to ensure that if the xml value is null, that the object value is ATOMIC
		// we don't want a "null value", as that will write null out to the XML if the object value is ATOMIC
		varietyConverter.addConversionValue(null, ATOMIC);
		varietyConverter.addConversionValue(ATOMIC, ATOMIC);
		varietyConverter.addConversionValue(LIST, LIST);
		varietyConverter.addConversionValue(UNION, UNION);
		varietyMapping.setConverter(varietyConverter);
		
		XMLCompositeObjectMapping baseTypeMapping = new XMLCompositeObjectMapping();
		baseTypeMapping.setAttributeName("baseType");
		baseTypeMapping.setReferenceClass(ReferencedSimpleTypeDefinition.class);
		baseTypeMapping.setXPath("base-type");
		descriptor.addMapping(baseTypeMapping);
		
		XMLCompositeObjectMapping itemTypeMapping = new XMLCompositeObjectMapping();
		itemTypeMapping.setAttributeName("itemType");
		itemTypeMapping.setReferenceClass(ReferencedSimpleTypeDefinition.class);
		itemTypeMapping.setXPath("item-type");
		descriptor.addMapping(itemTypeMapping);
		
		XMLCompositeCollectionMapping memberTypesMapping = new XMLCompositeCollectionMapping();
		memberTypesMapping.setAttributeName("memberTypes");
		memberTypesMapping.setReferenceClass(ReferencedSimpleTypeDefinition.class);
		memberTypesMapping.setXPath("member-types");
		descriptor.addMapping(memberTypesMapping);
		
		return descriptor;
	}
}
