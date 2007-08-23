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
import java.io.*;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.internal.databaseaccess.*;
import org.eclipse.persistence.internal.queries.*;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.helper.*;

/**
 * <code>AbstractSDKCall</code> provides a base implementation of
 * <code>SDKCall</code>. It
 * associates the call with its executing query. It also associates
 * the call with the appropriate SDK query mechanism.
 * It also provides for a call-specific field translator that allows
 * the database row to be translated on its way to and from the
 * data store.
 * Subclasses are still required to implement
 * <code>#execute(Record, Accessor)</code>.
 *
 * @see SDKQueryMechanism
 * @see org.eclipse.persistence.queries.DatabaseQuery
 * @see FieldTranslator
 * @see DefaultFieldTranslator
 *
 * @author Big Country
 * @since TOPLink/Java 3.0
 * @deprecated since OracleAS TopLink 10<i>g</i> (10.1.3).  This class is replaced by
 *         {@link org.eclipse.persistence.eis}
 */
public abstract class AbstractSDKCall extends DatasourceCall implements SDKCall {

    /** The field translator will munge a database row after it was read and before it is written. */
    private FieldTranslator fieldTranslator;

    /**
     * Default constructor.
     * Initialize the new instance.
     */
    protected AbstractSDKCall() {
        super();
        this.initialize();
    }

    /**
     * Add a translation. When a database row is read from the data store,
     * any field with the specified data store field name will be translated to the
     * specified mapping field name. The corresponding write translation will also be added.
     */
    public void addReadTranslation(String dataStoreFieldName, String mappingFieldName) {
        SimpleFieldTranslator newTranslator = this.buildSimpleFieldTranslator();
        newTranslator.addReadTranslation(dataStoreFieldName, mappingFieldName);
        this.setFieldTranslator(newTranslator);
    }

    /**
     * Add translations. When a database row is read from the data store,
     * any field with the specified data store field name will be translated to the
     * specified mapping field name. The corresponding write translations will also be added.
     */
    public void addReadTranslations(String[] dataStoreFieldNames, String[] mappingFieldNames) {
        SimpleFieldTranslator newTranslator = this.buildSimpleFieldTranslator();
        newTranslator.addReadTranslations(dataStoreFieldNames, mappingFieldNames);
        this.setFieldTranslator(newTranslator);
    }

    /**
     * Add translations. When a database row is read from the data store,
     * any field with the specified data store field name will be translated to the
     * specified mapping field name. The corresponding write translations will also be added.
     */
    public void addReadTranslations(Vector translations) {
        SimpleFieldTranslator newTranslator = this.buildSimpleFieldTranslator();
        newTranslator.addReadTranslations(translations);
        this.setFieldTranslator(newTranslator);
    }

    /**
     * Add a translation. When a database row is to be written to the data store,
     * any field with the specified mapping field name will be translated to the
     * specified data store field name. The corresponding read translation will also be added.
     */
    public void addWriteTranslation(String mappingFieldName, String dataStoreFieldName) {
        SimpleFieldTranslator newTranslator = this.buildSimpleFieldTranslator();
        newTranslator.addWriteTranslation(mappingFieldName, dataStoreFieldName);
        this.setFieldTranslator(newTranslator);
    }

    /**
     * Add translations. When a database row is to be written to the data store,
     * any field with the specified mapping field name will be translated to the
     * specified data store field name. The corresponding read translations will also be added.
     */
    public void addWriteTranslations(String[] mappingFieldNames, String[] dataStoreFieldNames) {
        SimpleFieldTranslator newTranslator = this.buildSimpleFieldTranslator();
        newTranslator.addWriteTranslations(mappingFieldNames, dataStoreFieldNames);
        this.setFieldTranslator(newTranslator);
    }

    /**
     * Add translations. When a database row is to be written to the data store,
     * any field with the specified mapping field name will be translated to the
     * specified data store field name. The corresponding read translations will also be added.
     */
    public void addWriteTranslations(Vector translations) {
        SimpleFieldTranslator newTranslator = this.buildSimpleFieldTranslator();
        newTranslator.addWriteTranslations(translations);
        this.setFieldTranslator(newTranslator);
    }

    /**
     * Build and return the default field translator that
     * converts database row field names.
     */
    public FieldTranslator buildDefaultTranslator() {
        return new DefaultFieldTranslator();
    }

    /**
     * Return the appropriate mechanism,
     * with the call added as necessary.
     */
    public DatabaseQueryMechanism buildNewQueryMechanism(DatabaseQuery query) {
        return new SDKQueryMechanism(query, this);
    }

    /**
     * Return the appropriate mechanism,
     * with the call added as necessary.
     */
    public DatabaseQueryMechanism buildQueryMechanism(DatabaseQuery query, DatabaseQueryMechanism mechanism) {
        if (mechanism instanceof SDKQueryMechanism) {
            ((SDKQueryMechanism)mechanism).addCall(this);
            return mechanism;
        } else {
            return this.buildNewQueryMechanism(query);
        }
    }

    /**
     * Build and return a simple field translator.
     */
    protected SimpleFieldTranslator buildSimpleFieldTranslator() {
        FieldTranslator translator = this.getFieldTranslator();
        if (translator instanceof SimpleFieldTranslator) {
            return (SimpleFieldTranslator)translator;
        } else {
            return new SimpleFieldTranslator(translator);
        }
    }

    /**
     * Return a database row with only the primary key fields populated.
     */
    protected AbstractRecord extractPrimaryKeyFrom(AbstractRecord row) {
        Vector keyFields = this.getOrderedPrimaryKeyFields();
        DatabaseRecord result = new DatabaseRecord(keyFields.size());
        for (Enumeration stream = keyFields.elements(); stream.hasMoreElements();) {
            Object keyField = stream.nextElement();
            result.put(keyField, row.get(keyField));
        }
        return result;
    }

