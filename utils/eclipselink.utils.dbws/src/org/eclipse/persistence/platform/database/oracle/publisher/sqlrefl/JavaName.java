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
 *     Mike Norman - from Proof-of-concept, become production code
 ******************************************************************************/
package org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl;

/**
 * A JavaName encapsulates the Java name of a class. A JavaName identifies the package name, the
 * class name within the package, and the "generated" name of the class.
 */
public class JavaName extends LangName {

    static String m_defaultPackage;

    // Generated user class interface
    protected String m_useItfPackage, m_useItf;
    // Declared Java class interface
    protected String m_declItfPackage, m_declItf;
    protected String m_usePackage;

    /**
     * Initializes a JavaName with a package name, use name, and decl name. If the supplied package
     * is null, the default package will be used. If the supplied decl name is null (the usual
     * case), the decl name will be the same as the use name. The decl name is the name of the class
     * that JPub generates. It is different from the use name, the name of the class that represents
     * the SQL type, if the class is user-written. The user tells JPub that this is the case by
     * putting the clause "GENERATE <decl class name> AS <use class name> in the input file.
     *
     * * @param package the package in which the class is declared
     *
     * @param useName
     *            the use name (class name for use as opposed to declaration)
     * @param declName
     *            the decl name (class name for declaration as opposed to use)
     */
    public JavaName(String packageName, String useName, String useItf, String declName,
        String declItf) {

        if (packageName == null) {
            packageName = "";
        }

        int dotPos = useName.lastIndexOf('.');
        if (dotPos >= 0) {
            m_usePackage = packageConcat(packageName, useName.substring(0, dotPos));
            m_useClass = useName.substring(dotPos + 1);
        }
        else {
            m_usePackage = (packageName.length() > 0) ? packageName : m_defaultPackage;
            m_useClass = useName;
        }

        try {
            dotPos = useItf.lastIndexOf('.');
        }
        catch (Exception e) {
            dotPos = -1;
        }

        if (dotPos >= 0) {
            m_useItfPackage = packageConcat(packageName, useItf.substring(0, dotPos));
            m_useItf = useItf.substring(dotPos + 1);
        }
        else {
            m_useItfPackage = (packageName.length() > 0) ? packageName : m_defaultPackage;
            m_useItf = useItf;
        }

        if (declName == null) {
            m_context = m_usePackage;
            m_name = m_useClass;
            m_declItf = m_useItf;
            m_declItfPackage = m_useItfPackage;
            m_useItf = null;
        }
        else {
            dotPos = declName.lastIndexOf('.');
            if (dotPos >= 0) {
                m_context = packageConcat(packageName, declName.substring(0, dotPos));
                m_name = declName.substring(dotPos + 1);
            }
            else {
                m_context = (packageName.length() > 0) ? packageName : m_defaultPackage;
                m_name = declName;
            }
            try {
                dotPos = declItf.lastIndexOf('.');
            }
            catch (Exception e) {
                dotPos = -1;
            }
            // Only allow one interface. If both defined, use m_useItf
            if (dotPos >= 0 && m_useItf == null) {
                m_declItfPackage = packageConcat(packageName, declItf.substring(0, dotPos));
                m_declItf = declItf.substring(dotPos + 1);
            }
            else {
                m_declItfPackage = (packageName.length() > 0) ? packageName : m_defaultPackage;
                m_declItf = declItf;
            }
        }

        processUnicode();
    }

    /**
     * Create a new JavaName. Do not modify the packageName, even if it is "". Both the useName and
     * declName are set equal to the typeName. This constructor is used for predefined Java types.
     */
    public JavaName(String packageName, String typeName) {
        super(packageName = (packageName == null ? "" : packageName), typeName);
        m_usePackage = packageName;
        m_useClass = typeName;
        processUnicode();
    }

    // Convert unicode in Java class/package names into ascii
    private void processUnicode() {
        m_name = unicode2Ascii(m_name);
        m_context = unicode2Ascii(m_context);
        m_declItf = unicode2Ascii(m_declItf);
        m_declItfPackage = unicode2Ascii(m_declItfPackage);
        m_useItf = unicode2Ascii(m_useItf);
        m_useItfPackage = unicode2Ascii(m_useItfPackage);
        m_useClass = unicode2Ascii(m_useClass);
        m_usePackage = unicode2Ascii(m_usePackage);
    }

    public String toString() {
        return packageConcat(getContextName(), getSimpleName());
    }

    /**
     * Returns the package name of the decl interface
     */
    public String getDeclItfPackage() {
        return m_declItfPackage;
    }

    /**
     * Returns the name of the decl interface
     */
    public String getDeclItf() {
        return m_declItf;
    }

    /**
     * Returns the name of the decl interface
     */
    public String getDeclItf(String currPackage) {
        if (currPackage == null) {
            return m_declItf;
        }
        return currPackage.equals(m_declItfPackage) ? m_declItf : packageConcat(m_declItfPackage,
            m_declItf);
    }

    public String getDeclClass(String currPackage) {
        if (currPackage == null) {
            return m_name;
        }
        return currPackage.equals(m_context) ? m_useClass : packageConcat(m_context, m_name);
    }

    public String getUseClass() {
        return m_useClass;
    }

    public String getUseClass(boolean full) {
        if (full) {
            return packageConcat(m_usePackage, m_useClass);
        }
        else {
            return m_useClass;
        }
    }

    /**
     * Returns the package name of the use class
     */
    public String getUsePackage() {
        return m_usePackage;
    }

    /**
     * Returns the package name of the use interface
     */
    public String getUseItfPackage() {
        return m_useItfPackage;
    }

    /**
     * if the represented type has the user subclass
     */
    public boolean hasUseClass() {
        boolean has = true;
        if (m_name == null || m_context == null || m_useClass == null) {
            has = false;
        }
        else if (m_name.equals(m_useClass) && m_context.equals(m_usePackage)) {
            has = false;
        }
        return has;
    }

    /**
     * Returns the name to be used to refer to this class. If the package in which this class is
     * mentioned is the use package of this class, return the unqualified use class name. Otherwise,
     * return use package name + "." + use class name.
     *
     * * @param currPackage the package in which the reference occurs
     */
    public String getUseClass(String currPackage) {
        if (currPackage == null) {
            return m_useClass;
        }
        return currPackage.equals(m_usePackage) ? m_useClass : packageConcat(m_usePackage,
            m_useClass);
    }

    // Undo genpattern for SQL collections
    // , which cannot have subclasses
    public void ungenPattern(String simpleName) {
        m_name = SqlName.sqlIdToJavaId(simpleName, true);
        m_useClass = m_name;
        m_usePackage = m_context;
        m_useItf = null;
        m_useItfPackage = m_declItfPackage;
    }

    /**
     * Returns the name of the use interface
     */
    public String getUseItf() {
        return m_useItf;
    }

    /**
     * Returns the name of the use interface
     */
    public String getUseItf(String currPackage) {
        if (currPackage == null) {
            return packageConcat(m_useItfPackage, m_useItf);
        }
        return currPackage.equals(m_useItfPackage) ? m_useItf : packageConcat(m_useItfPackage,
            m_useItf);
    }

    /**
     * Initialize the JavaName class with the default package name, used when a JavaName is created
     * without an explciit package name.
     *
     * * @param defaultPackage the name of the default package
     */
    public static void setDefaultPackage(String defaultPackage) {
        m_defaultPackage = defaultPackage;
    }

    public static String getDefaultPackage() {
        return m_defaultPackage;
    }

}
