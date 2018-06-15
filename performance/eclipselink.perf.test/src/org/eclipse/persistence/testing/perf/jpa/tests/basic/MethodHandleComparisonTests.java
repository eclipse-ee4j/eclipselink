/*
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//              Petros Splinakis - initial implementation
package org.eclipse.persistence.testing.perf.jpa.tests.basic;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * Benchmarks for assessing performance improvement with usage of MethodHandle over java.lang.Method/Field.
 *
 * @author Petros Splinakis
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class MethodHandleComparisonTests {
    private static final class TestClass {
        public Object object;

        public Object getObject() {
            return object;
        }

        public void setObject(Object object) {
            this.object = object;
        }
    }

    // Test objects
    private TestClass testClass;
    private Object object;
    private static TestClass staticTestClass;
    private static Object staticObject;
    private static final TestClass staticFinalTestClass;
    private static final Object staticFinalObject;

    // Field access
    private Field field;
    private MethodHandle unreflectGetter;
    private MethodHandle unreflectSetter;
    private static Field staticField;
    private static MethodHandle staticUnreflectGetter;
    private static MethodHandle staticUnreflectSetter;
    private static final Field staticFinalField;
    private static final MethodHandle staticFinalUnreflectGetter;
    private static final MethodHandle staticFinalUnreflectSetter;

    // Accessor method access
    private Method getter;
    private Method setter;
    private MethodHandle getterHandle;
    private MethodHandle setterHandle;
    private static Method staticGetter;
    private static Method staticSetter;
    private static MethodHandle staticGetterHandle;
    private static MethodHandle staticSetterHandle;
    private static final Method staticFinalGetter;
    private static final Method staticFinalSetter;
    private static final MethodHandle staticFinalGetterHandle;
    private static final MethodHandle staticFinalSetterHandle;

    // Static Setup
    static {
        staticTestClass = new TestClass();
        staticObject = new Object();
        staticFinalTestClass = staticTestClass;
        staticFinalObject = staticObject;

        MethodHandles.Lookup lookup = MethodHandles.lookup();

        try {
            // Direct field access
            staticField = TestClass.class.getField("object");
            staticUnreflectGetter = lookup.unreflectGetter(staticField);
            staticUnreflectSetter = lookup.unreflectSetter(staticField);
            staticFinalField = staticField;
            staticFinalUnreflectGetter = staticUnreflectGetter;
            staticFinalUnreflectSetter = staticUnreflectSetter;

            // Accessor method access
            staticGetter = TestClass.class.getMethod("getObject");
            staticSetter = TestClass.class.getMethod("setObject", Object.class);
            staticGetterHandle = lookup.unreflect(staticGetter);
            staticSetterHandle = lookup.unreflect(staticSetter);
            staticFinalGetter = staticGetter;
            staticFinalSetter = staticSetter;
            staticFinalGetterHandle = staticGetterHandle;
            staticFinalSetterHandle = staticSetterHandle;
        }
        catch (Throwable t) {
            throw new IllegalStateException(t);
        }
    }

    @Setup
    public void setup() {
        testClass = staticTestClass;
        object = staticObject;

        field = staticField;
        unreflectGetter = staticUnreflectGetter;
        unreflectSetter = staticUnreflectSetter;

        getter = staticGetter;
        setter = staticSetter;
        getterHandle = staticGetterHandle;
        setterHandle = staticSetterHandle;
    }

    /**************************************
     * Test using method local attributes *
     **************************************/

    @Benchmark
    public void testDynamicGetValueField(Blackhole blackhole) throws Exception {
        blackhole.consume(testClass.object);
    }

    @Benchmark
    public void testDynamicGetValueFieldReflection(Blackhole blackhole) throws Exception {
        blackhole.consume(TestClass.class.getField("object").get(testClass));
    }

    @Benchmark
    public void testDynamicGetValueFieldMethodHandleReflection(Blackhole blackhole) throws Throwable {
        blackhole.consume(MethodHandles.lookup().unreflectGetter(TestClass.class.getField("object")).invoke(testClass));
    }

    @Benchmark
    public void testDynamicGetValueFieldMethodHandleReflectionExact(Blackhole blackhole) throws Throwable {
        blackhole.consume(MethodHandles.lookup().unreflectGetter(TestClass.class.getField("object")).invokeExact(testClass));
    }

    @Benchmark
    public void testDynamicGetValueFieldMethodHandle(Blackhole blackhole) throws Throwable {
        blackhole.consume(MethodHandles.lookup().findGetter(TestClass.class, "object", Object.class).invoke(testClass));
    }

    @Benchmark
    public void testDynamicGetValueFieldMethodHandleExact(Blackhole blackhole) throws Throwable {
        blackhole.consume(MethodHandles.lookup().findGetter(TestClass.class, "object", Object.class).invokeExact(testClass));
    }

    @Benchmark
    public void testDynamicSetValueField() throws Exception {
        testClass.object = object;
    }

    @Benchmark
    public void testDynamicSetValueFieldReflection() throws Exception {
        TestClass.class.getField("object").set(testClass, object);
    }

    @Benchmark
    public void testDynamicSetValueFieldMethodHandleReflection() throws Throwable {
        MethodHandles.lookup().unreflectSetter(TestClass.class.getField("object")).invoke(testClass, object);
    }

    @Benchmark
    public void testDynamicSetValueFieldMethodHandleReflectionExact() throws Throwable {
        MethodHandles.lookup().unreflectSetter(TestClass.class.getField("object")).invokeExact(testClass, object);
    }

    @Benchmark
    public void testDynamicSetValueFieldMethodHandle() throws Throwable {
        MethodHandles.lookup().findSetter(TestClass.class, "object", Object.class).invoke(testClass, object);
    }

    @Benchmark
    public void testDynamicSetValueFieldMethodHandleExact() throws Throwable {
        MethodHandles.lookup().findSetter(TestClass.class, "object", Object.class).invokeExact(testClass, object);
    }

    @Benchmark
    public void testDynamicGetValueMethod(Blackhole blackhole) throws Exception {
        blackhole.consume(testClass.getObject());
    }

    @Benchmark
    public void testDynamicGetValueMethodReflection(Blackhole blackhole) throws Exception {
        blackhole.consume(TestClass.class.getMethod("getObject").invoke(testClass));
    }

    @Benchmark
    public void testDynamicGetValueMethodMethodHandleReflection(Blackhole blackhole) throws Throwable {
        blackhole.consume(MethodHandles.lookup().unreflect(TestClass.class.getMethod("getObject")).invoke(testClass));
    }

    @Benchmark
    public void testDynamicGetValueMethodMethodHandleReflectionExact(Blackhole blackhole) throws Throwable {
        blackhole.consume(MethodHandles.lookup().unreflect(TestClass.class.getMethod("getObject")).invokeExact(testClass));
    }

    @Benchmark
    public void testDynamicGetValueMethodMethodHandle(Blackhole blackhole) throws Throwable {
        blackhole.consume(MethodHandles.lookup().findVirtual(TestClass.class, "getObject", MethodType.methodType(Object.class)).invoke(testClass));
    }

    @Benchmark
    public void testDynamicGetValueMethodMethodHandleExact(Blackhole blackhole) throws Throwable {
        blackhole.consume(MethodHandles.lookup().findVirtual(TestClass.class, "getObject", MethodType.methodType(Object.class)).invokeExact(testClass));
    }

    @Benchmark
    public void testDynamicSetValueMethod() throws Exception {
        testClass.setObject(object);
    }

    @Benchmark
    public void testDynamicSetValueMethodReflection() throws Exception {
        TestClass.class.getMethod("setObject").invoke(testClass, object);
    }

    @Benchmark
    public void testDynamicSetValueMethodMethodHandleReflection() throws Throwable {
        MethodHandles.lookup().unreflect(TestClass.class.getMethod("setObject")).invoke(testClass, object);
    }

    @Benchmark
    public void testDynamicSetValueMethodMethodHandleReflectionExact() throws Throwable {
        MethodHandles.lookup().unreflect(TestClass.class.getMethod("setObject")).invokeExact(testClass, object);
    }

    @Benchmark
    public void testDynamicSetValueMethodMethodHandle() throws Throwable {
        MethodHandles.lookup().findVirtual(TestClass.class, "setObject", MethodType.methodType(Void.class, Object.class)).invoke(testClass, object);
    }

    @Benchmark
    public void testDynamicSetValueMethodMethodHandleExact() throws Throwable {
        MethodHandles.lookup().findVirtual(TestClass.class, "setObject", MethodType.methodType(Void.class, Object.class)).invokeExact(testClass, object);
    }

    /**********************************
     * Test using instance attributes *
     **********************************/

    @Benchmark
    public void testGetValueField(Blackhole blackhole) throws Exception {
        blackhole.consume(testClass.object);
    }

    @Benchmark
    public void testGetValueFieldReflection(Blackhole blackhole) throws Exception {
        blackhole.consume(field.get(testClass));
    }

    @Benchmark
    public void testGetValueFieldMethodHandle(Blackhole blackhole) throws Throwable {
        blackhole.consume(unreflectGetter.invoke(testClass));
    }

    @Benchmark
    public void testGetValueFieldMethodHandleExact(Blackhole blackhole) throws Throwable {
        blackhole.consume(unreflectGetter.invokeExact(testClass));
    }

    @Benchmark
    public void testSetValueField() throws Exception {
        testClass.object = object;
    }

    @Benchmark
    public void testSetValueFieldReflection() throws Exception {
        field.set(testClass, object);
    }

    @Benchmark
    public void testSetValueFieldMethodHandle() throws Throwable {
        unreflectSetter.invoke(testClass, object);
    }

    @Benchmark
    public void testSetValueFieldMethodHandleExact() throws Throwable {
        unreflectSetter.invokeExact(testClass, object);
    }

    @Benchmark
    public void testGetValueMethod(Blackhole blackhole) throws Exception {
        blackhole.consume(testClass.getObject());
    }

    @Benchmark
    public void testGetValueMethodReflection(Blackhole blackhole) throws Exception {
        blackhole.consume(getter.invoke(testClass));
    }

    @Benchmark
    public void testGetValueMethodMethodHandle(Blackhole blackhole) throws Throwable {
        blackhole.consume(getterHandle.invoke(testClass));
    }

    @Benchmark
    public void testGetValueMethodMethodHandleExact(Blackhole blackhole) throws Throwable {
        blackhole.consume(getterHandle.invokeExact(testClass));
    }

    @Benchmark
    public void testSetValueMethod() throws Exception {
        testClass.setObject(object);
    }

    @Benchmark
    public void testSetValueMethodReflection() throws Exception {
        setter.invoke(testClass, object);
    }

    @Benchmark
    public void testSetValueMethodMethodHandle() throws Throwable {
        setterHandle.invoke(testClass, object);
    }

    @Benchmark
    public void testSetValueMethodMethodHandleExact() throws Throwable {
        setterHandle.invokeExact(testClass, object);
    }

    /********************************
     * Test using static attributes *
     ********************************/

    @Benchmark
    public void testStaticGetValueField(Blackhole blackhole) throws Exception {
        blackhole.consume(staticTestClass.object);
    }

    @Benchmark
    public void testStaticGetValueFieldReflection(Blackhole blackhole) throws Exception {
        blackhole.consume(staticField.get(staticTestClass));
    }

    @Benchmark
    public void testStaticGetValueFieldMethodHandle(Blackhole blackhole) throws Throwable {
        blackhole.consume(staticUnreflectGetter.invoke(staticTestClass));
    }

    @Benchmark
    public void testStaticGetValueFieldMethodHandleExact(Blackhole blackhole) throws Throwable {
        blackhole.consume(staticUnreflectGetter.invokeExact(staticTestClass));
    }

    @Benchmark
    public void testStaticSetValueField() throws Exception {
        staticTestClass.object = staticObject;
    }

    @Benchmark
    public void testStaticSetValueFieldReflection() throws Exception {
        staticField.set(staticTestClass, staticObject);
    }

    @Benchmark
    public void testStaticSetValueFieldMethodHandle() throws Throwable {
        staticUnreflectSetter.invoke(staticTestClass, staticObject);
    }

    @Benchmark
    public void testStaticSetValueFieldMethodHandleExact() throws Throwable {
        staticUnreflectSetter.invokeExact(staticTestClass, staticObject);
    }

    @Benchmark
    public void testStaticGetValueMethod(Blackhole blackhole) throws Exception {
        blackhole.consume(staticTestClass.getObject());
    }

    @Benchmark
    public void testStaticGetValueMethodReflection(Blackhole blackhole) throws Exception {
        blackhole.consume(staticGetter.invoke(staticTestClass));
    }

    @Benchmark
    public void testStaticGetValueMethodMethodHandle(Blackhole blackhole) throws Throwable {
        blackhole.consume(staticGetterHandle.invoke(staticTestClass));
    }

    @Benchmark
    public void testStaticGetValueMethodMethodHandleExact(Blackhole blackhole) throws Throwable {
        blackhole.consume(staticGetterHandle.invokeExact(staticTestClass));
    }

    @Benchmark
    public void testStaticSetValueMethod() throws Exception {
        staticTestClass.setObject(staticObject);
    }

    @Benchmark
    public void testStaticSetValueMethodReflection() throws Exception {
        staticSetter.invoke(staticTestClass, staticObject);
    }

    @Benchmark
    public void testStaticSetValueMethodMethodHandle() throws Throwable {
        staticSetterHandle.invoke(staticTestClass, staticObject);
    }

    @Benchmark
    public void testStaticSetValueMethodMethodHandleExact() throws Throwable {
        staticSetterHandle.invokeExact(staticTestClass, staticObject);
    }

    /**************************************
     * Test using static final attributes *
     **************************************/

    @Benchmark
    public void testStaticFinalGetValueField(Blackhole blackhole) throws Exception {
        blackhole.consume(staticFinalTestClass.object);
    }

    @Benchmark
    public void testStaticFinalGetValueFieldReflection(Blackhole blackhole) throws Exception {
        blackhole.consume(staticFinalField.get(staticFinalTestClass));
    }

    @Benchmark
    public void testStaticFinalGetValueFieldMethodHandle(Blackhole blackhole) throws Throwable {
        blackhole.consume(staticFinalUnreflectGetter.invoke(staticFinalTestClass));
    }

    @Benchmark
    public void testStaticFinalGetValueFieldMethodHandleExact(Blackhole blackhole) throws Throwable {
        blackhole.consume(staticFinalUnreflectGetter.invokeExact(staticFinalTestClass));
    }

    @Benchmark
    public void testStaticFinalSetValueField() throws Exception {
        staticFinalTestClass.object = staticFinalObject;
    }

    @Benchmark
    public void testStaticFinalSetValueFieldReflection() throws Exception {
        staticFinalField.set(staticFinalTestClass, staticFinalObject);
    }

    @Benchmark
    public void testStaticFinalSetValueFieldMethodHandle() throws Throwable {
        staticFinalUnreflectSetter.invoke(staticFinalTestClass, staticFinalObject);
    }

    @Benchmark
    public void testStaticFinalSetValueFieldMethodHandleExact() throws Throwable {
        staticFinalUnreflectSetter.invokeExact(staticFinalTestClass, staticFinalObject);
    }

    @Benchmark
    public void testStaticFinalGetValueMethod(Blackhole blackhole) throws Exception {
        blackhole.consume(staticFinalTestClass.getObject());
    }

    @Benchmark
    public void testStaticFinalGetValueMethodReflection(Blackhole blackhole) throws Exception {
        blackhole.consume(staticFinalGetter.invoke(staticFinalTestClass));
    }

    @Benchmark
    public void testStaticFinalGetValueMethodMethodHandle(Blackhole blackhole) throws Throwable {
        blackhole.consume(staticFinalGetterHandle.invoke(staticFinalTestClass));
    }

    @Benchmark
    public void testStaticFinalGetValueMethodMethodHandleExact(Blackhole blackhole) throws Throwable {
        blackhole.consume(staticFinalGetterHandle.invokeExact(staticFinalTestClass));
    }

    @Benchmark
    public void testStaticFinalSetValueMethod() throws Exception {
        staticFinalTestClass.setObject(staticFinalObject);
    }

    @Benchmark
    public void testStaticFinalSetValueMethodReflection() throws Exception {
        staticFinalSetter.invoke(staticFinalTestClass, staticFinalObject);
    }

    @Benchmark
    public void testStaticFinalSetValueMethodMethodHandle() throws Throwable {
        staticFinalSetterHandle.invoke(staticFinalTestClass, staticFinalObject);
    }

    @Benchmark
    public void testStaticFinalSetValueMethodMethodHandleExact() throws Throwable {
        staticFinalSetterHandle.invokeExact(staticFinalTestClass, staticFinalObject);
    }

}
