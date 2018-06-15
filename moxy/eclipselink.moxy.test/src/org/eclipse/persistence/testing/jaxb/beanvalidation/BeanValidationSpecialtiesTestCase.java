/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Marcel Valovy - 2.6 - initial implementation
package org.eclipse.persistence.testing.jaxb.beanvalidation;

import org.eclipse.persistence.exceptions.BeanValidationException;
import org.eclipse.persistence.jaxb.BeanValidationHelper;
import org.eclipse.persistence.jaxb.ConstraintViolationWrapper;
import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.JAXBMarshaller;
import org.eclipse.persistence.testing.jaxb.beanvalidation.dom.Employee;
import org.eclipse.persistence.testing.jaxb.beanvalidation.special.ConstructorAnnotatedEmployee;
import org.eclipse.persistence.testing.jaxb.beanvalidation.special.CustomAnnotatedEmployee;
import org.eclipse.persistence.testing.jaxb.beanvalidation.special.InheritanceAnnotatedEmployee;
import org.eclipse.persistence.testing.jaxb.beanvalidation.special.MethodAnnotatedEmployee;
import org.eclipse.persistence.testing.jaxb.beanvalidation.special.NonConstrainedClass;

import javax.validation.ClockProvider;
import javax.validation.ConstraintValidatorFactory;
import javax.validation.ConstraintViolation;
import javax.validation.MessageInterpolator;
import javax.validation.ParameterNameProvider;
import javax.validation.Path;
import javax.validation.TraversableResolver;
import javax.validation.Validator;
import javax.validation.ValidatorContext;
import javax.validation.ValidatorFactory;
import javax.validation.executable.ExecutableValidator;
import javax.validation.metadata.BeanDescriptor;
import javax.validation.metadata.ConstraintDescriptor;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.time.Clock;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static org.eclipse.persistence.testing.jaxb.beanvalidation.ContentComparator.equalsXML;

/**
 * Test case storing non-standard tests, i.e. those that didn't fit neither in
 * {@link org.eclipse.persistence.testing.jaxb.beanvalidation.BeanValidationRuntimeTestCase} nor in
 * {@link org.eclipse.persistence.testing.jaxb.beanvalidation.BeanValidationBindingsTestCase}.
 *
 * @author Marcel Valovy - marcel.valovy@oracle.com
 */
public class BeanValidationSpecialtiesTestCase extends junit.framework.TestCase {

    private static final String NOT_NULL_MESSAGE = "{javax.validation.constraints.NotNull.message}";
    private static final String CUSTOM_ANNOTATION_MESSAGE = "{org.eclipse.persistence.moxy.CustomAnnotation.message}";

    private static final String GENERATOR_SCHEMA_WITH_FACETS =
            "org/eclipse/persistence/testing/jaxb/beanvalidation/generator/schema_with_facets.xsd";
    private static final String GENERATOR_SCHEMA_SUFFIX = "schema.xsd";
    private static String GENERATOR_SCHEMA;

    public void testGenerator() throws Exception {

        try {

            Map<String, Object> props = new HashMap<>();
            props.put(JAXBContextProperties.BEAN_VALIDATION_FACETS, true);
            javax.xml.bind.JAXBContext jaxbContext = JAXBContextFactory.createContext(new Class[] {Employee.class}, props);

            String generatorSchemaWithFacetsPath = Thread.currentThread().getContextClassLoader().getResource(GENERATOR_SCHEMA_WITH_FACETS).getPath();
            GENERATOR_SCHEMA = generatorSchemaWithFacetsPath.substring(0, generatorSchemaWithFacetsPath.lastIndexOf('/') + 1) + GENERATOR_SCHEMA_SUFFIX;

            SchemaOutputResolver sor = new MySchemaOutputResolver();
            jaxbContext.generateSchema(sor);
            assertTrue(equalsXML(new File(generatorSchemaWithFacetsPath), new File(GENERATOR_SCHEMA)));

        } finally {
            if(GENERATOR_SCHEMA!=null) {
                File generatorSchema = new File(GENERATOR_SCHEMA);
                if(generatorSchema.exists()) {
                    assertTrue("Generated schema '" + GENERATOR_SCHEMA + "' was not deleted properly. DO that manually.", generatorSchema.delete());
                }
            }
        }

    }

