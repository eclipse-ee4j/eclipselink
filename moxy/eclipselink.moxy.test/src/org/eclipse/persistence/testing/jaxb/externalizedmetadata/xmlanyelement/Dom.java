/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
// dmccann - February 16/2010 - 2.1 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlanyelement;

public class Dom {
    public String stuffStr;

    public boolean equals(Object obj) {
        Dom domObj;
        try {
            domObj = (Dom) obj;
        } catch (ClassCastException e) {
            return false;
        }
        return domObj.stuffStr.equals(this.stuffStr);
    }
}
