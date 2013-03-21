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

import java.lang.reflect.*;
import java.security.AccessController;

import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedMethodInvoker;
import org.eclipse.persistence.internal.sessions.AbstractRecord;

/**
 * <p><b>Purpose</b>:
 * Allows a class to be a <code>QueryRedirector</code> without implementing
 * {@link QueryRedirector QueryRedirector}.
 *
 * <p><b>Description</b>:
 * Normally to define a Redirector a Class must implement <code>QueryRedirector</code> and
 * the required {@link QueryRedirector#invokeQuery QueryRedirector.invokeQuery(DatabaseQuery, Record, Session)}.
 * <p>
 * To maintain transparency it is possible to instead only define a static
 * method that takes the same arguments as <code>invokeQuery</code>.
 * <p>
 * An instance of <code>MethodBaseQueryRedirector</code> can be constructed, taking the name of that static
 * method and the <code>Class</code> in which it is defined as parameters.
 * <p>
 * Whenever <code>invokeQuery</code> is called on this instance reflection will
 * automatically be used to invoke the custom method instead.
 * <p>
 * <b>Advantages</b>:
 * <ul>
 * <li> The Redirector class and method name can be specified dynamically.
 * <li> The class containing the <code>invokeQuery</code> method does not need to implement
 * <code>QueryRedirector</code>.
 * <li> The <code>invokeQuery</code> method can have any name.
 * <li> The <code>invokeQuery</code> method can alternatively be defined to accept only
 * <code>Session session</code> and <code>Vector arguments</code> as parameters.
 * </ul>
 * <b>Disadvantages</b>:
 * <ul>
 * <li> An extra step is added as the real <code>invokeQuery</code> method is called
 * dynamically.
 * </ul>
 * <p><b>Example</b>:
 * <PRE><BLOCKQUOTE>
 * // First create a named query, define a redirector for it, and add the query
 * // to the query manager.
 * ReadObjectQuery query = new ReadObjectQuery(Employee.class);
 * query.setName("findEmployeeByAnEmployee");
 * query.addArgument("employee");
 *
 * MethodBaseQueryRedirector redirector = new
 * MethodBaseQueryRedirector(QueryRedirectorTest.class, "findEmployeeByAnEmployee");
 * query.setRedirector(redirector);
 * ClassDescriptor descriptor = getSession().getDescriptor(query.getReferenceClass());
 * descriptor.getQueryManager().addQuery(query.getName(), query);
 *
 * // Now execute the query by name, passing in an Employee as an argument.
 * Vector arguments = new Vector();
 * arguments.addElement(employee);
 * objectFromDatabase =
 * getSession().executeQuery("findEmployeeByAnEmployee", Employee.class, arguments);
 *
 * // Note this Class does not implement QueryRedirector or method invokeQuery.
 * public class QueryRedirectorTest {
 * public static Object findEmployeeByAnEmployee(DatabaseQuery query, Record arguments, Session session) {
 * ((ReadObjectQuery) query).setSelectionObject(arguments.get("employee"));
 * return session.executeQuery(query);
 * }
 * }</PRE><BLOCKQUOTE><p>
 *
 * @see QueryRedirector
 * @author James Sutherland
 * @since TOPLink/Java 3.0
 */
public class MethodBaseQueryRedirector implements QueryRedirector {
    protected Class methodClass;
    protected String methodClassName;
    protected String methodName;
    protected transient Method method;

    /**
     * PUBLIC:
     * Returns a new query redirector.
     */
    public MethodBaseQueryRedirector() {
    }

    /**
     * PUBLIC:
     * Returns a new query redirector based on the static method in methodClass.
     */
    public MethodBaseQueryRedirector(Class methodClass, String methodName) {
        this.methodClass = methodClass;
        this.methodName = methodName;
    }

    /**
     * INTERNAL:
     * Returns the static method.
     */
    protected Method getMethod() {
        return method;
    }

    /**
     * PUBLIC:
     * Returns the class to execute the static method on.
     */
    public Class getMethodClass() {
        return methodClass;
    }

    /**
     * INTERNAL:
     * Returns the class to execute the static method on.
     */
    public String getMethodClassName() {
        if ((methodClassName == null) && (methodClass != null)) {
            methodClassName = methodClass.getName();
        }
        return methodClassName;
    }

