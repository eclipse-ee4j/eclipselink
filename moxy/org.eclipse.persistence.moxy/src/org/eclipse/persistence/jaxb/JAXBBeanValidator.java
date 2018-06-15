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
package org.eclipse.persistence.jaxb;

import org.eclipse.persistence.exceptions.BeanValidationException;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.groups.Default;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.PrivilegedAction;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * INTERNAL:
 *
 * JAXB Bean Validator. Serves three purposes:
 *  1. Determines if the validation callback should take place on the (un)marshal call.
 *  2. Processes the validation.
 *  3. Stores the constraintViolations from the last validation call.
 *
 * @author Marcel Valovy - marcel.valovy@oracle.com
 * @since 2.6
 */
class JAXBBeanValidator {

    private static Logger logger =
            Logger.getLogger(JAXBBeanValidator.class.getName());

    /**
     * Represents the Default validation group. Storing it in constant saves resources.
     */
    static final Class<?>[] DEFAULT_GROUP_ARRAY = new Class<?>[] { Default.class };

    /**
     * Represents the difference between words 'marshalling' and 'unmarshalling';
     */
    private static final String PREFIX_UNMARSHALLING = "un";

    /**
     * Prevents endless invocation loops between unmarshaller - validator - unmarshaller.
     * Only used / needed in case {@link #noOptimisation} is {@code true}.
     */
    private static final ReentrantLock lock = new ReentrantLock();

    /**
     * Disable optimisations that skip bean validation processes on non-constrained objects.
     */
    private boolean noOptimisation = false;

    /**
     * Stores {@link #PREFIX_UNMARSHALLING} if this instance belongs to
     * {@link org.eclipse.persistence.jaxb.JAXBUnmarshaller}, otherwise stores empty String.
     */
    private final String prefix;

    /**
     * Reference to {@link org.eclipse.persistence.jaxb.JAXBContext}. Allows for callbacks.
     */
    private final JAXBContext context;

    /**
     * Stores the {@link javax.validation.Validator} implementation. Once found, the reference is preserved.
     */
    private Validator validator;

    /**
     * Stores constraint violations returned by last call to {@link javax.validation.Validator#validate(Object, Class[])}.
     * <p>After each {@link #validate(Object, Class[])} call, the reference is replaced.
     */
    private Set<ConstraintViolation<Object>> constraintViolations = Collections.emptySet();

    /**
     * Computed value saying if the validation can proceed under current conditions, represented by:
     * <blockquote><pre>
     *     - {@link #beanValidationMode}
     *     - {@link javax.validation.Validator} implementation present on classpath
     * </pre></blockquote>
     * <p>
     * Value is recomputed only on {@link #changeInternalState()} call.
     */
    private boolean canValidate;

    /**
     * Represents a state where {@link #beanValidationMode} mode is set to
     * {@link org.eclipse.persistence.jaxb.BeanValidationMode#AUTO} and BV implementation could not be found.
     */
    private boolean stopSearchingForValidator;

    /**
     * This field will usually be {@code null}. However, user may pass his own instance of
     * {@link javax.validation.ValidatorFactory} to
     * {@link #shouldValidate}() method, and it will be assigned to this field.
     * <p>
     * If not null, {@link #validator} field will be assigned only by calling method
     * {@link javax.validation.ValidatorFactory#getValidator()} the instance assigned to this field.
     */
    private ValidatorFactory validatorFactory;

    /**
     * Setting initial value to "NONE" will not trigger internalStateChange() when validation is off and save resources.
     */
    private BeanValidationMode beanValidationMode = BeanValidationMode.NONE;

    /**
     * Private constructor. Only to be called by factory methods.
     * @param prefix differentiates between marshaller and unmarshaller during logging
     * @param context jaxb context reference
     */
    private JAXBBeanValidator(String prefix, JAXBContext context) {
        this.prefix = prefix;
        this.context = context;
    }

    /**
     * Factory method.
     * <p>
     * The only difference between this method and {@link #getUnmarshallingBeanValidator} is not having
     * {@link #PREFIX_UNMARSHALLING} in String messages constructed for exceptions.
     *
     * @param context jaxb context reference
     * @return
     *          a new instance of {@link JAXBBeanValidator}.
     */
    static JAXBBeanValidator getMarshallingBeanValidator(JAXBContext context){
        return new JAXBBeanValidator("", context);
    }

