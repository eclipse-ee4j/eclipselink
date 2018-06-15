/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package jpql.query;

import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class LargeProject extends Project {

    @Basic
    private String category;

    @Basic
    @Temporal(TemporalType.DATE)
    private Date endDate;

    @OneToOne
    private Project parent;

    @Basic
    @Temporal(TemporalType.DATE)
    private Date startDate;

    public LargeProject() {
        super();
    }
}
