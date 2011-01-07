/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
 *     02/25/2009-2.0 Guy Pelletier 
 *       - 265359: JPA 2.0 Element Collections - Metadata processing portions
 *     06/02/2009-2.0 Guy Pelletier 
 *       - 278768: JPA 2.0 Association Override Join Table
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.inherited;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;

@Entity(name="NOVICE_CONSUMER")
@DiscriminatorValue(value="NBC")
@AttributeOverrides({
    @AttributeOverride(name="accredidation.details", column=@Column(name="ACCR_DETAILS")),
    @AttributeOverride(name="records.date", column=@Column(name="REC_DATE")),
    @AttributeOverride(name="records.venue.name", column=@Column(name="VENUE")),
    @AttributeOverride(name="records.venue.history.yearBuilt", column=@Column(name="VENUE_YEAR_BUILT")),
    @AttributeOverride(name="records.venue.history.builder", column=@Column(name="VENUE_BUILDER"))
})
@AssociationOverrides({
    @AssociationOverride(name="records.location", joinColumns=@JoinColumn(name="LOC_ID", referencedColumnName="ID")),
    @AssociationOverride(name="committees", joinTable=@JoinTable(name="JPA_NBC_COMMITTEE",
        joinColumns=@JoinColumn(name="NBC_ID", referencedColumnName="ID"),
        inverseJoinColumns=@JoinColumn(name="COM_ID", referencedColumnName="ID"))),
    @AssociationOverride(name="accredidation.officials", joinColumns=@JoinColumn(name="FK_NBC_ID")),
    @AssociationOverride(name="accredidation.witnesses", joinTable=@JoinTable(name="NBC_ACCREDITATION_WITNESS",
        joinColumns=@JoinColumn(name="NBC_ID", referencedColumnName="ID"),
        inverseJoinColumns=@JoinColumn(name="WITNESSID", referencedColumnName="ID")))
})
public class NoviceBeerConsumer extends RatedBeerConsumer<Integer, Integer, Integer> {
    public NoviceBeerConsumer() {
        super();
    }
    
    public void addCommittee(Committee committee) {
        getCommittees().add(committee);
        committee.addNoviceBeerConsumer(this);
    }
}