    /**
     * Factory method.
     * <p>
     * The only difference between this method and {@link #getMarshallingBeanValidator} is having
     * {@link #PREFIX_UNMARSHALLING} in String messages constructed for exceptions.
     *
     * @param context jaxb context reference
     * @return
     *          a new instance of {@link JAXBBeanValidator}.
     */
    static JAXBBeanValidator getUnmarshallingBeanValidator(JAXBContext context){
        return new JAXBBeanValidator(PREFIX_UNMARSHALLING, context);
    }

    /**
     * PUBLIC:
     *
     * First, if validation has not been turned off before, check if passed value is constrained.
     *
     * Second, depending on Bean Validation Mode, either returns false or tries to initialize Validator:
     *  - AUTO tries to initialize Validator:
     *          returns true if succeeds, else false.
     *  - CALLBACK tries to initialize Validator:
     *          returns true if succeeds, else throws {@link BeanValidationException#providerNotFound}.
     *  - NONE returns false;
     *
     * BeanValidationMode is fetched from (un)marshaller upon each call.
     * If change in mode is detected, the internal state of the JAXBBeanValidator will be switched.
     *
     * Third, analyses the value and determines whether validation may be skipped.
     *
     * @param beanValidationMode Bean validation mode - allowed values AUTO, CALLBACK, NONE.
     * @param value validated object. It is passed because validation on some objects may be skipped,
     *              e.g. non-constrained objects (like XmlBindings).
     * @param preferredValidatorFactory Must be {@link ValidatorFactory} or null. Will use this factory as the
     *                                  preferred provider; if null, will use javax defaults.
     * @param noOptimisation if true, bean validation optimisations that skip non-constrained objects will not be
     *                       performed
     * @return
     *          true if should proceed with validation, else false.
     * @throws BeanValidationException
     *  {@link BeanValidationException#illegalValidationMode} or {@link BeanValidationException#providerNotFound}.
     * @since 2.6
     */
    boolean shouldValidate (Object value, BeanValidationMode beanValidationMode,
                            Object preferredValidatorFactory,
                            boolean noOptimisation) throws BeanValidationException {

        if (isValidationEffectivelyOff(beanValidationMode)) return false;

        this.noOptimisation = noOptimisation;

        if (!isConstrainedObject(value)) return false;

        /* Mode or validator factory was changed externally (or it's the first time this method is called). */
        if (this.beanValidationMode != beanValidationMode || this.validatorFactory != preferredValidatorFactory) {
            this.beanValidationMode = beanValidationMode;
            this.validatorFactory = (ValidatorFactory)preferredValidatorFactory;
            changeInternalState();
        }

        /* Is Validation implementation ready to validate. */
        return canValidate;
    }

    /**
     * Check if validation is effectively off, i.e. it was previously attempted to turn it on, but that failed.
     * @param beanValidationMode user passed beanValidationMode
     * @return true if validation is effectively off
     */
    private boolean isValidationEffectivelyOff(BeanValidationMode beanValidationMode) {
        return !  ((beanValidationMode == BeanValidationMode.AUTO && canValidate) /* most common case */
                || (beanValidationMode == BeanValidationMode.CALLBACK)
                /* beanValidationMode is AUTO but canValidate is yet to be resolved */
                || (beanValidationMode != BeanValidationMode.NONE && beanValidationMode != this.beanValidationMode)
        );
    }

    /**
     * Check if object contains any bean validation constraints or custom validation constraints.
     * @param value object
     * @return true if the object is not null and is constrained
     */
    private boolean isConstrainedObject(Object value) {
        /* Json is allowed to pass a null root object. Avoid NPE & speed things up. */
        if (value == null) return false;

        if (noOptimisation) {
            /* Stops the endless invocation loop which may occur when calling
             * Validation#buildDefaultValidatorFactory in a case when the user sets
             * custom validation configuration through "validation.xml" file and
             * the validation implementation tries to unmarshal the file with MOXy. */
            if (lock.isHeldByCurrentThread()) return false;

            /* Do not validate XmlBindings. */
            return !(value instanceof XmlBindings);
        }

        /* Ensure that the class contains BV annotations. If not, skip validation & speed things up.
         * note: This also effectively skips XmlBindings. */
        return context.getBeanValidationHelper().isConstrained(value.getClass());
    }

