/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     04/04/2012-2.3.3 Guy Pelletier
//       - 362180: ConcurrentModificationException on predeploy for AttributeOverride
package org.eclipse.persistence.testing.models.jpa.inherited;

import javax.persistence.Embeddable;

@Embeddable
public class EmbeddedTimeCaption {
    public String caption;

    public EmbeddedTimeCaption() {}

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }
}
