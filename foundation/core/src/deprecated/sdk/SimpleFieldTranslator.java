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
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.sessions.AbstractRecord;

/**
 * This is a simple implementation of the <code>FieldTranslator</code>
 * interface. It has the ability to do simple field name
 * translations to the database row after it has been returned from a read
 * and before it is used for a write. This implementation will also wrap
 * another <code>FieldTranslator</code>, allowing a stack of translators
 * to translate the database rows, without having to share implementation
 * details.
 *
 * @see AbstractSDKCall
 * @see org.eclipse.persistence.mappings.Association
 *
 * @author Les Davis
 * @since TOPLink/Java 3.0
 * @deprecated since OracleAS TopLink 10<i>g</i> (10.1.3).  This class is replaced by
 *         {@link org.eclipse.persistence.eis}
 */
public class SimpleFieldTranslator implements FieldTranslator {

    /** The translations applied after the database row is read from the data store. */
    private Hashtable readTranslations;

    /** The translations applied before the database row is written to the data store. */
    private Hashtable writeTranslations;

    /** A nested translator. */
    private FieldTranslator wrappedTranslator;

    /**
     * Default constructor.
     */
    public SimpleFieldTranslator() {
        this(new DefaultFieldTranslator());
    }

    /**
     * Constructor for wrapping another field translator.
     */
    public SimpleFieldTranslator(FieldTranslator wrappedTranslator) {
        super();
        this.initialize(wrappedTranslator);
    }

    /**
     * Add a translation. When a database row is read from the data store,
     * any field with the specified data store field name will be translated to the
     * specified mapping field name.
     */
    public void addReadOnlyTranslation(String dataStoreFieldName, String mappingFieldName) {
        this.getReadTranslations().put(dataStoreFieldName, mappingFieldName);
    }

    /**
     * Add translations. When a database row is read from the data store,
     * any field with the specified data store field name will be translated to the
     * specified mapping field name.
     */
    public void addReadOnlyTranslations(String[] dataStoreFieldNames, String[] mappingFieldNames) {
        if (dataStoreFieldNames.length != mappingFieldNames.length) {
            throw SDKDescriptorException.sizeMismatchOfFieldTranslations();
        }
        for (int i = 0; i < dataStoreFieldNames.length; i++) {
            this.addReadOnlyTranslation(dataStoreFieldNames[i], mappingFieldNames[i]);
        }
    }

    /**
     * Add translations. When a database row is read from the data store,
     * any field with the specified data store field name will be translated to the
     * specified mapping field name.
     */
    public void addReadOnlyTranslations(Vector translations) {
        for (Enumeration stream = translations.elements(); stream.hasMoreElements();) {
            Association element = (Association)stream.nextElement();
            this.addReadOnlyTranslation((String)element.getKey(), (String)element.getValue());
        }
    }

    /**
     * Add a translation. When a database row is read from the data store,
     * any field with the specified data store field name will be translated to the
     * specified mapping field name. The corresponding write translation will also be added.
     */
    public void addReadTranslation(String dataStoreFieldName, String mappingFieldName) {
        this.addReadOnlyTranslation(dataStoreFieldName, mappingFieldName);
        this.addWriteOnlyTranslation(mappingFieldName, dataStoreFieldName);
    }

    /**
     * Add translations. When a database row is read from the data store,
     * any field with the specified data store field name will be translated to the
     * specified mapping field name. The corresponding write translations will also be added.
     */
    public void addReadTranslations(String[] dataStoreFieldNames, String[] mappingFieldNames) {
        this.addReadOnlyTranslations(dataStoreFieldNames, mappingFieldNames);
        this.addWriteOnlyTranslations(mappingFieldNames, dataStoreFieldNames);
    }

    /**
     * Add translations. When a database row is read from the data store,
     * any field with the specified data store field name will be translated to the
     * specified mapping field name. The corresponding write translations will also be added.
     */
    public void addReadTranslations(Vector translations) {
        for (Enumeration stream = translations.elements(); stream.hasMoreElements();) {
            Association element = (Association)stream.nextElement();
            this.addReadTranslation((String)element.getKey(), (String)element.getValue());
        }
    }

    /**
     * Add a translation. When a database row is to be written to the data store,
     * any field with the specified mapping field name will be translated to the
     * specified data store field name.
     */
    public void addWriteOnlyTranslation(String mappingFieldName, String dataStoreFieldName) {
        this.getWriteTranslations().put(mappingFieldName, dataStoreFieldName);
    }

    /**
     * Add translations. When a database row is to be written to the data store,
     * any field with the specified mapping field name will be translated to the
     * specified data store field name.
     */
    public void addWriteOnlyTranslations(String[] mappingFieldNames, String[] dataStoreFieldNames) {
        if (mappingFieldNames.length != dataStoreFieldNames.length) {
            throw SDKDescriptorException.sizeMismatchOfFieldTranslations();
        }
        for (int i = 0; i < mappingFieldNames.length; i++) {
            this.addWriteOnlyTranslation(mappingFieldNames[i], dataStoreFieldNames[i]);
        }
    }

    /**
     * Add translations. When a database row is to be written to the data store,
     * any field with the specified mapping field name will be translated to the
     * specified data store field name.
     */
    public void addWriteOnlyTranslations(Vector translations) {
        for (Enumeration stream = translations.elements(); stream.hasMoreElements();) {
            Association element = (Association)stream.nextElement();
            this.addWriteOnlyTranslation((String)element.getKey(), (String)element.getValue());
        }
    }

