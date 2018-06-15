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
//     09/23/2008-1.1 Guy Pelletier
//       - 241651: JPA 2.0 Access Type support
//     03/27/2009-2.0 Guy Pelletier
//       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
package org.eclipse.persistence.testing.models.jpa.xml.merge.inherited;

import java.util.Date;
import javax.persistence.*;

/**
 * This class is mapped in:
 * resource/eclipselink-ddl-generation-model/merge-inherited-beers.xml
 */
@Table(name="BIGBAD_CANADIAN")
public class Canadian extends Beer {
    public enum Flavor { LAGER, LIGHT, ICE, DRY }

    @Basic
    private Flavor flavor;

    @Basic
    private Date bornOnDate;

    public Canadian() {}

    public Date getBornOnDate() {
        return bornOnDate;
    }

    public Flavor getFlavor() {
        return flavor;
    }

    public void setBornOnDate(Date bornOnDate) {
        this.bornOnDate = bornOnDate;
    }

    public void setFlavor(Flavor flavor) {
        this.flavor = flavor;
    }
}
