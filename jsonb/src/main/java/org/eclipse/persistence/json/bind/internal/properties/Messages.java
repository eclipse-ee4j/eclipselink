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
 *     David Král - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.json.bind.internal.properties;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * JSONB properties file manager.
 *
 * @author David Král
 */
public class Messages {

    private final static String MESSAGE_BUNDLE = "messages";
    private final static String ENCODING = "UTF-8";

    private Messages() {
    }

    public static String getMessage(MessageKeys key, Object... objects) {
        return getMessage(key, Locale.getDefault(), objects);
    }

    public static String getMessage(MessageKeys key, Locale locale, Object... objects) {
        ResourceBundle messages = ResourceBundle.getBundle(MESSAGE_BUNDLE, locale, new UTF8Control());
        MessageFormat formatter = new MessageFormat(messages.getString(key.key));
        return formatter.format(objects);
    }

    static class UTF8Control extends ResourceBundle.Control {
        public ResourceBundle newBundle
                (String baseName, Locale locale, String format, ClassLoader loader, boolean reload)
                throws IllegalAccessException, InstantiationException, IOException
        {
            // The below is a copy of the default implementation.
            String bundleName = toBundleName(baseName, locale);
            String resourceName = toResourceName(bundleName, "properties");
            ResourceBundle bundle = null;
            InputStream stream = null;
            if (reload) {
                URL url = loader.getResource(resourceName);
                if (url != null) {
                    URLConnection connection = url.openConnection();
                    if (connection != null) {
                        connection.setUseCaches(false);
                        stream = connection.getInputStream();
                    }
                }
            } else {
                stream = loader.getResourceAsStream(resourceName);
            }
            if (stream != null) {
                try {
                    // Only this line is changed to make it to read properties files as UTF-8.
                    bundle = new PropertyResourceBundle(new InputStreamReader(stream, ENCODING));
                } finally {
                    stream.close();
                }
            }
            return bundle;
        }
    }

}
