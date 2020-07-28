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
package org.eclipse.persistence.internal.jpa.deployment.xml.parser;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.internal.helper.Helper;

public class XMLException extends RuntimeException {
    private List m_nestedExceptions;

    public XMLException() {
        super();
        m_nestedExceptions = new ArrayList();
    }

    public void addNestedException(Exception nestedException) {
        m_nestedExceptions.add(nestedException);
    }

    public String getMessage() {
        StringBuffer buffer = new StringBuffer();
        Exception nestedException;
        for (int x=0; x<m_nestedExceptions.size(); x++) {
            nestedException = (Exception) m_nestedExceptions.get(x);
            buffer.append(Helper.cr());
            buffer.append('(');
            buffer.append(x + 1);
            buffer.append(". ");
            buffer.append(nestedException.getMessage());
            buffer.append(')');
        }
        return buffer.toString();
    }

    public String toString() { return getMessage(); }
}
