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

import java.util.List;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWDataField;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorLockingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWTransactionalPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWSchemaContextComponent;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXmlField;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXmlNode;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXpathContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXpathSpec;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.SchemaChange;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.TimestampLockingPolicy;
import org.eclipse.persistence.descriptors.VersionLockingPolicy;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;

public final class MWEisDescriptorLockingPolicy 
	extends MWDescriptorLockingPolicy
	implements MWXmlNode, MWXpathContext
{
	// **************** Variables *********************************************
	
	private MWXmlField versionXmlField; 
	
	
	// **************** Constructors ******************************************
	
	private MWEisDescriptorLockingPolicy() {
		super();
	}
	
	MWEisDescriptorLockingPolicy(MWEisTransactionalPolicy descriptor) {
		super(descriptor);
	}
	
	
	// **************** Initialization ****************************************
	
	protected void initialize(Node parent) {
		super.initialize(parent);
		this.versionXmlField = new MWXmlField(this);
	}
	
	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.versionXmlField);
	}

	public MWDataField getVersionLockField() {
		return this.versionXmlField;
	}
	
	public void setVersionLockField(MWDataField newLockField) {
        throw new UnsupportedOperationException("the xml field itself should be modified");
	}

	
	// **************** MWXpathContext implementation *************************
	
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
	
	
	// **************** Convenience *******************************************
	
	private MWRootEisDescriptor eisDescriptor() {
		return (MWRootEisDescriptor) ((MWTransactionalPolicy) this.getParent()).getParent();
	}
	
	
	// **************** Problem handling **************************************
	
	protected void checkLockFieldSpecifiedForLockingPolicy(List newProblems) {
		if(getLockingType() != NO_LOCKING && getVersionLockField() == null) {
			newProblems.add(buildProblem(ProblemConstants.DESCRIPTOR_LOCKING_VERSION_LOCK_FIELD_NOT_SPECIFIED));
		}
	}
	
	
	// **************** Model synchronization *********************************
	
	/** @see MWXmlNode#resolveXpaths() */
	public void resolveXpaths() {
		this.versionXmlField.resolveXpaths();
	}
	
	/** @see MWXmlNode#schemaChanged(SchemaChange) */
	public void schemaChanged(SchemaChange change) {
		this.versionXmlField.schemaChanged(change);
	}
	
	
	// **************** Runtime conversion ************************************
	
	public void adjustRuntimeDescriptor(ClassDescriptor runtimeDescriptor)
	{
		super.adjustRuntimeDescriptor(runtimeDescriptor);
		if (getLockingType() == OPTIMISTIC_LOCKING)
		{
			VersionLockingPolicy lockingPolicy;
			if (getOptimisticVersionLockingType() == OPTIMISTIC_VERSION_VERSION)
			{
				lockingPolicy = new VersionLockingPolicy();
			}
			else
			{
				lockingPolicy = new TimestampLockingPolicy();
				if (this.usesLocalTime()) {
					((TimestampLockingPolicy)lockingPolicy).useLocalTime();
				} else {
					((TimestampLockingPolicy)lockingPolicy).useServerTime();
				}
			}
			if (getVersionLockField() != null && getVersionLockField().runtimeField() != null) {
				lockingPolicy.setWriteLockFieldName(getVersionLockField().runtimeField().getQualifiedName());
			}

			lockingPolicy.setIsStoredInCache(shouldStoreVersionInCache());
			runtimeDescriptor.setOptimisticLockingPolicy(lockingPolicy);
		}
	}
	
	
	// **************** TopLink methods ***************************************
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		
		descriptor.setJavaClass(MWEisDescriptorLockingPolicy.class);
		descriptor.getInheritancePolicy().setParentClass(MWDescriptorLockingPolicy.class);
		
		XMLCompositeObjectMapping versionXmlFieldMapping = new XMLCompositeObjectMapping();
		versionXmlFieldMapping.setReferenceClass(MWXmlField.class);
		versionXmlFieldMapping.setAttributeName("versionXmlField");
		versionXmlFieldMapping.setGetMethodName("getVersionXmlFieldForTopLink");
		versionXmlFieldMapping.setSetMethodName("setVersionXmlFieldForTopLink");
		versionXmlFieldMapping.setXPath("version-xml-field");
		descriptor.addMapping(versionXmlFieldMapping);
		
		return descriptor;
	}
	
	private MWXmlField getVersionXmlFieldForTopLink() {
		return (this.versionXmlField.isSpecified()) ? this.versionXmlField : null;
	}
	
	private void setVersionXmlFieldForTopLink(MWXmlField xmlField) {
		this.versionXmlField = ((xmlField == null) ? new MWXmlField(this) : xmlField);
	}
}
