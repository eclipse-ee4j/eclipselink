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
package org.eclipse.persistence.queries;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedMethodInvoker;
import org.eclipse.persistence.exceptions.*;

/**
* <p><b>Purpose</b>:
* This policy defines the configuration options for a Query By Example query.
*
* <p><b>Description</b>:
* A Query By Example query is an <code>ObjectLevelReadQuery</code> where the
* selection criteria is built from an example domain object passed in via <code>setExampleObject</code>.
* <p>
* If no policy is specified the selection criteria is built from the example
* object in the following way:
* <ul>
* <li>Attributes of the example object set to <code>null</code> are ignored.
*
* <li>Attributes set to the default value for their primitive type (such as
* <code>0</code> for <code>int</code>) are ignored.
*
* <li>Unmapped attributes are ignored.
*
* <li>A domain object is returned by the query only if its values for all the
* included attributes equal those set in the example object.
* </ul><p>
* A policy can be set on the query to:
* <ul>
* <li>Always consider an attribute even if set to <code>null</code>
* or the default value for its type.  See {@link #alwaysIncludeAttribute alwaysIncludeAttribute}.
*
* <li>Ignore attributes set to some other special value.  See
* {@link #excludeValue(Object) excludeValue}.
*
* <li>Match a <code>null</code> attribute on the example object with domain objects that have
* either <code>null</code> for that attribute also, or have set a meaningful (<code>notNull</code>) value
* for that attribute.  See {@link #setShouldUseEqualityForNulls}.
*
* <li>Use specialized operations when comparing attributes set in the example object
* with those set in the domain objects.  Matching attributes can be those with
* values greater than, less than, like, or not equal to that set in the example
* object.  See {@link #addSpecialOperation}.
* </ul>
* <p>
* Note: When setting an attribute on the example object which is itself a java
* object with an ObjectReferenceMapping, the mapped components of that
* attribute will be considered, not the entire object.  There is no limit to
* how many mapped objects can be nested inside the example object.
* <p>
* Note: <code>setExampleObject</code> is different from <code>setSelectionObject</code> in
* <code>ReadObjectQuery</code> which reads a single object by first extracting
* the primary key from the selection object.
* <p>
* <b>Restrictions</b>:
* <ul>
* <li>Only attributes whose mappings are DirectToField or ObjectReference
* (OneToOne) are considered in a Query By Example.
* </ul>
* <p>
* <b>Example</b>:
* <PRE><BLOCKQUOTE>
* // This example uses like for Strings and the salary must be greater
* // than zero.
* ReadAllQuery query = new ReadAllQuery();
* Employee employee = new Employee();
* employee.setFirstName("B%");
* employee.setLastName("S%");
* employee.setSalary(0);
* query.setExampleObject(employee);
* QueryByExamplePolicy policy = new QueryByExamplePolicy();
* policy.addSpecialOperation(String.class, "like");
* policy.addSpecialOperation(Integer.class, "greaterThan");
* policy.alwaysIncludeAttribute(Employee.class, "salary");
* query.setQueryByExamplePolicy(policy);
* Vector results = (Vector) session.executeQuery(query);
* </PRE></BLOCKQUOTE>
* @see ObjectLevelReadQuery#setExampleObject
* @see ObjectLevelReadQuery#setQueryByExamplePolicy
*
* @since TOPLink/Java 3.0
*/
public class QueryByExamplePolicy implements java.io.Serializable {
    //CR3400 Make Serializable
    public Map valuesToExclude = new HashMap();
    public Map attributesToAlwaysInclude = new HashMap();
    public Map specialOperations = new HashMap();
    public boolean shouldUseEqualityForNulls;

    /**
     * PUBLIC:
     * Constructs a default policy equal to that used when no policy is specified.
     * <p>
     * Sets the default values to be excluded,
     * (that includes 0, false, empty String, etc).<p>
     * By default if an attribute is <code>null</code>, and yet has to be included at all times, equality (<code>isNull</code>)
     * is used for the comparison.  This is used for searching for an object with a <code>null</code> in a certain field.
     * @see #excludeDefaultPrimitiveValues
     * @see #setShouldUseEqualityForNulls setShouldUseEqualityForNulls(true)
     */
    public QueryByExamplePolicy() {
        this.valuesToExclude = new HashMap(10);
        this.attributesToAlwaysInclude = new HashMap(5);
        this.specialOperations = new HashMap(5);
        this.shouldUseEqualityForNulls = true;

        this.excludeDefaultPrimitiveValues();
    }

