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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.inheritance;

import org.eclipse.persistence.testing.models.inheritance.Animal;

public class Dog extends Animal {
    public Dog() {
        super();
    }

    public static Dog example1() {
        Dog example = new Dog();
        example.setId(1);
        example.setName("Dog1");
        return example;
    }

    public static Dog example2() {
        Dog example = new Dog();
        example.setName("Dog2");
        example.setId(2);
        return example;
    }

    public static Dog example3() {
        Dog example = new Dog();
        example.setName("Dog3");
        example.setId(3);
        return example;
    }

    public static Dog example4() {
        Dog example = new Dog();
        example.setName("Dog4");
        example.setId(4);
        return example;

    }

    public String getType() {
        return "d";
    }
}
