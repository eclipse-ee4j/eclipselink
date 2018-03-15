/*******************************************************************************
 * Copyright (c) 2018 IBM Corporation. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     03/14/2018-2.7 Will Dazey
 *       - 500753: Synchronize initialization of InsertQuery
 ******************************************************************************/
package org.eclipse.persistence.jpa.test.concurrency.model;

import javax.persistence.Embeddable;

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
