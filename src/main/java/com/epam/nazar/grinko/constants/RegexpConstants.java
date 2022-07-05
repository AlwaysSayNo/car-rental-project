package com.epam.nazar.grinko.constants;

public interface RegexpConstants {

    String NON_EMPTY_STRING_REGEXP = "[A-Za-z0-9\\-_]{1,}";

    String EMAIL = "[A-Za-z0-9]{2,16}(\\.{1}[A-Za-z0-9]{2,16})*@([A-Za-z0-9\\-]{2,8})(\\.{1}[A-Za-z0-9\\-]{2,8})*";
    String PASSWORD = "[A-Za-z0-9\\-_\\$]{4,}";
    String FIRST_NAME = "([A-ZÀ-ÿА-ЯЄІЇ][\\-,a-z\\. 'а-яєії]+[ ]*)+";
    String LAST_NAME = "([A-ZÀ-ÿА-ЯЄІЇ][\\-,a-z\\. 'а-яєії]+[ ]*)+";
    String PHONE_NUMBER = "[\\+]{1}([0-9]{3})(([ \\.\\-]?)([0-9]{2}))(([ \\.\\-]?)([0-9]{3}))(([ \\.\\-]?)([0-9]{2})){2}";

    String CAR_NUMBER_REGEXP = "[A-Z]{2}[ ]?[0-9]{4}[ ]?[A-Z]{2}";

}
