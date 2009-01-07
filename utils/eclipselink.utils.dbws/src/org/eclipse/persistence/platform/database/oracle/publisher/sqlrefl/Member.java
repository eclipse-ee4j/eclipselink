/* 1998 (c) Oracle Corporation */

package org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl;

/**
 * Member is an interface that reflects identifying information about a single member (a field or a
 * method) or a constructor.
 */
public class Member {

    /**
     * Construct a Member
     */
    public Member(String name, int modifiers) {
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

    String m_name;
    int m_modifiers;
}
