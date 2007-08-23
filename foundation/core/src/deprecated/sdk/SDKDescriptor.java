/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package deprecated.sdk;

import java.util.*;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorQueryManager;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * <code>SDKDescriptor</code> supplements
 * the <code>ClassDescriptor</code> protocol with a number of defaults and
 * helper methods that simplify the use of custom queries/calls
 * (since every query in an <code>SDKDescriptor</code> must be custom).
 * <code>SDKDescriptor</code> also implements support for reading
 * and writing non-relational data from database rows, using
 * <code>SDKFieldValue</code>s.
 *
 * @see SDKCall
 * @see SDKFieldValue
 *
 * @author Big Country
 * @since TOPLink/Java 3.0
 * @deprecated since OracleAS TopLink 10<i>g</i> (10.1.3).  This class is replaced by
 *         {@link org.eclipse.persistence.eis}
 */
public class SDKDescriptor extends ClassDescriptor {

    /**
     * Construct an initialized descriptor.
     */
    public SDKDescriptor() {
        super();
        initialize();
    }

    /**
     * PUBLIC:
     * Add a named read query to the descriptor that uses the specified call and no arguments.
     */
    public void addReadAllCall(String name, SDKCall call) {
        this.addReadAllCall(name, call, new Vector());
    }

    /**
     * PUBLIC:
     * Add a named read query to the descriptor that uses the specified call and argument names.
     */
    public void addReadAllCall(String name, SDKCall call, String[] argumentNames) {
        this.addReadAllCall(name, call, this.convertToVector(argumentNames));
    }

    /**
     * PUBLIC:
     * Add a named read query to the descriptor that uses the specified call and argument name.
     */
    public void addReadAllCall(String name, SDKCall call, String argumentName) {
        Vector argumentNames = new Vector(1);
        argumentNames.addElement(argumentName);
        this.addReadAllCall(name, call, argumentNames);
    }

    /**
     * PUBLIC:
     * Add a named read query to the descriptor that uses the specified call and argument names.
     */
    public void addReadAllCall(String name, SDKCall call, String argumentName1, String argumentName2) {
        Vector argumentNames = new Vector(2);
        argumentNames.addElement(argumentName1);
        argumentNames.addElement(argumentName2);
        this.addReadAllCall(name, call, argumentNames);
    }

    /**
     * PUBLIC:
     * Add a named read query to the descriptor that uses the specified call and argument names.
     */
    public void addReadAllCall(String name, SDKCall call, String argumentName1, String argumentName2, String argumentName3) {
        Vector argumentNames = new Vector(3);
        argumentNames.addElement(argumentName1);
        argumentNames.addElement(argumentName2);
        argumentNames.addElement(argumentName3);
        this.addReadAllCall(name, call, argumentNames);
    }

    /**
     * PUBLIC:
     * Add a named read query to the descriptor that uses the specified call and argument names.
     */
    public void addReadAllCall(String name, SDKCall call, Vector argumentNames) {
        this.addReadQuery(name, this.buildReadAllQuery(call), argumentNames);
    }

    /**
     * PUBLIC:
     * Add a named read query to the descriptor that uses the specified call and no arguments.
     */
    public void addReadObjectCall(String name, SDKCall call) {
        this.addReadObjectCall(name, call, new Vector(3));
    }

    /**
     * PUBLIC:
     * Add a named read query to the descriptor that uses the specified call and argument names.
     */
    public void addReadObjectCall(String name, SDKCall call, String[] argumentNames) {
        this.addReadObjectCall(name, call, this.convertToVector(argumentNames));
    }

    /**
     * PUBLIC:
     * Add a named read query to the descriptor that uses the specified call and argument name.
     */
    public void addReadObjectCall(String name, SDKCall call, String argumentName) {
        Vector argumentNames = new Vector(1);
        argumentNames.addElement(argumentName);
        this.addReadObjectCall(name, call, argumentNames);
    }

    /**
     * PUBLIC:
     * Add a named read query to the descriptor that uses the specified call and argument names.
     */
    public void addReadObjectCall(String name, SDKCall call, String argumentName1, String argumentName2) {
        Vector argumentNames = new Vector(2);
        argumentNames.addElement(argumentName1);
        argumentNames.addElement(argumentName2);
        this.addReadObjectCall(name, call, argumentNames);
    }

