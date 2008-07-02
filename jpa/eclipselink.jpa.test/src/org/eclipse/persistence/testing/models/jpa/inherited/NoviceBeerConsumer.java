/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.inherited;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity(name="NOVICE_CONSUMER")
@DiscriminatorValue(value="NBC")
public class NoviceBeerConsumer extends RatedBeerConsumer<Integer, Integer, Integer> {
    public NoviceBeerConsumer() {
        super();
    }
}
