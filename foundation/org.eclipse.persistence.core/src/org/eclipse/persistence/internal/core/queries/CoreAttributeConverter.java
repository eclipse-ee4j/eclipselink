/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Tomas Kraus - Initial API and implementation
package org.eclipse.persistence.internal.core.queries;

import java.util.ArrayDeque;

import org.eclipse.persistence.internal.helper.StringHelper;
import static org.eclipse.persistence.internal.helper.StringHelper.TAB;
import static org.eclipse.persistence.internal.helper.StringHelper.LF;
import static org.eclipse.persistence.internal.helper.StringHelper.FF;
import static org.eclipse.persistence.internal.helper.StringHelper.CR;
import static org.eclipse.persistence.internal.helper.StringHelper.SPACE;

// This class have huge performance impact because convert method is used very often.
/**
 * INTERNAL:
 * AttributeGroup attribute names converter.
 */
public class CoreAttributeConverter {

    /**
     * String containing '.'.
     */
    private static final String DOT = Character.toString(StringHelper.DOT);


    // Path convert state machine
    // Splits String around '.' character. Path elements starting or ending
    // with whitespace or of zero length are considered as illegal.
    //                  ,---+
    //                  | x V
    //           x     +----+     x
    //     ,---------->| CH |<-----------,
    //     |           +----+            |
    //  +-----+    '.'  |  |   ' '    +----+
    //  | DOT |<--------'  `--------->| SP |
    //  +-----+                       +----+
    //     | ' ', '.'  +-----+    '.'    |
    //     `---------->| ERR |<----------'
    //                 +-----+

    /**
     * Path convert state machine internal states.
     */
    private static enum ConvertState {
        /** Initial state or after path elements separator received. */
        DOT,
        /** After space in the middle of path (at least one regular character received in path element). */
        SP,
        /** After regular path character received in path element. */
        CH,
    }

