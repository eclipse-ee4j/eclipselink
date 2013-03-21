/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     12/12/2008-1.1 Guy Pelletier 
 *       - 249860: Implement table per class inheritance support.
 ******************************************************************************/  
package org.eclipse.persistence.descriptors;

import java.io.*;
import java.util.*;

import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.mappings.ManyToManyMapping;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;

/**
 * <p><b>Purpose</b>: Provides the functionality to support a TABLE_PER_CLASS
 * inheritance strategy. Resolves relational mappings and querying.
 */
public class TablePerClassPolicy extends InterfacePolicy implements Serializable, Cloneable {
    /**
     * Selection queries from read all mappings will be cached and re-used.
     * E.G Entity A has a 1-M to Entity D (who has a subclass Entity E). We will 
     * build a selection query to Entity E based on the selection query from the 
     * 1-M mapping from Entity A to Entity D.
     */
    protected HashMap<DatabaseMapping, DatabaseQuery> selectionQueriesForAllObjects;
    
    /**
     * INTERNAL:
     * Create a new policy.
     * Only descriptors involved in inheritance should have a policy.
     */
    public TablePerClassPolicy(ClassDescriptor descriptor) {
        setDescriptor(descriptor);
        selectionQueriesForAllObjects = new HashMap<DatabaseMapping, DatabaseQuery>();
    }
    
    /**
     * INTERNAL:
     */
    protected void addSelectionQuery(ForeignReferenceMapping cloneMapping, ForeignReferenceMapping sourceMapping, AbstractSession session) {
        // Set the new reference class
        cloneMapping.setReferenceClass(getDescriptor().getJavaClass());
        cloneMapping.setReferenceClassName(getDescriptor().getJavaClassName());
        
        // Force the selection criteria to be re-built.
        cloneMapping.setForceInitializationOfSelectionCriteria(true);
            
        // Now initialize the mapping
        cloneMapping.initialize(session);
            
        // The selection query should be initialized with all the right 
        // goodies now, cache it for quick retrieval.
        ObjectLevelReadQuery selectionQuery = (ObjectLevelReadQuery) cloneMapping.getSelectionQuery();
        selectionQuery.getExpressionBuilder().setQueryClassAndDescriptor(descriptor.getJavaClass(), descriptor);

        // By default its source mapping will be the cloned mapping, we
        // need to set the actual source mapping so that we can look it
        // back up correctly.
        selectionQuery.setSourceMapping(sourceMapping);
            
        // Cache the selection query for this source mapping.
        selectionQueriesForAllObjects.put(sourceMapping, selectionQuery);
    }
    
    /**
     * INTERNAL:
     */
    public boolean isTablePerClassPolicy() {
        return true;
    }
    
    /**
     * INTERNAL:
     * This method is called from individual mappings during their 
     * initialization. If the mapping is to a class within a TABLE_PER_CLASS
     * inheritance hierarchy, what this method will do is prepare a selection 
     * query to execute for every child descriptor in the hierarchy.
     * 
     * The selection queries are created by cloning the source mapping,
     * updating the necessary database fields on the mapping and then 
     * initializing the mapping to create the internal selection query. 
     * This query is then cached where needed using the source mapping's
     * selection query name as the key.
     * 
     * @see selectAllObjects(ReadAllQuery)
     * @see selectOneObject(ReadObjectQuery)
     */
    public void prepareChildrenSelectionQuery(DatabaseMapping sourceMapping, AbstractSession session) {
        // From collection mappings we must execute a query on every
        // subclass table to build our results. Tell all children 
        // descriptors to prepare their selection queries.
        for (ClassDescriptor childDescriptor : (Vector<ClassDescriptor>) getChildDescriptors()) {
            childDescriptor.getTablePerClassPolicy().prepareSelectionQuery(sourceMapping, session);
        }
    }
    
    /**
     * INTERNAL:
     */
    protected void prepareManyToManySelectionQuery(ManyToManyMapping sourceMapping, AbstractSession session) {
        // Clone the mapping because in reality that is what we have, that 
        // is, a M-M mapping to each class of the hierarchy.
        ManyToManyMapping manyToMany = (ManyToManyMapping) sourceMapping.clone();
        
        // Update the foreign key fields on the mapping. Basically, take the
        // table name off and let the descriptor figure it out.
        for (DatabaseField keyField : manyToMany.getTargetKeyFields()) {
            keyField.setTable(new DatabaseTable());
        }
        
        addSelectionQuery(manyToMany, sourceMapping, session);
    }
    
