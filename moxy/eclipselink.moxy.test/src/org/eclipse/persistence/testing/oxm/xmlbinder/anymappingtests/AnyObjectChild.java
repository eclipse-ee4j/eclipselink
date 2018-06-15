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
package org.eclipse.persistence.testing.oxm.xmlbinder.anymappingtests;
/**
 *  @version $Header: Child.java 07-apr-2005.15:35:48 mmacivor Exp $
 *  @author  mmacivor
 *  @since   release specific (what release of product did this appear in)
 */

public class AnyObjectChild {
    private String content;

    public String getContent() {
        return content;
    }
    public void setContent(String c) {
        content = c;
    }

    public boolean equals(Object object) {
        if (object instanceof AnyObjectChild) {
            if ((content == null) && (((AnyObjectChild)object).getContent() == null)) {
                return true;
            }
            return content.equals(((AnyObjectChild)object).getContent());
        }
        return false;
    }

}
