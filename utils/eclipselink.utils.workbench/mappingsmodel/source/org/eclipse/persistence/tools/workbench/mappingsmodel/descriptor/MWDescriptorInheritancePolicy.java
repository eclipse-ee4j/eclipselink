/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalDescriptorInheritancePolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWEisDescriptorInheritancePolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWOXDescriptorInheritancePolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWDescriptorHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle.NodeReferenceScrubber;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.ChainIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.FilteringIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.TreeIterator;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.InheritancePolicy;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;

/**
 * Parent of MWRelationalInheritancePolicy and MWOXInheritancePolicy
 */
public abstract class MWDescriptorInheritancePolicy extends MWAbstractDescriptorPolicy implements MWInheritancePolicy {
	
	private volatile boolean isRoot;
		public static final String IS_ROOT_PROPERTY = "isRoot";
		
	private MWDescriptorHandle parentDescriptorHandle;
		public static final String PARENT_DESCRIPTOR_PROPERTY = "parentDescriptor";

	private volatile MWClassIndicatorPolicy classIndicatorPolicy;
		public static final String CLASS_INDICATOR_POLICY_PROPERTY = "classIndicatorPolicy";


	// **************** Constructors ***************

	protected MWDescriptorInheritancePolicy() {
		// for TopLink use only
		super();
	}

	protected MWDescriptorInheritancePolicy(MWMappingDescriptor descriptor) {
		super(descriptor);
	}


	// **************** initialization ***************

	/**
	 * initialize persistent state
	 */	
	protected void initialize(Node parent) {	
		super.initialize(parent);
		this.parentDescriptorHandle = new MWDescriptorHandle(this, this.buildParentDescriptorScrubber());
		this.isRoot = false;
		this.classIndicatorPolicy = new MWNullClassIndicatorPolicy(this);
	}
	
	void initializeParentDescriptor() {
		MWDescriptor descriptor = getProject().descriptorForType(getOwningDescriptor().getMWClass().getSuperclass());
		if (descriptor != null && descriptor.isActive()) {
			setParentDescriptor(descriptor);
		}
	}


