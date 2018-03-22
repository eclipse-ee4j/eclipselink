/*******************************************************************************
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     03/19/2018-2.7.2 Lukas Jungmann
 *       - 413120: Nested Embeddable Null pointer 
 *       - 496836: NullPointerException on ObjectChangeSet.mergeObjectChanges
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.advanced.embeddable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Description {

    private String note;

    public Description() {
    }
    
    public Description(String note) {
        this.note = note;
    }

    @Column(name = "NOTE")
    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
    
    
}
