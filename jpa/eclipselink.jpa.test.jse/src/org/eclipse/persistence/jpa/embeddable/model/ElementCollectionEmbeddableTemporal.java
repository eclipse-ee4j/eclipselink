/*******************************************************************************
 * Copyright (c) 2021 IBM Corporation. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     01/15/2021 - Will Dazey
 *       - 570378 : NullPointerException from Embedded Temporal
 ******************************************************************************/
package org.eclipse.persistence.jpa.embeddable.model;

import java.util.Date;

import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Embeddable
public class ElementCollectionEmbeddableTemporal {

    @Temporal(value = TemporalType.DATE)
    private Date temporalValue;

    public ElementCollectionEmbeddableTemporal() { }

    public ElementCollectionEmbeddableTemporal(Date temporalValue) {
        this.temporalValue = temporalValue;
    }
}
