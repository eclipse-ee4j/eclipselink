/**
 * ****************************************************************************
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * <p/>
 * Contributors:
 *      Marcel Valovy - initial API and implementation
 * ****************************************************************************
 */
package org.eclipse.persistence.internal.jpa.metadata.beanvalidation;

/**
 * Crate serves for storing objects and optionally provides methods to perform basic
 * operations upon the stored objects.
 *
 * Crates can be sub-classed to chain-add more args or logic.
 */
public interface Crate {

    /**
     * Single-arg crate.
     *
     * @param <T> payload1
     */
    public static class Single<T> implements Crate {
        protected T payload1;

        Single() { }

        public T getPayload1() {
            return payload1;
        }

        public void setPayload1(T payload1) {
            this.payload1 = payload1;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Single single = (Single) o;

            return !(payload1 != null ? !payload1.equals(single.payload1) : single.payload1 != null);
        }

        @Override
        public int hashCode() {
            return payload1 != null ? payload1.hashCode() : 0;
        }
    }

    /**
     * Two-args crate.
     *
     * @param <T> payload1
     * @param <R> payload2
     */
    public static class Tuple<T, R> extends Single<T> {
        protected R payload2;

        Tuple() { }

        public R getPayload2() {
            return payload2;
        }

        public void setPayload2(R payload2) {
            this.payload2 = payload2;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;

            Tuple tuple = (Tuple) o;

            return !(payload2 != null ? !payload2.equals(tuple.payload2) : tuple.payload2 != null);
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + (payload2 != null ? payload2.hashCode() : 0);
            return result;
        }
    }

    /**
     * Three-args crate.
     *
     * @param <T> payload1
     * @param <R> payload2
     * @param <V> payload3
     */
    public static class Triplet<T, R, V> extends Tuple<T, R> {
        protected R payload3;

        Triplet() { }

        public R getPayload3() {
            return payload3;
        }

        public void setPayload3(R payload3) {
            this.payload3 = payload3;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;

            Tuple tuple = (Tuple) o;

            return !(payload3 != null ? !payload3.equals(tuple.payload2) : tuple.payload2 != null);
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + (payload3 != null ? payload3.hashCode() : 0);
            return result;
        }
    }

}