    /**
     * PUBLIC:
     * Add a named read query to the descriptor that uses the specified call and argument names.
     */
    public void addReadObjectCall(String name, SDKCall call, String argumentName1, String argumentName2, String argumentName3) {
        Vector argumentNames = new Vector(3);
        argumentNames.addElement(argumentName1);
        argumentNames.addElement(argumentName2);
        argumentNames.addElement(argumentName3);
        this.addReadObjectCall(name, call, argumentNames);
    }

    /**
     * PUBLIC:
     * Add a named read query to the descriptor that uses the specified call and argument names.
     */
    public void addReadObjectCall(String name, SDKCall call, Vector argumentNames) {
        this.addReadQuery(name, this.buildReadObjectQuery(call), argumentNames);
    }

    /**
     * Add a named read query to the descriptor that uses the specified argument names.
     */
    protected void addReadQuery(String name, ObjectLevelReadQuery query, Vector argumentNames) {
        query.setReferenceClass(this.getJavaClass());
        for (Enumeration stream = argumentNames.elements(); stream.hasMoreElements();) {
            query.addArgument((String)stream.nextElement());
        }
        this.getQueryManager().addQuery(name, query);
    }

    /**
     * Add a translation. When a database row is read from the data store,
     * any field with the specified data store field name will be translated to the
     * specified mapping field name. The corresponding write translation will also be added.
     */
    public void addReadTranslation(String dataStoreFieldName, String mappingFieldName) {
        for (Enumeration stream = this.getCustomQueries().elements(); stream.hasMoreElements();) {
            DatabaseQuery query = (DatabaseQuery)stream.nextElement();
            ((SDKQueryMechanism)query.getQueryMechanism()).addReadTranslation(dataStoreFieldName, mappingFieldName);
        }
    }

    /**
     * Add translations. When a database row is read from the data store,
     * any field with the specified data store field name will be translated to the
     * specified mapping field name. The corresponding write translations will also be added.
     */
    public void addReadTranslations(String[] dataStoreFieldNames, String[] mappingFieldNames) {
        for (Enumeration stream = this.getCustomQueries().elements(); stream.hasMoreElements();) {
            DatabaseQuery query = (DatabaseQuery)stream.nextElement();
            ((SDKQueryMechanism)query.getQueryMechanism()).addReadTranslations(dataStoreFieldNames, mappingFieldNames);
        }
    }

    /**
     * Add translations. When a database row is read from the data store,
     * any field with the specified data store field name will be translated to the
     * specified mapping field name. The corresponding write translations will also be added.
     */
    public void addReadTranslations(Vector translations) {
        for (Enumeration stream = this.getCustomQueries().elements(); stream.hasMoreElements();) {
            DatabaseQuery query = (DatabaseQuery)stream.nextElement();
            ((SDKQueryMechanism)query.getQueryMechanism()).addReadTranslations(translations);
        }
    }

    /**
     * Add a translation. When a database row is to be written to the data store,
     * any field with the specified mapping field name will be translated to the
     * specified data store field name. The corresponding read translation will also be added.
     */
    public void addWriteTranslation(String mappingFieldName, String dataStoreFieldName) {
        for (Enumeration stream = this.getCustomQueries().elements(); stream.hasMoreElements();) {
            DatabaseQuery query = (DatabaseQuery)stream.nextElement();
            ((SDKQueryMechanism)query.getQueryMechanism()).addWriteTranslation(mappingFieldName, dataStoreFieldName);
        }
    }

    /**
     * Add translations. When a database row is to be written to the data store,
     * any field with the specified mapping field name will be translated to the
     * specified data store field name. The corresponding read translations will also be added.
     */
    public void addWriteTranslations(String[] mappingFieldNames, String[] dataStoreFieldNames) {
        for (Enumeration stream = this.getCustomQueries().elements(); stream.hasMoreElements();) {
            DatabaseQuery query = (DatabaseQuery)stream.nextElement();
            ((SDKQueryMechanism)query.getQueryMechanism()).addWriteTranslations(mappingFieldNames, dataStoreFieldNames);
        }
    }

    /**
     * Add translations. When a database row is to be written to the data store,
     * any field with the specified mapping field name will be translated to the
     * specified data store field name. The corresponding read translations will also be added.
     */
    public void addWriteTranslations(Vector translations) {
        for (Enumeration stream = this.getCustomQueries().elements(); stream.hasMoreElements();) {
            DatabaseQuery query = (DatabaseQuery)stream.nextElement();
            ((SDKQueryMechanism)query.getQueryMechanism()).addWriteTranslations(translations);
        }
    }

