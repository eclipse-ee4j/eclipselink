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
package org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWDataField;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWNominative;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWAggregateDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWInterfaceDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWCompositeEisDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWOXDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWRootEisDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWClassHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle.NodeReferenceScrubber;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.DefaultMWClassRefreshPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.ExternalClassLoadFailureContainer;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.ExternalClassLoadFailureEvent;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassRefreshPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProjectDefaultsPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassNotFoundException;
import org.eclipse.persistence.tools.workbench.utility.iterators.NullIterator;
import org.eclipse.persistence.tools.workbench.utility.node.Node;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.InheritancePolicy;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;


public abstract class MWDescriptor extends MWModel 
	implements MWNominative {

	private MWClassHandle mwClassHandle;
		public final static String MW_CLASS_PROPERTY = "mwClass";
	
	private volatile String name; //fullClassName
		public static final String NAME_PROPERTY = "name";
		
	/** defaults to true **/
	private volatile boolean active;
		public final static String ACTIVE_PROPERTY = "active";
		
    protected MWTransactionalPolicy transactionalPolicy;

	/** used to store legacy values until they can be used in a postbuild **/
	protected Map legacyValuesMap;
	

	// ********** constructors **********
	
	/**
	 * Default constructor - for TopLink use only.
	 */
	protected MWDescriptor() {
		super();
	}

	protected MWDescriptor(MWProject parent, MWClass type, String name) {
		super(parent);
		initialize(type, name);
	}


	// ********** initialization **********
	
	/**
	 * initialize transient state
	 */	
	protected void initialize() {
		super.initialize();
		this.legacyValuesMap = new HashMap();
	}

	/**
	 * initialize persistent state
	 */
	protected void initialize(Node parent) {
		super.initialize(parent);
		this.mwClassHandle = new MWClassHandle(this, this.buildMWClassScrubber());
		this.active = true;
	}
	
	protected void initialize(MWClass mwClass, String descriptorName) {
		this.mwClassHandle.setType(mwClass);
		this.name = descriptorName;
        this.transactionalPolicy = buildDefaultTransactionalPolicy();
	}


	// ********** containment hierarchy **********

	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.mwClassHandle);
        children.add(this.transactionalPolicy);
	}

	private NodeReferenceScrubber buildMWClassScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWDescriptor.this.setMWClass(null);
			}
			public String toString() {
				return "MWDescriptor.buildMWClassScrubber()";
			}
		};
	}

    
    // ********** TransactionalPolicy API**********
    
    /** default implementation */
    protected MWTransactionalPolicy buildDefaultTransactionalPolicy() {
        return new MWNullTransactionalPolicy(this);
    }
    
    public MWTransactionalPolicy getTransactionalPolicy() {
        return this.transactionalPolicy;
    }

	public abstract void applyAdvancedPolicyDefaults(MWProjectDefaultsPolicy defaultsPolicy);
	 

	// *********** Accessors ************

	public MWClass getMWClass() {
		return this.mwClassHandle.getType();
	}
	
	public void setMWClass(MWClass newValue) {
		if (newValue == null) {
			throw new NullPointerException();
		}
		Object oldValue = this.mwClassHandle.getType();
		this.mwClassHandle.setType(newValue);
		this.firePropertyChanged(MW_CLASS_PROPERTY, oldValue, newValue);
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		Object old = this.name;
		this.name = name;
		this.firePropertyChanged(NAME_PROPERTY, old, name);
		if (this.attributeValueHasChanged(old, name)) {
			this.getProject().nodeRenamed(this);
		}
	}
	
	/**
	 * Indicates whether this descriptor is deamed a root descriptor.  All mapping descriptors
	 * have a notion of being root versus aggregate.
	 */
	public boolean isRootDescriptor() {
		return false;
	}
	
	public boolean isActive() {
		return this.active;
	}
	
	public boolean isInactive() {
		return ! this.active;
	}
	
	public void setActive(boolean newValue) {
		boolean oldValue = this.active;
		this.active = newValue;
		this.firePropertyChanged(ACTIVE_PROPERTY, oldValue, newValue);
	}
	
    public boolean supportsCachingPolicy() {
        return false;
    }
    
	
    // **************** queries *******************
	
	public String packageName() {
		return getMWClass().packageName();
	}
	
	public String shortName() {
		return getMWClass().shortName();
	}
	
	public Iterator mappings() {
		return NullIterator.instance();
	}
	
	public int inheritedAttributesSize() {
		return 0;
	}
	
	public int mappingsSize() {
		return 0;
	}
	
	public MWMapping mappingNamed(String mappingName) {
		return null;
	}
	
	public Iterator mappingsIncludingInherited() {
		return NullIterator.instance();
	}
    
    protected List getMappingsIncludingInherited() {
        return new Vector();
    }

    public Collection getAllQueryKeysIncludingInherited() {
        return new ArrayList();
    }
    
    public Collection getAutoGeneratedQueryKeysIncludingInherited() {
        return new ArrayList();
    }
   
    public Collection writableMappingsForField( MWDataField field ) {
        return Collections.EMPTY_LIST;
    }
        
    /** Use this method if other descriptor settings or policies depend upon inheritance */
    void inheritanceChanged() {
        this.transactionalPolicy.descriptorInheritanceChanged();
    }
   
	
    // **************** behavior *******************
	
	public void unmap() {
		//call super.unmap() when overriding this method
	}
	
	/** Return true if this descriptor can be part of a descriptor inheritance tree */
	public abstract boolean canHaveInheritance();
	
    public abstract MWInheritancePolicy getInheritancePolicy();
    
    
	/** Return true if this descriptor is part of a descriptor inheritance tree
	 * (simply having an inheritance policy is not enough) */
	public abstract boolean hasDefinedInheritance();
	
    /**
     *  Return true if this descriptor has a defined instantiation policy
     */
	public abstract boolean hasActiveInstantiationPolicy();
	
    /** Return an iterator of descriptors, each which inherits from the one before,
     * and terminates at the root descriptor (or at the point of cyclicity). */
    public Iterator inheritanceHierarchy() {
        return this.getInheritancePolicy().descriptorLineage();
    }
    
    /** 
     * Return true if this descriptor, or any descriptor in the 
     * inheritance hierarchy uses sequencing, as descriptors
     * inherit sequencing information
     * (overridden in MWTableDescriptor)
     */
    public boolean usesSequencingInDescriptorHierarchy() {
        return false;
    }
    
	
    // **************** morphing *******************

	/**
	 * Subclasses should override this method to call the
	 * appropriate initializeFromMW___Descriptor() method.
	 */
	public abstract void initializeOn(MWDescriptor newDescriptor);


	protected void initializeDescriptorAfterMorphing(MWDescriptor newDescriptor) {
		this.initializeOn(newDescriptor);
		newDescriptor.setChildBackpointers();
		getProject().replaceDescriptor(this, newDescriptor);
	}


	protected void initializeFromMWDescriptor(MWDescriptor oldDescriptor)
	{
		this.setActive(oldDescriptor.isActive());
		this.setName(oldDescriptor.getName());
	}

	
	protected void initializeFromMWMappingDescriptor(MWMappingDescriptor oldDescriptor) {
	   this.initializeFromMWDescriptor(oldDescriptor);
	}


	// **************** class refreshing *******************

	public void refreshClass(ExternalClassLoadFailureContainer failures, DescriptorCreationFailureListener descCreationFailureListener) {
		this.refreshClass(failures, this.buildMWClassRefreshPolicy(), descCreationFailureListener);
	}
	
	public void refreshClass()
		throws ExternalClassNotFoundException, InterfaceDescriptorCreationException {
		this.refreshClass(this.buildMWClassRefreshPolicy());
	}
	
	protected MWClassRefreshPolicy buildMWClassRefreshPolicy() {
		return DefaultMWClassRefreshPolicy.instance();
	}
	
	protected void refreshClass(ExternalClassLoadFailureContainer failures, MWClassRefreshPolicy refreshPolicy, DescriptorCreationFailureListener descCreationFailureListener) {
		try {
			this.refreshClass(refreshPolicy);
		} catch (ExternalClassNotFoundException e) {
			failures.externalClassLoadFailure(new ExternalClassLoadFailureEvent(this, this.getName(), e));
		} catch (InterfaceDescriptorCreationException e) {
			descCreationFailureListener.descriptorCreationFailure(new DescriptorCreationFailureEvent(this, this.getName(), "DESCRIPTOR_REFRESH_ERROR_MESSAGE"));
		}
	}
	
	// subclasses will throw InterfaceDescriptorCreationException
	protected void refreshClass(MWClassRefreshPolicy refreshPolicy)
		throws ExternalClassNotFoundException, InterfaceDescriptorCreationException {
		getMWClass().refresh(refreshPolicy);	
	}

	
	// *************** Problem Handling **************
	
	/** 
	 * This is overwritten so that *ONLY* active descriptors validate
	 * themselves and their branches.
	 * @see AbstractNodeModel.validateBranch()
	 */
	public boolean validateBranchInternal() {
		return this.isActive() ?
			super.validateBranchInternal()
		:
			this.clearAllBranchProblemsInternal();
	}
	
	protected void addProblemsTo(List currentProblems) {
		super.addProblemsTo(currentProblems);
		this.checkForFinalSuperClass(currentProblems);
		this.checkForPublicClass(currentProblems);
		// TODO this is a hack until we get a visible class repository
		this.getMWClass().addDescriptorProblemsTo(currentProblems);
	}
		
	private void checkForPublicClass(List currentProblems){
		if ( ! this.getMWClass().getModifier().isPublic()) {
			currentProblems.add(this.buildProblem(ProblemConstants.DESCRIPTOR_CLASS_NOT_PUBLIC));
		}
	}
	
	private void checkForFinalSuperClass(List currentProblems) {
		MWClass superclass = this.getMWClass().getSuperclass();
		if ((superclass != null) && superclass.getModifier().isFinal()) {
			currentProblems.add(this.buildProblem(ProblemConstants.DESCRIPTOR_CLASS_SUBCLASSES_FINAL_CLASS));
		}
	}


	// ************* Automap Support ****************

	public final void automapInheritanceHierarchy(Collection automapDescriptors) {
		if (this.isActive() && this.canHaveInheritance() && ! this.hasDefinedInheritance()) {
			this.automapInheritanceHierarchyInternal(automapDescriptors);
		}
	}

	protected void automapInheritanceHierarchyInternal(Collection automapDescriptors) {
		// do nothing by default
	}

	public final boolean autoMapRequiresMetaData() {
		return this.isActive() && this.autoMapRequiresMetaDataInternal();
	}

	protected boolean autoMapRequiresMetaDataInternal() {
		return false;		// the default is false;
	}

	/**
	 * make sure descriptor is active
	 */
	public final void automap() {
		if (this.isActive()) {
			this.automapInternal();
		}
	}

	protected void automapInternal() {
		// do nothing by default
	}


	// **************** Runtime Conversion *******************

	public ClassDescriptor buildRuntimeDescriptor() {
		ClassDescriptor runtimeDescriptor = this.buildBasicRuntimeDescriptor();
        this.transactionalPolicy.adjustRuntimeDescriptor(runtimeDescriptor);
        return runtimeDescriptor;
	}
	
	/**
	 * Build the runtime descriptor, set the type (interface, aggregate, etc), 
	 * and set the class name
	 */
	protected abstract ClassDescriptor buildBasicRuntimeDescriptor();
		

	//*************** Display Methods **************
	
	public String displayString() {
		return getMWClass().shortName();
	}
	
	public String displayStringWithPackage() {
		String packageName = this.packageName();
		
		if (StringTools.stringIsEmpty(packageName)) {
			packageName =  "default package";
		}
		
		return this.getMWClass().shortName() + " (" + packageName + ")";
	}

	public void toString(StringBuffer sb) {
		sb.append(getName());
	}


	//*************** TopLink Methods **************
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWDescriptor.class);

		descriptor.setDefaultRootElement("descriptor");

		org.eclipse.persistence.descriptors.InheritancePolicy ip = (org.eclipse.persistence.descriptors.InheritancePolicy)descriptor.getInheritancePolicy();
		ip.setClassIndicatorFieldName("@type");
		ip.addClassIndicator(MWTableDescriptor.class, "relational");
		ip.addClassIndicator(MWAggregateDescriptor.class, "aggregate");
		ip.addClassIndicator(MWInterfaceDescriptor.class, "interface");
		ip.addClassIndicator(MWRootEisDescriptor.class, "eis-root");
		ip.addClassIndicator(MWCompositeEisDescriptor.class, "eis-composite");
		ip.addClassIndicator(MWOXDescriptor.class, "o-x");
		
		descriptor.addDirectMapping("name", "name/text()");
		((XMLDirectMapping) descriptor.addDirectMapping("comment", "comment/text()")).setNullValue("");
		//TODO could change to inactive - then False would be the null value.
		//most descriptors are active
		((XMLDirectMapping) descriptor.addDirectMapping("active", "active/text()")).setNullValue(Boolean.TRUE);
		
		XMLCompositeObjectMapping mwClassHandleMapping = new XMLCompositeObjectMapping();
		mwClassHandleMapping.setAttributeName("mwClassHandle");
        mwClassHandleMapping.setGetMethodName("getMWClassHandleForTopLink");
        mwClassHandleMapping.setSetMethodName("setMWClassHandleForTopLink");
		mwClassHandleMapping.setReferenceClass(MWClassHandle.class);
		mwClassHandleMapping.setXPath("class-handle");
		descriptor.addMapping(mwClassHandleMapping);

        XMLCompositeObjectMapping transactionalPolicyMapping = new XMLCompositeObjectMapping();
        transactionalPolicyMapping.setAttributeName("transactionalPolicy");
        transactionalPolicyMapping.setGetMethodName("getTransactionalPolicyForTopLink");
        transactionalPolicyMapping.setSetMethodName("setTransactionalPolicyForTopLink");
        transactionalPolicyMapping.setReferenceClass(MWAbstractTransactionalPolicy.class);
        transactionalPolicyMapping.setXPath("transactional-policy");
        descriptor.addMapping(transactionalPolicyMapping);

		return descriptor;
	}	

	/**
	 * check for null
	 */
	private MWClassHandle getMWClassHandleForTopLink() {
		return (this.mwClassHandle.getType() == null) ? null : this.mwClassHandle;
	}
	private void setMWClassHandleForTopLink(MWClassHandle mwClassHandle) {
		NodeReferenceScrubber scrubber = this.buildMWClassScrubber();
		this.mwClassHandle = ((mwClassHandle == null) ? new MWClassHandle(this, scrubber) : mwClassHandle.setScrubber(scrubber));
	}

    private MWAbstractTransactionalPolicy getTransactionalPolicyForTopLink() {
        return this.transactionalPolicy.getValueForTopLink();
    }
    private void setTransactionalPolicyForTopLink(MWAbstractTransactionalPolicy transactionalPolicy) {
        if (transactionalPolicy == null) {
            this.transactionalPolicy = new MWNullTransactionalPolicy(this);
        } else {
            this.transactionalPolicy = transactionalPolicy;
        }
    }
    
}
