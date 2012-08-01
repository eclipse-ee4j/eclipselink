/*******************************************************************************
 * Copyright (c) 2009 Sun Microsystems, Inc. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     08/01/2012-2.5 Chris Delahunt
 *       - 371950: Metadata caching 
 ******************************************************************************/

package org.eclipse.persistence.internal.jpa.deployment;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.listeners.BeanValidationListener;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.exceptions.PersistenceUnitLoadingException;

import javax.validation.ValidatorFactory;
import javax.validation.Validation;
import java.util.Map;
import java.security.PrivilegedActionException;
import java.security.AccessController;

/**
 * Responsible for intialializing Bean Validation. The only expected instance of this interface is the inner class.
 * @author Mitesh Meswani
 */
public interface BeanValidationInitializationHelper {
    public void bootstrapBeanValidation(Map puProperties, AbstractSession session, ClassLoader appClassLoader);

    static class BeanValidationInitializationHelperImpl implements BeanValidationInitializationHelper {
        public void bootstrapBeanValidation(Map puProperties, AbstractSession session, ClassLoader appClassLoader) {

            ValidatorFactory validatorFactory = getValidatorFactory(puProperties);

            //Check user/environment has specified the validator factory
            if (validatorFactory != null) {
                // We could obtain a validator factory => Bean Validation API is available at runtime. It is ok to cast now
                ValidatorFactory beanValidatorFactory = validatorFactory;

                Class[] groupPrePersit = translateValidationGroups(
                        (String) puProperties.get(PersistenceUnitProperties.VALIDATION_GROUP_PRE_PERSIST), appClassLoader) ;
                Class[] groupPreUpdate = translateValidationGroups(
                        (String) puProperties.get(PersistenceUnitProperties.VALIDATION_GROUP_PRE_UPDATE), appClassLoader);
                Class[] groupPreRemove = translateValidationGroups(
                        (String) puProperties.get(PersistenceUnitProperties.VALIDATION_GROUP_PRE_REMOVE), appClassLoader);

                BeanValidationListener validationListener =
                        new BeanValidationListener(beanValidatorFactory, groupPrePersit, groupPreUpdate, groupPreRemove);
                //Install BeanValidationListener on the desc
                for (ClassDescriptor descriptor : session.getProject().getDescriptors().values()) {
                    if (descriptor.isDescriptorTypeNormal()) {
                        //add only to entities
                        descriptor.getEventManager().addInternalListener(validationListener);
                    }
                }
            }
        }

        /**
         * INTERNAL:
         * @param puProperties merged properties for this persitence unit
         * @return ValidatorFactory instance to be used for this persistence unit.
         */
        private ValidatorFactory getValidatorFactory(Map puProperties) {
            ValidatorFactory validatorFactory = (ValidatorFactory)puProperties.get(PersistenceUnitProperties.VALIDATOR_FACTORY);

            if (validatorFactory == null) {
                validatorFactory = Validation.buildDefaultValidatorFactory();
            }
            return validatorFactory;
        }


        /**
         * INTERNAL:
         * translate validation group specified as fully qualifed class names deliminated by ',' into Class[]
         * @param validationGroups Array of "," deliminated fully qualified class names
         * @param appClassLoader The classloader for application
         * @return Array of classes corresponding to classnames in given <code>validationGroups</code>.
         *         <code>null<code> if given <code>validationGroups</code> is null or empty
         */
        private Class[] translateValidationGroups(String validationGroups, ClassLoader appClassLoader) {
            Class[] validationGroupsClasses = null;
            if(validationGroups != null && validationGroups.length() != 0 ) {
                String[] validationGroupClassNames = validationGroups.split(",");
                validationGroupsClasses = new Class[validationGroupClassNames.length];
                for(int i = 0; i < validationGroupClassNames.length; i++) {
                    String validationGroupClassName = validationGroupClassNames[i];
                    try {
                        validationGroupsClasses[i] = loadClass(validationGroupClassName, appClassLoader);
                    } catch (Exception e) {
                        throw PersistenceUnitLoadingException.exceptionLoadingClassWhileInitializingValidationGroups(validationGroupClassName, e);
                    }
                }
            }
            return validationGroupsClasses;
        }

        /**
         * Internal:
         * Helper to load class.
         * Please do not make this method protected or public else it would expose a security hole that would
         * allow malicious code to use this method to load any class without security permission
         * @param className Fully qualified class name
         * @param classLoader ClassLoader to be used for loading the class
         * @return Loaded Class
         * @throws java.security.PrivilegedActionException
         * @throws ClassNotFoundException
         */
        private Class loadClass(String className, ClassLoader classLoader) throws PrivilegedActionException, ClassNotFoundException {
            Class loadedClass = null;
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
                loadedClass = (Class) AccessController.doPrivileged(
                        new PrivilegedClassForName(className, true, classLoader));
            } else {
                loadedClass = PrivilegedAccessHelper.getClassForName(className, true, classLoader);
            }
            return loadedClass;
        }
    }
}
