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
 *     tware - add for testing JPA 2.0 delimited identifiers
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.delimited;

import javax.persistence.*;

import org.eclipse.persistence.annotations.ExistenceChecking;

import static org.eclipse.persistence.annotations.ExistenceType.ASSUME_EXISTENCE;

/**
 * SmallProject class - empty subclass of Project
 */
@Entity
@Table(name="CMP3_DEL_PROJECT")
@DiscriminatorValue("S")
@ExistenceChecking(ASSUME_EXISTENCE)
public class SmallProject extends Project {
}
