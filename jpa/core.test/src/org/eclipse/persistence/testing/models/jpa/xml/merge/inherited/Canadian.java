/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.xml.merge.inherited;

import java.util.Date;
import javax.persistence.*;
import static javax.persistence.TemporalType.DATE;

// The table annotation is overidden in XML, remaining annotation is used
@Table(name="BIGBAD_CANADIAN")
public class Canadian extends Beer {
    public enum Flavor { LAGER, LIGHT, ICE, DRY }

    private Flavor flavor;
    private Date bornOnDate;
    
    public Canadian() {}
    
    public Date getBornOnDate() {
        return bornOnDate;
    }
    
    @Basic
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
