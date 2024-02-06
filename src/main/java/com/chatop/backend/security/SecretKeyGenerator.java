package com.chatop.backend.security;

import java.util.Base64;
import java.security.SecureRandom;
import java.util.Base64;

public class SecretKeyGenerator {
        public static void main(String[] args) {
            SecureRandom random = new SecureRandom();
            byte[] key = new byte[32]; // 256 bits sont égaux à 32 octets
            random.nextBytes(key);
            String cleSecreteBase64 = Base64.getEncoder().encodeToString(key);

            System.out.println("Clé secrète encodée en Base64 : " + cleSecreteBase64);
        }
    }
