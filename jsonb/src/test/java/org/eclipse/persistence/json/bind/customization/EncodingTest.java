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

package org.eclipse.persistence.json.bind.customization;

import org.eclipse.persistence.json.bind.TestTypeToken;
import org.junit.Test;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Tests encoding to JSONP propagation
 *
 * @author Roman Grigoriadi
 */
public class EncodingTest {

    private static final Logger logger = Logger.getLogger(EncodingTest.class.getName());

    private static final String[] CZECH;
    private static final String[] RUSSIAN;


    static {
        ResourceBundle messages = ResourceBundle.getBundle("messages");
        CZECH = messages.getString("czechDiacritics").split(",");
        RUSSIAN = messages.getString("russianDiacritics").split(",");
    }

    @Test
    public void testCP1250Encoding() throws UnsupportedEncodingException {
        testMarshaller(CZECH, "cp1250");
        testUnmarshaller(CZECH, "cp1250");
    }

    @Test
    public void testUTF8Encoding() throws UnsupportedEncodingException {
        testMarshaller(CZECH, "UTF-8");
        testUnmarshaller(CZECH, "UTF-8");
        testMarshaller(RUSSIAN, "UTF-8");
        testUnmarshaller(RUSSIAN, "UTF-8");
    }

    @Test
    public void testcp1251Encoding() throws UnsupportedEncodingException {
        testMarshaller(RUSSIAN, "cp1251");
        testUnmarshaller(RUSSIAN, "cp1251");
    }

    private void testMarshaller(String[] input, String encoding) throws UnsupportedEncodingException {
        JsonbConfig config = new JsonbConfig().withEncoding(encoding);
        final Jsonb jsonb = JsonbBuilder.create(config);

        List<String> strings = Arrays.asList(input);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        jsonb.toJson(strings, baos);
        String marshallerResult = baos.toString(encoding);
        logger.finest("Marshaller JSON result: "+marshallerResult);
        assertEquals(diacriticsToJsonArray(input), marshallerResult);
    }

    private void testUnmarshaller(String[] input, String encoding) throws UnsupportedEncodingException {
        JsonbConfig config = new JsonbConfig().withEncoding(encoding);
        final Jsonb jsonb = JsonbBuilder.create(config);

        String json = diacriticsToJsonArray(input);
        logger.finest("JSON for unmarshaller: "+json);
        InputStream bis = new ByteArrayInputStream(json.getBytes(encoding));
        ArrayList<String> result = jsonb.fromJson(bis, new TestTypeToken<ArrayList<String>>(){}.getType());
        assertArrayEquals(input, result.toArray(new String[result.size()]));
    }

    private String diacriticsToJsonArray(String[] diacritics) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (String str : diacritics) {
            sb.append("\"").append(str).append("\",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("]");
        return sb.toString();
    }
}
