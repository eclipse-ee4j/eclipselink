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
package org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWQueryKey;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumnPair;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalClassDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWCollectionContainerPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWContainerPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWIndirectableContainerMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWListContainerPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapContainerMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapContainerPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWSetContainerPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.utility.filters.Filter;
import org.eclipse.persistence.tools.workbench.utility.iterators.CloneListIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.FilteringIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.NullIterator;
import org.eclipse.persistence.tools.workbench.utility.node.Node;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.indirection.TransparentIndirectionPolicy;
import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLTransformationMapping;
import org.eclipse.persistence.sessions.Record;


public abstract class MWCollectionMapping 
	extends MWAbstractTableReferenceMapping
	implements MWIndirectableContainerMapping, MWMapContainerMapping
{	
	private volatile MWContainerPolicy containerPolicy;
	
    private List orderings;
        public static final String ORDERINGS_LIST = "orderings";
	
	
	// **************** static variables **************************************
	
	public final static String EJB_INDIRECT_LIST_CLASS_NAME = "org.eclipse.persistence.indirection.EJBIndirectList";
	public final static String EJB_INDIRECT_SET_CLASS_NAME = "org.eclipse.persistence.indirection.EJBIndirectSet";
	public final static String INDIRECT_LIST_CLASS_NAME = "org.eclipse.persistence.indirection.IndirectList";
	public final static String INDIRECT_MAP_CLASS_NAME = "org.eclipse.persistence.indirection.IndirectMap";
	public final static String INDIRECT_SET_CLASS_NAME = "org.eclipse.persistence.indirection.IndirectSet";


	// **************** Constructors ****************************************
	
	/** Default constructor - for TopLink use only */
	protected MWCollectionMapping() {
		super();
	}
	
	protected MWCollectionMapping(MWRelationalClassDescriptor descriptor, MWClassAttribute attribute, String name)  {
		super(descriptor, attribute, name);
	}
	
	
	// **************** Building and Initializing *************

	protected void initialize(Node parent) {
		super.initialize(parent);
		this.orderings = new Vector();
	}

	protected void initialize(MWClassAttribute attribute, String name) {
		super.initialize(attribute, name);
		
        //TODO wow this is not pretty! and it's copied in other collection mappings
		if (attribute.getDimensionality() > 0) {
			this.containerPolicy = new MWListContainerPolicy(this);
			this.setUseTransparentIndirection();
		} else {
			MWClass type = null;
			if (attribute.isValueHolder()) {
	            this.setUseValueHolderIndirection();
				type = attribute.getValueType();
			} else {
				this.setUseTransparentIndirection();
				type = attribute.getType();
			}
			if (type.isAssignableToMap()) {
				this.containerPolicy = new MWMapContainerPolicy(this);
			}
			else if (type.isAssignableToList()) {
				this.containerPolicy = new MWListContainerPolicy(this);
			}
			else if (type.isAssignableToSet()) {
				this.containerPolicy = new MWSetContainerPolicy(this);
			}
			else if (type.isAssignableToCollection()){
				this.containerPolicy = new MWCollectionContainerPolicy(this);
			}
			else { // this is the default in the runtime
				this.containerPolicy = new MWListContainerPolicy(this);
			}
        }
	}
	
	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.containerPolicy);
        synchronized (this.orderings) { children.addAll(this.orderings); }
	}
		
	
	// **************** Container policy **************************************
	
	public MWContainerPolicy getContainerPolicy() {
		return this.containerPolicy;
	}
	
	private void setContainerPolicy(MWContainerPolicy containerPolicy) {
		Object oldValue = this.containerPolicy;
		this.containerPolicy = containerPolicy;
		firePropertyChanged(CONTAINER_POLICY_PROPERTY, oldValue, containerPolicy);
	}
	
	public MWMapContainerPolicy setMapContainerPolicy() {
		if (this.containerPolicy instanceof MWMapContainerPolicy) {
			return (MWMapContainerPolicy) this.containerPolicy;
		}
		MWMapContainerPolicy cp = new MWMapContainerPolicy(this);
		this.setContainerPolicy(cp);
		return cp;
	}
	
	public MWCollectionContainerPolicy setCollectionContainerPolicy() {
		if (this.containerPolicy instanceof MWCollectionContainerPolicy) {
			return (MWCollectionContainerPolicy) this.containerPolicy;
		}
		MWCollectionContainerPolicy cp = new MWCollectionContainerPolicy(this);
		this.setContainerPolicy(cp);
		return cp;
	}
    
    public MWListContainerPolicy setListContainerPolicy() {
        if (this.containerPolicy instanceof MWListContainerPolicy) {
            return (MWListContainerPolicy) this.containerPolicy;
        }
        MWListContainerPolicy cp = new MWListContainerPolicy(this);
        this.setContainerPolicy(cp);
        return cp;
    }
    
    public MWSetContainerPolicy setSetContainerPolicy() {
        if (this.containerPolicy instanceof MWSetContainerPolicy) {
            return (MWSetContainerPolicy) this.containerPolicy;
        }
        MWSetContainerPolicy cp = new MWSetContainerPolicy(this);
        this.setContainerPolicy(cp);
        return cp;
    }
    
	public Iterator candidateKeyMethods(Filter keyMethodFilter) {
		if (this.getReferenceDescriptor() != null) {
			return new FilteringIterator(this.getReferenceDescriptor().getMWClass().allInstanceMethods(), keyMethodFilter);
		}
		
        return NullIterator.instance();
	}
	
	
	// **************** Morphing **********************
	
	protected void initializeFromMWCollectionMapping(MWCollectionMapping oldMapping) {
		super.initializeFromMWCollectionMapping(oldMapping);
        
        for (ListIterator i = orderings(); i.hasNext(); ) {
            MWCollectionOrdering ordering = (MWCollectionOrdering) i.next();
            MWCollectionOrdering newOrdering = oldMapping.addOrdering(ordering.getQueryKey());
            newOrdering.setAscending(ordering.isAscending());
            
        }
        this.getContainerPolicy().getDefaultingContainerClass().setContainerClass(oldMapping.getContainerPolicy().getDefaultingContainerClass().getContainerClass());
	}

	
	protected void initializeFromMWIndirectableContainerMapping(MWIndirectableContainerMapping oldMapping) {
		super.initializeFromMWIndirectableContainerMapping(oldMapping);
		
		if (oldMapping.usesTransparentIndirection()) {
			this.setUseTransparentIndirection();
		}
	}
	
	
	// **************** Accessors ***************
	
	public ListIterator orderings() {
        return new CloneListIterator(this.orderings);
	}  
    
    public int orderingsSize() {
        return this.orderings.size();
    }

    public int indexOfOrdering(MWCollectionOrdering ordering) {
        return this.orderings.indexOf(ordering);
    }
    
    public void moveOrderingUp(MWCollectionOrdering ordering) {
        int index = indexOfOrdering(ordering);
        removeOrdering(index);
        addOrdering(index - 1, ordering);
    }
    
    public void moveOrderingDown(MWCollectionOrdering ordering) {
        int index = indexOfOrdering(ordering);
        removeOrdering(ordering);
        addOrdering(index + 1, ordering);
    }
    
    public MWCollectionOrdering addOrdering(MWQueryKey queryKey) {
        MWCollectionOrdering ordering = new MWCollectionOrdering(this);
        ordering.setQueryKey(queryKey);
        addOrdering(orderingsSize(), ordering);
        return ordering;
    }
    
    private void addOrdering(int index, MWCollectionOrdering ordering) {
        addItemToList(index, ordering, this.orderings, ORDERINGS_LIST); 
        
    }
    
    public void removeOrdering(MWCollectionOrdering ordering) {
        removeOrdering(this.orderings.indexOf(ordering));
    }

    public void removeOrdering(int index) {
        removeItemFromList(index, this.orderings, ORDERINGS_LIST);
    }   
		
	public void setUseTransparentIndirection() {
		setIndirectionType(TRANSPARENT_INDIRECTION);	
	}
		
	public void setReferenceDescriptor(MWDescriptor descriptor) {
		super.setReferenceDescriptor(descriptor);
		
		getContainerPolicy().referenceDescriptorChanged(descriptor);
	}
	
	// ************** Queryable interface *************
	
	public boolean usesAnyOf() {
		return true;
	}
	
	public boolean isTraversableForReadAllQueryOrderable() {
		return false;
	}
	
	// **************** Queries ***************
	
	public boolean usesTransparentIndirection() {
		return getIndirectionType() == TRANSPARENT_INDIRECTION;
	}
	
	public boolean isCollectionMapping(){
		return true;
	}
	
	// **************** Behavior **********************************************
	
	protected void forceEjb20Indirection() {
		super.forceEjb20Indirection();
		setUseTransparentIndirection();
	}
		
		
	// **************** Aggregate Support *****************

	protected Collection buildAggregateFieldNameGenerators() {
		Collection generators = super.buildAggregateFieldNameGenerators();
		if (getReference() != null) { 
			Iterator i = getReference().columnPairs();
			while (i.hasNext()) {
				MWColumnPair association = (MWColumnPair) i.next();
				generators.add(new ColumnPairAggregateRuntimeFieldNameGenerator(this, association, false));
			}
		}
		
		return generators;
	}	

	protected boolean fieldIsWritten(MWColumnPair association) {
		return false;
	}
	
	//********* runtime conversion *********
	
	public DatabaseMapping runtimeMapping() {
		CollectionMapping runtimeMapping = (CollectionMapping) super.runtimeMapping();

		// *** Indirection ***
		if (usesTransparentIndirection()) 
			runtimeMapping.setIndirectionPolicy(new TransparentIndirectionPolicy());
		else if (usesValueHolderIndirection())
			runtimeMapping.useBasicIndirection();
		else
			runtimeMapping.dontUseIndirection();
	
        runtimeMapping.setContainerPolicy(this.containerPolicy.runtimeContainerPolicy());
        if (getContainerPolicy().usesSorting() && getContainerPolicy().getComparatorClass() != null) {
        	runtimeMapping.useSortedSetClassName(getContainerPolicy().getDefaultingContainerClass().getContainerClass().getName(), getContainerPolicy().getComparatorClass().getName());
        }

        for (ListIterator i = orderings(); i.hasNext(); ) {
            ((MWCollectionOrdering) i.next()).adjustRuntimeMapping(runtimeMapping);
        }

		return runtimeMapping;
	}
	

	// ************** Problem Handling ****************
	
	protected void addProblemsTo(List newProblems) {
		super.addProblemsTo(newProblems);
		this.checkContainerClassIsValidForTransparentIndirection(newProblems);
		this.addUsesTransparentIndirectionWhileMaintainsBiDirectionalRelationship(newProblems);
	}
	
	public void addWrittenFieldsTo(Collection writtenFields) {
		//m-m and 1-m mappings do not directly write their fields
	}
	// overridden from MWTableReferenceMapping
	protected void mappingAndVariableDontUseIndirectionTest(List newProblems) {
		if (! this.usesValueHolderIndirection() && this.getInstanceVariable().isValueHolder()) {
			newProblems.add(this.buildProblem(ProblemConstants.MAPPING_VALUE_HOLDER_ATTRIBUTE_WITHOUT_VALUE_HOLDER_INDIRECTION));
		}
	} 
	
	// overridden from MWTableReferenceMapping
	protected void mappingAndVariableUseIndirectionTest(List newProblems) {
		if ( ! this.getProject().usesWeaving() && this.usesValueHolderIndirection() && ! getInstanceVariable().isValueHolder()) {
			newProblems.add(this.buildProblem(ProblemConstants.MAPPING_VALUE_HOLDER_INDIRECTION_WITHOUT_VALUE_HOLDER_ATTRIBUTE));
		}
	}
	
	private void checkContainerClassIsValidForTransparentIndirection(List newProblems) {
		if ( ! this.usesTransparentIndirection()) {
			return;
		}
		
		MWClass collectionType = this.getContainerPolicy().getDefaultingContainerClass().getContainerClass();
		if (collectionType != null && ! collectionType.mightBeAssignableToIndirectContainer()) {
			newProblems.add(this.buildProblem(ProblemConstants.MAPPING_CONTAINER_CLASS_INVALID_FOR_TRANSPARENT_INDIRECTION));
		}
	}
	
	private void addUsesTransparentIndirectionWhileMaintainsBiDirectionalRelationship(List newProblems) {
		if (this.maintainsBidirectionalRelationship() && (this.usesNoIndirection() || this.usesValueHolderIndirection())) {
			newProblems.add(this.buildProblem(ProblemConstants.MAPPING_COLLECTION_MAINTTAINSBIDIRECTIONAL_NO_TRANSPARENT_INDIRECTION));
		}
	}

	// **************** TopLink methods ****************

	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();

		descriptor.setJavaClass(MWCollectionMapping.class);
		descriptor.getInheritancePolicy().setParentClass(MWAbstractTableReferenceMapping.class);

		XMLCompositeObjectMapping containerPolicyMapping = new XMLCompositeObjectMapping();
		containerPolicyMapping.setAttributeName("containerPolicy");
		containerPolicyMapping.setReferenceClass(MWContainerPolicy.MWContainerPolicyRoot.class);
		containerPolicyMapping.setXPath("container-policy");
		descriptor.addMapping(containerPolicyMapping);
						
        
        XMLCompositeCollectionMapping orderingsMapping = new XMLCompositeCollectionMapping();
        orderingsMapping.setAttributeName("orderings");
        orderingsMapping.setReferenceClass(MWCollectionOrdering.class);
        orderingsMapping.setXPath("orderings/ordering");
        descriptor.addMapping(orderingsMapping);
	
		return descriptor;
	}
	
}