    /**
     * PUBLIC:
     * Allows operations other than <code>Expression.equal</code> to be used
     * for comparisons.
     * <p>
     * For example if an attribute of type <code>int</code> is
     * set to <code>x</code> in the example object, normally the query will be on all objects
     * whose attributes are also equal to <code>x</code>.  The query could however be all
     * objects whose attributes are not <code>x</code>, greater than <code>x</code>, or even less than or
     * equal to <code>x</code>.
     * <p>
     * Any comparison operation in {@link org.eclipse.persistence.expressions.Expression Expression} which takes the example attribute as a parameter
     * can be used.  A list of supported operations is provided below.
     * <p>
     * Note: A special operation can not be used for attributes set to <code>null</code>.  The only
     * options are {@link org.eclipse.persistence.expressions.Expression#isNull(Object) isNull} (default) and
     * {@link org.eclipse.persistence.expressions.Expression#notNull(Object) notNull}.  See
     * {@link #setShouldUseEqualityForNulls}.
     * @param attributeValueClass Attribute values of which type, for instance
     * <code>Integer</code>, to apply to.  Note for <code>int</code> attributes the
     * class is <code>Integer.class</code> not <code>int.class</code>.  This is not
     * the <code>Class</code> of the example object the attribute is an instance variable of.
     * @param operation Name of method in <code>Expression</code> used
     * @see org.eclipse.persistence.expressions.Expression#equal equal (default)
     * @see org.eclipse.persistence.expressions.Expression#notEqual notEqual
     * @see org.eclipse.persistence.expressions.Expression#equalsIgnoreCase equalsIgnoreCase
     * @see org.eclipse.persistence.expressions.Expression#lessThan lessThan
     * @see org.eclipse.persistence.expressions.Expression#lessThanEqual lessThanEqual
     * @see org.eclipse.persistence.expressions.Expression#greaterThan greaterThan
     * @see org.eclipse.persistence.expressions.Expression#greaterThanEqual greaterThanEqual
     * @see org.eclipse.persistence.expressions.Expression#like like
     * @see org.eclipse.persistence.expressions.Expression#likeIgnoreCase likeIgnoreCase
     * @see org.eclipse.persistence.expressions.Expression#containsAllKeyWords containsAllKeyWords
     * @see org.eclipse.persistence.expressions.Expression#containsAnyKeyWords containsAnyKeyWords
     * @see org.eclipse.persistence.expressions.Expression#containsSubstring(java.lang.String) containsSubstring
     * @see org.eclipse.persistence.expressions.Expression#containsSubstringIgnoringCase(java.lang.String) containsSubstringIgnoringCase
     */
    public void addSpecialOperation(Class attributeValueClass, String operation) {
        this.getSpecialOperations().put(attributeValueClass, operation);
    }

    /**
     * PUBLIC:
     * Always considers the value for a particular attribute as meaningful in a
     * query by example.
     * <p>
     * Required to override the normal behavior which is to ignore an
     * attribute of the example object if set to <code>null</code>, or an excluded value
     * like <code>0</code>.
     * <p>
     * Example: To find all projects without a budget set <code>budget</code> to 0 in the
     * example object and call <code>alwaysIncludeAttribute(Project.class, "budget")</code>
     * on the policy.
     * <p>
     * @param exampleClass The class that the attribute belongs to, normally this is the example class unless using nested QBE.
     * @param attributeName The name of a mapped attribute.
     */
    public void alwaysIncludeAttribute(Class exampleClass, String attributeName) {
        Vector included = (Vector)getAttributesToAlwaysInclude().get(exampleClass);
        if (included == null) {
            included = new Vector(3);
        }
        included.addElement(attributeName);

        getAttributesToAlwaysInclude().put(exampleClass, included);
    }

