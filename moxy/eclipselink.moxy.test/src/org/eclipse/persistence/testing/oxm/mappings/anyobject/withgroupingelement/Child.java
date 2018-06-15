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
package org.eclipse.persistence.testing.oxm.mappings.anyobject.withgroupingelement;


/**
 *  @version $Header: Child.java 17-jul-2006.08:46:57 dmahar Exp $
 *  @author  mmacivor
 *  @since   release specific (what release of product did this appear in)
 */
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
            return content.equals(((Child)object).getContent());
        }
        return false;
    }

    public String toString() {
        String string = "Child(content=";
        string += content;
        string += ")";
        return string;
    }
}
