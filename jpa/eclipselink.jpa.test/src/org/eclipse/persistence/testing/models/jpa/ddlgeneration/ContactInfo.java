/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     06/14/2010-2.2 Karen Moore 
 *       - 264417: Table generation is incorrect for JoinTables in AssociationOverrides
 *     07/19/2011-2.2.1 Guy Pelletier 
 *       - 338812: ManyToMany mapping in aggregate object violate integrity constraint on deletion
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.ddlgeneration;

import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.FetchType.EAGER;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

@Embeddable
public class ContactInfo {
    // Bi-directional M-M
    @ManyToMany(targetEntity=PhoneNumber.class, cascade=PERSIST, fetch=EAGER)
    @JoinTable(name="SHOULD_BE_OVERRIDEN_AND_NAME_TO_LONG_FOR_DATABASE_WILL_CAUSE_ERROR_NOT_GOOD_VERY_BAD_INDEED")
    public List phoneNumbers;
    
    // Uni-directional M-M
    @ManyToMany(cascade=PERSIST, fetch=EAGER)
    @JoinTable(name="DDL_EMP_COMMENTS")
    public List<Comment> comments;
    
    // Direct collection
    @ElementCollection
    @CollectionTable(name="DDL_EMP_UPDATES")
    public List<String> updates;

    public ContactInfo() {
        phoneNumbers = new ArrayList<PhoneNumber>();
        comments = new ArrayList<Comment>();
        updates = new ArrayList<String>();
    }
    
    public void addComment(Comment comment) {
        comments.add(comment);
    }
    
    public void addPhoneNumber(PhoneNumber phoneNumber) {
        phoneNumbers.add(phoneNumber);
    }
    
    public void addUpdate(String update) {
        updates.add(update);
    }
    
    public List<Comment> getComments() {
        return comments;
    }
    
    public List getPhoneNumbers() {
        return phoneNumbers;
    }
    
    public List<String> getUpdates() {
        return updates;
    }

    public void setComments(List comments) {
        this.comments = comments;
    }
    
    public void setPhoneNumbers(List phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }
    
    public void setUpdates(List<String> updates) {
        this.updates = updates;
    }
}
