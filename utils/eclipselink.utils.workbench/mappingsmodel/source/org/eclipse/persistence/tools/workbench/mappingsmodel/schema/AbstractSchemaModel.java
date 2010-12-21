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

import java.util.Iterator;

import org.apache.xerces.xs.XSObject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.QName;
import org.eclipse.persistence.tools.workbench.utility.iterators.NullIterator;


public abstract class AbstractSchemaModel
	extends MWModel
	implements MWSchemaModel
{
	// **************** Variables *********************************************
	
	/** Used to prevent infinite loops in reload cycle */
	private boolean reloadInProgress;
	
	
	// **************** Constructors ******************************************
	
	/** Default Constructor - Toplink Use Only */
	protected AbstractSchemaModel() {
		super();
	}
	
	AbstractSchemaModel(MWModel parent) {
		super(parent);
	}
	
	
	// **************** Public ************************************************
	
	/**
	 * Do NOT override this method.
	 * Every schema model object (except Namespace) should have a schema model parent.
	 */
	public final AbstractSchemaModel getSchemaModelParent() {
        //use instanceof instead of catching ClassCastException
        //for performance reasons while importing a schema
        if (getParent() instanceof AbstractSchemaModel) {
            return (AbstractSchemaModel) this.getParent();
        }
		return null;
	}
	
	
	// **************** MWSchemaModel contract ********************************
	
	/**
	 * Do NOT override this method.
	 * Every schema object should be able to return its schema.
	 */
	public final MWXmlSchema getSchema() {
		return this.getParentNamespace().getSchema();
	}
	
	/**
	 * Do NOT override this method.
	 * Every schema object should be able to return its parent namespace.
	 */
	public final MWNamespace getParentNamespace() {
		if (this.getSchemaModelParent() == null) {
			try {
				return (MWNamespace) this.getParent();
			} catch (ClassCastException ex) {
				throw new IllegalStateException("This object is missing a parent namespace: " + this.toString());
			}
		} else {
			return this.getSchemaModelParent().getParentNamespace();
		}
	}
	
	/** default implementation */
	public Iterator structuralComponents() {
		return NullIterator.instance();
	}
	
	/** default implementation */
	public Iterator descriptorContextComponents() {
		return NullIterator.instance();
	}
	
	/** default implementation */
	public Iterator xpathComponents() {
		return NullIterator.instance();
	}
	
	public MWNamedSchemaComponent parentNamedComponent() {
		MWSchemaModel parent = this.getSchemaModelParent();
		
		if (parent == null) {
			return null;
		}
		else {
			MWNamedSchemaComponent namedParent;
			
			try {
				namedParent = (MWNamedSchemaComponent) parent;
			}
			catch (ClassCastException cce) {
				return parent.parentNamedComponent();
			}
			
			return (namedParent.getName() == null) ? namedParent.parentNamedComponent() : namedParent;
		}
	}
	
	MWXpathableSchemaComponent parentXPathableComponent() {
		try {
			return (MWXpathableSchemaComponent) this.parentNamedComponent();
		}
		catch (ClassCastException cce) {
			return null;
		}
	}
	
	/** default implementation */
	public MWNamedSchemaComponent nestedNamedComponent(QName qName) {
		if (qName.getComponentType() == QName.ATTRIBUTE_TYPE) {
			return this.nestedAttribute(qName.getNamespaceURI(), qName.getLocalName());
		}
		else if (qName.getComponentType() == QName.ELEMENT_TYPE) {
			return this.nestedElement(qName.getNamespaceURI(), qName.getLocalName());
		}
		else {
			return null;
		}
	}
	
	/** default implementation */
	public MWAttributeDeclaration nestedAttribute(String namespaceUrl, String attributeName) {
		return null;
	}
	
	/** default implementation */
	public MWElementDeclaration nestedElement(String namespaceUrl, String elementName) {
		return null;
	}
	
	/** default implementation */
	public int totalElementCount() {
		return 0;
	}
	
	
	// **************** SchemaModel contract **********************************
	
	public final void reload(XSObject schemaObject) {
		if (this.reloadInProgress) {
			return;
		}
		
		this.reloadInProgress = true;
		this.reloadInternal(schemaObject);
		this.reloadInProgress = false;
		this.fireStateChanged();
	}
	
	protected void reloadInternal(XSObject schemaObject) {
		// subclasses must call super.reloadInternal(XSDNode)
		// no op
	}
	
	public void resolveReferences() {
		// subclasses must call super.resolveReferences()
		// no op
	}
}