    /**
     * INTERNAL:
     * This method is used to determine which operation to use for comparison (equal, or a special operation).
     */
    public Expression completeExpression(Expression expression, Object attributeValue, Class attributeValueClass) {
        String operation = this.getOperation(attributeValue.getClass());

        if (operation == null) {
            //it means no special operation used. Use equal.
            return expression.equal(attributeValue);
        }

        Class[] argTypes = { attributeValueClass };
        Object[] args = { attributeValue };
        try {
            java.lang.reflect.Method anOperator = Helper.getDeclaredMethod(ClassConstants.Expression_Class, operation, argTypes);
            if (anOperator == null) {
                throw QueryException.methodDoesNotExistOnExpression(operation, argTypes);
            }
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                try{
                    expression = (Expression)AccessController.doPrivileged(new PrivilegedMethodInvoker(anOperator, expression, args));
                }catch (PrivilegedActionException ex){
                    throw (RuntimeException) ex.getCause();
                }
            }else{
                expression = (Expression)PrivilegedAccessHelper.invokeMethod(anOperator, expression, args);
            }
        } catch (NoSuchMethodException nsme) {
            Class superClass = attributeValueClass.getSuperclass();
            if (superClass != null) {
                return completeExpression(expression, attributeValue, superClass);
            } else {
                throw QueryException.methodDoesNotExistOnExpression(operation, argTypes);
            }
        } catch (IllegalAccessException iae) {
            throw QueryException.methodDoesNotExistOnExpression(operation, argTypes);
        } catch (java.lang.reflect.InvocationTargetException ite) {
            throw QueryException.methodDoesNotExistOnExpression(operation, argTypes);
        }
        return expression;
    }

    /**
     * INTERNAL:
     * This method is used when the attribute value is null, but it has
     * to be included at all times. It determines whether to use isNull, or notNull.
     */
    public Expression completeExpressionForNull(Expression expression) {
        if (shouldUseEqualityForNulls()) {
            return expression.isNull();
        } else {
            return expression.notNull();
        }
    }

    /**
     * PUBLIC:
     * Ignores attributes set to the default value for their primitive type.
     * <p>
     * For instance <code>0</code> is used as <code>null</code> for deciding
     * which <code>int</code> attributes of the example object can be ignored in a
     * query by example.
     * <p>
     * Called by the constructor.
     */
    public void excludeDefaultPrimitiveValues() {
        excludeValue(0);
        excludeValue(0.0);
        excludeValue(false);
        excludeValue((short)0);
        excludeValue('\u0000');
        excludeValue((long)0);
        excludeValue((byte)0);
        excludeValue(0.0f);
        excludeValue(new String(""));
    }

    /**
     * PUBLIC:
     * An attribute in the example object set to an excluded value will be
     * ignored in a Query By Example.<p>
     * The default excluded value for <code>byte</code> is <code>0</code>.
     */
    public void excludeValue(byte value) {
        excludeValue(Byte.valueOf(value));
    }

    /**
     * PUBLIC:
     * An attribute in the example object set to an excluded value will be
     * ignored in a Query By Example.<p>
     * The default excluded value for <code>char</code> is <code>'\u0000'</code>.
     */
    public void excludeValue(char value) {
        excludeValue(Character.valueOf(value));
    }

    /**
     * PUBLIC:
     * An attribute in the example object set to an excluded value will be
     * ignored in a Query By Example.<p>
     * The default excluded value for <code>double</code> is <code>0.0</code>.
     */
    public void excludeValue(double value) {
        excludeValue(Double.valueOf(value));
    }

    /**
     * PUBLIC:
     * An attribute in the example object set to an excluded value will be
     * ignored in a Query By Example.<p>
     * The default excluded value for <code>float</code> is <code>0.0f</code>.
     */
    public void excludeValue(float value) {
        excludeValue(Float.valueOf(value));
    }

    /**
     * PUBLIC:
     * An attribute in the example object set to be an excluded value will be
     * ignored in a Query By Example.<p>
     * The default excluded value for <code>int</code> is <code>0</code>.
     */
    public void excludeValue(int value) {
        excludeValue(Integer.valueOf(value));
    }

    /**
     * PUBLIC:
     * An attribute in the example object set to an excluded value will be
     * ignored in a Query By Example.<p>
     * The default excluded value for <code>long</code> is <code>0</code>.
     */
    public void excludeValue(long value) {
        excludeValue(Long.valueOf(value));
    }

    /**
     * PUBLIC:
     * An attribute in the example object set to an excluded value will be
     * ignored in a Query By Example.<p>
     * The default excluded value for <code>String</code> is <code>""</code>.<p>
     * Note: <code>null</code> is special and always considered an excluded value.
     */
    public void excludeValue(Object value) {
        this.valuesToExclude.put(value, value);
    }

    /**
     * PUBLIC:
     * An attribute in the example object set to an excluded value will be
     * ignored in a Query By Example.<p>
     * The default excluded value for <code>short</code> is <code>0</code>.
     */
    public void excludeValue(short value) {
        excludeValue(Short.valueOf(value));
    }

    /**
     * PUBLIC:
     * An attribute in the example object set to an excluded value will be
     * ignored in a Query By Example.<p>
     * The default excluded value for <code>boolean</code> is <code>false</code>.
     */
    public void excludeValue(boolean value) {
        excludeValue(Boolean.valueOf(value));
    }

    /**
     * PUBLIC:
     * Attributes to always consider even if set to <code>null</code> or an excluded
     * value like <code>0</code> or <code>false</code>.
     * @see #alwaysIncludeAttribute
     */
    public Map getAttributesToAlwaysInclude() {
        return attributesToAlwaysInclude;
    }

    /**
     * INTERNAL:
     * determines which operation to use for comparison.
     */
    public String getOperation(Class aClass) {
        String operation = (String)this.getSpecialOperations().get(aClass);
        if (operation != null) {
            if (!operation.equals("equal")) {
                return operation;
            }
        }
        return null;

    }

    /**
     * PUBLIC:
     * The special operations to use in place of <code>equal</code>.
     * @return A hashtable where the keys are <code>Class</code> objects and the values
     * are the names of operations to use for attributes of that <code>Class</code>.
     * @see #addSpecialOperation
     */
    public Map getSpecialOperations() {
        return specialOperations;
    }

    /**
     * PUBLIC:
     * Decides which attributes to ignore based on the values they are set to.
     * <p>
     * If an attribute of the example domain object is set to one of these values it will
     * be ignored, and not considered in the query.
     * <p>
     * Attributes set to excluded values are not always ignored.
     * See {@link #alwaysIncludeAttribute alwaysIncludeAttribute}.
     * @return valuesToExclude The keys and values are values to exclude (key == value).  Primitives are
     * wrapped, so <code>int 0</code> will be stored as <code>Integer(0)</code>.
     * @see #excludeValue
     * @see #excludeDefaultPrimitiveValues
     * @see #includeAllValues
     */
    public Map getValuesToExclude() {
        return valuesToExclude;
    }

    /**
     * PUBLIC:
     * Considers all mapped attributes in the example object as meaningful in a
     * Query By Example.<p>
     * Note: Even attributes of the example object that are
     * not set, and therefore zero or empty by default, will be included.<p>
     * Reverses a previous call to {@link #excludeDefaultPrimitiveValues}.
     */
    public void includeAllValues() {
        setValuesToExclude(new HashMap(5));
    }

    /**
     * INTERNAL:
     * returns whether the attributeName is to be always included.
     */
    public boolean isAlwaysIncluded(Class theClass, String attributeName) {
        Vector values = (Vector)this.getAttributesToAlwaysInclude().get(theClass);
        if (values != null) {
            return (values.contains(attributeName));
        }
        return false;

    }

    /**
     * INTERNAL:
     * returns if the value is in the values to be excluded automatically.
     */
    public boolean isExcludedValue(Object value) {
        return this.getValuesToExclude().containsKey(value);

    }

    /**
     * PUBLIC:
     * Considers all attributes set to a previously excluded value on the example object.
     * <p>
     * Primitive values to be removed must first be wrapped inside an Object.
     * <p>
     * @param value No attributes set to <code>value</code> will be excluded from a Query By Example.
     * <code>value.getClass()</code> is a key of the Hashtable returned by {@link #getValuesToExclude}.
     * <p>Note: There is a distinction between an attribute and the value
     * it is set to.  An attribute can be included independently of its value with
     * {@link #alwaysIncludeAttribute alwaysIncludeAttribute} (recommended).  It can also be included
     * by making the value it is set to no longer excluded.
     * <p>Note: <code>null</code> values are special and will always be excluded.
     * @see #excludeDefaultPrimitiveValues
     * @see #includeAllValues
     * @see #excludeValue(Object)
     */
    public void removeFromValuesToExclude(Object value) {
        getValuesToExclude().remove(value);
    }

    /**
     * INTERNAL:
     * It is possible to generate a Hashtable (keys are the Class, and values the attribute names)
     * of the attributes to be included at all times (even if the value is null, or the value
     * belongs to the values to be excluced automatically).
     */
    public void setAttributesToAlwaysInclude(Map newAttributesToAlwaysInclude) {
        attributesToAlwaysInclude = newAttributesToAlwaysInclude;
    }

    /**
     * PUBLIC:
     * Matches an included <code>null</code> attribute in the example object
     * either to objects with that attribute also set to <code>null</code> or to any
     * value other than <code>null</code>.
     * <p>
     * Set to <code>false</code> to only select objects where certain attributes have been set.
     * <p>
     * Example: to find all Employees with an assigned <code>address</code>, set
     * attribute <code>address</code> to <code>null</code> in the example object,
     * call <code>alwaysIncludeAttribute(Employee.class, "address")</code> and then
     * call <code>setShouldUseEqualityForNulls(false)</code>.
     * <p>
     * Note: Unless an attribute set to <code>null</code> is specifically included, it
     * will not be considered at all in the Query By Example.
     * @param shouldUseEqualityForNulls If true (by default) uses <code>isNull</code> else <code>notNull</code>.
     * @see #addSpecialOperation addSpecialOperation
     * @see #alwaysIncludeAttribute alwaysIncludeAttribute
     */
    public void setShouldUseEqualityForNulls(boolean shouldUseEqualityForNulls) {
        this.shouldUseEqualityForNulls = shouldUseEqualityForNulls;
    }

    /**
     * PUBLIC:
     * The special operations to use in place of <code>equal</code>.<p>
     * @param newOperations A hashtable where the keys are <code>Class</code> objects and the values
     * are the names of operations to use for attributes of that <code>Class</code>.
     * @see #addSpecialOperation
     */
    public void setSpecialOperations(Map newOperations) {
        specialOperations = newOperations;
    }

    /**
     * PUBLIC:
     * Decides which attributes to ignore based on the values they are set to.
     * <p>
     * An attribute of the example domain object set to one of these values will
     * be ignored, and not considered in the query.
     * <p>
     * Attributes set to excluded values are not always ignored.
     * See {@link #alwaysIncludeAttribute alwaysIncludeAttribute}.
     * @param newValuesToExclude The keys and values are values to exclude (key == value).  Primitives are
     * wrapped, so <code>int 0</code> will be stored as <code>Integer(0)</code>.
     * @see #excludeValue
     * @see #excludeDefaultPrimitiveValues
     * @see #includeAllValues
     */
    public void setValuesToExclude(Map newValuesToExclude) {
        valuesToExclude = newValuesToExclude;
    }

    /**
     * INTERNAL:
     * This method determines whether an attribute pair is be included in the query.
     */
    public boolean shouldIncludeInQuery(Class aClass, String attributeName, Object attributeValue) {
        if (attributeValue == null) {
            if (this.isAlwaysIncluded(aClass, attributeName)) {
                //this attribute is to be included always, even if its value is null.
                return true;
            } else {
                return false;
            }
        }

        if (this.isExcludedValue(attributeValue)) {
            if (this.isAlwaysIncluded(aClass, attributeName)) {
                //this attribute is to be included always, even if its value belongs to the list of values to be excluded.
                return true;
            } else {
                return false;
            }
        }

        return true;
    }

    /**
     * PUBLIC:
     * Matches an included <code>null</code> attribute in the example object
     * either to objects with that attribute also set to <code>null</code> or to any
     * value other than <code>null</code>.
     * <p>
     * Set to <code>false</code> to only select objects where certain attributes have been set.
     * <p>
     * Example: to find all Employees with an assigned <code>address</code>, set
     * attribute <code>address</code> to <code>null</code> in the example object,
     * call <code>alwaysIncludeAttribute(Employee.class, "address")</code> and then
     * call <code>setShouldUseEqualityForNulls(false)</code>.
     * <p>
     * @return If true (by default) uses <code>isNull</code> else <code>notNull</code>.
     * @see #addSpecialOperation addSpecialOperation
     * @see #alwaysIncludeAttribute alwaysIncludeAttribute
     */
    public boolean shouldUseEqualityForNulls() {
        return shouldUseEqualityForNulls;
    }
}
