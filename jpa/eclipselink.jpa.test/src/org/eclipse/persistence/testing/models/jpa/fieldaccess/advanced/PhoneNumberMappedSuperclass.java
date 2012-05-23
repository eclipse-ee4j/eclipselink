/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     10/28/2010-2.2 Guy Pelletier 
 *       - 3223850: Primary key metadata issues
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.eclipse.persistence.annotations.PrimaryKey;

/**
 * Test for bug fix 323850. Since the inheriting entity PhoneNumber defines 
 * the the PrimaryKey metadata, this metadata should be ignored. If it is not
 * ignored, it will lead to test failures.
 */
@MappedSuperclass
@PrimaryKey(columns={@Column(name = "ERRONEOUS"), @Column(name = "SHOULD"), @Column(name = "BE"), @Column(name = "OVERRIDDEN")})
public class PhoneNumberMappedSuperclass {

}
