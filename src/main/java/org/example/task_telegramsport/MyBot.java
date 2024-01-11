package org.example.task_telegramsport;

import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class MyBot extends TelegramLongPollingBot {
    List<User> users = new ArrayList<>();

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        Long chatId = message.getChatId();
        User user = findUser(chatId);
        if (message.hasText()) {
            if (message.getText().equalsIgnoreCase("/start") && user.getStatus().equals(Status.START)) {
                user.setStatus(Status.SET_FIRSTNAME);

                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(user.getChatId());
                sendMessage.setText("Assalomu alaykum botga xush kelibsiz siz bilan Masharipov Nematillo qilgan bot Ismingizni yuboring ");
                execute(sendMessage);

            } else if (user.getStatus().equals(Status.SET_FIRSTNAME)) {

                user.setFirstName(message.getText());
                user.setStatus(Status.SET_LASTNAME);

                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(user.getChatId());
                sendMessage.setText("Familiya yuboring ");
                execute(sendMessage);

            } else if (user.getStatus().equals(Status.SET_LASTNAME)) {

                user.setLastName(message.getText());
                user.setStatus(Status.SET_PHONE);

                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(user.getChatId());
                sendMessage.setText("phoneNumber yuboring ");
                sendMessage.setReplyMarkup(genContactButton());
                Message execute = execute(sendMessage);
                user.setShareContactMessageId(execute.getMessageId());

            } else if (user.getStatus().equals(Status.SET_GENDER)) {

                if (message.getText().equalsIgnoreCase(Gender.MALE.nameUz)) {
                    user.setGender(Gender.MALE);
                } else {
                    user.setGender(Gender.FEMALE);
                }
                user.setStatus(Status.SET_SPORT);

                DeleteMessage deleteMessage = new DeleteMessage();
                deleteMessage.setChatId(user.getChatId());
                deleteMessage.setMessageId(user.getShareGenderMessageId());
                execute(deleteMessage);

                SendMessage sendMessage = new SendMessage();
                sendMessage.setText("Sport turini tanlang");
                sendMessage.setChatId(user.getChatId());
                sendMessage.setReplyMarkup(getSportButton());
                Message execute = execute(sendMessage);
                user.setShareSportMessageId(execute.getMessageId());

            } else if (user.getStatus().equals(Status.SET_SPORT)) {

                user.setSport(message.getText());
                user.setStatus(Status.SET_BALANCE);

                DeleteMessage deleteMessage = new DeleteMessage();
                deleteMessage.setChatId(user.getChatId());
                deleteMessage.setMessageId(user.getShareSportMessageId());
                execute(deleteMessage);

                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(user.getChatId());
                sendMessage.setText("Balance kiriting");
                sendMessage.setReplyMarkup(getBalanceButton());
                Message execute = execute(sendMessage);
                user.setShareBalanceMessageId(execute.getMessageId());

            } else if (user.getStatus().equals(Status.SET_BALANCE)) {

                if (message.getText().equals(ReplyButtonText.PLUS)) {

                    user.setBalance(user.getBalance() + 10);
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(user.getChatId());
                    sendMessage.setText("Knopka tanlang Balance: " + user.getBalance() + " so'm");
                    execute(sendMessage);

                } else if (message.getText().equals(ReplyButtonText.MINUS)) {

                    if (user.getBalance() > 0) {

                        user.setBalance(user.getBalance() - 10);
                        SendMessage sendMessage = new SendMessage();
                        sendMessage.setChatId(user.getChatId());
                        sendMessage.setText("Knopka tanlang Balance: " + user.getBalance() + " so'm");
                        execute(sendMessage);

                    } else {
                        SendMessage sendMessage = new SendMessage();
                        sendMessage.setChatId(user.getChatId());
                        sendMessage.setText("Balance yetarli emas");
                        execute(sendMessage);
                    }

                } else if (message.getText().equals(ReplyButtonText.Close)) {
                    user.setStatus(Status.SET_Wait);

                    DeleteMessage deleteMessage = new DeleteMessage();
                    deleteMessage.setChatId(user.getChatId());
                    deleteMessage.setMessageId(user.getShareBalanceMessageId());
                    execute(deleteMessage);

                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(user.getChatId());
                    sendMessage.setText("Xabar qoldiring");
                    execute(sendMessage);
                }
            } else if (user.getStatus().equals(Status.SET_Wait)) {

                if (2 >= user.getCount()) {

                    user.setCount(user.getCount() + 1);
                    user.setMessage(message.getText());
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(user.getChatId());
                    sendMessage.setText("Xabar qoldiring");
                    execute(sendMessage);

                } else {

                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(user.getChatId());
                    sendMessage.setText("Admin Aloqaga chiqadi");
                    execute(sendMessage);

                }

            }
        } else if (message.hasContact()) {
            Contact contact = message.getContact();
            user.setPhoneNumber(contact.getPhoneNumber());
            user.setStatus(Status.SET_LOCATION);

            DeleteMessage deleteMessage = new DeleteMessage();
            deleteMessage.setChatId(user.getChatId());
            deleteMessage.setMessageId(user.getShareContactMessageId());
            execute(deleteMessage);

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(user.getChatId());
            sendMessage.setText("Location kiriting");
            sendMessage.setReplyMarkup(getLocationButton());
            Message execute = execute(sendMessage);
            user.setShareLocationMessageId(execute.getMessageId());

        } else if (message.hasLocation()) {
            Location location = message.getLocation();

            Locations locations = new Locations();
            locations.setLatitude(location.getLatitude());
            locations.setLongitude(location.getLongitude());
            user.setLocation(locations);

            user.setStatus(Status.SET_GENDER);
            DeleteMessage deleteMessage = new DeleteMessage();
            deleteMessage.setChatId(user.getChatId());
            deleteMessage.setMessageId(user.getShareLocationMessageId());
            execute(deleteMessage);

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(user.getChatId());
            sendMessage.setText("Jinsni kiriting");
            sendMessage.setReplyMarkup(genGenderButton());
            Message execute = execute(sendMessage);
            user.setShareGenderMessageId(execute.getMessageId());
        }
    }

    private ReplyKeyboard getBalanceButton() {
        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();


        KeyboardButton keyboardButton = new KeyboardButton();
        row.add(keyboardButton);
        keyboardButton.setText(ReplyButtonText.PLUS);

        KeyboardButton keyboardButton2 = new KeyboardButton();
        row.add(keyboardButton2);
        keyboardButton2.setText(ReplyButtonText.MINUS);

        KeyboardButton keyboardButton3 = new KeyboardButton();
        row.add(keyboardButton3);
        keyboardButton3.setText(ReplyButtonText.Close);

        rows.add(row);

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(rows);
        replyKeyboardMarkup.setResizeKeyboard(true);
        return replyKeyboardMarkup;
    }

    User findUser(Long chatId) {
        for (User user : users) {
            if (user.getChatId().equals(chatId)) {
                return user;
            }
        }
        User user = new User();
        user.setChatId(chatId);
        user.setStatus(Status.START);
        users.add(user);
        return user;
    }


    private ReplyKeyboard getSportButton() {
        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();


        KeyboardButton keyboardButton = new KeyboardButton();
        row.add(keyboardButton);
        keyboardButton.setText(ReplyButtonText.football);

        KeyboardButton keyboardButton2 = new KeyboardButton();
        row.add(keyboardButton2);
        keyboardButton2.setText(ReplyButtonText.basketball);

        KeyboardButton keyboardButton3 = new KeyboardButton();
        row.add(keyboardButton3);
        keyboardButton3.setText(ReplyButtonText.valeyball);

        rows.add(row);

        KeyboardRow row1 = new KeyboardRow();
        KeyboardButton keyboardButtons = new KeyboardButton();
        row1.add(keyboardButtons);
        keyboardButtons.setText("hech qaysi");
        rows.add(row1);

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(rows);
        replyKeyboardMarkup.setResizeKeyboard(true);
        return replyKeyboardMarkup;
    }

    private ReplyKeyboard getLocationButton() {
        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();

        KeyboardButton keyboardButton = new KeyboardButton();
        row.add(keyboardButton);
        keyboardButton.setText("share location");
        keyboardButton.setRequestLocation(true);

        rows.add(row);
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(rows);
        replyKeyboardMarkup.setResizeKeyboard(true);
        return replyKeyboardMarkup;
    }

    private ReplyKeyboard genGenderButton() {
        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();


        KeyboardButton keyboardButton = new KeyboardButton();
        row.add(keyboardButton);
        keyboardButton.setText(ReplyButtonText.MAN);

        KeyboardButton keyboardButton2 = new KeyboardButton();
        row.add(keyboardButton2);
        keyboardButton2.setText(ReplyButtonText.WOMAN);

        rows.add(row);
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(rows);
        replyKeyboardMarkup.setResizeKeyboard(true);
        return replyKeyboardMarkup;
    }

    ReplyKeyboardMarkup genContactButton() {
        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        rows.add(row);

        KeyboardButton button = new KeyboardButton();
        button.setText("Share your contact");
        button.setRequestContact(true);
        row.add(button);

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(rows);
        replyKeyboardMarkup.setResizeKeyboard(true);
        return replyKeyboardMarkup;
    }

    @Override
    public String getBotUsername() {
        return "masharipov_gr01_bot";
    }

    @Override
    public String getBotToken() {
        return "6704541573:AAFD8h6T6NE6FAmhexbClX3Lr1GY2E8XIqM";
    }
}
