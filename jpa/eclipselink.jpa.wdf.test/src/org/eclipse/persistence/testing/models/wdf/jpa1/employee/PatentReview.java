/*******************************************************************************
 * Copyright (c) 2005, 2009 SAP. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     SAP - initial API and implementation
 ******************************************************************************/

package org.eclipse.persistence.testing.models.wdf.jpa1.employee;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToOne;

@SuppressWarnings("serial")
@Entity
public class PatentReview extends Review {

    @OneToOne
    @JoinColumns( { @JoinColumn(name = "PATENT_NAME", referencedColumnName = "PAT_NAME"),
            @JoinColumn(name = "PATENT_YEAR", referencedColumnName = "PAT_YEAR") })
    protected Patent patent;

    public Patent getPatent() {
        return patent;
    }

    public void setPatent(Patent patent) {
        this.patent = patent;
    }

}
