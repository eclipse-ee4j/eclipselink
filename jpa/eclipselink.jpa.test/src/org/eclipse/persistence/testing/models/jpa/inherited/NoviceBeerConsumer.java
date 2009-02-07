/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     06/20/2008-1.0 Guy Pelletier 
 *       - 232975: Failure when attribute type is generic
 *     01/28/2009-2.0 Guy Pelletier 
 *       - 248293: JPA 2.0 Element Collections (part 1)
 *     02/06/2009-2.0 Guy Pelletier 
 *       - 248293: JPA 2.0 Element Collections (part 2)
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.inherited;

import javax.persistence.AssociationOverride;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;

@Entity(name="NOVICE_CONSUMER")
@DiscriminatorValue(value="NBC")
@AttributeOverrides({
    @AttributeOverride(name="records.date", column=@Column(name="REC_DATE")),
    @AttributeOverride(name="records.description", column=@Column(name="DESCRIP")),
    @AttributeOverride(name="records.venue.name", column=@Column(name="VENUE")),
    @AttributeOverride(name="records.venue.history.yearBuilt", column=@Column(name="VENUE_YEAR_BUILT")),
    @AttributeOverride(name="records.venue.history.builder", column=@Column(name="VENUE_BUILDER"))
})
@AssociationOverride(name="records.location", joinColumns=@JoinColumn(name="LOC_ID", referencedColumnName="ID"))
public class NoviceBeerConsumer extends RatedBeerConsumer<Integer, Integer, Integer> {
    public NoviceBeerConsumer() {
        super();
    }
}
