/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
import java.util.List;

public class Union implements SimpleDerivation {
    private List<String> memberTypes;
    private List<SimpleType> simpleTypes;

    public Union() {
    }

    public void setMemberTypes(List<String> memberTypes) {
        this.memberTypes = memberTypes;
    }

    public List<String> getMemberTypes() {
        return memberTypes;
    }

    public void setSimpleTypes(List<SimpleType> simpleTypes) {
        this.simpleTypes = simpleTypes;
    }

    public List<SimpleType> getSimpleTypes() {
        return simpleTypes;
    }

    public List<String> getAllMemberTypes() {
        List<String> allTypes = new ArrayList<>(getMemberTypes());
        for (int i = 0; i < getSimpleTypes().size(); i++) {
            SimpleType next = getSimpleTypes().get(i);
            if (next.getRestriction() != null) {
                allTypes.add(next.getRestriction().getBaseType());
            }
        }
        return allTypes;
    }
}
