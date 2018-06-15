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

/**
 * INTERNAL
 * <p><b>Purpose</b>: this is the superclass of the SELECT, FROM, WHERE and ORDER BY nodes
 * <p><b>Responsibilities</b>:<ul>
 * <li> Maintain a reference to the ParseTreeContext
 * <li>
 * User: jdriscol
 * Date: Nov 13, 2002
 * Time: 10:58:20 AM
 */
package org.eclipse.persistence.internal.jpa.parsing;

public class MajorNode extends Node {
    private ParseTreeContext context;

    // Accessors
    public ParseTreeContext getContext() {
        return context;
    }

    public void setContext(ParseTreeContext context) {
        this.context = context;
    }
}
