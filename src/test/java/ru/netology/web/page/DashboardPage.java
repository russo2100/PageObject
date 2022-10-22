package ru.netology.web.page;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.Keys;
import ru.netology.web.data.DataHelper;

import static com.codeborne.selenide.Condition.disabled;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class DashboardPage {

    private SelenideElement heading = $("[data-test-id=dashboard]");
    private SelenideElement amountInput = $("[data-test-id='amount'] input");
    private SelenideElement from = $("[data-test-id='from'] input");
    private SelenideElement buttonTopUp = $("[data-test-id='action-transfer']");

    public DashboardPage() {
        heading.shouldBe(visible);
        amountInput.shouldBe(visible);
        from.shouldBe(visible);
    }

    public void enterData(int amount, DataHelper.CardsInfo card) {
        amountInput.sendKeys(Keys.CONTROL + "a");
        amountInput.sendKeys(Keys.DELETE);
        amountInput.setValue(String.valueOf(amount));
        from.sendKeys(Keys.CONTROL + "a");
        from.sendKeys(Keys.DELETE);
        from.setValue(card.getNumberCard());
    }

    public ListCardsPage topUp(int amount, DataHelper.CardsInfo card) {
        enterData(amount, card);
        buttonTopUp.click();
        return new ListCardsPage();
    }

    public void topUpInvalid(int invalidAmount, DataHelper.CardsInfo card) {
        enterData(invalidAmount, card);
    }

}
