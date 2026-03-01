/*
 * Copyright (c) 2019, 2025 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.internal.identitymaps;

import java.util.Comparator;

/**
 * Comparator for CacheIds with array comparison support
 */
class CacheIdComparator implements Comparator<CacheId> {

    @Override
    public int compare(CacheId id1, CacheId id2) {
        if (id1 == id2) {
            return 0;
        }
        return compareObjectArrays(id1.primaryKey, id2.primaryKey);
    }

    private int compareObjectArrays(Object[] array1, Object[] array2) {
        if (array1.length == array2.length) {
            for (int index = 0; index < array1.length; index++) {
                Object value1 = array1[index];
                Object value2 = array2[index];
                if (value1 == null) {
                    if (value2 != null) {
                        return -1;
                    } else {
                        continue;
                    }
                } else if (value2 == null) {
                    return 1;
                }

                Class<?> value1Class = value1.getClass();
                if (value1Class.isArray()) {
                    Class<?> value2Class = value2.getClass();
                    if (value1Class == CacheId.APBYTE && value2Class == CacheId.APBYTE) {
                        int result = compareByteArrays((byte[])value1, (byte[])value2);
                        if (result != 0) {
                            return result;
                        }
                    } else if (value1Class == CacheId.APCHAR && value2Class == CacheId.APCHAR) {
                        int result = compareCharArrays((char[])value1, (char[])value2);
                        if (result != 0) {
                            return result;
                        }
                    } else {
                        int result = compareObjectArrays((Object[])value1, (Object[])value2);
                        if (result != 0) {
                            return result;
                        }
                    }
                } else {
                    if (value1.getClass().isInstance(value2) && value1 instanceof Comparable<?>) {
                        int result = compareComparable((Comparable<?>)value1, (Comparable<?>) value2);
                        if (result != 0) {
                            return result;
                        }
                    } else if (value2.getClass().isInstance(value1) && value1 instanceof Comparable<?>) {
                        int result = compareComparable((Comparable<?>)value2, (Comparable<?>) value1);
                        if (result != 0) {
                            return result;
                        }
                    } else {
                        int result = value1.hashCode() - value2.hashCode();
                        if (result != 0) {
                            return result > 0 ? 1 : -1;
                        }
                    }
                }
            }
            return 0;
        }
        return array1.length > array2.length ? 1 : -1;
    }

    private int compareCharArrays(char[] array1, char[] array2) {
        if (array1.length == array2.length) {
            for (int index = 0; index < array1.length; index++) {
                if (array1[index] != array2[index]) {
                    return array1[index] > array2[index] ? 1 : -1;
                }
            }
            return 0;
        }
        return array1.length > array2.length ? 1 : -1;
    }

    private int compareByteArrays(byte[] array1, byte[] array2) {
        if (array1.length == array2.length) {
            for (int index = 0; index < array1.length; index++) {
                if (array1[index] != array2[index]) {
                    return array1[index] > array2[index] ? 1 : -1;
                }
            }
            return 0;
        }
        return array1.length > array2.length ? 1 : -1;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private int compareComparable(Comparable value1, Comparable value2) {
        return value1.compareTo(value2);
    }
}
