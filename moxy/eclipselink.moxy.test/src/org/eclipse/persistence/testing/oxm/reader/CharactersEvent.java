/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.2 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.oxm.reader;

public class CharactersEvent extends Event {

    private String string;

    public CharactersEvent(String string) {
        this.string = string;
    }

    public String getString() {
        return string;
    }

    @Override
    public boolean equals(Object object) {
        if(!super.equals(object)) {
            return false;
        }
        CharactersEvent testEvent = (CharactersEvent) object;
        return equals(string, testEvent.getString());
    }

}