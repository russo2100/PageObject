package ru.netology.web.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import lombok.Value;
import lombok.val;
import org.hamcrest.Condition;
import ru.netology.web.data.DataHelper;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class ListCardsPage {

    private ElementsCollection cards = $$(".list__item div");
    private final String balanceStart = "баланс: ";
    private final String balanceFinish = " р.";

    public ListCardsPage() {
        cards.get(0).shouldBe(visible);
    }

    public DashboardPage topUpButton(DataHelper.CardsInfo card) {
        $("[data-test-id = '" + card.getDataTestId() + "'] button").click();
        return new DashboardPage();
    }


    public int getCardBalance(DataHelper.CardsInfo card) {
        String text = $("[data-test-id = '" + card.getDataTestId() + "']").text();
        return extractBalance(text);
    }

    private int extractBalance(String text) {
        val start = text.indexOf(balanceStart);
        val finish = text.indexOf(balanceFinish);
        val value = text.substring(start + balanceStart.length(), finish);
        return Integer.parseInt(value);
    }


}
