package com.flashpoint.mitto.aes;

//import android.widget.Toast;

import java.io.File;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.Key;
/**
 * Created by hashdroid on 4/7/2018.
 */
public class TestFileEncryption {
    private static final String ALGORITHM = "Blowfish";
    private static String keyString = "DesireSecretKey";
    public static void encrypt(File inputFile, File outputFile)
            throws Exception {
        doCrypto(Cipher.ENCRYPT_MODE, inputFile, outputFile);
    }
    public static void decrypt(File inputFile, File outputFile)
            throws Exception {
        doCrypto(Cipher.DECRYPT_MODE, inputFile, outputFile);
    }
    private static void doCrypto(int cipherMode, File inputFile,
                                 File outputFile) throws Exception {
        Key secretKey = new SecretKeySpec(keyString.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(cipherMode, secretKey);

        FileInputStream inputStream = new FileInputStream(inputFile);
        byte[] inputBytes = new byte[(int) inputFile.length()];
        inputStream.read(inputBytes);

        byte[] outputBytes = cipher.doFinal(inputBytes);

        FileOutputStream outputStream = new FileOutputStream(outputFile);
        outputStream.write(outputBytes);

        inputStream.close();
        outputStream.close();

    }
}