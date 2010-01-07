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
package org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWDataField;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWOXDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWXmlDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWDescriptorHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle.NodeReferenceScrubber;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWAbstractReferenceMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWReferenceMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWSchemaContextComponent;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXmlField;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXmlNode;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXpathContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXpathSpec;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.SchemaChange;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.CloneListIterator;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.mappings.AggregateMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLObjectReferenceMapping;

public abstract class MWAbstractXmlReferenceMapping extends MWAbstractReferenceMapping implements MWXpathContext, MWXmlReferenceMapping {

	private List<MWXmlFieldPair> xmlFieldPairs;

			
	// **************** Constructors ******************************************

	/**
	 * Default constructor, TopLink use only.
	 */
	protected MWAbstractXmlReferenceMapping() {
		super();
	}
	
	MWAbstractXmlReferenceMapping(MWXmlDescriptor parent, MWClassAttribute attribute, String name) {
		super(parent, attribute, name);
	}

	// **************** Initialization ****************************************
	@Override
	protected void initialize() {
		super.initialize();
		this.xmlFieldPairs = new Vector<MWXmlFieldPair>();
	}
	
	@Override
	public void initializeFromMWXmlObjectReferenceMapping(MWXmlObjectReferenceMapping oldMapping) {
		super.initializeFromMWXmlObjectReferenceMapping(oldMapping);
		this.setReferenceDescriptor(oldMapping.getReferenceDescriptor());
	}
	
	@Override
	public void initializeFromMWXmlCollectionReferenceMapping(MWXmlCollectionReferenceMapping oldMapping) {
		super.initializeFromMWXmlCollectionReferenceMapping(oldMapping);
		this.setReferenceDescriptor(oldMapping.getReferenceDescriptor());
	}
	
	//********* model synchronization support *********
	
