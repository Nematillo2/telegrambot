package org.example.task_telegramsport;

public enum Gender {
    MALE(ReplyButtonText.MAN),
    FEMALE(ReplyButtonText.WOMAN);

    public String nameUz;

    Gender(String nameUz) {
        this.nameUz = nameUz;
    }

}
