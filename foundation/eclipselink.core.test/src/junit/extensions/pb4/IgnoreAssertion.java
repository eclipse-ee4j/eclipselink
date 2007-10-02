/*******************************************************************************
 * Copyright (c) 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - purpose: extended JUnit4 testing for Oracle TopLink
 ******************************************************************************/

package junit.extensions.pb4;

public class IgnoreAssertion {

    static public void ignore() throws IgnoreException {
        throw new IgnoreException();
    }

    static public void ignore(String message) throws IgnoreException {

        throw new IgnoreException(message);
    }

    static public void ignore(boolean flag) throws IgnoreException {

        if (flag) {
            throw new IgnoreException();
        }
    }

    static public void ignore(String message, boolean flag) throws IgnoreException {

        if (flag) {
            throw new IgnoreException(message);
        }
    }
}
