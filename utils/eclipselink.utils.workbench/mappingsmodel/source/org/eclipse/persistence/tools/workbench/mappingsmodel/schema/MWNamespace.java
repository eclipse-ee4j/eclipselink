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

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.XMLConstants;

import org.apache.xerces.impl.dv.xs.XSSimpleTypeDecl;
import org.apache.xerces.impl.xs.XSAttributeDecl;
import org.apache.xerces.impl.xs.XSComplexTypeDecl;
import org.apache.xerces.impl.xs.XSElementDecl;
import org.apache.xerces.impl.xs.opti.SchemaDOM;
import org.apache.xerces.xs.XSConstants;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSModelGroupDefinition;
import org.apache.xerces.xs.XSNamedMap;
import org.apache.xerces.xs.XSTypeDefinition;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.xml.MWXmlProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.SchemaChange;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.HashBag;
import org.eclipse.persistence.tools.workbench.utility.iterators.CompositeIterator;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

public final class MWNamespace 
	extends MWModel
{
	// **************** Static variables **************************************
	
	public static final java.util.Comparator COMPARATOR = new NamespaceComparator();
		
	// **************** Static methods ****************************************
	
	static MWNamespace xsdNamespace(MWXmlSchema schema) {
		MWNamespace xsdNamespace = new MWNamespace(schema);
		xsdNamespace.namespaceUrl = XMLConstants.W3C_XML_SCHEMA_NS_URI;
		xsdNamespace.namespacePrefix = "xsd";
		
		// ur-types
		xsdNamespace.addBuiltInComplexType("anyType");
		xsdNamespace.addBuiltInSimpleType("anySimpleType");
		
		// time types
		xsdNamespace.addBuiltInSimpleType("duration");
		xsdNamespace.addBuiltInSimpleType("date");
		xsdNamespace.addBuiltInSimpleType("time");
		xsdNamespace.addBuiltInSimpleType("dateTime");
		xsdNamespace.addBuiltInSimpleType("gDay");
		xsdNamespace.addBuiltInSimpleType("gMonth");
		xsdNamespace.addBuiltInSimpleType("gYear");
		xsdNamespace.addBuiltInSimpleType("gMonthDay");
		xsdNamespace.addBuiltInSimpleType("gYearMonth");
		
		// string types
		xsdNamespace.addBuiltInSimpleType("string");
		xsdNamespace.addBuiltInSimpleType("normalizedString");
		xsdNamespace.addBuiltInSimpleType("token");
		xsdNamespace.addBuiltInSimpleType("language");
		xsdNamespace.addBuiltInSimpleType("Name");
		xsdNamespace.addBuiltInSimpleType("NMTOKEN");
		xsdNamespace.addBuiltInSimpleType("NMTOKENS");
		xsdNamespace.addBuiltInSimpleType("NCName");
		xsdNamespace.addBuiltInSimpleType("ID");
		xsdNamespace.addBuiltInSimpleType("IDREF");
		xsdNamespace.addBuiltInSimpleType("IDREFS");
		xsdNamespace.addBuiltInSimpleType("ENTITY");
		xsdNamespace.addBuiltInSimpleType("ENTITIES");
		
		// mathematical types
		xsdNamespace.addBuiltInSimpleType("boolean");
		xsdNamespace.addBuiltInSimpleType("base64Binary");
		xsdNamespace.addBuiltInSimpleType("hexBinary");
		xsdNamespace.addBuiltInSimpleType("float");
		xsdNamespace.addBuiltInSimpleType("double");
		xsdNamespace.addBuiltInSimpleType("decimal");
		xsdNamespace.addBuiltInSimpleType("integer");
		xsdNamespace.addBuiltInSimpleType("nonPositiveInteger");
		xsdNamespace.addBuiltInSimpleType("long");
		xsdNamespace.addBuiltInSimpleType("nonNegativeInteger");
		xsdNamespace.addBuiltInSimpleType("negativeInteger");
		xsdNamespace.addBuiltInSimpleType("int");
		xsdNamespace.addBuiltInSimpleType("short");
		xsdNamespace.addBuiltInSimpleType("byte");
		xsdNamespace.addBuiltInSimpleType("positiveInteger");
		xsdNamespace.addBuiltInSimpleType("unsignedLong");
		xsdNamespace.addBuiltInSimpleType("unsignedInt");
		xsdNamespace.addBuiltInSimpleType("unsignedShort");
		xsdNamespace.addBuiltInSimpleType("unsignedByte");
		
		// misc types
		xsdNamespace.addBuiltInSimpleType("anyURI");
		xsdNamespace.addBuiltInSimpleType("QName");
		xsdNamespace.addBuiltInSimpleType("NOTATION");
		
		return xsdNamespace;
	}
	
	static MWNamespace xsiNamespace(MWXmlSchema schema) {
		MWNamespace xsiNamespace = new MWNamespace(schema);
		xsiNamespace.namespaceUrl = XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI;
		xsiNamespace.namespacePrefix = "xsi";
		
		xsiNamespace.addAttribute("nil");
		xsiNamespace.addAttribute("type");
		xsiNamespace.addAttribute("schemaLocation");
		xsiNamespace.addAttribute("noNamespaceSchemaLocation");
		
		return xsiNamespace;
	}
		
	
	// **************** Instance variables ************************************
	
	/** The URL for this namespace */
	private String namespaceUrl;
		public final static String NAMESPACE_URL_PROPERTY = "namespaceUrl";
	
	/** The prefix for this namespace */
	private String namespacePrefix;
		public final static String NAMESPACE_PREFIX_PROPERTY = "namespacePrefix";
	
	/** Indicates whether or not the user wishes to define the prefix, or if the prefix should be taken from the schema document */
	private boolean namespacePrefixIsUserDefined;
	
	public static final String NAME_XML_NAMESPACE_URL = "http://www.w3.org/XML/1998/namespace";

	/** 
	 * Indicates whether the user wishes this namespace to be declared in instance documents.
	 * The default value is true.  (i.e. a user must purposefully "un-declare" this namespace)
	 */
	private boolean declared;
		public final static String DECLARED_PROPERTY = "declared";
	
	/** The top-level attributes, keyed by their names */
	private Map attributeDeclarations;
	
	/** The top-level elements, keyed by their names */
	private Map elementDeclarations;
	
	/** The top-level types, keyed by their names */
	private Map typeDefinitions;
	
	/** The top-level groups, keyed by their names */
	private Map modelGroupDefinitions;
	
	/** future
	attributeGroups - not likely
	*/
	
	
	// **************** Constructors ******************************************
	
	/** For TopLink only */
	protected MWNamespace() {
		super();
	}
	
	MWNamespace(MWXmlSchema parent) {
		super(parent);
	}
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWNamespace.class);
		
		descriptor.setDefaultRootElement("namespace");
		
		XMLDirectMapping namespacePrefixMapping = new XMLDirectMapping();
		namespacePrefixMapping.setNullValue("");
		namespacePrefixMapping.setAttributeName("namespacePrefix");
		namespacePrefixMapping.setXPath("namespace-prefix/text()");
		descriptor.addMapping(namespacePrefixMapping);
		
		descriptor.addDirectMapping("namespacePrefixIsUserDefined", "namespace-prefix-is-user-defined/text()");
		
		XMLDirectMapping namespaceUrlMapping = new XMLDirectMapping();
		namespaceUrlMapping.setNullValue("");
		namespaceUrlMapping.setAttributeName("namespaceUrl");
		namespaceUrlMapping.setXPath("namespace-url/text()");
		descriptor.addMapping(namespaceUrlMapping);
		
		descriptor.addDirectMapping("declared", "getDeclaredForTopLink", "setDeclaredForTopLink", "declared/text()");
		
		XMLCompositeCollectionMapping attributeDeclarationsMapping = new XMLCompositeCollectionMapping();
		attributeDeclarationsMapping.setAttributeName("attributeDeclarations");
		attributeDeclarationsMapping.setGetMethodName("getAttributeDeclarationsForToplink");
		attributeDeclarationsMapping.setSetMethodName("setAttributeDeclarationsForToplink");
		attributeDeclarationsMapping.setXPath("attribute-declarations/attribute-declaration");
		attributeDeclarationsMapping.setReferenceClass(ExplicitAttributeDeclaration.class);
		attributeDeclarationsMapping.useMapClass(TreeMap.class, "getName");
		descriptor.addMapping(attributeDeclarationsMapping);
		
		XMLCompositeCollectionMapping elementDeclarationsMapping = new XMLCompositeCollectionMapping();
		elementDeclarationsMapping.setAttributeName("elementDeclarations");
		elementDeclarationsMapping.setGetMethodName("getElementDeclarationsForToplink");
		elementDeclarationsMapping.setSetMethodName("setElementDeclarationsForToplink");
		elementDeclarationsMapping.setXPath("element-declarations/element-declaration");
		elementDeclarationsMapping.setReferenceClass(ExplicitElementDeclaration.class);
		elementDeclarationsMapping.useMapClass(TreeMap.class, "getName");
		descriptor.addMapping(elementDeclarationsMapping);

		XMLCompositeCollectionMapping typeDefinitionsMapping = new XMLCompositeCollectionMapping();
		typeDefinitionsMapping.setAttributeName("typeDefinitions");
		typeDefinitionsMapping.setGetMethodName("getTypeDefinitionsForToplink");
		typeDefinitionsMapping.setSetMethodName("setTypeDefinitionsForToplink");
		typeDefinitionsMapping.setXPath("type-definitions/type-definition");
		typeDefinitionsMapping.setReferenceClass(ExplicitSchemaTypeDefinition.class);
		typeDefinitionsMapping.useMapClass(TreeMap.class, "getName");
		descriptor.addMapping(typeDefinitionsMapping);

		XMLCompositeCollectionMapping modelGroupDefinitionsMapping = new XMLCompositeCollectionMapping();
		modelGroupDefinitionsMapping.setAttributeName("modelGroupDefinitions");
		modelGroupDefinitionsMapping.setGetMethodName("getModelGroupDefinitionsForToplink");
		modelGroupDefinitionsMapping.setSetMethodName("setModelGroupDefinitionsForToplink");
		modelGroupDefinitionsMapping.setXPath("model-group-definitions/model-group-definition");
		modelGroupDefinitionsMapping.setReferenceClass(ModelGroupDefinition.class);
		modelGroupDefinitionsMapping.useMapClass(TreeMap.class, "getName");
		descriptor.addMapping(modelGroupDefinitionsMapping);
		
		return descriptor;
	}
	
	
	// **************** Initialization ****************************************
	
	protected void initialize(Node parent) {
		super.initialize(parent);
		this.namespacePrefix = "";
		this.namespaceUrl = "";
		this.attributeDeclarations = new Hashtable();
		this.elementDeclarations = new Hashtable();
		this.typeDefinitions = new Hashtable();
		this.modelGroupDefinitions = new Hashtable();
	}
	
	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		synchronized (this.attributeDeclarations) { children.addAll(this.attributeDeclarations.values()); }
		synchronized (this.elementDeclarations) { children.addAll(this.elementDeclarations.values()); }
		synchronized (this.typeDefinitions) { children.addAll(this.typeDefinitions.values()); }
		synchronized (this.modelGroupDefinitions) { children.addAll(this.modelGroupDefinitions.values()); }
	}
	
	
	// **************** Namespace API *****************************************
	
	public String getNamespaceUrl() {
		return this.namespaceUrl;
	}
	
	/**
	 * Shows "(absent)" if the namespace is null or "".
	 */
	public String getNamespaceUrlForDisplay() {
		String namespace = this.getNamespaceUrl();
		
		if (namespace == null || namespace.equals("")) {
			return "(absent)";
		}
		else {
			return namespace;
		}
	}
	
	private void setNamespaceUrl(String newNamespaceUrl) {
		String oldNamespaceUrl = this.namespaceUrl;
		this.namespaceUrl = newNamespaceUrl;
		this.firePropertyChanged(NAMESPACE_URL_PROPERTY, oldNamespaceUrl, newNamespaceUrl);
		
		// if namespace is the xml namespace, the prefix must be "xml"
		if (NAME_XML_NAMESPACE_URL.equals(newNamespaceUrl)) {
			this.setNamespacePrefix("xml");
			this.namespacePrefixIsUserDefined = false;
		}
	}
	
	public String getNamespacePrefix() {
		return this.namespacePrefix;
	}
	
	public void setNamespacePrefixFromUser(String newNamespacePrefix) {
		this.setNamespacePrefix(newNamespacePrefix);
		this.setNamespacePrefixIsUserDefined(true);
	}
	
	public void setNamespacePrefixFromSchemaDoc(String newNamespacePrefix) {
		if (this.namespacePrefixIsUserDefined()) {
			return;
		}
		else {
			this.setNamespacePrefix(newNamespacePrefix);
			
			if (! this.isBuiltInNamespace()) {
				this.setDeclared(true);
			}
		}
	}
	
	private void setNamespacePrefix(String newNamespacePrefix) {
		if (newNamespacePrefix == null) {
			newNamespacePrefix = "";
		}
		
		String oldNamespacePrefix = this.namespacePrefix;
		this.namespacePrefix = newNamespacePrefix;
		this.firePropertyChanged(NAMESPACE_PREFIX_PROPERTY, oldNamespacePrefix, newNamespacePrefix);
		((MWXmlProject) this.getProject()).schemaChanged(SchemaChange.namespacePrefixesChanged(this.getSchema()));
	}
	
	public boolean namespacePrefixIsUserDefined() {
		return this.namespacePrefixIsUserDefined;
	}
	
	public void setNamespacePrefixIsUserDefined(boolean newValue) {
		this.namespacePrefixIsUserDefined = newValue;
	}
	
	public boolean isDeclared() {
		return this.declared;
	}
	
	public void setDeclared(boolean newValue) {
		boolean oldValue = this.declared;
		this.declared = newValue;
		this.firePropertyChanged(DECLARED_PROPERTY, oldValue, newValue);
	}
	
	
	// **************** Children API ******************************************
	
	public Iterator attributes() {
		return this.attributeDeclarations.values().iterator();
	}
	
	public int attributeCount() {
		return this.attributeDeclarations.size();
	}
	
	public Iterator elements() {
		return this.elementDeclarations.values().iterator();
	}
	
	public int elementCount() {
		return this.elementDeclarations.size();
	}
	
	public Iterator complexTypes() {
		List complexTypeList = new ArrayList();
		Iterator typesIter = types();
		while (typesIter.hasNext()) {
			MWSchemaTypeDefinition typeDefinition = (MWSchemaTypeDefinition)typesIter.next();
			if (typeDefinition.isComplex()) {
				complexTypeList.add(typeDefinition);
			}
		}
		return complexTypeList.iterator();
	} 
	
	public Iterator simpleTypes() {
		List simpleTypeList = new ArrayList();
		Iterator typesIter = types();
		while (typesIter.hasNext()) {
			MWSchemaTypeDefinition typeDefinition = (MWSchemaTypeDefinition)typesIter.next();
			if (!typeDefinition.isComplex()) {
				simpleTypeList.add(typeDefinition);
			}
		}
		return simpleTypeList.iterator();	
	}
	
	public Iterator types() {
		return this.typeDefinitions.values().iterator();
	}
	
	public int typeCount() {
		return this.typeDefinitions.size();
	}
	
	public Iterator modelGroupDefinitions() {
		return this.modelGroupDefinitions.values().iterator();
	}
	
	public int modelGroupDefinitionsCount() {
		return this.modelGroupDefinitions.size();
	}
	
	
	// **************** Adding/removing children ******************************
	
	private ExplicitSimpleTypeDefinition addBuiltInSimpleType(String simpleTypeName) {
		ExplicitSimpleTypeDefinition simpleType = new ExplicitSimpleTypeDefinition(this, simpleTypeName, this.namespaceUrl, true);
		this.typeDefinitions.put(simpleTypeName, simpleType);
		return simpleType;
	}
	
	private ExplicitSimpleTypeDefinition addSimpleType(String simpleTypeName) {
		ExplicitSimpleTypeDefinition simpleType = new ExplicitSimpleTypeDefinition(this, simpleTypeName, this.namespaceUrl);
		this.typeDefinitions.put(simpleTypeName, simpleType);
		return simpleType;
	}
	
	private ExplicitComplexTypeDefinition addBuiltInComplexType(String complexTypeName) {
		ExplicitComplexTypeDefinition complexType = new ExplicitComplexTypeDefinition(this, complexTypeName, this.namespaceUrl, true);
		this.typeDefinitions.put(complexTypeName, complexType);
		return complexType;
	}
	
	private ExplicitComplexTypeDefinition addComplexType(String complexTypeName) {
		ExplicitComplexTypeDefinition complexType = new ExplicitComplexTypeDefinition(this, complexTypeName, this.namespaceUrl);
		this.typeDefinitions.put(complexTypeName, complexType);
		return complexType;
	}
	
	private void removeType(String typeName) {
		ExplicitSchemaTypeDefinition type = (ExplicitSchemaTypeDefinition) this.typeDefinitions.remove(typeName);
		this.getProject().nodeRemoved(type);
	}
	
	private ExplicitAttributeDeclaration addAttribute(String attributeName) {
		ExplicitAttributeDeclaration attribute = new ExplicitAttributeDeclaration(this, attributeName);
		this.attributeDeclarations.put(attributeName, attribute);
		return attribute;
	}
	
	private void removeAttribute(ExplicitAttributeDeclaration attribute) {
		this.attributeDeclarations.remove(attribute.getName());
		this.getProject().nodeRemoved(attribute);
	}
	
	private ExplicitElementDeclaration addElement(String elementName) {
		ExplicitElementDeclaration element = new ExplicitElementDeclaration(this, elementName);
		this.elementDeclarations.put(elementName, element);
		return element;
	}
	
	private void removeElement(ExplicitElementDeclaration element) {
		this.elementDeclarations.remove(element.getName());
		this.getProject().nodeRemoved(element);
	}	
	
	private ModelGroupDefinition addGroup(String groupName) {
		ModelGroupDefinition group = new ModelGroupDefinition(this, groupName);
		this.modelGroupDefinitions.put(groupName, group);
		return group;
	}
	
	private void removeGroup(ModelGroupDefinition group) {
		this.modelGroupDefinitions.remove(group.getName());
		this.getProject().nodeRemoved(group);
	}
	
	
	// **************** Queries ***********************************************
	
	public MWAttributeDeclaration attribute(String attributeName) {
		// TODO: for now, only return top level attributes - need to return imbedded attributes
		return (ExplicitAttributeDeclaration) this.attributeDeclarations.get(attributeName);
	}
	
	public MWElementDeclaration element(String elementName) {
		// TODO: for now, only return top level elements - need to return imbedded elements
		if (elementName != null) {
			return (ExplicitElementDeclaration) this.elementDeclarations.get(elementName);
		}
		return null;
	}
	
	public MWComplexTypeDefinition complexType(String complexTypeName) {
		return (ExplicitComplexTypeDefinition) this.typeDefinitions.get(complexTypeName);
	}
	
	public MWSimpleTypeDefinition simpleType(String simpleTypeName) {
		return (ExplicitSimpleTypeDefinition) this.typeDefinitions.get(simpleTypeName);
	}
	
	ModelGroupDefinition modelGroupDefinition(String modelGroupDefinitionName) {
		return (ModelGroupDefinition) this.modelGroupDefinitions.get(modelGroupDefinitionName);	
	}
	
	public MWModelGroup modelGroup(String modelGroupDefinitionName) {
		return this.modelGroupDefinition(modelGroupDefinitionName).getModelGroup();
	}
	
	public Iterator structuralComponents() {
		return new CompositeIterator(new Iterator[] {this.elements(), this.modelGroupDefinitions(), this.attributes(), this.types()});
	}
	
	public int structuralComponentCount() {
		return this.elementCount() + this.modelGroupDefinitionsCount() + this.attributeCount() + this.typeCount();
	}
	
	public Iterator contextComponents() {
		return new CompositeIterator(new Iterator[] {this.elements(), this.types(), this.modelGroupDefinitions()});
	}
	
	public boolean isTargetNamespace() {
		return this.getSchema().targetNamespace() == this;
	}
	
	public boolean isBuiltInNamespace() {
		return CollectionTools.contains(this.getSchema().builtInNamespaces(), this);
	}
	
	public MWXmlSchema getSchema() {
		return (MWXmlSchema) this.getParent();
	}
	
	
	// **************** Loading ***********************************************
	
	public void reload(XSModel xmlSchema, SchemaDOM schemaDOM, String namespaceUrl) {
		
		// target namespace
		this.setNamespaceUrl(namespaceUrl);
		
		// complex types
		this.reloadTypes(xmlSchema.getComponents(XSConstants.TYPE_DEFINITION));
		
		// attributes
		this.reloadAttributes(xmlSchema.getComponents(XSConstants.ATTRIBUTE_DECLARATION));
		
		// elements
		this.reloadElements(xmlSchema.getComponents(XSConstants.ELEMENT_DECLARATION));
		
		// model groups
		this.reloadGroups(xmlSchema.getComponents(XSConstants.MODEL_GROUP_DEFINITION));
		
		// if namespace is absent, the prefix must not be specified
		if ("".equals(this.namespaceUrl)) {
			this.setNamespacePrefix("");
			this.namespacePrefixIsUserDefined = false;
		}
	}
	
	private void reloadTypes(XSNamedMap typeDefs) {
		Collection removedTypeNames = CollectionTools.collection(this.typeDefinitions.keySet().iterator());
		
		for (int i = typeDefs.getLength() - 1; i >= 0; i --) {
			XSTypeDefinition typeDef = (XSTypeDefinition)typeDefs.item(i);
			ExplicitSchemaTypeDefinition type = null;
			String ns = typeDef.getNamespace();
			if (ns == null) {
				ns = "";
			}
			if (!XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(typeDef.getNamespace()) && ns.equals(getNamespaceUrl())) {
				if (typeDef.getTypeCategory() == XSTypeDefinition.SIMPLE_TYPE) {
					type = this.reloadSimpleType((XSSimpleTypeDecl)typeDef);
				} else {
					type = this.reloadComplexType((XSComplexTypeDecl)typeDef);
				}
				removedTypeNames.remove(type.getName());
			}
		}
		
		for (Iterator stream = removedTypeNames.iterator(); stream.hasNext(); ) {
			this.removeType((String) stream.next());
		}

	}
	
	private ExplicitSimpleTypeDefinition reloadSimpleType(XSSimpleTypeDecl simpleTypeDecl) {
		String simpleTypeName = simpleTypeDecl.getName();
		ExplicitSchemaTypeDefinition type = (ExplicitSchemaTypeDefinition) this.typeDefinitions.get(simpleTypeName);
		ExplicitSimpleTypeDefinition simpleType;
		
		try {
			simpleType = (ExplicitSimpleTypeDefinition) type;
		}
		catch (ClassCastException cce) {
			simpleType = null;
		}
		
		if (simpleType == null) {
			simpleType = this.addSimpleType(simpleTypeName);
		}
		
		simpleType.reload(simpleTypeDecl);
		return simpleType;
	}
	
	private ExplicitComplexTypeDefinition reloadComplexType(XSComplexTypeDecl complexTypeDecl) {
		String complexTypeName = complexTypeDecl.getName();
		ExplicitSchemaTypeDefinition type = (ExplicitSchemaTypeDefinition) this.typeDefinitions.get(complexTypeName);
		ExplicitComplexTypeDefinition complexType;
		
		try {
			complexType = (ExplicitComplexTypeDefinition) type;
		}
		catch (ClassCastException cce) {
			complexType = null;
		}
		
		if (complexType == null) {
			complexType = this.addComplexType(complexTypeName);
		}
		
		complexType.reload(complexTypeDecl);
		return complexType;
	}
	
	private void reloadAttributes(XSNamedMap attributeDeclarations) {
		Collection removedAttributes = new HashBag(this.attributeDeclarations.values());
		
		for (int i = attributeDeclarations.getLength() - 1; i >= 0; i --) {
			XSAttributeDecl attrDecl = (XSAttributeDecl)attributeDeclarations.item(i);
			String ns = attrDecl.getNamespace();
			if (ns == null) {
				ns = "";
			}
			if (ns.equals(getNamespaceUrl())) {
				ExplicitAttributeDeclaration attribute = this.reloadAttribute(attrDecl);
				removedAttributes.remove(attribute);
			}
		}
		
		for (Iterator stream = removedAttributes.iterator(); stream.hasNext(); ) {
			this.removeAttribute((ExplicitAttributeDeclaration) stream.next());
		}
	}
	
	private ExplicitAttributeDeclaration reloadAttribute(XSAttributeDecl attributeDecl) {
		String attributeName = attributeDecl.getName();
		ExplicitAttributeDeclaration attribute = (ExplicitAttributeDeclaration) this.attributeDeclarations.get(attributeName);
		
		if (attribute == null)
			attribute = this.addAttribute(attributeName);
		
		attribute.reload(attributeDecl);
		return attribute;
	}
	
	private void reloadElements(XSNamedMap elementDeclarations) {
		Collection removedElements = new HashBag(this.elementDeclarations.values());
		
		for (int i = elementDeclarations.getLength() - 1; i >= 0; i --) {
			XSElementDecl elementDecl = (XSElementDecl) elementDeclarations.item(i);
			String ns = elementDecl.getNamespace();
			if (ns == null) {
				ns = "";
			}
			if (ns.equals(this.getNamespaceUrl())) {
				ExplicitElementDeclaration element = this.reloadElement(elementDecl);
				removedElements.remove(element);
			}
		}
		
		for (Iterator stream = removedElements.iterator(); stream.hasNext(); ) {
			this.removeElement((ExplicitElementDeclaration) stream.next());
		}
	}
	
	private ExplicitElementDeclaration reloadElement(XSElementDecl elementDecl) { 
		String elementName = elementDecl.getName();
		ExplicitElementDeclaration element = (ExplicitElementDeclaration) this.elementDeclarations.get(elementName);
		
		if (element == null)
			element = this.addElement(elementName);
		
		element.reload(elementDecl);
		return element;
	}
	
	private void reloadGroups(XSNamedMap modelGroupDefs) {
		Collection removedGroups = new HashBag(this.modelGroupDefinitions.values());
		
		for (int i = modelGroupDefs.getLength() - 1; i >= 0; i --) {
			XSModelGroupDefinition modelGroupDef = (XSModelGroupDefinition) modelGroupDefs.item(i);
			String ns = modelGroupDef.getNamespace();
			if (ns == null) {
				ns = "";
			}
			if (ns.equals(getNamespaceUrl())) {
				ModelGroupDefinition group = this.reloadGroup(modelGroupDef);
				removedGroups.remove(group);
			}
		}
		
		for (Iterator stream = removedGroups.iterator(); stream.hasNext(); ) {
			this.removeGroup((ModelGroupDefinition) stream.next());
		}
	}
	
	private ModelGroupDefinition reloadGroup(XSModelGroupDefinition groupDef) {
		String groupName = groupDef.getName();
		ModelGroupDefinition group = (ModelGroupDefinition) this.modelGroupDefinitions.get(groupName);
		
		if (group == null)
			group = this.addGroup(groupName);
		
		group.reload(groupDef);
		return group;
	}
	
	public void resolveReferences() {
		for (Iterator stream = this.types(); stream.hasNext(); ) {
			((ExplicitSchemaTypeDefinition) stream.next()).resolveReferences();
		}
		
		for (Iterator stream = this.attributes(); stream.hasNext(); ) {
			((ExplicitAttributeDeclaration) stream.next()).resolveReferences();
		}
		
		for (Iterator stream = this.elements(); stream.hasNext(); ) {
			((ExplicitElementDeclaration) stream.next()).resolveReferences();
		}
		
		for (Iterator stream = this.modelGroupDefinitions(); stream.hasNext(); ) {
			((ModelGroupDefinition) stream.next()).resolveReferences();
		}
	}
	
	
	// **************** Display ***********************************************
	
	public void toString(StringBuffer sb) {
		super.toString(sb);
		String url = (this.getNamespaceUrl().equals("")) ? "(absent)" : this.getNamespaceUrl();
		sb.append("namespace: " + url);
	}
	
	public int compareTo(Object o) {
		if(this == o)
			return 0;
		 	
		return this.getNamespaceUrl().compareTo(((MWNamespace)o).getNamespaceUrl());
	}
	
	
	//******************* Toplink persistence methods ***************************
	
	private Boolean getDeclaredForTopLink() {
		return new Boolean(this.declared);
	}
	
	private void setDeclaredForTopLink(Boolean newValue) {
		if (newValue == null) {
			newValue = Boolean.TRUE;
		}
		
		this.declared = newValue.booleanValue();
	}
	
	private Map getAttributeDeclarationsForToplink() {
		return new TreeMap(this.attributeDeclarations);
	}
	
	private void setAttributeDeclarationsForToplink(Map map) {
		this.attributeDeclarations = mapFromNamedElements(map.values().iterator());
	}
	
	private Map getElementDeclarationsForToplink() {
		return new TreeMap(this.elementDeclarations);
	}
	
	private void setElementDeclarationsForToplink(Map map) {
		this.elementDeclarations = mapFromNamedElements(map.values().iterator());
	}
	
	private Map getTypeDefinitionsForToplink() {
		return new TreeMap(this.typeDefinitions);
	}
	
	private void setTypeDefinitionsForToplink(Map map) {
		this.typeDefinitions = mapFromNamedElements(map.values().iterator());
	}
	
	private Map getModelGroupDefinitionsForToplink() {
		return new TreeMap(this.modelGroupDefinitions);
	}
	
	private void setModelGroupDefinitionsForToplink(Map map) {
		this.modelGroupDefinitions = mapFromNamedElements(map.values().iterator());
	}
	
	private Map mapFromNamedElements(Iterator iter) {
		Map elements = new Hashtable();
		while(iter.hasNext()) {
			MWNamedSchemaComponent next = (MWNamedSchemaComponent)iter.next();
			elements.put(next.getName(), next);
		}
		return elements;
	}
	
	public void setNamespacePrefixFromSchemaDocForTopLink(String newNamespacePrefix) {
		if (this.namespacePrefixIsUserDefined()) {
			return;
		}
		else {
			this.namespacePrefix = newNamespacePrefix;
		}
	}
	
	public void setNamespaceIsDeclaredFromSchemaDocForTopLink(boolean newIsDeclared) {
		this.declared = newIsDeclared;
	}
	
	// **************** Member classes ****************************************
	
	private static class NamespaceComparator
		implements java.util.Comparator
	{
		NamespaceComparator() {
			super();
		}
		
		public int compare(Object obj1, Object obj2) {
			return this.compare((MWNamespace) obj1, (MWNamespace) obj2);
		}
		
		public int compare(MWNamespace ns1, MWNamespace ns2) {
			// the URL "http://www.w3.org/2001/XMLSchema" always comes first 
			if (XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(ns1.getNamespaceUrl())) {
				return -1;
			}
			else if (XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(ns2.getNamespaceUrl())) {
				return 1;
			}
			
			// the URL "http://www.w3.org/2001/XMLSchema-instance" always comes next
			if (XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(ns1.getNamespaceUrl())) {
				return -1;
			}
			else if (XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(ns2.getNamespaceUrl())) {
				return 1;
			}
			
			// the target namespace of the schema comes next
			if (ns1.isTargetNamespace()) {
				return -1;
			}
			else if (ns2.isTargetNamespace()) {
				return 1;
			}
			
			// from there namespaces are ordered by their URL
			return Collator.getInstance().compare(ns1.getNamespaceUrl(), ns2.getNamespaceUrl());
		}
	}
}
