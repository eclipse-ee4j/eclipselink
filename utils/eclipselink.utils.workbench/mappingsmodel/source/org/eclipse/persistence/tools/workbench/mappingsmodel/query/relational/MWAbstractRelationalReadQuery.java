/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
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
import org.eclipse.persistence.mappings.ObjectTypeMapping;
import org.eclipse.persistence.mappings.TransformationMapping;
import org.eclipse.persistence.mappings.converters.ObjectTypeConverter;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import deprecated.sdk.SDKAggregateCollectionMapping;
import deprecated.sdk.SDKAggregateObjectMapping;
import deprecated.sdk.SDKFieldValue;
import org.eclipse.persistence.sessions.Record;

public abstract class MWAbstractRelationalReadQuery 
	extends MWAbstractReadQuery
	implements MWRelationalReadQuery {
	
	/**
	 * Allow for the cache usage to be specified to enable in-memory querying.
	 */
	private volatile CacheUsageModel cacheUsage;
		private static TopLinkOptionSet cacheUsageOptions;


	/** Used to determine behaviour of indirection in InMemoryQuerying */
	private volatile InMemoryQueryIndirectionPolicyModel inMemoryQueryIndirectionPolicy;
		private static TopLinkOptionSet inMemoryQueryIndirectionPolicyOptions;

	
	private volatile MWRelationalSpecificQueryOptions relationalOptions;
		
	private List joinedItems;
		public static final String JOINED_ITEMS_LIST = "joinedItems";

	
	private boolean legacy50Prepare;
	private Boolean legacyCacheStatement;
	private Boolean legacyBindAllParameters;
	private MWQueryFormat legacy50QueryFormat;
	private String legacy4XQueryFormatType;
	private String legacy4XQueryString;
	
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

	public static ClassDescriptor legacy50BuildDescriptor() {
		ClassDescriptor descriptor = MWModel.legacy50BuildStandardDescriptor();
		descriptor.descriptorIsAggregate();
		descriptor.setJavaClass(MWAbstractRelationalReadQuery.class);
		descriptor.setTableName("query");
		
		InheritancePolicy inheritancePolicy = (InheritancePolicy) descriptor.getInheritancePolicy();
		inheritancePolicy.setClassIndicatorFieldName("query-type");
		inheritancePolicy.addClassIndicator(MWRelationalReadAllQuery.class, "org.eclipse.persistence.queries.ReadAllQuery");
		inheritancePolicy.addClassIndicator(MWRelationalReadObjectQuery.class, "org.eclipse.persistence.queries.ReadObjectQuery");
		
		// DTF
		descriptor.addDirectMapping("name", "name");
		
		//object type mapping -  cacheUsage
		ObjectTypeMapping cacheUsage = new ObjectTypeMapping();
		cacheUsageOptions().addConversionValuesForTopLinkTo(cacheUsage.getObjectTypeConverter());
		cacheUsage.setAttributeName("cacheUsage");
		cacheUsage.setFieldName("cache-usage");
		descriptor.addMapping(cacheUsage);
		
		//object type mapping -  lockMode
		ObjectTypeMapping lockMode = new ObjectTypeMapping();
		lockingOptions().addConversionValuesForTopLinkTo(lockMode.getObjectTypeConverter());
		lockMode.setAttributeName("lockMode");
		lockMode.setFieldName("lock-mode");
		descriptor.addMapping(lockMode);
			
		//object type mapping -  distinctState
		ObjectTypeMapping distinctState = new ObjectTypeMapping();
		distinctStateOptions().addConversionValuesForTopLinkTo(distinctState.getObjectTypeConverter());
		distinctState.setAttributeName("distinctState");
		distinctState.setFieldName("distinct-state");
		descriptor.addMapping(distinctState);
		
		//object type mapping - inMemoryQueryIndirectionPolicy
		ObjectTypeMapping inMemoryQueryIndirectionPolicy = new ObjectTypeMapping();
		inMemoryQueryIndirectionPolicyOptions().addConversionValuesForTopLinkTo(inMemoryQueryIndirectionPolicy.getObjectTypeConverter());
		inMemoryQueryIndirectionPolicy.setAttributeName("inMemoryQueryIndirectionPolicy");
		inMemoryQueryIndirectionPolicy.setFieldName("in-memory-query-indirection-policy");
		descriptor.addMapping(inMemoryQueryIndirectionPolicy);
		
		
		//object type mapping bindAllParameters
		DirectToFieldMapping bindAllParameters =
			(DirectToFieldMapping) descriptor.addDirectMapping(
				"legacyBindAllParameters",
				"bind-all-parameters");
		bindAllParameters.setNullValue(null);
		
		//object type mapping cacheStatement
		DirectToFieldMapping cacheStatement =
			(DirectToFieldMapping) descriptor.addDirectMapping(
				"legacyCacheStatement",
				"cache-statement");
		cacheStatement.setNullValue(null);
		
		descriptor.addDirectMapping("cacheQueryResults", "cache-query-results");

		ObjectTypeMapping maintainCacheMapping = new ObjectTypeMapping();
		maintainCacheMapping.setAttributeName("maintainCache");
		maintainCacheMapping.setFieldName("maintain-cache");
		maintainCacheMapping.addConversionValue("false", Boolean.FALSE);
		maintainCacheMapping.addConversionValue("undefined", Boolean.TRUE);
		maintainCacheMapping.addConversionValue("true", Boolean.TRUE);
		maintainCacheMapping.setNullValue(Boolean.TRUE);
		maintainCacheMapping.setDefaultAttributeValue(Boolean.TRUE);
		descriptor.addMapping(maintainCacheMapping);

	
		descriptor.addDirectMapping("refreshIdentityMapResult", "refresh-identity-map-result");
		descriptor.addDirectMapping("refreshRemoteIdentityMapResult", "refresh-remote-identity-map-result");
		descriptor.addDirectMapping("useWrapperPolicy", "use-wrapper-policy");
		
		descriptor.addDirectMapping("legacy50Prepare", "prepare");


		DirectToFieldMapping queryTimeoutMapping = new DirectToFieldMapping();
		queryTimeoutMapping.setAttributeName("queryTimeout");
		queryTimeoutMapping.setFieldName("query-timeout");
		queryTimeoutMapping.setGetMethodName("getQueryTimeoutForTopLink");
		queryTimeoutMapping.setSetMethodName("setQueryTimeoutForTopLink");
		descriptor.addMapping(queryTimeoutMapping);

		descriptor.addDirectMapping("maximumRows", "maximum-rows");
		
		// Aggregate mapping - query format
		SDKAggregateObjectMapping formatMapping = new SDKAggregateObjectMapping();
		formatMapping.setAttributeName("legacy50QueryFormat");
		formatMapping.setReferenceClass(MWQueryFormat.class);
		formatMapping.setFieldName("format");
		descriptor.addMapping(formatMapping);
		
		// Aggregate collection mapping - parameterList
		SDKAggregateCollectionMapping parameterListMapping = new SDKAggregateCollectionMapping();
		parameterListMapping.setAttributeName("parameters");
		parameterListMapping.setReferenceClass(MWQueryParameter.class);
		parameterListMapping.setFieldName("parameter-list");
		descriptor.addMapping(parameterListMapping);
		return descriptor;
	}

	public static ClassDescriptor legacy45BuildDescriptor() {
		ClassDescriptor descriptor = MWModel.legacy45BuildStandardDescriptor();
		descriptor.descriptorIsAggregate();
		descriptor.setJavaClass(MWAbstractRelationalReadQuery.class);
		descriptor.setTableName("Query");
	
		InheritancePolicy inheritancePolicy = (InheritancePolicy) descriptor.getInheritancePolicy();
		inheritancePolicy.setClassIndicatorFieldName("queryType");
		inheritancePolicy.addClassIndicator(MWRelationalReadAllQuery.class, "org.eclipse.persistence.queries.ReadAllQuery");
		inheritancePolicy.addClassIndicator(MWRelationalReadObjectQuery.class, "org.eclipse.persistence.queries.ReadObjectQuery");

		// DTF
		descriptor.addDirectMapping("name", "name");
		descriptor.addDirectMapping("cacheQueryResults", "shouldCacheQueryResults");
		descriptor.addDirectMapping("maintainCache", "shouldMaintainCache");
		//this is always false in old projects, because the ui did not have a widget to set this
		//we will use the setter for refreshIdentityMapResult to set this up.  If refreshIdentityMapResult
		//is true, then refresh remote identity map result will be set to true.  This is why the order has changed
		descriptor.addDirectMapping("refreshRemoteIdentityMapResult", "shouldRefreshRemoteIdentityMapResult");
		descriptor.addDirectMapping("refreshIdentityMapResult", "legacyGetRefreshIdentityMapResultForTopLink", "legacySetRefreshIdentityMapResultForTopLink","shouldRefreshIdentityMapResult");	
	
		descriptor.addDirectMapping("legacyBindAllParameters", "shouldBindAllParameters");
		descriptor.addDirectMapping("legacyCacheStatement", "shouldCacheStatement");
		
		
		ObjectTypeMapping cacheUsage = new ObjectTypeMapping();
		cacheUsageOptions().addConversionValuesForTopLink4X(cacheUsage);
		cacheUsage.setAttributeName("cacheUsage");
		cacheUsage.setFieldName("cacheUsage");
		descriptor.addMapping(cacheUsage);
	
	
		//object type mapping -  lockMode
		ObjectTypeMapping lockMode = new ObjectTypeMapping();
		lockingOptions().addConversionValuesForTopLink4X(lockMode);
		lockMode.setAttributeName("lockMode");
		lockMode.setFieldName("lockMode");
		descriptor.addMapping(lockMode);
	
		// Transformation - query format
		TransformationMapping formatMapping = new TransformationMapping();
		formatMapping.setAttributeName("legacy4XQueryFormatType");
		formatMapping.setAttributeTransformation("legacy45GetQueryFormatFromRowForTopLink");
		descriptor.addMapping(formatMapping);		// Aggregate mapping - query format
		
		// Aggregate collection mapping - parameterList
		SDKAggregateCollectionMapping parameterListMapping = new SDKAggregateCollectionMapping();
		parameterListMapping.setAttributeName("parameters");
		parameterListMapping.setReferenceClass(MWQueryParameter.class);
		parameterListMapping.setFieldName("parameterList");
		descriptor.addMapping(parameterListMapping);
	
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
		this.relationalOptions.setQueryFormatToAutoGenerated();
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
	
	
	// ************ TopLink only methods **************
	
	protected void legacy50PostBuild(DescriptorEvent event) {
		super.legacy50PostBuild(event);
		this.relationalOptions = new MWRelationalSpecificQueryOptions(this);
		this.relationalOptions.legacySetQueryFormatForToplink(this.legacy50QueryFormat);
		this.relationalOptions.legacySetPrepareForToplink(this.legacy50Prepare);
		this.relationalOptions.legacySetCacheStatementForToplink(this.legacyCacheStatement);
		this.relationalOptions.legacySetBindAllParametersForToplink(this.legacyBindAllParameters);
		this.joinedItems = new Vector();
	}
	
	protected void legacy45PostBuild(DescriptorEvent event) {
		super.legacy45PostBuild(event);
		this.inMemoryQueryIndirectionPolicy = (InMemoryQueryIndirectionPolicyModel) inMemoryQueryIndirectionPolicyOptions().topLinkOptionForMWModelOption(THROW_INDIRECTION_EXCEPTION);
		this.relationalOptions = new MWRelationalSpecificQueryOptions(this);
		this.relationalOptions.legacy4XSetQueryFormatForToplink(this.legacy4XQueryFormatType, this.legacy4XQueryString);
		this.relationalOptions.legacySetCacheStatementForToplink(this.legacyCacheStatement);
		this.relationalOptions.legacySetBindAllParametersForToplink(this.legacyBindAllParameters);
		this.joinedItems = new Vector();
	}

	private String legacy45GetQueryFormatFromRowForTopLink(Record row) {
		SDKFieldValue queryFormatValue = (SDKFieldValue)row.get("queryFormat");
		Record queryFormatRow = (Record) queryFormatValue.getElements().get(0);
		String queryFormatType = (String) queryFormatRow.get("queryFormatType");
		
		legacy4XQueryString = (String) queryFormatRow.get("queryString");

		return (String) queryFormatRow.get("queryFormatType");
	}

}
