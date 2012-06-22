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
package org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWEisDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWRootEisDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWMappingHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle.NodeReferenceScrubber;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWAbstractReferenceMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWAbstractTableReferenceMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.xml.MWEisInteraction;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWSchemaContextComponent;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXmlField;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXmlNode;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.SchemaChange;
import org.eclipse.persistence.tools.workbench.utility.iterators.CloneListIterator;
import org.eclipse.persistence.tools.workbench.utility.node.Node;
import org.eclipse.persistence.tools.workbench.utility.node.Problem;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;

public abstract class MWEisReferenceMapping 
	extends MWAbstractReferenceMapping
	implements MWXmlMapping, MWXmlReferenceMapping
{
	// **************** Variables *********************************************
	
	private List xmlFieldPairs;
		public final static String XML_FIELD_PAIRS_LIST = "xmlFieldPairs";
	
	private volatile MWEisInteraction selectionInteraction;
		public final static String SELECTION_INTERACTION_PROPERTY = "selectionInteraction";
	
	private volatile boolean maintainsBidirectionalRelationship;
		public final static String MAINTAINS_BIDIRECTIONAL_RELATIONSHIP_PROPERTY = "maintainsBidirectionalRelationship";
	
	private MWMappingHandle relationshipPartnerMappingHandle;
		public final static String RELATIONSHIP_PARTNER_MAPPING_PROPERTY = "relationshipPartnerMapping";
	
	
	// **************** Constructors ******************************************
	
	/** Default constructor - for TopLink use only */
	protected MWEisReferenceMapping() {
		super();
	}
	
	protected MWEisReferenceMapping(MWMappingDescriptor descriptor, MWClassAttribute attribute, String attributeName) {
		super(descriptor, attribute, attributeName);
	}
	
	
	// **************** Initialization ****************************************
	
	/** initialize persistent state */
	protected void initialize(Node parent) {
		super.initialize(parent);
		this.xmlFieldPairs = new Vector();
		this.relationshipPartnerMappingHandle = new MWMappingHandle(this, this.buildRelationshipPartnerMappingScrubber());
		if (this.requiresSelectionInteraction()) {
			this.selectionInteraction = new MWEisInteraction(this);
		}
	}
	
	/**
	 * return whether the mapping requires a selection interaction
	 * to always be present
	 */
	protected abstract boolean requiresSelectionInteraction();
	
	
	// **************** Reference descriptor **********************************
	
	public boolean descriptorIsValidReferenceDescriptor(MWDescriptor descriptor) {
		return ((MWEisDescriptor) descriptor).isRootDescriptor();
	}
	
	
	// **************** Xml field pairs ***************************************
	
	public int xmlFieldPairsSize() {
		return this.xmlFieldPairs.size();
	}
	
	public ListIterator xmlFieldPairs() {
		return new CloneListIterator(this.xmlFieldPairs);
	}
	
	public MWXmlFieldPair xmlFieldPairAt(int index) {
		return (MWXmlFieldPair) this.xmlFieldPairs.get(index);
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
	
	
	// **************** Maintain bidirectional relationship *******************
	
	public boolean maintainsBidirectionalRelationship() {
		return this.maintainsBidirectionalRelationship;
	}
	
	public void setMaintainsBidirectionalRelationship(boolean maintainsBidirectionalRelationship) {
		boolean old = this.maintainsBidirectionalRelationship;
		this.maintainsBidirectionalRelationship = maintainsBidirectionalRelationship;
		firePropertyChanged(MAINTAINS_BIDIRECTIONAL_RELATIONSHIP_PROPERTY, old, maintainsBidirectionalRelationship);
		
		if (maintainsBidirectionalRelationship == false) {
			this.setRelationshipPartnerMapping(null);
		}
	}
	
	
	// **************** Relationship partner mapping **************************
	
	public MWMapping getRelationshipPartnerMapping() {
		return this.relationshipPartnerMappingHandle.getMapping();
	}
	
	public void setRelationshipPartnerMapping(MWMapping relationshipPartnerMapping) {
		Object old = this.relationshipPartnerMappingHandle.getMapping();
		this.relationshipPartnerMappingHandle.setMapping(relationshipPartnerMapping);
		firePropertyChanged(RELATIONSHIP_PARTNER_MAPPING_PROPERTY, old, relationshipPartnerMapping);
	}
	
	public void setReferenceDescriptor(MWDescriptor newReferenceDescriptor) {
		if (getReferenceDescriptor() != newReferenceDescriptor) {
			setRelationshipPartnerMapping(null);
		}
		super.setReferenceDescriptor(newReferenceDescriptor);
	}
	
	public boolean isValidRelationshipPartner() {
		return true;
	}	
	
		
	// **************** Selection interaction ***************
	
	public MWEisInteraction getSelectionInteraction() {
		return this.selectionInteraction;
	}
	
	protected void setSelectionInteraction(MWEisInteraction selectionInteraction) {
		if (this.requiresSelectionInteraction() && (selectionInteraction == null)) {
			throw new NullPointerException();
		}
		Object old = this.selectionInteraction;
		this.selectionInteraction = selectionInteraction;
		this.firePropertyChanged(SELECTION_INTERACTION_PROPERTY, old, selectionInteraction);
	}
	
	
	// **************** MWXmlMapping contract *********************************
	
	public MWSchemaContextComponent schemaContext() {
		return this.eisDescriptor().getSchemaContext();
	}
	
	public MWXmlField firstMappedXmlField() {
		for (Iterator stream = this.xmlFieldPairs(); stream.hasNext(); ) {
			MWXmlField xmlField = ((MWXmlFieldPair) stream.next()).getSourceXmlField();
			
			if (xmlField.isSpecified()) {
				return xmlField;
			}
		}
		
		return null;
	}
	
	public void addWrittenFieldsTo(Collection writtenXpaths) {
		if (this.isReadOnly()) {
			return;
		}
		for (Iterator stream = this.xmlFieldPairs(); stream.hasNext(); ) {
			MWXmlField xmlField = ((MWXmlFieldPair) stream.next()).getSourceXmlField();
			
			if (xmlField.isSpecified()) {
				writtenXpaths.add(xmlField);
			}
		}
	}
	
	
	// **************** MWXpathContext contract (for field pairs) *************
	
	/** Return true if a source field may use a collection xpath */
	public abstract boolean sourceFieldMayUseCollectionXpath();
	
	
	// **************** Convenience *******************************************
	
	protected MWEisDescriptor eisDescriptor() {
		return (MWEisDescriptor) getParent();
	}
	
	protected MWEisDescriptor referenceEisDescriptor() {
		return (this.getReferenceDescriptor() == null) ? null : (MWEisDescriptor) this.getReferenceDescriptor();
	}
	
	protected MWRootEisDescriptor referenceRootEisDescriptor() {
		return (this.referenceEisDescriptor() == null || ! this.referenceEisDescriptor().isRootDescriptor()) ? 
			null : (MWRootEisDescriptor) this.referenceEisDescriptor();
	}
	
	public MWEisDescriptor referenceDescriptor() {
		return (this.getReferenceDescriptor() == null) ? null : (MWEisDescriptor) this.getReferenceDescriptor();
	}
		
	// *********** morphing ************
	
	protected void initializeFromMWEisReferenceMapping(MWEisReferenceMapping oldMapping) {
		super.initializeFromMWEisReferenceMapping(oldMapping);
		
		for (Iterator stream = oldMapping.xmlFieldPairs(); stream.hasNext(); ) {
			MWXmlFieldPair fieldPair = (MWXmlFieldPair) stream.next();
			this.addFieldPair(fieldPair.getSourceXmlField().getXpath(), fieldPair.getTargetXmlField().getXpath());
		}
		
		this.setMaintainsBidirectionalRelationship(oldMapping.maintainsBidirectionalRelationship());
		this.setRelationshipPartnerMapping(oldMapping.getRelationshipPartnerMapping());
		MWEisInteraction interaction = oldMapping.getSelectionInteraction();
		if (this.requiresSelectionInteraction() && (interaction == null)) {
			this.setSelectionInteraction(new MWEisInteraction(this));
		} else {
			this.setSelectionInteraction(interaction);
		}
	}
	
	
	//********* model synchronization support *********
	
	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		synchronized (this.xmlFieldPairs) { children.addAll(this.xmlFieldPairs); }
		children.add(this.relationshipPartnerMappingHandle);
		
		if (this.requiresSelectionInteraction()) {
			children.add(this.selectionInteraction);
		} else {
			if (this.selectionInteraction != null) {
				children.add(this.selectionInteraction);
			}
		}
	}
	
	private NodeReferenceScrubber buildRelationshipPartnerMappingScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWEisReferenceMapping.this.setRelationshipPartnerMapping(null);
			}
			public String toString() {
				return "MWEisReferenceMapping.buildRelationshipPartnerMappingScrubber()";
			}
		};
	}

	public void mappingReplaced(MWMapping oldMapping, MWMapping newMapping) {
		super.mappingReplaced(oldMapping, newMapping);
		if (oldMapping == getRelationshipPartnerMapping()) {
			setRelationshipPartnerMapping(newMapping);
		}
	}
	
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
	

	// ************** Problem Handling **************
	
	protected void addProblemsTo(List newProblems) {
		super.addProblemsTo(newProblems);
		this.checkDuplicateSourceXpath(newProblems);
		this.checkDuplicateTargetXpath(newProblems);
		this.checkRelationshipPartner(newProblems);
	}
	
	protected String referenceDescriptorInvalidProblemString() {
		return ProblemConstants.MAPPING_REFERENCE_DESCRIPTOR_NOT_ROOT;
	}
	
	private void checkDuplicateSourceXpath(List newProblems) {
		for (Iterator stream = this.xmlFieldPairs(); stream.hasNext(); ) {
			MWXmlFieldPair fieldPair = (MWXmlFieldPair) stream.next();
			String sourceXpath = fieldPair.getSourceXmlField().getXpath();
			
			if (fieldPair.duplicateSourceXpath(sourceXpath)) {
				Problem problem = this.buildProblem(ProblemConstants.MAPPING_SOURCE_XPATH_DUPLICATE, sourceXpath);
				
				if (! newProblems.contains(problem)) {
					newProblems.add(problem);
				}
			}
		}
	}
	
	private void checkDuplicateTargetXpath(List newProblems) {
		for (Iterator stream = this.xmlFieldPairs(); stream.hasNext(); ) {
			MWXmlFieldPair fieldPair = (MWXmlFieldPair) stream.next();
			String targetXpath = fieldPair.getTargetXmlField().getXpath();
			
			if (fieldPair.duplicateTargetXpath(targetXpath)) {
				Problem problem = this.buildProblem(ProblemConstants.MAPPING_TARGET_XPATH_DUPLICATE, targetXpath);
				
				if (! newProblems.contains(problem)) {
					newProblems.add(problem);
				}
			}
		}
	}
	
	private void checkRelationshipPartner(List newProblems) {
		if (this.maintainsBidirectionalRelationship()) {
			MWMapping partnerMapping = this.getRelationshipPartnerMapping();
			if (partnerMapping == null) {
				newProblems.add(this.buildProblem(ProblemConstants.MAPPING_EIS_RELATIONSHIP_PARTNER_NOT_SPECIFIED));
			} else {
				if ( ! partnerMapping.isValidRelationshipPartner()) {
					newProblems.add(this.buildProblem(ProblemConstants.MAPPING_EIS_RELATIONSHIP_PARTNER_INVALID));
				}
				if ( ! (partnerMapping.maintainsBidirectionalRelationship() && partnerMapping.getRelationshipPartnerMapping() == this)) {
					newProblems.add(buildProblem(ProblemConstants.MAPPING_EIS_RELATIONSHIP_PARTNER_NOT_MUTUAL));
				}
			}
		}
	}
	
	/** 
	 * Return true if the source xml field has a duplicate xpath among my source xml fields.
	 * (Used in rules and in ui validation) 
	 */
	public boolean duplicatedSourceXmlField(MWXmlField sourceXmlField) {
		if (sourceXmlField.isSpecified()) {
			for (Iterator stream = this.xmlFieldPairs(); stream.hasNext(); ) {
				MWXmlField nextSourceXmlField = ((MWXmlFieldPair) stream.next()).getSourceXmlField();
				
				if (sourceXmlField != nextSourceXmlField && sourceXmlField.getXpath().equals(nextSourceXmlField.getXpath())) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	/** 
	 * Return true if the target xml field has a duplicate xpath among my target xml fields.
	 * (Used in rules and in ui validation) 
	 */
	public boolean duplicatedTargetXmlField(MWXmlField targetXmlField) {
		if (targetXmlField.isSpecified()) {
			for (Iterator stream = this.xmlFieldPairs(); stream.hasNext(); ) {
				MWXmlField nextTargetXmlField = ((MWXmlFieldPair) stream.next()).getTargetXmlField();
				
				if (targetXmlField != nextTargetXmlField && targetXmlField.getXpath().equals(nextTargetXmlField.getXpath())) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	
	
	// **************** Runtime Conversion ***************
	
	public DatabaseMapping runtimeMapping() {
		ForeignReferenceMapping mapping = (ForeignReferenceMapping) super.runtimeMapping();
		
		if (this.maintainsBidirectionalRelationship() && this.getRelationshipPartnerMapping() != null) {
			mapping.setRelationshipPartnerAttributeName(this.getRelationshipPartnerMapping().getName());
		}

		MWEisInteraction interaction = this.getSelectionInteraction();
		if (this.requiresSelectionInteraction()) {
			mapping.setSelectionCall(interaction.runtimeInteraction());
		} else {
			if ((interaction != null) && interaction.isSpecified()) {
				mapping.setSelectionCall(interaction.runtimeInteraction());
			}
		}
		return mapping;
	}
	
	
	//***************** TopLink methods ***************************************
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWEisReferenceMapping.class);
		descriptor.getInheritancePolicy().setParentClass(MWAbstractReferenceMapping.class);
		
		XMLCompositeCollectionMapping xpathFieldPairsListMapping = new XMLCompositeCollectionMapping();
		xpathFieldPairsListMapping.setAttributeName("xmlFieldPairs");
		xpathFieldPairsListMapping.setReferenceClass(MWXmlFieldPair.class);
		xpathFieldPairsListMapping.setXPath("xml-field-pairs/field-pair");
		descriptor.addMapping(xpathFieldPairsListMapping);
		
		XMLDirectMapping mbdrMapping = (XMLDirectMapping) descriptor.addDirectMapping("maintainsBidirectionalRelationship", "maintains-bidirectional-relationship/text()");
		mbdrMapping.setNullValue(Boolean.FALSE);
		
		XMLCompositeObjectMapping relationshipPartnerMapping = new XMLCompositeObjectMapping();
		relationshipPartnerMapping.setAttributeName("relationshipPartnerMappingHandle");
		relationshipPartnerMapping.setGetMethodName("getRelationshipPartnerMappingHandleForTopLink");
		relationshipPartnerMapping.setSetMethodName("setRelationshipPartnerMappingHandleForTopLink");
		relationshipPartnerMapping.setReferenceClass(MWMappingHandle.class);
		relationshipPartnerMapping.setXPath("relationship-partner-mapping-handle");
		descriptor.addMapping(relationshipPartnerMapping);
		
		XMLCompositeObjectMapping selectionInteractionMapping = new XMLCompositeObjectMapping();
		selectionInteractionMapping.setAttributeName("selectionInteraction");
		selectionInteractionMapping.setReferenceClass(MWEisInteraction.class);
		selectionInteractionMapping.setXPath("selection-interaction");
		descriptor.addMapping(selectionInteractionMapping);
		
		return descriptor;	
	}
	
	/**
	 * check for null
	 */
	private MWMappingHandle getRelationshipPartnerMappingHandleForTopLink() {
		return (this.relationshipPartnerMappingHandle.getMapping() == null) ? null : this.relationshipPartnerMappingHandle;
	}
	private void setRelationshipPartnerMappingHandleForTopLink(MWMappingHandle handle) {
		NodeReferenceScrubber scrubber = this.buildRelationshipPartnerMappingScrubber();
		this.relationshipPartnerMappingHandle = ((handle == null) ? new MWMappingHandle(this, scrubber) : handle.setScrubber(scrubber));
	}

}
