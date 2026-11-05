/*
 * Copyright (c) 2026 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.jpa.test.query.methodnative;

public final class MethodNativeResults {
    private MethodNativeResults() {
    }

    public record Summary(Integer id, String title) {
    }

    public record Text(String value) {
    }

    public record EntityValue(String title, MethodNativeBook book) {
    }

    public static final class Ordinary {
        public final Integer id;
        public final String title;

        public Ordinary(Integer id, String title) {
            this.id = id;
            this.title = title;
        }
    }
}
