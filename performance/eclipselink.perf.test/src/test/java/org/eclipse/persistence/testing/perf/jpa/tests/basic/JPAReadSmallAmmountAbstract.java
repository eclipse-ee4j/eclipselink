/*
 * Copyright (c) 2024 Oracle and/or its affiliates. All rights reserved.
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
//              Oracle - initial implementation
package org.eclipse.persistence.testing.perf.jpa.tests.basic;

/**
 * Benchmarks for JPA reading data (small amount - up 20 rows per each request em.find()).
 *
 * @author Oracle
 */
public abstract class JPAReadSmallAmmountAbstract extends JPAReadAbstract {

    public int getMasterSize() {
        return 10;
    }

    public int getDetailSize() {
        return 10;
    }
}
