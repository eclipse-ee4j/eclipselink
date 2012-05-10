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
 *     Mike Norman - from Proof-of-concept, become production code
 ******************************************************************************/
package dbws.testing.shadowddlgeneration.oldjpub;

//EclipseLink imports

/**
 * A Method provides information about a single method of a type.
 */
public class ProcedureMethod implements Comparable<ProcedureMethod> {

    public static final int IN = 1;
    public static final int OUT = 2;
    public static final int INOUT = IN ^ OUT;
    public static final int RETURN = 4;
    public static final int ALL = 0;

    protected String m_name;
    protected int m_modifiers;
    protected TypeClass m_returnType;
    protected TypeClass[] m_paramTypes;
    protected String[] m_paramNames;
    protected int[] m_paramModes;
    protected boolean[] m_paramDefaults;
    protected boolean m_hasDefault;
    protected boolean m_keepMethodName = false;
    protected String m_overloadNumber;

    /**
     * Construct a Method
     */
    public ProcedureMethod(String name, int modifiers) {
        m_name = name;
        m_modifiers = modifiers;
    }

    /**
     * Returns the simple name of the underlying member or constructor represented by this JSMember.
     */
    public String getName() {
        return m_name;
    }

    /**
     * Returns the source language modifiers for the member or constructor represented by this
     * Member, as an integer. The Modifier class should be used to decode the modifiers in the
     * integer.
     */
    public int getModifiers() {
        return m_modifiers;
    }

    /**
     * Construct a Method
     */
    public ProcedureMethod(String name, String overloadNumber, int modifiers, TypeClass returnType,
        TypeClass[] parameterTypes, String[] parameterNames, int[] parameterModes,
        boolean[] parameterDefaults, int paramLen) {
        this(name, modifiers);
        m_returnType = returnType;
        m_overloadNumber = overloadNumber;
        if (paramLen > -1 && parameterTypes != null && parameterNames != null
            && parameterModes != null && parameterDefaults != null) {
            m_paramTypes = new TypeClass[paramLen];
            m_paramNames = new String[paramLen];
            m_paramModes = new int[paramLen];
            m_paramDefaults = new boolean[paramLen];
            m_hasDefault = false;
            for (int i = 0; i < paramLen; i++) {
                m_paramModes[i] = parameterModes[i];
                m_paramNames[i] = parameterNames[i];
                m_paramTypes[i] = parameterTypes[i];
                m_paramDefaults[i] = parameterDefaults[i];
                if (m_paramDefaults[i]) {
                    m_hasDefault = true;
                }
            }
        }
    }

    /**
     * Returns a Type object that represents the formal return type of the method represented by
     * this Method object. If the method does not return anything, getReturnType() may return null
     * or a representation of the void type.
     */
    public TypeClass getReturnType() {
        return m_returnType;
    }

    /**
     * Returns an array of String objects that represent the formal parameter names, in declaration
     * order, of the method represented by this Method object. Returns an array of length 0 if the
     * underlying method takes no parameters. Returns null if the parameter names are not known.
     */
    public String[] getParamNames() {
        return m_paramNames;
    }

    /**
     * Returns an array of Type objects that represent the formal parameter types, in declaration
     * order, of the method represented by this Method object. Returns an array of length 0 if the
     * underlying method takes no parameters.
     */
    public TypeClass[] getParamTypes() {
        return m_paramTypes;
    }

    /**
     * Returns an array of ints that represent the parameter modes, in declaration order, of the
     * method represented by this Method object. Returns an array of length 0 if the underlying
     * method takes no parameters. Possible modes are: IN, OUT, INOUT.
     */
    public int[] getParamModes() {
        return m_paramModes;
    }

    public boolean[] getParamDefaults() {
        return m_paramDefaults;
    }

    public boolean hasDefault() {
        return m_hasDefault;
    }

    public String getSqlStatement() {
        return null;
    }

    public String toString() {
        String printout = m_returnType + " " + m_name + "(";
        for (int i = 0; i < m_paramTypes.length; i++) {
            printout += m_paramTypes[i].toString() + " " + m_paramNames[i].toString();
            if (i < m_paramTypes.length - 1) {
                printout += ",";
            }
        }
        printout += ");";
        return printout;
    }

    // Keep Java method name same as Sql method name
    // Use case: SqlStatementMethod and -dbjava generated methods
    public boolean keepMethodName() {
        return m_keepMethodName;
    }

    public void setKeepMethodName(boolean keep) {
        m_keepMethodName = keep;
    }

    protected String getSortingKey() {
        String key = m_name;
        if (m_overloadNumber != null) {
            for (int i = 0; i < 6 - m_overloadNumber.length(); i++) {
                key = key + "9";
            }

            key = key + m_overloadNumber;
        }
        for (int i = 0; i < m_paramTypes.length; i++) {
            if (i > 0) {
                key += ",";
            }
            key += m_paramTypes[i].getName();
        }
        return key;
    }
    public int compareTo(ProcedureMethod other) {
        return getSortingKey().compareTo(other.getSortingKey());
    }
}
