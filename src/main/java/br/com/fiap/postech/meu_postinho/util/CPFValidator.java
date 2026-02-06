package br.com.fiap.postech.meu_postinho.util;

public class CPFValidator {
    
    public static boolean isValid(String cpf) {
        if (cpf == null) {
            return false;
        }
        
        // Remove non-digit characters
        cpf = cpf.replaceAll("\\D", "");
        
        // Check if it has 11 digits
        if (cpf.length() != 11) {
            return false;
        }
        
        // Check if all digits are the same (invalid CPF)
        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }
        
        // Calculate first check digit
        int sum = 0;
        int remainder;
        
        for (int i = 1; i <= 9; i++) {
            sum += Integer.parseInt(cpf.substring(i - 1, i)) * (11 - i);
        }
        
        remainder = (sum * 10) % 11;
        if (remainder == 10 || remainder == 11) {
            remainder = 0;
        }
        
        if (remainder != Integer.parseInt(cpf.substring(9, 10))) {
            return false;
        }
        
        // Calculate second check digit
        sum = 0;
        for (int i = 1; i <= 10; i++) {
            sum += Integer.parseInt(cpf.substring(i - 1, i)) * (12 - i);
        }
        
        remainder = (sum * 10) % 11;
        if (remainder == 10 || remainder == 11) {
            remainder = 0;
        }
        
        return remainder == Integer.parseInt(cpf.substring(10, 11));
    }
    
    public static String formatCPF(String cpf) {
        if (cpf == null) {
            return null;
        }
        cpf = cpf.replaceAll("\\D", "");
        if (cpf.length() == 11) {
            return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "." + 
                   cpf.substring(6, 9) + "-" + cpf.substring(9, 11);
        }
        return cpf;
    }
}
