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
package org.eclipse.persistence.testing.oxm.xmlbinder.anymappingtests;

/**
 *  @version $Header: Child.java 07-apr-2005.15:35:47 mmacivor Exp $
 *  @author  mmacivor
 *  @since   release specific (what release of product did this appear in)
 */
public class AnyCollectionChild {
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String c) {
        content = c;
    }

    public boolean equals(Object object) {
        if (object instanceof AnyCollectionChild) {
            if ((content == null) && (((AnyCollectionChild)object).getContent() == null)) {
                return true;
            }
            if ((content == null) && (((AnyCollectionChild)object).getContent() != null)) {
                return false;
            }
            if ((content != null) && (((AnyCollectionChild)object).getContent() == null)) {
                return false;
            }
            return this.content.equals(((AnyCollectionChild)object).getContent());
        }
        return false;
    }

    public String toString() {
        return "Child: " + content + "\n";
    }
}
