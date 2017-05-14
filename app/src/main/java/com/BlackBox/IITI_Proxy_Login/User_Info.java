package com.BlackBox.IITI_Proxy_Login;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Base64;

import java.security.spec.KeySpec;
import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

class User_Info {

    private String id;
    private String pwd;

    String getID() {
        return id;
    }

    void setID(String id) {
        this.id = id;
    }

    String getpwd() {
        return pwd;
    }

    void setpwd(String pwd) {
        this.pwd = pwd;
    }

    boolean load_Cred(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean checkBoxValue = sharedPreferences.getBoolean("CheckBox_Value", false);
        String tmp_id = sharedPreferences.getString("saved_id", "");
        String tmp_pwd = sharedPreferences.getString("saved_pwd", "");
        String seed = sharedPreferences.getString("saved_seed", "");
        try {
            id = CryptoHelper.decrypt(seed, tmp_id);
            pwd = CryptoHelper.decrypt(seed, tmp_pwd);
        } catch (Exception e) {
            id = "";
            pwd = "";
        }
        return checkBoxValue;
    }

    void save_cred(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String tmp_id = "";
        String tmp_pwd = "";
        String seed = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            seed = Base64.encodeToString(md.digest(id.getBytes()), Base64.DEFAULT).trim();
            tmp_id = CryptoHelper.encrypt(seed, id);
            tmp_pwd = CryptoHelper.encrypt(seed, pwd);
        } catch (Exception e) {
            e.printStackTrace();
        }
        editor.putBoolean("CheckBox_Value", true);
        editor.putString("saved_id", tmp_id);
        editor.putString("saved_pwd", tmp_pwd);
        editor.putString("saved_seed", seed);
        editor.apply();

    }

    private static class CryptoHelper {

        // Algorithm used
        private final static String ALGORITHM = "AES";

        /**
         * Encrypt data
         *
         * @param secretKey - a secret key used for encryption
         * @param data      - data to encrypt
         * @return Encrypted data
         * @throws Exception unknown
         */
        private static String encrypt(String secretKey, String data) throws Exception {

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            KeySpec spec = new PBEKeySpec(secretKey.toCharArray(), secretKey.getBytes(), 128, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKey key = new SecretKeySpec(tmp.getEncoded(), ALGORITHM);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);

            return toHex(cipher.doFinal(data.getBytes()));
        }

        /**
         * Decrypt data
         *
         * @param secretKey - a secret key used for decryption
         * @param data      - data to decrypt
         * @return Decrypted data
         * @throws Exception unknown
         */
        private static String decrypt(String secretKey, String data) throws Exception {

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            KeySpec spec = new PBEKeySpec(secretKey.toCharArray(), secretKey.getBytes(), 128, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKey key = new SecretKeySpec(tmp.getEncoded(), ALGORITHM);

            Cipher cipher = Cipher.getInstance(ALGORITHM);

            cipher.init(Cipher.DECRYPT_MODE, key);

            return new String(cipher.doFinal(toByte(data)));
        }

// Helper methods

        private static byte[] toByte(String hexString) {
            int len = hexString.length() / 2;

            byte[] result = new byte[len];

            for (int i = 0; i < len; i++)
                result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2), 16).byteValue();
            return result;
        }

        private static String toHex(byte[] stringBytes) {
            StringBuffer result = new StringBuffer(2 * stringBytes.length);

            for (int i = 0; i < stringBytes.length; i++) {
                result.append(HEX.charAt((stringBytes[i] >> 4) & 0x0f)).append(HEX.charAt(stringBytes[i] & 0x0f));
            }

            return result.toString();
        }

        private final static String HEX = "0123456789ABCDEF";
    }

}