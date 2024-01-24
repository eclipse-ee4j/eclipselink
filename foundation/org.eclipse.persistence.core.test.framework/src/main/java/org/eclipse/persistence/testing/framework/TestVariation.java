/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.framework;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Vector;

/**
 * <p><b>Purpose</b>:Creates several variations of the passed TestCase
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

    public static Vector<TestCase> get(Object object, String str, TestCase test) {
        Vector<TestCase> testsIn = new Vector<>(1);
        testsIn.add(test);
        return get(object, str, testsIn);
    }

    public static Vector<TestCase> get(Object object, String str, Vector<TestCase> testsIn) {
        Vector<String> names = getNames(str);
        Vector<TestCase> testsOut = new Vector<>();
        if (names.isEmpty() || testsIn.isEmpty()) {
            return testsOut;
        }
        Vector<Method> getters = new Vector<>();
        Vector<Method> setters = new Vector<>();
        Vector<Field> fields = new Vector<>();
        getMembers(object.getClass(), names, getters, setters, fields, true);
        int numberOfTests = (int)java.lang.Math.pow(2, names.size());
        for (int i = 0; i < numberOfTests; i++) {
            Enumeration<TestCase> enumtr = testsIn.elements();
            while (enumtr.hasMoreElements()) {
                TestWrapper testWrapper = createTest(i, object, names, getters, setters, fields, enumtr.nextElement());
                testsOut.addElement(testWrapper);
            }
        }
        return testsOut;
    }

    protected static Vector<String> getNames(String str) {
        Vector<String> names = new Vector<>();
        String[] strPair = splitString(str);
        while (strPair[0] != null) {
            names.addElement(strPair[0]);
            strPair = splitString(strPair[1]);
        }
        return names;
    }

    protected static InnerTestWrapper createTest(int index, Object object, Vector<String> names, Vector<Method> getters, Vector<Method> setters, Vector<Field> fields, TestCase test) {
        boolean[] values = new boolean[names.size()];
        int i = 0;
        while (index > 0) {
            values[i] = (index % 2) == 1;
            i++;
            index = index / 2;
        }
        String[] namesArray = new String[names.size()];
        namesArray = names.toArray(namesArray);
        Method[] gettersArray = new Method[names.size()];
        gettersArray = getters.toArray(gettersArray);
        Method[] settersArray = new Method[names.size()];
        settersArray = setters.toArray(settersArray);
        Field[] fieldsArray = new Field[names.size()];
        fieldsArray = fields.toArray(fieldsArray);
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
        out[1] = str.substring(lastPlusOne);

        return out;
    }

    protected static void getMembers(Class<?> cls, Vector<String> names, Vector<Method> getters, Vector<Method> setters, Vector<Field> fields, boolean throwExceptionIfNotFound) {
        Method[] allMethods = cls.getMethods();
        Field[] allFields = cls.getFields();

        Vector<Method> candidateGetters = new Vector<>();
        Vector<Method> candidateSetters = new Vector<>();
        Vector<Field> candidateFields = new Vector<>();

        // Need those to save lower case names
        Vector<String> candidateGettersNames = new Vector<>();
        Vector<String> candidateSettersNames = new Vector<>();
        Vector<String> candidateFieldsNames = new Vector<>();

        for (Method allMethod : allMethods) {
            Class<?> returnType = allMethod.getReturnType();
            Class<?>[] parameterTypes = allMethod.getParameterTypes();
            if (returnType.equals(boolean.class) && (parameterTypes.length == 0)) {
                candidateGetters.addElement(allMethod);
                candidateGettersNames.addElement(allMethod.getName().toLowerCase());
            } else if (returnType.equals(void.class) && (parameterTypes.length == 1)) {
                if (parameterTypes[0].equals(boolean.class)) {
                    candidateSetters.addElement(allMethod);
                    candidateSettersNames.addElement(allMethod.getName().toLowerCase());
                }
            }
        }
        for (Field allField : allFields) {
            Class<?> type = allField.getType();
            if (type.equals(boolean.class)) {
                candidateFields.addElement(allField);
                candidateFieldsNames.addElement(allField.getName().toLowerCase());
            }
        }
        for (int i = 0; i < names.size(); i++) {
            Method getter = null;
            Method setter = null;
            Field field = null;
            String name = names.elementAt(i).toLowerCase();
            for (int j = 0; j < candidateGetters.size(); j++) {
                if (candidateGettersNames.elementAt(j).contains(name)) {
                    getter = candidateGetters.elementAt(j);
                    candidateGetters.remove(j);
                    candidateGettersNames.remove(j);
                    break;
                }
            }
            for (int j = 0; j < candidateSetters.size(); j++) {
                if (candidateSettersNames.elementAt(j).contains(name)) {
                    setter = candidateSetters.elementAt(j);
                    candidateSetters.remove(j);
                    candidateSettersNames.remove(j);
                    break;
                }
            }
            for (int j = 0; j < candidateFields.size(); j++) {
                if (candidateFieldsNames.elementAt(j).contains(name)) {
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

        @Override
        protected void setup() throws Throwable {
            for (int i = 0; i < required.length; i++) {
                if (getters[i] != null) {
                    Object[] args = {  };
                    Boolean res = (Boolean)getters[i].invoke(object, args);
                    original[i] = res;
                } else {
                    original[i] = fields[i].getBoolean(object);
                }

                if (setters[i] != null) {
                    Object[] args = {required[i]};
                    setters[i].invoke(object, args);
                } else {
                    fields[i].setBoolean(object, required[i]);
                }
            }
            super.setup();
        }

        @Override
        public void reset() throws Throwable {
            super.reset();
            for (int i = required.length - 1; i >= 0; i--) {
                if (setters[i] != null) {
                    Object[] args = {original[i]};
                    setters[i].invoke(object, args);
                } else {
                    fields[i].setBoolean(object, original[i]);
                }
            }
        }
    }
}
