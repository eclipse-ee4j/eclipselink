/*
 * Copyright (c) 2018, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     03/14/2018-2.7 Will Dazey
//       - 500753: Synchronize initialization of InsertQuery
package org.eclipse.persistence.jpa.test.concurrency.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class UserTag {

    private String userKey;
    private String userValue;

    public UserTag(String userKey, String userValue) {
        this.userKey = userKey;
        this.userValue = userValue;
    }

    public UserTag() {
        this("","");
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public String getUserValue() {
        return userValue;
    }

    public void setUserValue(String userValue) {
        this.userValue = userValue;
    }
}
