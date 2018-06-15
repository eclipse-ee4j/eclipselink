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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.test.models.crimescene;

public class Weapon extends PieceOfEvidence {
    private boolean usedInCrime;
/**
 * Weapon constructor comment.
 */
public Weapon() {
    super();
}
/**
 * @author Christopher Garrett
 * @since TopLink for Java Course 1.1
 * @return boolean
 */
public boolean isUsedInCrime() {
    return this.usedInCrime;
}
/**
 * @author Christopher Garrett
 * @since TopLink for Java Course 1.1
 * @param newValue boolean
 */
public void setUsedInCrime(boolean newValue) {
    this.usedInCrime = newValue;
}
}
