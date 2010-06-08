/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.clientserver;


// A simple thread-safe FirstInFirstOut queue (non-null objects only)
class FIFO {
    // Parameter is a maximum number of objects in FIFO
    public FIFO(int nSize) {
        this.nSize = nSize;
        objectArray = new Object[nSize];
    }

    public boolean isFull() {
        return nCount == nSize;
    }

    public boolean isEmpty() {
        return nCount == 0;
    }

    // Adds an object to FIFO.
    // Returns true if successful.
    // Returns false in two cases:
    // 1. There is no room in FIFO;
    // 2. Attempt to insert null;
    public synchronized boolean insertTail(Object object) {
        if (!isFull() && (object != null)) {
            if (!isEmpty()) {
                nTail = next(nTail);
            } else if (nHead != nTail) {
                error();
            }

            if (objectArray[nTail] == null) {
                objectArray[nTail] = object;
                nCount++;
                return true;
            } else {
                error();
                return false;
            }
        } else {
            return false;
        }
    }

    // Removes an object from FIFO.
    // Returns the removed object,
    // or null if FIFO empty.
    public synchronized Object removeHead() {
        Object head = null;
        if (!isEmpty()) {
            head = objectArray[nHead];
            if (head != null) {
                objectArray[nHead] = null;
                nCount--;
                if (!isEmpty()) {
                    nHead = next(nHead);
                }
            } else {
                error();
            }
        } else if (nHead != nTail) {
            error();
        }

        return head;
    }

    protected int next(int i) {
        return (i + 1) % nSize;
    }

    // An error check - should NEVER happen.
    // Checks that the class works as designed.
    // Here are the invariants which if broken cause error() to be called:
    // 1. nCount==0 ==> nHead==nTail;
    // 2. The elements of objectArray not currently occupied should contain null.
    // 3. Stored objects should be NON null.
    private void error() {
        System.out.println("FIFO error");
    }

    private int nTail;
    private int nHead;
    private int nCount;
    private int nSize;
    private Object[] objectArray;
}
