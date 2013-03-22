/*******************************************************************************
 * Copyright (c) 2012, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 *
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *  - rbarkhouse - 07 February 2012 - 2.3.3 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.oxm;

import java.io.IOException;
import java.io.Writer;

/**
 * <p>
 * Provide an interface to allow for custom character escaping behaviour.
 * </p>
 */
public interface CharacterEscapeHandler {

    /**
     * <p>
     * Perform character escaping and write the result to the output.
     * </p>
     *
     * <p>
     * Note: This feature is <i>not</i> supported when marshalling to the following targets:
     * <ul>
     *  <li>javax.xml.stream.XMLStreamWriter</li>
     *  <li>javax.xml.stream.XMLEventWriter</li>
     *  <li>org.xml.sax.ContentHandler</li>
     *  <li>org.w3c.dom.Node</li>
     * </ul>
     * </p>
     *
     * @param buffer Array of characters to be escaped
     * @param start The starting position
     * @param length The number of characters being escaped
     * @param isAttributeValue A value of 'true' indicates this is an attribute value
     * @param out The resulting escaped characters will be written to this Writer
     * @throws IOException In an error condition, IOException can be thrown to stop the marshalling process
     */
    public void escape(char[] buffer, int start, int length, boolean isAttributeValue, Writer out) throws IOException;

}