/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     09/23/2009-2.0  mobrien
//       - 266912: JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)
package org.eclipse.persistence.testing.models.jpa.metamodel;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Use Case: IdClass identifiers declared across multiple mappedSuperclasses in an inheritance hierarchy.
 * Note: The following Entity inherits 4 of 4 of the Id fields declared across 2 mappedSuperclasses above as part of the IdClass MSIdClassPK.
 * The IdClass annotation can go on the first mappedSuperclass superclass or this entity but not on the root.
 * As long as resolution of all fields in the IdClass are available - the configuration is good.
 */
@Entity(name="MS_MS_EntityLeafMetamodel")
@Table(name="CMP3_MM_MSMSENTITY_LEAF")
public class MS_MS_Entity_Leaf extends MS_MS_Entity_Center {

    private String declaredLeafStringField;

    public String getDeclaredLeafStringField() {
        return declaredLeafStringField;
    }

    public void setDeclaredLeafStringField(String declaredLeafStringField) {
        this.declaredLeafStringField = declaredLeafStringField;
    }
}
