/*******************************************************************************
 * Copyright (c) 2016 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Roman Grigoriadi
 ******************************************************************************/

package org.eclipse.persistence.json.bind.internal;

import javax.json.stream.JsonParser;

/**
 * Jsonb parsing helper methods on top of JSOPN parser.
 * @author Roman Grigoriadi
 */
public interface JsonbParser extends JsonParser {

    /**
     * Moves parser to required event, if current event is equal to required does nothing.
     * @param event required event
     */
    void moveTo(JsonParser.Event event);

    /**
     * Moves parser cursor to any JSON value.
     */
    Event moveToValue();

    /**
     * Moves parser cursor to START_OBJECT or START_ARRAY.
     */
    Event moveToStartStructure();

    /**
     * Current level of JsonbRiParser.
     * @return current level
     */
    JsonbRiParser.LevelContext getCurrentLevel();

    /**
     * Skips a value or a structure.
     * If current event is START_ARRAY or START_OBJECT, whole structure is skipped to end.
     */
    void skipJsonStructure();
}
