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

import java.util.Iterator;
import java.util.List;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorQueryManager;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.queries.DeleteObjectQuery;
import org.eclipse.persistence.queries.InsertObjectQuery;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.queries.UpdateObjectQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalTransactionalPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryManager;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWReadAllQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWReadObjectQuery;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

/**
 * This class holds any custom sql the user has created.
 */
public final class MWRelationalQueryManager extends MWQueryManager {

	private volatile MWInsertQuery insertQuery;
		public final static String INSERT_QUERY_PROPERTY = "insertQuery";
	private volatile MWUpdateQuery updateQuery;
		public final static String UPDATE_QUERY_PROPERTY = "updateQuery";
	private volatile MWDeleteQuery deleteQuery;
		public final static String DELETE_QUERY_PROPERTY = "deleteQuery";
	private volatile MWCustomReadObjectQuery readObjectQuery;
		public final static String READ_OBJECT_QUERY_PROPERTY = "readObjectQuery";
	private volatile MWCustomReadAllQuery readAllQuery;
		public final static String READ_ALL_QUERY_PROPERTY = "readAllQuery";
	
        
    private String legacyDescriptorAlias;
    
    
	//Toplink persistence use only please
	private MWRelationalQueryManager() {
		super();
	}	

	public MWRelationalQueryManager(MWRelationalTransactionalPolicy descriptor) {
		super(descriptor);
	}
	
	@Override
	protected void initialize() {
		super.initialize();
		this.insertQuery = new MWInsertQuery(this);
		this.deleteQuery = new MWDeleteQuery(this);
		this.updateQuery = new MWUpdateQuery(this);
		this.readObjectQuery = new MWCustomReadObjectQuery(this);
		this.readAllQuery = new MWCustomReadAllQuery(this);
	}
	
