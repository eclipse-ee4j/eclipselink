/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
