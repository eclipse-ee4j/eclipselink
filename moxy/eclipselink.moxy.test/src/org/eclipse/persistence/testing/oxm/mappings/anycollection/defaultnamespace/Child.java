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
//     bdoughan - Feb 27/2009 - 1.1 - Initial implementation
package org.eclipse.persistence.testing.oxm.mappings.anycollection.defaultnamespace;

public class Child {
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String c) {
        content = c;
    }

    public boolean equals(Object object) {
        if (object instanceof Child) {
            if ((content == null) && (((Child)object).getContent() == null)) {
                return true;
            }
            return this.content.equals(((Child)object).getContent());
        }
        return false;
    }

    public String toString() {
        return "Child: " + content + "\n";
    }
}
