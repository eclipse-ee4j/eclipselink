/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     03/29/2010-2.1 Guy Pelletier 
 *       - 267217: Add Named Access Type to EclipseLink-ORM
 *     04/09/2010-2.1 Guy Pelletier 
 *       - 307050: Add defaults for access methods of a VIRTUAL access type
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.xml.advanced;

import static org.eclipse.persistence.annotations.ChangeTrackingType.DEFERRED;

import org.eclipse.persistence.annotations.ChangeTracking;

/**
 * This class is used to test the extended orm named-access setting.
 * 
 * It is mapped as an entity in the following resource file:
 * 
 *  resource/eclipselinkorm/eclipselink-xml-extended-model/eclipselink-orm.xml
 *  
 * @author gpelleti
 */
@ChangeTracking(DEFERRED)
public class ShovelOwner extends ShovelPerson {
    public ShovelOwner() {}
}
