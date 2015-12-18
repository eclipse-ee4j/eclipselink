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
 * Roman Grigoriadi
 ******************************************************************************/

package org.eclipse.persistence.json.bind.defaultmapping.inheritance;

import org.eclipse.persistence.json.bind.defaultmapping.generics.model.GenericTestClass;
import org.eclipse.persistence.json.bind.defaultmapping.generics.model.PropagatedGenericClass;
import org.eclipse.persistence.json.bind.defaultmapping.inheritance.model.SecondLevel;
import org.eclipse.persistence.json.bind.defaultmapping.inheritance.model.generics.ExtendsExtendsPropagatedGenericClass;
import org.eclipse.persistence.json.bind.defaultmapping.inheritance.model.generics.ExtendsPropagatedGenericClass;
import org.eclipse.persistence.json.bind.defaultmapping.inheritance.model.generics.ImplementsGenericInterfaces;
import org.eclipse.persistence.json.bind.defaultmapping.inheritance.model.generics.SecondLevelGeneric;
import org.junit.Before;
import org.junit.Test;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Tests inheritance model marshalling / unmarshalling
 *
 * Tests property order, method overloading, generic type ({@link java.lang.reflect.TypeVariable}) resolving for unmarshalling.
 *
 * @author Roman Grigoriadi
 */
public class InheritanceTest {

    private Jsonb jsonb;

    @Before
    public void setUp() throws Exception {
        jsonb = JsonbBuilder.create();
    }

    @Test
    public void testBasicInheritance() throws Exception {
        SecondLevel secondLevel = new SecondLevel();
        secondLevel.setInFirstLevel("IN_FIRST_LEVEL");
        secondLevel.setInSecondLevel("IN_SECOND_LEVEL");
        secondLevel.setInZeroOverriddenInFirst("IN_ZERO_OVERRIDDEN_IN_FIRST");

        String json = "{\"inZeroOverriddenInFirst\":\"IN_ZERO_OVERRIDDEN_IN_FIRST\",\"inFirstLevel\":\"IN_FIRST_LEVEL\",\"inSecondLevel\":\"IN_SECOND_LEVEL\"}";
        assertEquals(json, jsonb.toJson(secondLevel));

        SecondLevel result = jsonb.fromJson(json, SecondLevel.class);
        assertEquals("IN_FIRST_LEVEL", result.getInFirstLevel());
        assertEquals("IN_SECOND_LEVEL", result.getInSecondLevel());
        assertEquals("IN_ZERO_OVERRIDDEN_IN_FIRST", result.getInZeroOverriddenInFirst());

    }

    @Test
    public void testBasicGenericInheritance() {
        SecondLevelGeneric<Number, Short, String> secondLevelGeneric = new SecondLevelGeneric<>();
        secondLevelGeneric.setInSecondLevel(BigDecimal.TEN);
        secondLevelGeneric.setInFirstLevel((short) 255);
        secondLevelGeneric.setInZeroOverriddenInFirst("IN_ZERO_OVERRIDDEN_IN_FIRST");
        secondLevelGeneric.setInZero("IN_ZERO");

        String json = "{\"inZero\":\"IN_ZERO\",\"inFirstLevel\":255,\"inZeroOverriddenInFirst\":\"IN_ZERO_OVERRIDDEN_IN_FIRST\",\"inSecondLevel\":10}";
        assertEquals(json, jsonb.toJson(secondLevelGeneric));

        SecondLevelGeneric<Number, Short, String> result = jsonb.fromJson(json, new SecondLevelGeneric<Number, Short, String>() {}.getClass());
        assertEquals(BigDecimal.TEN, result.getInSecondLevel());
        assertEquals(Short.valueOf("255"), result.getInFirstLevel());
        assertEquals("IN_ZERO_OVERRIDDEN_IN_FIRST", result.getInZeroOverriddenInFirst());
        assertEquals("IN_ZERO", result.getInZero());
    }

    @Test
    public void testPropagatedGenericInheritance() throws Exception {

        List<String> stringList = new ArrayList<>();
        stringList.add("first");
        stringList.add("second");

        GenericTestClass<List<String>, BigDecimal> genericInList = new GenericTestClass<>();
        genericInList.field1 = stringList;
        genericInList.field2 = BigDecimal.TEN;

        List<GenericTestClass<List<String>, BigDecimal>> listWithGenerics = new ArrayList<>();
        listWithGenerics.add(genericInList);

        GenericTestClass<String, BigDecimal> genericTestClass = new GenericTestClass<>();
        genericTestClass.field1 = "GENERIC_STRING";
        genericTestClass.field2 = BigDecimal.ONE;

        ExtendsExtendsPropagatedGenericClass underTest = new ExtendsExtendsPropagatedGenericClass();
        underTest.genericTestClass = genericTestClass;
        underTest.genericList = listWithGenerics;

        String json = "{\"genericList\":[{\"field1\":[\"first\",\"second\"],\"field2\":10}],\"genericTestClass\":{\"field1\":\"GENERIC_STRING\",\"field2\":1}}";
        assertEquals(json, jsonb.toJson(underTest));

        ExtendsExtendsPropagatedGenericClass result = jsonb.fromJson(json, ExtendsExtendsPropagatedGenericClass.class);
        assertEquals(GenericTestClass.class, result.genericList.get(0).getClass());
        assertEquals("first", result.genericList.get(0).field1.get(0));
        assertEquals("second", result.genericList.get(0).field1.get(1));
        assertEquals(BigDecimal.TEN, result.genericList.get(0).field2);
        assertEquals(GenericTestClass.class, result.genericTestClass.getClass());
        assertEquals("GENERIC_STRING", result.genericTestClass.field1);
        assertEquals(BigDecimal.ONE, result.genericTestClass.field2);
    }

