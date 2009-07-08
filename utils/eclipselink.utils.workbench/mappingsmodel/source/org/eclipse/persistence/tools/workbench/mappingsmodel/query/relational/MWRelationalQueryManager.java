/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
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

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalTransactionalPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryManager;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWReadAllQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWReadObjectQuery;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorQueryManager;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;

/**
 * This class holds any custom sql the user has created.
 */
public final class MWRelationalQueryManager extends MWQueryManager {

	private volatile String insertSQLString;
		public final static String INSERT_SQL_STRING_PROPERTY = "insertSQLString";
	private volatile String updateSQLString;
		public final static String UPDATE_SQL_STRING_PROPERTY = "updateSQLString";
	private volatile String deleteSQLString;
		public final static String DELETE_SQL_STRING_PROPERTY = "deleteSQLString";
	private volatile String readObjectSQLString;
		public final static String READ_OBJECT_SQL_STRING_PROPERTY = "readObjectSQLString";
	private volatile String readAllSQLString;
		public final static String READ_ALL_SQL_STRING_PROPERTY = "readAllSQLString";
	
        
    private String legacyDescriptorAlias;
    
    
	//Toplink persistence use only please
	private MWRelationalQueryManager() {
		super();
	}	

	public MWRelationalQueryManager(MWRelationalTransactionalPolicy descriptor) {
		super(descriptor);
	}
	
	
	//Persistence
	public static XMLDescriptor buildDescriptor() {
		
		XMLDescriptor descriptor = new XMLDescriptor();
		
		descriptor.setJavaClass(MWRelationalQueryManager.class);
		
		descriptor.getInheritancePolicy().setParentClass(MWQueryManager.class);
		
		descriptor.addDirectMapping("insertSQLString", "insert-string/text()");
		descriptor.addDirectMapping("updateSQLString", "update-string/text()");
		descriptor.addDirectMapping("deleteSQLString", "delete-string/text()");
		descriptor.addDirectMapping("readObjectSQLString", "read-object-string/text()");
		descriptor.addDirectMapping("readAllSQLString", "read-all-string/text()");
		
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
	
	public String getDeleteSQLString() {
		return this.deleteSQLString;
	}
	
	public String getInsertSQLString() {
		return this.insertSQLString;
	}
	
	public String getReadAllSQLString() {
		return this.readAllSQLString;
	}
	
	public String getReadObjectSQLString(){
		return this.readObjectSQLString;
	}

	public String getUpdateSQLString() {
		return this.updateSQLString;
	}

	public void setDeleteSQLString(String deleteSQLString) {
		String oldDeleteSQLString = getDeleteSQLString();
		this.deleteSQLString = deleteSQLString;
		firePropertyChanged(DELETE_SQL_STRING_PROPERTY, oldDeleteSQLString, deleteSQLString);
	}
	public void setInsertSQLString(String insertSQLString) {
		String oldInsertSQLString = getInsertSQLString();
		this.insertSQLString = insertSQLString;
		firePropertyChanged(INSERT_SQL_STRING_PROPERTY, oldInsertSQLString, insertSQLString);
	}
		
	public void setReadAllSQLString(String readAllSQLString) {
		String oldReadAllSQLString = getReadAllSQLString();
		this.readAllSQLString = readAllSQLString;
		firePropertyChanged(READ_ALL_SQL_STRING_PROPERTY, oldReadAllSQLString, readAllSQLString);
	}
	
	public void setReadObjectSQLString(String readObjectSQLString) {
		String oldReadObjectSQLString = getReadObjectSQLString();
		this.readObjectSQLString = readObjectSQLString;
		firePropertyChanged(READ_OBJECT_SQL_STRING_PROPERTY, oldReadObjectSQLString, readObjectSQLString);
	}
	
	public void setUpdateSQLString(String updateSQLString) {
		String oldUpdateSQLString = getUpdateSQLString();
		this.updateSQLString = updateSQLString;
		firePropertyChanged(UPDATE_SQL_STRING_PROPERTY, oldUpdateSQLString, updateSQLString);
	}
		
	public void notifyExpressionsToRecalculateQueryables(){
		for (Iterator queries = queries(); queries.hasNext();) {
			((MWRelationalQuery) queries.next()).notifyExpressionsToRecalculateQueryables();
		}	
	}

	//Conversion methods
	public void adjustRuntimeDescriptor(ClassDescriptor runtimeDescriptor) {		
		super.adjustRuntimeDescriptor(runtimeDescriptor);

		DescriptorQueryManager rtQueryManager = (DescriptorQueryManager) runtimeDescriptor.getQueryManager();

		// Custom SQL
		if (!StringTools.stringIsEmpty(getDeleteSQLString())) {
			rtQueryManager.setDeleteSQLString(getDeleteSQLString());
		}
		if (!StringTools.stringIsEmpty(getInsertSQLString())) {
			rtQueryManager.setInsertSQLString(getInsertSQLString());
		}
		if (!StringTools.stringIsEmpty(getUpdateSQLString())) {
			rtQueryManager.setUpdateSQLString(getUpdateSQLString());
		}
		if (!StringTools.stringIsEmpty(getReadAllSQLString())) {
			rtQueryManager.setReadAllSQLString(getReadAllSQLString());
		}
		if (!StringTools.stringIsEmpty(getReadObjectSQLString())) {
			rtQueryManager.setReadObjectSQLString(getReadObjectSQLString());
		}
	}
	
	public void adjustFromRuntimeDescriptor(ClassDescriptor runtimeDescriptor) {
		super.adjustFromRuntime(runtimeDescriptor);

		DescriptorQueryManager rtQueryManager = (DescriptorQueryManager) runtimeDescriptor.getQueryManager();
		
		// Custom SQL
		setDeleteSQLString(rtQueryManager.getDeleteSQLString());
		setInsertSQLString(rtQueryManager.getInsertSQLString());
		setUpdateSQLString(rtQueryManager.getUpdateSQLString());
		setReadAllSQLString(rtQueryManager.getReadAllSQLString());
		setReadObjectSQLString(rtQueryManager.getReadObjectSQLString());		

	}

}
