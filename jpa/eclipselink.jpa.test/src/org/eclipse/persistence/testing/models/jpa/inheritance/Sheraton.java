/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     10/18/2010-2.2 Guy Pelletier
//       - 326973: TABLE_PER_CLASS with EmbeddedId results in DescriptorException EclipseLink-74
//                 "The primary key fields are not set for this descriptor"
package org.eclipse.persistence.testing.models.jpa.inheritance;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Model class added to test metadata processing error for bug 326973.
 * Model not added to the InheritanceTableCreator.
 */
@Entity
@Table(name="TPC_SHERATON")
public class Sheraton extends Hotel {

}
