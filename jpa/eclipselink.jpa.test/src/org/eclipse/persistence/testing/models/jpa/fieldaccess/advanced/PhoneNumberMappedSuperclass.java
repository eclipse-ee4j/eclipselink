/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     10/28/2010-2.2 Guy Pelletier
//       - 3223850: Primary key metadata issues
package org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced;

import java.io.Serializable;

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
public class PhoneNumberMappedSuperclass implements Serializable {

}
