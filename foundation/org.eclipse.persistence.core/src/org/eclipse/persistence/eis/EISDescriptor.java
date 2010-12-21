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
package org.eclipse.persistence.eis;

import java.util.List;
import java.util.Vector;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorQueryManager;
import org.eclipse.persistence.descriptors.InheritancePolicy;
import org.eclipse.persistence.eis.mappings.EISDirectMapping;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.internal.databaseaccess.DatabaseCall;
import org.eclipse.persistence.internal.expressions.SQLStatement;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.oxm.*;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.oxm.*;
import org.eclipse.persistence.oxm.record.*;
import org.eclipse.persistence.sessions.DatabaseRecord;

/**
 * 
 * <p>An <code>EISDescriptor</code> defines the mapping from a JCA data 
 * structure to a Java object.  There are two types of EIS descriptors:
 * <ul>
 * <li>Root - indicates to the EclipseLink runtime that the EIS descriptor's 
 * reference class is a parent class: no other class will reference it by way of 
 * a composite object mapping or composite collection mapping.  For an EIS root 
 * descriptor, EIS interactions can be defined to invoke methods on an EIS
 * <li>Composite - indicates to the EclipseLink runtime that the EIS descriptor's 
 * reference class may be referenced by a composite object mapping or composite 
 * collection mapping
 * </ul>
 *
 * @see org.eclipse.persistence.eis.interactions.EISInteraction
 * @see org.eclipse.persistence.eis.mappings.EISMapping
 *
 * @author James
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public class EISDescriptor extends ClassDescriptor {

    /** Define the type of data the descriptor maps to. */
    protected String dataFormat;

    /** Define the valid data formats that the descriptor can map to. */
    public static final String MAPPED = "mapped";
    public static final String INDEXED = "indexed";
    public static final String XML = "xml";

    /** Allow namespaces to be specified for XML type descriptors. */
    protected NamespaceResolver namespaceResolver;

    /**
     * Default constructor.
     */
    public EISDescriptor() {
        super();
        this.shouldOrderMappings = false;
        this.dataFormat = XML;
    }

    protected void validateMappingType(DatabaseMapping mapping) {
        if (!(mapping.isEISMapping())) {
            throw DescriptorException.invalidMappingType(mapping);
        }
    }

    /**
     * PUBLIC:
     * Specify the data type name for the class of objects the descriptor maps.
     * This may be the XML schema complex type name, or the JCA record name for the type being mapped.
     */
    public void setDataTypeName(String dataTypeName) throws DescriptorException {
        this.setTableName(dataTypeName);
    }

    /**
     * PUBLIC:
     * Return the XML namespace resolver.
     * XML type EIS descriptor can use a namespace resolver to support XML schema namespaces.
     */
    public NamespaceResolver getNamespaceResolver() {
        return namespaceResolver;
    }

    /**
     * PUBLIC:
     * The inheritance policy is used to define how a descriptor takes part in inheritance.
     * All inheritance properties for both child and parent classes is configured in inheritance policy.
     * Caution must be used in using this method as it lazy initializes an inheritance policy.
     * Calling this on a descriptor that does not use inheritance will cause problems, #hasInheritance() must always first be called.
     */
    public InheritancePolicy getInheritancePolicy() {
        if (inheritancePolicy == null) {
            if(getDataFormat() == EISDescriptor.XML) {
                // Lazy initialize to conserve space in non-inherited classes.
                setInheritancePolicy(new org.eclipse.persistence.internal.oxm.QNameInheritancePolicy(this));
            } else {
                setInheritancePolicy(new InheritancePolicy(this));
            }
        }
        return inheritancePolicy;
    }

    /**
     * PUBLIC:
     * Set the XML namespace resolver.
     * XML type EIS descriptor can use a namespace resolver to support XML schema namespaces.
     */
    public void setNamespaceResolver(NamespaceResolver namespaceResolver) {
        this.namespaceResolver = namespaceResolver;
    }

    /**
     * INTERNAL:
     * Avoid SDK initialization.
     */
    public void setQueryManager(DescriptorQueryManager queryManager) {
        this.queryManager = queryManager;
        if (queryManager != null) {
            queryManager.setDescriptor(this);
        }
    }

    /**
     * INTERNAL:
     * Configure the object builder for the correct dataFormat.
     */
    public void preInitialize(AbstractSession session) {
        // Must not initialize if already done.
        if (isInitialized(PREINITIALIZED)) {
            return;
        }

        if (dataFormat.equals(XML)) {
            setObjectBuilder(new XMLObjectBuilder(this));
            if(this.hasInheritance()) {
                ((QNameInheritancePolicy)getInheritancePolicy()).setNamespaceResolver(this.namespaceResolver);
            }
        }

        //		initializeQueryManager();
        super.preInitialize(session);
    }

    /**
     * PUBLIC:
     * Return the data format that the descriptor maps to.
     */
    public String getDataFormat() {
        return dataFormat;
    }

    /**
     * PUBLIC:
     * Specify the data type name for the class of objects the descriptor maps.
     * This may be the XML schema complex type name, or the JCA record name for the type being mapped.
     */
    public String getDataTypeName() throws DescriptorException {
        return this.getTableName();
    }

    /**
     * PUBLIC:
     * Configure the data format that the descriptor maps to.
     */
    public void setDataFormat(String dataFormat) {
        this.dataFormat = dataFormat;
    }

    /**
     * PUBLIC:
     * Configure the data format to use mapped records.
     */
    public void useMappedRecordFormat() {
        setDataFormat(MAPPED);
    }

    /**
     * PUBLIC:
     * Configure the data format to use indexed records.
     */
    public void useIndexedRecordFormat() {
        setDataFormat(INDEXED);
    }

    /**
     * PUBLIC:
     * Configure the data format to use xml records.
     */
    public void useXMLRecordFormat() {
        setDataFormat(XML);
    }

    /**
     * INTERNAL:
     * Build the nested row.
     */
    public AbstractRecord buildNestedRowFromFieldValue(Object fieldValue) {
        if (!getDataFormat().equals(XML)) {
            if (!(fieldValue instanceof List)) {
                return new DatabaseRecord(1);
            }
            List nestedRows = ((List)fieldValue);
            if (nestedRows.isEmpty()) {
                return new DatabaseRecord(1);
            } else {
                // BUG#2667762 if the tag was empty this could be a string of whitespace.
                if (!(nestedRows.get(0) instanceof AbstractRecord)) {
                    return new DatabaseRecord(1);
                }
                return (AbstractRecord)nestedRows.get(0);
            }
        }

        if (fieldValue instanceof XMLRecord) {
            return (XMLRecord)fieldValue;
        }

        // BUG#2667762 if the tag was empty this could be a string of whitespace.
        if (!(fieldValue instanceof Vector)) {
            return getObjectBuilder().createRecord(null);
        }
        Vector nestedRows = (Vector)fieldValue;
        if (nestedRows.isEmpty()) {
            return getObjectBuilder().createRecord(null);
        } else {
            // BUG#2667762 if the tag was empty this could be a string of whitespace.
            if (!(nestedRows.firstElement() instanceof XMLRecord)) {
                return getObjectBuilder().createRecord(null);
            }
            return (XMLRecord)nestedRows.firstElement();
        }
    }

    /**
     * INTERNAL:
     * Build the nested rows.
     */
    public Vector buildNestedRowsFromFieldValue(Object fieldValue, AbstractSession session) {
        if (!getDataFormat().equals(XML)) {
            if (!(fieldValue instanceof List)) {
                return new Vector();
            }
            return new Vector((List)fieldValue);
        }

        // BUG#2667762 if the tag was empty this could be a string of whitespace.
        if (!(fieldValue instanceof Vector)) {
            return new Vector(0);
        }
        return (Vector)fieldValue;
    }

    /**
     * INTERNAL:
     * Extract the direct values from the specified field value.
     * Return them in a vector.
     * The field value could be a vector or could be a text value if only a single value.
     */
    public Vector buildDirectValuesFromFieldValue(Object fieldValue) {
        if (!getDataFormat().equals(XML)) {
            return super.buildDirectValuesFromFieldValue(fieldValue);
        }

        if (!(fieldValue instanceof Vector)) {
            Vector fieldValues = new Vector(1);
            fieldValues.add(fieldValue);
            return fieldValues;
        }
        return (Vector)fieldValue;
    }

    /**
     * INTERNAL:
     * Build the appropriate field value for the specified
     * set of direct values.
     * The database better be expecting a Vector.
     */
    public Object buildFieldValueFromDirectValues(Vector directValues, String elementDataTypeName, AbstractSession session) {
        if (!getDataFormat().equals(XML)) {
            return super.buildFieldValueFromDirectValues(directValues, elementDataTypeName, session);
        }
        return directValues;
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
     */
    public Object buildFieldValueFromNestedRows(Vector nestedRows, String structureName, AbstractSession session) throws DatabaseException {
        return nestedRows;
    }

    /**
    * INTERNAL:
    * XML type descriptors should use XMLFields.
    */
    public DatabaseField buildField(String fieldName) {
        if (getDataFormat().equals(XML)) {
            XMLField xmlField = new XMLField(fieldName);
            xmlField.setNamespaceResolver(this.getNamespaceResolver());
            xmlField.initialize();
            return xmlField;
        } else {
            return super.buildField(fieldName);
        }
    }

    /**
     * INTERNAL:
     * If the field is an XMLField then set the namespace resolver from the descriptor.
     * This allows the resolver to only be set in the descriptor.
     */
    public DatabaseField buildField(DatabaseField field) {
        if(getDataFormat().equals(XML)) {
            if(!(field instanceof XMLField)) {
                field = new XMLField(field.getName());
            }
            ((XMLField)field).setNamespaceResolver(getNamespaceResolver());
            ((XMLField)field).initialize();
        }
        return super.buildField(field);
    }

    /**
        * PUBLIC:
        * Add a direct mapping to the receiver. The new mapping specifies that
        * an instance variable of the class of objects which the receiver describes maps in
        * the default manner for its type to the indicated database field.
        *
        * @param String instanceVariableName is the name of an instance variable of the
        * class which the receiver describes.
        * @param String fieldName is the name of the xml element or attribute which corresponds
        * with the designated instance variable.
        * @return The newly created DatabaseMapping is returned.
        */
    public DatabaseMapping addDirectMapping(String attributeName, String fieldName) {
        EISDirectMapping mapping = new EISDirectMapping();
        mapping.setAttributeName(attributeName);

        if (getDataFormat() == EISDescriptor.XML) {
            mapping.setXPath(fieldName);
        } else {
            mapping.setFieldName(fieldName);
        }

        return addMapping(mapping);
    }

    /**
    * PUBLIC:
    * Add a direct to node mapping to the receiver. The new mapping specifies that
    * a variable accessed by the get and set methods of the class of objects which
    * the receiver describes maps in  the default manner for its type to the indicated
    * database field.
    */
    public DatabaseMapping addDirectMapping(String attributeName, String getMethodName, String setMethodName, String fieldName) {
        EISDirectMapping mapping = new EISDirectMapping();

        mapping.setAttributeName(attributeName);
        mapping.setSetMethodName(setMethodName);
        mapping.setGetMethodName(getMethodName);
        if (getDataFormat() == EISDescriptor.XML) {
            mapping.setXPath(fieldName);
        } else {
            mapping.setFieldName(fieldName);
        }
        return addMapping(mapping);
    }

    /**
     * PUBLIC:
     * Specify the primary key field.
     * This should be called for each field that make up the primary key.
     * For EIS XML Descriptors use the addPrimaryKeyField(DatabaseField) API
     * and supply an org.eclipse.persistence.oxm.XMLField parameter instead of using this method
     */
    public void addPrimaryKeyFieldName(String fieldName) {
        if (getDataFormat() == EISDescriptor.XML) {
            addPrimaryKeyField(new XMLField(fieldName));
        } else {
            super.addPrimaryKeyFieldName(fieldName);
        }
    }

    /**
     * PUBLIC:
     * Set the sequence number field name.
     * This is the field in the descriptors table that needs its value to be generated.
     * This is normally the primary key field of the descriptor.
     * For EIS XML Descriptors use the setSequenceNumberFieldName(DatabaseField) API
     * and supply an org.eclipse.persistence.oxm.XMLField parameter instead of using this method
     */
    public void setSequenceNumberFieldName(String fieldName) {
        super.setSequenceNumberFieldName(fieldName);
    }

    /**
      *INTERNAL:
      * Override this method to throw an exception. SQL should not be generated for
      * EIS Calls.
      */
    public DatabaseCall buildCallFromStatement(SQLStatement statement, AbstractSession session) {
        throw QueryException.noCallOrInteractionSpecified();
    }

    public void initialize(AbstractSession session) throws DescriptorException {
        if (getDataFormat().equals(XML)) {
            for(int x = 0, primaryKeyFieldsSize = this.primaryKeyFields.size(); x<primaryKeyFieldsSize; x++) {
                XMLField pkField = (XMLField) this.primaryKeyFields.get(x);
                pkField.setNamespaceResolver(this.namespaceResolver);
                pkField.initialize();
            }
        }
        super.initialize(session);
    }

    /**
        * INTERNAL:
        * This is needed by regular aggregate descriptors
        * * but not by EIS aggregate descriptors.
        */
    public void initializeAggregateInheritancePolicy(AbstractSession session) {
        // do nothing, since the parent descriptor was already modified during pre-initialize
    }

    /**
     * INTERNAL:
     * XML descriptors are initialized normally, since they do
     * not need to be cloned by ESI aggregate mappings.
     */
    public boolean requiresInitialization() {
        return (!isDescriptorForInterface());
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
     * INTERNAL:
     * Indicates if a return type is required for the field set on the
     * returning policy.  For EIS descriptors, this should always
     * return false.
     */
    public boolean isReturnTypeRequiredForReturningPolicy() {
        return false;
    }
    
    /**
     * INTERNAL:
     * Return if change sets are required for new objects.
     */
    public boolean shouldUseFullChangeSetsForNewObjects() {
        // This is currently set to allow EIS tests to pass the same a before.
        // TODO: It should be removed, and the test issues fixed (bug was logged).
        return true;
    }
}
