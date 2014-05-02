/*******************************************************************************
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Martin Grebac - 2.6.0 July 2014
 ******************************************************************************/
package org.eclipse.persistence.internal.oxm.record.json;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import org.eclipse.persistence.testing.framework.TestCase;

/**
 * Test Json support in core.
 */
public class JsonTestSuite extends TestCase {

    private static final Map<String, String> testMap = new TreeMap<String,String>();
    
    public JsonTestSuite() {
        super();
        setName(getName());
        setDescription("Test Json implementation methods");
    }

    public void setup() throws Exception {
        testMap.put("\"abc\"",              "abc");
        testMap.put("\"a\\bbc\"",           "a\bbc");
        testMap.put("\"\\n\"",              "\n");
        testMap.put("\"\\r\\n\"",           "\r\n");
        testMap.put("\"a\\r\\n\"",          "a\r\n");
        testMap.put("\"\r\n\"",             "\n");
        testMap.put("\"\\u041e\\u041a\"",   "ОК");
        testMap.put("\"abc\n\"",            "abc\n");
        testMap.put("\"abc\r\n\"",          "abc\n");
        testMap.put("\"abc\\r\\n\"",        "abc\r\n");
        testMap.put("\"abcrr\\n\\nd\"",     "abcrr\n\nd");
        testMap.put("\"abcrr\\n\\ndd\"",    "abcrr\n\ndd");

        // create some large string to deal with
        StringBuilder input = new StringBuilder();
        input.append("\"");
        for (int i=1;i<10000;i++) {
            input.append("aaaaaaaaaaaaaaaaaa\baaaaaaaaaaaaaaaaaaa\\n\n");
        }
        input.append("\"");

        StringBuilder output = new StringBuilder();
        for (int i=1;i<10000;i++) {
            output.append("aaaaaaaaaaaaaaaaaa\baaaaaaaaaaaaaaaaaaa\n\n");
        }
        testMap.put(input.toString(), output.toString());            
    }

    // Execute all tests in suite.
    public void test() {
        jsonStringEscapingTest();
    }

    public void jsonStringEscapingTest() {
        Iterator<Map.Entry<String,String>> testEntryIterator = testMap.entrySet().iterator();
        while (testEntryIterator.hasNext()) {
            Map.Entry<String,String> entry = testEntryIterator.next();
            String input = entry.getKey();
            String output = JSONReader.string(input);
            String expectedOutput = entry.getValue();
            assertEquals("Difference found in json string escaping for string " + input, expectedOutput, output);
        }
    }

}
