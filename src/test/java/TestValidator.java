import iv.lib.validator.Validator;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestValidator {
    public static void main(String[] args) {
        test1();
        test2();
        test3();
        test4();
    }

    @Test
    public void testValidator() {
        Assertions.assertNull(null);
        Assertions.assertNotNull("");
    }

    private static void test1() {
        TestDTO dto = new TestDTO(null, null, null, null);

        // Step 1: Подготовка структуры, которую будем проверять
        Validator validator = Validator
                .create()
                .addField("number")
                .addField("title")
                .addField("name")
                .addField("desc");
        // Step 2: Использование подготовленного валидатора. В первую очередь нужно очистить сообщения об ошибках
        validator
                .clearMessage()
                .validJSON(dto);

        // Step 3: Получение результата и анализ ошибок
        System.out.println(validator.isCorrect());
        if (!validator.isCorrect())
            System.out.println(validator.export().toString());
    }

    private static void test2() {
        TestDTO dto = new TestDTO(1L, "", "", "");

        // Step 1: Подготовка структуры, которую будем проверять
        Validator validator = Validator
                .create()
                .addField("number")
                .addField("title")
                .addField("name")
                .addField("desc");
        // Step 2: Использование подготовленного валидатора. В первую очередь нужно очистить сообщения об ошибках
        validator
                .clearMessage()
                .validJSON(dto);

        // Step 3: Получение результата и анализ ошибок
        System.out.println(validator.isCorrect());
        if (!validator.isCorrect())
            System.out.println();
    }

    private static void test3() {
        TestDTO dto = new TestDTO(2L, null, "", "");

        // Приммер быстрого использования. Сразу задаём структуру, сразу получаем результат
        // Нет необходимости очищать сообщения но и повторно проверить объект не получится,
        // Придётся заново задавать структуру для проверки
        boolean correct = Validator
                            .create()
                            .addField("number")
                            .addNullField("title")
                            .addField("name")
                            .addField("desc")
                            .validJSON(dto)
                            .valid();
        System.out.println(correct);

    }

    private static void test4() {
        TestDTO test = new TestDTO(0L, "", null, "");
        UserDTO user = new UserDTO("login", "password", 2L, test);

        Validator validator = Validator.create()
                .addField("login")
                .addField("password")
                .addField("age")
                .addField("test")
                .addField("test.number")
                .addField("test.title")
                .addNullField("test.name")
                .addNullField("test.desc");

        validator
                .clearMessage()
                .validJSON(user);

        System.out.println(validator.valid());
        if (!validator.valid())
            System.out.println(validator.export().toString());
    }

    @Data
    @AllArgsConstructor
    static private class TestDTO {
        private Long number;
        private String title;
        private String name;
        private String desc;
    }

    @Data
    @AllArgsConstructor
    static private class UserDTO {
        private String login;
        private String password;
        private Long age;
        private TestDTO test;
    }
}