    /**
     * Add a translation. When a database row is to be written to the data store,
     * any field with the specified mapping field name will be translated to the
     * specified data store field name. The corresponding read translation will also be added.
     */
    public void addWriteTranslation(String mappingFieldName, String dataStoreFieldName) {
        this.addReadTranslation(dataStoreFieldName, mappingFieldName);
    }

    /**
     * Add translations. When a database row is to be written to the data store,
     * any field with the specified mapping field name will be translated to the
     * specified data store field name. The corresponding read translations will also be added.
     */
    public void addWriteTranslations(String[] mappingFieldNames, String[] dataStoreFieldNames) {
        this.addReadTranslations(dataStoreFieldNames, mappingFieldNames);
    }

    /**
     * Add translations. When a database row is to be written to the data store,
     * any field with the specified mapping field name will be translated to the
     * specified data store field name. The corresponding read translations will also be added.
     */
    public void addWriteTranslations(Vector translations) {
        for (Enumeration stream = translations.elements(); stream.hasMoreElements();) {
            Association element = (Association)stream.nextElement();
            this.addWriteTranslation((String)element.getKey(), (String)element.getValue());
        }
    }

    /**
     * Return the read translations.
     * The keys are the data store field names.
     * The values are the field names expected by the TopLink mappings.
     */
    protected Hashtable getReadTranslations() {
        return readTranslations;
    }

    /**
     * Return the wrapped field translator.
     */
    public FieldTranslator getWrappedTranslator() {
        return wrappedTranslator;
    }

    /**
     * Return the write translations.
     * The keys are the field names generated by the TopLink mappings.
     * The values are the data store field names.
     */
    protected Hashtable getWriteTranslations() {
        return writeTranslations;
    }

    /**
     * Initialize the newly-created instance.
     */
    protected void initialize(FieldTranslator wrappedTranslator) {
        this.readTranslations = new Hashtable(10);
        this.writeTranslations = new Hashtable(10);

        this.setWrappedTranslator(wrappedTranslator);
    }

    /**
     * Remove the specified read translation.
     */
    public void removeReadOnlyTranslation(String dataStoreFieldName) {
        this.getReadTranslations().remove(dataStoreFieldName);
    }

    /**
     * Remove the specified read translation.
     * The corresponding write translation will also be removed.
     */
    public void removeReadTranslation(String dataStoreFieldName) {
        this.removeTranslations(dataStoreFieldName, (String)this.getReadTranslations().get(dataStoreFieldName));
    }

    /**
     * Remove the specified translations.
     */
    protected void removeTranslations(String dataStoreFieldName, String mappingFieldName) {
        this.removeReadOnlyTranslation(dataStoreFieldName);
        this.removeWriteOnlyTranslation(mappingFieldName);
    }

    /**
     * Remove the specified write translation.
     */
    public void removeWriteOnlyTranslation(String mappingFieldName) {
        this.getWriteTranslations().remove(mappingFieldName);
    }

    /**
     * Remove the specified write translation.
     * The corresponding read translation will also be removed.
     */
    public void removeWriteTranslation(String mappingFieldName) {
        this.removeTranslations((String)this.getWriteTranslations().get(mappingFieldName), mappingFieldName);
    }

    /**
     * Set the wrapped field translator.
     */
    public void setWrappedTranslator(FieldTranslator wrappedTranslator) {
        this.wrappedTranslator = wrappedTranslator;
    }

    /**
     * Translate the specified database field with the specified translations.
     */
    protected DatabaseField translate(DatabaseField field, Hashtable translations) {
        String newFieldName = (String)translations.get(field.getName());
        if (newFieldName == null) {
            return field;
        } else {
            return new DatabaseField(newFieldName, field.getTable());
        }
    }

    /**
     * Translate the specified database row with the specified translations.
     */
    protected AbstractRecord translate(AbstractRecord row, Hashtable translations) {
        if (translations.isEmpty()) {
            // don't build a new row for no reason, just return the original
            return row;
        }
        DatabaseRecord result = new DatabaseRecord(row.size());

        for (Enumeration stream = row.getFields().elements(); stream.hasMoreElements();) {
            DatabaseField field = (DatabaseField)stream.nextElement();
            Object value = row.get(field);
            if (value instanceof SDKFieldValue) {
                // translate the nested database rows
                value = this.translate((SDKFieldValue)value, translations);
            }
            result.put(this.translate(field, translations), value);
        }

        return result;
    }

    /**
     * Translate the specified SDK field value with the specified translations.
     */
    protected SDKFieldValue translate(SDKFieldValue fieldValue, Hashtable translations) {
        // direct collections do not have any nested rows to translate
        if (fieldValue.isDirectCollection()) {
            return fieldValue;
        }

        Vector newRows = new Vector(fieldValue.getElements().size());
        for (Enumeration stream = fieldValue.getElements().elements(); stream.hasMoreElements();) {
            newRows.addElement(this.translate((AbstractRecord)stream.nextElement(), translations));
        }

        return fieldValue.clone(newRows);
    }

    /**
     * Translate and return the specified database row that was
     * read from the data store.
     * Invoke the wrapped translator before performing the field translations.
     */
    public Record translateForRead(Record row) {
        return this.translate((AbstractRecord)this.getWrappedTranslator().translateForRead(row), this.getReadTranslations());
    }

    /**
     * Translate and return the specified database row that will
     * be written to the data store.
     * Perform the field translations, then invoke the wrapped translator.
     */
    public Record translateForWrite(Record row) {
        return this.getWrappedTranslator().translateForWrite(this.translate((AbstractRecord)row, this.getWriteTranslations()));
    }
}