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
 ******************************************************************************/  
package org.eclipse.persistence.eis.interactions;

import java.io.*;
import java.util.*;
import javax.resource.*;
import javax.resource.cci.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.internal.databaseaccess.DatasourceCall;
import org.eclipse.persistence.eis.*;

/**
 * Defines the specification for a call to a JCA interaction.
 * Builds the input and output records from the arguments.
 *
 * @author James
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public abstract class EISInteraction extends DatasourceCall {

    /** Adapter specific interaction spec. */
    protected InteractionSpec interactionSpec;

    /** Name of the function the interaction describes. */
    protected String functionName;

    /** Name to pass to the input record creation. */
    protected String inputRecordName;

    /** Adapter specific properties may be added. */
    protected Map properties;

    /** Holds database row of input values. */
    protected AbstractRecord inputRow;

    /** Defines the arguments to the interaction, these can be the values or argument names/fields the values come from. */
    protected Vector arguments;

    /**
     * Defines the output argument names as defined in the output record for the interaction.
     * This is shared as indexed interaction may still have mapped results.
     */
    protected Vector outputArgumentNames;

    /** Defines the field values the output arguments of the interaction map to.  These are order dependent with the names. */
    protected Vector outputArguments;

    /** Path to the desired output record if nested. */
    protected String outputResultPath;

    /**
     * PUBLIC:
     * Default constructor.
     */
    public EISInteraction() {
        super();
        this.functionName = "";
        this.inputRecordName = "";
        this.outputResultPath = "";
    }

    /**
     * PUBLIC:
     * Define the output argument to the interaction and the field/argument name to be substitute for it.
     * This is only required if an output row is not used.
     * The parameterAndArgumentFieldName is the name of the output record argument expected,
     * and is the field or argument name to be used to be used for it.
     * These names are assumed to be the same, if not this method can be called with two arguments.
     */
    public void addOutputArgument(String parameterAndArgumentFieldName) {
        addOutputArgument(parameterAndArgumentFieldName, parameterAndArgumentFieldName);
    }

    /**
     * PUBLIC:
     * Define the output argument to the interaction and the field/argument name to be substitute for it.
     * This is only required if an output row is not used.
     * The parameterName is the name of the output record argument expected.
     * The argumentFieldName is the field or argument name to be used to be used for it.
     * If these names are the same (as they normally are) this method can be called with a single argument.
     */
    public void addOutputArgument(String parameterName, String argumentFieldName) {
        getOutputArgumentNames().addElement(parameterName);
        getOutputArguments().addElement(new DatabaseField(argumentFieldName));
    }

    /**
     * The argument fields or values to the interaction that map into the input record.
     */
    public Vector getArguments() {
        // This is lazy initialized to conserv space on calls that have no parameters.
        if (arguments == null) {
            arguments = new Vector();
        }
        return arguments;
    }

    /**
     * INTERNAL:
     * The argument fields or values to the interaction that map into the input record.
     */
    public void setArguments(Vector arguments) {
        this.arguments = arguments;
    }

    /**
     * Return if argumented.
     */
    public boolean hasArguments() {
        return (arguments != null) && (!arguments.isEmpty());
    }

    public boolean isEISInteraction() {
        return true;
    }

    /**
     * PUBLIC:
     * The output result path defines the root key for the MappedRecord that
     * the desired interaction result is nested into.
     * This is required for read interactions that need a nested record to build from the mapped object.
     */
    public String getOutputResultPath() {
        return outputResultPath;
    }

    /**
     * PUBLIC:
     * The output result path defines the root key for the MappedRecord that
     * the desired interaction result is nested into.
     * This is required for read interactions that need a nested record to build from the mapped object.
     */
    public void setOutputResultPath(String outputResultPath) {
        this.outputResultPath = outputResultPath;
    }

    /**
     * The argument names for the output record.
     */
    public Vector getOutputArgumentNames() {
        // This is lazy initialized to conserv space on calls that have no parameters.
        if (outputArgumentNames == null) {
            outputArgumentNames = new Vector();
        }
        return outputArgumentNames;
    }

    /**
     * The argument fields to the interaction that map into the output record.
     */
    public Vector getOutputArguments() {
        // This is lazy initialized to conserv space on calls that have no parameters.
        if (outputArguments == null) {
            outputArguments = new Vector();
        }
        return outputArguments;
    }

    /**
     * The output arguments.
     */
    public void setOutputArguments(Vector outputArguments) {
        this.outputArguments = outputArguments;
    }

    /**
     * Set the output argument names.
     */
    public void setOutputArgumentNames(Vector outputArgumentNames) {
        this.outputArgumentNames = outputArgumentNames;
    }

    /**
     * Return if argumented.
     */
    public boolean hasOutputArguments() {
        return (outputArguments != null) && (!outputArguments.isEmpty());
    }

    /**
     * Set the default record name from the descriptor.
     */
    public void prepare(AbstractSession session) {
        if (getInputRecordName().length() == 0) {
            if ((getQuery() != null) && (getQuery().getDescriptor() instanceof EISDescriptor)) {
                EISDescriptor descriptor = (EISDescriptor)getQuery().getDescriptor();
                setInputRecordName(descriptor.getDataTypeName());
            } else {
                setInputRecordName("input");
            }
        }
        super.prepare(session);
    }

    /**
     * Create the appropriate record element for the data value.
     * If the value is a collection, create a collection of elements,
     * if the value is a map, create a nested map,
     * otherwise just return the value (primitive data).
     */
    public Object createRecordElement(String elementName, Object value, EISAccessor accessor) {
        try {
            Object element = value;

            // Handle nested collections.
            if (element instanceof List) {
                // Convert each element in the list.
                List values = (List)element;
                List elements = new Vector(values.size());
                for (int index = 0; index < values.size(); index++) {
                    elements.add(createRecordElement(elementName, values.get(index), accessor));
                }
                element = elements;
                // Handle nested rows.
            } else if (value instanceof AbstractRecord) {
            	AbstractRecord valuesRow = (AbstractRecord)value;

                // The record name for the row must be determined,
                // currently the SDK uses the table name of the row's field for this,
                // ideally this would be a property on the row.
                String recordName = elementName;
                if (valuesRow.size() > 0) {
                    recordName = valuesRow.getFields().get(0).getTableName();
                }
                MappedRecord record = accessor.getRecordFactory().createMappedRecord(recordName);
                for (Iterator keysIterator = valuesRow.getFields().iterator();
                         keysIterator.hasNext();) {
                    DatabaseField field = (DatabaseField)keysIterator.next();
                    Object elementValue = createRecordElement(field.getName(), valuesRow.get(field), accessor);
                    accessor.getEISPlatform().setValueInRecord(field.getName(), elementValue, record, accessor);
                }
                element = record;
            }
            return element;
        } catch (ResourceException exception) {
            throw EISException.resourceException(exception, accessor, null);
        }
    }

    /**
     * PUBLIC:
     * Return the JCA InteractionSpec that defines this EIS interaction.
     * The InteractionSpec is JCA adapter specific and typically
     * defines the function name.
     */
    public InteractionSpec getInteractionSpec() {
        return interactionSpec;
    }

    /**
     * PUBLIC:
     * Set the JCA InteractionSpec that defines this EIS interaction.
     * The InteractionSpec is JCA adapter specific and typically
     * defines the function name.
     */
    public void setInteractionSpec(InteractionSpec interactionSpec) {
        this.interactionSpec = interactionSpec;
    }

    /**
     * Return the string for logging purposes.
     */
    public String getLogString(Accessor accessor) {
        StringWriter writer = new StringWriter();
        writer.write("Executing ");
        writer.write(toString());
        writer.write(Helper.cr());
        writer.write("\tspec => ");
        writer.write(String.valueOf(getInteractionSpec()));
        writer.write(Helper.cr());
        writer.write("\tproperties => ");
        writer.write(String.valueOf(getProperties()));
        writer.write(Helper.cr());
        writer.write("\tinput => [");
        if (!getParameters().isEmpty()) {
            for (Iterator iterator = getParameters().iterator(); iterator.hasNext();) {
                Object parameter = iterator.next();
                writer.write(String.valueOf(parameter));
                if (iterator.hasNext()) {
                    writer.write(", ");
                } else {
                    writer.write("]");
                }
            }
        } else {
            writer.write(String.valueOf(getInputRow()));
            writer.write("]");
        }
        return writer.toString();
    }

    /**
     * Arguments to the interaction can be passed two ways.
     * The entire argument row can be converted to the input record,
     * or the arguments from the row can be translated into the interaction parameters.
     */
    public void translate(AbstractRecord translationRow, AbstractRecord modifyRow, AbstractSession session) {
        if (modifyRow != null) {
            // Avoid clearing a prepared row.
            setInputRow(modifyRow);
        }
        if (hasArguments()) {
            List parametersValues = new ArrayList(getArguments().size());
            for (int index = 0; index < getArguments().size(); index++) {
                Object argument = getArguments().elementAt(index);

                // The argument is either a value or a databasefield that needs to be translated.
                if ((argument != null) && (argument instanceof DatabaseField)) {
                    DatabaseField field = (DatabaseField)argument;
                    Object value = translationRow.get(field);

                    //BUG#12345:modifyRow is null for reads
                    if ((value == null) && (modifyRow != null)) {
                        value = modifyRow.get(field);
                    }
                    parametersValues.add(value);
                } else {
                    parametersValues.add(argument);
                }
            }
            setParameters(parametersValues);
        }
    }

    /**
     * Create the appropriate input record for this interaction.
     * Populate the data into the record from this interaction's arguments.
     */
    public abstract Record createInputRecord(EISAccessor accessor);

    /**
     * INTERNAL:
     * Build a database row from the record returned from the interaction.
     */
    public abstract AbstractRecord buildRow(Record record, EISAccessor accessor);

    /**
     * Build a collection of database rows from the Record returned from the interaction.
     * This handles IndexedRecords used as sets of result records,
     * and a single MappedRecord with a list of result records.
     */
    public Vector buildRows(Record record, EISAccessor accessor) {
        Vector rows = null;
        if (record instanceof IndexedRecord) {
            IndexedRecord indexedRecord = (IndexedRecord)record;
            rows = new Vector(indexedRecord.size());
            for (int index = 0; index < indexedRecord.size(); index++) {
                Object element = indexedRecord.get(index);
                if (element instanceof Record) {
                    rows.addElement(buildRow((Record)element, accessor));
                } else {
                    // It is a single row record.
                    rows.add(buildRow(record, accessor));
                    return rows;
                }
            }
        } else if (record instanceof MappedRecord) {
            MappedRecord mappedRecord = (MappedRecord)record;

            // Handle the case of a single output argument of the entire row contained within the return record.
            if (getOutputResultPath().length() > 0) {
                Object element = mappedRecord.get(getOutputResultPath());

                // Handles nested rows inside an IndexedRecord, MappedRecord or List.
                if (element instanceof IndexedRecord) {
                    return buildRows((IndexedRecord)element, accessor);
                } else if (element instanceof MappedRecord) {
                    mappedRecord = (MappedRecord)element;
                } else if (element instanceof List) {
                    List elements = (List)element;
                    rows = new Vector(elements.size());
                    for (int index = 0; index < elements.size(); index++) {
                        Object elementValue = elements.get(index);
                        if (elementValue instanceof Record) {
                            rows.addElement(buildRow((Record)elementValue, accessor));
                        } else {
                            rows.add(elementValue);
                        }
                    }
                    return rows;
                }
            }
            rows = new Vector(1);
            AbstractRecord row = new EISMappedRecord(mappedRecord, accessor);
            rows.add(row);
        } else {
            rows = new Vector(1);
        }
        return rows;
    }

    /**
     * PUBLIC:
     * Return the function name of the interaction.
     * A function name is a common properties for a CCI InteractionSpec.
     * This cannot be generically set on the spec as it is not in the interface
     * however the EIS platform may make use of this in build the interaction spec.
     */
    public String getFunctionName() {
        return functionName;
    }

    /**
     * PUBLIC:
     * Set the function name of the interaction.
     * A function name is a common properties for a CCI InteractionSpec.
     * This cannot be generically set on the spec as it is not in the interface
     * however the EIS platform may make use of this in build the interaction spec.
     */
    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    /**
     * PUBLIC:
     * Return the input record name.
     * This is used as the name passed to the adapter when creating the input record.
     */
    public String getInputRecordName() {
        return inputRecordName;
    }

    /**
     * PUBLIC:
     * Set the input record name.
     * This is used as the name passed to the adapter when creating the input record.
     */
    public void setInputRecordName(String recordName) {
        this.inputRecordName = recordName;
    }

    /**
     * Returns the adapter specific properties.
     */
    public Map getProperties() {
        if (properties == null) {
            properties = new HashMap(5);
        }
        return properties;
    }

    /**
     * PUBLIC:
     * Returns the adapter property associated the given name.
     * If the interaction is associated with a query that has a descriptor,
     * The descriptor's properties are automatically inherited.
     */
    public Object getProperty(String name) {
        Object value = getProperties().get(name);

        // Check descriptor property if available.
        if ((value == null) && (getQuery() != null) && (getQuery().getDescriptor() != null)) {
            value = getQuery().getDescriptor().getProperty(name);
        }
        return value;
    }

    /**
     * Set the adapter specific properties.
     */
    public void setProperties(Map properties) {
        this.properties = properties;
    }

    /**
     * PUBLIC:
     * Set the adapter specific property.
     */
    public void setProperty(String name, Object value) {
        getProperties().put(name, value);
    }

    /**
     * INTERNAL:
     * Return the input database row.
     */
    public AbstractRecord getInputRow() {
        return inputRow;
    }

    /**
     * PUBLIC:
     * Set the input database row.
     */
    public void setInputRow(AbstractRecord inputRow) {
        this.inputRow = inputRow;
    }

    public String toString() {
        return Helper.getShortClassName(getClass()) + "(" + getFunctionName() + ")";
    }
}