    /**
     * Tests that we do not skip validation on classes that do not have any bean validation annotations but have a
     * custom validation annotation.
     */
    public void testCustomAnnotations() throws Exception {
        JAXBMarshaller marshaller = (JAXBMarshaller) JAXBContextFactory.createContext(new
                Class[]{CustomAnnotatedEmployee.class}, null).createMarshaller();
        CustomAnnotatedEmployee employee = new CustomAnnotatedEmployee().withId(0xCAFEBABE);

        try {
            marshaller.marshal(employee, new StringWriter());
            assertFalse("Constraints-breaking class escaped validation -> fail.", true);
        } catch (BeanValidationException ignored) {
        }

        Set<ConstraintViolationWrapper<Object>> violations = marshaller.getConstraintViolations();

        assertFalse("Some constraints were not validated, even though they should have been.", violations.isEmpty());

        // For all, i.e. one constraintViolations.
        for (ConstraintViolationWrapper cv : violations) {
            assertEquals(CUSTOM_ANNOTATION_MESSAGE, cv.getMessage());
        }
    }

    /**
     * Tests that we do not skip validation on classes that do not have any bean validation annotations on fields but
     * have some on methods.
     */
    public void testMethodAnnotations() throws Exception {
        JAXBMarshaller marshaller = (JAXBMarshaller) JAXBContextFactory.createContext(new
                Class[]{MethodAnnotatedEmployee.class}, null).createMarshaller();
        MethodAnnotatedEmployee employee = new MethodAnnotatedEmployee().withId(null);

        try {
            marshaller.marshal(employee, new StringWriter());
            assertFalse("Constraints-breaking class escaped validation -> fail.", true);
        } catch (BeanValidationException ignored) {
        }

        Set<ConstraintViolationWrapper<Object>> violations = marshaller.getConstraintViolations();

        assertFalse(violations.isEmpty());

        // For all, i.e. one constraintViolations.
        for (ConstraintViolationWrapper cv : violations) {
            assertEquals(NOT_NULL_MESSAGE, cv.getMessageTemplate());
        }
    }

    /**
     * Tests that we detect inherited constraints.
     */
    public void testInheritedAnnotations() throws Exception {
        JAXBMarshaller marshaller = (JAXBMarshaller) JAXBContextFactory.createContext(new
                Class[]{InheritanceAnnotatedEmployee.class}, null).createMarshaller();

        InheritanceAnnotatedEmployee employee = (InheritanceAnnotatedEmployee) new InheritanceAnnotatedEmployee()
                .withId(null);

        try {
            marshaller.marshal(employee, new StringWriter());
            assertFalse("Constraints-breaking class escaped validation -> fail.", true);
        } catch (BeanValidationException ignored) {
        }

        Set<ConstraintViolationWrapper<Object>> violations = marshaller.getConstraintViolations();

        assertFalse(violations.isEmpty());

        // For all, i.e. one constraintViolations.
        for (ConstraintViolationWrapper cv : violations) {
            assertEquals(NOT_NULL_MESSAGE, cv.getMessageTemplate());
        }
    }

    /**
     * Tests that we do not skip validation on classes that do not have any bean validation annotations on fields or
     * methods but have some on constructors.
     */
    public void testConstructorAnnotations() throws Exception {
        JAXBContext context = (JAXBContext)JAXBContextFactory.createContext(new Class[]{ConstructorAnnotatedEmployee.class}, null);
        JAXBMarshaller marshaller = context.createMarshaller();

        ConstructorAnnotatedEmployee employee = new ConstructorAnnotatedEmployee(null);

        try {
            marshaller.marshal(employee, new StringWriter());
        } catch (BeanValidationException ignored) {
        }

        // Ok, HV is not picking up constraints on constructor. But that does not mean anything. Our job is to ensure
        // that we correctly identify that the class is constrained and pass the object to the underlying BV impl.
        BeanValidationHelper beanValidationHelper = context.getBeanValidationHelper();
        assertTrue(beanValidationHelper.getConstraintsMap().containsKey(ConstructorAnnotatedEmployee.class));

        // This will not detect the constraints violation on constructor (on HV 5.1), although it should.
//        Set<? extends ConstraintViolation<?>> violations = marshaller.getConstraintViolations();
//
//        assertFalse(violations.isEmpty());
//
//        // For all, i.e. one constraintViolations.
//        for (ConstraintViolation constraintViolation : violations) {
//            assertEquals(NOT_NULL_MESSAGE, constraintViolation.getMessageTemplate());
//        }
    }

