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
package org.eclipse.persistence.testing.models.mapping;

import java.io.*;
import java.util.*;

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
        example.keys.addElement(new Key("A", example));
        example.keys.addElement(new Key("B", example));
        example.keys.addElement(new Key("C", example));
        return example;
    }

    public static Keyboard example2() {
        Keyboard example = new Keyboard();
        example.id = 6789;
        example.keys.addElement(new Key("D", example));
        example.keys.addElement(new Key("E", example));
        example.keys.addElement(new Key("F", example));
        return example;
    }
}