    /**
     * INTERNAL:
     *
     * Validates the value, as per BV spec.
     * Stores the result of validation in {@link #constraintViolations}.
     *
     * @param value Object to be validated.
     * @param groups Target groups as per BV spec. If null {@link #DEFAULT_GROUP_ARRAY} is used.
     * @throws BeanValidationException {@link BeanValidationException#constraintViolation}
     */
    void validate(Object value, Class<?>... groups) throws BeanValidationException {
        Class<?>[] grp = groups;
        if (grp == null || grp.length == 0) {
            grp = DEFAULT_GROUP_ARRAY;
        }
        constraintViolations = validator.validate(value, grp);
        if (!constraintViolations.isEmpty())
            throw buildConstraintViolationException();
    }

    /**
     * @return constraintViolations from the last {@link #validate} call.
     */
    Set<ConstraintViolationWrapper<Object>> getConstraintViolations() {
        Set<ConstraintViolationWrapper<Object>> result = new HashSet<>(constraintViolations.size());
        for (ConstraintViolation cv : constraintViolations) {
            result.add(new ConstraintViolationWrapper<>(cv));
        }
        return result;
    }

    /**
     * INTERNAL:
     *
     * Puts variables to states which conform to the internal state machine.
     *
     * Internal states:
     *  Mode/Field Value          | NONE        | AUTO         | CALLBACK
     *  --------------------------|-------------|--------------|--------------
     *  canValidate               | false       | true/false   | true/false
     *  stopSearchingForValidator | false       | true/false   | false
     *  constraintViolations      | EmptySet    | n/a          | n/a
     *
     *  n/a ... value is not altered.
     *
     * @throws BeanValidationException illegalValidationMode or providerNotFound
     */
    private void changeInternalState() throws BeanValidationException {
        stopSearchingForValidator = false; // Reset the switch.
        switch (beanValidationMode) {
        case NONE:
            canValidate = false;
            constraintViolations = Collections.emptySet(); // Clear the reference from previous (un)marshal calls.
            break;
        case CALLBACK:
        case AUTO:
            canValidate = initValidator();
            break;
        default:
            throw BeanValidationException.illegalValidationMode(prefix, beanValidationMode.toString());
        }
    }

    /**
     * PUBLIC:
     *
     * Initializes validator if not already initialized.
     * If mode is BeanValidationMode.AUTO, then after an unsuccessful try to
     * initialize a Validator, property {@code stopSearchingForValidator} will be set to true.
     *
     * NOTE: Property {@code stopSearchingForValidator} can be reset only by triggering
     * {@link #changeInternalState}.
     *
     * @return {@code true} if validator initialization succeeded, otherwise {@code false}.
     * @throws BeanValidationException
     *              throws {@link org.eclipse.persistence.exceptions.BeanValidationException#PROVIDER_NOT_FOUND}
     */
    private boolean initValidator() throws BeanValidationException {
        if (validator == null && !stopSearchingForValidator){
            try {
                ValidatorFactory factory = getValidatorFactory();
                validator = factory.getValidator();
                printValidatorInfo();
            } catch (ValidationException ve) {
                if (beanValidationMode == BeanValidationMode.CALLBACK){
                    /* The following line ensures that changeInternalState() will be the
                     triggered on next (un)marshalling trials if mode is still CALLBACK.
                      That will ensure searching for Validator implementation again. */
                    beanValidationMode = BeanValidationMode.AUTO;
                    throw BeanValidationException.providerNotFound(prefix, ve);
                } else { // mode AUTO
                    stopSearchingForValidator = true; // Will not try to initialize validator on next tries.
                }
            }
        }
        return validator != null;
    }

    /**
     * INTERNAL:
     *
     * @return Preferred ValidatorFactory if set, else {@link Validation#buildDefaultValidatorFactory()}.
     */
    private ValidatorFactory getValidatorFactory() {
        if (validatorFactory != null) {
            return validatorFactory;
        }

        if (noOptimisation) {
            lock.lock();
            try {
                return Validation.buildDefaultValidatorFactory();
            } finally {
                lock.unlock();
            }
        }

        return Validation.buildDefaultValidatorFactory();
    }

