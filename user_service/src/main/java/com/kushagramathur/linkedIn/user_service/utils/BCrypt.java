package com.kushagramathur.linkedIn.user_service.utils;

import static org.mindrot.jbcrypt.BCrypt.*;

public class BCrypt {

    public static String hash(String s) {
        return hashpw(s, gensalt());
    }

    public static boolean match(String s, String hash) {
        return checkpw(s, hash);
    }

}
