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
//  - rbarkhouse - 27 February - 2.3.3 - Initial implementation
package org.eclipse.persistence.testing.jaxb.idresolver;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlIDREF;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.oxm.annotations.XmlJoinNode;
import org.eclipse.persistence.oxm.annotations.XmlJoinNodes;

@XmlRootElement
class MelonRef {

    @XmlIDREF
    Melon ref;

    @Override
    public String toString() {
        if (ref == null) return "null";
        return "ref" + ref.toString();
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
