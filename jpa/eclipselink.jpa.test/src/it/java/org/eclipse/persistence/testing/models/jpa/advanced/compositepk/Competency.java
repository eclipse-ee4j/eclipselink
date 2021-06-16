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
//     05/31/2010-2.1 Guy Pelletier
//       - 314941: multiple joinColumns without referenced column names defined, no error
package org.eclipse.persistence.testing.models.jpa.advanced.compositepk;

import jakarta.persistence.Access;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import static jakarta.persistence.AccessType.FIELD;

@Embeddable
@Access(FIELD)
public class Competency {
    @Column(name="DESCRIP")
    public String description;

    @Column(name="RATING")
    public Integer rating;

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the rating
     */
    public Integer getRating() {
        return rating;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @param rating the rating to set
     */
    public void setRating(Integer rating) {
        this.rating = rating;
    }


}
