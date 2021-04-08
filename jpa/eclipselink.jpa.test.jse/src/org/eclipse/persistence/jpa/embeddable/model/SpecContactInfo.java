/*
 * Copyright (c) 2021 IBM Corporation. All rights reserved.
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
//     04/09/2021 - Will Dazey
//       - 570702 : Using embeddable fields in query JOINs
package org.eclipse.persistence.jpa.embeddable.model;

import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.ManyToMany;

@Embeddable
public class SpecContactInfo {
    @Embedded
    private SpecAddress primaryAddress;

    @ElementCollection
    @CollectionTable(name="PREV_ADDRESSES")
    private List<SpecAddress> previousAddresses;

    @ManyToMany 
    private List<SpecPhone> phones;
}