    /**
     * INTERNAL:
     *
     * Builds ConstraintViolationException with constraintViolations, but no message.
     * Builds BeanValidationException with fully descriptive message, containing
     * the ConstraintViolationException.
     *
     * @return BeanValidationException, containing ConstraintViolationException.
     */
    @SuppressWarnings({"RedundantCast", "unchecked"})
    private BeanValidationException buildConstraintViolationException() {
        ConstraintViolationException cve = new ConstraintViolationException(
                /* Do not remove the cast. */ constraintViolations);
        return BeanValidationException.constraintViolation(createConstraintViolationExceptionArgs(), cve);
    }

    /**
     * INTERNAL:
     * Builds an Object array containing args for ConstraintViolationException constructor.
     *
     * @return  [0] - prefix,
     *          [1] - rootBean (on what object the validation failed),
     *          [2] - linkedList of violatedConstraints, with overriden toString() for better formatting.
     */
    private Object[] createConstraintViolationExceptionArgs() {
        Object[] args = new Object[3];
        Iterator<? extends ConstraintViolation<?>> iterator = constraintViolations.iterator();
        assert iterator.hasNext(); // this method is to be called only if constraints violations are not empty
        ConstraintViolation<?> cv = iterator.next();
        Collection<ConstraintViolationInfo> violatedConstraints = new LinkedList<ConstraintViolationInfo>(){
            @Override
            public String toString() {
                Iterator<ConstraintViolationInfo> it = iterator();
                StringBuilder sb = new StringBuilder();
                while (it.hasNext())
                    sb.append("\n-->").append(it.next().toString());
                return sb.toString();
            }
        };
        args[0] = prefix;
        Object bean = cv.getRootBean();
        // NOTE:
        // 1. Do not use bean.toString(), it could leak secure information.
        // 2. And use identityHashCode, for these reasons:
        //      - prevents NPE which could be caused by a poorly implemented hashCode
        //      - serves as a better mean of identification of the bean.
        args[1] = bean.getClass().toString().substring("class ".length())
                + "@" + Integer.toHexString(System.identityHashCode(bean));
        args[2] = violatedConstraints;
        for (;;) {
            violatedConstraints.add(new ConstraintViolationInfo(cv.getMessage(), cv.getPropertyPath()));
            if (iterator.hasNext()) cv = iterator.next();
            else break;
        }
        return args;
    }

    /**
     * Logs the name of underlying validation impl jar used. Only logs once per context to avoid log cluttering.
     * To be called after successful assignment of validator.
     */
    private void printValidatorInfo() {
        if (!context.getHasLoggedValidatorInfo().getAndSet(true)) {
            CodeSource validationImplJar = getValidatorCodeSource();
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("EclipseLink is using " + validationImplJar + " as BeanValidation implementation.");
            }
        }
    }

    /**
     * INTERNAL:
     * Retrieves code source of validator.
     *
     * @return Validator code source. May be null.
     */
    private CodeSource getValidatorCodeSource() {
        if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
            return AccessController.doPrivileged(new PrivilegedAction<CodeSource>() {
                @Override
                public CodeSource run() {
                    return validator.getClass().getProtectionDomain().getCodeSource();
                }
            });
        } else {
            return validator.getClass().getProtectionDomain().getCodeSource();
        }
    }

    /**
     * INTERNAL:
     *
     * Value Object class that provides adequate toString() method which describes
     * on which field a Validation Constraint was violated and includes it's violationDescription.
     */
    private static class ConstraintViolationInfo {
        /**
         * Description of constraint violation.
         */
        private final String violationDescription;

        /**
         * Path to element on which the constraint violation occurred.
         */
        private final Path propertyPath;

        /**
         * Private constructor. Only to be used from within {@link org.eclipse.persistence.jaxb.JAXBBeanValidator}.
         *
         * @param message description of constraint violation
         * @param propertyPath path to element on which the constraint violation occurred
         */
        private ConstraintViolationInfo(String message, Path propertyPath){
            this.violationDescription = message;
            this.propertyPath = propertyPath;
        }

        @Override
        public String toString() {
            return "Violated constraint on property " + propertyPath + ": \"" + violationDescription + "\".";
        }
    }
}
