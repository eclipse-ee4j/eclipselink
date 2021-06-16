/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     06/03/2011-2.3.1 Guy Pelletier
//       - 347563: transient field/property in embeddable entity
package org.eclipse.persistence.testing.models.jpa.xml.inherited;

import jakarta.persistence.Embeddable;

public class RedStripe {
    private Double alcoholContent;
    // Marked as transient in XML (to expose bug 347563)
    private String transientString;

    public RedStripe() {}

    public RedStripe(Double content) {
        this.alcoholContent = content;
    }

    public Double getAlcoholContent() {
        return alcoholContent;
    }

    public String getTransientString() {
        return transientString;
    }

    public void setAlcoholContent(Double alcoholContent) {
        this.alcoholContent = alcoholContent;
    }

    public void setTransientString(String transientString) {
        this.transientString = transientString;
    }
}
