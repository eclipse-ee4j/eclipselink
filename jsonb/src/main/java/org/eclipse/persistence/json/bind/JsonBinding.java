package org.eclipse.persistence.json.bind;

import org.eclipse.persistence.json.bind.internal.Context;
import org.eclipse.persistence.json.bind.internal.Marshaller;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;

/**
 * Implementation of Jsonb interface.
 *
 * @author Dmitry Kornilov
 */
public class JsonBinding implements Jsonb {
    private Context context;

    JsonBinding(JsonBindingBuilder builder) {
        // TODO set internal properties of the context from builder
        context = new Context();
    }

    @Override
    public <T> T fromJson(String str, Class<T> type) throws JsonbException {
        return null;
    }

    @Override
    public <T> T fromJson(String str, Type runtimeType) throws JsonbException {
        return null;
    }

    @Override
    public <T> T fromJson(Readable readable, Class<T> type) throws JsonbException {
        return null;
    }

    @Override
    public <T> T fromJson(Readable readable, Type runtimeType) throws JsonbException {
        return null;
    }

    @Override
    public <T> T fromJson(InputStream stream, Class<T> type) throws JsonbException {
        return null;
    }

    @Override
    public <T> T fromJson(InputStream stream, Type runtimeType) throws JsonbException {
        return null;
    }

    @Override
    public String toJson(Object object) throws JsonbException {
        final Marshaller marshaller = new Marshaller(context);
        return marshaller.marshall(object);
    }

    @Override
    public String toJson(Object object, Type runtimeType) throws JsonbException {
        return null;
    }

    @Override
    public void toJson(Object object, Appendable appendable) throws JsonbException {

    }

    @Override
    public void toJson(Object object, Type runtimeType, Appendable appendable) throws JsonbException {

    }

    @Override
    public void toJson(Object object, OutputStream stream) throws JsonbException {

    }

    @Override
    public void toJson(Object object, Type runtimeType, OutputStream stream) throws JsonbException {

    }
}
