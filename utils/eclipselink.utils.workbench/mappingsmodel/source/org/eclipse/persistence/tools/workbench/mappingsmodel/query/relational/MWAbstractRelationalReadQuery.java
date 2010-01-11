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
package org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.TopLinkOption;
import org.eclipse.persistence.tools.workbench.mappingsmodel.TopLinkOptionSet;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWAbstractReadQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryParameter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWReadQuery;
import org.eclipse.persistence.tools.workbench.utility.TriStateBoolean;
import org.eclipse.persistence.tools.workbench.utility.iterators.CloneListIterator;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.InheritancePolicy;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.TransformationMapping;
import org.eclipse.persistence.mappings.converters.ObjectTypeConverter;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.sessions.Record;

public abstract class MWAbstractRelationalReadQuery 
	extends MWAbstractReadQuery
	implements MWRelationalReadQuery {
	
	/**
	 * Allow for the cache usage to be specified to enable in-memory querying.
	 */
	private volatile CacheUsageModel cacheUsage;
		private static TopLinkOptionSet cacheUsageOptions;


	/** Used to determine behavior of indirection in InMemoryQuerying */
	private volatile InMemoryQueryIndirectionPolicyModel inMemoryQueryIndirectionPolicy;
		private static TopLinkOptionSet inMemoryQueryIndirectionPolicyOptions;

	
	private volatile MWRelationalSpecificQueryOptions relationalOptions;
		
	private List joinedItems;
		public static final String JOINED_ITEMS_LIST = "joinedItems";

	//joining
	
	public static class CacheUsageModel extends TopLinkOption { 

		public CacheUsageModel(String mwModelString, String externalString, int topLinkModelOption) {
			super(mwModelString, externalString, new Integer(topLinkModelOption));
		}
					
		public void setMWOptionOnTopLinkObject(Object query) {
			((ObjectLevelReadQuery) query).setCacheUsage(((Integer) getTopLinkModelOption()).intValue());
		}
	}

	public static class InMemoryQueryIndirectionPolicyModel extends TopLinkOption { 
		
		public InMemoryQueryIndirectionPolicyModel(String mwModelString, String externalString, int topLinkModelOption) {
			super(mwModelString, externalString, new Integer(topLinkModelOption));
		}
		
		public void setMWOptionOnTopLinkObject(Object query) {
			((ObjectLevelReadQuery) query).getInMemoryQueryIndirectionPolicy().setPolicy(((Integer) getTopLinkModelOption()).intValue());
		}
	}
	public synchronized static TopLinkOptionSet cacheUsageOptions() {
		if (cacheUsageOptions == null) {	
			List list = new ArrayList();
            list.add(new CacheUsageModel(UNDEFINED_CACHE_USAGE, "UNDEFINED_CACHE_USAGE_OPTION", ObjectLevelReadQuery.UseDescriptorSetting));
            list.add(new CacheUsageModel(DO_NOT_CHECK_CACHE, "DO_NOT_CHECK_CACHE_OPTION", ObjectLevelReadQuery.DoNotCheckCache));
            list.add(new CacheUsageModel(CHECK_CACHE_BY_EXACT_PRIMARY_KEY, "CHECK_CACHE_BY_EXACT_PRIMARY_KEY_OPTION", ObjectLevelReadQuery.CheckCacheByExactPrimaryKey));
            list.add(new CacheUsageModel(CHECK_CACHE_BY_PRIMARY_KEY, "CHECK_CACHE_BY_PRIMARY_KEY_OPTION", ObjectLevelReadQuery.CheckCacheByPrimaryKey));
            list.add(new CacheUsageModel(CHECK_CACHE_THEN_DATABASE, "CHECK_CACHE_THEN_DATABASE_OPTION",ObjectLevelReadQuery.CheckCacheThenDatabase));
            list.add(new CacheUsageModel(CHECK_CACHE_ONLY, "CHECK_CACHE_ONLY_OPTION", ObjectLevelReadQuery.CheckCacheOnly));
            list.add(new CacheUsageModel(CONFORM_RESULTS_IN_UNIT_OF_WORK, "CONFORM_RESULTS_IN_UNIT_OF_WORK_OPTION", ObjectLevelReadQuery.ConformResultsInUnitOfWork));
			
			cacheUsageOptions = new TopLinkOptionSet(list);
		}
		
		return cacheUsageOptions;
	}
	
	public synchronized static TopLinkOptionSet inMemoryQueryIndirectionPolicyOptions() {
		if (inMemoryQueryIndirectionPolicyOptions == null) {	
            List list = new ArrayList();
            list.add(new InMemoryQueryIndirectionPolicyModel(THROW_INDIRECTION_EXCEPTION, "THROW_INDIRECTION_EXCEPTION_OPTION", ObjectLevelReadQuery.DoNotCheckCache));
            list.add(new InMemoryQueryIndirectionPolicyModel(TRIGGER_INDIRECTION, "TRIGGER_INDIRECTION_OPTION", ObjectLevelReadQuery.CheckCacheByExactPrimaryKey));
            list.add(new InMemoryQueryIndirectionPolicyModel(IGNORE_EXCEPTION_RETURN_CONFORMED, "IGNORE_EXCEPTION_RETURN_CONFORMED_OPTION", ObjectLevelReadQuery.CheckCacheByPrimaryKey));
            list.add(new InMemoryQueryIndirectionPolicyModel(IGNORE_EXCEPTION_RETURN_NOT_CONFORMED, "IGNORE_EXCEPTION_RETURN_NOT_CONFORMED_OPTION", ObjectLevelReadQuery.CheckCacheThenDatabase));
			inMemoryQueryIndirectionPolicyOptions = new TopLinkOptionSet(list);
		}
		
		return inMemoryQueryIndirectionPolicyOptions;
	}

	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWAbstractRelationalReadQuery.class);	
		descriptor.getInheritancePolicy().setParentClass(MWAbstractReadQuery.class);
		
		//object type mapping -  cacheUsage
		XMLDirectMapping cacheUsage = new XMLDirectMapping();
		cacheUsage.setAttributeName("cacheUsage");
		cacheUsage.setXPath("cache-usage/text()");
		ObjectTypeConverter cacheUsageConverter = new ObjectTypeConverter();
		cacheUsageOptions().addConversionValuesForTopLinkTo(cacheUsageConverter);
		cacheUsage.setConverter(cacheUsageConverter);
        cacheUsage.setNullValue(cacheUsageOptions().topLinkOptionForMWModelOption(UNDEFINED_CACHE_USAGE));
		descriptor.addMapping(cacheUsage);
				
		//object type mapping - inMemoryQueryIndirectionPolicy
		XMLDirectMapping inMemoryQueryIndirectionPolicyMapping = new XMLDirectMapping();
        inMemoryQueryIndirectionPolicyMapping.setAttributeName("inMemoryQueryIndirectionPolicy");
        inMemoryQueryIndirectionPolicyMapping.setXPath("in-memory-query-indirection-policy/text()");
		ObjectTypeConverter inMemoryQueryIndirectionPolicyConverter = new ObjectTypeConverter();
		inMemoryQueryIndirectionPolicyOptions().addConversionValuesForTopLinkTo(inMemoryQueryIndirectionPolicyConverter);
        inMemoryQueryIndirectionPolicyMapping.setConverter(inMemoryQueryIndirectionPolicyConverter);
        inMemoryQueryIndirectionPolicyMapping.setNullValue(inMemoryQueryIndirectionPolicyOptions().topLinkOptionForMWModelOption(THROW_INDIRECTION_EXCEPTION));
		descriptor.addMapping(inMemoryQueryIndirectionPolicyMapping);

		XMLCompositeObjectMapping relationalOptionsMaping = new XMLCompositeObjectMapping();
		relationalOptionsMaping.setAttributeName("relationalOptions");
		relationalOptionsMaping.setReferenceClass(MWRelationalSpecificQueryOptions.class);
		relationalOptionsMaping.setXPath("relational-options");
		descriptor.addMapping(relationalOptionsMaping);

		XMLCompositeCollectionMapping joinedItemsMapping = new XMLCompositeCollectionMapping();
		joinedItemsMapping.setAttributeName("joinedItems");
		joinedItemsMapping.setReferenceClass(MWOrderingItem.class);
		joinedItemsMapping.setXPath("joins/joined-item");
		descriptor.addMapping(joinedItemsMapping);

		return descriptor;
	}	

	/** Default constructor - for TopLink use only. */			
	MWAbstractRelationalReadQuery() {
		super();
	}

	MWAbstractRelationalReadQuery(MWRelationalQueryManager queryManager, String name) {
		super(queryManager, name);
	}

	protected void initialize(Node parent) {
		super.initialize(parent);
		inMemoryQueryIndirectionPolicy = (InMemoryQueryIndirectionPolicyModel) inMemoryQueryIndirectionPolicyOptions().topLinkOptionForMWModelOption(THROW_INDIRECTION_EXCEPTION);	
		this.joinedItems = new Vector();
		cacheUsage = (CacheUsageModel) cacheUsageOptions().topLinkOptionForMWModelOption(UNDEFINED_CACHE_USAGE);	
	}
	
	protected void initialize(String name) {
		super.initialize(name);
		this.relationalOptions = new MWRelationalSpecificQueryOptions(this);
		setDefaultQueryFormat();
	}
	
	private void setDefaultQueryFormat() {
		if (isTopLinkReservedFinder()) {
			this.relationalOptions.setQueryFormatToAutoGenerated();
		}
	}
	
	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.relationalOptions);
		synchronized (this.joinedItems) { children.addAll(this.joinedItems); }
	}
	
	
	// ************ Morphing ************	
	
	public void initializeFrom(MWRelationalQuery query) {
		super.initializeFrom(query);
		getRelationalOptions().initializeFrom(query.getRelationalOptions());
	}
	
	public void initializeFrom(MWReadQuery query) {
		super.initializeFrom(query);
		initializeFrom((MWRelationalQuery) query);
	}
	
	
	// ************ accessors ************		

	public MWRelationalSpecificQueryOptions getRelationalOptions() {
		return this.relationalOptions;
	}	

	// ************ Cache Usage ************		

	public CacheUsageModel getCacheUsage() {
		return this.cacheUsage;
	}

	private CacheUsageModel getCacheUsageFromTopLinkModelOption(int topLinkCacheUsage) {
		Iterator cacheUsageModels = cacheUsageOptions().toplinkOptions();
		while (cacheUsageModels.hasNext()) {
			CacheUsageModel model = (CacheUsageModel) cacheUsageModels.next();
			if (((Integer) model.getTopLinkModelOption()).intValue() == topLinkCacheUsage) {
				return model;
			}
		}
		return null;
	}
	
	public void setCacheUsage(CacheUsageModel model) {
		CacheUsageModel oldCacheUsage = this.cacheUsage;
		this.cacheUsage = model;
		firePropertyChanged(CACHE_USAGE_PROPERTY, oldCacheUsage, this.cacheUsage);
	}
	
	public void setCacheUsage(String cacheUsage) {
	    setCacheUsage((CacheUsageModel) cacheUsageOptions().topLinkOptionForMWModelOption(cacheUsage));
	}
	
	private void setCacheUsage(ObjectLevelReadQuery runtimeQuery) {
		setCacheUsage(getCacheUsageFromTopLinkModelOption(runtimeQuery.getCacheUsage()));
	}
	
	// ************ In memory query indirection ************		

	public InMemoryQueryIndirectionPolicyModel getInMemoryQueryIndirectionPolicy() {
		return this.inMemoryQueryIndirectionPolicy;
	}	
	
	
	public void setInMemoryQueryIndirectionPolicy(InMemoryQueryIndirectionPolicyModel inMemoryQueryIndirectionPolicyModel) {
		InMemoryQueryIndirectionPolicyModel oldQueryPolicy = this.inMemoryQueryIndirectionPolicy;
		this.inMemoryQueryIndirectionPolicy = inMemoryQueryIndirectionPolicyModel;
		firePropertyChanged(IN_MEMORY_QUERY_INDIRECTION_PROPERTY, oldQueryPolicy, this.inMemoryQueryIndirectionPolicy);
	}
	
	public void setInMemoryQueryIndirectionPolicy(String inMemoryQueryIndirectionPolicyModel) {
	    setInMemoryQueryIndirectionPolicy((InMemoryQueryIndirectionPolicyModel) inMemoryQueryIndirectionPolicyOptions().topLinkOptionForMWModelOption(inMemoryQueryIndirectionPolicyModel));
	}	
	
	private void setInMemoryQueryIndirectionPolicy(ObjectLevelReadQuery runtimeQuery) {
		int inMemoryQueryIndirectionPolicy = runtimeQuery.getInMemoryQueryIndirectionPolicy().getPolicy();
		Iterator inMemoryQueryIndirectionPolicyOptions = inMemoryQueryIndirectionPolicyOptions().toplinkOptions();
		while (inMemoryQueryIndirectionPolicyOptions.hasNext()) {
			InMemoryQueryIndirectionPolicyModel model = (InMemoryQueryIndirectionPolicyModel) inMemoryQueryIndirectionPolicyOptions.next();
			if (((Integer)model.getTopLinkModelOption()).intValue() == inMemoryQueryIndirectionPolicy) {
				setInMemoryQueryIndirectionPolicy(model);
			}
		}
	}

	
	// ********** joinedItems **********

	public MWJoinedItem addJoinedItem(MWQueryable queryable) {
        if (queryable == null) {
            throw new NullPointerException();
        }
		MWJoinedItem item = new MWJoinedItem(this, queryable);
		addJoinedItem(item);
		return item;
	}
	
	public MWJoinedItem addJoinedItem(Iterator queryables) {
		MWJoinedItem item = new MWJoinedItem(this, queryables);
		addJoinedItem(item);
		return item;
	}
	
	public MWJoinedItem addJoinedItem(Iterator queryables, Iterator allowsNull) {
		MWJoinedItem item = new MWJoinedItem(this, queryables, allowsNull);
		addJoinedItem(item);
		return item;
	}
	
	public MWJoinedItem addJoinedItem(int index, Iterator queryables, Iterator allowsNull) {
		MWJoinedItem item = new MWJoinedItem(this, queryables, allowsNull);
		addJoinedItem(index, item);
		return item;
	}
	
	private void addJoinedItem(MWJoinedItem item) {
		addJoinedItem(joinedItemsSize(), item);	
	}
	
	private void addJoinedItem(int index, MWJoinedItem item) {
		addItemToList(index, item, this.joinedItems, JOINED_ITEMS_LIST);	
	}
	
	public void removeJoinedItem(MWJoinedItem joinedItem) {
		removeJoinedItem(this.joinedItems.indexOf(joinedItem));
	}
	
	public void removeJoinedItem(int index) {
		removeItemFromList(index, this.joinedItems, JOINED_ITEMS_LIST);
	}
	
	public ListIterator joinedItems() {
		return new CloneListIterator(this.joinedItems);
	}	
	
	public int joinedItemsSize() {
		return this.joinedItems.size();
	}	
	
	public int indexOfJoinedItem(MWJoinedItem item) {
		return this.joinedItems.indexOf(item);
	}
	
	public void moveJoinedItemUp(MWJoinedItem item) {
	    int index = indexOfJoinedItem(item);
	    removeJoinedItem(index);
	    addJoinedItem(index - 1, item);
	}
	
	public void moveJoinedItemDown(MWJoinedItem item) {
	    int index = indexOfJoinedItem(item);
	    removeJoinedItem(index);
	    addJoinedItem(index + 1, item);
	}
	
	
	// ********** queryFormat **********
	
	public String getQueryFormatType() {
		return this.relationalOptions.getQueryFormatType();
	}
	
	public void setQueryFormatType(String type) {
		this.relationalOptions.setQueryFormatType(type);
	}

	public MWQueryFormat getQueryFormat() {
		return this.relationalOptions.getQueryFormat();
	}
	
	public TriStateBoolean isCacheStatement() {
		return this.relationalOptions.isCacheStatement();	
	}
	
	public void setCacheStatement(TriStateBoolean cacheStatement) {
		this.relationalOptions.setCacheStatement(cacheStatement);	
	}
		
	public TriStateBoolean isBindAllParameters() {
		return this.relationalOptions.isBindAllParameters();	
	}
	
	public void setBindAllParameters(TriStateBoolean bindAllParameters) {
		this.relationalOptions.setBindAllParameters(bindAllParameters);	
	}

	public boolean isPrepare() {
		return this.relationalOptions.isPrepare();	
	}
	
	public void setPrepare(boolean bindAllParameters) {
		this.relationalOptions.setPrepare(bindAllParameters);	
	}
	
	public void notifyExpressionsToRecalculateQueryables() {
		this.relationalOptions.notifyExpressionsToRecalculateQueryables();	
	}		
	
	// ************ runtime conversion **************
	
	public DatabaseQuery runtimeQuery() {
		ObjectLevelReadQuery runtimeQuery = (ObjectLevelReadQuery) super.runtimeQuery();
		
		getCacheUsage().setMWOptionOnTopLinkObject(runtimeQuery);
		
		getInMemoryQueryIndirectionPolicy().setMWOptionOnTopLinkObject(runtimeQuery);

		getRelationalOptions().adjustRuntimeQuery(runtimeQuery);
        
        for (Iterator i = joinedItems(); i.hasNext(); ) {
            ((MWJoinedItem) i.next()).adjustRuntimeQuery(runtimeQuery);
        }   
		return runtimeQuery;
	}

	public void adjustFromRuntime(ObjectLevelReadQuery runtimeQuery) {
		super.adjustFromRuntime(runtimeQuery);
				
		setCacheUsage(runtimeQuery);
		setInMemoryQueryIndirectionPolicy(runtimeQuery);

		getRelationalOptions().adjustFromRuntime(runtimeQuery);
	}

}
