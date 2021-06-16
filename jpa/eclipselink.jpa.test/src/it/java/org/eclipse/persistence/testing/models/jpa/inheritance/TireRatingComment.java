/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.jpa.inheritance;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;
import static jakarta.persistence.GenerationType.TABLE;

@Entity
@Table(name="CMP3_TIRE_RATING_COMMENT")
public class TireRatingComment  {
    private Integer id;
    private String comment;

    public TireRatingComment() {}

    public TireRatingComment(String comment) {
        setComment(comment);
    }

    @Column(name="DESCRIP")
    public String getComment() {
        return comment;
    }

    @Id
    @GeneratedValue(strategy=TABLE, generator="TIRE_RATING_COMMENT_TABLE_GENERATOR")
    @TableGenerator(
        name="TIRE_RATING_COMMENT_TABLE_GENERATOR",
        table="CMP3_INHERITANCE_SEQ",
        pkColumnName="SEQ_NAME",
        valueColumnName="SEQ_COUNT",
        pkColumnValue="TIRE_RATING_COMMENT_SEQ")
    @Column(name="ID")
    public Integer getId() {
        return id;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
