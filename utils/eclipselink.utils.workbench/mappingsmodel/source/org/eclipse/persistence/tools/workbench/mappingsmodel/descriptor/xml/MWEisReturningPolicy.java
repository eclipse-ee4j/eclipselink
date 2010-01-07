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
package org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWDataField;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWReturningPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWSchemaContextComponent;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXmlField;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXpathContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXpathSpec;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.CloneIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.FilteringIterator;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;

public final class MWEisReturningPolicy
	extends MWReturningPolicy
	implements MWXpathContext
{
	/** The implementation of the implied collection on the superclass */
	private Collection updateFields;
	
	
	// ********** constructors/initialization **********

	/** Default constructor - for TopLink use only */
	private MWEisReturningPolicy() {
		super();
	}
	
	MWEisReturningPolicy(MWEisDescriptor parent) {
		super(parent);
	}
	
	protected void initialize(Node parent) {
		super.initialize(parent);
		this.updateFields = new Vector();
	}
	
	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		synchronized (this.updateFields) { children.addAll(this.updateFields); }
	}
	
	
	// **************** Insert fields *****************************************
	
	public MWEisReturningPolicyInsertFieldReturnOnlyFlag addInsertFieldReadOnlyFlag(String xpath) {
		MWEisReturningPolicyInsertFieldReturnOnlyFlag insertFieldReturnOnlyFlag = 
			this.buildEmptyInsertFieldReadOnlyFlag();
		insertFieldReturnOnlyFlag.getXmlField().setXpath(xpath);
		this.addInsertFieldReadOnlyFlag(insertFieldReturnOnlyFlag);
		return insertFieldReturnOnlyFlag;
	}
	
	public MWEisReturningPolicyInsertFieldReturnOnlyFlag buildEmptyInsertFieldReadOnlyFlag() {
		return new MWEisReturningPolicyInsertFieldReturnOnlyFlag(this);
	}
	
	
	// **************** Update fields *****************************************
	
	public Iterator updateFields() {
		return new CloneIterator(this.updateFields) {
			protected void remove(Object current) {
				MWEisReturningPolicy.this.removeUpdateField((MWXmlField) current);
			}
		};
	}
	
	public int updateFieldsSize() {
		return this.updateFields.size();
	}
	
	public MWXmlField addUpdateField(String xpath) {
		MWXmlField updateField = this.buildEmptyUpdateField();
		updateField.setXpath(xpath);
		this.addUpdateField(updateField);
		return updateField;
	}
	
	public void addUpdateField(MWXmlField updateField) {
		this.addItemToCollection(updateField, this.updateFields, UPDATE_FIELDS_COLLECTION);
	}
	
	public MWXmlField buildEmptyUpdateField() {
		return new MWXmlField(this);
	}

	public void removeUpdateField(MWDataField updateField) {
		this.removeUpdateField((MWXmlField) updateField);
	}

	public void removeUpdateField(MWXmlField updateField) {
		this.removeItemFromCollection(updateField, this.updateFields, UPDATE_FIELDS_COLLECTION);
	}


	// ********** MWXpathContext implementation **********
	
	public MWSchemaContextComponent schemaContext(MWXmlField xmlField) {
		return this.eisDescriptor().getSchemaContext();
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
	
	
	// ********** Convenience **********
	
	public MWEisDescriptor eisDescriptor() {
		return (MWEisDescriptor) this.getParent();
	}
	
		
	// ********** TopLink methods **********
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWEisReturningPolicy.class);

		XMLCompositeCollectionMapping insertFieldReturnOnlyFlagsMapping = new XMLCompositeCollectionMapping();
		insertFieldReturnOnlyFlagsMapping.setAttributeName("insertFieldReturnOnlyFlags");
		insertFieldReturnOnlyFlagsMapping.setGetMethodName("getInsertFieldReturnOnlyFlagsForTopLink");
		insertFieldReturnOnlyFlagsMapping.setSetMethodName("setInsertFieldReturnOnlyFlagsForTopLink");
		insertFieldReturnOnlyFlagsMapping.setReferenceClass(MWEisReturningPolicyInsertFieldReturnOnlyFlag.class);
		insertFieldReturnOnlyFlagsMapping.setXPath("insert-field-read-only-flags/insert-field-read-only-flag");
		descriptor.addMapping(insertFieldReturnOnlyFlagsMapping);
		
		XMLCompositeCollectionMapping updateFieldsMapping = new XMLCompositeCollectionMapping();
		updateFieldsMapping.setAttributeName("updateFields");
		updateFieldsMapping.setGetMethodName("getUpdateFieldsForTopLink");
		updateFieldsMapping.setSetMethodName("setUpdateFieldsForTopLink");
		updateFieldsMapping.setReferenceClass(MWXmlField.class);
		updateFieldsMapping.setXPath("update-fields/update-field");
		descriptor.addMapping(updateFieldsMapping);
				
		return descriptor;
	}

	/**
	 * "override" this method to write out only the fields that have an xpath
	 */
	private Collection getInsertFieldReturnOnlyFlagsForTopLink() {
		return CollectionTools.sortedSet(this.specifiedInsertFieldReturnOnlyFlags());
	}

	private Iterator specifiedInsertFieldReturnOnlyFlags() {
		return new FilteringIterator(this.insertFieldReturnOnlyFlags()) {
			protected boolean accept(Object o) {
				return ((MWEisReturningPolicyInsertFieldReturnOnlyFlag) o).getXmlField().isSpecified();
			}
		};
	}

	private Collection getUpdateFieldsForTopLink() {
		return CollectionTools.sortedSet(this.specifiedUpdateFields());
	}

	private Iterator specifiedUpdateFields() {
		return new FilteringIterator(this.updateFields()) {
			protected boolean accept(Object o) {
				return ((MWXmlField) o).isSpecified();
			}
		};
	}
	
	private void setUpdateFieldsForTopLink(Collection updateFields) {
		this.updateFields = updateFields;
	}
}
