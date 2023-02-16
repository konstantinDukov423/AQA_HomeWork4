import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Condition.*;

public class CardDeliveryTest {

    @BeforeEach
    void localHostSetUp() {
        Configuration.headless = true;
        open("http://localhost:9999");

    }

    @Test
    void successfulBooking() {
        // Успешное бронирование
        String data = LocalDate.now().plusDays(7).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        $("span[data-test-id='city'] input").setValue("Тюмень");
        $("span[data-test-id=date] [type=tel]").sendKeys(Keys.CONTROL + "A", Keys.BACK_SPACE);
        $("span[data-test-id=date] [type=tel]").setValue(data);
        $x("//span[@data-test-id='name']//input[@class='input__control']").setValue("Дюков Константин");
        $x("//span[@data-test-id='phone']//input[@class='input__control']").setValue("+79069935042");
        $x("//span[@class='checkbox__box']").click();
        $x("//span[@class='button__text']").click();
        $x("//*[contains(text(), 'Встреча успешно забронирована на ')]").shouldBe(exactText("Встреча успешно забронирована на " + data), Duration.ofSeconds(15));
    }

    @Test
    void citySelectionPrompt() {
        // Успешное бронирование с выбором города из выпадающего списка
        String data = LocalDate.now().plusDays(7).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        $("span[data-test-id='city'] input").setValue("Мо");
        $(byText("Москва")).click();
        $("[data-test-id=date] [value]").click();
        $("span[data-test-id=date] [type=tel]").sendKeys(Keys.CONTROL + "A", Keys.BACK_SPACE);
        $("span[data-test-id=date] [type=tel]").setValue(data);
        $x("//span[@data-test-id='name']//input[@class='input__control']").setValue("Дюков Константин");
        $x("//span[@data-test-id='phone']//input[@class='input__control']").setValue("+79069935042");
        $x("//span[@class='checkbox__box']").click();
        $x("//span[@class='button__text']").click();
        $x("//*[contains(text(), 'Встреча успешно забронирована на ')]").shouldBe(exactText("Встреча успешно забронирована на " + data), Duration.ofSeconds(15));
    }

    @Test
    void dataPicketWidget() {
        // Успешное бронирование, с выбором даты через виджет календаря
        $("[data-test-id=city] input").setValue("Москва");
        LocalDate defaultDay = LocalDate.now();
        LocalDate planDay = LocalDate.now().plusDays(7);
        String data = LocalDate.now().plusDays(7).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        $("[data-test-id=date] [value]").click();
        if ((planDay.getYear() > defaultDay.getYear() | planDay.getMonthValue() > defaultDay.getMonthValue())) {
            $(".calendar__arrow_direction_right[data-step='1']").click();
        }
        String seekingDay = String.valueOf(planDay.getDayOfMonth());
        $$("td.calendar__day").find(text(seekingDay)).click();
        $x("//span[@data-test-id='name']//input[@class='input__control']").setValue("Дюков Константин");
        $x("//span[@data-test-id='phone']//input[@class='input__control']").setValue("+79069935042");
        $x("//span[@class='checkbox__box']").click();
        $x("//span[@class='button__text']").click();
        $x("//*[contains(text(), 'Встреча успешно забронирована на ')]").shouldBe(exactText("Встреча успешно забронирована на " + data), Duration.ofSeconds(15));
    }

    @Test
    void incorrectCity() {
        // Неудачное бронирование, вводя название города, которого нет в списке городов РФ
        String data = LocalDate.now().plusDays(7).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        $("span[data-test-id='city'] input").setValue("Воображляндия");
        $("span[data-test-id=date] [type=tel]").sendKeys(Keys.CONTROL + "A", Keys.BACK_SPACE);
        $("span[data-test-id=date] [type=tel]").setValue(data);
        $x("//span[@data-test-id='name']//input[@class='input__control']").setValue("Дюков Константин");
        $x("//span[@data-test-id='phone']//input[@class='input__control']").setValue("+79069935042");
        $x("//span[@class='checkbox__box']").click();
        $x("//span[@class='button__text']").click();
        $x("//span[@data-test-id='city']//span[@class='input__sub']").shouldBe(exactText("Доставка в выбранный город недоступна"));
    }