    /**
     * INTERNAL:
     * Extract the direct values from the specified field value.
     * Return them in a vector.
     * The field value better be an SDKFieldValue.
     */
    public Vector buildDirectValuesFromFieldValue(Object fieldValue) throws DatabaseException {
        return ((SDKFieldValue)fieldValue).getElements();
    }

    /**
     * INTERNAL:
     * Build the appropriate field value for the specified
     * set of direct values.
     * The database better be expecting an SDKFieldValue.
     */
    public Object buildFieldValueFromDirectValues(Vector directValues, String elementDataTypeName, AbstractSession session) throws DatabaseException {
        return SDKFieldValue.forDirectValues(directValues, elementDataTypeName);
    }

    /**
     * INTERNAL:
     * Build and return the appropriate field value for the specified
     * set of foreign keys (i.e. each row has the fields that
     * make up a foreign key).
     * The database better be expecting an SDKFieldValue.
     */
    public Object buildFieldValueFromForeignKeys(Vector foreignKeys, String referenceDataTypeName, AbstractSession session) throws DatabaseException {
        return SDKFieldValue.forRecords(foreignKeys, referenceDataTypeName);
    }

    /**
     * INTERNAL:
     * Build and return the field value from the specified nested database row.
     * The database better be expecting an SDKFieldValue.
     */
    public Object buildFieldValueFromNestedRow(AbstractRecord nestedRow, AbstractSession session) throws DatabaseException {
        Vector nestedRows = new Vector(1);
        nestedRows.addElement(nestedRow);
        return this.buildFieldValueFromNestedRows(nestedRows, "", session);
    }

    /**
     * INTERNAL:
     * Build and return the appropriate field value for the specified
     * set of nested rows.
     * The database better be expecting an SDKFieldValue.
     */
    public Object buildFieldValueFromNestedRows(Vector nestedRows, String structureName, AbstractSession session) throws DatabaseException {
        return SDKFieldValue.forRecords(nestedRows, this.getTableName());
    }

    /**
     * INTERNAL:
     * Build and return the nested database row from the specified field value.
     * The field value better be an SDKFieldValue.
     */
    public AbstractRecord buildNestedRowFromFieldValue(Object fieldValue) throws DatabaseException {
        // BUG#2667762 if the tag was empty this could be a string of whitespace.
        if (!(fieldValue instanceof SDKFieldValue)) {
            return new DatabaseRecord(1);
        }
        Vector nestedRows = ((SDKFieldValue)fieldValue).getElements();
        if (nestedRows.isEmpty()) {
            return new DatabaseRecord(1);
        } else {
            // BUG#2667762 if the tag was empty this could be a string of whitespace.
            if (!(nestedRows.firstElement() instanceof AbstractRecord)) {
                return new DatabaseRecord(1);
            }
            return (AbstractRecord)nestedRows.firstElement();
        }
    }

    /**
     * INTERNAL:
     * Build and return the nested rows from the specified field value.
     * The field value better be an SDKFieldValue.
     */
    public Vector buildNestedRowsFromFieldValue(Object fieldValue, AbstractSession session) throws DatabaseException {
        // BUG#2667762 if the tag was empty this could be a string of whitespace.
        if (!(fieldValue instanceof SDKFieldValue)) {
            return new Vector();
        }
        return ((SDKFieldValue)fieldValue).getElements();
    }

    /**
     * Build and return a read-all query for the specified call.
     */
    protected ReadAllQuery buildReadAllQuery(SDKCall call) {
        ReadAllQuery query = new ReadAllQuery();
        query.setCall(call);
        return query;
    }

    /**
     * Build and return a read object query for the specified call.
     */
    protected ReadObjectQuery buildReadObjectQuery(SDKCall call) {
        ReadObjectQuery query = new ReadObjectQuery();
        query.setCall(call);
        return query;
    }

    /**
     * Helper method.
     * Convert the specified array to a vector.
     */
    protected Vector convertToVector(Object[] array) {
        return Helper.vectorFromArray(array);
    }

    /**
     * Aggregates use a dummy table as default.
     */
    protected DatabaseTable extractDefaultTable() {
        if (this.isAggregateDescriptor()) {
            return new DatabaseTable();
        }
        return super.extractDefaultTable();
    }