	@Override
	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		synchronized (this.xmlFieldPairs) { children.addAll(this.xmlFieldPairs); }
	}
	
	// **************** Model synchronization *********************************
	
	/** @see MWXmlNode#resolveXpaths */
	public void resolveXpaths() {
		for (Iterator stream = this.xmlFieldPairs(); stream.hasNext(); ) {
			((MWXmlNode) stream.next()).resolveXpaths();
		}
	}
	
	/** @see MWXmlNode#schemaChanged(SchemaChange) */
	public void schemaChanged(SchemaChange change) {
		for (Iterator stream = this.xmlFieldPairs(); stream.hasNext(); ) {
			((MWXmlNode) stream.next()).schemaChanged(change);
		}
	}

	// **************** TopLink methods ***************************************
	@SuppressWarnings("deprecation")
	public static XMLDescriptor buildDescriptor() {	
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWAbstractXmlReferenceMapping.class);
		descriptor.getInheritancePolicy().setParentClass(MWAbstractReferenceMapping.class);
	
		XMLCompositeCollectionMapping xpathFieldPairsListMapping = new XMLCompositeCollectionMapping();
		xpathFieldPairsListMapping.setAttributeName("xmlFieldPairs");
		xpathFieldPairsListMapping.setReferenceClass(MWXmlFieldPair.class);
		xpathFieldPairsListMapping.setXPath("xml-field-pairs/field-pair");
		descriptor.addMapping(xpathFieldPairsListMapping);

		return descriptor;	
	}
	
	// **************** MWXmlMapping contract *********************************
	
	public MWSchemaContextComponent schemaContext() {
		return this.xmlDescriptor().getSchemaContext();
	}
	
	public MWXmlField firstMappedXmlField() {
		MWXmlFieldPair firstFieldPair = xmlFieldPairAt(0); 
		if (firstFieldPair != null && firstFieldPair.getSourceXmlField() != null && firstFieldPair.getSourceXmlField().isResolved()) {
			return firstFieldPair.getSourceXmlField();
		}
		else {
			return null;
		}
	}
	
	@Override
	public void addWrittenFieldsTo(Collection writtenXpaths) {
		if (!this.isReadOnly()) {
			Iterator<MWXmlFieldPair> fielPairIter = xmlFieldPairs();
			while (fielPairIter.hasNext()) {
				MWXmlField field = fielPairIter.next().getTargetXmlField();
				if (!field.getXpath().equals("")) {
					writtenXpaths.add(field);					
				}
			}
		}
	}
	
	// **************** MWXpathContext implementation  ************************
	
	public MWSchemaContextComponent schemaContext(MWXmlField xmlField) {
		return this.xmlDescriptor().getSchemaContext();
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
	
	// **************** MWReferenceMapping contract ***************************
		
	public boolean descriptorIsValidReferenceDescriptor(MWDescriptor descriptor) {
		return true;
	}
	
	
	// **************** MWAbstractReferenceMapping contract *******************
	
	@Override
	protected String referenceDescriptorInvalidProblemString() {
		return ProblemConstants.MAPPING_REFERENCE_DESCRIPTOR_NOT_ROOT;
	}
	
	public MWXmlDescriptor referenceDescriptor() {
		return (MWXmlDescriptor)getReferenceDescriptor();
	}
	
	// **************** Convenience *******************************************
	
	protected MWXmlDescriptor xmlDescriptor() {
		return (MWXmlDescriptor) this.getParent();
	}

	// **************** Xml field pairs ***************************************
	
	public int xmlFieldPairsSize() {
		return this.xmlFieldPairs.size();
	}
	
	public ListIterator<MWXmlFieldPair> xmlFieldPairs() {
		return new CloneListIterator(this.xmlFieldPairs);
	}
	
	public MWXmlFieldPair xmlFieldPairAt(int index) {
		if (index < this.xmlFieldPairsSize()) {
			return this.xmlFieldPairs.get(index);
		}
		return null;
	}
	
	public MWXmlFieldPair addFieldPair(String sourceXpath, String targetXpath) {
		MWXmlFieldPair xmlFieldPair = new MWXmlFieldPair(this, sourceXpath, targetXpath);
		this.addFieldPair(xmlFieldPair);
		return xmlFieldPair;
	}
	
	public void addFieldPair(MWXmlFieldPair xmlFieldPair) {
		this.addItemToList(this.xmlFieldPairsSize(), xmlFieldPair, this.xmlFieldPairs, XML_FIELD_PAIRS_LIST);		
	}
	
	/** Builds an empty field pair, but does not add it */
	public MWXmlFieldPair buildEmptyFieldPair() {
		return new MWXmlFieldPair(this);
	}
	
	public void removeXmlFieldPair(MWXmlFieldPair xmlFieldPair) {
		this.removeNodeFromList(this.xmlFieldPairs.indexOf(xmlFieldPair), this.xmlFieldPairs, XML_FIELD_PAIRS_LIST);
	}
	
	public void clearXmlFieldPairs() {
		this.clearList(this.xmlFieldPairs, XML_FIELD_PAIRS_LIST);
	}

	public boolean sourceFieldMayUseCollectionXpath() {
		return false;
	}

	@Override
	public DatabaseMapping runtimeMapping() {
		XMLObjectReferenceMapping runtimeMapping = (XMLObjectReferenceMapping) this.buildRuntimeMapping();		
		
		runtimeMapping.setAttributeName(this.getInstanceVariable().getName());
    	
		if (this.usesMethodAccessing()) {
			if (getGetMethod() != null) {
				runtimeMapping.setGetMethodName(this.getGetMethod().getName());
			}		
			if (getSetMethod() != null) {
				runtimeMapping.setSetMethodName(this.getSetMethod().getName());
			}
		}
		runtimeMapping.setIsReadOnly(this.isReadOnly()); 
		if (getReferenceDescriptor() != null) {
			runtimeMapping.setReferenceClassName(getReferenceDescriptor().getMWClass().getName());
		}
		Iterator<MWXmlFieldPair> fieldpairsIter = this.xmlFieldPairs();
		while (fieldpairsIter.hasNext()) {
			MWXmlFieldPair pair = fieldpairsIter.next();
			runtimeMapping.addSourceToTargetKeyFieldAssociation(pair.getSourceXmlField().getXpath(), pair.getTargetXmlField().getXpath());
		}
		
		return runtimeMapping;
	}
			
	//********************** Validation ***************************************
	
	@Override
	protected void addProblemsTo(List newProblems) {
		super.addProblemsTo(newProblems);
		
		checkXmlFieldPairsNotZero(newProblems);
		checkTargetPrimaryKey(newProblems);
	}
	
	protected void checkXmlFieldPairsNotZero(List newProblems) {
		if (this.xmlFieldPairsSize() <= 0) {
			newProblems.add(this.buildProblem(ProblemConstants.MAPPING_NO_XML_FIELD_PAIRS_SPECIFIED));
		}
	}
	
	@Override
	protected void checkReferenceDescriptorCachIsolation(List newProblems) {
		// doesn't apply to OX projects
	}

	@Override
	protected void checkReferenceDescriptor(List newProblems)	{
		MWDescriptor refDescriptor = this.getReferenceDescriptor();
		if (refDescriptor == null) {
			newProblems.add(this.buildProblem(ProblemConstants.MAPPING_REFERENCE_DESCRIPTOR_NOT_SPECIFIED));
		} else {
			if ( ! refDescriptor.isActive()) {
				newProblems.add(this.buildProblem(ProblemConstants.MAPPING_REFERENCE_DESCRIPTOR_IS_INACTIVE, 
					this.getInstanceVariable().getName(), this.getReferenceDescriptor().getMWClass().shortName()));
			}
			this.checkReferenceDescriptorCachIsolation(newProblems);
		}
	}

	protected void checkTargetPrimaryKey(List newProblems) {
		Iterator<MWXmlFieldPair> fieldPairs = xmlFieldPairs();
		 
		while(fieldPairs.hasNext()) {
			MWXmlFieldPair pair = fieldPairs.next();
			MWXmlField targetField = pair.getTargetXmlField();
			MWOXDescriptor refDesc = (MWOXDescriptor)getReferenceDescriptor();
			Collection pkFieldNames = new Vector(refDesc.primaryKeyPolicy().primaryKeysSize());
			Iterator pkFieldIter = refDesc.primaryKeyPolicy().primaryKeys();
			while (pkFieldIter.hasNext()) {
				pkFieldNames.add(((MWXmlField)pkFieldIter.next()).fieldName());
			}
			
			if (pkFieldNames.contains(targetField.fieldName())) {
				continue;
			} else {
				newProblems.add(this.buildProblem(ProblemConstants.MAPPING_TARGET_NOT_PRIMARY_KEY_ON_REFERENCE_DESCRIPTOR, targetField.fieldName()));
			}
		}
		
	}
}
