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
//     06/03/2011-2.3.1 Guy Pelletier
//       - 347563: transient field/property in embeddable entity
package org.eclipse.persistence.testing.models.jpa.xml.inherited;

import javax.persistence.Embeddable;

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
