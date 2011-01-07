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
package org.eclipse.persistence.tools.workbench.mappingsmodel.query;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWAbstractTransactionalPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWTransactionalPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWRelationalQueryManager;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.xml.MWEisQueryManager;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.xml.MWOXQueryManager;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.CloneIterator;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorQueryManager;
import org.eclipse.persistence.descriptors.InheritancePolicy;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;


/**
 * This class is used to maintain the collection of queries for a particular descriptor.
 * It also holds the descriptor alias and any custom sql the user has created.
 * Each descriptor has a query manager
 */
public abstract class MWQueryManager extends MWModel {

	private Collection queries;
		//property change
		public static final String QUERY_COLLECTION = "queries";

	/**
	 * 0 means no timeout
	 * -1 means default timeout.  This will defer to the parent descriptor's setting.  The runtime takes care of this situation
	 */
	//TODO This really should be 2 separate variables.  One would be the queryTimeOutType, the
	//other would be the queryTimeout if the queryTimeOutType is set to QUERY_TIMEOUT_TIMEOUT.
	//The queryTimeout could be stored as an int and queryTimeOutType could be a useful String instead
	//of an Integer.  See MWQuery, it as the same problem
	private volatile Integer queryTimeout;
		public final static String QUERY_TIMEOUT_PROPERTY = "queryTimeout";
		public final static Integer DEFAULT_QUERY_TIMEOUT = new Integer(-1);
		public final static Integer QUERY_TIMEOUT_NO_TIMEOUT = new Integer(0);
		public final static Integer QUERY_TIMEOUT_TIMEOUT = new Integer(1);


	/**
	 * Default constructor - for TopLink use only.
	 */	
	protected MWQueryManager() {
		super();
	}