    // PERF: Whitespace characters set reduced to TAB, LF, FF, CR and SPACE.
    //       Everything is one long and ugly method.
    /**
     * INTERNAL:
     * Splits given <code>nameOrPath[0]</code> argument around <code>'.'</code> character
     * when <code>nameOrPath.length</code> is equal to 1. Arrays of <code>nameOrPath</code>
     * containing more than one element are only validated an passed without any changes.
     * Zero length arrays are considered as invalid.
     *
     * @param nameOrPath {@link String} to be split.
     * @return An array of {@link String}s computed by splitting provided
     *         <code>nameOrPath[0]</code> argument around <code>'.'</code> character.
     * @throws IllegalArgumentException If <code>nameOrPath</code> argument is <code>null</code>
     *         or any element to be returned after split is <code>null</code>, empty or contains
     *         whitespace at the beginning or end.
     */
    public static final String[] convert(final String... nameOrPath)
            throws IllegalArgumentException {
        if (nameOrPath == null) {
            throw new IllegalArgumentException("Name or path value is null");
        }
        switch(nameOrPath.length) {
        // Empty String array.
        case 0:
            throw new IllegalArgumentException("Name or path value size is zero");
        // Single String: may contain path elements separated by '.'.
        case 1:
            final String str = nameOrPath[0];
            if (str == null) {
                throw new IllegalArgumentException("Name or path value is null");
            }
            final int len = str.length();
            if (!str.contains(DOT)) {
                switch(len) {
                case 0:
                    throw new IllegalArgumentException("Empty name or path value");
                case 1:
                    switch(str.charAt(0)) {
                    case TAB: case LF: case FF: case CR: case SPACE:
                        throw new IllegalArgumentException("Name or path value starts with whitespace.");
                    }
                default:
                    switch(str.charAt(0)) {
                    case TAB: case LF: case FF: case CR: case SPACE:
                        throw new IllegalArgumentException("Name or path value starts with whitespace.");
                    }
                    switch(str.charAt(len - 1)) {
                    case TAB: case LF: case FF: case CR: case SPACE:
                        throw new IllegalArgumentException("Name or path value ends with whitespace.");
                    }
                }
            return nameOrPath;
            }
            final char[] chars = nameOrPath[0].toCharArray();
            // Current character being processed.
            char c;
            // Current state machine state.
            ConvertState s = ConvertState.DOT;
            // Index of current character in parsed String
            int index;
            // Index of 1st path element regular character.
            int begIndex = 0;
            // Path elements storage (not needed when there is just one element).
            ArrayDeque<String> elements = null;
            for (index = 0; index < len; index++) {
                c = chars[index];
                switch (s) {
                // Initial state or after path elements separator received.
                case DOT:
                    switch (c) {
                    // Path elements separator at the beginning or two path element separators
                    // next to each other results in zero length path.
                    case StringHelper.DOT:
                        throw new IllegalArgumentException("Name or path value contains empty path element");
                    // Whitespace at the beginning.
                    case TAB: case LF: case FF: case CR: case SPACE:
                        throw new IllegalArgumentException("Path element starts with whitespace.");
                    // First regular character.
                    default:
                        s = ConvertState.CH;
                    }
                    break;
                // After space in the middle of path.
                case SP:
                    switch (c) {
                    // Path elements separator after whitespace.
                    case StringHelper.DOT:
                        throw new IllegalArgumentException("Path element ends with whitespace.");
                    // Whitespace after whitespace.
                    case TAB: case LF: case FF: case CR: case SPACE:
                        break;
                    // Regular character after whitespace in the middle of path.
                    default:
                        s = ConvertState.CH;
                    }
                    break;
                // After regular path character received.
                case CH:
                    switch (c) {
                    // Path element separator after regular path character.
                    case StringHelper.DOT:
                        // Lazy initialization of elements storage.
                        if (elements == null) {
                            elements = new ArrayDeque(4);
                        }
                        // Store finished path element.
                        elements.addLast(new String(chars, begIndex, index - begIndex));
                        // Next character starts next path element.
                        begIndex = index + 1;
                        s = ConvertState.DOT;
                        break;
                    // Space in the middle of path element.
                    case TAB: case LF: case FF: case CR: case SPACE:
                        s = ConvertState.SP;
                    }
                }
            }
            // Now process end of input attribute.
            switch (s) {
            // Last character is path elements separator so there is an empty
            // path element at the end of string or input string was empty.
            case DOT:
                throw new IllegalArgumentException("Empty name or path value or last path element");
            // Last character is whitespace.
            case SP:
                throw new IllegalArgumentException("Name or path value ends with space");
            // Remaining CH as default: Last character is regular character.
            default:
                // There was no separator in the name or path value. Original
                // value is returned.
                if (elements == null) {
                    return nameOrPath;
                // Return array of path elements found. Last element is still
                // in input String.
                } else {
                    int n = 0, count = elements.size();
                    String[] paths = new String[count + 1];
                    while ((paths[n++] = elements.pollFirst()) != null);
                    paths[count] = new String(chars, begIndex, index - begIndex);
                    return paths;
                }
            }
        // Multiple Strings: Validate and pass them as they are.
        default:
            String item;
            for (int i = 0; i < nameOrPath.length; i++) {
                item = nameOrPath[i];
                if (item == null) {
                    throw new IllegalArgumentException("Name or path value at index "
                            + Integer.toString(i) + " is null");
                }
                final int itemLen = item.length();
                // Empty String.
                if (itemLen == 0) {
                    throw new IllegalArgumentException("Name or path value at index "
                            + Integer.toString(i) + " is empty String");
                // String contains at least one character.
                } else {
                    switch(item.charAt(0)) {
                    case TAB: case LF: case FF: case CR: case SPACE:
                        throw new IllegalArgumentException("Name or path value at index "
                                + Integer.toString(i) + " starts with whitespace");
                    }
                    // String contains more than one character.
                    if (itemLen > 0) {
                        switch(item.charAt(itemLen - 1)) {
                        case TAB: case LF: case FF: case CR: case SPACE:
                            throw new IllegalArgumentException("Name or path value at index "
                                    + Integer.toString(i) + " ends with whitespace");
                        }
                    }
                }
            }
            return nameOrPath;
        }
    }

    /**
     * INTERNAL:
     * This class is just an envelope for static methods so no instances are allowed.
     */
    private CoreAttributeConverter() {
        throw new UnsupportedOperationException("Instances of CoreAttributeConverter are not allowed");
    }

}
