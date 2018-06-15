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
//     tware - add for testing JPA 2.0 delimited identifiers
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
