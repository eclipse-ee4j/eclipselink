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

public class EndElementEvent extends Event {

    private String uri;
    private String localName;
    private String qName;

    public EndElementEvent(String uri, String localName, String qName) {
        this.uri = uri;
        this.localName = localName;
        this.qName = qName;
    }

    public String getUri() {
        return uri;
    }

    public String getLocalName() {
        return localName;
    }

    public String getQName() {
        return qName;
    }

    @Override
    public boolean equals(Object object) {
        if(!super.equals(object)) {
            return false;
        }
        EndElementEvent testEvent = (EndElementEvent) object;
        return equals(uri, testEvent.getUri()) && equals(localName, testEvent.getLocalName()) && qName.equals(testEvent.getQName());
    }

}