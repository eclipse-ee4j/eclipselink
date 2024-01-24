/*
 * Copyright (c) 2011, 2024 Oracle and/or its affiliates. All rights reserved.
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
//  - rbarkhouse - 27 February - 2.3.3 - Initial implementation
package org.eclipse.persistence.testing.jaxb.sun.idresolver;

import jakarta.xml.bind.annotation.XmlIDREF;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
class MelonRef {

    @XmlIDREF
    Melon ref;

    @Override
    public String toString() {
        if (ref == null) return "null";
        return "ref" + ref;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof MelonRef)) {
            return false;
        }
        MelonRef a = (MelonRef) obj;

        return this.ref.equals(a.ref);
    }

}
