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

import org.apache.xerces.impl.xs.XSParticleDecl;
import org.apache.xerces.xs.XSObject;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.utility.HashBag;
import org.eclipse.persistence.tools.workbench.utility.iterators.ChainIterator;

public abstract class AbstractNamedSchemaComponent 
	extends AbstractSchemaComponent
	implements MWNamedSchemaComponent
{
	/** Some components may be anonymous (types), in which case the name is null. */
	private volatile String name;
	
	/** Target namespace will always at least be "". */
	private volatile String namespaceUrl;
	
	
	// **************** Static methods ****************************************
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(AbstractNamedSchemaComponent.class);
		descriptor.getInheritancePolicy().setParentClass(AbstractSchemaComponent.class);
		
		descriptor.addDirectMapping("name", "name/text()");
		((XMLDirectMapping) descriptor.addDirectMapping("namespaceUrl", "namespace-url/text()")).setNullValue("");
		
		return descriptor;
	}
	
	
	// **************** Constructors ******************************************
	
	/** For Toplink Use Only */
	protected AbstractNamedSchemaComponent() {
		super();
	}
	
	protected /* private-protected */ AbstractNamedSchemaComponent(MWModel parent, String name) {
		super(parent);
		this.name = name;
	}
	
	protected /* private-protected */ AbstractNamedSchemaComponent(MWModel parent, String name, String namespace) {
		this(parent, name);
		this.namespaceUrl = namespace;
	}
	
	
	// **************** Initialization ****************************************
	
	protected void initialize() {
		super.initialize();
		this.namespaceUrl = "";
	}
	
	
	// **************** MWNamedSchemaComponent contract ***********************
	
	public String getName() {
		return this.name;
	}
	
	public String getNamespaceUrl() {
		return this.namespaceUrl;
	}
	
	public MWNamespace getTargetNamespace() {
		return this.getParentNamespace();
	}
	
	/**
	 * return an iterator of components from this one up to the top named component
	 */
	public Iterator namedComponentChain() {
		return new ChainIterator(this) {
			protected Object nextLink(Object currentLink) {
				return ((MWNamedSchemaComponent) currentLink).parentNamedComponent();
			}
		};
	}
	
	public boolean directlyOwns(MWNamedSchemaComponent nestedComponent) {
		return this.directlyOwnedComponents().contains(nestedComponent);
	}
	
	/** No-op implementation */
	public void addDirectlyOwnedComponentsTo(Collection directlyOwnedComponents) {}
	
	private Collection directlyOwnedComponents() {
		HashBag directlyOwnedComponents = new HashBag();
		this.addDirectlyOwnedComponentsTo(directlyOwnedComponents);
		return directlyOwnedComponents;
	}
	
	public String qName() {
		String qName = this.getName();
		String prefix = this.getSchema().namespacePrefixForUrl(this.getNamespaceUrl());
		
		if (prefix != null && ! prefix.equals("")) {
			qName = prefix + ":" + qName;
		}
		
		return qName;
	}
	
	
	// **************** SchemaModel contract **********************************
	
	protected void reloadInternal(XSObject schemaObject) {
		super.reloadInternal(schemaObject);
		if (schemaObject instanceof XSParticleDecl) {
			XSParticleDecl xsParticle = (XSParticleDecl)schemaObject;
			this.reloadName(xsParticle.getTerm().getName(), xsParticle.getTerm().getNamespace());
		} else {
			this.reloadName(schemaObject.getName(), schemaObject.getNamespace());
		}
	}
	
	protected void reloadName(String name, String namespace) {
		this.name = name;
		if (namespace != null) {
			this.namespaceUrl = namespace;
		} else {
			this.namespaceUrl = "";
		}
	}
	
	
	// **************** AbstractModel contract ********************************
	
	public void toString(StringBuffer sb) {
		sb.append("name = " + this.name);	
	}
}
