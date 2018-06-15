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
package org.eclipse.persistence.tools.workbench.test.utility.classfile;

import java.util.Iterator;

/**
 *
 */
public interface ClassFileTestInterface extends java.util.Collection, java.lang.Comparable {
    String test = "test";
    int limit = 99;

    void foo();
    int bar(int[][] indexPairs);
    String baz(String[][] names);

    interface InnerInterface1 {
        void innerFoo();
    }

    class NullClassFileTestInterface extends java.util.AbstractCollection implements ClassFileTestInterface {
        public int compareTo(Object o) {
            return 0;
        }
        public void foo() {
            // do nothing
        }
        public String baz(String[][] names) {
            return null;
        }
        public Iterator iterator() {
            return null;
        }
        public int bar(int[][] indexPairs) {
            return 0;
        }
        public int size() {
            return 0;
        }
    }

}
