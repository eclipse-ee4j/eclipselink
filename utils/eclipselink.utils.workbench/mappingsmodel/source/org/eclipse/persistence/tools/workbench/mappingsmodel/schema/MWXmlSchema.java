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
package org.eclipse.persistence.tools.workbench.mappingsmodel.schema;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import javax.xml.XMLConstants;

import org.apache.xerces.impl.xs.XMLSchemaLoader;
import org.apache.xerces.impl.xs.opti.AttrImpl;
import org.apache.xerces.impl.xs.opti.ElementImpl;
import org.apache.xerces.impl.xs.opti.SchemaDOM;
import org.apache.xerces.impl.xs.opti.SchemaDOMParser;
import org.apache.xerces.impl.xs.opti.SchemaParsingConfig;
import org.apache.xerces.util.URI;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xs.StringList;
import org.apache.xerces.xs.XSException;
import org.apache.xerces.xs.XSModel;
import org.eclipse.persistence.mappings.foundation.AbstractDirectMapping;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.schema.XMLSchemaClassPathReference;
import org.eclipse.persistence.oxm.schema.XMLSchemaFileReference;
import org.eclipse.persistence.oxm.schema.XMLSchemaReference;
import org.eclipse.persistence.oxm.schema.XMLSchemaURLReference;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWNominative;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.QName;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.xml.MWXmlProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.resource.ClasspathResourceSpecification;
import org.eclipse.persistence.tools.workbench.mappingsmodel.resource.FileResourceSpecification;
import org.eclipse.persistence.tools.workbench.mappingsmodel.resource.ResourceException;
import org.eclipse.persistence.tools.workbench.mappingsmodel.resource.ResourceSpecification;
import org.eclipse.persistence.tools.workbench.mappingsmodel.resource.UrlResourceSpecification;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.SchemaChange;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.HashBag;
import org.eclipse.persistence.tools.workbench.utility.iterators.CompositeIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.FilteringIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;
import org.eclipse.persistence.tools.workbench.utility.node.Node;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;
import org.w3c.dom.NamedNodeMap;

