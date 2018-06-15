/*
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     03/30/2016-2.7 Tomas Kraus
//       - 490677: Initial API and implementation.
package org.eclipse.persistence.testing.tests.nosql;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;

/**
 * Oracle NoSQL database URI.
 * <p>
 * Parses URI {@link String} {@code "nosql://" <host> [ ':' <port> ] '/' <store>}.
 */
public class NoSQLURI {

    /** Logger. */
    private static final SessionLog LOG = AbstractSessionLog.getLog();

    /** Shortcut to match beginning of URI. */
    static final String KEYWORD = "nosql://";

    /**
     * Validate that provided {@link String} starts as Oracle NoSQL database URI.
     * @return Value of {@code true} when provided {@link String} starts as Oracle NoSQL database URI or {@code false}
     *         otherwise.
     */
    public static boolean startsUri(final String uri) {
        return uri.trim().toLowerCase().startsWith(KEYWORD);
    }

    /** NoSQL database host. */
    private final String host;

    /** NoSQL database port. */
    private final int port;

    /** NoSQL database store. */
    private final String store;

    /**
     * Creates an instance of Oracle NoSQL database URI.
     * @param uriStr NoSQL database URI {@link String}.
     */
    public NoSQLURI(final String uriStr) {
        URI uri = null;
        try {
            uri = new URI(uriStr.trim());
        } catch (URISyntaxException e) {
            LOG.log(SessionLog.WARNING, String.format("Error when parsing URI: %s", e.getLocalizedMessage()));
        }
        if (uri != null) {
            String uriPath = uri.getPath();
            if (uriPath.startsWith("/")) {
                uriPath = uriPath.substring(1);
            }
            host = uri.getHost();
            port = uri.getPort();
            store = uriPath;
        } else {
            host = null;
            port = -1;
            store = null;
        }
    }

    /**
     * Get NoSQL database host.
     * @return NoSQL database host.
     */
    public String getHost() {
        return host;
    }

    /**
     * Get NoSQL database port.
     * @return NoSQL database port.
     */
    public int getPort() {
        return port;
    }

    /**
     * Get NoSQL database store.
     * @return NoSQL database store.
     */
    public String getStore() {
        return store;
    }

}
