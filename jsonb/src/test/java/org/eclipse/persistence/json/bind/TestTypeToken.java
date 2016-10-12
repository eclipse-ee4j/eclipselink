package org.eclipse.persistence.json.bind;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author Roman Grigoriadi
 */
public abstract class TestTypeToken<T> {
    public Type getType() {
        return ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }
}
