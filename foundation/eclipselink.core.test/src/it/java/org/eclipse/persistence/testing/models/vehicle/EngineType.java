/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Vikram Bhatia - initial API and implementation
package org.eclipse.persistence.testing.models.vehicle;

public class EngineType implements java.io.Serializable {
    public Integer id;
    public String type;

    public void setId(Integer aid) {
        id = aid;
    }

    public void setType(String aType) {
        type = aType;
    }

    public Integer getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public static EngineType example1() {
        EngineType example = new EngineType();
        example.setId(new Integer(1));
        example.setType("Steel");
        return example;
    }

    public static EngineType example2() {
        EngineType example = new EngineType();
        example.setId(new Integer(2));
        example.setType("AlumSteel");
        return example;
    }
}