    /**
     * INTERNAL:
     */
    protected void prepareOneToManySelectionQuery(OneToManyMapping sourceMapping, AbstractSession session) {
        // Clone the mapping because in reality that is what we have, that 
        // is, a 1-M mapping to each class of the hierarchy.
        OneToManyMapping oneToMany = (OneToManyMapping) sourceMapping.clone();
            
        // Update the foreign key fields on the mapping. Basically, take the
        // table name off and let the descriptor figure it out.
        Vector<DatabaseField> targetForeignKeyFields = new Vector<DatabaseField>();
        for (DatabaseField fkField : oneToMany.getTargetForeignKeysToSourceKeys().keySet()) {
            targetForeignKeyFields.add(new DatabaseField(fkField.getName()));
        }
                    
        // Update our foreign key fields and clear the key maps.
        oneToMany.setTargetForeignKeyFields(targetForeignKeyFields);
        oneToMany.getTargetForeignKeysToSourceKeys().clear();
        oneToMany.getSourceKeysToTargetForeignKeys().clear();
        
        addSelectionQuery(oneToMany, sourceMapping, session);
    }
    
    /**
     * INTERNAL:
     */
    protected void prepareOneToOneSelectionQuery(OneToOneMapping sourceMapping, AbstractSession session) {
        // Clone the mapping because in reality that is what we have, that 
        // is, a 1-1 mapping to each class of the hierarchy.
        OneToOneMapping oneToOne = (OneToOneMapping) sourceMapping.clone();
            
        // Update the target keys to have an empty table (descriptor will figure it out)
        for (DatabaseField targetField : oneToOne.getTargetToSourceKeyFields().keySet()) {
            targetField.setTable(new DatabaseTable());
        }
        
        addSelectionQuery(oneToOne, sourceMapping, session);
    }
    
    /**
     * INTERNAL:
     * The selection queries are created by cloning the source mapping,
     * updating the necessary database fields on the mapping and then 
     * initializing the mapping to create the internal selection query. 
     * This query is then cached where needed using the source mapping 
     * as the key. A prepare is performed for each child of the hierarchy.
     */
    protected void prepareSelectionQuery(DatabaseMapping sourceMapping, AbstractSession session) {
        // Recurse through our child descriptors to set up the selection query
        // before execution.
        for (ClassDescriptor childDescriptor : (Vector<ClassDescriptor>) getChildDescriptors()) {
            childDescriptor.getTablePerClassPolicy().prepareSelectionQuery(sourceMapping, session);
        }
        
        if (sourceMapping.isOneToManyMapping()) {
            prepareOneToManySelectionQuery((OneToManyMapping) sourceMapping, session);
        } else if (sourceMapping.isManyToManyMapping()) {
            prepareManyToManySelectionQuery((ManyToManyMapping) sourceMapping, session);
        } else if (sourceMapping.isOneToOneMapping()) {
            prepareOneToOneSelectionQuery((OneToOneMapping) sourceMapping, session);
        }
        
        // No support for other mappings at this point.
    }
    
    /**
     * INTERNAL:
     * Select all objects for a concrete descriptor.
     */
    @Override
    protected Object selectAllObjects(ReadAllQuery query) { 
        if (this.descriptor.isAbstract()) {
            return query.getContainerPolicy().containerInstance();
        }
        // If we came from a source mapping the execute the selection query
        // we prepared from it.
        if (selectionQueriesForAllObjects.containsKey(query.getSourceMapping())) {
            return query.getExecutionSession().executeQuery(selectionQueriesForAllObjects.get(query.getSourceMapping()), query.getTranslationRow());  
        } else {
            return super.selectAllObjects(query);
        }
    }
    
    /**
     * INTERNAL:
     * Select one object of any concrete subclass.
     */
    @Override
    protected Object selectOneObject(ReadObjectQuery query) throws DescriptorException {
        if (this.descriptor.isAbstract()) {
            return null;
        }
        // If we came from a source mapping the execute the selection query
        // we prepared from it.
        if (selectionQueriesForAllObjects.containsKey(query.getSourceMapping())) {
            return query.getExecutionSession().executeQuery(selectionQueriesForAllObjects.get(query.getSourceMapping()), query.getTranslationRow());  
        } else {
            // Assuming we're doing a find by primary key ...
            // We have to update the translation row to be to the correct field.
            AbstractRecord translationRow = query.getTranslationRow().clone();
            Vector allFields = new Vector();
            
            for (DatabaseField field : translationRow.getFields()) {
                // Remove the table and let the descriptor figure it out.
                allFields.add(new DatabaseField(field.getName()));
            }
            
            translationRow.getFields().clear();
            translationRow.getFields().addAll(allFields);
            return query.getSession().executeQuery(getDescriptor().getQueryManager().getReadObjectQuery(), translationRow);
        }
    }
}
