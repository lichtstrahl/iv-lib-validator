package iv.lib.validator;

import iv.lib.validator.dto.DetailsDTO;
import iv.lib.validator.dto.UserDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestValidator {
    private static final  String FIRST_NAME = "example:FirstName";
    private static final String LAST_NAME = "example:LastName";

    @Test
    void testAllFieldsNull() {
        // Step 1: Подготовка структуры, которую будем проверять
        Validatable dto = DetailsDTO.create(null, null, null, null);

        // Step 2: Осуществление проверки
        Validator validator = dto.createValidator()
                                        .validJSON(dto);

        // Step 3: Получение результата и анализ ошибок
        Assertions.assertFalse(validator.isCorrect());
        String report = validator.export().toString();
        Assertions.assertTrue(report.contains("firstName"));
        Assertions.assertTrue(report.contains("lastName"));
        Assertions.assertTrue(report.contains("age"));
        Assertions.assertTrue(report.contains("magicNumbers"));
    }

    @Test
    void testOneFieldNull() {
        DetailsDTO dto = DetailsDTO.create(FIRST_NAME, LAST_NAME, null, 0);

        // Создание валидатора "на ходу"
        String report = Validator.create()
                            .addField("firstName")
                            .addField("lastName")
                            .addField("age")
                            .addField("magicNumbers")
                            .validJSON(dto)
                            .export()
                            .toString();

        Assertions.assertFalse(report.isEmpty());
        Assertions.assertFalse(report.contains("firstName"));
        Assertions.assertFalse(report.contains("lastName"));
        Assertions.assertTrue(report.contains("age"));
        Assertions.assertFalse(report.contains("magicNumbers"));

    }

    @Test
    void useIntfAllCorrect() {
        Validatable dto = DetailsDTO.create("", "", 0L, 2);


        // Step 2: Создание валидатора самой DTO
        Validator validator = dto
                                .createValidator()
                                .validJSON(dto);

        // Step 3: Получение результата и анализ ошибок
        Assertions.assertTrue(validator.export().toString().isEmpty());
        Assertions.assertTrue(validator.isCorrect());
    }


    @Test
    void innerDTOAllCorrect() {
        UserDTO user = UserDTO
                .builder()
                .login("igor")
                .password("pass")
                .details(DetailsDTO.create(FIRST_NAME, LAST_NAME, 0L, 1,2,3))
                .build();

        Validator validator = user.createValidator();

        validator.validJSON(user);

        Assertions.assertTrue(validator.isCorrect());
        Assertions.assertTrue(validator.export().toString().isEmpty());
    }

    // Вложенное и внешнее поле оказывается NULL
    @Test
    void innerDTOinnerFieldNull() {
        UserDTO user = UserDTO
                .builder()
                .login("igor")
                .password(null)
                .details(DetailsDTO.create(FIRST_NAME, null, null, 1,2,3))
                .build();

        Validator validator = user.createValidator();

        validator.validJSON(user);
        String report = validator.export().toString();

        Assertions.assertFalse(validator.isCorrect());
        Assertions.assertTrue(report.contains("password"));
        Assertions.assertTrue(report.contains("details.lastName"));
        Assertions.assertTrue(report.contains("details.age"));
    }

    // Вложенное и внешнее поле не NULL, хотя должны
    @Test
    void innerDTOnotNull() {
        UserDTO user = UserDTO
                .builder()
                .login("login")
                .password("password") // Must be NULL
                .details(DetailsDTO.create(FIRST_NAME, LAST_NAME, 0L, 1,2,3)) // Magic must be NULL
                .build();

        // Создание валидатора "на ходу"
        Validator validator = Validator.create()
                                    .addField("login")
                                    .addField("password", Validator.FieldType.NULLABLE)
                                    .addField("details")
                                    .addField("details.firstName")
                                    .addField("details.lastName")
                                    .addField("details.age")
                                    .addField("details.magicNumbers", Validator.FieldType.NULLABLE);

        validator.validJSON(user);
        String report = validator.export().toString();

        Assertions.assertFalse(validator.isCorrect());
        Assertions.assertTrue(report.contains("password"));
        Assertions.assertTrue(report.contains("details.magicNumbers"));
    }
}
