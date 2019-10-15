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
package org.eclipse.persistence.testing.models.inheritance;

import org.eclipse.persistence.testing.models.inheritance.Animal;

public class Cat extends Animal {
    public Cat() {
        super();
    }

    public static Cat example1() {
        Cat example = new Cat();
        example.setId(1);
        example.setName("Cat1");
        return example;
    }

    public static Cat example2() {
        Cat example = new Cat();
        example.setId(2);
        example.setName("Cat2");
        return example;

    }

    public static Cat example3() {
        Cat example = new Cat();
        example.setId(3);
        example.setName("Cat3");
        return example;

    }

    public static Cat example4() {
        Cat example = new Cat();
        example.setName("Cat4");
        example.setId(4);
        return example;

    }

    public String getType() {
        return "c";
    }
}
