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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.jpa.inheritance;

import javax.persistence.Embeddable;
import javax.persistence.OneToOne;
import javax.persistence.JoinColumn;

import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.FetchType.LAZY;

@Embeddable
public class TireRating {
    protected String rating;
    protected TireRatingComment comment;

    public TireRating() {}

    @OneToOne(cascade=PERSIST, fetch=LAZY)
    @JoinColumn(name="COMMENT_ID")
    public TireRatingComment getComment() {
        return comment;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating =rating;
    }

    public void setComment(TireRatingComment comment) {
        this.comment = comment;
    }
}
