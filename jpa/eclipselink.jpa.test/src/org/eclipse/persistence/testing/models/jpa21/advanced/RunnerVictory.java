/*******************************************************************************
 * Copyright (c) 1998, 2014 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     11/05/2014-2.6 Tomas Kraus
 *       - 449818: Initial API and implementation.
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa21.advanced;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Victory of an Runner in a competition.<br/>
 * Embedded class to verify {@code @Convert} annotation with {@code @ElementCollection} mapping.
 * @author Tomas Kraus
 */
@Embeddable
public class RunnerVictory {

    /** Primary key. */
    private int id;

    /** Competition name. */
    private String competition;

    /** Victory date. */
    @Column(name="VDATE")
    private Date date;

    /**
     * Construct an instance of this class with no attribute set.
     */
    public RunnerVictory() {
    }

    /**
     * Constructs an instance of this class with all attributes set.
     * @param id          Primary key.
     * @param competition Competition name.
     * @param date        Victory date.
     */
    public RunnerVictory(final int id, final String competition, final Date date) {
        this.id = id;
        this.competition = competition;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getCompetition() {
        return competition;
    }

    public void setCompetition(final String competition) {
        this.competition = competition;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(final Date date) {
        this.date = date;
    }

}
