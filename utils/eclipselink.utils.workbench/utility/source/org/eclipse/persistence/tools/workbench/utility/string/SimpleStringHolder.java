/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.utility.string;

/**
 * Straightforward implementation of the StringHolder interface.
 */
public class SimpleStringHolder extends AbstractStringHolder {
    protected final String string;

    public SimpleStringHolder(String string) {
        super();
        this.string = this.buildString(string);
    }

    /**
     * Allow subclasses to manipulate the string before it is stored
     * (e.g. convert it to lowercase).
     */
    protected String buildString(String s) {
        return s;
    }

    /**
     * @see StringHolder#getString()
     */
    public String getString() {
        return this.string;
    }

}
