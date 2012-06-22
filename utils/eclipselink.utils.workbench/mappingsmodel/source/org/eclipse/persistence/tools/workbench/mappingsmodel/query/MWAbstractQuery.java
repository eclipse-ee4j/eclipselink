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
package org.eclipse.persistence.tools.workbench.mappingsmodel.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import org.eclipse.persistence.descriptors.DescriptorQueryManager;
import org.eclipse.persistence.descriptors.InheritancePolicy;
import org.eclipse.persistence.mappings.converters.ObjectTypeConverter;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.QueryResultsCachePolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.TopLinkOption;
import org.eclipse.persistence.tools.workbench.mappingsmodel.TopLinkOptionSet;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWRelationalQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWRelationalReadAllQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWRelationalReadObjectQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWReportQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.xml.MWEisReadAllQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.xml.MWEisReadObjectQuery;
import org.eclipse.persistence.tools.workbench.utility.NameTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.CloneListIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationListIterator;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

public abstract class MWAbstractQuery 
	extends MWModel 
	implements 
			MWQuery {

	private volatile String name;

	/**
	 * Paramaters list. The order is important.
	 */
	private List parameters;

	/**
	 * Flag indicating if results of the read should be cached.
	 */
	private volatile boolean cacheQueryResults;

	/**
	 * Flag indicating if should outer join all subclasses
	 */
	private volatile boolean outerJoinAllSubclasses;

	/** 
	 * Used for retrieve limited rows through the query. 
	 * 0 means unlimited
	 */
	private volatile int maximumRows;

	/** 
	 * Used for telling query which result to return first.
	 */
	private volatile int firstResult;

	/** 
	 * Use the query timeout limit setup 
	 * zero means unlimited (by default)
	 */
	//TODO This really should be 2 separate variables.  One would be the queryTimeOutType, the
	//other would be the queryTimeout if the queryTimeOutType is set to QUERY_TIMEOUT_TIMEOUT.
	//The queryTimeout could be stored as an int and queryTimeOutType could be a useful String instead
	//of an Integer.  See MWQueryManager, it as the same problem
	private volatile Integer queryTimeout;

	private volatile boolean exclusiveConnection;
	
	/**
	 * Used for pessimistic locking.
	 */
	private volatile LockingModel lockMode;
		private static TopLinkOptionSet lockingOptions;
	
	
	/** 
	 * Indicates if distinct should be used or not. 
	 */
	private volatile DistinctStateModel distinctState;
		private static TopLinkOptionSet distinctStateOptions;
	
	
	//*********** static methods ************

	public static class LockingModel extends TopLinkOption { 

		public LockingModel(String mwModelString, String externalString, short topLinkModelOption) {
			super(mwModelString, externalString, new Short(topLinkModelOption));
		}
					
		public void setMWOptionOnTopLinkObject(Object query) {
			((ObjectLevelReadQuery) query).setLockMode(((Short) getTopLinkModelOption()).shortValue());
		}
	}
	
	public synchronized static TopLinkOptionSet lockingOptions() {
		if (lockingOptions == null) {	
			List list = new ArrayList();
            list.add(new LockingModel(DEFAULT_LOCK_MODE, "USE_DESCRIPTOR_SETTING_OPTION", ObjectLevelReadQuery.DEFAULT_LOCK_MODE));
            list.add(new LockingModel(LOCK, "ACQUIRE_LOCKS_OPTION", ObjectLevelReadQuery.LOCK));
            list.add(new LockingModel(LOCK_NOWAIT, "ACQUIRE_LOCKS_NO_WAIT_OPTION", ObjectLevelReadQuery.LOCK_NOWAIT));
            list.add(new LockingModel(NO_LOCK, "DONT_ACQUIRE_LOCKS_OPTION", ObjectLevelReadQuery.NO_LOCK));
			lockingOptions = new TopLinkOptionSet(list);
		}
		
		return lockingOptions;
	}
	
	public static class DistinctStateModel extends TopLinkOption { 

		public DistinctStateModel(String mwModelString, String externalString, short topLinkModelOption) {
			super(mwModelString, externalString, new Short(topLinkModelOption));
		}
					
		public void setMWOptionOnTopLinkObject(Object query) {
			((ObjectLevelReadQuery) query).setDistinctState(((Short) getTopLinkModelOption()).shortValue());
		}
	}

	public synchronized static TopLinkOptionSet distinctStateOptions() {
		if (distinctStateOptions == null) {	
            List list = new ArrayList();
            list.add(new DistinctStateModel(UNCOMPUTED_DISTINCT, "UNCOMPUTED_DISTINCT_OPTION", ObjectLevelReadQuery.UNCOMPUTED_DISTINCT));
            list.add(new DistinctStateModel(USE_DISTINCT, "USE_DISTINCT_OPTION", ObjectLevelReadQuery.USE_DISTINCT));
            list.add(new DistinctStateModel(DONT_USE_DISTINCT, "DO_NOT_USE_DISTINCT_OPTION", ObjectLevelReadQuery.DONT_USE_DISTINCT));
			distinctStateOptions = new TopLinkOptionSet(list);
		}
		
		return distinctStateOptions;
	}

	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();

		descriptor.setJavaClass(MWAbstractQuery.class);
			
		InheritancePolicy ip = (InheritancePolicy)descriptor.getInheritancePolicy();
		ip.setClassIndicatorFieldName("@type");
		ip.addClassIndicator(MWRelationalReadAllQuery.class, "relational-read-all");
		ip.addClassIndicator(MWRelationalReadObjectQuery.class, "relational-read-object");
		ip.addClassIndicator(MWEisReadAllQuery.class, "eis-read-all");
		ip.addClassIndicator(MWEisReadObjectQuery.class, "eis-read-object");
		ip.addClassIndicator(MWReportQuery.class, "report");
		
		descriptor.addDirectMapping("name", "name/text()");
		
		XMLCompositeCollectionMapping parameterListMapping = new XMLCompositeCollectionMapping();
		parameterListMapping.setAttributeName("parameters");
		parameterListMapping.setReferenceClass(MWQueryParameter.class);
		parameterListMapping.setXPath("parameter-list/query-parameter");
		descriptor.addMapping(parameterListMapping);
		
		XMLDirectMapping cacheQueryResultsMapping = new XMLDirectMapping();
		cacheQueryResultsMapping.setAttributeName("cacheQueryResults");
		cacheQueryResultsMapping.setXPath("cache-query-results/text()");
		cacheQueryResultsMapping.setNullValue(Boolean.FALSE);
		descriptor.addMapping(cacheQueryResultsMapping);

		XMLDirectMapping outerJoinAllSubclassesMapping = new XMLDirectMapping();
		outerJoinAllSubclassesMapping.setAttributeName("outerJoinAllSubclasses");
		outerJoinAllSubclassesMapping.setXPath("outer-join-all-subclasses/text()");
		outerJoinAllSubclassesMapping.setNullValue(Boolean.FALSE);
		descriptor.addMapping(outerJoinAllSubclassesMapping);

		XMLDirectMapping exclusiveConnectionMapping = new XMLDirectMapping();
		exclusiveConnectionMapping.setAttributeName("exclusiveConnection");
		exclusiveConnectionMapping.setXPath("exclusive-connection/text()");
		exclusiveConnectionMapping.setNullValue(Boolean.FALSE);
		descriptor.addMapping(exclusiveConnectionMapping);
	
		XMLDirectMapping queryTimeoutMapping = new XMLDirectMapping();
		queryTimeoutMapping.setAttributeName("queryTimeout");
		queryTimeoutMapping.setXPath("query-timeout/text()");
		queryTimeoutMapping.setGetMethodName("getQueryTimeoutForTopLink");
		queryTimeoutMapping.setSetMethodName("setQueryTimeoutForTopLink");
		queryTimeoutMapping.setNullValue(QUERY_TIMEOUT_UNDEFINED);
		descriptor.addMapping(queryTimeoutMapping);
		
		XMLDirectMapping maximumRowsMapping = new XMLDirectMapping();
		maximumRowsMapping.setAttributeName("maximumRows");
		maximumRowsMapping.setXPath("maximum-rows/text()");
		maximumRowsMapping.setNullValue(new Integer(0));
		descriptor.addMapping(maximumRowsMapping);

		XMLDirectMapping firstResultMapping = new XMLDirectMapping();
		firstResultMapping.setAttributeName("firstResult");
		firstResultMapping.setXPath("first-result/text()");
		firstResultMapping.setNullValue(new Integer(0));
		descriptor.addMapping(firstResultMapping);

		//object type mapping -  lockMode
		XMLDirectMapping lockingMapping = new XMLDirectMapping();
		lockingMapping.setAttributeName("lockMode");
		lockingMapping.setXPath("lock-mode/text()");
		ObjectTypeConverter lockingConverter = new ObjectTypeConverter();
		lockingOptions().addConversionValuesForTopLinkTo(lockingConverter);
		lockingMapping.setConverter(lockingConverter);
        lockingMapping.setNullValue(lockingOptions().topLinkOptionForMWModelOption(DEFAULT_LOCK_MODE));
		descriptor.addMapping(lockingMapping);
				

		//object type mapping -  distinctState
		XMLDirectMapping distinctStateMapping = new XMLDirectMapping();
		distinctStateMapping.setAttributeName("distinctState");
		distinctStateMapping.setXPath("distinct-state/text()");
		ObjectTypeConverter distinctStateConverter = new ObjectTypeConverter();
		distinctStateOptions().addConversionValuesForTopLinkTo(distinctStateConverter);
		distinctStateMapping.setConverter(distinctStateConverter);
        distinctStateMapping.setNullValue(distinctStateOptions().topLinkOptionForMWModelOption(UNCOMPUTED_DISTINCT));
		descriptor.addMapping(distinctStateMapping);

		return descriptor;
	}

	public static XMLDescriptor legacy60BuildDescriptor() {
		XMLDescriptor descriptor = MWModel.legacy60BuildStandardDescriptor();

		descriptor.setJavaClass(MWAbstractQuery.class);
			
		InheritancePolicy ip = (InheritancePolicy)descriptor.getInheritancePolicy();
		ip.setClassIndicatorFieldName("@type");
		ip.addClassIndicator(MWRelationalReadAllQuery.class, "relational-read-all");
		ip.addClassIndicator(MWRelationalReadObjectQuery.class, "relational-read-object");
		ip.addClassIndicator(MWEisReadAllQuery.class, "eis-read-all");
		ip.addClassIndicator(MWEisReadObjectQuery.class, "eis-read-object");
		ip.addClassIndicator(MWReportQuery.class, "report");
		
		descriptor.addDirectMapping("name", "name/text()");
		
		XMLCompositeCollectionMapping parameterListMapping = new XMLCompositeCollectionMapping();
		parameterListMapping.setAttributeName("parameters");
		parameterListMapping.setReferenceClass(MWQueryParameter.class);
		parameterListMapping.setXPath("parameter-list/query-parameter");
		descriptor.addMapping(parameterListMapping);
		
		XMLDirectMapping cacheQueryResultsMapping = new XMLDirectMapping();
		cacheQueryResultsMapping.setAttributeName("cacheQueryResults");
		cacheQueryResultsMapping.setXPath("cache-query-results/text()");
		cacheQueryResultsMapping.setNullValue(Boolean.FALSE);
		descriptor.addMapping(cacheQueryResultsMapping);

		XMLDirectMapping exclusiveConnectionMapping = new XMLDirectMapping();
		exclusiveConnectionMapping.setAttributeName("exclusiveConnection");
		exclusiveConnectionMapping.setXPath("exclusive-connection/text()");
		exclusiveConnectionMapping.setNullValue(Boolean.FALSE);
		descriptor.addMapping(exclusiveConnectionMapping);
	
		XMLDirectMapping queryTimeoutMapping = new XMLDirectMapping();
		queryTimeoutMapping.setAttributeName("queryTimeout");
		queryTimeoutMapping.setXPath("query-timeout/text()");
		queryTimeoutMapping.setGetMethodName("getQueryTimeoutForTopLink");
		queryTimeoutMapping.setSetMethodName("setQueryTimeoutForTopLink");
		queryTimeoutMapping.setNullValue(QUERY_TIMEOUT_UNDEFINED);
		descriptor.addMapping(queryTimeoutMapping);
		
		XMLDirectMapping maximumRowsMapping = new XMLDirectMapping();
		maximumRowsMapping.setAttributeName("maximumRows");
		maximumRowsMapping.setXPath("maximum-rows/text()");
		maximumRowsMapping.setNullValue(new Integer(0));
		descriptor.addMapping(maximumRowsMapping);
				
		//object type mapping -  lockMode
		XMLDirectMapping lockingMapping = new XMLDirectMapping();
		lockingMapping.setAttributeName("lockMode");
		lockingMapping.setXPath("lock-mode/text()");
		ObjectTypeConverter lockingConverter = new ObjectTypeConverter();
		lockingOptions().addConversionValuesForTopLinkTo(lockingConverter);
		lockingMapping.setConverter(lockingConverter);
        lockingMapping.setNullValue(lockingOptions().topLinkOptionForMWModelOption(DEFAULT_LOCK_MODE));
		descriptor.addMapping(lockingMapping);
				

		//object type mapping -  distinctState
		XMLDirectMapping distinctStateMapping = new XMLDirectMapping();
		distinctStateMapping.setAttributeName("distinctState");
		distinctStateMapping.setXPath("distinct-state/text()");
		ObjectTypeConverter distinctStateConverter = new ObjectTypeConverter();
		distinctStateOptions().addConversionValuesForTopLinkTo(distinctStateConverter);
		distinctStateMapping.setConverter(distinctStateConverter);
        distinctStateMapping.setNullValue(distinctStateOptions().topLinkOptionForMWModelOption(UNCOMPUTED_DISTINCT));
		descriptor.addMapping(distinctStateMapping);

		return descriptor;
	}
	
	/**
	 * Default constructor - for TopLink use only.
	 */	
	protected MWAbstractQuery() {
		super();
	}

	protected MWAbstractQuery(MWQueryManager queryManager, String name) {
		super(queryManager);
		initialize(name);
	}
	
	/**
	 * initialize persistent state
	 */
	protected void initialize(Node parent) { //private-protected 
		super.initialize(parent);
		this.parameters = new Vector();
		this.cacheQueryResults = false;
		this.outerJoinAllSubclasses = false;
		this.maximumRows = 0;
		this.firstResult = 0;
		this.queryTimeout = QUERY_TIMEOUT_UNDEFINED;
		this.exclusiveConnection = false;
		this.lockMode = (LockingModel) lockingOptions().topLinkOptionForMWModelOption(DEFAULT_LOCK_MODE);
		this.distinctState = (DistinctStateModel) distinctStateOptions().topLinkOptionForMWModelOption(UNCOMPUTED_DISTINCT);
	}

	/**
	 * initialize persistent state that depends on the name
	 */
	protected void initialize(String name) {	
		this.name = name;
	}
	
	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		synchronized (this.parameters) { children.addAll(this.parameters); }
	}

	
	
	// ************* MWQuery Implementation ************
	
	public MWMappingDescriptor getOwningDescriptor() {
		return ((MWQueryManager) getParent()).getOwningDescriptor();
	}
	
	public String signature() {
		StringBuffer sb = new StringBuffer(100);
		sb.append(this.getName());
		sb.append('(');
		for (Iterator stream = this.parameters(); stream.hasNext(); ) {
			sb.append(((MWQueryParameter) stream.next()).getType().getName());
			if (stream.hasNext()) {
				sb.append(", ");
			}
		}
		sb.append(')');
		return sb.toString();
	}
	
	/**
	 * if the query's signature has changed the query
	 * has effectively been "renamed"
	 */
	public void signatureChanged() {
		this.getProject().nodeRenamed(this);
		firePropertyChanged(MWQuery.SIGNATURE_PROPERTY, this.signature());
	}

	
	// ************* Morphing ************
	
	public Iterator queryTypes() {
		List list = new ArrayList();
		list.add(READ_ALL_QUERY);
		list.add(READ_OBJECT_QUERY);
		list.add(REPORT_QUERY);
		return list.iterator();
	}
	
	public MWReadAllQuery asReadAllQuery() {
		getQueryManager().removeQuery(this);
		MWReadAllQuery newQuery = getQueryManager().addReadAllQuery(getName());
		((MWAbstractQuery) newQuery).initializeFrom(this);		
		return newQuery;
	}
	
	public MWReadObjectQuery asReadObjectQuery() {
		getQueryManager().removeQuery(this);
		MWReadObjectQuery newQuery = getQueryManager().addReadObjectQuery(getName());
		((MWAbstractQuery) newQuery).initializeFrom(this);		
		return newQuery;
	}
		
	public void initializeFrom(MWQuery query) {
		setCacheQueryResults(query.isCacheQueryResults());
		setExclusiveConnection(query.isExclusiveConnection());
		setOuterJoinAllSubclasses(query.isOuterJoinAllSubclasses());
		setMaximumRows(query.getMaximumRows());
		setFirstResult(query.getFirstResult());
		setQueryTimeout(query.getQueryTimeout());
		for (Iterator i = query.parameters(); i.hasNext();) {
			MWQueryParameter parameter = (MWQueryParameter) i.next();
			//this code can be called several times depending on the situation given the MWQuery hierarchy
			//avoid adding the same parameter multiple times
			if (getParameterNamed(parameter.getName()) != null) {
				removeParameter(getParameterNamed(parameter.getName()));
			}
			addParameter(parameter.getType(), parameter.getName());
		}
		setDistinctState(query.getDistinctState());
		setLocking(query.getLocking());
	}
		
	public void initializeFrom(MWReadQuery query) {
		initializeFrom((MWQuery) query);
	}
	
	public void initializeFrom(MWReadAllQuery query) {
		initializeFrom((MWReadQuery) query);
	}
	
	public void initializeFrom(MWRelationalQuery query) {
		initializeFrom((MWQuery) query);		
	}
	
	public void initializeFrom(MWReportQuery query) {
		initializeFrom((MWRelationalQuery) query);
	}	
	
	public MWQueryManager getQueryManager() {
		return (MWQueryManager) getParent();
	}	

	// ****************** accessors ****************
	
	// ************ name ***********

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

	// ********** parameters **********
	
	public MWQueryParameter addParameter(MWClass type) {
		return addParameter(type, NameTools.uniqueNameFor(PARAMETER_NAME_PREFIX, parameterNames()));
	}
	
	public MWQueryParameter addParameter(MWClass type, String name) {	
		MWQueryParameter parameter = new MWQueryParameter(this, name, type);
		addParameter(parameter);
		signatureChanged();
		return parameter;
	}
	
	private void addParameter(MWQueryParameter parameter) {
        addParameter(parametersSize(), parameter);
	}
		
    private void addParameter(int index, MWQueryParameter parameter) {
        this.parameters.add(index, parameter);
        fireItemAdded(PARAMETERS_LIST, index,  parameter);     
    }
    
	public void removeParameter(MWQueryParameter parameter) {
		this.removeNodeFromList(this.parameters.indexOf(parameter), this.parameters, PARAMETERS_LIST);
		signatureChanged();
	}
    
    public void removeParameters(Iterator params) {
        while (params.hasNext()) {
            this.removeParameter((MWQueryParameter) params.next());
        }
    }

    public void removParameters(Collection params) {
        this.removeParameters(params.iterator());
    }
    
    
	public ListIterator parameters() {
		return new CloneListIterator(this.parameters);
	}	
	
	public int parametersSize() {
		return this.parameters.size();
	}
	
	public int getParameterIndex(MWQueryParameter parameter) {
		return this.parameters.indexOf(parameter);
	}

	public MWQueryParameter getParameter(int index) {
		return (MWQueryParameter) this.parameters.get(index);
	}
		
	public MWQueryParameter getParameterNamed(String name) {
		Iterator parameterIterator = this.parameters.iterator();
		while (parameterIterator.hasNext()) {
			MWQueryParameter queryParameter = (MWQueryParameter) parameterIterator.next();
			if (queryParameter.getName().equals(name))
				return queryParameter;
		}
		return null;
	}

	public Iterator parameterNames() {
		return new TransformationListIterator(parameters()) {
			protected Object transform(Object next) {
				return ((MWQueryParameter) next).getName();
			}
		};
	}
    
    public void moveParameterUp(MWQueryParameter parameter) {
        int index = getParameterIndex(parameter);
        removeParameter(parameter);
        addParameter(index - 1, parameter);
    }
    
    public void moveParameterDown(MWQueryParameter parameter) {
        int index = getParameterIndex(parameter);
        removeParameter(parameter);
        addParameter(index + 1, parameter);
    }   

    
	// ********** maximum rows **********
	
	public int getMaximumRows() {
		return this.maximumRows;
	}
	
	public void setMaximumRows(int maximumRows) {
		int old = this.maximumRows;
		this.maximumRows = maximumRows;
		firePropertyChanged(MAXIMUM_ROWS_PROPERTY, old, this.maximumRows);
	}
	
	// ********** first result **********
	
	public int getFirstResult() {
		return this.firstResult;
	}
	
	public void setFirstResult(int firstResult) {
		int old = this.firstResult;
		this.firstResult = firstResult;
		firePropertyChanged(FIRST_RESULT_PROPERTY, old, this.firstResult);
	}
	
	// ********** query timeout **********

	public Integer getQueryTimeout() {
		return this.queryTimeout;
	}

	public void setQueryTimeout(Integer queryTimeout) {
		Integer oldQueryTimeout = getQueryTimeout();
		this.queryTimeout = queryTimeout;
		firePropertyChanged(QUERY_TIMEOUT_PROPERTY, oldQueryTimeout, queryTimeout);
	}

	// ********** exclusive connection **********

	public boolean isExclusiveConnection() {
		return this.exclusiveConnection;
	}
	
	public void setExclusiveConnection(boolean exclusiveConnection) {
		boolean old = this.exclusiveConnection;
		this.exclusiveConnection = exclusiveConnection;
		firePropertyChanged(EXCLUSIVE_CONNECTION_PROPERTY, old, this.exclusiveConnection);
	}

	// ********** cache query results **********

	public boolean isCacheQueryResults() {
		return this.cacheQueryResults;
	}
	
	public void setCacheQueryResults(boolean cacheQueryResults) {
		boolean old = isCacheQueryResults();
		this.cacheQueryResults = cacheQueryResults;
		firePropertyChanged(CACHE_QUERY_RESULTS_PROPERTY, old, cacheQueryResults);
	}
	
	// ********** outer join all subclasses **********

	public boolean isOuterJoinAllSubclasses() {
		return this.outerJoinAllSubclasses;
	}
	
	public void setOuterJoinAllSubclasses(boolean outerJoinAllSubclasses) {
		boolean old = isOuterJoinAllSubclasses();
		this.outerJoinAllSubclasses = outerJoinAllSubclasses;
		firePropertyChanged(OUTER_JOIN_ALL_SUBCLASSES_PROPERTY, old, outerJoinAllSubclasses);
	}
	
	// ************* distinct state ***********
		
	public DistinctStateModel getDistinctState() {
		return this.distinctState;
	}
	
	public void setDistinctState(DistinctStateModel model) {
		Object old = this.distinctState;
		this.distinctState = model;
		firePropertyChanged(DISTINCT_STATE_PROPERTY, old, this.distinctState);
	}
	
	public void setDistinctState(String distinctState) {
	    setDistinctState((DistinctStateModel) distinctStateOptions.topLinkOptionForMWModelOption(distinctState));
	}
	
	private void setDistinctStateFrom(ObjectLevelReadQuery runtimeQuery) {
		setDistinctState(distinctStateModelFromTopLinkModelOption(runtimeQuery.getDistinctState()));
	}
	
	private DistinctStateModel distinctStateModelFromTopLinkModelOption(short topLinkDistinctState) {
		for (Iterator i = distinctStateOptions().toplinkOptions(); i.hasNext();) {
			DistinctStateModel model = (DistinctStateModel) i.next();
			if (((Short) model.getTopLinkModelOption()).shortValue() == topLinkDistinctState) {
				return model;
			}
		}
		throw new IllegalArgumentException();		
	}
	
	
	// ************* locking ***********

	public LockingModel getLocking() {
		return this.lockMode;
	}
	
	public void setLocking(LockingModel model) {
		LockingModel oldLocking = this.lockMode;
		this.lockMode = model;
		firePropertyChanged(LOCK_MODE_PROPERTY, oldLocking, this.lockMode);
	}
	
	public void setLocking(String locking) {
	    setLocking((LockingModel) lockingOptions().topLinkOptionForMWModelOption(locking));
	}

	private void setLockingFrom(ObjectLevelReadQuery runtimeQuery) {
		setLocking(lockingModelFromTopLinkModelOption(runtimeQuery.getLockMode()));
	}
	
	private LockingModel lockingModelFromTopLinkModelOption(short topLinkLocking) {
		for (Iterator i = lockingOptions().toplinkOptions(); i.hasNext();) {
			LockingModel model = (LockingModel) i.next();
			if (((Short) model.getTopLinkModelOption()).shortValue() == topLinkLocking) {
				return model;
			}
		}
		throw new IllegalArgumentException();		
	}
	
	public boolean isTopLinkReservedFinder() {
		return MWQueryManager.topLinkReservedFinderNames().contains(this.getName());
	}

	// ************ displaying ************
			
	public String displayString() {
		return this.signature();
	}
	
	public void toString(StringBuffer sb) {
		sb.append(signature());
	}
	
	
	// ********* Runtime Conversion ***********

	public DatabaseQuery runtimeQuery() {
		ObjectLevelReadQuery runtimeQuery = buildRuntimeQuery();
		runtimeQuery.setName(getName());

		for (Iterator i = parameters(); i.hasNext(); ) {
			((MWQueryParameter) i.next()).convertToRuntime(runtimeQuery);			
		}
		if (isCacheQueryResults()) {
			runtimeQuery.setQueryResultsCachePolicy(new QueryResultsCachePolicy());
		}
		runtimeQuery.setShouldOuterJoinSubclasses(isOuterJoinAllSubclasses());
		runtimeQuery.setShouldUseExclusiveConnection(isExclusiveConnection());
		if (getMaximumRows() > 0) {
			runtimeQuery.setMaxRows(getMaximumRows());
		}
		if (getFirstResult() > 0) {
			runtimeQuery.setFirstResult(getFirstResult());
		}
		if (getQueryTimeout() != MWQuery.QUERY_TIMEOUT_UNDEFINED) {
			runtimeQuery.setQueryTimeout(getQueryTimeout().intValue());
		}
		
		getLocking().setMWOptionOnTopLinkObject(runtimeQuery);

		getDistinctState().setMWOptionOnTopLinkObject(runtimeQuery);
		
		return runtimeQuery;
	}
	
	protected abstract ObjectLevelReadQuery buildRuntimeQuery();
	
	
	public void adjustFromRuntime(ObjectLevelReadQuery runtimeQuery) {
		this.parameters = new Vector();
		Iterator argIt = runtimeQuery.getArguments().iterator();
		Iterator argTypeIt = runtimeQuery.getArgumentTypes().iterator();
		
		// we have to assume that both collections are the same size
		while (argIt.hasNext() && argTypeIt.hasNext()) {
			String paramName = (String) argIt.next();
			MWClass paramType = typeNamed(((Class) argTypeIt.next()).getName());
			addParameter(new MWQueryParameter(this, paramName, paramType));
		}
			
		setCacheQueryResults(runtimeQuery.shouldCacheQueryResults());
		setMaximumRows(runtimeQuery.getMaxRows());
		setFirstResult(runtimeQuery.getFirstResult());
		if (runtimeQuery.getQueryTimeout() == DescriptorQueryManager.DefaultTimeout) {
			setQueryTimeout(QUERY_TIMEOUT_UNDEFINED);
		}
		else if (runtimeQuery.getQueryTimeout() == DescriptorQueryManager.NoTimeout) {
			setQueryTimeout(QUERY_TIMEOUT_NO_TIMEOUT);
		}
		else {
			setQueryTimeout(new Integer(runtimeQuery.getQueryTimeout()));
		}

		setLockingFrom(runtimeQuery);
		
		setExclusiveConnection(runtimeQuery.shouldUseExclusiveConnection());

		setDistinctStateFrom(runtimeQuery);
	}

	
	// ********* TopLink only methods ***********
	
	
	private Integer getQueryTimeoutForTopLink() {
		return this.queryTimeout;
	}
	
	private void setQueryTimeoutForTopLink(Integer queryTimeout) {
		if (queryTimeout.equals(QUERY_TIMEOUT_NO_TIMEOUT)) {
			this.queryTimeout = QUERY_TIMEOUT_NO_TIMEOUT;
		}
		else if (queryTimeout.equals(QUERY_TIMEOUT_UNDEFINED)) {
			this.queryTimeout = QUERY_TIMEOUT_UNDEFINED;
		}
		else {
			this.queryTimeout = queryTimeout;
		}
	}
	
}
