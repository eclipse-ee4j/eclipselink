/*******************************************************************************
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Dmitry Kornilov - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.json.bind.defaultmapping.collections;

import org.eclipse.persistence.json.bind.JsonBindingBuilder;
import org.junit.Test;

import javax.json.bind.Jsonb;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Default mapping arrays/collections/enums tests.
 *
 * @author Dmitry Kornilov
 */
public class CollectionsTest {
    @Test
    public void testMarshallCollection() {
        final Jsonb jsonb = (new JsonBindingBuilder()).build();

        final Collection<Integer> collection = Arrays.asList(1, 2, 3);
        assertEquals("[1,2,3]", jsonb.toJson(collection));
    }

    @Test
    public void testMarshallMap() {
        final Jsonb jsonb = (new JsonBindingBuilder()).build();

        final Map<String, Integer> map = new LinkedHashMap<>();
        map.put("1",1);
        map.put("2",2);
        map.put("3",3);

        assertEquals("{\"1\":1,\"2\":2,\"3\":3}", jsonb.toJson(map));
    }

    @Test
    public void testMarshallAnyCollection() {
        final Jsonb jsonb = (new JsonBindingBuilder()).build();
        final Deque<String> deque = new ArrayDeque<>();
        deque.add("first");
        deque.add("second");

        assertEquals("[\"first\",\"second\"]", jsonb.toJson(deque));
    }

    @Test
    public void testMarshallArray() {
        final Jsonb jsonb = (new JsonBindingBuilder()).build();

        //support of arrays of types that JSON Binding is able to serialize
        //Byte[], Short[], Integer[] Long[], Float[], Double[], BigInteger[], BigDecimal[], Number[]
        //Object[], JsonArray[], JsonObject[], JsonStructure[]
        //String[], Character[]
        //byte[], short[], int[], long[], float[], double[], char[], boolean[]
        //java.net.URL[], java.net.URI[]
        //Map[], Collection[], other collections ...
        //enum, EnumSet, EnumMap
        //support of multidimensional arrays


        final Byte[] byteArray = {1, 2, 3};
        assertEquals("[1,2,3]", jsonb.toJson(byteArray));

        final Integer[] integerArray = {1, 2, 3};
        assertEquals("[1,2,3]", jsonb.toJson(integerArray));

        final String[] stringArray = {"first", "second", "third"};
        assertEquals("[\"first\",\"second\",\"third\"]", jsonb.toJson(stringArray));

        final byte[] bytePrimitivesArray = {1, 2, 3};
        assertEquals("[1,2,3]", jsonb.toJson(bytePrimitivesArray));

        final int[] intArray = {1, 2, 3};
        assertEquals("[1,2,3]", jsonb.toJson(intArray));

        final String[][] stringMultiArray = {{"first", "second"},{"third", "fourth"}};
        assertEquals("[[\"first\",\"second\"],[\"third\",\"fourth\"]]", jsonb.toJson(stringMultiArray));

        final Map<String, Object>[][] mapMultiArray = new LinkedHashMap[2][2];
        mapMultiArray[0][0] = new LinkedHashMap<>(1);
        mapMultiArray[0][0].put("0", 0);
        mapMultiArray[0][1] = new LinkedHashMap<>(1);
        mapMultiArray[0][1].put("0", 1);
        mapMultiArray[1][0] = new LinkedHashMap<>(1);
        mapMultiArray[1][0].put("1", 0);
        mapMultiArray[1][1] = new LinkedHashMap<>(1);
        mapMultiArray[1][1].put("1", 1);

        assertEquals("[[{\"0\":0},{\"0\":1}],[{\"1\":0},{\"1\":1}]]", jsonb.toJson(mapMultiArray));
    }

    @Test
    public void testMarshallEnum() {
        final Jsonb jsonb = (new JsonBindingBuilder()).build();

        final Language language = Language.Russian;
        assertEquals("\"Russian\"", jsonb.toJson(language));
    }

    @Test
    public void testMarshallEnumSet() {
        final Jsonb jsonb = (new JsonBindingBuilder()).build();

        final EnumSet<Language> languageEnumSet = EnumSet.of(Language.Czech, Language.Slovak);

        final String result = jsonb.toJson(languageEnumSet);
        assertTrue("[\"Czech\",\"Slovak\"]".equals(result) || "[\"Slovak\",\"Czech\"]".equals(result));
    }

    @Test
    public void testMarshallEnumMap() {
        final Jsonb jsonb = (new JsonBindingBuilder()).build();

        final EnumMap<Language, String> languageEnumMap = new EnumMap<>(Language.class);
        languageEnumMap.put(Language.Russian, "ru");
        languageEnumMap.put(Language.English, "en");

        final String result = jsonb.toJson(languageEnumMap);
        assertTrue("{\"Russian\":\"ru\",\"English\":\"en\"}".equals(result) ||
                "{\"English\":\"en\",\"Russian\":\"ru\"}".equals(result));
    }
}
