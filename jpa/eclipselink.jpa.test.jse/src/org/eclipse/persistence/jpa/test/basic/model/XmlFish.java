/*******************************************************************************
 * Copyright (c) 2014, 2015  IBM Corporation. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     11/04/2014 - Rick Curtis
 *       - 450010 : Add java se test bucket
 ******************************************************************************/
package org.eclipse.persistence.jpa.test.basic.model;

public class XmlFish {
    int id;
    int version;
    String name;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getId() {
        return id;
    }
    public int getVersion() {
        return version;
    }



}