    /**
     * Convenience method.
     * Build and return a Vector of the ClassDescriptor's custom queries.
     */
    protected Vector getCustomQueries() {
        DescriptorQueryManager dqm = this.getQueryManager();

        Vector result = new Vector();
        result.addElement(dqm.getReadObjectQuery());
        result.addElement(dqm.getReadAllQuery());
        result.addElement(dqm.getInsertQuery());
        result.addElement(dqm.getUpdateQuery());
        result.addElement(dqm.getDeleteQuery());
        result.addElement(dqm.getDoesExistQuery());

        for (Iterator stream = dqm.getQueries().values().iterator(); stream.hasNext();) {
            result.addElement(stream.next());
        }

        return result;
    }

    /**
     * PUBLIC:
     * Return the data type name for the class of objects the descriptor maps.
     */
    public String getDataTypeName() throws DescriptorException {
        return this.getTableName();
    }

    /**
     * Initialize the descriptor.
     */
    protected void initialize() {
        this.initializeQueryManager();
    }

    protected void validateMappingType(DatabaseMapping mapping) {
        //do nothing
    }

    /**
     * INTERNAL:
     * This is needed by regular aggregate descriptors (because they are screwed up);
     * but not by SDK aggregate descriptors.
     */
    public void initializeAggregateInheritancePolicy(AbstractSession session) {
        // do nothing, since the parent descriptor was already modified during pre-initialize
    }

    /**
     * Initialize the descriptor's query manager.
     */
    protected void initializeQueryManager() {
        if (getQueryManager().getReadObjectCall() == null) {
            getQueryManager().setReadObjectCall(InvalidSDKCall.getInstance());
        }
        if (getQueryManager().getReadAllCall() == null) {
            getQueryManager().setReadAllCall(InvalidSDKCall.getInstance());
        }
        if (getQueryManager().getInsertCall() == null) {
            getQueryManager().setInsertCall(InvalidSDKCall.getInstance());
        }
        if (getQueryManager().getDeleteCall() == null) {
            getQueryManager().setDeleteCall(InvalidSDKCall.getInstance());
        }
        if (getQueryManager().getUpdateCall() == null) {
            getQueryManager().setUpdateCall(InvalidSDKCall.getInstance());
        }
        if (getQueryManager().getDoesExistCall() == null) {
            getQueryManager().setDoesExistCall(InvalidSDKCall.getInstance());
        }
    }

    /**
     * INTERNAL:
     * SDK descriptors are initialized normally, since they do
     * not need to be cloned by SDK aggregate mappings.
     */
    public boolean requiresInitialization() {
        return (!isDescriptorForInterface());
    }

    /**
     * PUBLIC:
     * Specify the data type name for the class of objects the descriptor maps.
     */
    public void setDataTypeName(String dataTypeName) throws DescriptorException {
        this.setTableName(dataTypeName);
    }

    /**
     * PUBLIC:
     * Set the call to be used for delete operations.
     */
    public void setDeleteCall(SDKCall call) {
        DeleteObjectQuery query = new DeleteObjectQuery();
        query.setCall(call);
        this.getQueryManager().setDeleteQuery(query);
    }

    /**
     * PUBLIC:
     * Set the call to be used for testing for an object's existence on the data store.
     */
    public void setDoesExistCall(SDKCall call) {
        DoesExistQuery query = new DoesExistQuery();
        query.setCall(call);
        this.getQueryManager().setDoesExistQuery(query);
    }

    /**
     * PUBLIC:
     * Set the call to be used for insert operations.
     */
    public void setInsertCall(SDKCall call) {
        InsertObjectQuery query = new InsertObjectQuery();
        query.setCall(call);
        this.getQueryManager().setInsertQuery(query);
    }

    /**
     * This method should only be called by di when a descriptor
     * has been read from the data store.
     */
    public void setQueryManager(DescriptorQueryManager queryManager) {
        super.setQueryManager(queryManager);
        initializeQueryManager();
    }

    /**
     * PUBLIC:
     * Set the call to be used for standard read-all operations.
     */
    public void setReadAllCall(SDKCall call) {
        ReadAllQuery query = new ReadAllQuery();
        query.setCall(call);
        this.getQueryManager().setReadAllQuery(query);
    }

    /**
     * PUBLIC:
     * Set the call to be used for standard read by primary key operations.
     */
    public void setReadObjectCall(SDKCall call) {
        ReadObjectQuery query = new ReadObjectQuery();
        query.setCall(call);
        this.getQueryManager().setReadObjectQuery(query);
    }

    /**
     * PUBLIC:
     * Set the call to be used for update operations.
     */
    public void setUpdateCall(SDKCall call) {
        UpdateObjectQuery query = new UpdateObjectQuery();
        query.setCall(call);
        this.getQueryManager().setUpdateQuery(query);
    }
}