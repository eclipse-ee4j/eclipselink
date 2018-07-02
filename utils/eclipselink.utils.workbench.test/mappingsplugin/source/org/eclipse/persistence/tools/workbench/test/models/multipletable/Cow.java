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
package org.eclipse.persistence.tools.workbench.test.models.multipletable;


/**
 * A cow object uses mutliple table foreign key.
 *
 * @auther Guy Pelletier
 * @version 1.0
 * @date June 17, 2005
 */
public class Cow {
    protected int cowId;
    protected int calfCount;
    protected int calfCountId;
    protected String name;

    public Cow() {
    }

    public int getCalfCount() {
        return this.calfCount;
    }

    public int getCalfCountId() {
        return calfCountId;
    }

    public int getCowId() {
        return this.cowId;
    }

    public String getName() {
        return this.name;
    }

    public void setCalfCount(int calfCount) {
        this.calfCount = calfCount;
    }

    public void setCowId(int cowId) {
        this.cowId = cowId;
    }

    public void setName(String name) {
        this.name = name;
    }
}
