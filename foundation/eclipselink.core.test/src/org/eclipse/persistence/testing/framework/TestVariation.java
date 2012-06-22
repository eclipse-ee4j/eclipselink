/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.framework;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Vector;

/**
 * <p>Purpose<b></b>:Creates several variations of the passed TestCase
 * (or of each member of a Vector of TestCases) using all combinations
 * of values of the specified boolean attributes on the specified object.
 * Suppose we want to run an existing test with four different DatabasePlatform settings:
 * shouldBindAllParameters  shouldCacheAllStatements
 * false                    false
 * false                    true
 * true                     false
 * true                     true
 * All we should do is to substitute:
 *      testSuite.addTest(test);
 * for:
 *      Object obj = getSession().getPlatform();
 *      String str = "shouldBindAllParameters shouldCacheAllStatements"
 *      testSuite.addTests(TestVariation.get(obj, str, test));
 * The attributes names should be separated by a space.
 * Reflection is used to find a getter and a setter for the specified attribute:
 * the list of all the methods with appropriate signature is examined, and the first
 * method with a name containing the specified name (case insensitive comparison) is used.
 * Therefore if the name passed is "foo", both "getFoo" and "setFoo" will be found,
 * but if there also a "getFooo" and "setFooo" methods, they could be wrongly picked up.
 * If the getter/setter pair, or a field for the specified name is not found,
 * a TestWarningException is thrown.
 */
public class TestVariation {
    protected static char separator = ' ';

    public static Vector get(Object object, String str, TestCase test) {
        Vector testsIn = new Vector(1);
        testsIn.add(test);
        return get(object, str, testsIn);
    }

    public static Vector get(Object object, String str, Vector testsIn) {
        Vector names = getNames(str);
        Vector testsOut = new Vector();
        if (names.isEmpty() || testsIn.isEmpty()) {
            return testsOut;
        }
        Vector getters = new Vector();
        Vector setters = new Vector();
        Vector fields = new Vector();
        getMembers(object.getClass(), names, getters, setters, fields, true);
        int numberOfTests = (int)java.lang.Math.pow(2, names.size());
        for (int i = 0; i < numberOfTests; i++) {
            Enumeration enumtr = testsIn.elements();
            while (enumtr.hasMoreElements()) {
                TestWrapper testWrapper = createTest(i, object, names, getters, setters, fields, (TestCase)enumtr.nextElement());
                testsOut.addElement(testWrapper);
            }
        }
        return testsOut;
    }

    protected static Vector getNames(String str) {
        Vector names = new Vector();
        String[] strPair = splitString(str);
        while (strPair[0] != null) {
            names.addElement(strPair[0]);
            strPair = splitString(strPair[1]);
        }
        return names;
    }

    protected static InnerTestWrapper createTest(int index, Object object, Vector names, Vector getters, Vector setters, Vector fields, TestCase test) {
        boolean[] values = new boolean[names.size()];
        int i = 0;
        while (index > 0) {
            values[i] = (index % 2) == 1;
            i++;
            index = index / 2;
        }
        String[] namesArray = new String[names.size()];
        namesArray = (String[])names.toArray(namesArray);
        Method[] gettersArray = new Method[names.size()];
        gettersArray = (Method[])getters.toArray(gettersArray);
        Method[] settersArray = new Method[names.size()];
        settersArray = (Method[])setters.toArray(settersArray);
        Field[] fieldsArray = new Field[names.size()];
        fieldsArray = (Field[])fields.toArray(fieldsArray);
        return new InnerTestWrapper(test, object, values, namesArray, gettersArray, settersArray, fieldsArray);
    }

    protected static String[] splitString(String str) {
        String[] out = new String[2];
        out[0] = null;
        out[1] = null;
        if (str == null) {
            return out;
        }

        int first = -1;
        int lastPlusOne = str.length();
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (first == -1) {
                if (ch != separator) {
                    first = i;
                }
            } else {
                if (ch == separator) {
                    lastPlusOne = i;
                    break;
                }
            }
        }
        if (first == -1) {
            return out;
        }

        out[0] = str.substring(first, lastPlusOne);
        out[1] = str.substring(lastPlusOne, str.length());