	// **************** containment hierarchy ***************

	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.classIndicatorPolicy);
		children.add(this.parentDescriptorHandle);
	}
		
	private NodeReferenceScrubber buildParentDescriptorScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWDescriptorInheritancePolicy.this.setParentDescriptor(null);
			}
			public String toString() {
				return "MWDescriptorInheritancePolicy.buildParentDescriptorScrubber()";
			}
		};
	}
	
	public void descriptorInheritanceChanged() {
		for (Iterator stream = this.childDescriptors(); stream.hasNext(); ) {
			((MWDescriptor) stream.next()).inheritanceChanged();
		}
	}

	public void parentDescriptorMorphedToAggregate() {
		getClassIndicatorPolicy().parentDescriptorMorphedToAggregate();
	}
		
	
	
	// **************** Parent descriptor *************************************
	
	public MWDescriptor getParentDescriptor() {
		return this.parentDescriptorHandle.getDescriptor();
	}
	
	public void setParentDescriptor(MWDescriptor newParentDescriptor) {
		if (newParentDescriptor != null && this.isRoot()) {
			throw new IllegalStateException("Unable to set a parent descriptor on a root descriptor");
		}
		
		MWDescriptor oldParentDescriptor = this.getParentDescriptor();
		
		if (oldParentDescriptor != newParentDescriptor) {
			MWInheritancePolicy rootInheritancePolicy = null;
			
			rootInheritancePolicy = this.getRootDescriptor().getInheritancePolicy();
			
			this.parentDescriptorHandle.setDescriptor(newParentDescriptor);
			
			rootInheritancePolicy.buildClassIndicatorValues();			
			this.firePropertyChanged(PARENT_DESCRIPTOR_PROPERTY, oldParentDescriptor, newParentDescriptor);
			this.getRootDescriptor().getInheritancePolicy().buildClassIndicatorValues();

			this.getOwningDescriptor().inheritanceChanged();
		}
	}
	
	public Iterator candidateParentDescriptors() {
		final Set descendentDescriptors = CollectionTools.set(this.descendentDescriptors());
		final MWDescriptor thisDescriptor = this.getOwningDescriptor();
		
		return new FilteringIterator(this.getProject().descriptors()) {
			protected boolean accept(Object o) {
				MWDescriptor descriptor = (MWDescriptor) o;
				return descriptor.canHaveInheritance()
					&& descriptor != thisDescriptor
					&& ! descendentDescriptors.contains(o); 
			}
		};
	}
	
	
	// **************** Is root ***********************************************
	
	public boolean isRoot() {
		return this.isRoot;
	}
		
	public void setIsRoot(boolean newValue) {
		MWInheritancePolicy rootInheritancePolicy = getRootDescriptor().getInheritancePolicy();

		boolean oldValue = this.isRoot;
		this.isRoot = newValue;
		if (oldValue != newValue) {
			if (this.isRoot) {				
				setParentDescriptor(null);
				useClassIndicatorFieldPolicy();
				buildClassIndicatorValues();
			}
			else {
				setClassIndicatorPolicy(new MWNullClassIndicatorPolicy(this));
				initializeParentDescriptor();
				rootInheritancePolicy.buildClassIndicatorValues();
				rootInheritancePolicy = getRootDescriptor().getInheritancePolicy();
			}
			rootInheritancePolicy.buildClassIndicatorValues();

			firePropertyChanged(IS_ROOT_PROPERTY, oldValue, newValue);
		}
	}

	public MWClassIndicatorPolicy getClassIndicatorPolicy() {
		return this.classIndicatorPolicy;
	}

	public void useClassExtractionMethodIndicatorPolicy() {
		if (getClassIndicatorPolicy().getType() == MWClassIndicatorPolicy.CLASS_EXTRACTION_METHOD_TYPE) {
			return;
		}
		setClassIndicatorPolicy(new MWClassIndicatorExtractionMethodPolicy(this));
	}
	
	public void useClassIndicatorFieldPolicy() {
		if (getClassIndicatorPolicy().getType() == MWClassIndicatorPolicy.CLASS_INDICATOR_FIELD_TYPE) {
			return;
		}
		setClassIndicatorPolicy(buildClassIndicatorFieldPolicy());
	}
	
	protected abstract MWClassIndicatorFieldPolicy buildClassIndicatorFieldPolicy();
	
	
	protected void setClassIndicatorPolicy(MWClassIndicatorPolicy classIndicatorPolicy) {
		Object oldValue = getClassIndicatorPolicy();
		this.classIndicatorPolicy = classIndicatorPolicy;
		firePropertyChanged(CLASS_INDICATOR_POLICY_PROPERTY, oldValue, classIndicatorPolicy);
	}
	
	public HashSet getAllDescriptorsAvailableForIndicatorDictionary() {
		HashSet result = new HashSet();
		result.add(this.getOwningDescriptor());
		CollectionTools.addAll(result, this.descendentDescriptors());
		return result;
	}
	
	public Iterator descendentDescriptors() {
		return new TreeIterator(this.childDescriptors()) {
			protected Iterator children(Object next) {
				return ((MWDescriptor) next).getInheritancePolicy().childDescriptors();
			}
		};
	}
	
	public boolean hasDescendentDescriptors() {
		return this.descendentDescriptors().hasNext();
	}
	
	public Iterator childDescriptors() {
		return new FilteringIterator(this.getProject().mappingDescriptors()) {
			protected boolean accept(Object o) {
				return ((MWDescriptor) o).getInheritancePolicy().getParentDescriptor() 
					== MWDescriptorInheritancePolicy.this.getOwningDescriptor();
			}
		};
	}
	
	public boolean hasChildDescriptors() {
		return this.childDescriptors().hasNext();
	}
	
	/** Return a full inheritance lineage, starting with this policy's descriptor
	 * and continuing up until there is a descriptor with no parent descriptor */
	public Iterator descriptorLineage() {
		// using a chain iterator to traverse up the inheritance tree
		return new ChainIterator(this.getOwningDescriptor()) {
			protected Object nextLink(Object currentLink) {
				return ((MWDescriptor) currentLink).getInheritancePolicy().getParentDescriptor();
			}
		};	
	}

	public MWDescriptor getRootDescriptor() {
		MWDescriptor rootDescriptor = null;
		
		for (Iterator stream = this.descriptorLineage(); stream.hasNext(); ) {
			rootDescriptor = (MWDescriptor) stream.next();
		}		
		return rootDescriptor;
	}

	public void descriptorReplaced(MWDescriptor oldDescriptor, MWDescriptor newDescriptor) {
		super.descriptorReplaced(oldDescriptor, newDescriptor);
		if (this.getParentDescriptor() == oldDescriptor) {
			this.setParentDescriptor(newDescriptor);
		}
	}
	
	public MWTable getReadAllSubclassesView() {
		return null;
	}
	
	public void buildClassIndicatorValues() {
		getClassIndicatorPolicy().rebuildClassIndicatorValues(getAllDescriptorsAvailableForIndicatorDictionary());	
	}
	
	
	// **************** MWClassIndicatorPolicy.Parent implementation **********
	
	public MWMappingDescriptor getContainingDescriptor() {
		return this.getOwningDescriptor();
	}
	
	
	//*************** Problem Handling *************
	
	protected void addProblemsTo(List newProblems) {
		super.addProblemsTo(newProblems);
		if (this.isRoot()) {
			this.checkClassIndicatorValues(newProblems);
			this.descriptorTypeInheritanceMismatchTest(newProblems);
		} else {
			this.checkParentDescriptor(newProblems);
		}
		this.checkRootClassIndicatorFieldPolicy(newProblems);
	}
		

	private void checkClassIndicatorValues(List newProblems) {
		MWClassIndicatorPolicy cip = this.getClassIndicatorPolicy();
		if (cip.getType() != MWClassIndicatorPolicy.CLASS_INDICATOR_FIELD_TYPE) {
			return;
		}
		MWClassIndicatorFieldPolicy cifp = (MWClassIndicatorFieldPolicy) this.getClassIndicatorPolicy();
		if (cifp.classNameIsIndicator()) {
			return;
		}
		for (Iterator stream = cifp.includedClassIndicatorValues(); stream.hasNext(); ) {
			MWClassIndicatorValue value = (MWClassIndicatorValue) stream.next();
			if (value.getDescriptorValue().getMWClass().isAbstract() && value.getIndicatorValue() != null) {
				newProblems.add(this.buildProblem(ProblemConstants.CLASS_INDICATOR_FOR_ABSTRACT_CLASS, value.getDescriptorValue().getMWClass().shortName()));
			}
			if ( ! value.getDescriptorValue().isActive()) {
				newProblems.add(this.buildProblem(ProblemConstants.DESCRIPTOR_INHERITANCE_MAPPED_CHILD_DESCRIPTOR_INACTIVE, value.getDescriptorValue().getMWClass().shortName()));
			}
		}
		if ((cifp.classIndicatorValuesSize() == 0)  && this.hasDescendentDescriptors()) {
			newProblems.add(this.buildProblem(ProblemConstants.NO_INDICATOR_MAPPINGS));
		}
	}	

	private void checkParentDescriptor(List newProblems) {
		if (this.getParentDescriptor() == null) {
			newProblems.add(this.buildProblem(ProblemConstants.DESCRIPTOR_INHERITANCE_NO_PARENT));
		} else {
			if ( ! this.getParentDescriptor().isActive()) {
				newProblems.add(this.buildProblem(ProblemConstants.DESCRIPTOR_INHERITANCE_PARENT_DESCRIPTOR_INACTIVE));
			}
			if ( ! this.getParentDescriptor().getInheritancePolicy().isActive()) {
				newProblems.add(buildProblem(ProblemConstants.MISSING_INHERITANCE_POLICY_IN_PARENT_DESCRIPTOR));
			}
		}
	}	

	private void checkRootClassIndicatorFieldPolicy(List newProblems) {
		if (this.getOwningDescriptor().getMWClass().isAbstract()) {
			return;
		}
		if (this.getParentDescriptor() == null) {
			return;
		}
		MWInheritancePolicy ip = this.getRootDescriptor().getInheritancePolicy();
		if ( ! ip.isActive()) {
			return;
		}
		MWDescriptorInheritancePolicy dip = (MWDescriptorInheritancePolicy) ip;
		if ( ! (dip.getClassIndicatorPolicy().getType() == MWClassIndicatorPolicy.CLASS_INDICATOR_FIELD_TYPE)) {
			return;
		}
		MWClassIndicatorFieldPolicy rootIndicatorPolicy = (MWClassIndicatorFieldPolicy) dip.getClassIndicatorPolicy();
		if (rootIndicatorPolicy.classNameIsIndicator()) {
			return;
		}
		MWClassIndicatorValue value = rootIndicatorPolicy.getClassIndicatorValueForDescriptor(this.getOwningDescriptor());
		if ((value == null)
				|| (value.getIndicatorValue() == null)
				|| (value.getIndicatorValue().toString().length() == 0)) {
			newProblems.add(this.buildProblem(ProblemConstants.NO_ROOT_CLASS_INDICATOR_MAPPING_FOR_CLASS));
		}
	}
	
	private void descriptorTypeInheritanceMismatchTest(List newProblems) {
		if (this.checkDescendantsForDescriptorTypeMismatch()) {
			newProblems.add(this.buildProblem(this.descendantDescriptorTypeMismatchProblemString()));
		}
	}
	
	protected abstract boolean checkDescendantsForDescriptorTypeMismatch();
	protected abstract String descendantDescriptorTypeMismatchProblemString();


	// *************** Automap Support *************

	public void automap() {
		// Nothing to do
	}


	// **************** Runtime Conversion ***************

	public void adjustRuntimeDescriptor(ClassDescriptor runtimeDescriptor) {
		InheritancePolicy runtimeInheritancePolicy = (InheritancePolicy)runtimeDescriptor.getInheritancePolicy();
		if (this.getParentDescriptor() != null) {
			runtimeInheritancePolicy.setParentClassName(this.getParentDescriptor().getMWClass().fullName());
		}

		this.classIndicatorPolicy.adjustRuntimeInheritancePolicy(runtimeInheritancePolicy);
		
	}


	
	// **************** display methods ***************

	public void toString(StringBuffer sb) {
		if (this.getParentDescriptor() != null) {
			sb.append("Parent = " + getParentDescriptor().getMWClass().shortName() + " ");
		} else if (getClassIndicatorPolicy() != null) {
				sb.append("Root");
		}
	}
	
	public boolean isActive() {
		return true;
	}

	public MWDescriptorPolicy getPersistedPolicy() {
		return this;
	}

	// **************** TopLink methods ***************

	public static XMLDescriptor buildDescriptor() {		
		XMLDescriptor descriptor = new XMLDescriptor();

		descriptor.setJavaClass(MWDescriptorInheritancePolicy.class);

		InheritancePolicy ip = (InheritancePolicy)descriptor.getInheritancePolicy();
		ip.setClassIndicatorFieldName("@type");
		ip.addClassIndicator(MWRelationalDescriptorInheritancePolicy.class, "relational");
		ip.addClassIndicator(MWOXDescriptorInheritancePolicy.class, "o-x");
		ip.addClassIndicator(MWEisDescriptorInheritancePolicy.class, "eis");

		XMLDirectMapping isRootMapping = (XMLDirectMapping) descriptor.addDirectMapping("isRoot", "is-root/text()");
		isRootMapping.setNullValue(Boolean.TRUE);

		XMLCompositeObjectMapping parentDescriptorMapping = new XMLCompositeObjectMapping();
		parentDescriptorMapping.setAttributeName("parentDescriptorHandle");
		parentDescriptorMapping.setGetMethodName("getParentDescriptorForTopLink");
		parentDescriptorMapping.setSetMethodName("setParentDescriptorForTopLink");		
		parentDescriptorMapping.setReferenceClass(MWDescriptorHandle.class);
		parentDescriptorMapping.setXPath("parent-descriptor-handle");
		descriptor.addMapping(parentDescriptorMapping);

		XMLCompositeObjectMapping classIndicatorPolicyMapping = new XMLCompositeObjectMapping();
		classIndicatorPolicyMapping.setAttributeName("classIndicatorPolicy");
		classIndicatorPolicyMapping.setGetMethodName("getClassIndicatorPolicyForTopLink");
		classIndicatorPolicyMapping.setSetMethodName("setClassIndicatorPolicyForTopLink");
		classIndicatorPolicyMapping.setReferenceClass(MWAbstractClassIndicatorPolicy.class);
		classIndicatorPolicyMapping.setXPath("class-indicator-policy");
		descriptor.addMapping(classIndicatorPolicyMapping);

		return descriptor;
		
	}

	private MWDescriptorHandle getParentDescriptorForTopLink() {
		return (this.parentDescriptorHandle.getDescriptor() == null) ? null : this.parentDescriptorHandle;
	}
	private void setParentDescriptorForTopLink(MWDescriptorHandle handle) {
		NodeReferenceScrubber scrubber = this.buildParentDescriptorScrubber();
		this.parentDescriptorHandle = ((handle == null) ? new MWDescriptorHandle(this, scrubber) : handle.setScrubber(scrubber));
	}

	private MWAbstractClassIndicatorPolicy getClassIndicatorPolicyForTopLink() {
		return (this.classIndicatorPolicy.getValueForTopLink() == null) ? null : this.classIndicatorPolicy.getValueForTopLink();
	}
	private void setClassIndicatorPolicyForTopLink(MWAbstractClassIndicatorPolicy classIndicatorPolicy) {
		this.classIndicatorPolicy = (classIndicatorPolicy == null) ? new MWNullClassIndicatorPolicy(this) : classIndicatorPolicy;
	}
	
	public void postProjectBuild() {
		super.postProjectBuild();
		if (isRoot()) {
			getClassIndicatorPolicy().setDescriptorsAvailableForIndicatorDictionaryForTopLink(getAllDescriptorsAvailableForIndicatorDictionary().iterator());
		}
	}

}
