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
package org.eclipse.persistence.tools.workbench.test.models.crimescene;

import java.util.Vector;

public class Firearm extends Weapon {
    private String caliber;
    private String type;
    private byte[] byteArray;

/**
 * Firearm constructor comment.
 */
public Firearm() {
    super();
}
/**
 * Return this weapon's caliber
 * @author Christopher Garrett
 * @since TopLink for Java Course 1.1
 * @return java.lang.String
 */
public String getCaliber() {
    return caliber;
}
/**
 * Return the type of this weapon
 * @author Christopher Garrett
 * @since TopLink for Java Course 1.1
 * @return java.lang.String
 */
public String getType() {
    return type;
}
/**
 * Set the caliber of this weapon
 * @author Christopher Garrett
 * @since TopLink for Java Course 1.1
 * @param newValue java.lang.String
 */
public void setCaliber(String newValue) {
    this.caliber = newValue;
}
/**
 * Set the type of this weapon
 * @author Christopher Garrett
 * @since TopLink for Java Course 1.1
 * @param newValue java.lang.String
 */
public void setType(String newValue) {
    this.type = newValue;
}

public byte[] getByteArray() {
    return this.byteArray;
}

public void setByteArray(byte[] byteArray) {
    this.byteArray = byteArray;
}

    public static Vector types() {
        Vector v = new Vector();
        v.addElement("Pistol");
        v.addElement("Rifle");
        v.addElement("Shotgun");
        v.addElement("Other");
        return v;
    }
}
