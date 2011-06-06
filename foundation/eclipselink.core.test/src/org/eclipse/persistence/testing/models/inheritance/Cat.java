/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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
