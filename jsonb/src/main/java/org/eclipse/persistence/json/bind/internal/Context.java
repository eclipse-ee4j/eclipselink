package org.eclipse.persistence.json.bind.internal;

import org.eclipse.persistence.json.bind.model.ClassModel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * JSONB context. Created once per {@link javax.json.bind.Jsonb} instance. Represents a global scope.
 * Holds internal model.
 *
 * @author Dmitry Kornilov
 */
public class Context {
    private Map<Class, ClassModel> classes = new ConcurrentHashMap<>();
    private ClassParser classParser = new ClassParser();

    public ClassModel getClassModel(Class clazz) {
        if (!classes.containsKey(clazz)) {
            classes.put(clazz, classParser.parse(clazz));
        }
        return classes.get(clazz);
    }
}