    /**
     * PUBLIC:
     * Returns the name of the static method.
     * This method must be public, static and have argument of DatabaseQuery, Vector, Session.
     * @see #setMethodName
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * INTERNAL:
     * Set the method.
     */
    protected void initializeMethod(DatabaseQuery query) throws QueryException {
        if ((getMethodName() == null) || (getMethodClass() == null)) {
            throw QueryException.redirectionClassOrMethodNotSet(query);
        }

        // Must check 3 possible argument sets for backward compatibility.
        // The DatabaseQuery, Record, Session should be used, check last the throw correct exception.
        // Check Session, Vector.
        Class[] arguments = new Class[2];
        arguments[0] = ClassConstants.SessionsSession_Class;
        arguments[1] = ClassConstants.Vector_class;
        try {
            setMethod(Helper.getDeclaredMethod(getMethodClass(), getMethodName(), arguments));
        } catch (Exception ignore) {
            // Check DatabaseQuery, Record, Session.
            arguments = new Class[3];
            arguments[0] = ClassConstants.DatabaseQuery_Class;
            arguments[1] = ClassConstants.Record_Class;
            arguments[2] = ClassConstants.SessionsSession_Class;
            try {
                setMethod(Helper.getDeclaredMethod(getMethodClass(), getMethodName(), arguments));
            } catch (Exception ignoreAgain) {
                // Check DatabaseQuery, Record, Session.
                arguments = new Class[3];
                arguments[0] = ClassConstants.DatabaseQuery_Class;
                arguments[1] = ClassConstants.Record_Class;
                arguments[2] = ClassConstants.SessionsSession_Class;
                try {
                    setMethod(Helper.getDeclaredMethod(getMethodClass(), getMethodName(), arguments));
                } catch (Exception exception) {
                    throw QueryException.redirectionMethodNotDefinedCorrectly(getMethodClass(), getMethodName(), exception, query);
                }
            }
        }

        // Ensure the method is static.
        if (!Modifier.isStatic(getMethod().getModifiers())) {
            throw QueryException.redirectionMethodNotDefinedCorrectly(getMethodClass(), getMethodName(), null, query);
        }
    }

    /**
     * INTERNAL:
     * Call the static method to execute the query.
     */
    public Object invokeQuery(DatabaseQuery query, Record arguments, Session session) {
        if (getMethod() == null) {
            initializeMethod(query);
        }

        // To different methods type are supported for backward compatibility.
        // Check method types to call with correct arguments.
        Object result = null;
        if (getMethod().getParameterTypes().length == 3) {
            Object[] argumentArray = new Object[3];
            argumentArray[0] = query;
            argumentArray[1] = arguments;
            argumentArray[2] = session;
            try {
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                    result = AccessController.doPrivileged(new PrivilegedMethodInvoker(getMethod(), null, argumentArray));
                }else{
                    result = PrivilegedAccessHelper.invokeMethod(getMethod(), null, argumentArray);
                }
            } catch (Exception exception) {
                throw QueryException.redirectionMethodError(exception, query);
            }
        } else {
            Object[] argumentArray = new Object[2];
            argumentArray[0] = session;
            argumentArray[1] = ((AbstractRecord)arguments).getValues();
            try {
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                    result = AccessController.doPrivileged(new PrivilegedMethodInvoker(getMethod(), null, argumentArray));
                }else{
                    result = PrivilegedAccessHelper.invokeMethod(getMethod(), null, argumentArray);
                }
            } catch (Exception exception) {
                throw QueryException.redirectionMethodError(exception, query);
            }
        }

        return result;
    }

    /**
     * INTERNAL:
     * Sets the static method.
     */
    protected void setMethod(Method newMethod) {
        method = newMethod;
    }

    /**
     * PUBLIC:
     * Sets the class to execute the static method on.
     */
    public void setMethodClass(Class newMethodClass) {
        methodClass = newMethodClass;
    }

    /**
     * INTERNAL:
     * Sets the class to execute the static method on.
     */
    public void setMethodClassName(String newMethodClassName) {
        methodClassName = newMethodClassName;
    }

    /**
     * PUBLIC:
     * Sets the name of the static method.<p>
     * This method must be public, static and have arguments of DatabaseQuery, Record, and Session.
     * <p>
     * The DatabaseQuery argument is the query that is currently being executed.
     * <p>
     * The Record will contain the Argument names added to the Query through addArgument(Sting) or, in the case
     * of an Object query, the object attribute field names.  These names will
     * reference the argument values passed into the query, or in the case of an
     * Object Query the values from the object.
     * <p>
     * The session argument is the session that the query is currently being executed on.
     * <p>
     * Alternatively the method can take only <code>(Session session, Vector arguments)</code>
     * as parameters.
     */
    public void setMethodName(String newMethodName) {
        methodName = newMethodName;
    }
}
