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
//     rbarkhouse - 2009-10-07 13:24:58 - initial implementation
package org.eclipse.persistence.testing.oxm.mappings.collectionreference.reuse;

public class Address {

    public int id;
    public String info;
    public String type;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Address) {
            Address addObj = (Address) obj;
            if (this.id != addObj.id) {
                return false;
            }
            if (!(this.info.equals(addObj.info))) {
                return false;
            }
            if (!(this.type.equals(addObj.type))) {
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

}
