/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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