public final class MWXmlSchema extends MWModel
	implements MWNominative
{
	
	public static final int INFINITY = 2147483640;

	private boolean shouldUseDefaultNamespace;
		public static final String SHOULD_USE_DEFAULT_NAMESPACE = "shouldUseDefaultNamespace";
	
	private String defaultNamespaceUrl;
		public static final String DEFAULT_NAMESPACE_URL = "defaultNamespaceUrl";
	
	// **************** MW-specific info **************************************
	
	/** Used by user to uniquely identify a schema */
	private volatile String name;
		public static final String NAME_PROPERTY = "name";
	
	/** Location of the schema file resource */
	private volatile ResourceSpecification schemaSource;
		public static final String SCHEMA_SOURCE_PROPERTY = "schemaSource";
		
	
	// **************** XDK-imported info *************************************
	
	/** The url of the target namespace */
	private volatile String targetNamespaceUrl;
	
	/** 
	 * Contains the target Namespace object and all imported Namespace objects
	 * These are the namespaces directly declared by the schema
	 */
	private Collection declaredNamespaces;
		public static final String NAMESPACES_COLLECTION = "namespaces";
	
	/** Contains all built-in types - not persisted */
	private List builtInNamespaces;
	
	
	// **************** Static creators ***************************************
	
	public static MWXmlSchema createFromFile(MWXmlSchemaRepository parent, String name, String filePath) {
		MWXmlSchema schema = new MWXmlSchema(parent, name);
		schema.setSchemaSource(new FileResourceSpecification(schema, filePath));
		return schema;
	}
	
	public static MWXmlSchema createFromUrl(MWXmlSchemaRepository parent, String name, String url) {
		MWXmlSchema schema = new MWXmlSchema(parent, name);
		schema.setSchemaSource(new UrlResourceSpecification(schema, url));
		return schema;
	}
	
	public static MWXmlSchema createFromClasspath(MWXmlSchemaRepository parent, String name, String resourceName) {
		MWXmlSchema schema = new MWXmlSchema(parent, name);
		schema.setSchemaSource(new ClasspathResourceSpecification(schema, resourceName));
		return schema;
	}
	
	
	// **************** Constructors ******************************************
	
	/** For TopLink only */
	private MWXmlSchema() {
		super();
	}
	
	private MWXmlSchema(MWModel parent) {
		super(parent);
	}
	
	private MWXmlSchema(MWModel parent, String name) {
		this(parent);
		this.initialize(name);
	}
	
	
	// **************** Initialization ****************************************
	
	protected void initialize() {
		super.initialize();
		this.initializeBuiltInNamespaces();
	}
	
	private void initializeBuiltInNamespaces() {
		this.builtInNamespaces = new Vector();
		this.builtInNamespaces.add(MWNamespace.xsdNamespace(this));
		this.builtInNamespaces.add(MWNamespace.xsiNamespace(this));
	}
	
	protected void initialize(Node parent) {
		super.initialize(parent);
		this.targetNamespaceUrl = "";
		this.defaultNamespaceUrl = "";
		this.shouldUseDefaultNamespace = false;
		this.declaredNamespaces = new HashBag();
	}
	
	private void initialize(String schemaName) {
		this.name = schemaName;
	}
	
	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.schemaSource);
		synchronized (this.declaredNamespaces) { children.addAll(this.declaredNamespaces); }
		synchronized (this.builtInNamespaces) { children.addAll(this.builtInNamespaces); }
	}
	
	
	// **************** Nominative implementation ***************************
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		if (name == null) {
			throw new NullPointerException();
		}
		String old = this.name;
		this.name = name;
		if (this.attributeValueHasChanged(old, name)) {
			// synch up the repository before notifying everyone
			try {
				this.schemaRepository().schemaRenamed(old, name, this);
			} catch (RuntimeException ex) {
				this.name = old;		// restore the name before re-throwing the exception
				throw ex;
			}
			this.firePropertyChanged(NAME_PROPERTY, old, name);
			this.getProject().nodeRenamed(this);
		}
	}
	
	
	// **************** Load mechanism API ************************************
	
	public ResourceSpecification getSchemaSource() {
		return this.schemaSource;
	}
	
	private void setSchemaSource(ResourceSpecification newSchemaSource) {
		ResourceSpecification oldSchemaLocation = this.schemaSource;
		this.schemaSource = newSchemaSource;
		this.firePropertyChanged(SCHEMA_SOURCE_PROPERTY, oldSchemaLocation, newSchemaSource);
	}
	
	public void setFileSchemaLocation(String filePath) {
		this.setSchemaSource(new FileResourceSpecification(this, filePath));
	}
	
	public void setUrlSchemaLocation(String url) {
		this.setSchemaSource(new UrlResourceSpecification(this, url));
	}
	
	public void setClasspathSchemaLocation(String resourceName) {
		this.setSchemaSource(new ClasspathResourceSpecification(this, resourceName));
	}
	
	
	// **************** Namespaces ********************************************
	
	public String targetNamespaceUrl() {
		return this.targetNamespaceUrl;
	}
	
	public void setTargetNamespaceUrl(String newTargetNamespaceUrl) {
		this.targetNamespaceUrl = newTargetNamespaceUrl;
		if ("".equals(this.defaultNamespaceUrl)) {
			this.defaultNamespaceUrl = this.targetNamespaceUrl;
		}
	}
	
	/** Returns an Iterator of declared Namespace objects only */
	public Iterator declaredNamespaces() {
		return this.declaredNamespaces.iterator();
	}
	
	private MWNamespace declaredNamespace(String namespaceUrl) {
		for (Iterator stream = this.declaredNamespaces(); stream.hasNext(); ) {
			MWNamespace next = (MWNamespace) stream.next(); 
			
			if (next.getNamespaceUrl().equals(namespaceUrl)) {
				return next;
			}
		}
		
		return null;
	}
	
	private MWNamespace addDeclaredNamespace(String namespaceUrl) {
		MWNamespace namespace = new MWNamespace(this);
		this.addDeclaredNamespace(namespace);
		return namespace;
	}
	
	private void addDeclaredNamespace(MWNamespace namespace) {
		this.declaredNamespaces.add(namespace);
		this.fireItemAdded(NAMESPACES_COLLECTION, namespace);
	}
	
	private void removeDeclaredNamespace(MWNamespace namespace) {
		this.declaredNamespaces.remove(namespace);
		this.fireItemRemoved(NAMESPACES_COLLECTION, namespace);
		this.getProject().nodeRemoved(namespace);
	}
	
	public MWNamespace targetNamespace() {
		return this.declaredNamespace(this.targetNamespaceUrl());
	}
	
	public Iterator importedNamespaces() {
		return new FilteringIterator(this.declaredNamespaces()) {
			protected boolean accept(Object next) {
				return ! ((MWNamespace) next).isTargetNamespace();
			}
		};
	}
	
	// **************** Built in namespaces ***********************************
	
	public ListIterator builtInNamespaces() {
		return this.builtInNamespaces.listIterator();
	}
	
	private MWNamespace builtInNamespace(String namespaceUrl) {
		for (Iterator stream = this.builtInNamespaces(); stream.hasNext(); ) {
			MWNamespace next = (MWNamespace) stream.next(); 
			
			if (next.getNamespaceUrl().equals(namespaceUrl)) {
				return next;
			}
		}
		
		return null;
	}
	
	
	// **************** Convenience *******************************************
	
	public MWXmlSchemaRepository schemaRepository() {
		return (MWXmlSchemaRepository) this.getParent();
	}

	public void toString(StringBuffer sb) {
		sb.append(this.name);
	}
	
	
	// **************** General namespace API *********************************
	
	/** Return an iterator over all the contained namespaces (built-in last) */
	private Iterator namespaces() {
		return new CompositeIterator(this.declaredNamespaces(), this.builtInNamespaces());
	}
	
	/** Return the URL string of the first namespace for the given prefix, if it exists */
	public String namespaceUrlForPrefix(String namespacePrefix) {
		for (Iterator stream = this.namespaces(); stream.hasNext(); ) {
			MWNamespace namespace = (MWNamespace) stream.next();
			
			if (namespacePrefix.equals(namespace.getNamespacePrefix())) {
				return namespace.getNamespaceUrl();
			}
		}
		
		return "";
	}
	
	/** Return the prefix of the first namespace for the given URL string, if it exists */
	public String namespacePrefixForUrl(String namespaceUrl) {
		if (namespaceUrl == null || "".equals(namespaceUrl)) {
			return "";
		}
		return this.namespaceForUrl(namespaceUrl).getNamespacePrefix();
	}
	
	/** Return the first namespace for the given URL string, if it exists */
	public MWNamespace namespaceForUrl(String namespaceUrl) {
		for (Iterator stream = this.namespaces(); stream.hasNext(); ) {
			MWNamespace next = (MWNamespace) stream.next(); 
			
			if (next.getNamespaceUrl().equals(namespaceUrl)) {
				return next;
			}
		}
		
		return null;
	}
	
	
	// **************** Querying **********************************************
	
	public int attributeCount() {
		int attributeCount = 0;
		
		for (Iterator stream = this.declaredNamespaces(); stream.hasNext(); ) {
			attributeCount += ((MWNamespace) stream.next()).attributeCount();
		}
		
		return attributeCount;
	}
	
	public MWAttributeDeclaration attribute(String attributeName) {
		return this.attribute(this.targetNamespaceUrl, attributeName);
	}
	
	public MWAttributeDeclaration attribute(String namespaceUrl, String attributeName) {
		if (namespaceUrl == null || "".equals(namespaceUrl)) {
			namespaceUrl = targetNamespaceUrl();
		}
		return this.namespaceForUrl(namespaceUrl).attribute(attributeName);	
	}
	
	public int elementCount() {
		int elementCount = 0;
		
		for (Iterator stream = this.declaredNamespaces(); stream.hasNext(); ) {
			elementCount += ((MWNamespace) stream.next()).elementCount();
		}
		
		return elementCount;
	}
	
	public MWElementDeclaration element(String elementName) {
		return this.element(this.targetNamespaceUrl, elementName);
	}
	
	public MWElementDeclaration element(String namespaceUrl, String elementName) {
		if (namespaceUrl == null || "".equals(namespaceUrl)) {
			namespaceUrl = targetNamespaceUrl();
		}
		return this.namespaceForUrl(namespaceUrl).element(elementName);	
	}
	
	public int typeCount() {
		int typeCount = 0;
		
		for (Iterator stream = this.declaredNamespaces(); stream.hasNext(); ) {
			typeCount += ((MWNamespace) stream.next()).typeCount();
		}
		
		return typeCount;
	}
	
	public MWComplexTypeDefinition complexType(String complexTypeName) {
		return this.complexType(this.targetNamespaceUrl, complexTypeName);
	}
	
	public MWComplexTypeDefinition complexType(String namespaceUrl, String complexTypeName) {
		if (namespaceUrl == null || "".equals(namespaceUrl)) {
			namespaceUrl = targetNamespaceUrl();
		}
		return this.namespaceForUrl(namespaceUrl).complexType(complexTypeName);
	}

	public Iterator complexTypes() {
		return new CompositeIterator(this.complexTypeIterators());
	}

	private Iterator complexTypeIterators() {
		return new TransformationIterator(this.declaredNamespaces()) {
			protected Object transform(Object next) {
				return ((MWNamespace) next).complexTypes();
			}
		};
	}
	
	public MWSimpleTypeDefinition simpleType(String simpleTypeName) {
		return this.simpleType(this.targetNamespaceUrl, simpleTypeName);
	}
	
	public MWSimpleTypeDefinition simpleType(String namespaceUrl, String simpleTypeName) {
		if (namespaceUrl == null || "".equals(namespaceUrl)) {
			namespaceUrl = targetNamespaceUrl();
		}
		return this.namespaceForUrl(namespaceUrl).simpleType(simpleTypeName);
	}
	
	public Iterator simpleTypes() {
		return new CompositeIterator(this.simpleTypeIterators());
	}

	private Iterator simpleTypeIterators() {
		return new TransformationIterator(this.declaredNamespaces()) {
			protected Object transform(Object next) {
				return ((MWNamespace) next).simpleTypes();
			}
		};
	}

	public int modelGroupDefinitionCount() {
		int groupCount = 0;
		
		for (Iterator stream = this.declaredNamespaces(); stream.hasNext(); ) {
			groupCount += ((MWNamespace) stream.next()).modelGroupDefinitionsCount();
		}
		
		return groupCount;
	}
	
	public MWModelGroupDefinition modelGroupDefinition(String modelGroupDefName) {
		return this.modelGroupDefinition(this.targetNamespaceUrl, modelGroupDefName);
	}
	
	public MWModelGroupDefinition modelGroupDefinition(String namespaceUrl, String modelGroupDefName) {
		if (namespaceUrl == null || "".equals(namespaceUrl)) {
			namespaceUrl = targetNamespaceUrl();
		}
		return this.namespaceForUrl(namespaceUrl).modelGroupDefinition(modelGroupDefName);
	}
	
	public MWModelGroup modelGroup(String modelGroupDefName) {
		return this.modelGroup(this.targetNamespaceUrl, modelGroupDefName);
	}
	
	public MWModelGroup modelGroup(String namespaceUrl, String modelGroupDefName) {
		if (namespaceUrl == null || "".equals(namespaceUrl)) {
			namespaceUrl = targetNamespaceUrl();
		}
		return this.modelGroupDefinition(namespaceUrl, modelGroupDefName).getModelGroup();
	}
	
	public Iterator structuralComponents() {
		return new CompositeIterator(this.structuralComponentIterators());
	}
	
	private Iterator structuralComponentIterators() {
		return new TransformationIterator(this.declaredNamespaces()) {
			protected Object transform(Object next) {
				return ((MWNamespace) next).structuralComponents();
			}
		};
	}
	
	public Iterator contextComponents() {
		return new CompositeIterator(this.contextComponentIterators());
	}
	
	private Iterator contextComponentIterators() {
		return new TransformationIterator(this.declaredNamespaces()) {
			protected Object transform(Object next) {
				return ((MWNamespace) next).contextComponents();
			}
		};
	}
	
	public Iterator rootElements() {
		return new CompositeIterator(this.rootElementIterators());
	}
	
	private Iterator rootElementIterators() {
		return new TransformationIterator(this.declaredNamespaces()) {
			protected Object transform(Object next) {
				return ((MWNamespace) next).elements();
			}
		};
	}
	
	public MWNamedSchemaComponent component(QName qName) {
		if (qName.getComponentType() == QName.ATTRIBUTE_TYPE) {
			return this.attribute(qName.getNamespaceURI(), qName.getLocalName());
		}
		else if (qName.getComponentType() == QName.ELEMENT_TYPE) {
			return this.element(qName.getNamespaceURI(), qName.getLocalName());
		}
		else if (qName.getComponentType() == QName.COMPLEX_TYPE_TYPE) {
			return this.complexType(qName.getNamespaceURI(), qName.getLocalName());
		}
		else if (qName.getComponentType() == QName.GROUP_TYPE) {
			return this.modelGroupDefinition(qName.getNamespaceURI(), qName.getLocalName());
		}
		else {
			return null;
		}
	}
	
	
	// **************** Refreshing ********************************************
	
	public void reload() 
		throws ResourceException
	{
		URI uri = this.schemaSource.validResourceURI();
		XMLSchemaLoader schemaLoader = new XMLSchemaLoader();
		XSModel xmlSchemaModel = null;

		XMLInputSource inputSource = new XMLInputSource(null, uri.toString(), null);
		SchemaDOMParser parser = new SchemaDOMParser(new SchemaParsingConfig());
		try {
			xmlSchemaModel = schemaLoader.loadURI(uri.toString());	
			parser.parse(inputSource);
		} catch (Throwable t) {
			if (t instanceof XSException) {
				throw (XSException) t;
			}
			throw new RuntimeException(t);
		}
		SchemaDOM schemaDOM = (SchemaDOM)parser.getDocument2();		
		this.reload(xmlSchemaModel, schemaDOM);
		this.fireStateChanged();
		((MWXmlProject) this.getProject()).schemaChanged(SchemaChange.schemaStructureChange(this));
		
		this.reloadSchemaPrefixes(schemaDOM);

	}

	private void reload(XSModel xmlSchema, SchemaDOM schemaDOM) {
		// Keep track of the namespaces that are no longer present.
		Collection oldNamespaces = CollectionTools.collection(this.declaredNamespaces());
		
		this.reloadTargetNamespace(xmlSchema, schemaDOM, oldNamespaces);
		
		StringList allNamespaceUrls = xmlSchema.getNamespaces();
		for (int i = 0; i < allNamespaceUrls.getLength(); i ++ ) {
			String namespaceUrl = allNamespaceUrls.item(i);
			
			if (namespaceUrl != null && ! namespaceUrl.equals(this.targetNamespaceUrl()) && ! namespaceUrl.equals(XMLConstants.W3C_XML_SCHEMA_NS_URI)) {
				this.reloadImportedNamespace(xmlSchema, schemaDOM, namespaceUrl, oldNamespaces);
			}
		}
		
		for (Iterator stream = oldNamespaces.iterator(); stream.hasNext(); ) {
			this.removeDeclaredNamespace((MWNamespace) stream.next());
		}
		
		this.resolveReferences();
	}


	private void reloadTargetNamespace(XSModel xmlSchema, SchemaDOM schemaDOM, Collection oldNamespaces) {
		String url = "";
		String uriString = schemaDOM.getDocumentElement().getAttribute("targetNamespace");
		if (uriString != null && uriString != "") {
			url = uriString;
		} 
		MWNamespace targetNamespace = this.targetNamespace();
		this.setTargetNamespaceUrl(url);
		this.reloadNamespace(xmlSchema, schemaDOM, targetNamespace, url, oldNamespaces);
		
		targetNamespace = this.targetNamespace();
		if (targetNamespace.getNamespacePrefix().equals("")) {
			targetNamespace.setDeclared(false);
		}	
	}
	
	private void reloadImportedNamespace(XSModel xmlSchema, SchemaDOM schemaDOM, String namespaceUrl, Collection oldNamespaces) {
		MWNamespace namespace = this.declaredNamespace(namespaceUrl);
		this.reloadNamespace(xmlSchema, schemaDOM, namespace, namespaceUrl, oldNamespaces);
	}
		
	private void reloadNamespace(XSModel xmlSchema, SchemaDOM schemaDOM, MWNamespace namespace, String namespaceUrl, Collection oldNamespaces) {
		if (namespace == null) {
			namespace = this.addDeclaredNamespace(namespaceUrl);
		}
		else {
			oldNamespaces.remove(namespace);
		}
		
		namespace.reload(xmlSchema, schemaDOM, namespaceUrl);
	}
	
	private void reloadSchemaPrefixes(SchemaDOM schemaDOM) {
		ElementImpl schemaElement = (ElementImpl)schemaDOM.getDocumentElement();
		
		// parse <schema attributes ...>
		NamedNodeMap map   = schemaElement.getAttributes();
		int          nAttr = map.getLength();
		AttrImpl attr;
		String  localName, value, ns;
		
		for (int i = 0; i < nAttr; i++) {
			attr   = (AttrImpl)map.item(i);
			localName   = attr.getLocalName();
			value  = attr.getValue();
			ns     = attr.getNamespaceURI();
			
			if (ns == XMLConstants.XMLNS_ATTRIBUTE_NS_URI && localName != XMLConstants.XMLNS_ATTRIBUTE) {
				MWNamespace namespace = this.namespaceForUrl(value);
				
				if (namespace != null) {
					namespace.setNamespacePrefixFromSchemaDoc(localName);
				}
			}
		}
	}
	
	public void resolveReferences() {
		for (Iterator stream = this.namespaces(); stream.hasNext(); ) {
			((MWNamespace) stream.next()).resolveReferences();
		}
	}
	
	
	// **************** Problems **********************************************
	
	protected void addProblemsTo(List currentProblems) {
		super.addProblemsTo(currentProblems);
		this.checkRequiredNamespacePrefix(currentProblems);
		this.checkDuplicateNamespacePrefix(currentProblems);
		this.checkSpacesinNamespacePrefix(currentProblems);
	}
	
	private void checkSpacesinNamespacePrefix(List currentProblems) {
		for (Iterator stream = this.namespaces(); stream.hasNext();) {
			MWNamespace namespace = (MWNamespace) stream.next();
			
			if (! "".equals(namespace.getNamespaceUrl()) && StringTools.contains(namespace.getNamespacePrefix(), " ", '\0')) {
				currentProblems.add(this.buildProblem(ProblemConstants.SCHEMA_NAMESPACE_PREFIX_CONTAINS_SPACE, namespace.getNamespaceUrlForDisplay()));
			}
		}
	}
	
	private void checkRequiredNamespacePrefix(List currentProblems) {
		for (Iterator stream = this.namespaces(); stream.hasNext(); ) {
			MWNamespace namespace = (MWNamespace) stream.next();
			
			if (! "".equals(namespace.getNamespaceUrl()) && "".equals(namespace.getNamespacePrefix()) && !namespace.getNamespaceUrl().equals(this.defaultNamespaceUrl)) {
				currentProblems.add(this.buildProblem(ProblemConstants.SCHEMA_NAMESPACE_PREFIX_NOT_SPECIFIED, namespace.getNamespaceUrlForDisplay()));
			}
		}
	}
	
	private void checkDuplicateNamespacePrefix(List currentProblems) {
		HashBag usedPrefixes = new HashBag();
		
		for (Iterator stream = this.namespaces(); stream.hasNext(); ) {
			String prefix = ((MWNamespace) stream.next()).getNamespacePrefix();
			
			if (! "".equals(prefix)) {
				usedPrefixes.add(prefix);
			}
		}
		
		for (Iterator stream = usedPrefixes.uniqueIterator(); stream.hasNext(); ) {
			String prefix = (String) stream.next();
			
			if (usedPrefixes.count(prefix) > 1) {
				currentProblems.add(this.buildProblem(ProblemConstants.SCHEMA_NAMESPACE_PREFIX_DUPLICATED, prefix));
			}
		}
	}
	
	
	// **************** Runtime conversion ************************************
	
	public XMLSchemaReference runtimeSchemaReference() {
		if (this.schemaSource instanceof ClasspathResourceSpecification) {
			return new XMLSchemaClassPathReference(this.schemaSource.getLocation());
		}
		else if (this.schemaSource instanceof FileResourceSpecification) {
			return new XMLSchemaFileReference(this.schemaSource.getLocation());
		}
		else if (this.schemaSource instanceof UrlResourceSpecification) {
			return new XMLSchemaURLReference(this.schemaSource.getLocation());
		}
		else {
			return null;
		}
	}
	
	public NamespaceResolver runtimeNamespaceResolver() {
		NamespaceResolver namespaceResolver = new NamespaceResolver();
		
		for (Iterator stream = this.namespaces(); stream.hasNext(); ) {
			MWNamespace namespace = (MWNamespace) stream.next();
			
			if (namespace.isDeclared() && ! "".equals(namespace.getNamespacePrefix())) {
				namespaceResolver.put(namespace.getNamespacePrefix(), namespace.getNamespaceUrl());
			}
		}
		
		if (this.shouldUseDefaultNamespace() && !"".equals(this.getDefaultNamespaceUrl())) {
			namespaceResolver.setDefaultNamespaceURI(this.getDefaultNamespaceUrl());
		}
		
		return namespaceResolver;
	}
	
	
    // **************** Toplink use only  *************************************
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWXmlSchema.class);
		
		descriptor.setDefaultRootElement("xml-schema");

		descriptor.addDirectMapping("name", "name/text()");
		
		XMLDirectMapping targetNamespaceMapping = new XMLDirectMapping();
		targetNamespaceMapping.setAttributeName("targetNamespaceUrl");
		targetNamespaceMapping.setXPath("target-namespace-url/text()");
		targetNamespaceMapping.setNullValue("");
		descriptor.addMapping(targetNamespaceMapping);
		
		XMLDirectMapping defaultNamespaceMapping = new XMLDirectMapping();
		defaultNamespaceMapping.setAttributeName("defaultNamespaceUrl");
		defaultNamespaceMapping.setXPath("default-namespace-url/text()");
		defaultNamespaceMapping.setNullValue("");
		descriptor.addMapping(defaultNamespaceMapping);

		XMLDirectMapping shouldDefaultNamespaceMapping = new XMLDirectMapping();
		shouldDefaultNamespaceMapping.setAttributeName("shouldUseDefaultNamespace");
		shouldDefaultNamespaceMapping.setXPath("should-default-namespace-url/text()");
		shouldDefaultNamespaceMapping.setNullValue(false);
		descriptor.addMapping(shouldDefaultNamespaceMapping);

		XMLCompositeObjectMapping schemaLocationMapping = new XMLCompositeObjectMapping();
		schemaLocationMapping.setAttributeName("schemaSource");
		schemaLocationMapping.setXPath("schema-source");
		schemaLocationMapping.setReferenceClass(ResourceSpecification.class);
		descriptor.addMapping(schemaLocationMapping);
		
		XMLCompositeCollectionMapping builtInNamespacesMapping = new XMLCompositeCollectionMapping();
		builtInNamespacesMapping.setAttributeName("builtInNamespaces");
		builtInNamespacesMapping.setGetMethodName("getPersistedBuiltInNamespacesForTopLink");
		builtInNamespacesMapping.setSetMethodName("setPersistedBuiltInNamespacesForTopLink");
		builtInNamespacesMapping.setXPath("built-in-namespaces/namespace");
		builtInNamespacesMapping.setReferenceClass(BuiltInNamespace.class);
		descriptor.addMapping(builtInNamespacesMapping);
		
		XMLCompositeCollectionMapping namespacesMapping = new XMLCompositeCollectionMapping();
		namespacesMapping.setAttributeName("declaredNamespaces");
		namespacesMapping.setGetMethodName("getDeclaredNamespacesForToplink");
		namespacesMapping.setSetMethodName("setDeclaredNamespacesForToplink");
		namespacesMapping.setXPath("declared-namespaces/namespace");
		namespacesMapping.setReferenceClass(MWNamespace.class);
		descriptor.addMapping(namespacesMapping);

		return descriptor;
	}
	
	
	private Collection getPersistedBuiltInNamespacesForTopLink() {
		Collection persistedBuiltInNamespaces = new Vector();
		
		for (Iterator stream = this.builtInNamespaces(); stream.hasNext(); ) {
			MWNamespace nonPersistedNamespace = (MWNamespace) stream.next();
			BuiltInNamespace persistedNamespace = new BuiltInNamespace();
			persistedNamespace.url = nonPersistedNamespace.getNamespaceUrl();
			persistedNamespace.prefix = nonPersistedNamespace.getNamespacePrefix();
			persistedNamespace.declared = Boolean.valueOf(nonPersistedNamespace.isDeclared());
			persistedBuiltInNamespaces.add(persistedNamespace);
		}
		
		return persistedBuiltInNamespaces;
	}
	
	private void setPersistedBuiltInNamespacesForTopLink(Collection persistedBuiltInNamespaces) {
		for (Iterator stream = persistedBuiltInNamespaces.iterator(); stream.hasNext(); ) {
			BuiltInNamespace persistedNamespace = (BuiltInNamespace) stream.next();
			String url = persistedNamespace.url;
			String prefix = persistedNamespace.prefix;
			boolean declared = persistedNamespace.declared.booleanValue();
			
			MWNamespace nonpersistedNamespace = this.builtInNamespace(url);
			nonpersistedNamespace.setNamespacePrefixFromSchemaDocForTopLink(prefix);
			nonpersistedNamespace.setNamespaceIsDeclaredFromSchemaDocForTopLink(declared);
		}
	}
	
	private Collection getDeclaredNamespacesForToplink() {
		return CollectionTools.sortedSet(declaredNamespaces());
	}
	
	private void setDeclaredNamespacesForToplink(Collection newNamespaces) {
		this.declaredNamespaces = newNamespaces;
	}
	
	
	// **************** Member classes ****************************************
	
	/**
	 * Used only by TopLink.
	 * This class is used to persist built in namespace information.
	 */
	public static class BuiltInNamespace 
	{
		public static XMLDescriptor buildDescriptor(){
			XMLDescriptor descriptor = new XMLDescriptor();
			descriptor.setJavaClass(BuiltInNamespace.class);
			
			((AbstractDirectMapping) descriptor.addDirectMapping("url", "@url")).setNullValue("");
			((AbstractDirectMapping) descriptor.addDirectMapping("prefix", "@prefix")).setNullValue("");
			((AbstractDirectMapping) descriptor.addDirectMapping("declared", "@declared")).setNullValue(Boolean.FALSE);
			
			return descriptor;
		}
		
		String url;
		
		String prefix;
		
		Boolean declared;
		
		private BuiltInNamespace() {
			super();
		}
	}


	public String getDefaultNamespaceUrl() {
		return defaultNamespaceUrl;
	}

	public void setDefaultNamespaceUrl(String newDefaultNamespaceUrl) {
		String oldUrl = this.defaultNamespaceUrl;
		this.defaultNamespaceUrl = newDefaultNamespaceUrl;
		firePropertyChanged(DEFAULT_NAMESPACE_URL, oldUrl, newDefaultNamespaceUrl); 
	}

	public boolean shouldUseDefaultNamespace() {
		return shouldUseDefaultNamespace;
	}

	public void setShouldUseDefaultNamespace(boolean newValue) {
		boolean oldValue = this.shouldUseDefaultNamespace;
		this.shouldUseDefaultNamespace = newValue;
		firePropertyChanged(SHOULD_USE_DEFAULT_NAMESPACE, oldValue, newValue);
	}
}
