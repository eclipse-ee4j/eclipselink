/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.expressions;

import java.util.*;
import java.io.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.descriptors.ClassDescriptor;

/**
 * Used for parameterized expressions, such as expression defined in mapping queries.
 */
public class ParameterExpression extends BaseExpression {

    /** The parameter field or name. */
    protected DatabaseField field;

    /** The opposite side of the relation, this is used for conversion of the parameter using the others mapping. */
    protected Expression localBase;

    /** The infered type of the parameter.
     * Please note that the type might not be always initialized to correct value.
     * It might be null if not initialized correctly.
     */
    Object type;

    public ParameterExpression() {
        super();
    }
    
    public ParameterExpression(String fieldName) {
        this(new DatabaseField(fieldName));
    }

    public ParameterExpression(DatabaseField field) {
        super();
        this.field = field;
    }

    // For bug 3107049 ParameterExpression will now be built with a
    // default localBase, same as with ConstantExpression.
    public ParameterExpression(String fieldName, Expression localbaseExpression, Object type) {
        this(new DatabaseField(fieldName), localbaseExpression);
        this.type = type;
    }

    public ParameterExpression(DatabaseField field, Expression localbaseExpression) {
        super();
        this.field = field;
        localBase = localbaseExpression;
    }
    
    /**
     * INTERNAL:
     * Return if the expression is equal to the other.
     * This is used to allow dynamic expression's SQL to be cached.
     */
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!super.equals(object)) {
            return false;
        }
        ParameterExpression expression = (ParameterExpression) object;
        return ((getField() == expression.getField()) || ((getField() != null) && getField().equals(expression.getField())));
    }
        
    /**
     * INTERNAL:
     * Compute a consistent hash-code for the expression.
     * This is used to allow dynamic expression's SQL to be cached.
     */
    public int computeHashCode() {
        int hashCode = super.computeHashCode();
        if (getField() != null) {
            hashCode = hashCode + getField().hashCode();
        }
        return hashCode;
    }

    /**
     * Return description.
     * Used for toString.
     */
    public String basicDescription() {
        return String.valueOf(getField());
    }

    /**
     * INTERNAL:
     * Used for debug printing.
     */
    public String descriptionOfNodeType() {
        return "Parameter";
    }

    /**
     * This allows for nesting of parametrized expression.
     * This is used for parameterizing object comparisons.
     */
    public Expression get(String attributeOrQueryKey) {
        ParameterExpression expression = new ParameterExpression(attributeOrQueryKey);
        expression.setBaseExpression(this);

        return expression;
    }

    /**
     * Return the expression builder which is the ultimate base of this expression, or
     * null if there isn't one (shouldn't happen if we start from a root)
     */
    public ExpressionBuilder getBuilder() {
        if (localBase == null) {
            //Bug#5097278 Need to return the builder from the base expression if nested.
            if (getBaseExpression() != null) {
                return ((ParameterExpression)getBaseExpression()).getBuilder();
            } else {
                return null;
            }
        }
        return localBase.getBuilder();
    }

    public DatabaseField getField() {
        return field;
    }

    /**
     * This allows for nesting of parametrized expression.
     * This is used for parameterizing object comparisons.
     */
    public Expression getField(DatabaseField field) {
        ParameterExpression expression = new ParameterExpression(field);
        expression.setBaseExpression(this);

        return expression;
    }

    /**
     * The opposite side of the relation, this is used for conversion of the parameter using the others mapping.
     */
    public Expression getLocalBase() {
        return localBase;
    }

    /**
     * The infered type of this parameter.
     * Please note that the type might not be always initialized to correct value.
     * It might be null if not initialized correctly
     */
    public Object getType() { return type; }

    /**
     * Extract the value from the row.
     * This may require recusion if it is a nested parameter.
     */
    public Object getValue(AbstractRecord translationRow, AbstractSession session) {
        if (getField() == null) {
            return null;
        }

        Object value = null;

        // Check for nested parameters.
        if (getBaseExpression() != null) {
            value = ((ParameterExpression)getBaseExpression()).getValue(translationRow, session);
            if (value == null) {
                return null;
            }

            ClassDescriptor descriptor = session.getDescriptor(value);
            //Bug4924639  Aggregate descriptors have to be acquired from their mapping as they are cloned and initialized by each mapping
            if (descriptor.isAggregateDescriptor() && ((ParameterExpression)getBaseExpression()).getLocalBase().isObjectExpression()) {
                descriptor = ((ObjectExpression)((ParameterExpression)getBaseExpression()).getLocalBase()).getDescriptor();
            }
            if (descriptor != null) {
                // For bug 2990493 must unwrap for EJBQL "Select Person(p) where p = ?1"
                //if we had to unwrap it make sure we replace the argument with this value
                //incase it is needed again, say in conforming.
                //bug 3750793
                value = descriptor.getObjectBuilder().unwrapObject(value, session);
                translationRow.put(((ParameterExpression)getBaseExpression()).getField(), value);

                // The local parameter is either a field or attribute of the object.
                DatabaseMapping mapping = descriptor.getObjectBuilder().getMappingForField(getField());
                if (mapping != null) {
                    value = mapping.valueFromObject(value, getField(), session);
                } else {
                    mapping = descriptor.getObjectBuilder().getMappingForAttributeName(getField().getName());
                    if (mapping != null) {
                        value = mapping.getRealAttributeValueFromObject(value, session);
                    } else {
                        DatabaseField queryKeyField = descriptor.getObjectBuilder().getFieldForQueryKeyName(getField().getName());
                        if (queryKeyField != null) {
                            mapping = descriptor.getObjectBuilder().getMappingForField(getField());
                            if (mapping != null) {
                                value = mapping.valueFromObject(value, getField(), session);
                            }
                        }
                    }
                }
            }
        } else {
            // Check for null translation row.
            if (translationRow == null) {
                value = AbstractRecord.noEntry;
            } else {
                value = translationRow.getIndicatingNoEntry(getField());
            }
            // Throw an exception if the field is not mapped.
            if (value == AbstractRecord.noEntry) {
                throw QueryException.parameterNameMismatch(getField().getName());
            }
        }

        // Convert the value to the correct type, i.e. object type mappings.
        if (getLocalBase() != null) {
            value = getLocalBase().getFieldValue(value, session);
        }

        return value;
    }

    public boolean isParameterExpression() {
        return true;
    }

    /**
     * INTERNAL:
     */
    public boolean isValueExpression() {
        return true;
    }

    /**
     * INTERNAL:
     * Used for cloning.
     */
    protected void postCopyIn(Dictionary alreadyDone) {
        super.postCopyIn(alreadyDone);
        if (getLocalBase() != null) {
            setLocalBase(getLocalBase().copiedVersionFrom(alreadyDone));
        }
    }

    /**
     * INTERNAL:
     * Print SQL onto the stream, using the ExpressionPrinter for context
     */
    public void printSQL(ExpressionSQLPrinter printer) {
        if (printer.shouldPrintParameterValues()) {
            Object value = getValue(printer.getTranslationRow(), printer.getSession());
            if (value instanceof Collection) {
                printer.printValuelist((Collection)value);
            }else{
                if(getField() == null) {
                    printer.printPrimitive(value);
                } else {
                    printer.printParameter(this);
                }
            }
        } else {
            if (getField() != null) {
                printer.printParameter(this);
            }
        }
    }

    /**
     * INTERNAL:
     * Print java for project class generation
     */
    public void printJava(ExpressionJavaPrinter printer) {
        ((DataExpression)getLocalBase()).getBaseExpression().printJava(printer);
        printer.printString(".getParameter(\"" + getField().getQualifiedName() + "\")");        
    }

    /**
     * INTERNAL:
     * This expression is built on a different base than the one we want. Rebuild it and
     * return the root of the new tree
     */
    public Expression rebuildOn(Expression newBase) {
        ParameterExpression result = (ParameterExpression)clone();
        result.setLocalBase(localBase.rebuildOn(newBase));
        return result;
    }

    /**
     * The opposite side of the relation, this is used for conversion of the parameter using the others mapping.
     */
    public void setLocalBase(Expression localBase) {
        this.localBase = localBase;
    }

    /**
     * INTERNAL:
     * Rebuild against the base, with the values of parameters supplied by the context
     * expression. This is used for transforming a standalone expression (e.g. the join criteria of a mapping)
     * into part of some larger expression. You normally would not call this directly, instead calling twist,
     * (see the comment there for more details).
     */
    public Expression twistedForBaseAndContext(Expression newBase, Expression context) {
        return context.getField(getField());
    }

    /**
     * INTERNAL:
     * Return the value for in memory comparison.
     * This is only valid for valueable expressions.
     */
    public Object valueFromObject(Object object, AbstractSession session, AbstractRecord translationRow, int valueHolderPolicy, boolean isObjectUnregistered) {
        // Run ourselves through the translation row to find the desired value
        if (getField() != null) {
            return getValue(translationRow, session);
        }

        throw QueryException.cannotConformExpression();
    }

    /**
     * INTERNAL:
     * Used to print a debug form of the expression tree.
     */
    public void writeDescriptionOn(BufferedWriter writer) throws IOException {
        writer.write(basicDescription());
    }

    /**
     * INTERNAL:
     * Append the parameter into the printer.
     * "Normal" ReadQuery never has ParameterExpression in it's select clause hence for a "normal" ReadQuery this method is never called.
     * The reason this method was added is that UpdateAllQuery (in case temporary storage is required)
     * creates a "helper" ReportQuery with ReportItem corresponding to each update expression - and update expression
     * may be a ParameterExpression. The call created by "helper" ReportQuery is never executed - 
     * it's used during construction of insert call into temporary storage.
     */
    public void writeFields(ExpressionSQLPrinter printer, Vector newFields, SQLSelectStatement statement) {
        //print ", " before each selected field except the first one
        if (printer.isFirstElementPrinted()) {
            printer.printString(", ");
        } else {
            printer.setIsFirstElementPrinted(true);
        }

        // This field is a parameter value, so any name can be used.
        newFields.addElement(new DatabaseField("*"));
        printSQL(printer);
    }

    /**
     * Print the base for debuggin purposes.
     */
    public void writeSubexpressionsTo(BufferedWriter writer, int indent) throws IOException {
        if (getBaseExpression() != null) {
            getBaseExpression().toString(writer, indent);
        }
    }
}
