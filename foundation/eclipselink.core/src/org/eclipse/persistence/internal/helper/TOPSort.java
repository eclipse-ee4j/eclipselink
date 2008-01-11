/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.helper;


/**
 * INTERNAL:
 */
public class TOPSort {
    public TOPSort() {
    }

    public static void quicksort(Object[] arrayToSort, int left, int right, TOPComparison compareOperator) {
        if (left >= right) {
            return;
        }
        swapElements(arrayToSort, left, (left + right) / 2);
        int last = left;
        for (int i = left + 1; i <= right; i++) {
            if (compareOperator.compare(arrayToSort[i], arrayToSort[left]) < 0) {
                swapElements(arrayToSort, ++last, i);
            }
        }
        swapElements(arrayToSort, left, last);
        quicksort(arrayToSort, left, last - 1, compareOperator);
        quicksort(arrayToSort, last + 1, right, compareOperator);
    }

    public static void quicksort(Object[] arrayToSort, TOPComparison compareOperator) {
        quicksort(arrayToSort, 0, arrayToSort.length - 1, compareOperator);
    }

    protected static void swapElements(Object[] arrayToSort, int index1, int index2) {
        Object tempIndex = arrayToSort[index1];
        arrayToSort[index1] = arrayToSort[index2];
        arrayToSort[index2] = tempIndex;
    }
}