    @Test
    void incorrectData() {
        // Неудачное бронирование, вводя некорректную дату
        $("span[data-test-id='city'] input").setValue("Тюмень");
        $("span[data-test-id=date] [type=tel]").sendKeys(Keys.CONTROL + "A", Keys.BACK_SPACE);
        $("span[data-test-id=date] [type=tel]").setValue("01.01.0001");
        $x("//span[@data-test-id='name']//input[@class='input__control']").setValue("Дюков Константин");
        $x("//span[@data-test-id='phone']//input[@class='input__control']").setValue("+79069935042");
        $x("//span[@class='checkbox__box']").click();
        $x("//span[@class='button__text']").click();
        $x("//span[@data-test-id='date']//span[@class='input__sub']").should(exactText("Заказ на выбранную дату невозможен"));
    }

    @Test
    void incorrectName() {
        // Неудачное бронирование, вводя латинские символы в поле "Фамилия и имя"
        String data = LocalDate.now().plusDays(7).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        $("span[data-test-id='city'] input").setValue("Тюмень");
        $("span[data-test-id=date] [type=tel]").sendKeys(Keys.CONTROL + "A", Keys.BACK_SPACE);
        $("span[data-test-id=date] [type=tel]").setValue(data);
        $x("//span[@data-test-id='name']//input[@class='input__control']").setValue("Dukov Konstantin");
        $x("//span[@data-test-id='phone']//input[@class='input__control']").setValue("+79069935042");
        $x("//span[@class='checkbox__box']").click();
        $x("//span[@class='button__text']").click();
        $x("//span[@data-test-id='name']//span[@class='input__sub']").shouldBe(appear);
    }

    @Test
    void incorrectPhoneNumber() {
        // Неудачное бронирование, введя в после "Мобильный телефон" только 1 цифру
        String data = LocalDate.now().plusDays(7).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        $("span[data-test-id='city'] input").setValue("Тюмень");
        $("span[data-test-id=date] [type=tel]").sendKeys(Keys.CONTROL + "A", Keys.BACK_SPACE);
        $("span[data-test-id=date] [type=tel]").setValue(data);
        $x("//span[@data-test-id='name']//input[@class='input__control']").setValue("Дюков Константин");
        $x("//span[@data-test-id='phone']//input[@class='input__control']").setValue("1");
        $x("//span[@class='checkbox__box']").click();
        $x("//span[@class='button__text']").click();
        $x("//span[@data-test-id='phone']//span[@class='input__sub']").should(exactText("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678."));
    }

    @Test
    void noAgreementButton() {
        // Неудачное бронирование, без соглашения на обработку данных
        String data = LocalDate.now().plusDays(7).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        $("span[data-test-id='city'] input").setValue("Тюмень");
        $("span[data-test-id=date] [type=tel]").sendKeys(Keys.CONTROL + "A", Keys.BACK_SPACE);
        $("span[data-test-id=date] [type=tel]").setValue(data);
        $x("//span[@data-test-id='name']//input[@class='input__control']").setValue("Дюков Константин");
        $x("//span[@data-test-id='phone']//input[@class='input__control']").setValue("+79069935042");
        String Color = $x("//span[@class='checkbox__box']").getCssValue("color: rgba(11,31,53,.95)");
        $x("//span[@class='button__text']").click();
        $x("//span[@class='checkbox__box']").should(cssValue("color: #ff5c5c!important", Color));
    }

    @Test
    void leaveFieldsEmpty () {
        // Неудачное бронирование, оставив все поля пустыми
        $x("//span[@class='button__text']").click();
        $x("//span[@data-test-id='city']//span[@class='input__sub']").shouldBe(exactText("Поле обязательно для заполнения"));
    }
}
