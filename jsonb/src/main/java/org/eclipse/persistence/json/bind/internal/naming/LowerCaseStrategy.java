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

package org.eclipse.persistence.json.bind.internal.naming;

import javax.json.bind.config.PropertyNamingStrategy;
import java.nio.CharBuffer;
import java.util.Objects;

/**
 * Common parent for lowercase strategies.
 * @author Roman Grigoriadi
 */
public abstract class LowerCaseStrategy implements PropertyNamingStrategy {

    @Override
    public String translateName(String propertyName) {
        Objects.requireNonNull(propertyName);
        CharBuffer charBuffer = CharBuffer.allocate(propertyName.length() * 2);
        char last = Character.MIN_VALUE;
        for(int i=0; i<propertyName.length(); i++) {
            final char current = propertyName.charAt(i);
            if (i > 0 && Character.isUpperCase(current) && isLowerCaseCharacter(last)) {
                charBuffer.append(getSerarator());
            }
            charBuffer.append(Character.toLowerCase(current));
            last = current;
        }
        return new String(charBuffer.array(), 0, charBuffer.position());
    }

    private boolean isLowerCaseCharacter(char character) {
        return Character.isAlphabetic(character) && Character.isLowerCase(character);
    }

    /**
     * Separator for replacing camel case.
     * @return separator
     */
    protected abstract char getSerarator();
}
