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

package org.eclipse.persistence.json.bind.internal.cdi;

import org.eclipse.persistence.json.bind.internal.properties.MessageKeys;
import org.eclipse.persistence.json.bind.internal.properties.Messages;

import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.logging.Logger;

/**
 * Factory method for Jsonb component instance creator.
 *
 * @author Roman Grigoriadi
 */
public class JsonbComponentInstanceCreatorFactory {

    private static final Logger log = Logger.getLogger(JsonbComponentInstanceCreator.class.getName());

    public static final String BEAN_MANAGER_NAME = "java:comp/BeanManager";

    /**
     * First check a CDI provider, if available use those.
     * Try to lookup in a JNDI if no provider is registered.
     * If one of the above is found {@link BeanManagerInstanceCreator} is returned,
     * or {@link DefaultConstructorCreator} otherwise.
     * @return Component instance creator, either CDI or default constructor.
     */
    public static JsonbComponentInstanceCreator getComponentInstanceCreator() {
        try {
            return new BeanManagerInstanceCreator(CDI.current().getBeanManager());
        } catch (IllegalStateException e) {
            log.finest(Messages.getMessage(MessageKeys.BEAN_MANAGER_NOT_FOUND_NO_PROVIDER));
            try {
                InitialContext context = new InitialContext();
                final BeanManager lookup = (BeanManager) context.lookup(BEAN_MANAGER_NAME);
                return new BeanManagerInstanceCreator(lookup);
            } catch (NamingException e1) {
                log.finest(Messages.getMessage(MessageKeys.BEAN_MANAGER_NOT_FOUND_JNDI, BEAN_MANAGER_NAME, e1.getExplanation()));
            }
        }
        log.finest(Messages.getMessage(MessageKeys.BEAN_MANAGER_NOT_FOUND_USING_DEFAULT));
        return new DefaultConstructorCreator();
    }
}
