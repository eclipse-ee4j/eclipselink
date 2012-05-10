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
package org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptorLockingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWEisDescriptorLockingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWOXDescriptorLockingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.descriptors.CMPPolicy;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.InheritancePolicy;
import org.eclipse.persistence.descriptors.PessimisticLockingPolicy;
import org.eclipse.persistence.mappings.converters.ObjectTypeConverter;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;

public abstract class MWDescriptorLockingPolicy extends MWModel implements MWLockingPolicy
{
	private volatile String lockingType;
		
	private volatile String optimisticVersionLockingType;
		public final static String OPTIMISTIC_VERSION_LOCKING_TYPE_PROPERTY = "optimisticVersionLockingType";
		// version optimistic locking
		public final static String OPTIMISTIC_VERSION_VERSION = "Version Locking";
		public final static String OPTIMISTIC_VERSION_TIMESTAMP = "Timestamp Locking";
		public final static String DEFAULT_OPTIMISTIC_VERSION_LOCKING_TYPE = OPTIMISTIC_VERSION_VERSION;
	

	private volatile boolean storeInCache;

    private volatile boolean waitForLock;	
		public final static String WAIT_FOR_LOCK_PROPERTY = "waitForLock";
		
	private volatile String retrieveTimeFrom;
		public final static String RETRIEVE_TIME_FROM_PROPERTY = "retrieveTimeFrom";
		public final static String SERVER_TIME = "Server";
		public final static String LOCAL_TIME = "Local";
		
	
	// ********** static methods **********
	public static XMLDescriptor buildDescriptor()
	{
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.getInheritancePolicy();
		descriptor.setJavaClass(MWDescriptorLockingPolicy.class);
		
		ObjectTypeConverter lockingTypeConverter = new ObjectTypeConverter();
        lockingTypeConverter.addConversionValue(
                NO_LOCKING,
                NO_LOCKING);
		lockingTypeConverter.addConversionValue(
				OPTIMISTIC_LOCKING,
				OPTIMISTIC_LOCKING);
		lockingTypeConverter.addConversionValue(
				PESSIMISTIC_LOCKING,
				PESSIMISTIC_LOCKING);
		XMLDirectMapping lockingTypeMapping = new XMLDirectMapping();
		lockingTypeMapping.setAttributeName("lockingType");
		lockingTypeMapping.setXPath("locking-type/text()");
        lockingTypeMapping.setNullValue(NO_LOCKING);
		lockingTypeMapping.setConverter(lockingTypeConverter);
		descriptor.addMapping(lockingTypeMapping);
		
		
		ObjectTypeConverter versionLockingTypeConverter = new ObjectTypeConverter();
		versionLockingTypeConverter.addConversionValue(
				MWDescriptorLockingPolicy.OPTIMISTIC_VERSION_VERSION,
				MWDescriptorLockingPolicy.OPTIMISTIC_VERSION_VERSION);
		versionLockingTypeConverter.addConversionValue(
				MWDescriptorLockingPolicy.OPTIMISTIC_VERSION_TIMESTAMP,
				MWDescriptorLockingPolicy.OPTIMISTIC_VERSION_TIMESTAMP);
		XMLDirectMapping versionLockingTypeMapping = new XMLDirectMapping();
		versionLockingTypeMapping.setAttributeName("optimisticVersionLockingType");
		versionLockingTypeMapping.setXPath("version-locking-type/text()");
		versionLockingTypeMapping.setConverter(versionLockingTypeConverter);
		descriptor.addMapping(versionLockingTypeMapping);

		ObjectTypeConverter retrieveTimeFromTypeConverter = new ObjectTypeConverter();
		retrieveTimeFromTypeConverter.addConversionValue(
				MWDescriptorLockingPolicy.SERVER_TIME,
				MWDescriptorLockingPolicy.SERVER_TIME);
		retrieveTimeFromTypeConverter.addConversionValue(
				MWDescriptorLockingPolicy.LOCAL_TIME,
				MWDescriptorLockingPolicy.LOCAL_TIME);
		XMLDirectMapping retrieveTimeFromMapping = new XMLDirectMapping();
		retrieveTimeFromMapping.setAttributeName("retrieveTimeFrom");
		retrieveTimeFromMapping.setXPath("retrieve-time-from/text()");
		retrieveTimeFromMapping.setConverter(retrieveTimeFromTypeConverter);
		retrieveTimeFromMapping.setNullValue(MWDescriptorLockingPolicy.SERVER_TIME);
		descriptor.addMapping(retrieveTimeFromMapping);

		((XMLDirectMapping) descriptor.addDirectMapping("storeInCache", "store-in-cache/text()")).setNullValue(Boolean.TRUE);
        ((XMLDirectMapping) descriptor.addDirectMapping("waitForLock", "wait-for-lock/text()")).setNullValue(Boolean.TRUE);
		
		InheritancePolicy ip = (InheritancePolicy)descriptor.getInheritancePolicy();
		ip.setClassIndicatorFieldName("@type");
		ip.addClassIndicator(MWTableDescriptorLockingPolicy.class, "relational");
		ip.addClassIndicator(MWEisDescriptorLockingPolicy.class, "eis");
		ip.addClassIndicator(MWOXDescriptorLockingPolicy.class, "ox");
	
		return descriptor;
	}
 

	protected MWDescriptorLockingPolicy()
	{
		// for TopLink use only
		super();
	}

