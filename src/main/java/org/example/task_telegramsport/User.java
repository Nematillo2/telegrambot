package org.example.task_telegramsport;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Long chatId;
    private String lastName;
    private String firstName;
    private String phoneNumber;
    private Locations location;
    private Gender gender;
    private Status status;
    private String sport;
    private int balance;
    private String message;
    private int count = 1;
    private Integer shareContactMessageId;
    private Integer shareSportMessageId;
    private Integer shareGenderMessageId;
    private Integer shareLocationMessageId;
    private Integer shareBalanceMessageId;


}
