package org.eclipse.persistence.json.bind.internal;

import org.eclipse.persistence.json.bind.internal.properties.MessageKeys;
import org.eclipse.persistence.json.bind.internal.properties.Messages;

import javax.json.JsonValue;
import javax.json.bind.JsonbConfig;
import javax.json.bind.JsonbException;
import javax.json.stream.JsonGenerator;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Jsonb decorator for JsonP generator which supports I-Json
 * Adding support for I-Json to JsonGenerator. When the value at top level
 * is not object or array and I-Json is required, then it throws an exception.
 *
 * @author David Kral
 */
public class IJsonJsonGeneratorDecorator implements JsonGenerator {

    private final JsonGenerator jsonGenerator;
    private boolean first = true;

    public IJsonJsonGeneratorDecorator(JsonGenerator jsonGenerator) {
        this.jsonGenerator = jsonGenerator;
    }

    @Override
    public JsonGenerator writeStartObject() {
        first = false;
        return jsonGenerator.writeStartObject();
    }

    @Override
    public JsonGenerator writeStartObject(String s) {
        first = false;
        return jsonGenerator.writeStartObject(s);
    }

    @Override
    public JsonGenerator writeStartArray() {
        first = false;
        return jsonGenerator.writeStartArray();
    }

    @Override
    public JsonGenerator writeStartArray(String s) {
        first = false;
        return jsonGenerator.writeStartArray(s);
    }

    @Override
    public JsonGenerator write(String s, JsonValue jsonValue) {
        return jsonGenerator.write(s, jsonValue);
    }

    @Override
    public JsonGenerator write(String s, String s1) {
        return jsonGenerator.write(s, s1);
    }

    @Override
    public JsonGenerator write(String s, BigInteger bigInteger) {
        return jsonGenerator.write(s, bigInteger);
    }

    @Override
    public JsonGenerator write(String s, BigDecimal bigDecimal) {
        return jsonGenerator.write(s, bigDecimal);
    }

    @Override
    public JsonGenerator write(String s, int i) {
        return jsonGenerator.write(s, i);
    }

    @Override
    public JsonGenerator write(String s, long l) {
        return jsonGenerator.write(s, l);
    }

    @Override
    public JsonGenerator write(String s, double v) {
        return jsonGenerator.write(s, v);
    }

    @Override
    public JsonGenerator write(String s, boolean b) {
        return jsonGenerator.write(s, b);
    }

    @Override
    public JsonGenerator writeNull(String s) {
        return jsonGenerator.writeNull(s);
    }

    @Override
    public JsonGenerator writeEnd() {
        return jsonGenerator.writeEnd();
    }

    @Override
    public JsonGenerator write(JsonValue jsonValue) {
        if (first) {
            evaluateIjson();
        }
        return jsonGenerator.write(jsonValue);
    }

    @Override
    public JsonGenerator write(String s) {
        if (first) {
            evaluateIjson();
        }
        return jsonGenerator.write(s);
    }

    @Override
    public JsonGenerator write(BigDecimal bigDecimal) {
        if (first) {
            evaluateIjson();
        }
        return jsonGenerator.write(bigDecimal);
    }

    @Override
    public JsonGenerator write(BigInteger bigInteger) {
        if (first) {
            evaluateIjson();
        }
        return jsonGenerator.write(bigInteger);
    }

    @Override
    public JsonGenerator write(int i) {
        if (first) {
            evaluateIjson();
        }
        return jsonGenerator.write(i);
    }

    @Override
    public JsonGenerator write(long l) {
        if (first) {
            evaluateIjson();
        }
        return jsonGenerator.write(l);
    }

    @Override
    public JsonGenerator write(double v) {
        if (first) {
            evaluateIjson();
        }
        return jsonGenerator.write(v);
    }

    @Override
    public JsonGenerator write(boolean b) {
        if (first) {
            evaluateIjson();
        }
        return jsonGenerator.write(b);
    }

    @Override
    public JsonGenerator writeNull() {
        return jsonGenerator.writeNull();
    }

    @Override
    public void close() {
        first = true;
        jsonGenerator.close();
    }

    @Override
    public void flush() {
        jsonGenerator.flush();
    }

    private void evaluateIjson() {
        boolean isStrict = (boolean) JsonbContext.getInstance().getConfig().getProperty(JsonbConfig.STRICT_IJSON).orElse(false);
        if (isStrict){
            throw new JsonbException(Messages.getMessage(MessageKeys.IJSON_ENABLED_SINGLE_VALUE));
        }
    }
}
