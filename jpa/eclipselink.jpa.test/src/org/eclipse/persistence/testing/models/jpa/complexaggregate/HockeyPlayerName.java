/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     tware - added as part of test for bug 299774
package org.eclipse.persistence.testing.models.jpa.complexaggregate;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class HockeyPlayerName {

    private String firstName;
    private String lastName;

    @Column(name="FNAME", insertable=false, updatable=false)
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Column(name="LNAME", insertable=false, updatable=false)
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
