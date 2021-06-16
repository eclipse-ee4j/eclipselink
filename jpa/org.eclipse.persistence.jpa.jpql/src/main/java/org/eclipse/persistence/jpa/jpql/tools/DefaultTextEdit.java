/*
 * Copyright (c) 2012, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.jpql.tools;

/**
 * The default implementation of a {@link TextEdit}, which contains the location of the change within
 * the JPQL query (offset) and the old and new values.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public class DefaultTextEdit implements TextEdit {

    /**
     * The new value that should replace the old value.
     */
    private String newValue;

    /**
     * The location of the old value within the text.
     */
    private int offset;

    /**
     * The value that was found within the text that should be replaced by the new value.
     */
    private String oldValue;

    /**
     * Creates a new <code>DefaultTextEdit</code>.
     *
     * @param offset The location of the old value within the text
     * @param oldValue the value that was found within the text that should be replaced by the new value
     * @param newValue The new value that should replace the old value
     */
    public DefaultTextEdit(int offset, String oldValue, String newValue) {
        super();
        this.offset   = offset;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getLength() {
        return oldValue.length();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getNewValue() {
        return newValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getOffset() {
        return offset;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getOldValue() {
        return oldValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append(offset);
        sb.append("] ");
        sb.append(oldValue);
        sb.append(" -> ");
        sb.append(newValue);
        return sb.toString();
    }
}
