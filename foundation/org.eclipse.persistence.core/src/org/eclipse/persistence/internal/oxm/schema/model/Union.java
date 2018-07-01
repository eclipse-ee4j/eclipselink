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
package org.eclipse.persistence.internal.oxm.schema.model;

import java.util.ArrayList;

public class Union implements SimpleDerivation {
    private java.util.List memberTypes;
    private java.util.List simpleTypes;

    public Union() {
    }

    public void setMemberTypes(java.util.List memberTypes) {
        this.memberTypes = memberTypes;
    }

    public java.util.List getMemberTypes() {
        return memberTypes;
    }

    public void setSimpleTypes(java.util.List simpleTypes) {
        this.simpleTypes = simpleTypes;
    }

    public java.util.List getSimpleTypes() {
        return simpleTypes;
    }

    public java.util.List getAllMemberTypes() {
        ArrayList allTypes = new ArrayList();
        allTypes.addAll(getMemberTypes());
        for (int i = 0; i < getSimpleTypes().size(); i++) {
            SimpleType next = (SimpleType)getSimpleTypes().get(i);
            if (next.getRestriction() != null) {
                allTypes.add(next.getRestriction().getBaseType());
            }
        }
        return allTypes;
    }
}
