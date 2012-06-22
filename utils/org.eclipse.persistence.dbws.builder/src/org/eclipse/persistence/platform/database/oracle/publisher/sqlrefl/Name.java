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
package org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl;

/**
 * A Name encapsulates the name of an entity. A Name identifies the context (e.g., schema or Java
 * package) in which the entity is declared, and the simple name of the entity within the context.
 * This class implements 'equals' and 'hash', so that Names may be stored in Hashtables.
 */
public abstract class Name {

    public static final String NO_CONTEXT = "";

    protected String m_context;
    protected String m_name;
    protected Object m_annotation;

    /**
     * Initializes a Name with the context and simple name of a declared entity. The context and
     * name arguments may not be null.
     *
     * * @param context the context in which the entity is declared
     *
     * @param name
     *            the declared name of the entity in the context
     */
    public Name(String context, String name) {
        m_context = context;
        m_name = name;
    }

    /**
     * Returns the context name of the declared entity.
     */
    public String getContextName() {
        return m_context;
    }

    /**
     * Returns the name of the declared entity within the context.
     */
    public String getSimpleName() {
        return m_name;
    }

    /**
     * Returns the annotation associated with the Name. The annotation was set by the client of the
     * Name using setAnnotation().
     */
    public Object getAnnotation() {
        return m_annotation;
    }

    /**
     * Sets the annotation associated with the Name. The annotation is an arbitary object chosed by
     * the client of the Name.
     */
    public void setAnnotation(Object o) {
        m_annotation = o;
    }

    public String getDeclClass() {
        return null;
    }

    public boolean hasDeclItf() {
        return false;
    }

    public String getDeclItf() {
        return null;
    }

    public String getDeclPackage() {
        return null;
    }

    public String getDeclItfPackage() {
        return null;
    }

    public String getUseClass(String currPackage) {
        return null;
    }

    public String getUseClass(boolean full) {
        return null;
    }

    public String getUseClass() {
        return null;
    }

    public String getUsePackage() {
        return null;
    }

    public String getUseItfPackage() {
        return null;
    }

    public boolean hasUseItf() {
        return false;
    }

    public String getUseItf() {
        return null;
    }

    public abstract boolean hasUseClass();

    // Used for renaming generated Java methods
    // to be distinguished with user subclass methods.
    // Vary the method names for the declared Java class,
    // to allow the method in the user subclass return different types
    // as the corresponding method in the declared class.
    public String renameJavaMethodPrefix() {
        return "_";

    }

    /**
     * Returns the complete name of the declared entity. The returned name includes the context name
     * and the name of the entity within the context.
     */
    public abstract String toString();

    /**
     * Returns a hash code for the Name. Implemented so that Names may be put in Hashtables.
     */
    public int hashCode() {
        // System.out.println("[Name] hashCode: " + this + "," + this.getClass() +"," + m_context +
        // "," + m_name.hashCode()); //D+
        return (m_context == null) ? m_name.hashCode() : m_context.hashCode() ^ m_name.hashCode();
    }

    /**
     * Returns true if and only if two Names are equal.
     */
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }

        Name n = (Name)obj;
        return (m_context == null) ? (n.m_context == null && m_name.equals(n.m_name)) : (m_context
            .equals(n.m_context) && m_name.equals(n.m_name));
    }

    protected Name() {
    }

}