        return out;
    }

    protected static void getMembers(Class cls, Vector names, Vector getters, Vector setters, Vector fields, boolean throwExceptionIfNotFound) {
        Method[] allMethods = cls.getMethods();
        Field[] allFields = cls.getFields();

        Vector candidateGetters = new Vector();
        Vector candidateSetters = new Vector();
        Vector candidateFields = new Vector();

        // Need those to save lower case names
        Vector candidateGettersNames = new Vector();
        Vector candidateSettersNames = new Vector();
        Vector candidateFieldsNames = new Vector();

        for (int i = 0; i < allMethods.length; i++) {
            Class returnType = allMethods[i].getReturnType();
            Class[] parameterTypes = allMethods[i].getParameterTypes();
            if (returnType.equals(boolean.class) && (parameterTypes.length == 0)) {
                candidateGetters.addElement(allMethods[i]);
                candidateGettersNames.addElement(allMethods[i].getName().toLowerCase());
            } else if (returnType.equals(void.class) && (parameterTypes.length == 1)) {
                if (parameterTypes[0].equals(boolean.class)) {
                    candidateSetters.addElement(allMethods[i]);
                    candidateSettersNames.addElement(allMethods[i].getName().toLowerCase());
                }
            }
        }
        for (int i = 0; i < allFields.length; i++) {
            Class type = allFields[i].getType();
            if (type.equals(boolean.class)) {
                candidateFields.addElement(allFields[i]);
                candidateFieldsNames.addElement(allFields[i].getName().toLowerCase());
            }
        }
        for (int i = 0; i < names.size(); i++) {
            Object getter = null;
            Object setter = null;
            Object field = null;
            String name = ((String)names.elementAt(i)).toLowerCase();
            for (int j = 0; j < candidateGetters.size(); j++) {
                if (((String)candidateGettersNames.elementAt(j)).indexOf(name) != -1) {
                    getter = candidateGetters.elementAt(j);
                    candidateGetters.remove(j);
                    candidateGettersNames.remove(j);
                    break;
                }
            }
            for (int j = 0; j < candidateSetters.size(); j++) {
                if (((String)candidateSettersNames.elementAt(j)).indexOf(name) != -1) {
                    setter = candidateSetters.elementAt(j);
                    candidateSetters.remove(j);
                    candidateSettersNames.remove(j);
                    break;
                }
            }
            for (int j = 0; j < candidateFields.size(); j++) {
                if (((String)candidateFieldsNames.elementAt(j)).indexOf(name) != -1) {
                    field = candidateFields.elementAt(j);
                    candidateFields.remove(j);
                    candidateFieldsNames.remove(j);
                    break;
                }
            }
            if ((field != null) || ((setter != null) && (getter != null))) {
                setters.add(setter);
                getters.add(getter);
                fields.add(field);
            } else {
                if (throwExceptionIfNotFound) {
                    throw new TestWarningException("Can't find a setter/getter or field for " + names.elementAt(i));
                } else {
                    names.remove(i);
                    i--;
                }
            }
        }
    }

    protected static class InnerTestWrapper extends TestWrapper {
        protected Object object;
        private Field[] fields;
        private Method[] getters;
        private Method[] setters;
        private boolean[] required;
        private boolean[] original;

        public InnerTestWrapper(TestCase test, Object object, boolean[] values, String[] names, Method[] getters, Method[] setters, Field[] fields) {
            super(test);
            this.object = object;
            original = new boolean[values.length];
            required = new boolean[values.length];
            this.getters = getters;
            this.setters = setters;
            this.fields = fields;
            System.arraycopy(values, 0, required, 0, values.length);
            for (int i = 0; i < values.length; i++) {
                String name;
                if (fields[i] != null) {
                    name = fields[i].getName();
                } else {
                    name = names[i];
                }
                setName(getName() + " " + name + "=" + values[i]);
            }
        }

        protected void setup() throws Throwable {
            for (int i = 0; i < required.length; i++) {
                if (getters[i] != null) {
                    Object[] args = {  };
                    Boolean res = (Boolean)getters[i].invoke(object, args);
                    original[i] = res.booleanValue();
                } else {
                    original[i] = fields[i].getBoolean(object);
                }

                if (setters[i] != null) {
                    Object[] args = { new Boolean(required[i]) };
                    setters[i].invoke(object, args);
                } else {
                    fields[i].setBoolean(object, required[i]);
                }
            }
            super.setup();
        }

        public void reset() throws Throwable {
            super.reset();
            for (int i = required.length - 1; i >= 0; i--) {
                if (setters[i] != null) {
                    Object[] args = { new Boolean(original[i]) };
                    setters[i].invoke(object, args);
                } else {
                    fields[i].setBoolean(object, original[i]);
                }
            }
        }
    }
}
