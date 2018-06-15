/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     02/08/2013-2.5 Chris Delahunt
//       - 374771 - JPA 2.1 TREAT support
package org.eclipse.persistence.testing.models.jpa.inheritance;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Chris Delahunt
 *
 */
@Entity
@Table(name="CMP3_TIRE")
@DiscriminatorValue("PassPerf")
public class PassengerPerformanceTireInfo extends PerformanceTireInfo {
    //might make sense to add type mapping for enum: all-season, summer, winter.
}
