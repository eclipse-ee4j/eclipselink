/*******************************************************************************
 * Copyright (c) 2012  Arron Ferguson. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     05/17/2012-2.3.3 Arron Ferguson  
 *       - 379829: NPE Thrown with OneToOne Relationship
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.ddlgeneration;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;


@Entity
@Table(name = "address2")
public class Address2 implements Serializable {

    @Id
    private Long id;
    
    @MapsId
    @OneToOne(mappedBy = "address")
    private Sponsor sponsor;

}
