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
import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

/**
 *  @version $Header: Root.java 17-jul-2006.11:19:10 dmahar Exp $
 *  @author  mmacivor
 *  @since   release specific (what release of product did this appear in)
 */

public class AnyObjectRoot {
    private Object any;

    public Object getAny() {
        return any;
    }
    public void setAny(Object a) {
        any = a;
    }
    public boolean equals(Object object) {
        if(object instanceof AnyObjectRoot) {
            if(any == null && ((AnyObjectRoot)object).getAny() == null) {
                return true;
            } else if(any == null && ((AnyObjectRoot)object).getAny() != null) {
                return false;
            } else {
                Object value1 = any;
                Object value2 = ((AnyObjectRoot)object).getAny();
                if ((value1 instanceof XMLRoot) && (value2 instanceof XMLRoot)) {
                    XMLMappingTestCases.compareXMLRootObjects((XMLRoot)value1, (XMLRoot)value2);
                    return true;
                } else {
                    return this.any.equals(((AnyObjectRoot)object).getAny());
                }
            }
        }
        return false;
    }

}
