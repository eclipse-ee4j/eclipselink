/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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