    /**
     * Return the translator that converts database row field names.
     */
    public FieldTranslator getFieldTranslator() {
        return fieldTranslator;
    }

    /**
     * Return a string appropriate for the session log.
     */
    public String getLogString(Accessor accessor) {
        StringWriter sw = new StringWriter(1000);
        PrintWriter writer = new PrintWriter(sw);
        this.writeLogDescription(writer);
        writer.write(": ");
        writer.write(this.getLogTableName());
        this.writeLogData(writer);
        return sw.toString();
    }

    /**
     * Append a string describing the call to the specified writer.
     */
    protected void writeLogDescription(PrintWriter writer) {
        writer.write("SDK Call");
    }

    /**
     * Return the name of the table to be logged.
     */
    protected String getLogTableName() {
        return this.getTableName();
    }

    /**
     * Append call-specific data to the specified writer.
     */
    protected void writeLogData(PrintWriter writer) {
        writer.write(Helper.cr());
        writer.write("\t");
        this.write(this.getLogRow(), writer, 0);
    }

    /**
     * Return the row appropriate for logging.
     */
    protected AbstractRecord getLogRow() {
        return this.getTranslationRow();
    }

    /**
     * Append a pithy rendition of the specified row onto the specified writer.
     */
    protected void write(AbstractRecord row, PrintWriter writer, int indent) {
        boolean first = true;

        // First write all the simple values, then tab the complex ones.
        for (Enumeration stream = row.keys(); stream.hasMoreElements();) {
            DatabaseField key = (DatabaseField)stream.nextElement();
            Object value = row.get(key);
            if (!(value instanceof SDKFieldValue)) {
                if (!first) {
                    first = false;
                    writer.write("; ");
                } else {
                    first = false;
                }
                writer.write(key.getName());
                writer.write("=>");
                writer.print(value);
            }
        }

        for (Enumeration stream = row.keys(); stream.hasMoreElements();) {
            DatabaseField key = (DatabaseField)stream.nextElement();
            Object value = row.get(key);
            if (value instanceof SDKFieldValue) {
                if (!first) {
                    writer.write("; ");
                } else {
                    first = false;
                }
                writer.write(Helper.cr());
                for (int count = 0; count <= indent; count++) {
                    writer.write("\t");
                }
                writer.write(key.getName());
                writer.write("=>");
                writer.write("[");
                this.writeSDKFieldValue((SDKFieldValue)value, writer, indent);
                writer.write("]");
            }
        }
    }

    /**
     * Loop through the elements in the SDKFieldValue,
     * appending them to the writer.
     */
    protected void writeSDKFieldValue(SDKFieldValue fieldValue, PrintWriter writer, int indent) {
        int nestedIndent = indent;
        if (fieldValue.getElements().size() > 1) {
            nestedIndent++;
        }
        for (Enumeration stream = fieldValue.getElements().elements(); stream.hasMoreElements();) {
            if (fieldValue.isDirectCollection()) {
                writer.print(stream.nextElement());
                if (stream.hasMoreElements()) {
                    writer.write(", ");
                }
            } else {
                writer.write("(");
                if (fieldValue.getElements().size() > 1) {
                    writer.write(Helper.cr());
                    for (int count = 0; count <= nestedIndent; count++) {
                        writer.write("\t");
                    }
                }
                this.write((AbstractRecord)stream.nextElement(), writer, nestedIndent + 1);
                writer.write(")");
                if (stream.hasMoreElements()) {
                    writer.write(" ");
                }
            }
        }
    }

    /**
     * Convenience method.
     * Return the current accessor (from the query).
     */
    protected Accessor getAccessor() {
        return this.getQuery().getAccessor();
    }

    /**
     * Return the primary key fields.
     * Depending on the <code>Accessor</code>, the
     * order of these fields may be significant.
     * The default is to return the fields in the same order
     * as they are stored in the descriptor.
     */
    protected Vector getOrderedPrimaryKeyFields() {
        return new Vector(getQuery().getDescriptor().getPrimaryKeyFields());
    }

    /**
     * Convenience method.
     * Return the current session (from the query).
     */
    protected AbstractSession getSession() {
        return this.getQuery().getSession();
    }

    /**
     * Convenience method.
     * Return the name of the default table for the call.
     */
    protected String getTableName() {
        return this.getQuery().getDescriptor().getTableName();
    }

    /**
     * Convenience method.
     * Return the translation row from the query.
     */
    protected AbstractRecord getTranslationRow() {
        return this.getQuery().getTranslationRow();
    }

    /**
     * Initialize the newly-created instance.
     */
    protected void initialize() {
        this.fieldTranslator = this.buildDefaultTranslator();
    }

    /**
     * Return whether the call is finished returning
     * all of its results (e.g. a call that returns a cursor
     * that will continue to access the data store after
     * the initial invocation will answer false).
     * The default is true.
     */
    public boolean isFinished() {
        return true;
    }

    /**
     * Set the translator that converts database row field names.
     */
    public void setFieldTranslator(FieldTranslator fieldTranslator) {
        this.fieldTranslator = fieldTranslator;
    }

    /**
     * Call <code>#toString(PrintWriter)</code>, to allow subclasses to
     * insert additional information.
     */
    public String toString() {
        StringWriter sw = new StringWriter();
        PrintWriter writer = new PrintWriter(sw);
        writer.write(Helper.getShortClassName(this));
        writer.write("(");
        this.toString(writer);
        writer.write(")");
        return sw.toString();
    }

    /**
     * Append more information to the writer.
     */
    protected void toString(PrintWriter writer) {
        writer.print(this.getQuery());
    }
}