	protected MWQueryManager(MWAbstractTransactionalPolicy descriptor) {
	    super(descriptor);
	}

	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		synchronized (this.queries) { children.addAll(this.queries); }
	}

	protected void initialize(Node parent) {
		super.initialize(parent);
		this.queries = new Vector();
		this.queryTimeout = DEFAULT_QUERY_TIMEOUT;	// default value
	}
		
	public MWMappingDescriptor getOwningDescriptor() {
		return (MWMappingDescriptor) ((MWTransactionalPolicy)this.getParent()).getParent();
	}
	

	private void adjustParametersForToplinkReservedFinder(MWQuery query) {
		MWClass objectClass = this.typeFor(Object.class);
		MWClass stringClass = this.typeFor(String.class);
		MWClass vectorClass = this.typeFor(Vector.class);
		
		
		if (query.getName().equals("findByPrimaryKey" )) {
			query.addParameter(objectClass).setName("primaryKey");
		} else if (query.getName().equals("findAll" )) {
			//no parameters
		} else if (query.getName().equals("findOneBySql" )) {
			query.addParameter(stringClass).setName("1");
			query.addParameter(vectorClass).setName("2");
		} else if (query.getName().equals("findManyBySql" )) {
			query.addParameter(stringClass).setName("1");
			query.addParameter(vectorClass).setName("2");
		} else if (query.getName().equals("findOneByEjbql" )) {
			query.addParameter(stringClass).setName("1");
			query.addParameter(vectorClass).setName("2");
		} else if (query.getName().equals("findManyByEjbql" )) {
			query.addParameter(stringClass).setName("1");
			query.addParameter(vectorClass).setName("2");
		} else if (query.getName().equals("findOneByQuery" )) {
			query.addParameter(this.typeFor(ReadObjectQuery.class)).setName("1");
			query.addParameter(vectorClass).setName("2");
		} else if (query.getName().equals("findManyByQuery" )) {
			query.addParameter(this.typeFor(ReadAllQuery.class)).setName("1");
			query.addParameter(vectorClass).setName("2");		
		}
		else {
			throw new IllegalArgumentException();
		}
	}
	
	public MWReadObjectQuery addReadObjectQuery(String queryName) {
		return (MWReadObjectQuery) this.addQuery(buildReadObjectQuery(queryName));		
	}
	
	public MWReadAllQuery addReadAllQuery(String queryName) {
		return (MWReadAllQuery) this.addQuery(buildReadAllQuery(queryName));
	}
	
	public abstract boolean supportsReportQueries();

	public abstract MWReadObjectQuery buildReadObjectQuery(String queryName);
	
	public abstract MWReadAllQuery buildReadAllQuery(String queryName);

	protected MWQuery addQuery(MWQuery query) {
		this.queries.add(query);
		fireItemAdded(QUERY_COLLECTION, query);
				
		return query;
	}
	
	public void removeQuery(MWQuery query) {
		removeItemFromCollection(query,this.queries, QUERY_COLLECTION);
	}
	
	public Iterator queries() 
	{
		return new CloneIterator(this.queries);
	}
	
	public int queriesSize()
	{
		return this.queries.size();
	}

	public MWQuery queryWithSignature(String querySignature) {
		for (Iterator queries = queries(); queries.hasNext(); ) {
			MWQuery query = (MWQuery) queries.next();
			if (query.signature().equals(querySignature)) {
				return query;
			}
		}
		return null;
	}		
		
	public Integer getQueryTimeout() 
	{
		return this.queryTimeout;
	}

	/**
	 * sort the queries for TopLink
	 */
	private Collection getQueriesForTopLink() {
		return CollectionTools.sort((List) this.queries);
	}
	private void setQueriesForTopLink(Collection queries) {
		this.queries = queries;
	}
	
	public void setQueryTimeout(Integer queryTimeout) 
	{
		Integer oldQueryTimeout = getQueryTimeout();
		this.queryTimeout = queryTimeout;
		firePropertyChanged(QUERY_TIMEOUT_PROPERTY, oldQueryTimeout, queryTimeout);
	}


	public static Vector topLinkReservedFinderNames()
	{
		Vector finderNames = new Vector();
		finderNames.add("findByPrimaryKey");
		finderNames.add("findAll");
		finderNames.add("findOneBySql");
		finderNames.add("findManyBySql");
		finderNames.add("findOneByEjbql");
		finderNames.add("findManyByEjbql");
		finderNames.addAll(topLinkReservedFindByQueryFinderNames());
		return finderNames;
	}
	
	public static Vector topLinkReservedFindByQueryFinderNames()
	{
		Vector finderNames = new Vector();
		finderNames.add("findOneByQuery");
		finderNames.add("findManyByQuery");
		return finderNames;
	}
	
	//Problems methods	
	

	
	
	//Persistence
	public static XMLDescriptor buildDescriptor()
	{
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWQueryManager.class);
	
		InheritancePolicy ip = (InheritancePolicy)descriptor.getInheritancePolicy();
		ip.setClassIndicatorFieldName("@type");
		ip.addClassIndicator(MWRelationalQueryManager.class, "relational");
		ip.addClassIndicator(MWEisQueryManager.class, "eis");
		ip.addClassIndicator(MWOXQueryManager.class, "ox");


		// DTFs
		XMLDirectMapping queryTimeoutMapping = new XMLDirectMapping();
		queryTimeoutMapping.setAttributeName("queryTimeout");
		queryTimeoutMapping.setXPath("query-timeout/text()");
		queryTimeoutMapping.setGetMethodName("getQueryTimeoutForTopLink");
		queryTimeoutMapping.setSetMethodName("setQueryTimeoutForTopLink");
        queryTimeoutMapping.setNullValue(DEFAULT_QUERY_TIMEOUT);
		descriptor.addMapping(queryTimeoutMapping);
		
		// Aggregate collection - queries
		XMLCompositeCollectionMapping queriesMapping = new XMLCompositeCollectionMapping();
		queriesMapping.setAttributeName("queries");
		queriesMapping.setGetMethodName("getQueriesForTopLink");
		queriesMapping.setSetMethodName("setQueriesForTopLink");
		queriesMapping.setReferenceClass(MWAbstractQuery.class);
		queriesMapping.setXPath("query-list/query");
		descriptor.addMapping(queriesMapping);
		return descriptor;
	}
	
	private Integer getQueryTimeoutForTopLink() {
		return this.queryTimeout;
	}
	
	private void setQueryTimeoutForTopLink(Integer queryTimeout) {
		if (queryTimeout.equals(QUERY_TIMEOUT_NO_TIMEOUT)) {
			this.queryTimeout = QUERY_TIMEOUT_NO_TIMEOUT;
		}
		else if (queryTimeout.equals(DEFAULT_QUERY_TIMEOUT)) {
			this.queryTimeout = DEFAULT_QUERY_TIMEOUT;
		}
		else {
			this.queryTimeout = queryTimeout;
		}
	}
	// ************* runtime conversion ************
	
	public void adjustRuntimeDescriptor(ClassDescriptor runtimeDescriptor) {		
		DescriptorQueryManager rtQueryManager = (DescriptorQueryManager) runtimeDescriptor.getQueryManager();

		rtQueryManager.setQueryTimeout(getQueryTimeout().intValue());
		for (Iterator i = queries(); i.hasNext(); ) {
			rtQueryManager.addQuery(((MWQuery) i.next()).runtimeQuery());
		}		
	}
	
	public void adjustFromRuntime(ClassDescriptor runtimeDescriptor) {
		DescriptorQueryManager rtQueryManager = (DescriptorQueryManager) runtimeDescriptor.getQueryManager();

        // queries
		this.queries.clear();
		
		//Iterating twice because there can be more than 1 query with the same name just different parameters
		for (Iterator queryVectorIt = rtQueryManager.getQueries().values().iterator(); queryVectorIt.hasNext();)
		{
			Iterator queriesIterator = ((Vector) queryVectorIt.next()).iterator();
			while(queriesIterator.hasNext())
			{
				DatabaseQuery query = (DatabaseQuery) queriesIterator.next();

				MWQuery mwQuery;
				if (query instanceof ReadObjectQuery) {
					mwQuery = addReadObjectQuery(query.getName());
				}
				else if (query instanceof ReadAllQuery){
					mwQuery = addReadAllQuery(query.getName());
				}
				else {
					continue;
				}
				mwQuery.adjustFromRuntime((ObjectLevelReadQuery) query);
			}
		}
	}
	
	

}
