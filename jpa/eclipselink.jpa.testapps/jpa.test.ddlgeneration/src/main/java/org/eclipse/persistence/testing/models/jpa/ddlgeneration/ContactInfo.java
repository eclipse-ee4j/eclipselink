/*
 * Copyright (c) 2011, 2022 Oracle and/or its affiliates. All rights reserved.
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
//     06/14/2010-2.2 Karen Moore
//       - 264417: Table generation is incorrect for JoinTables in AssociationOverrides
//     07/19/2011-2.2.1 Guy Pelletier
//       - 338812: ManyToMany mapping in aggregate object violate integrity constraint on deletion
package org.eclipse.persistence.testing.models.jpa.ddlgeneration;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.FetchType.EAGER;

@Embeddable
public class ContactInfo {
    // Bi-directional M-M
    @ManyToMany(targetEntity=PhoneNumber.class, cascade=PERSIST, fetch=EAGER)
    @JoinTable(name="SHOULD_BE_OVERRIDEN_AND_NAME_TO_LONG_FOR_DATABASE_WILL_CAUSE_ERROR_NOT_GOOD_VERY_BAD_INDEED")
    public List<PhoneNumber> phoneNumbers;

    // Uni-directional M-M
    @ManyToMany(cascade=PERSIST, fetch=EAGER)
    @JoinTable(name="DDL_EMP_COMMENTS")
    public List<Comment<?>> comments;

    // Direct collection
    @ElementCollection
    @CollectionTable(name="DDL_EMP_UPDATES")
    public List<String> updates;

    public ContactInfo() {
        phoneNumbers = new ArrayList<>();
        comments = new ArrayList<>();
        updates = new ArrayList<>();
    }

    public void addComment(Comment<?> comment) {
        comments.add(comment);
    }

    public void addPhoneNumber(PhoneNumber phoneNumber) {
        phoneNumbers.add(phoneNumber);
    }

    public void addUpdate(String update) {
        updates.add(update);
    }

    public List<Comment<?>> getComments() {
        return comments;
    }

    public List<PhoneNumber> getPhoneNumbers() {
        return phoneNumbers;
    }

    public List<String> getUpdates() {
        return updates;
    }

    public void setComments(List<Comment<?>> comments) {
        this.comments = comments;
    }

    public void setPhoneNumbers(List<PhoneNumber> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public void setUpdates(List<String> updates) {
        this.updates = updates;
    }
}
