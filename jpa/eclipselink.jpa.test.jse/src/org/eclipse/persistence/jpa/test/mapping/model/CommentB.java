/*******************************************************************************
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     09/12/2018 - Will Dazey
 *       - 391279: Add support for Unidirectional OneToMany mappings with non-nullable values
 ******************************************************************************/  
package org.eclipse.persistence.jpa.test.mapping.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class CommentB {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public CommentB() {
    }
}
