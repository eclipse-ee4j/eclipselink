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
package org.eclipse.persistence.testing.models.jpa.xml.advanced;

import javax.persistence.*;

public class PartnerLinkPK {
    private int manId;
    private Integer womanId;

    public PartnerLinkPK() {}

    @Id
    public int getMan() {
        return manId;
    }

    @Id
    public Integer getWoman() {
        return womanId;
    }

    public void setMan(int manId) {
        this.manId = manId;
    }

    public void setWoman(Integer womanId) {
        this.womanId = womanId;
    }

    public boolean equals(Object anotherPartnerLinkPK) {
        if (anotherPartnerLinkPK.getClass() != PartnerLinkPK.class) {
            return false;
        }

        return getMan() ==(((PartnerLinkPK) anotherPartnerLinkPK).getMan()) &&
               getWoman().equals(((PartnerLinkPK) anotherPartnerLinkPK).getWoman());
    }
}