	public MWDescriptorLockingPolicy(MWTransactionalPolicy descriptor)
	{
		super(descriptor);
	}

	protected void initialize(Node parent)
	{
		super.initialize(parent);
		this.lockingType = DEFAULT_LOCKING_TYPE;
		this.storeInCache = true;
		this.waitForLock = true;
		this.retrieveTimeFrom = SERVER_TIME;
	}

	// ******** accessors *********

	public String getLockingType()
	{
		return this.lockingType;
	}

	public void setLockingType(String newLockingType)
	{
		String oldLockingType = this.lockingType;
		this.lockingType = newLockingType;
		firePropertyChanged(LOCKING_TYPE_PROPERTY, oldLockingType, newLockingType);

		if (attributeValueHasChanged(oldLockingType, this.lockingType)) {
			if (newLockingType != OPTIMISTIC_LOCKING) {
				setOptimisticVersionLockingType(null);
			}
			else {
				setOptimisticVersionLockingType(DEFAULT_OPTIMISTIC_VERSION_LOCKING_TYPE);	
			}
		}
	}
	
	public boolean shouldStoreVersionInCache()
	{
		return this.storeInCache;
	}
	
	public void setStoreInCache(boolean newStoreInCache)
	{
		boolean oldStoreInCache = this.storeInCache;
		this.storeInCache = newStoreInCache;
		firePropertyChanged(STORE_IN_CACHE_PROPERTY, oldStoreInCache, newStoreInCache);
	}
	
	public boolean shouldWaitForLock()
	{
		return this.waitForLock;
	}
	
	public void setWaitForLock(boolean newValue)
	{
		boolean oldWaitForLock = this.waitForLock;
		this.waitForLock = newValue;
		firePropertyChanged(WAIT_FOR_LOCK_PROPERTY, oldWaitForLock, this.waitForLock);
	}



    public MWMappingDescriptor getOwningDescriptor() {
        return (MWMappingDescriptor) ((MWTransactionalPolicy) this.getParent()).getParent();
    }

    public void toString(StringBuffer sb) {
		sb.append(getLockingType());
	}

	public void adjustRuntimeDescriptor(ClassDescriptor runtimeDescriptor)
	{
		if (getLockingType() == PESSIMISTIC_LOCKING)
		{
			if(runtimeDescriptor.getCMPPolicy() == null) {
				runtimeDescriptor.setCMPPolicy(new CMPPolicy());
			}
			runtimeDescriptor.getCMPPolicy().setPessimisticLockingPolicy(new PessimisticLockingPolicy());
			if (shouldWaitForLock()) 
			{
				runtimeDescriptor.getCMPPolicy().getPessimisticLockingPolicy().setLockingMode(ObjectLevelReadQuery.LOCK);
			}
			else 
			{
				runtimeDescriptor.getCMPPolicy().getPessimisticLockingPolicy().setLockingMode(ObjectLevelReadQuery.LOCK_NOWAIT);
			}
		}
	}
	
	protected void addProblemsTo(List problems) {
		super.addProblemsTo(problems);
		this.checkLockFieldSpecifiedForLockingPolicy(problems);
		this.checkWriteLockFieldWritable(problems);
	}
	
	protected abstract void checkLockFieldSpecifiedForLockingPolicy(List newProblems);
	
	protected void checkWriteLockFieldWritable(List newProblems) {
		if (this.getLockingType() != OPTIMISTIC_LOCKING) {
			return;
		}
		if (this.shouldStoreVersionInCache()) {
			return;
		}
		Collection writtenFields = new ArrayList();
		for (Iterator mappings = this.getOwningDescriptor().mappingsIncludingInherited(); mappings.hasNext(); ) {
			MWMapping mapping = (MWMapping) mappings.next();
			if (mapping.isReadOnly()) {
				continue;	// skip to next mapping
			}
			mapping.addWrittenFieldsTo(writtenFields);
			for (Iterator fields = writtenFields.iterator(); fields.hasNext(); ) {
				// find out if there is a writable mapping to the write
				// lock field, if so this test passes
				if (fields.next() == this.getVersionLockField()) {
					return;
				}
			}
			writtenFields.clear();
		}
		// a writable mapping was not found
		newProblems.add(this.buildProblem(ProblemConstants.DESCRIPTOR_LOCKING_FIELD_WRITEABLE));
	}

	public String getOptimisticVersionLockingType()
	{
		return this.optimisticVersionLockingType;
	}

	public void setOptimisticVersionLockingType(String versionType)
	{
		String oldValue = this.optimisticVersionLockingType; 
		this.optimisticVersionLockingType = versionType;
		firePropertyChanged(OPTIMISTIC_VERSION_LOCKING_TYPE_PROPERTY, oldValue, this.optimisticVersionLockingType);
	}
	    
    public String getRetrieveTimeFrom() {
    	return this.retrieveTimeFrom;
    }
    
    public boolean usesServerTime() {
    	return this.retrieveTimeFrom == SERVER_TIME;
    }
    
    public boolean usesLocalTime() {
    	return this.retrieveTimeFrom == LOCAL_TIME;
    }
    
    public void setRetrieveTimeFrom(String newValue) {
    	Object oldValue = this.retrieveTimeFrom;
    	retrieveTimeFrom = newValue;
    	firePropertyChanged(RETRIEVE_TIME_FROM_PROPERTY, oldValue, this.retrieveTimeFrom);
    }
}
