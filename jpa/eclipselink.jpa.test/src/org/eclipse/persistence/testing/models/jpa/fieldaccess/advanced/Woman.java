/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced;

import javax.persistence.*;

import org.eclipse.persistence.annotations.CascadeOnDelete;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.GenerationType.*;

@Entity(name = "Woman")
@Table(name = "CMP3_FA_WOMAN")
public class Woman {
    @Id
    @GeneratedValue(strategy = SEQUENCE, generator = "FA_WOMAN_SEQ_GENERATOR")
    @SequenceGenerator(name = "FA_WOMAN_SEQ_GENERATOR", sequenceName = "WOMAN_SEQ")
    private Integer id;
    @OneToOne(mappedBy = "woman", cascade = ALL)
    @CascadeOnDelete
    private PartnerLink partnerLink;

    private String name;

    public Woman() {
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
