package com.example.qrallye;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class SessionMGR {

    public static Team team;

    public SessionMGR() {
    }
    public  static boolean isLoged(){
        return false;
    }
    public static  boolean loging(String user,String password){

        //return password.equals(requirePassOf(user));
        return true;
    }
    public  boolean logout(){
        return false;
    }
    public String getUser(){
        return "";
    }
    public String encript(String text){
        try {
            String key = "Bar12345Bar12345"; // 128 bit key
            // Create key and cipher
            Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            // encrypt the text
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            byte[] encrypted = cipher.doFinal(text.getBytes());

            System.err.println(new String(encrypted));

            return new String(encrypted);

            // decrypt the text
            //cipher.init(Cipher.DECRYPT_MODE, aesKey);
            //String decrypted = new String(cipher.doFinal(encrypted));
            //System.err.println(decrypted);
        }
        catch (Exception e){
            return "";
        }

    }
    public void setTeam(Team team) {
        this.team = team;
    }
}
