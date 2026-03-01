/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.models.mapping;

import java.io.Serializable;
import java.util.Vector;

public class Keyboard implements Serializable {
    public double id;
    public String brand;
    public Vector keys;

    public Keyboard() {
        this.keys = new Vector();
        this.brand = "Generic";
    }

    public static Keyboard example1() {
        Keyboard example = new Keyboard();
        example.id = 12345;
        example.brand = "Microsoft";
        example.keys.add(new Key("A", example));
        example.keys.add(new Key("B", example));
        example.keys.add(new Key("C", example));
        return example;
    }

    public static Keyboard example2() {
        Keyboard example = new Keyboard();
        example.id = 6789;
        example.keys.add(new Key("D", example));
        example.keys.add(new Key("E", example));
        example.keys.add(new Key("F", example));
        return example;
    }
}
