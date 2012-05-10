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
package org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWDataField;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWAbstractTransactionalPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWTransactionalPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWSchemaContextComponent;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXmlField;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXmlNode;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXpathContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXpathSpec;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.SchemaChange;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.CloneIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.FilteringIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;


public final class MWXmlPrimaryKeyPolicy 
	extends MWModel
	implements MWXmlNode, MWXpathContext
{
    // **************** Instance variables *********************
    
    /** Collection of primary keys */
    protected List primaryKeys; 
        public final static String PRIMARY_KEYS_COLLECTION = "primaryKeys";

    
    // **************** Constructors ******************************************
		
	/** Default constructor - for TopLink use only. */
	private MWXmlPrimaryKeyPolicy() {
		super();
	}
	
	MWXmlPrimaryKeyPolicy(MWAbstractTransactionalPolicy parent) {
		super(parent);
	}

    // **************** Initialization ****************************************
    
    protected void initialize(Node parent) {
        super.initialize(parent);
        this.primaryKeys = new Vector();
    }
    
    protected void addChildrenTo(List children) {
        super.addChildrenTo(children);
        synchronized (this.primaryKeys) { children.addAll(this.primaryKeys); }
    }

    
	// **************** Primary keys ******************************************
	
	public Iterator primaryKeys() {
		return new CloneIterator(this.primaryKeys);
	}
	   
    public int primaryKeysSize() {
        return this.primaryKeys.size();
    }
    
    public void clearPrimaryKeys() {
        this.primaryKeys.clear();
        this.fireCollectionChanged(PRIMARY_KEYS_COLLECTION);
    }

    /** Return an iterator of the string xpaths used as primary keys */
	public Iterator primaryKeyXpaths() {
		return new TransformationIterator(this.primaryKeys()) {
			protected Object transform(Object next) {
				return ((MWXmlField) next).getXpath();
			}
		};
	}
	
	public MWXmlField addPrimaryKey(String xpath) {
		MWXmlField xmlField = this.buildEmptyPrimaryKey();
		xmlField.setXpath(xpath);
		this.addPrimaryKey(xmlField);
		return xmlField;
	}
	
	public MWXmlField buildEmptyPrimaryKey() {
		return new MWXmlField(this);
	}
	
	public void addPrimaryKey(MWXmlField xmlField) {
		this.addItemToCollection(xmlField, this.primaryKeys, PRIMARY_KEYS_COLLECTION);
	}
	
	public void removePrimaryKey(MWXmlField xmlField) {
		this.removeItemFromCollection(xmlField, this.primaryKeys, PRIMARY_KEYS_COLLECTION);
	}
	
	
	// **************** MWXpathContext implementation  ************************
	
	public MWSchemaContextComponent schemaContext(MWXmlField xmlField) {
		return this.xmlDescriptor().getSchemaContext();
	}
	
	private MWXmlDescriptor xmlDescriptor() {
		return (MWXmlDescriptor)getParent().getParent();
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
	
	// **************** Model synchronization *********************************
	
	/** @see MWXmlNode#resolveXpaths() */
	public void resolveXpaths() {
		for (Iterator stream = this.primaryKeys(); stream.hasNext(); ) {
			((MWXmlNode) stream.next()).resolveXpaths();
		}
	}
	
	/** @see MWXmlNode#schemaChanged(SchemaChange) */
	public void schemaChanged(SchemaChange change) {
		for (Iterator stream = this.primaryKeys(); stream.hasNext(); ) {
			((MWXmlNode) stream.next()).schemaChanged(change);
		}
	}
	
    // **************** Runtime Conversion ************************************
    
    public void adjustRuntimeDescriptor(ClassDescriptor runtimeDescriptor) {        
        for (Iterator primaryKeys = primaryKeys(); primaryKeys.hasNext(); ) {
            runtimeDescriptor.addPrimaryKeyField(((MWDataField) primaryKeys.next()).runtimeField());
        }
    }

    
	// **************** TopLink methods ***************************************
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWXmlPrimaryKeyPolicy.class);
        		
		XMLCompositeCollectionMapping primaryKeysMapping = new XMLCompositeCollectionMapping();
		primaryKeysMapping.setReferenceClass(MWXmlField.class);
		primaryKeysMapping.setAttributeName("primaryKeys");
		primaryKeysMapping.setGetMethodName("getPrimaryKeysForTopLink");
		primaryKeysMapping.setSetMethodName("setPrimaryKeysForTopLink");
		primaryKeysMapping.setXPath("primary-keys/xml-field");
		primaryKeysMapping.useCollectionClass(java.util.Vector.class);
		descriptor.addMapping(primaryKeysMapping);
		
		return descriptor;
	}
	
	private List getPrimaryKeysForTopLink() {
		return CollectionTools.list(this.specifiedPrimaryKeys());
	}
	
	private Iterator specifiedPrimaryKeys() {
		return new FilteringIterator(this.primaryKeys()) {
			protected boolean accept(Object o) {
				return ((MWXmlField) o).isSpecified();
			}
		};
	}
	
	private void setPrimaryKeysForTopLink(List primaryKeys) {
		this.primaryKeys = primaryKeys;
	}
}
