/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
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

public class StartPrefixMappingEvent extends Event {

    private String prefix;
    private String uri;

    public StartPrefixMappingEvent(String prefix, String uri) {
        this.prefix = prefix;
        this.uri = uri;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getUri() {
        return uri;
    }

    @Override
    public boolean equals(Object object) {
        if(!super.equals(object)) {
            return false;
        }
        StartPrefixMappingEvent testEvent = (StartPrefixMappingEvent) object;
        return equals(prefix, testEvent.getPrefix()) && equals(uri, testEvent.getUri());
    }

}