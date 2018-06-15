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
package org.eclipse.persistence.tools.workbench.mappingsmodel.schema;

import java.util.ArrayList;
import java.util.ListIterator;

import org.apache.xerces.xs.XSObject;
import org.apache.xerces.xs.XSObjectList;


final class XercesTools
{
    static void schemaElement() {
    }

    /**
     * Get all the top level groups in the schema
     * @return an array of top level nodes
     */
    static void getSchemaNodeGroupSet() {
    }

    static boolean attributeNodeIsProhibited() {
        return false;
    }

    static String getElementNodeEquivClassRefNamespace() {
        return  "equivRefNamespace";
    }

    static ListIterator<XSObject> listIteratorFromXSObjectList(XSObjectList list) {
        ArrayList<XSObject> objects = new ArrayList<XSObject>(list.getLength());
        for (int i = 0; i < list.getLength(); i++) {
            if (list.item(i) != null) {
                objects.add(list.item(i));
            }
        }
        return objects.listIterator();
    }

}
