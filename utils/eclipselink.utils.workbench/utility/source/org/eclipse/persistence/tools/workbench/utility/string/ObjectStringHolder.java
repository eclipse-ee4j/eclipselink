/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.utility.string;

/**
 * This implementation of StringHolder associates an object with the string.
 */
public class ObjectStringHolder extends AbstractStringHolder {
    /** The object associated with the string holder's string. */
    protected final Object object;

    /** The string associated with the string holder's object. */
    protected final String string;


    /**
     * Use the default string converter, which simply converts an object to
     * a string by calling #toString().
     */
    public ObjectStringHolder(Object object) {
        this(object, StringConverter.DEFAULT_INSTANCE);
    }

    /**
     * Use the specified string converter to convert the specified object
     * to a string.
     */
    public ObjectStringHolder(Object object, StringConverter stringConverter) {
        super();
        this.object = object;
        this.string = stringConverter.convertToString(object);
    }

    /**
     * @see StringHolder#getString()
     */
    public final String getString() {
        return this.string;
    }

    /**
     * Return the object associated with the string holder's string.
     */
    public final Object getObject() {
        return this.object;
    }

}
