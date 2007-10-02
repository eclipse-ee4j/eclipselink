/*******************************************************************************
 * Copyright (c) 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - purpose: extended JUnit4 testing for Oracle TopLink
 ******************************************************************************/

package junit.extensions.pb4;

// javase imports
import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;

// JUnit imports
import org.junit.*;
import org.junit.internal.runners.*;

public class PB4MethodValidator {

    protected TestIntrospector introspector;
    protected List<Throwable> errors = new ArrayList<Throwable>();
    protected Class<?> testClass;

    public PB4MethodValidator(Class<?> testClass) {
        setIntrospector(new TestIntrospector(testClass));
        setTestClass(testClass);
    }

    public void assertValid() throws InitializationError {
        if (!getErrors().isEmpty())
            throw new InitializationError(getErrors());
    }

    public List<Throwable> validateAllMethods() {
        validateNoArgConstructor();
        validateStaticMethods();
        validateInstanceMethods();
        return getErrors();
    }

    public void validateNoArgConstructor() {
        try {
            getTestClass().getConstructor();
        } catch (Exception e) {
            getErrors().add(
                new Exception("Test class should have public zero-argument constructor", e));
        }
    }

    public void validateStaticMethods() {

        validateStaticTestMethod(BeforeClass.class);
        validateStaticTestMethod(AfterClass.class);
    }

    protected void validateStaticTestMethod(Class<? extends Annotation> annotation) {

        List<Method> methods = getIntrospector().getTestMethods(annotation);
        for (Method each : methods) {
            if (!Modifier.isStatic(each.getModifiers())) {
                getErrors().add(
                    new Exception("Method " + each.getName() + "() " + "should be static"));
            }
            if (!Modifier.isPublic(each.getModifiers()))
                getErrors().add(new Exception("Method " + each.getName() + " should be public"));
            if (each.getReturnType() != Void.TYPE)
                getErrors().add(new Exception("Method " + each.getName() + " should be void"));
            Class<?> parms[] = each.getParameterTypes();
            if (parms.length == 0) { // allow 'regular' JUnit4 static methods without Properties
                return;
            }
            if (parms.length != 1 || !parms[0].isAssignableFrom(Properties.class))
                getErrors().add(
                    new Exception("Method " + each.getName()
                        + " should have single Properties parameters"));
        }
    }

    public void validateInstanceMethods() {
        validateTestMethods(After.class, false);
        validateTestMethods(Before.class, false);
        validateTestMethods(Test.class, false);
        validateTestMethods(Ignore.class, false);
    }

    private void validateTestMethods(Class<? extends Annotation> annotation, boolean isStatic) {
        List<Method> methods = introspector.getTestMethods(annotation);
        for (Method each : methods) {
            if (Modifier.isStatic(each.getModifiers()) != isStatic) {
                String state = isStatic ? "should" : "should not";
                getErrors().add(
                    new Exception("Method " + each.getName() + "() " + state + " be static"));
            }
            if (!Modifier.isPublic(each.getModifiers()))
                getErrors().add(new Exception("Method " + each.getName() + " should be public"));
            if (each.getReturnType() != Void.TYPE)
                getErrors().add(new Exception("Method " + each.getName() + " should be void"));
            if (each.getParameterTypes().length != 0)
                getErrors().add(
                    new Exception("Method " + each.getName() + " should have no parameters"));
        }
    }

    protected List<Throwable> getErrors() {
        return errors;
    }

    protected void setErrors(List<Throwable> errors) {
        this.errors = errors;
    }

    protected TestIntrospector getIntrospector() {
        return introspector;
    }

    protected void setIntrospector(TestIntrospector introspector) {
        this.introspector = introspector;
    }

    protected Class<?> getTestClass() {
        return testClass;
    }

    protected void setTestClass(Class<?> testClass) {
        this.testClass = testClass;
    }

}
