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
package org.eclipse.persistence.testing.tests.transparentindirection;

import java.util.*;
import java.lang.reflect.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * provide support for zunit stuff...
 */
public class ZTestSuite extends TestSuite {
    public ZTestSuite() {
        this("Unnamed Test Suite");
    }

    public ZTestSuite(Class testCaseClass) {
        this(testCaseClass.getName(), testCaseClass);
    }

    public ZTestSuite(String name) {
        super();
        this.setName(name);
    }

    public ZTestSuite(String name, Class testCaseClass) {
        this(name);
        this.add(testCaseClass);
    }

    public void add(Class testCaseClass) {
        Constructor ctor = this.singleArgumentConstructorFor(testCaseClass);
        Object[] arguments = new Object[1];

        Enumeration enumtr = this.methodNamesStartingWithTestFor(testCaseClass);
        while (enumtr.hasMoreElements()) {
            try {
                arguments[0] = enumtr.nextElement();
                this.addTest((ZTestCase)ctor.newInstance(arguments));
            } catch (IllegalAccessException iae) {
                throw new RuntimeException("The constructor \"" + ctor + "\" (and its class) must be public.");
            } catch (InvocationTargetException ite) {
                throw new RuntimeException("The constructor \"" + ctor + "\" threw an exception during invocation: " + ite.getTargetException());
            } catch (InstantiationException ie) {
                throw new RuntimeException("The constructor \"" + ctor + "\" was unable to instantiate an instance....");
            }
        }
    }

    /**
     * Return an enumeration on all the method names for the 
     * specified class that begin with "test", removing any duplicates.
     * @param testCaseClass a subclass of <code>TestCase</code>
     */
    public Enumeration methodNamesStartingWithTestFor(Class testClass) {
        Method[] methods = testClass.getMethods();
        Vector names = new Vector(methods.length);
        for (int i = 0; i < methods.length; i++) {
            String name = methods[i].getName();
            if (name.startsWith("test")) {
                names.addElement(name);
            }
        }
        Hashtable uniqueNames = new Hashtable();
        for (Enumeration e = names.elements(); e.hasMoreElements();) {
            Object name = e.nextElement();
            uniqueNames.put(name, name);// use the Hashtable as a Set
        }
        return uniqueNames.elements();
    }

    /**
     * Return the required constructor for the specified class.
     * @param testCaseClass a subclass of <code>TestCase</code>
     */
    private Constructor singleArgumentConstructorFor(Class testCaseClass) {
        try {
            return testCaseClass.getConstructor(new Class[] { String.class });
        } catch (NoSuchMethodException e) {
            String name = testCaseClass.getName();
            throw new RuntimeException("The class '" + name + "' must implement the constructor '" + name + "(String)'");
        }
    }
}
