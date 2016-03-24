package org.eclipse.persistence.json.bind.internal.conversion;

import org.eclipse.persistence.json.bind.internal.JsonbContext;

import javax.json.bind.JsonbConfig;
import javax.json.bind.JsonbException;
import javax.json.bind.config.BinaryDataStrategy;
import java.lang.reflect.Type;
import java.util.Base64;

/**
 * Converts byte of array according to specific strategy
 *
 * @author David Kral
 */
public class ByteArrayTypeConverter extends AbstractTypeConverter<byte[]> {

    public ByteArrayTypeConverter() {
        super(byte[].class);
    }

    @Override
    public byte[] fromJson(String jsonValue, Type type) {
        if ((boolean)JsonbContext.getInstance().getConfig().getProperty(JsonbConfig.STRICT_IJSON).orElse(false)) {
            return Base64.getUrlDecoder().decode(jsonValue);
        } else if (JsonbContext.getInstance().getConfig().getProperty(JsonbConfig.BINARY_DATA_STRATEGY).isPresent()) {
            String strategy = (String) JsonbContext.getInstance().getConfig().getProperty(JsonbConfig.BINARY_DATA_STRATEGY).get();
            if (strategy == null){
                throw new JsonbException("Unsupported binary data strategy!");
            }
            switch(strategy) {
                case BinaryDataStrategy.BYTE:
                    break;
                case BinaryDataStrategy.BASE_64:
                    return Base64.getDecoder().decode(jsonValue);
                case BinaryDataStrategy.BASE_64_URL:
                    return Base64.getUrlDecoder().decode(jsonValue);
                default:
                    throw new JsonbException("Unsupported binary data strategy!");
            }
        }
        String[] byteValues = jsonValue.split(",");
        byte[] bytes = new byte[byteValues.length];

        for (int i=0, len=bytes.length; i<len; i++) {
            bytes[i] = Byte.parseByte(byteValues[i].trim());
        }
        return bytes;
    }

    @Override
    public String toJson(byte[] object) {
        if ((boolean)JsonbContext.getInstance().getConfig().getProperty(JsonbConfig.STRICT_IJSON).orElse(false)) {
            return Base64.getUrlEncoder().encodeToString(object);
        } else if (JsonbContext.getInstance().getConfig().getProperty(JsonbConfig.BINARY_DATA_STRATEGY).isPresent()) {
            String strategy = (String) JsonbContext.getInstance().getConfig().getProperty(JsonbConfig.BINARY_DATA_STRATEGY).get();
            if (strategy == null){
                throw new JsonbException("Unsupported binary data strategy!");
            }
            switch(strategy) {
                case BinaryDataStrategy.BYTE:
                    break;
                case BinaryDataStrategy.BASE_64:
                    return Base64.getEncoder().encodeToString(object);
                case BinaryDataStrategy.BASE_64_URL:
                    return Base64.getUrlEncoder().encodeToString(object);
                default:
                    throw new JsonbException("Unsupported binary data strategy!");
            }
        }
        int iMax = object.length - 1;
        if (iMax == -1)
            return "[]";

        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0; ; i++) {
            b.append(object[i]);
            if (i == iMax)
                return b.append(']').toString();
            b.append(",");
        }
    }
}
