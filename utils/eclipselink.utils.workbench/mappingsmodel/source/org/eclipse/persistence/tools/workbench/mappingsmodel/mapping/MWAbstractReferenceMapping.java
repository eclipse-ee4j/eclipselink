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
package org.eclipse.persistence.tools.workbench.mappingsmodel.mapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWCachingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWDescriptorHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle.NodeReferenceScrubber;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.utility.node.Node;
import org.eclipse.persistence.tools.workbench.utility.string.CollectionStringHolder;
import org.eclipse.persistence.tools.workbench.utility.string.PartialStringComparator;
import org.eclipse.persistence.tools.workbench.utility.string.PartialStringMatcher;
import org.eclipse.persistence.tools.workbench.utility.string.SimplePartialStringMatcher;
import org.eclipse.persistence.tools.workbench.utility.string.PartialStringMatcher.StringHolderScore;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.mappings.converters.ObjectTypeConverter;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;


public abstract class MWAbstractReferenceMapping extends MWMapping 
	implements MWReferenceMapping
{
	private MWDescriptorHandle referenceDescriptorHandle;
	
	protected volatile String indirectionType;		
		
	private volatile boolean privateOwned;


	// **************** Constructors ***************

	/** Default constructor - for TopLink use only */
	protected MWAbstractReferenceMapping() {
		super();
	}
	
	protected MWAbstractReferenceMapping(MWMappingDescriptor descriptor, MWClassAttribute attribute, String attributeName) {
		super(descriptor, attribute, attributeName);
	}

	
	// **************** Initialization ***************
	
	/**
	 * initialize persistent state
	 */
	protected void initialize(Node parent) {
		super.initialize(parent);
		this.referenceDescriptorHandle = new MWDescriptorHandle(this, this.buildReferenceDescriptorScrubber());
		this.privateOwned = false;
	}
	
	protected void initialize(MWClassAttribute attribute, String name) {
		super.initialize(attribute, name);
		if (this.getInstanceVariable().isEjb20Attribute()) {
			this.forceEjb20Indirection();
		} else {
			if (this.getInstanceVariable().isValueHolder()) {
				this.indirectionType = VALUE_HOLDER_INDIRECTION;
			}
			else {
				this.indirectionType = NO_INDIRECTION;
			}
		}
	}

	protected void forceEjb20Indirection() {
		setUseValueHolderIndirection();
	}
	

	// **************** Accessors ***************
	
	public MWDescriptor getReferenceDescriptor() {
		return this.referenceDescriptorHandle.getDescriptor();
	}		

	public void setReferenceDescriptor(MWDescriptor newValue) {
		Object oldValue = this.referenceDescriptorHandle.getDescriptor();
		this.referenceDescriptorHandle.setDescriptor(newValue);
		this.firePropertyChanged(REFERENCE_DESCRIPTOR_PROPERTY, oldValue, newValue);
		
		//newValue will not be null if the method resetReferenceDescriptor called this method.
		//resetReferenceDescriptor is called when morphing mappings.  The mapping's parent would
		//not be set at this point, so we must get the project from a different object.
		if (newValue != null)
			newValue.getProject().notifyExpressionsToRecalculateQueryables();
		else 	
			getProject().notifyExpressionsToRecalculateQueryables();
	}

	public boolean isReferenceMapping(){
		return true;
	}
	
	public boolean usesNoIndirection() {
		return this.indirectionType == NO_INDIRECTION;
	}

	public boolean usesValueHolderIndirection() {
		return this.indirectionType == VALUE_HOLDER_INDIRECTION;
	}

	public void setUseValueHolderIndirection() {
		setIndirectionType(VALUE_HOLDER_INDIRECTION);	
	}

	public void setUseNoIndirection() {
		setIndirectionType(NO_INDIRECTION);
	}

	protected void setIndirectionType(String indirectionType) {
		Object oldValue = this.indirectionType;
		this.indirectionType = indirectionType;
		firePropertyChanged(INDIRECTION_PROPERTY, oldValue, indirectionType);
	}
	
	protected String getIndirectionType() {
		return this.indirectionType;
	}

	// **************** private owned ***************

	public boolean isPrivateOwned() {
		return this.privateOwned;
	}
			
	public void setPrivateOwned(boolean newValue) {
		boolean oldValue = this.privateOwned;
		this.privateOwned = newValue;
		this.firePropertyChanged(PRIVATE_OWNED_PROPERTY, oldValue, newValue);
	}

	//********* mapping morphing support *********

	protected void initializeFromMWReferenceObjectMapping(MWReferenceObjectMapping oldMapping) {
		super.initializeFromMWReferenceObjectMapping(oldMapping);
	
		this.setReferenceDescriptor(oldMapping.getReferenceDescriptor());		
	}
	
	protected void initializeFromMWReferenceMapping(MWReferenceMapping oldMapping) {
		super.initializeFromMWReferenceMapping(oldMapping);
		
		this.setPrivateOwned(oldMapping.isPrivateOwned());
	}

	protected void initializeFromMWIndirectableMapping(MWIndirectableMapping oldMapping) {
		super.initializeFromMWIndirectableMapping(oldMapping);
		
		if (oldMapping.usesValueHolderIndirection()) {
			this.setUseValueHolderIndirection();
		}
		else if (oldMapping.usesNoIndirection()) {
			this.setUseNoIndirection();
		}
	}

	// ************** Automap Support *****************************************
	
	public void automap() {
		super.automap();
		this.automapReferenceDescriptor();
	}
	
	/**
	 * try to find a reasonable reference descriptor
	 */
	private void automapReferenceDescriptor() {
		if (this.getReferenceDescriptor() != null) {
			return;
		}

		MWDescriptor referenceDescriptor = this.findReferenceDescriptor();
		if (referenceDescriptor != null) {
			this.setReferenceDescriptor(referenceDescriptor);
		}
	}
	private MWDescriptor findReferenceDescriptor() {
		MWClass type = this.getInstanceVariable().getType();
		// calculate a name to use for finding a reference descriptor;
		// if the type is "non-descript", use the attribute name;
		// otherwise, use the "short" name of the attribute's declared type
		String name;
		if (type.isPrimitive() ||
			 type.isValueHolder() ||
			 type.isAssignableToMap() ||
			 type.isAssignableToCollection() ||
			 type.isAssignableTo(this.typeFor(Number.class)) ||
			 type == this.typeFor(String.class))
		{
			name = this.getName();
		} else {
			name = type.shortName();
		}

		CollectionStringHolder[] holders = this.buildMultiDescriptorStringHolders();
		StringHolderScore shs = this.match(name.toLowerCase(), holders);
		if (shs.getScore() < 0.80) {	// ???
			return null;
		}
		// look for a descriptor in the same package as this descriptor
		String packageName = this.getParentDescriptor().packageName();
		MWDescriptor descriptor = null;
		for (Iterator stream = ((CollectionStringHolder) shs.getStringHolder()).iterator(); stream.hasNext(); ) {
			descriptor = (MWDescriptor) stream.next();
			if (descriptor.packageName().equals(packageName)) {
				return descriptor;
			}
		}
		// if none of the descriptors is in the same package, take the last one
		return descriptor;
	}

	/**
	 * gather together all the "candidate" descriptors that have
	 * the same "short" name but different packages
	 */
	private CollectionStringHolder[] buildMultiDescriptorStringHolders() {
		Collection descriptors = this.candidateReferenceDescriptors();
		Map holders = new HashMap(descriptors.size());
		for (Iterator stream = descriptors.iterator(); stream.hasNext(); ) {
			MWDescriptor descriptor = (MWDescriptor) stream.next();
			String shortName = descriptor.shortName().toLowerCase();
			CollectionStringHolder holder = (CollectionStringHolder) holders.get(shortName);
			if (holder == null) {
				holder = new CollectionStringHolder(shortName);
				holders.put(shortName, holder);
			}
			holder.add(descriptor);
		}
		return (CollectionStringHolder[]) holders.values().toArray(new CollectionStringHolder[holders.size()]);
	}

	private Collection candidateReferenceDescriptors() {
		Collection descriptors = new ArrayList();
		for (Iterator stream = this.getProject().descriptors(); stream.hasNext(); ) {
			MWDescriptor descriptor = (MWDescriptor) stream.next();
			if (this.descriptorIsValidReferenceDescriptor(descriptor)) {
				descriptors.add(descriptor);
			}
		}
		return descriptors;
	}

	private StringHolderScore match(String string, CollectionStringHolder[] multiDescriptorStringHolders) {
		return PARTIAL_STRING_MATCHER.match(string, multiDescriptorStringHolders);
	}

	private static final PartialStringMatcher PARTIAL_STRING_MATCHER = new SimplePartialStringMatcher(PartialStringComparator.DEFAULT_COMPARATOR);


	//********* containment hierarchy *********

	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.referenceDescriptorHandle);
	}

	private NodeReferenceScrubber buildReferenceDescriptorScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWAbstractReferenceMapping.this.setReferenceDescriptor(null);
			}
			public String toString() {
				return "MWAbstractReferenceMapping.buildReferenceDescriptorScrubber()";
			}
		};
	}

	public void descriptorReplaced(MWDescriptor oldDescriptor, MWDescriptor newDescriptor) {
		super.descriptorReplaced(oldDescriptor, newDescriptor);
		if (this.getReferenceDescriptor() == oldDescriptor) {
			this.setReferenceDescriptor(newDescriptor);
		}
	}
	
	// ************** Problem Handling *************
		
	protected void addProblemsTo(List newProblems) {
		super.addProblemsTo(newProblems);
		this.checkReferenceDescriptor(newProblems);
		this.checkIndirection(newProblems);
	}

	protected void checkReferenceDescriptor(List newProblems)	{
		MWDescriptor refDescriptor = this.getReferenceDescriptor();
		if (refDescriptor == null) {
			newProblems.add(this.buildProblem(ProblemConstants.MAPPING_REFERENCE_DESCRIPTOR_NOT_SPECIFIED));
		} else {
			if ( ! this.descriptorIsValidReferenceDescriptor(refDescriptor)) {
				newProblems.add(this.buildProblem(this.referenceDescriptorInvalidProblemString()));
			}
			if ( ! refDescriptor.isActive()) {
				newProblems.add(this.buildProblem(ProblemConstants.MAPPING_REFERENCE_DESCRIPTOR_IS_INACTIVE, 
					this.getInstanceVariable().getName(), this.getReferenceDescriptor().getMWClass().shortName()));
			}
			this.checkReferenceDescriptorCachIsolation(newProblems);
		}
	}
	
	protected abstract String referenceDescriptorInvalidProblemString();

	protected void checkReferenceDescriptorCachIsolation(List newProblems) {
		MWCachingPolicy policy = this.getParentDescriptor().getTransactionalPolicy().getCachingPolicy();
		String cacheIsolation = policy.getCacheIsolation().getMWModelOption();

		if (cacheIsolation == MWCachingPolicy.CACHE_ISOLATION_PROJECT_DEFAULT) {
			policy = policy.getProject().getDefaultsPolicy().getCachingPolicy();
			cacheIsolation = policy.getCacheIsolation().getMWModelOption();
		}

		// If descriptor is shared then this reference mapping
		// can't access an isolated descriptor
		if (cacheIsolation == MWCachingPolicy.CACHE_ISOLATION_SHARED) {
			policy = this.getReferenceDescriptor().getTransactionalPolicy().getCachingPolicy();
			String referenceCacheIsolation = policy.getCacheIsolation().getMWModelOption();

			if (referenceCacheIsolation == MWCachingPolicy.CACHE_ISOLATION_PROJECT_DEFAULT) {
				policy = policy.getProject().getDefaultsPolicy().getCachingPolicy();
				referenceCacheIsolation = policy.getCacheIsolation().getMWModelOption();
			}

			if (referenceCacheIsolation == MWCachingPolicy.CACHE_ISOLATION_ISOLATED) {
				newProblems.add(this.buildProblem(ProblemConstants.MAPPING_CANNOT_ACCESS_ISOLATED_DESCRIPTOR,
						this.getReferenceDescriptor().getName(), this.getName()));
			}
		}
	}

	//TODO refactor this along with other mappings that test 022 and 044.
	protected void checkIndirection(List newProblems) {
		if (this.usesValueHolderIndirection()) {
			if (this.getInstanceVariable().isTLValueHolder()) {
				newProblems.add(this.buildProblem(ProblemConstants.MAPPING_VALUE_HOLDER_INDIRECTION_WITH_TL_VALUE_HOLDER_ATTRIBUTE));
			} else if ( ! getProject().usesWeaving() && ! this.getInstanceVariable().isValueHolder()) {
				newProblems.add(this.buildProblem(ProblemConstants.MAPPING_VALUE_HOLDER_INDIRECTION_WITHOUT_VALUE_HOLDER_ATTRIBUTE));
			}
		} else {
			if (this.getInstanceVariable().isValueHolder()) {
				newProblems.add(this.buildProblem(ProblemConstants.MAPPING_VALUE_HOLDER_ATTRIBUTE_WITHOUT_VALUE_HOLDER_INDIRECTION));
			}
		}
	}
		
	
	//********* Runtime conversion *********
	
	public DatabaseMapping runtimeMapping() {
		ForeignReferenceMapping runtimeMapping = (ForeignReferenceMapping) super.runtimeMapping();		
		
		if (getReferenceDescriptor() != null) {
			runtimeMapping.setReferenceClassName(getReferenceDescriptor().getMWClass().getName());
		}
		
		if (usesValueHolderIndirection()) {
			runtimeMapping.useBasicIndirection();
		}
		else {
			runtimeMapping.dontUseIndirection();
		}
		runtimeMapping.setIsPrivateOwned(isPrivateOwned());
			
	
		return runtimeMapping;	
	}


	//********* Displaying *********

	public void toString(StringBuffer sb) {
		if (getInstanceVariable() == null) {
			sb.append("<no instance variable>");
		} else {
			sb.append(getInstanceVariable().getName());
		}
		sb.append(" -> ");
		if (getReferenceDescriptor() == null) {
			sb.append("<no reference descriptor selected>");
		} else {
			sb.append(getReferenceDescriptor().getMWClass().shortName());
		}
	}
	
		
	//********* TopLink only methods *********
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();

		descriptor.setJavaClass(MWAbstractReferenceMapping.class);
		descriptor.getInheritancePolicy().setParentClass(MWMapping.class);

		XMLCompositeObjectMapping referenceDescriptorMapping = new XMLCompositeObjectMapping();
		referenceDescriptorMapping.setAttributeName("referenceDescriptorHandle");
		referenceDescriptorMapping.setGetMethodName("getReferenceDescriptorHandleForTopLink");
		referenceDescriptorMapping.setSetMethodName("setReferenceDescriptorHandleForTopLink");
		referenceDescriptorMapping.setReferenceClass(MWDescriptorHandle.class);
		referenceDescriptorMapping.setXPath("reference-descriptor-handle");
		descriptor.addMapping(referenceDescriptorMapping);

		
		ObjectTypeConverter indirectionTypeConverter = new ObjectTypeConverter();
		indirectionTypeConverter.addConversionValue(NO_INDIRECTION, NO_INDIRECTION);
		indirectionTypeConverter.addConversionValue(VALUE_HOLDER_INDIRECTION, VALUE_HOLDER_INDIRECTION);
		//TODO this is not valid for refereceMapping, just for collectionMapping, should we not persist it here?
        indirectionTypeConverter.addConversionValue(MWIndirectableContainerMapping.TRANSPARENT_INDIRECTION, MWIndirectableContainerMapping.TRANSPARENT_INDIRECTION);
        indirectionTypeConverter.addConversionValue(MWProxyIndirectionMapping.PROXY_INDIRECTION, MWProxyIndirectionMapping.PROXY_INDIRECTION);
		XMLDirectMapping indirectionTypeMapping = new XMLDirectMapping();
		indirectionTypeMapping.setAttributeName("indirectionType");
		indirectionTypeMapping.setXPath("indirection-type/text()");
		indirectionTypeMapping.setNullValue(NO_INDIRECTION);
		indirectionTypeMapping.setConverter(indirectionTypeConverter);
		descriptor.addMapping(indirectionTypeMapping);

		XMLDirectMapping privateOwnedMapping = (XMLDirectMapping) descriptor.addDirectMapping("privateOwned", "private-owned/text()");
		privateOwnedMapping.setNullValue(Boolean.FALSE);

		return descriptor;
	}
	
	/**
	 * check for null
	 */
	private MWDescriptorHandle getReferenceDescriptorHandleForTopLink() {
		return (this.referenceDescriptorHandle.getDescriptor() == null) ? null : this.referenceDescriptorHandle;
	}
	private void setReferenceDescriptorHandleForTopLink(MWDescriptorHandle referenceDescriptorHandle) {
		NodeReferenceScrubber scrubber = this.buildReferenceDescriptorScrubber();
		this.referenceDescriptorHandle = ((referenceDescriptorHandle == null) ? new MWDescriptorHandle(this, scrubber) : referenceDescriptorHandle.setScrubber(scrubber));
	}
	
}
