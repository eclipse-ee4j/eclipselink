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
//     Denise Smith  February 9, 2009 - 2.1
package org.eclipse.persistence.testing.jaxb.typemappinginfo.simple;

public class Person {

    public Person(){

    }

    public boolean equals(Object obj){
        return obj instanceof Person;
    }
}
