package iv.lib.validator;

import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Validator {
    private static final String VALIDATOR_NOT_READY = "Validator not ready";
    private static final String FIELDS_LIST_EMPTY = "List fields is empty";
    private static final String SEPARATOR = ".";


    private Logger logger = LoggerFactory.getLogger(getClass());

    private List<String> messages;
    private List<NullField> fields;

    private Validator() {}

    public static Validator create() {
        Validator validator = new Validator();

        validator.messages = new ArrayList<>();
        validator.fields = new ArrayList<>();

        return validator;
    }

    public Validator notNullJSON(Object field, String name) {
        if (field == null)
            messages.add(String.format("JSON field %s must be not null", name));
        return this;
    }

    public Validator mustNullJSON(Object field, String name) {
        if (field != null)
            messages.add(String.format("JSON field %s must be null", name));
        return this;
    }

    public Validator notNullParam(Object param, String name) {
        if (param == null)
            messages.add(String.format("Request param %s must be not null", name));
        return this;
    }

    public Validator load(StringBuilder buffer) {
        for (String str : messages)
            buffer.append(str.concat("; "));
        return this;
    }

    public Validator addField(String fieldName, FieldType type) {
        fields.add(NullField.create(fieldName, type));
        return this;
    }

    public Validator addField(String fieldPath) {
        return addField(fieldPath, FieldType.NOT_NULL);
    }

    public Validator addNullField(String fieldPath) {
        return addField(fieldPath, FieldType.NULLABLE);
    }

    public Validator validJSON(Object object) {
        if (fields.isEmpty()) throw new IllegalArgumentException(FIELDS_LIST_EMPTY);

        fields.forEach(field -> {
            try {
                String[] inner = field.getFieldPath().split("\\" + SEPARATOR);
                StringBuilder msg = new StringBuilder();

                Field currentField;
                for (int i = 0; i < inner.length; i++) {
                    String currentName = inner[i];
                    currentField = object.getClass().getDeclaredField(currentName);
                    currentField.setAccessible(true);
                    msg.append(SEPARATOR.concat(currentName));

                    switch (field.getType()) {
                        case NOT_NULL:
                            if (currentField.get(object) == null) {
                                notNullJSON(null, msg.toString());
                                break;
                            }
                            break;
                        case NULLABLE:
                            if (currentField.get(object) != null && i == inner.length-1) {
                                mustNullJSON(true, msg.toString());
                                break;
                            }
                            break;
                    }


                }
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        });

        return this;
    }

    public StringBuilder export() {
        StringBuilder buffer = new StringBuilder();
        load(buffer);
        return buffer;
    }

    public boolean isCorrect() {
        return export().toString().isEmpty();
    }

    public boolean valid() {
        return messages.size() == 0;
    }

    public Validator reset() {
        return clearMessage().clearFields();
    }

    public Validator clearMessage() {
        messages.clear();
        return this;
    }

    public Validator clearFields() {
        fields.clear();
        return this;
    }

    @Getter
    static class NullField {
        private FieldType type;
        private String fieldPath;

        public static NullField create(String path, FieldType t) {
            NullField field = new NullField();
            field.type = t;
            field.fieldPath = path;
            return field;
        }
    }

    public enum FieldType {
        NOT_NULL,
        NULLABLE
    }
}
