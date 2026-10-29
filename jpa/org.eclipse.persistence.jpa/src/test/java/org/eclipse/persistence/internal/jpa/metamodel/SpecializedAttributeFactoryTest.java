/*
 * Copyright (c) 2026 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.internal.jpa.metamodel;

import java.lang.ref.Reference;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.Temporal;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import jakarta.persistence.metamodel.BooleanAttribute;
import jakarta.persistence.metamodel.ComparableAttribute;
import jakarta.persistence.metamodel.NumericAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.TemporalAttribute;
import jakarta.persistence.metamodel.TextAttribute;

import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.internal.jpa.EntityManagerSetupImpl;
import org.eclipse.persistence.internal.jpa.metamodel.proxy.SingularAttributeProxyImpl;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.eclipse.persistence.internal.jpa.metamodel.SpecializedAttributeFactory.Kind.BOOLEAN;
import static org.eclipse.persistence.internal.jpa.metamodel.SpecializedAttributeFactory.Kind.COMPARABLE;
import static org.eclipse.persistence.internal.jpa.metamodel.SpecializedAttributeFactory.Kind.NUMERIC;
import static org.eclipse.persistence.internal.jpa.metamodel.SpecializedAttributeFactory.Kind.SINGULAR;
import static org.eclipse.persistence.internal.jpa.metamodel.SpecializedAttributeFactory.Kind.TEMPORAL;
import static org.eclipse.persistence.internal.jpa.metamodel.SpecializedAttributeFactory.Kind.TEXT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpecializedAttributeFactoryTest {

    static Stream<Arguments> classifications() {
        return Stream.of(
                Arguments.of(Boolean.class, BOOLEAN), Arguments.of(boolean.class, BOOLEAN),
                Arguments.of(String.class, TEXT),
                Arguments.of(byte.class, NUMERIC), Arguments.of(Byte.class, NUMERIC),
                Arguments.of(short.class, NUMERIC), Arguments.of(Short.class, NUMERIC),
                Arguments.of(int.class, NUMERIC), Arguments.of(Integer.class, NUMERIC),
                Arguments.of(long.class, NUMERIC), Arguments.of(Long.class, NUMERIC),
                Arguments.of(float.class, NUMERIC), Arguments.of(Float.class, NUMERIC),
                Arguments.of(double.class, NUMERIC), Arguments.of(Double.class, NUMERIC),
                Arguments.of(BigInteger.class, NUMERIC), Arguments.of(BigDecimal.class, NUMERIC),
                Arguments.of(LocalDate.class, TEMPORAL), Arguments.of(LocalTime.class, TEMPORAL),
                Arguments.of(LocalDateTime.class, TEMPORAL),
                Arguments.of(char.class, COMPARABLE), Arguments.of(Character.class, COMPARABLE),
                Arguments.of(SampleEnum.class, COMPARABLE), Arguments.of(Date.class, COMPARABLE),
                Arguments.of(Calendar.class, COMPARABLE), Arguments.of(Duration.class, COMPARABLE),
                Arguments.of(AtomicInteger.class, SINGULAR), Arguments.of(Number.class, SINGULAR),
                Arguments.of(CharSequence.class, SINGULAR), Arguments.of(Object.class, SINGULAR),
                Arguments.of(byte[].class, SINGULAR), Arguments.of(void.class, SINGULAR),
                Arguments.of(null, SINGULAR),
                Arguments.of(SelfNumber.class, NUMERIC), Arguments.of(ChildNumber.class, COMPARABLE),
                Arguments.of(WrongNumber.class, SINGULAR), Arguments.of(GoodTemporal.class, TEMPORAL),
                Arguments.of(WrongTemporal.class, SINGULAR), Arguments.of(PlainTemporal.class, SINGULAR),
                Arguments.of(SelfGeneric.class, COMPARABLE), Arguments.of(GenericComparable.class, SINGULAR),
                Arguments.of(RawComparable.class, SINGULAR),
                Arguments.of(BoxComparableChild.class, COMPARABLE), Arguments.of(WrongBoxChild.class, SINGULAR),
                Arguments.of(InheritedBoxChild.class, COMPARABLE),
                Arguments.of(NestedBoxChild.class, COMPARABLE), Arguments.of(WrongNestedBoxChild.class, SINGULAR),
                Arguments.of(WildBoxChild.class, COMPARABLE), Arguments.of(WrongWildBoxChild.class, SINGULAR),
                Arguments.of(WildNestedBoxChild.class, COMPARABLE),
                Arguments.of(BoundedNestedBoxChild.class, COMPARABLE),
                Arguments.of(WrongBoundedNestedBoxChild.class, SINGULAR),
                Arguments.of(LowerBoundBoxChild.class, COMPARABLE),
                Arguments.of(WrongLowerBoundBoxChild.class, SINGULAR),
                Arguments.of(IdenticalWildcard.class, COMPARABLE),
                Arguments.of(ActualWildcard.class, COMPARABLE),
                Arguments.of(WrongActualWildcard.class, SINGULAR),
                Arguments.of(InvariantMismatch.class, SINGULAR),
                Arguments.of(ActualLowerWildcard.class, COMPARABLE),
                Arguments.of(WrongActualLowerWildcard.class, SINGULAR),
                Arguments.of(UnboundedActualWildcard.class, COMPARABLE),
                Arguments.of(WrongUnboundedActualWildcard.class, SINGULAR));
    }

    @ParameterizedTest(name = "{0} -> {1}")
    @MethodSource("classifications")
    void selectsFirstApplicableTypeWithActualComparableBounds(Class<?> type,
                                                             SpecializedAttributeFactory.Kind expected) {
        assertEquals(expected, SpecializedAttributeFactory.kind(type));
    }

    static Stream<Class<?>> canonicalTypes() {
        return Stream.of(BooleanAttribute.class, TextAttribute.class, NumericAttribute.class,
                TemporalAttribute.class, ComparableAttribute.class, SingularAttribute.class);
    }

    @ParameterizedTest
    @MethodSource("canonicalTypes")
    void createsOnlyTheDeclaredSpecializedProxyWithoutDeploying(Class<?> canonicalType) {
        SingularAttributeProxyImpl<?, ?> proxy = SingularAttributeProxyImpl.create(canonicalType);
        assertInstanceOf(canonicalType, proxy);
        assertNull(proxy.getAttributeInternal());
        for (Class<?> other : List.of(BooleanAttribute.class, TextAttribute.class,
                NumericAttribute.class, TemporalAttribute.class)) {
            if (other != canonicalType) {
                assertFalse(other.isInstance(proxy), other.getSimpleName());
            }
        }
        assertEquals(canonicalType != SingularAttribute.class, proxy instanceof ComparableAttribute);
    }

    static Stream<Arguments> declaredTypes() throws NoSuchFieldException {
        return Stream.of(
                Arguments.of("value", COMPARABLE), Arguments.of("number", NUMERIC),
                Arguments.of("wrong", SINGULAR), Arguments.of("rawValue", SINGULAR),
                Arguments.of("unresolvedValue", SINGULAR), Arguments.of("unresolvedWildcard", SINGULAR),
                Arguments.of("owner", COMPARABLE),
                Arguments.of("wrongOwner", SINGULAR), Arguments.of("array", COMPARABLE),
                Arguments.of("wrongArray", SINGULAR), Arguments.of("nestedArray", COMPARABLE),
                Arguments.of("wrongNestedArray", SINGULAR), Arguments.of("arrayBound", COMPARABLE),
                Arguments.of("wrongArrayBound", SINGULAR), Arguments.of("arrayObjectBound", COMPARABLE),
                Arguments.of("wildcardValue", COMPARABLE), Arguments.of("wildcardNumber", NUMERIC),
                Arguments.of("boundedWildcardNumber", NUMERIC), Arguments.of("lowerWildcardNumber", NUMERIC),
                Arguments.of("wrongNestedNumber", SINGULAR), Arguments.of("exactNestedNumber", NUMERIC),
                Arguments.of("numberWithWildcardSupertype", COMPARABLE),
                Arguments.of("directWildcardNumber", SINGULAR), Arguments.of("directWildcardValue", SINGULAR),
                Arguments.of("directBoundedWildcardValue", SINGULAR),
                Arguments.of("fixedComparableWildcard", COMPARABLE));
    }

    @ParameterizedTest(name = "declared {0} -> {1}")
    @MethodSource("declaredTypes")
    void preservesDeclaredTypeArguments(String field, SpecializedAttributeFactory.Kind expected) throws Exception {
        Type type = TypeDeclarations.class.getDeclaredField(field).getGenericType();
        assertEquals(expected, SpecializedAttributeFactory.kind(type, TypeDeclarations.class));
    }

    @Test
    void resolvesInheritedMemberAndNestedDomainBindingsBeforeRebinding() throws Exception {
        Type inherited = GenericMember.class.getDeclaredField("value").getGenericType();
        assertEquals(COMPARABLE, SpecializedAttributeFactory.kind(inherited, ConcreteMember.class));
        assertEquals(SINGULAR, SpecializedAttributeFactory.kind(inherited, GenericMember.class));
        Type nested = SelfMember.class.getDeclaredField("nested").getGenericType();
        assertEquals(COMPARABLE, SpecializedAttributeFactory.kind(nested, ConcreteSelfMember.class));
    }

    @Test
    void resolvedTypesHonorReflectionEqualityAndDefensiveArrayContracts() throws Exception {
        for (String field : List.of("value", "owner", "parameterizedArray", "wildcard")) {
            Type original = TypeDeclarations.class.getDeclaredField(field).getGenericType();
            Type resolved = SpecializedAttributeFactory.resolve(original, TypeDeclarations.class);
            assertEquals(original, resolved);
            assertEquals(resolved, original);
            assertEquals(original.hashCode(), resolved.hashCode());
            if (resolved instanceof ParameterizedType parameterized) {
                Type[] arguments = parameterized.getActualTypeArguments();
                arguments[0] = Void.class;
                assertNotEquals(Void.class, parameterized.getActualTypeArguments()[0]);
            }
            if (resolved instanceof GenericArrayType array) {
                assertEquals(((GenericArrayType) original).getGenericComponentType(), array.getGenericComponentType());
            }
        }
        ParameterizedType parameterized = (ParameterizedType) SpecializedAttributeFactory.resolve(
                TypeDeclarations.class.getDeclaredField("wildcard").getGenericType(), TypeDeclarations.class);
        WildcardType wildcard = (WildcardType) parameterized.getActualTypeArguments()[0];
        wildcard.getUpperBounds()[0] = Void.class;
        assertEquals(Number.class, wildcard.getUpperBounds()[0]);
        ParameterizedType lower = (ParameterizedType) SpecializedAttributeFactory.resolve(
                TypeDeclarations.class.getDeclaredField("lowerWildcard").getGenericType(), TypeDeclarations.class);
        WildcardType lowerBound = (WildcardType) lower.getActualTypeArguments()[0];
        lowerBound.getLowerBounds()[0] = Void.class;
        assertEquals(String.class, lowerBound.getLowerBounds()[0]);
        Type wrongOwner = TypeDeclarations.class.getDeclaredField("differentOwner").getGenericType();
        assertNotEquals(SpecializedAttributeFactory.resolve(wrongOwner, TypeDeclarations.class),
                SpecializedAttributeFactory.resolve(TypeDeclarations.class.getDeclaredField("owner").getGenericType(), TypeDeclarations.class));
    }

    @ParameterizedTest
    @MethodSource("canonicalTypes")
    void concurrentFirstAccessCompletesProxiesWithoutHoldingTheirMonitors(Class<?> canonicalType) throws Exception {
        SingularAttributeProxyImpl<Object, Object> first = typedProxy(canonicalType);
        SingularAttributeProxyImpl<Object, Object> second = typedProxy(canonicalType);
        SingularAttributeProxyImpl<Object, Object> resolved = typedProxy(canonicalType);
        CountDownLatch entered = new CountDownLatch(2);
        Object deployment = new Object();
        AtomicInteger completions = new AtomicInteger();
        EntityManagerFactoryImpl factory = new EntityManagerFactoryImpl((EntityManagerSetupImpl) null, Map.of()) {
            @Override
            public DatabaseSessionImpl getDatabaseSession() {
                // Check before the barrier, so the old monitor boundary fails without a deadlock.
                assertFalse(Thread.holdsLock(first));
                assertFalse(Thread.holdsLock(second));
                entered.countDown();
                try {
                    assertTrue(entered.await(5, TimeUnit.SECONDS), "Concurrent proxy accesses did not enter deployment");
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    throw new AssertionError(exception);
                }
                synchronized (deployment) {
                    if (first.getAttributeInternal() == null) {
                        first.setAttribute(resolved);
                        second.setAttribute(resolved);
                        completions.incrementAndGet();
                    }
                }
                return null;
            }
        };
        first.addFactory(factory);
        second.addFactory(factory);
        var executor = Executors.newFixedThreadPool(2, Thread.ofPlatform().daemon().factory());
        var firstResult = executor.submit(first::getAttribute);
        var secondResult = executor.submit(second::getAttribute);
        try {
            assertSame(resolved, firstResult.get(10, TimeUnit.SECONDS));
            assertSame(resolved, secondResult.get(10, TimeUnit.SECONDS));
            assertSame(resolved, first.getAttribute());
            assertSame(resolved, second.getAttribute());
            assertEquals(1, completions.get());
        } finally {
            firstResult.cancel(true);
            secondResult.cancel(true);
            executor.shutdownNow();
            assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS), "Proxy test threads did not terminate");
            Reference.reachabilityFence(factory);
        }
    }

    @ParameterizedTest
    @MethodSource("canonicalTypes")
    void firstAccessPropagatesDeploymentFailureAndAllowsRetry(Class<?> canonicalType) {
        SingularAttributeProxyImpl<Object, Object> proxy = typedProxy(canonicalType);
        SingularAttributeProxyImpl<Object, Object> resolved = typedProxy(canonicalType);
        IllegalStateException failure = new IllegalStateException("deployment failure");
        AtomicInteger calls = new AtomicInteger();
        EntityManagerFactoryImpl factory = new EntityManagerFactoryImpl((EntityManagerSetupImpl) null, Map.of()) {
            @Override
            public DatabaseSessionImpl getDatabaseSession() {
                assertFalse(Thread.holdsLock(proxy));
                if (calls.getAndIncrement() == 0) {
                    throw failure;
                }
                proxy.setAttribute(resolved);
                return null;
            }
        };
        proxy.addFactory(factory);
        try {
            assertSame(failure, assertThrows(IllegalStateException.class, proxy::getAttribute));
            assertNull(proxy.getAttributeInternal());
            assertSame(resolved, proxy.getAttribute());
            assertSame(resolved, proxy.getAttribute());
            assertEquals(2, calls.get());
        } finally {
            Reference.reachabilityFence(factory);
        }
    }

    @SuppressWarnings("unchecked")
    private static SingularAttributeProxyImpl<Object, Object> typedProxy(Class<?> canonicalType) {
        return (SingularAttributeProxyImpl<Object, Object>) SingularAttributeProxyImpl.create(canonicalType);
    }

    abstract static class Value<T> implements Comparable<Value<T>> { }
    abstract static class GenericNumber<T> extends Number implements Comparable<GenericNumber<T>> { }
    abstract static class NestedNumber<T> extends Number implements Comparable<NestedNumber<List<? extends Number>>> { }
    abstract static class WildcardNumber<T> extends Number implements Comparable<WildcardNumber<? extends Number>> { }
    abstract static class FixedComparable<T> implements Comparable<Object> { }
    abstract static class WrongValue<T> implements Comparable<WrongValue<Integer>> { }
    static class Outer<T> {
        abstract class Inner<U> implements Comparable<Outer<T>.Inner<U>> { }
        abstract class WrongInner<U> implements Comparable<Outer<Integer>.WrongInner<U>> { }
    }
    abstract static class ArrayComparable<T> extends Box<T[]> implements Comparable<Box<String[]>> { }
    abstract static class ArrayMatch extends ArrayComparable<String> { }
    abstract static class ArrayWrong extends ArrayComparable<Integer> { }
    abstract static class NestedArrayComparable<T> extends Box<List<T>[]> implements Comparable<Box<List<String>[]>> { }
    abstract static class NestedArrayMatch extends NestedArrayComparable<String> { }
    abstract static class NestedArrayWrong extends NestedArrayComparable<Integer> { }
    abstract static class ArrayBoundComparable<T> extends Box<T[]> implements Comparable<Box<? extends CharSequence[]>> { }
    abstract static class ArrayBoundMatch extends ArrayBoundComparable<String> { }
    abstract static class ArrayBoundWrong extends ArrayBoundComparable<Integer> { }
    abstract static class ArrayObjectBound<T> extends Box<List<T>[]> implements Comparable<Box<? extends Object>> { }
    abstract static class ArrayObjectMatch extends ArrayObjectBound<String> { }
    static class GenericMember<T> { Value<T> value; }
    static class ConcreteMember extends GenericMember<String> { }
    abstract static class SelfMember<T> implements Comparable<SelfMember<T>> { SelfMember<List<T>> nested; }
    abstract static class ConcreteSelfMember extends SelfMember<String> { }
    static class TypeDeclarations<T> {
        Value<String> value;
        GenericNumber<String> number;
        Value<List<?>> wildcardValue;
        GenericNumber<List<?>> wildcardNumber;
        GenericNumber<List<? extends Number>> boundedWildcardNumber;
        GenericNumber<List<? super Integer>> lowerWildcardNumber;
        NestedNumber<List<Integer>> wrongNestedNumber;
        NestedNumber<List<? extends Number>> exactNestedNumber;
        WildcardNumber<Integer> numberWithWildcardSupertype;
        GenericNumber<?> directWildcardNumber;
        Value<?> directWildcardValue;
        Value<? extends Number> directBoundedWildcardValue;
        FixedComparable<?> fixedComparableWildcard;
        WrongValue<String> wrong;
        @SuppressWarnings("rawtypes") Value rawValue;
        Value<T> unresolvedValue;
        Value<? extends T> unresolvedWildcard;
        Outer<String>.Inner<Integer> owner;
        Outer<Integer>.Inner<Integer> differentOwner;
        Outer<String>.WrongInner<Integer> wrongOwner;
        ArrayMatch array;
        ArrayWrong wrongArray;
        NestedArrayMatch nestedArray;
        NestedArrayWrong wrongNestedArray;
        ArrayBoundMatch arrayBound;
        ArrayBoundWrong wrongArrayBound;
        ArrayObjectMatch arrayObjectBound;
        List<String>[] parameterizedArray;
        List<? extends Number> wildcard;
        List<? super String> lowerWildcard;
    }

    enum SampleEnum { FIRST, SECOND }

    abstract static class SelfNumber extends Number implements Comparable<SelfNumber> { }
    abstract static class ChildNumber extends SelfNumber { }
    abstract static class WrongNumber extends Number implements Comparable<String> { }
    abstract static class PlainTemporal implements Temporal { }
    abstract static class GoodTemporal extends PlainTemporal implements Comparable<GoodTemporal> { }
    abstract static class WrongTemporal extends PlainTemporal implements Comparable<String> { }
    abstract static class GenericComparable<T> implements Comparable<T> { }
    abstract static class SelfGeneric extends GenericComparable<SelfGeneric> { }
    @SuppressWarnings("rawtypes")
    abstract static class RawComparable implements Comparable { }
    static class Box<T> { }
    abstract static class BoxComparableChild extends Box<String> implements Comparable<Box<String>> { }
    abstract static class WrongBoxChild extends Box<Integer> implements Comparable<Box<String>> { }
    abstract static class GenericBox<T> implements Comparable<GenericBox<T>> { }
    abstract static class InheritedBoxChild extends GenericBox<String> { }
    abstract static class NestedBoxChild extends Box<List<String>> implements Comparable<Box<List<String>>> { }
    abstract static class WrongNestedBoxChild extends Box<List<Integer>> implements Comparable<Box<List<String>>> { }
    abstract static class WildBoxChild extends Box<String> implements Comparable<Box<? extends CharSequence>> { }
    abstract static class WrongWildBoxChild extends Box<Integer> implements Comparable<Box<? extends CharSequence>> { }
    abstract static class WildNestedBoxChild extends Box<List<String>> implements Comparable<Box<?>> { }
    abstract static class BoundedNestedBoxChild extends Box<List<String>> implements Comparable<Box<? extends Collection<String>>> { }
    abstract static class WrongBoundedNestedBoxChild extends Box<List<Integer>> implements Comparable<Box<? extends Collection<String>>> { }
    abstract static class LowerBoundBoxChild extends Box<CharSequence> implements Comparable<Box<? super String>> { }
    abstract static class WrongLowerBoundBoxChild extends Box<Integer> implements Comparable<Box<? super String>> { }
    abstract static class IdenticalWildcard extends Box<List<?>> implements Comparable<Box<List<?>>> { }
    abstract static class ActualWildcard extends Box<List<? extends Integer>>
            implements Comparable<Box<? extends Collection<? extends Number>>> { }
    abstract static class WrongActualWildcard extends Box<List<? extends String>>
            implements Comparable<Box<? extends Collection<? extends Number>>> { }
    abstract static class InvariantMismatch extends Box<List<Integer>>
            implements Comparable<Box<List<? extends Number>>> { }
    abstract static class ActualLowerWildcard extends Box<List<? super Number>>
            implements Comparable<Box<? extends Collection<? super Integer>>> { }
    abstract static class WrongActualLowerWildcard extends Box<List<? super Integer>>
            implements Comparable<Box<? extends Collection<? super Number>>> { }
    abstract static class UnboundedActualWildcard extends Box<List<?>>
            implements Comparable<Box<? extends Collection<?>>> { }
    abstract static class WrongUnboundedActualWildcard extends Box<List<?>>
            implements Comparable<Box<? extends Collection<? super Integer>>> { }
}