	@Override
	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.insertQuery);
		children.add(this.deleteQuery);
		children.add(this.updateQuery);
		children.add(this.readObjectQuery);
		children.add(this.readAllQuery);
	}
	
	//Persistence
	public static XMLDescriptor buildDescriptor() {
		
		XMLDescriptor descriptor = new XMLDescriptor();
		
		descriptor.setJavaClass(MWRelationalQueryManager.class);
		
		descriptor.getInheritancePolicy().setParentClass(MWQueryManager.class);
		
		//custom queries
		XMLCompositeObjectMapping insertQueryMapping = new XMLCompositeObjectMapping();
		insertQueryMapping.setAttributeName("insertQuery");
		insertQueryMapping.setGetMethodName("getInsertQueryForTopLink");
		insertQueryMapping.setSetMethodName("setInsertQueryForTopLink");
		insertQueryMapping.setReferenceClass(MWInsertQuery.class);
		insertQueryMapping.setXPath("insert-query");
		descriptor.addMapping(insertQueryMapping);

		XMLCompositeObjectMapping deleteQueryMapping = new XMLCompositeObjectMapping();
		deleteQueryMapping.setAttributeName("deleteQuery");
		deleteQueryMapping.setGetMethodName("getDeleteQueryForTopLink");
		deleteQueryMapping.setSetMethodName("setDeleteQueryForTopLink");
		deleteQueryMapping.setReferenceClass(MWDeleteQuery.class);
		deleteQueryMapping.setXPath("delete-query");
		descriptor.addMapping(deleteQueryMapping);

		XMLCompositeObjectMapping updateQueryMapping = new XMLCompositeObjectMapping();
		updateQueryMapping.setAttributeName("updateQuery");
		updateQueryMapping.setGetMethodName("getUpdateQueryForTopLink");
		updateQueryMapping.setSetMethodName("setUpdateQueryForTopLink");
		updateQueryMapping.setReferenceClass(MWUpdateQuery.class);
		updateQueryMapping.setXPath("update-query");
		descriptor.addMapping(updateQueryMapping);

		XMLCompositeObjectMapping readObjectQueryMapping = new XMLCompositeObjectMapping();
		readObjectQueryMapping.setAttributeName("readObjectQuery");
		readObjectQueryMapping.setGetMethodName("getReadObjectQueryForTopLink");
		readObjectQueryMapping.setSetMethodName("setReadObjectQueryForTopLink");
		readObjectQueryMapping.setReferenceClass(MWCustomReadObjectQuery.class);
		readObjectQueryMapping.setXPath("read-object-query");
		descriptor.addMapping(readObjectQueryMapping);

		XMLCompositeObjectMapping readAllQueryMapping = new XMLCompositeObjectMapping();
		readAllQueryMapping.setAttributeName("readAllQuery");
		readAllQueryMapping.setGetMethodName("getReadAllQueryForTopLink");
		readAllQueryMapping.setSetMethodName("setReadAllQueryForTopLink");
		readAllQueryMapping.setReferenceClass(MWCustomReadAllQuery.class);
		readAllQueryMapping.setXPath("read-all-query");
		descriptor.addMapping(readAllQueryMapping);

		descriptor.addDirectMapping("insertSQLString", "legacyGetInsertSQLString", "legacySetInsertSQLString", "insert-string/text()");
		descriptor.addDirectMapping("updateSQLString", "legacyGetUpdateSQLString", "legacySetUpdateSQLString", "update-string/text()");
		descriptor.addDirectMapping("deleteSQLString", "legacyGetDeleteSQLString", "legacySetDeleteSQLString", "delete-string/text()");
		descriptor.addDirectMapping("readObjectSQLString", "legacyGetReadObjectSQLString", "legacySetReadObjectSQLString", "read-object-string/text()");
		descriptor.addDirectMapping("readAllSQLString", "legacyGetReadAllSQLString", "legacySetReadAllSQLString", "read-all-string/text()");

		return descriptor;
	}
	
	public static XMLDescriptor buildStandalone60Descriptor() {
		XMLDescriptor descriptor = MWModel.legacy60BuildStandardDescriptor();

		descriptor.setJavaClass(MWRelationalQueryManager.class);

		descriptor.getInheritancePolicy().setParentClass(MWQueryManager.class);

		descriptor.addDirectMapping("insertSQLString", "legacyGetInsertSQLString", "legacySetInsertSQLString", "insert-string/text()");
		descriptor.addDirectMapping("updateSQLString", "legacyGetUpdateSQLString", "legacySetUpdateSQLString", "update-string/text()");
		descriptor.addDirectMapping("deleteSQLString", "legacyGetDeleteSQLString", "legacySetDeleteSQLString", "delete-string/text()");
		descriptor.addDirectMapping("readObjectSQLString", "legacyGetReadObjectSQLString", "legacySetReadObjectSQLString", "read-object-string/text()");
		descriptor.addDirectMapping("readAllSQLString", "legacyGetReadAllSQLString", "legacySetReadAllSQLString", "read-all-string/text()");

		return descriptor;
	}
	public MWReportQuery addReportQuery(String queryName) {
		return (MWReportQuery) this.addQuery(new MWReportQuery(this, queryName));		
	}
	
	public MWReadAllQuery buildReadAllQuery(String queryName) {
		return new MWRelationalReadAllQuery(this, queryName);
	}
	
	public MWReadObjectQuery buildReadObjectQuery(String queryName) {
		return new MWRelationalReadObjectQuery(this, queryName);
	}

	public boolean supportsReportQueries() {
		return true;
	}
	
	public MWDeleteQuery getDeleteQuery() {
		return this.deleteQuery;
	}

	public MWInsertQuery getInsertQuery() {
		return this.insertQuery;
	}

	public MWCustomReadAllQuery getReadAllQuery() {
		return this.readAllQuery;
	}

	public MWCustomReadObjectQuery getReadObjectQuery(){
		return this.readObjectQuery;
	}

	public MWUpdateQuery getUpdateQuery() {
		return this.updateQuery;
	}

	public void setDeleteQuery(MWDeleteQuery delete) {
		MWDeleteQuery oldDelete = getDeleteQuery();
		this.deleteQuery = delete;
		firePropertyChanged(DELETE_QUERY_PROPERTY, oldDelete, delete);
	}
	
	public void setDeleteSQLString(String sqlString) {
		this.deleteQuery.setSQLString(sqlString);
	}
	
	public void setInsertQuery(MWInsertQuery insert) {
		MWInsertQuery oldInsert = getInsertQuery();
		this.insertQuery = insert;
		firePropertyChanged(INSERT_QUERY_PROPERTY, oldInsert, insert);
	}

	public void setInsertSQLString(String sqlString) {
		this.insertQuery.setSQLString(sqlString);
	}

	public void setReadAllQuery(MWCustomReadAllQuery readAll) {
		MWCustomReadAllQuery oldReadAll = getReadAllQuery();
		this.readAllQuery = readAll;
		firePropertyChanged(READ_ALL_QUERY_PROPERTY, oldReadAll, readAll);
	}

	public void setReadAllSQLString(String sqlString) {
		this.readAllQuery.setSQLString(sqlString);
	}
	
	public void setReadObjectQuery(MWCustomReadObjectQuery readObject) {
		MWCustomReadObjectQuery oldReadObject = getReadObjectQuery();
		this.readObjectQuery = readObject;
		firePropertyChanged(READ_OBJECT_QUERY_PROPERTY, oldReadObject, readObject);
	}

	public void setReadObjectSQLString(String sqlString) {
		this.readObjectQuery.setSQLString(sqlString);
	}
	
	public void setUpdateQuery(MWUpdateQuery update) {
		MWUpdateQuery oldUpdate = getUpdateQuery();
		this.updateQuery = update;
		firePropertyChanged(UPDATE_QUERY_PROPERTY, oldUpdate, update);
	}

	public void setUpdateSQLString(String sqlString) {
		this.updateQuery.setSQLString(sqlString);
	}
	
	public void notifyExpressionsToRecalculateQueryables(){
		for (Iterator queries = queries(); queries.hasNext();) {
			((MWRelationalQuery) queries.next()).notifyExpressionsToRecalculateQueryables();
		}	
	}
	
	//TopLink use only
	private MWInsertQuery getInsertQueryForTopLink() {
		return this.insertQuery;
	}
	
	//TopLink use only
	private MWDeleteQuery getDeleteQueryForTopLink() {
		return this.deleteQuery;
	}

	//TopLink use only
	private MWUpdateQuery getUpdateQueryForTopLink() {
		return this.updateQuery;
	}
	
	//TopLink use only
	private MWCustomReadObjectQuery getReadObjectQueryForTopLink() {
		return this.readObjectQuery;
	}
	
	//TopLink use only
	private MWCustomReadAllQuery getReadAllQueryForTopLink() {
		return this.readAllQuery;
	}

	//TopLink use only
	private void setInsertQueryForTopLink(MWInsertQuery query) {
		if (query != null) {
			this.insertQuery = query;
		}
	}

	//TopLink use only
	private void setDeleteQueryForTopLink(MWDeleteQuery query) {
		if (query != null) {
			this.deleteQuery = query;
		}
	}

	//TopLink use only
	private void setUpdateQueryForTopLink(MWUpdateQuery query) {
		if (query != null) {
			this.updateQuery = query;
		}
	}

	//TopLink use only
	private void setReadObjectQueryForTopLink(MWCustomReadObjectQuery query) {
		if (query != null) {
			this.readObjectQuery = query;
		}
	}

	//TopLink use only
	private void setReadAllQueryForTopLink(MWCustomReadAllQuery query) {
		if (query != null) {
			this.readAllQuery = query;
		}
	}
	
	@Override
	protected void legacy60PostBuild(DescriptorEvent event) {
		super.legacy60PostBuild(event);

		// Checks null since in cases where sql was specified,
		// these object will be not null.
		
		if (this.deleteQuery == null) {
			this.deleteQuery = new MWDeleteQuery(this);
		}
		if (this.insertQuery == null) {
			this.insertQuery = new MWInsertQuery(this);
		}
		if (this.readAllQuery == null) {
			this.readAllQuery = new MWCustomReadAllQuery(this);
		}
		if (this.readObjectQuery == null) {
			this.readObjectQuery =  new MWCustomReadObjectQuery(this);;
		}
		if (this.updateQuery == null) {
			this.updateQuery = new MWUpdateQuery(this);
		}

	}

	//for legacy TopLink use only
	private String legacyGetInsertSQLString() {
		return null;
	}

	//for legacy TopLink use only
	private String legacyGetUpdateSQLString() {
		return null;
	}

	//for legacy TopLink use only
	private String legacyGetDeleteSQLString() {
		return null;
	}

	//for legacy TopLink use only
	private String legacyGetReadObjectSQLString() {
		return null;
	}

	//for legacy TopLink use only
	private String legacyGetReadAllSQLString() {
		return null;
	}

	//for legacy TopLink use only
	private void legacySetInsertSQLString(String sql) {
		if (sql != null) {
			this.insertQuery = new MWInsertQuery(this);
			this.insertQuery.setQueryFormatType(MWRelationalQuery.SQL_FORMAT);
			MWSQLQueryFormat queryFormat = (MWSQLQueryFormat)this.insertQuery.getQueryFormat();
			queryFormat.legacySetQueryStringForTopLink(sql);
		}
	}

	//for legacy TopLink use only
	private void legacySetUpdateSQLString(String sql) {
		if (sql != null) {
			this.updateQuery = new MWUpdateQuery(this);
			this.updateQuery.setQueryFormatType(MWRelationalQuery.SQL_FORMAT);
			MWSQLQueryFormat queryFormat = (MWSQLQueryFormat)this.updateQuery.getQueryFormat();
			queryFormat.legacySetQueryStringForTopLink(sql);
		}
	}

	//for legacy TopLink use only
	private void legacySetDeleteSQLString(String sql) {
		if (sql != null) {
			this.deleteQuery = new MWDeleteQuery(this);
			this.deleteQuery.setQueryFormatType(MWRelationalQuery.SQL_FORMAT);
			MWSQLQueryFormat queryFormat = (MWSQLQueryFormat)this.deleteQuery.getQueryFormat();
			queryFormat.legacySetQueryStringForTopLink(sql);
		}
	}

	//for legacy TopLink use only
	private void legacySetReadObjectSQLString(String sql) {
		if (sql != null) {
			this.readObjectQuery = new MWCustomReadObjectQuery(this);
			this.readObjectQuery.setQueryFormatType(MWRelationalQuery.SQL_FORMAT);
			MWSQLQueryFormat queryFormat = (MWSQLQueryFormat)this.readObjectQuery.getQueryFormat();
			queryFormat.legacySetQueryStringForTopLink(sql);
		}
	}

	//for legacy TopLink use only
	private void legacySetReadAllSQLString(String sql) {
		if (sql != null) {
			this.readAllQuery = new MWCustomReadAllQuery(this);
			this.readAllQuery.setQueryFormatType(MWRelationalQuery.SQL_FORMAT);
			MWSQLQueryFormat queryFormat = (MWSQLQueryFormat)this.readAllQuery.getQueryFormat();
			queryFormat.legacySetQueryStringForTopLink(sql);
		}
	}

	//Conversion methods
	public void adjustRuntimeDescriptor(ClassDescriptor runtimeDescriptor) {		
		super.adjustRuntimeDescriptor(runtimeDescriptor);

		DescriptorQueryManager rtQueryManager = (DescriptorQueryManager) runtimeDescriptor.getQueryManager();

		// Custom Calls
		if (!this.deleteQuery.isContentEmpty()) {
			DeleteObjectQuery rtDeleteQuery = (DeleteObjectQuery)this.deleteQuery.buildRuntimeQuery();
			this.deleteQuery.adjustRuntimeQuery(rtDeleteQuery);
			rtQueryManager.setDeleteQuery(rtDeleteQuery);
		}
		
		if (!this.insertQuery.isContentEmpty()) {
			InsertObjectQuery rtInsertQuery = (InsertObjectQuery)this.insertQuery.buildRuntimeQuery();
			this.insertQuery.adjustRuntimeQuery(rtInsertQuery);
			rtQueryManager.setInsertQuery(rtInsertQuery);
		}
		
		if (!this.updateQuery.isContentEmpty()) {
			UpdateObjectQuery rtUpdateQuery = (UpdateObjectQuery)this.updateQuery.buildRuntimeQuery();
			this.updateQuery.adjustRuntimeQuery(rtUpdateQuery);
			rtQueryManager.setUpdateQuery(rtUpdateQuery);
		}
		
		if (!this.readAllQuery.isContentEmpty()) {
			ReadAllQuery rtReadAllQuery = (ReadAllQuery)this.readAllQuery.buildRuntimeQuery();
			this.readAllQuery.adjustRuntimeQuery(rtReadAllQuery);
			rtQueryManager.setReadAllQuery(rtReadAllQuery);
		}
		
		if (!this.readObjectQuery.isContentEmpty()) {
			ReadObjectQuery rtReadObjectQuery = (ReadObjectQuery)this.readObjectQuery.buildRuntimeQuery();
			this.readObjectQuery.adjustRuntimeQuery(rtReadObjectQuery);
			rtQueryManager.setReadObjectQuery(rtReadObjectQuery);
		}
		
	}
	
	public void adjustFromRuntimeDescriptor(ClassDescriptor runtimeDescriptor) {
		super.adjustFromRuntime(runtimeDescriptor);

		DescriptorQueryManager rtQueryManager = (DescriptorQueryManager) runtimeDescriptor.getQueryManager();
		
		// Custom Calls
		DeleteObjectQuery rtDeleteQuery = (DeleteObjectQuery)this.deleteQuery.buildRuntimeQuery();
		this.deleteQuery.adjustRuntimeQuery(rtDeleteQuery);
		rtQueryManager.setDeleteQuery(rtDeleteQuery);
		
		InsertObjectQuery rtInsertQuery = (InsertObjectQuery)this.insertQuery.buildRuntimeQuery();
		this.insertQuery.adjustRuntimeQuery(rtInsertQuery);
		rtQueryManager.setInsertQuery(rtInsertQuery);
		
		UpdateObjectQuery rtUpdateQuery = (UpdateObjectQuery)this.updateQuery.buildRuntimeQuery();
		this.updateQuery.adjustRuntimeQuery(rtUpdateQuery);
		rtQueryManager.setUpdateQuery(rtUpdateQuery);
		
		ReadAllQuery rtReadAllQuery = (ReadAllQuery)this.readAllQuery.buildRuntimeQuery();
		this.readAllQuery.adjustRuntimeQuery(rtReadAllQuery);
		rtQueryManager.setReadAllQuery(rtReadAllQuery);
		
		ReadObjectQuery rtReadObjectQuery = (ReadObjectQuery)this.readObjectQuery.buildRuntimeQuery();
		this.readObjectQuery.adjustRuntimeQuery(rtReadObjectQuery);
		rtQueryManager.setReadObjectQuery(rtReadObjectQuery);
		

	}

	public static XMLDescriptor legacy60BuildDescriptor() {

		XMLDescriptor descriptor = MWModel.legacy60BuildStandardDescriptor();

		descriptor.setJavaClass(MWRelationalQueryManager.class);

		descriptor.getInheritancePolicy().setParentClass(MWQueryManager.class);

		descriptor.addDirectMapping("insertSQLString", "legacyGetInsertSQLString", "legacySetInsertSQLString", "insert-string/text()");
		descriptor.addDirectMapping("updateSQLString", "legacyGetUpdateSQLString", "legacySetUpdateSQLString", "update-string/text()");
		descriptor.addDirectMapping("deleteSQLString", "legacyGetDeleteSQLString", "legacySetDeleteSQLString", "delete-string/text()");
		descriptor.addDirectMapping("readObjectSQLString", "legacyGetReadObjectSQLString", "legacySetReadObjectSQLString", "read-object-string/text()");
		descriptor.addDirectMapping("readAllSQLString", "legacyGetReadAllSQLString", "legacySetReadAllSQLString", "read-all-string/text()");

		return descriptor;
	}
}
