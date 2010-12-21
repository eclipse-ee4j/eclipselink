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

import java.util.Iterator;
import java.util.List;

import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWEisDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWProxyIndirectionMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.xml.MWEisInteraction;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.xml.MWEisQueryManager;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.xml.MWEisInteraction.ArgumentPair;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXmlField;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.eis.mappings.EISOneToOneMapping;
import org.eclipse.persistence.internal.indirection.ProxyIndirectionPolicy;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;

public final class MWEisOneToOneMapping 
	extends MWEisReferenceMapping
    implements MWProxyIndirectionMapping
{
	// **************** Variables *********************************************
	private volatile boolean useDescriptorReadObjectInteraction;
		public final static String USE_DESCRIPTOR_READ_OBJECT_INTERACTION_PROPERTY = "useDescriptorReadObjectInteraction";
	
	
	// **************** Constructors ***************
	
	/** Default constructor - for TopLink use only */
	private MWEisOneToOneMapping() {
		super();
	}
	
	public MWEisOneToOneMapping(MWEisDescriptor parent, MWClassAttribute attribute, String name) {
		super(parent, attribute, name);
	}
	
	
	// **************** Initialization ****************************************
	
	/** initialize persistent state */
	protected void initialize(Node parent) {
		super.initialize(parent);
		this.useDescriptorReadObjectInteraction = true;
	}
	
	protected void initialize(MWClassAttribute attribute, String name) {
		super.initialize(attribute, name);
		if (!getInstanceVariable().isValueHolder() && getInstanceVariable().getType().isInterface()) {
			this.indirectionType = PROXY_INDIRECTION;
		}
	}
		
	/**
	 * 1:1 mappings don't have a selection interaction when they
	 * use the descriptor read object interaction
	 */
	protected boolean requiresSelectionInteraction() {
		return false;
	}
	
	
	// **************** Selection interaction *********************************
	
	public boolean usesDescriptorReadObjectInteraction() {
		return this.useDescriptorReadObjectInteraction;
	}
	
	public void setUseDescriptorReadObjectInteraction(boolean useDescriptorReadObjectInteraction) {
		boolean old = this.useDescriptorReadObjectInteraction;
		this.useDescriptorReadObjectInteraction = useDescriptorReadObjectInteraction;
		if (old != useDescriptorReadObjectInteraction) {
			this.firePropertyChanged(USE_DESCRIPTOR_READ_OBJECT_INTERACTION_PROPERTY, old, useDescriptorReadObjectInteraction);
			// clear the selection interaction if the new value is true
			this.setSelectionInteraction(useDescriptorReadObjectInteraction ? null : new MWEisInteraction(this));
		}
	}
	
	
	// **************** Convenience *******************************************
	
	private MWEisInteraction referenceDescriptorReadInteraction() {
		return (this.referenceRootEisDescriptor() == null) ? 
			null
		: 
			((MWEisQueryManager) this.referenceRootEisDescriptor().getQueryManager()).getReadObjectInteraction();
	}
	
	
	// **************** MWXpathContext contract (for field pairs) *************
	
	/** 
	 * Return true if a source field may use a collection xpath
	 * @see MWEisReferenceMapping#sourceFieldMayUseCollectionXpath()
	 */
	public boolean sourceFieldMayUseCollectionXpath() {
		return false;
	}
	
    
    // *********** MWProxyIndirectionMapping implementation ***********
    
    public boolean usesProxyIndirection() {
        return getIndirectionType() == PROXY_INDIRECTION;
    }

    public void setUseProxyIndirection() {
        setIndirectionType(PROXY_INDIRECTION);  
    }
    
	// **************** Morphing **********************************************
	
	protected void initializeOn(MWMapping newMapping) {
		newMapping.initializeFromMWEisOneToOneMapping(this);
	}
	
	
	// ************** Problem handling ****************************************
	
	protected void addProblemsTo(List newProblems) {
		super.addProblemsTo(newProblems);
		
		this.checkFieldPairs(newProblems);
		this.checkReferenceDescriptorReadInteraction(newProblems);
		this.checkFieldPairsAndReadInteractionArguments(newProblems);
		this.addUsesIndirectionWhileMaintainsBiDirectionalRelationship(newProblems);
	}
	
	private void checkFieldPairs(List newProblems) {
		// the mapping must have field pairs unless 
		// 1) there is a selection interaction specified -AND-
		// 2) the mapping is read only
		if ((this.isReadOnly() && this.getSelectionInteraction() == null) || this.xmlFieldPairsSize() == 0) {
			newProblems.add(this.buildProblem(ProblemConstants.MAPPING_1_TO_1_FIELD_PAIRS_NOT_SPECIFIED));
		}
	}
	
	private void checkReferenceDescriptorReadInteraction(List newProblems) {
		// if the mapping does not specify a selection interaction, the reference descriptor must
		// have a read object interaction specified
		if (
			this.usesDescriptorReadObjectInteraction() 
			&& this.referenceRootEisDescriptor() != null 
			&& this.referenceDescriptorReadInteraction().getFunctionName() == null
		) {
			newProblems.add(this.buildProblem(ProblemConstants.MAPPING_REFERENCE_DESCRIPTOR_READ_INTERACTION_NOT_SPECIFIED));
		}
	}
	
	private void checkFieldPairsAndReadInteractionArguments(List newProblems) {
		// if the selection interaction is not specified, each argument in the
		// reference descriptor's read interaction must correspond to a target key
		// in the field pairs.
		if (! this.usesDescriptorReadObjectInteraction() || this.referenceDescriptorReadInteraction() == null) {
			return;
		}
		
		for (Iterator stream = this.referenceDescriptorReadInteraction().inputArguments(); stream.hasNext(); ) {
			String argumentFieldName = ((ArgumentPair) stream.next()).getArgumentFieldName();
			
			if (! this.hasCorrespondingTargetKey(argumentFieldName)) {
				newProblems.add(this.buildProblem(ProblemConstants.MAPPING_NONCORRESPONDING_TARGET_KEY, argumentFieldName));
			}
		}
	}

	private void addUsesIndirectionWhileMaintainsBiDirectionalRelationship(List newProblems) {
		if (this.maintainsBidirectionalRelationship() && this.usesNoIndirection()) {
			newProblems.add(this.buildProblem(ProblemConstants.MAPPING_REFERENCE_MAINTAINS_BIDI_BUT_NO_INDIRECTION));
		}
	}
	
	private boolean hasCorrespondingTargetKey(String argumentFieldName) {
		for (Iterator stream = this.xmlFieldPairs(); stream.hasNext(); ) {
			MWXmlField targetField = ((MWXmlFieldPair) stream.next()).getTargetXmlField();
			
			if (targetField != null && targetField.getXpath().equals(argumentFieldName)) {
				return true;
			}
		}
		
		return false;
	}
	
	
	// **************** Runtime Conversion ***************
	
	protected DatabaseMapping buildRuntimeMapping() {
		return new EISOneToOneMapping();
	}
	
	public DatabaseMapping runtimeMapping() {
		EISOneToOneMapping mapping = (EISOneToOneMapping) super.runtimeMapping();
		
		for (Iterator stream = this.xmlFieldPairs(); stream.hasNext(); ) {
			((MWXmlFieldPair) stream.next()).addRuntimeForeignKeyField(mapping);
		}
		
        if (usesProxyIndirection()) {
            mapping.setIndirectionPolicy(new ProxyIndirectionPolicy());
        }
		return mapping;
	}
	
	
	// **************** TopLink methods ***************************************
		
	public static XMLDescriptor buildDescriptor() {	
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWEisOneToOneMapping.class);

		descriptor.getInheritancePolicy().setParentClass(MWEisReferenceMapping.class);

		XMLDirectMapping useDescriptorReadObjectInteractionMapping = (XMLDirectMapping) descriptor.addDirectMapping("useDescriptorReadObjectInteraction", "use-descriptor-read-object-interaction/text()");
		useDescriptorReadObjectInteractionMapping.setNullValue(Boolean.FALSE);

		return descriptor;	
	}

}
