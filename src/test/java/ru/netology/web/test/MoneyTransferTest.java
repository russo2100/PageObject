package ru.netology.web.test;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.web.data.DataHelper;
import ru.netology.web.page.LoginPageV2;

import static com.codeborne.selenide.Selenide.*;


class MoneyTransferTest {

    @BeforeEach
    void setUp() {
        Configuration.holdBrowserOpen = true;
        open("http://localhost:9999");
    }

    @Test  // тест переводит случайную валидную сумму с одной карты на другую, а потом обратно ту же самую сумму.
    void shouldTransferMoneyBetweenOwnCardsV1() {
        var loginPage = new LoginPageV2();
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        var listCardsPage = verificationPage.validVerify(verificationCode);

        var card1 = DataHelper.getFirstCard();
        var card2 = DataHelper.getSecondCard();
        int balance1 = listCardsPage.getCardBalance(card1);
        int balance2 = listCardsPage.getCardBalance(card2);
        int amount = DataHelper.getValidTransferAmount(balance2);

        var dashboardPage = listCardsPage.topUpButton(card1);
        listCardsPage = dashboardPage.topUp(amount, card2);

        Assertions.assertEquals((balance1+amount), listCardsPage.getCardBalance(card1));
        Assertions.assertEquals((balance2-amount), listCardsPage.getCardBalance(card2));

        balance1 = listCardsPage.getCardBalance(card1);
        balance2 = listCardsPage.getCardBalance(card2);
        dashboardPage = listCardsPage.topUpButton(card2);
        listCardsPage = dashboardPage.topUp(amount, card1);


        Assertions.assertEquals((balance2+amount), listCardsPage.getCardBalance(card2));
        Assertions.assertEquals((balance1-amount), listCardsPage.getCardBalance(card1));

    }

    @Test  // сумма перевода равна сумме баланса карты, с которой осуществляется перевод.
    void shouldNotTransferValidAmountEqualToBalance() {
        var loginPage = new LoginPageV2();
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        var listCardsPage = verificationPage.validVerify(verificationCode);


        var card1 = DataHelper.getFirstCard();
        var card2 = DataHelper.getSecondCard();
        int balance1 = listCardsPage.getCardBalance(card1);
        int amount = listCardsPage.getCardBalance(card2);

        var dashboardPage = listCardsPage.topUpButton(card1);
        listCardsPage = dashboardPage.topUp(amount, card2);

        Assertions.assertEquals((balance1+amount), listCardsPage.getCardBalance(card1));
        Assertions.assertEquals(0, listCardsPage.getCardBalance(card2));

        balance1 = listCardsPage.getCardBalance(card1);
        dashboardPage = listCardsPage.topUpButton(card2);
        listCardsPage = dashboardPage.topUp(amount, card1);

        Assertions.assertEquals(amount, listCardsPage.getCardBalance(card2));
        Assertions.assertEquals((balance1-amount), listCardsPage.getCardBalance(card1));
    }

    @Test //Тест вносит данные с суммой, равной 0, и проверяет чтобы кнопка 'Пополнить' осталась неактивной.
    void shouldNotTransferValidAmountEqualToNull() {
        var loginPage = new LoginPageV2();
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        var listCardsPage = verificationPage.validVerify(verificationCode);

        var card1 = DataHelper.getFirstCard();
        var card2 = DataHelper.getSecondCard();
        int amount = 0;

        var dashboardPage = listCardsPage.topUpButton(card1);
        dashboardPage.topUpInvalid(amount, card2);
        $("[data-test-id='action-transfer']").shouldBe(Condition.disabled);
    }


    @Test //Сумма перевода превышает баланс карты, с которой осуществляется перевод.
        // Тест проверяет, чтобы кнопка Пополнить осталась неактивной
        // и появилось сообщение: На счете недостаточно средств
    void shouldNotTransferInvalidAmountMoreBalance() {
        var loginPage = new LoginPageV2();
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        var listCardsPage = verificationPage.validVerify(verificationCode);

        var card1 = DataHelper.getFirstCard();
        var card2 = DataHelper.getSecondCard();
        int balance2 = listCardsPage.getCardBalance(card2);
        int amount = DataHelper.getInvalidTransferAmount(balance2);

        var dashboardPage = listCardsPage.topUpButton(card1);
        dashboardPage.topUpInvalid(amount, card2);

        $("[data-test-id='action-transfer']").shouldBe(Condition.disabled);
        $x("//*[contains(text(), 'недостаточно средств']").shouldBe(Condition.visible);

    }

    @Test //Указываем один и тот же счет, с которого переводим и на который переводим.
        // Ожидается сообщение типа: "Вы указали один и тот же счет", кнопка Пополнить должна быть неактивна,
        // Дополнительный варианты реализации: автоматически меняется счет зачисления на другой или невозможно выбрать такой же счет.
        // Необходимо уточнять требования.
    void shouldNotTransferIdenticalAccount() {
        var loginPage = new LoginPageV2();
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        var listCardsPage = verificationPage.validVerify(verificationCode);

        var card1 = DataHelper.getFirstCard();
        int balance1 = listCardsPage.getCardBalance(card1);
        int amount = DataHelper.getValidTransferAmount(balance1);

        var dashboardPage = listCardsPage.topUpButton(card1);
        dashboardPage.topUpInvalid(amount, card1);

        $("[data-test-id='action-transfer']").shouldBe(Condition.disabled);
        $x("//*[contains(text(), 'вы указали один и тот же счет']").shouldBe(Condition.visible);

    }

}