    /**
     * Tests {@link org.eclipse.persistence.jaxb.JAXBContextProperties#BEAN_VALIDATION_NO_OPTIMISATION} property.
     */
    public void testNoOptimisationOption() throws Exception {
        JAXBMarshaller marshaller = (JAXBMarshaller) JAXBContextFactory.createContext(
                new Class[]{ NonConstrainedClass.class },
                new HashMap<String, Object>(){{
                    put(JAXBContextProperties.BEAN_VALIDATION_NO_OPTIMISATION, true);
                    put(JAXBContextProperties.BEAN_VALIDATION_FACTORY, new CustomValidatorFactory());
                }})
                .createMarshaller();

        try {
            marshaller.marshal( new NonConstrainedClass(), new StringWriter());
        } catch (BeanValidationException ignored) {
        }

        Set<ConstraintViolationWrapper<Object>> violations = marshaller.getConstraintViolations();

        assertFalse(violations.isEmpty());
    }

    private static class MySchemaOutputResolver extends SchemaOutputResolver {

        @Override
        public Result createOutput(String uri, String suggestedFileName) throws IOException {
            File file = new File(GENERATOR_SCHEMA);
            StreamResult result = new StreamResult(file);
            result.setSystemId(file.toURI().toURL().toString());
            return result;
        }

    }

    /**
     * Validator factory which returns {@code false} from method {@code validate()} in provided {@code Validator}.
     */
    private class CustomValidatorFactory implements ValidatorFactory {

        @Override
        public Validator getValidator() {
            return new Validator() {
                @Override
                public <T> Set<ConstraintViolation<T>> validate(T t, Class<?>... classes) {
                    return new HashSet<ConstraintViolation<T>>(){
                        { this.add(new ConstraintViolation<T>() {
                            @Override
                            public String getMessage() {
                                return "";
                            }

                            @Override
                            public String getMessageTemplate() {
                                return null;
                            }

                            @Override
                            public T getRootBean() {
                                //noinspection unchecked
                                return (T) new Object();
                            }

                            @Override
                            public Class<T> getRootBeanClass() {
                                return null;
                            }

                            @Override
                            public Object getLeafBean() {
                                return null;
                            }

                            @Override
                            public Object[] getExecutableParameters() {
                                return new Object[0];
                            }

                            @Override
                            public Object getExecutableReturnValue() {
                                return null;
                            }

                            @Override
                            public Path getPropertyPath() {
                                return new Path() {
                                    @Override
                                    public Iterator<Node> iterator() {
                                        return null;
                                    }
                                };
                            }

                            @Override
                            public Object getInvalidValue() {
                                return null;
                            }

                            @Override
                            public ConstraintDescriptor<?> getConstraintDescriptor() {
                                return null;
                            }

                            @Override
                            public <U> U unwrap(Class<U> type) {
                                return null;
                            }
                        }); }
                    };
                }

                @Override
                public <T> Set<ConstraintViolation<T>> validateProperty(T t, String s, Class<?>... classes) {
                    return null;
                }

                @Override
                public <T> Set<ConstraintViolation<T>> validateValue(Class<T> tClass, String s, Object o, Class<?>... classes) {
                    return null;
                }

                @Override
                public BeanDescriptor getConstraintsForClass(Class<?> aClass) {
                    return null;
                }

                @Override
                public <T> T unwrap(Class<T> tClass) {
                    return null;
                }

                @Override
                public ExecutableValidator forExecutables() {
                    return null;
                }
            };
        }

        @Override
        public ValidatorContext usingContext() {
            return null;
        }

        @Override
        public MessageInterpolator getMessageInterpolator() {
            return null;
        }

        @Override
        public TraversableResolver getTraversableResolver() {
            return null;
        }

        @Override
        public ConstraintValidatorFactory getConstraintValidatorFactory() {
            return null;
        }

        @Override
        public ParameterNameProvider getParameterNameProvider() {
            return null;
        }

        @Override
        public <T> T unwrap(Class<T> tClass) {
            return null;
        }

        @Override
        public void close() {

        }

        public ClockProvider getClockProvider() {
            return new ClockProvider() {
                public Clock getClock() {
                    return Clock.systemUTC();
                }
            };
        }
    }
}
