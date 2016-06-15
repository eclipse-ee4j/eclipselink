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

import java.nio.CharBuffer;

/**
 * Upper case first character separate words by spaces.
 *
 * @author Roman Grigoriadi
 */
public class UpperCamelCaseWithSpacesStrategy extends UpperCamelCaseStrategy {

    @Override
    public String translateName(String propertyName) {
        String upperCased = super.translateName(propertyName);
        CharBuffer buffer = CharBuffer.allocate(upperCased.length() * 2);
        char last = Character.MIN_VALUE;
        for(int i=0; i<upperCased.length(); i++) {
            char current = upperCased.charAt(i);
            if (i > 0 && Character.isUpperCase(current) && isLowerCaseCharacter(last)) {
                buffer.append(' ');
            }
            last = current;
            buffer.append(current);
        }
        return new String(buffer.array(), 0, buffer.position());

    }

    private boolean isLowerCaseCharacter(char character) {
        return Character.isAlphabetic(character) && Character.isLowerCase(character);
    }
}