    @Test
    public void testPropagatedGenericInheritance1() throws Exception {

        List<String> stringList = new ArrayList<>();
        stringList.add("first");
        stringList.add("second");

        GenericTestClass<List<String>, BigDecimal> genericInList = new GenericTestClass<>();
        genericInList.field1 = stringList;
        genericInList.field2 = BigDecimal.TEN;

        List<GenericTestClass<List<String>, BigDecimal>> listWithGenerics = new ArrayList<>();
        listWithGenerics.add(genericInList);

        GenericTestClass<String, BigDecimal> genericTestClass = new GenericTestClass<>();
        genericTestClass.field1 = "SECOND_LEVEL_GENERIC_STRING";
        genericTestClass.field2 = BigDecimal.ONE;

        PropagatedGenericClass<String, BigDecimal> propagatedGenericClass = new PropagatedGenericClass<>();
        propagatedGenericClass.genericList = listWithGenerics;
        propagatedGenericClass.genericTestClass = genericTestClass;


        stringList = new ArrayList<>();
        stringList.add("third");
        stringList.add("fourth");

        genericInList = new GenericTestClass<>();
        genericInList.field1 = stringList;
        genericInList.field2 = BigDecimal.ZERO;

        listWithGenerics = new ArrayList<>();
        listWithGenerics.add(genericInList);

        genericTestClass = new GenericTestClass<>();
        genericTestClass.field1 = "FIRST_LEVEL_GENERIC_STRING";
        genericTestClass.field2 = new BigDecimal("11");

        ExtendsPropagatedGenericClass<String, BigDecimal> extendsPropagatedGenericClass = new ExtendsPropagatedGenericClass<>();
        extendsPropagatedGenericClass.genericList = listWithGenerics;
        extendsPropagatedGenericClass.genericTestClass = genericTestClass;

        SecondLevelGeneric<PropagatedGenericClass<String, BigDecimal>, ExtendsPropagatedGenericClass<String, BigDecimal>, String> secondLevelGeneric = new SecondLevelGeneric<>();
        secondLevelGeneric.setInSecondLevel(propagatedGenericClass);
        secondLevelGeneric.setInFirstLevel(extendsPropagatedGenericClass);
        secondLevelGeneric.setInZeroOverriddenInFirst("STRING_IN_ZERO_OVERRIDDEN_IN_FIRST");
        secondLevelGeneric.setInZero("IN_ZERO");

        String json = "{\"inZero\":\"IN_ZERO\",\"inFirstLevel\":{\"genericList\":[{\"field1\":[\"third\",\"fourth\"],\"field2\":0}],\"genericTestClass\":{\"field1\":\"FIRST_LEVEL_GENERIC_STRING\",\"field2\":11}},\"inZeroOverriddenInFirst\":\"STRING_IN_ZERO_OVERRIDDEN_IN_FIRST\",\"inSecondLevel\":{\"genericList\":[{\"field1\":[\"first\",\"second\"],\"field2\":10}],\"genericTestClass\":{\"field1\":\"SECOND_LEVEL_GENERIC_STRING\",\"field2\":1}}}";
        assertEquals(json, jsonb.toJson(secondLevelGeneric));

        SecondLevelGeneric<PropagatedGenericClass<String, BigDecimal>, ExtendsPropagatedGenericClass<String, BigDecimal>, String> result =
                jsonb.fromJson(json, new SecondLevelGeneric<PropagatedGenericClass<String, BigDecimal>, ExtendsPropagatedGenericClass<String, BigDecimal>, String>() {}.getClass());

        assertEquals("first", result.getInSecondLevel().genericList.get(0).field1.get(0));
        assertEquals("second", result.getInSecondLevel().genericList.get(0).field1.get(1));
        assertEquals(BigDecimal.TEN, result.getInSecondLevel().genericList.get(0).field2);
        assertEquals("SECOND_LEVEL_GENERIC_STRING", result.getInSecondLevel().genericTestClass.field1);
        assertEquals(BigDecimal.ONE, result.getInSecondLevel().genericTestClass.field2);

        assertEquals("third", result.getInFirstLevel().genericList.get(0).field1.get(0));
        assertEquals("fourth", result.getInFirstLevel().genericList.get(0).field1.get(1));
        assertEquals(BigDecimal.ZERO, result.getInFirstLevel().genericList.get(0).field2);
        assertEquals("FIRST_LEVEL_GENERIC_STRING", result.getInFirstLevel().genericTestClass.field1);
        assertEquals(new BigDecimal("11"), result.getInFirstLevel().genericTestClass.field2);

        assertEquals("STRING_IN_ZERO_OVERRIDDEN_IN_FIRST", result.getInZeroOverriddenInFirst());
        assertEquals("IN_ZERO", result.getInZero());
    }

    @Test
    public void testInterfaceGenericInheritance() throws Exception {

        ImplementsGenericInterfaces<String, Integer> implementsGenericInterfaces = new ImplementsGenericInterfaces<>();

        implementsGenericInterfaces.setGenericValue("GENERIC_VALUE");
        implementsGenericInterfaces.setAnotherGenericValue(255);

        String json = "{\"anotherGenericValue\":255,\"genericValue\":\"GENERIC_VALUE\"}";
        assertEquals(json, jsonb.toJson(implementsGenericInterfaces));

        ImplementsGenericInterfaces<String, Integer> result = jsonb.fromJson(json, new ImplementsGenericInterfaces<String, Integer>() {}.getClass());
        assertEquals("GENERIC_VALUE", result.getGenericValue());
        assertEquals(Integer.valueOf(255), result.getAnotherGenericValue());
    }

}
