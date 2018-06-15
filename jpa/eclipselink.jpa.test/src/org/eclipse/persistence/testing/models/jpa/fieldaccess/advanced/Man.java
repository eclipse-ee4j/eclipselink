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
package org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced;

import java.io.Serializable;

import javax.persistence.*;

import org.eclipse.persistence.annotations.CascadeOnDelete;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.GenerationType.*;

@Entity(name = "Man")
@Table(name = "CMP3_FA_MAN")
public class Man implements Serializable {
    @Id
    @GeneratedValue(strategy = SEQUENCE, generator = "FA_MAN_SEQ_GENERATOR")
    @SequenceGenerator(name = "FA_MAN_SEQ_GENERATOR", sequenceName = "MAN_SEQ")
    private Integer id;
    @OneToOne(mappedBy = "man", cascade = ALL)
    @CascadeOnDelete
    private PartnerLink partnerLink;

    private String name;

    public Man() {
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public PartnerLink getPartnerLink() {
        return partnerLink;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPartnerLink(PartnerLink partnerLink) {
        this.partnerLink = partnerLink;
    }
}
