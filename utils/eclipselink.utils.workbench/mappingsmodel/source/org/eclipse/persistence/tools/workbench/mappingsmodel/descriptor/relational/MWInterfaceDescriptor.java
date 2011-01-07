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
package org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWQueryKey;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.InterfaceDescriptorCreationException;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWInheritancePolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWNullInheritancePolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWDescriptorHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle.NodeReferenceScrubber;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassRefreshPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProjectDefaultsPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassNotFoundException;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.HashBag;
import org.eclipse.persistence.tools.workbench.utility.filters.Filter;
import org.eclipse.persistence.tools.workbench.utility.iterators.CloneIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.NullIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.mappings.querykeys.QueryKey;

public final class MWInterfaceDescriptor extends MWDescriptor 
	implements MWRelationalDescriptor {

	// these must all be relational descriptors
	private Collection implementorHandles;
		public static final String IMPLEMENTORS_COLLECTION = "implementors";
		private NodeReferenceScrubber implementorScrubber;
        
    private MWInheritancePolicy nullInheritancePolicy = new MWNullInheritancePolicy(this);
	
	
	// ********** Constructors **********

	/**
	 * Default constructor - for TopLink use only.
	 */
	private MWInterfaceDescriptor() {
		super();
	}

	public MWInterfaceDescriptor(MWProject parent, MWClass type, String name) {
		super(parent, type, name);
	}

	
	// ********** initialization **********

	protected void initialize(Node parent) {
		super.initialize(parent);
		this.implementorHandles = new Vector();
	}


	// ********** accessors **********

	// ***** implementors
	private Iterator implementorHandles() {
		return new CloneIterator(this.implementorHandles) {
			protected void remove(Object current) {
				MWInterfaceDescriptor.this.removeImplementorHandle((MWDescriptorHandle) current);
			}
		};
	}

	void removeImplementorHandle(MWDescriptorHandle handle) {
		this.implementorHandles.remove(handle);
		this.fireItemRemoved(IMPLEMENTORS_COLLECTION, handle.getDescriptor());

		this.getProject().implementorsChangedFor(this);
	}

	public Iterator implementors() {
		return new TransformationIterator(this.implementorHandles()) {
			protected Object transform(Object next) {
				return ((MWDescriptorHandle) next).getDescriptor();
			}
		};
	}

	public int implementorsSize() {
		return this.implementorHandles.size();
	}

	public void addImplementor(MWDescriptor descriptor) {
		if (CollectionTools.contains(this.implementors(), descriptor)) {
			throw new IllegalArgumentException(descriptor.toString());
		}
		this.implementorHandles.add(new MWDescriptorHandle(this, descriptor, this.implementorScrubber()));
		this.fireItemAdded(IMPLEMENTORS_COLLECTION, descriptor);

		this.getProject().implementorsChangedFor(this);
	}

	public void removeImplementor(MWDescriptor descriptor) {
		for (Iterator stream = this.implementors(); stream.hasNext(); ) {
			if (stream.next() == descriptor) {
				stream.remove();
				return;
			}
		}
		throw new IllegalArgumentException(descriptor.toString());
	}
	
	public void removeImplementors(Collection descriptors) {
		this.removeImplementors(descriptors.iterator());
	}
	
	public void removeImplementors(Iterator descriptors) {
		while (descriptors.hasNext()) {
			this.removeImplementor((MWDescriptor) descriptors.next());
		}
	}

	public void clearImplementors() {
		for (Iterator stream = this.implementorHandles(); stream.hasNext(); ) {
			stream.next();
			stream.remove();
		}
	}
	
	
	// ********** queries **********

	public SortedSet getAllQueryKeys() {
        HashBag queryKeyNameBag = new HashBag();
        SortedSet commonQueryKeys = new TreeSet();
        MWTableDescriptor descriptor = null;
    
        Iterator implementors = implementors();
    
        while (implementors.hasNext()) {
            descriptor = (MWTableDescriptor) implementors.next();
            Iterator allQueryKeys = descriptor.getAllQueryKeysIncludingInherited().iterator();
            Collection duplicates = new ArrayList();
            while (allQueryKeys.hasNext()) {
                String queryKeyName =((MWQueryKey) allQueryKeys.next()).getName();
                //only add non-duplicate query keys
                if (!duplicates.contains(queryKeyName)) {
                    duplicates.add(queryKeyName);
                    queryKeyNameBag.add(queryKeyName);
                }
            }
        }

        Iterator queryKeyNames = queryKeyNameBag.iterator();
        while (queryKeyNames.hasNext()) {
            String queryKeyName = (String) queryKeyNames.next();
            if (queryKeyNameBag.count(queryKeyName) == implementorsSize()) {
                commonQueryKeys.add(descriptor.queryKeyNamedIncludingInherited(queryKeyName));
            }
        }
        return commonQueryKeys;
    }
	
    public Iterator allQueryKeyNames(Iterator iterator) {
        return new TransformationIterator(iterator) {
            protected Object transform(Object next) {            
                return ((MWQueryKey) next).getName();
            }
        };
    }

	public boolean hasImplementor(MWDescriptor descriptor) {
		return CollectionTools.contains(this.implementors(), descriptor);
	}

	public Iterator allAssociatedFields() {
		return NullIterator.instance();
	}
	
	
	// ********** containment hierarchy **********

	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		synchronized (this.implementorHandles) { children.addAll(this.implementorHandles); }
	}

	private NodeReferenceScrubber implementorScrubber() {
		if (this.implementorScrubber == null) {
			this.implementorScrubber = this.buildImplementorScrubber();
		}
		return this.implementorScrubber;
	}

	private NodeReferenceScrubber buildImplementorScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWInterfaceDescriptor.this.removeImplementorHandle((MWDescriptorHandle) handle);
			}
			public String toString() {
				return "MWInterfaceDescriptor.buildImplementorScrubber()";
			}
		};
	}

	//TODO If a descriptor is made aggregate, do we actually want to remove it, 
	//shouldn't we just have problems, aggregates are not allowed as implementors
	public void descriptorReplaced(MWDescriptor oldDescriptor, MWDescriptor newDescriptor) {
		super.descriptorReplaced(oldDescriptor, newDescriptor);
		if (this.hasImplementor(oldDescriptor)) {
			this.removeImplementor(oldDescriptor);
			if (newDescriptor instanceof MWTableDescriptor) {
				this.addImplementor(newDescriptor);
			}
		}
	}
	
	
	// ********** MWDescriptor Implementation **********
	
	public void initializeOn(MWDescriptor newDescriptor) {
		((MWRelationalDescriptor) newDescriptor).initializeFromMWInterfaceDescriptor(this);
	}
	
	protected void refreshClass(MWClassRefreshPolicy refreshPolicy)
		throws ExternalClassNotFoundException, InterfaceDescriptorCreationException
	{
		super.refreshClass(refreshPolicy);
		
		if (! this.getMWClass().isInterface()) {
			this.asMWTableDescriptor();
		}
	}

	public void applyAdvancedPolicyDefaults(MWProjectDefaultsPolicy defaultsPolicy) {
		//do nothing, advanced properites do not apply to interface descriptors
	}

	public void unmap() {
		super.unmap();
		this.clearImplementors();
	}
	
	public boolean canHaveInheritance() {
		return false;
	}
	
    public MWInheritancePolicy getInheritancePolicy() {
        return nullInheritancePolicy;
    }
    
 	public boolean hasDefinedInheritance() {
		return false;
	}
	
	public boolean hasActiveInstantiationPolicy() {
		return false;
	}
	

	// ********** MWRelationalDescriptor implementation **********
	
	public Iterator allQueryKeys() {
		return this.getAllQueryKeys().iterator();
	}
	
	public Iterator allQueryKeysIncludingInherited() {
		return allQueryKeys();
	}
	
	public Iterator allQueryKeyNames() {
		return new TransformationIterator(allQueryKeys()) {
			protected Object transform(Object next) {			
				return ((MWQueryKey) next).getName();
			}
		};
	}
	
	public MWQueryKey queryKeyNamed(String name) {
		for (Iterator stream = this.allQueryKeys(); stream.hasNext(); ) {
			MWQueryKey queryKey = (MWQueryKey) stream.next();
			if (queryKey.getName().equals(name)) {
				return queryKey;
			}
		}
		return null;
	}

	public MWQueryKey queryKeyNamedIncludingInherited(String name) {
		return queryKeyNamed(name);
	}
	
	public Iterator associatedTables() {
		return NullIterator.instance();
	}
	
	public int associatedTablesSize() {
		return 0;
	}
	
	public MWTable getPrimaryTable() {
		return null;
	}
	
	public Iterator associatedTablesIncludingInherited() {
		return this.associatedTables();
	}
	
	public int associatedTablesIncludingInheritedSize() {
		return this.associatedTablesSize();
	}
	
	public Iterator candidateTables() {
		return this.associatedTables();
	}

	public int candidateTablesSize() {
		return this.associatedTablesSize();
	}

	public Iterator candidateTablesIncludingInherited() {
		return this.candidateTables();
	}
	
	public int candidateTablesIncludingInheritedSize() {
		return this.candidateTablesSize();
	}

	public void notifyExpressionsToRecalculateQueryables() {
		// do nothing
	}

	public List getQueryables(Filter queryableFilter) {
		return Collections.EMPTY_LIST;
	}
	
	public boolean isTableDescriptor() {
		return false;
	}

	public boolean isAggregateDescriptor() {
		return false;
	}

	public Collection buildAggregateFieldNameGenerators() {
		return Collections.EMPTY_SET;
	}

	public void initializeFromMWAggregateDescriptor(MWAggregateDescriptor oldDescriptor) {
	//	super.initializeFromMWAggregateDescriptor(oldDescriptor);
		this.initializeFromMWRelationalClassDescriptor(oldDescriptor);
	}
	
	public void initializeFromMWRelationalClassDescriptor(MWRelationalClassDescriptor oldDescriptor) {
	//	super.initializeFromMWRelationalClassDescriptor(oldDescriptor);
		this.initializeFromMWMappingDescriptor(oldDescriptor);
	}
	
	public void initializeFromMWTableDescriptor(MWTableDescriptor oldDescriptor) {
	//	super.initializeFromMWTableDescriptor(oldDescriptor);
		this.initializeFromMWRelationalClassDescriptor(oldDescriptor);
	}
	
	public void initializeFromMWInterfaceDescriptor(MWInterfaceDescriptor oldDescriptor) {
	//	super.initializeFromMWInterfaceDescriptor(oldDescriptor);
	   this.initializeFromMWDescriptor(oldDescriptor);
	}
	
	
	public MWAggregateDescriptor asMWAggregateDescriptor() {
		throw new RuntimeException("Can't change an interface descriptor to an aggregate descriptor unless the type is not an interface.");
	}
	
	public MWTableDescriptor asMWTableDescriptor() throws InterfaceDescriptorCreationException {
		if (getMWClass().isInterface()) {
			throw new RuntimeException("Can't change an interface descriptor to a class descriptor unless the type is not an interface.");
		}
		MWTableDescriptor newDescriptor = (MWTableDescriptor) this.getProject().addDescriptorForType(this.getMWClass());
		this.initializeDescriptorAfterMorphing(newDescriptor);
		return newDescriptor;
	}

	public MWInterfaceDescriptor asMWInterfaceDescriptor() {
		return this;
	}

	public boolean isInterfaceDescriptor() {
		return true;
	}


	// *************** Problem Handling **************
	
	protected void addProblemsTo(List newProblems) {
		super.addProblemsTo(newProblems);
		this.checkImplementors(newProblems);		
	}
	
	private void checkImplementors(List newProblems) {
		for (Iterator stream = this.implementors(); stream.hasNext(); ) {
			MWDescriptor implementor = (MWDescriptor) stream.next();
			if ( ! CollectionTools.contains(implementor.getMWClass().allInterfaces(), this.getMWClass())) {
				newProblems.add(this.buildProblem(ProblemConstants.INTERFACE_DESCRIPTOR_IMPLEMENTOR_DOES_NOT_IMPLEMENT_INTERFACE, implementor.displayString()));
			}
		}
	}


	// ********** Runtime Conversion **********
	
	public ClassDescriptor buildRuntimeDescriptor() {
		ClassDescriptor runtimeDescriptor = super.buildRuntimeDescriptor();
		
		Iterator queryKeys = allQueryKeys();
		while (queryKeys.hasNext()) {
			MWQueryKey bldrQueryKey = (MWQueryKey) queryKeys.next();
			QueryKey queryKey = new QueryKey();
			queryKey.setName(bldrQueryKey.getName());
			runtimeDescriptor.addQueryKey(queryKey);
		}		
	
		return runtimeDescriptor;	
	}
		
	protected ClassDescriptor buildBasicRuntimeDescriptor() {
		RelationalDescriptor descriptor = new RelationalDescriptor();
		descriptor.setJavaClassName(getMWClass().getName());
		descriptor.descriptorIsForInterface();
		return descriptor;
	}
	

	// ********** TopLink methods **********

	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWInterfaceDescriptor.class);
		descriptor.getInheritancePolicy().setParentClass(MWDescriptor.class);

		XMLCompositeCollectionMapping implementorHandlesMapping = new XMLCompositeCollectionMapping();
		implementorHandlesMapping.setAttributeName("implementorHandles");
		implementorHandlesMapping.setSetMethodName("setImplementorHandlesForTopLink");
		implementorHandlesMapping.setGetMethodName("getImplementorHandlesForTopLink");
		implementorHandlesMapping.setReferenceClass(MWDescriptorHandle.class);
		implementorHandlesMapping.setXPath("implementor-handles/descriptor-handle");
		descriptor.addMapping(implementorHandlesMapping);
	
		return descriptor;
	}	

	/**
	 * sort the collection for TopLink
	 */
	private Collection getImplementorHandlesForTopLink() {
		synchronized (this.implementorHandles) {
			return new TreeSet(this.implementorHandles);
		}
	}
	private void setImplementorHandlesForTopLink(Collection implementorHandles) {
		for (Iterator stream = implementorHandles.iterator(); stream.hasNext(); ) {
			((MWDescriptorHandle) stream.next()).setScrubber(this.implementorScrubber());
		}
		this.implementorHandles = implementorHandles;
	}

}
