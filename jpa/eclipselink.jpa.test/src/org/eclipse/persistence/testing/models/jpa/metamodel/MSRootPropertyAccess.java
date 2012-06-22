/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     16/06/2010-2.2  mobrien - 316991: Attribute.getJavaMember() requires reflective getMethod call
 *       when only getMethodName is available on accessor for attributes of Embeddable types.
 *       http://wiki.eclipse.org/EclipseLink/Development/JPA_2.0/metamodel_api#DI_95:_20091017:_Attribute.getJavaMember.28.29_returns_null_for_a_BasicType_on_a_MappedSuperclass_because_of_an_uninitialized_accessor
 ******************************************************************************/ 
package org.eclipse.persistence.testing.models.jpa.metamodel;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;

@MappedSuperclass // concrete
@Access(AccessType.PROPERTY) // for 316991
public class MSRootPropertyAccess {

    private ArrayProcessor primarySuperComputer;

    @OneToOne // unidirectional
    public ArrayProcessor getPrimarySuperComputer() {
        return primarySuperComputer;
    }

    public void setPrimarySuperComputer(ArrayProcessor primarySuperComputer) {
        this.primarySuperComputer = primarySuperComputer;
    }

}
