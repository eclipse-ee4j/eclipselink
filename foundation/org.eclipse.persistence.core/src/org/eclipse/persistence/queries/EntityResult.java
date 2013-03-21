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
 *     Oracle - initial API and implementation from Oracle TopLink
 *     02/08/2012-2.4 Guy Pelletier 
 *       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
 ******************************************************************************/  
package org.eclipse.persistence.queries;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.sessions.DatabaseRecord;

/**
 * <p><b>Purpose</b>:
 * Concrete class to represent the EntityResult structure as defined by
 * the EJB 3.0 Persistence specification.  This class is a subcomponent of the 
 * SQLResultSetMapping
 * 
 * @see SQLResultSetMapping
 * @author Gordon Yorke
 * @since TopLink Java Essentials
 */
public class EntityResult extends SQLResult {
    /** Stores the class name of result  */
    protected String entityClassName;
    protected transient Class entityClass;
    
    /** Stores the list of FieldResult */
    protected Map fieldResults;
    
    /** Stores the column that will contain the value to determine the correct subclass
     * to create if applicable.
     */
    protected DatabaseField discriminatorColumn;
    
    public EntityResult(Class entityClass){
        this.entityClass = entityClass;
        if (this.entityClass == null){
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("null_value_for_entity_result"));
        }
        this.entityClassName = entityClass.getName();
    }
    
    public EntityResult(String entityClassName){
        this.entityClassName = entityClassName;
        if (this.entityClassName == null){
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("null_value_for_entity_result"));
        }
    }
    
    public void addFieldResult(FieldResult fieldResult){
        if (fieldResult == null || fieldResult.getAttributeName() == null){
            return;
        }
        FieldResult existingFieldResult = (FieldResult)getFieldResults().get(fieldResult.getAttributeName());
        if (existingFieldResult==null){
            getFieldResults().put(fieldResult.getAttributeName(), fieldResult);
        }else{
            existingFieldResult.add(fieldResult);
        }
    }
    
    /**
     * INTERNAL:
     * Convert all the class-name-based settings in this query to actual class-based
     * settings. This method is used when converting a project that has been built
     * with class names to a project with classes.
     * @param classLoader 
     */
    public void convertClassNamesToClasses(ClassLoader classLoader){
        super.convertClassNamesToClasses(classLoader);
        Class entityClass = null;
        try{
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                try {
                    entityClass = (Class)AccessController.doPrivileged(new PrivilegedClassForName(entityClassName, true, classLoader));
                } catch (PrivilegedActionException exception) {
                    throw ValidationException.classNotFoundWhileConvertingClassNames(entityClassName, exception.getException());
                }
            } else {
                entityClass = org.eclipse.persistence.internal.security.PrivilegedAccessHelper.getClassForName(entityClassName, true, classLoader);
            }
        } catch (ClassNotFoundException exc){
            throw ValidationException.classNotFoundWhileConvertingClassNames(entityClassName, exc);
        }
        this.entityClass = entityClass;
    };   

    /**
     * Accessor for the internally stored list of FieldResult.  Calling this
     * method will result in a collection being created to store the FieldResult
     */
    public Map getFieldResults(){
        if (this.fieldResults == null){
            this.fieldResults = new HashMap();
        }
        return this.fieldResults;
    }
    
    /**
     * Returns the column name for the column that will store the value used to
     * determine the subclass type if applicable.
     */
    public DatabaseField getDiscriminatorColumn(){
        return this.discriminatorColumn;
    }

    /**
     * Sets the column name for the column that will store the value used to
     * determine the subclass type if applicable.
     */
    public void setDiscriminatorColumn(String column){
        if (column == null){
            return;
        }
        this.discriminatorColumn = new DatabaseField(column);
    }
    
    public void setDiscriminatorColumn(DatabaseField column){
        if (column == null){
            return;
        }
        this.discriminatorColumn = column;
    }

    /**
     * INTERNAL:
     * This method is a convenience method for extracting values from Results
     */
    public Object getValueFromRecord(DatabaseRecord record, ResultSetMappingQuery query){
        // From the row data build result entity.
        // To do this let's collect the column based data for this entity from
        // the results and call build object with this new row.
        ClassDescriptor descriptor = query.getSession().getDescriptor(this.entityClass);
        DatabaseRecord entityRecord = new DatabaseRecord(descriptor.getFields().size());
        if (descriptor.hasInheritance()) {
            Object value = null;
            if (this.discriminatorColumn == null) {
                value = record.get(descriptor.getInheritancePolicy().getClassIndicatorField());
            } else {
                value = record.getIndicatingNoEntry(this.discriminatorColumn);
                if (value == AbstractRecord.noEntry){
                    throw QueryException.discriminatorColumnNotSelected(this.discriminatorColumn.getName(), getSQLResultMapping().getName());
                }
            }            
            entityRecord.put(descriptor.getInheritancePolicy().getClassIndicatorField(), value);
            
            // if multiple types may have been read get the correct descriptor.
            if ( descriptor.getInheritancePolicy().shouldReadSubclasses()) {
                Class classValue = descriptor.getInheritancePolicy().classFromRow(entityRecord, query.getSession());
                descriptor = query.getSession().getDescriptor(classValue);
            }
        }
        for (Iterator mappings = descriptor.getMappings().iterator(); mappings.hasNext();) {
            DatabaseMapping mapping = (DatabaseMapping)mappings.next();
            FieldResult fieldResult = (FieldResult)this.getFieldResults().get(mapping.getAttributeName());
            if (fieldResult != null){
                if (mapping.getFields().size() == 1 ) {
                    entityRecord.put(mapping.getFields().firstElement(), record.get(fieldResult.getColumn()));
                } else if (mapping.getFields().size() >1){
                    getValueFromRecordForMapping(entityRecord,mapping,fieldResult,record);
                }
            } else {
                for (Iterator fields = mapping.getFields().iterator(); fields.hasNext();) {
                    DatabaseField field = (DatabaseField)fields.next();
                    entityRecord.put(field, record.get(field));
                }
            }
        }
        query.setReferenceClass(this.entityClass);
        query.setDescriptor(descriptor);
        return descriptor.getObjectBuilder().buildObject(query, entityRecord, null);
    }

    public boolean isEntityResult(){
        return true;
    }
    
    /**
     * INTERNAL:
     *   This method is for processing all FieldResults for a mapping.  Adds DatabaseFields to the passed in entityRecord
     */
    public void getValueFromRecordForMapping(DatabaseRecord entityRecord,DatabaseMapping mapping, FieldResult fieldResult, DatabaseRecord databaseRecord){
        ClassDescriptor currentDescriptor = mapping.getReferenceDescriptor();
        /** check if this FieldResult contains any other FieldResults, process it if it doesn't */
        if (fieldResult.getFieldResults()==null){
            DatabaseField dbfield = processValueFromRecordForMapping(currentDescriptor,fieldResult.getMultipleFieldIdentifiers(),1);
            /** If it is a 1:1 mapping we need to do the target to source field conversion.  If it is an aggregate, it is fine as it is*/
            if (mapping.isOneToOneMapping()){
                dbfield = (((OneToOneMapping)mapping).getTargetToSourceKeyFields().get(dbfield));
            }
            entityRecord.put(dbfield, databaseRecord.get(fieldResult.getColumn()));
            return;
        }
        /** This processes each FieldResult stored in the collection of FieldResults individually */
        Iterator fieldResults = fieldResult.getFieldResults().iterator();
        while (fieldResults.hasNext()){
            FieldResult tempFieldResult = ((FieldResult)fieldResults.next());
            DatabaseField dbfield = processValueFromRecordForMapping(currentDescriptor,tempFieldResult.getMultipleFieldIdentifiers(),1);
             if (mapping.isOneToOneMapping()){
                dbfield = (((OneToOneMapping)mapping).getTargetToSourceKeyFields().get(dbfield));
            }
            entityRecord.put(dbfield, databaseRecord.get(tempFieldResult.getColumn()));
        }
    }
    
    /**
     * INTERNAL:
     *   This method is for processing a single FieldResult, returning the DatabaseField it refers to.
     */
    public DatabaseField processValueFromRecordForMapping(ClassDescriptor descriptor, String[] attributeNames, int currentLoc){
        DatabaseMapping mapping = descriptor.getObjectBuilder().getMappingForAttributeName(attributeNames[currentLoc]);
        if (mapping==null){throw QueryException.mappingForFieldResultNotFound(attributeNames,currentLoc);}
        currentLoc++;
        if (attributeNames.length!=currentLoc){
            ClassDescriptor currentDescriptor = mapping.getReferenceDescriptor();
            DatabaseField df= processValueFromRecordForMapping(currentDescriptor, attributeNames, currentLoc);
            if (mapping.isOneToOneMapping()){
                return (((OneToOneMapping)mapping).getTargetToSourceKeyFields().get(df));
            }
            return df;
        }else{
            //this is it.. return this mapping's field
            return mapping.getFields().firstElement();
        }
    }
    
}
