package br.com.fiap.postech.meu_postinho.util;

import java.util.regex.Pattern;

public class PhoneValidator {
    
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^\\(?\\d{2}\\)?\\s?9?\\d{4}-?\\d{4}$"
    );
    
    public static boolean isValid(String phone) {
        if (phone == null) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone).matches();
    }
    
    public static String formatPhone(String phone) {
        if (phone == null) {
            return null;
        }
        phone = phone.replaceAll("\\D", "");
        
        if (phone.length() == 10) {
            return "(" + phone.substring(0, 2) + ") " + phone.substring(2, 6) + "-" + phone.substring(6, 10);
        } else if (phone.length() == 11) {
            return "(" + phone.substring(0, 2) + ") 9" + phone.substring(2, 7) + "-" + phone.substring(7, 11);
        }
        return phone;
    }
}
