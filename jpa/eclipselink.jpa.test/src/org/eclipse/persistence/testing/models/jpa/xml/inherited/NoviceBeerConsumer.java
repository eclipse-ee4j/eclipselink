/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
 *     06/02/2009-2.0 Guy Pelletier 
 *       - 278768: JPA 2.0 Association Override Join Table
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.xml.inherited;

public class NoviceBeerConsumer extends RatedBeerConsumer<Integer, Integer, Integer> {
    public NoviceBeerConsumer() {
        super();
    }
    
    public void addCommittee(Committee committee) {
        getCommittees().add(committee);
        committee.addNoviceBeerConsumer(this);
    }
}
