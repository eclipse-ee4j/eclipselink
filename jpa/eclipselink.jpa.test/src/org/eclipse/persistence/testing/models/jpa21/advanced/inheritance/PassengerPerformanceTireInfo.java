/*
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019 IBM Corporation. All rights reserved.
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
//     02/08/2013-2.5 Chris Delahunt
//       - 374771 - JPA 2.1 TREAT support
package org.eclipse.persistence.testing.models.jpa21.advanced.inheritance;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Chris Delahunt
 *
 */
@Entity
@Table(name="JPA21_TIRE")
@DiscriminatorValue("PassPerf")
public class PassengerPerformanceTireInfo extends PerformanceTireInfo {
    //might make sense to add type mapping for enum: all-season, summer, winter.
}
