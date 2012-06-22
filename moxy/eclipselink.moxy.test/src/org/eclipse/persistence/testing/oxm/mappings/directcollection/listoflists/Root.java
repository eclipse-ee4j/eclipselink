/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     rbarkhouse - 2009-05-05 14:32:00 - initial implementation 
 ******************************************************************************/
package org.eclipse.persistence.testing.oxm.mappings.directcollection.listoflists;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class Root {

    public ArrayList<ArrayList<Double>> items = new ArrayList<ArrayList<Double>>();

    public ArrayList<ArrayList<Double>> getItems() {
        return this.items;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Root)) {
            return false;
        }

        Root otherRoot = (Root) obj;

        if (this.items.size() != otherRoot.items.size()) {
            return false;
        }

        return this.items.equals(otherRoot.items);
    }

}
