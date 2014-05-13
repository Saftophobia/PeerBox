package PeerBox;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionUtils {

	// generates a 128-bit AES key
	private static String generateAESSecret() throws NoSuchAlgorithmException {
		KeyGenerator kgen = KeyGenerator.getInstance("AES");
		kgen.init(128);
		SecretKey skey = kgen.generateKey();
		byte[] raw = skey.getEncoded();

		return toHexString(raw);
	}

	// returns MD5 digest of given data as a string
	public static String getMD5Hash(byte[] data)
			throws NoSuchAlgorithmException {
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		byte[] hash = md5.digest(data);

		return toHexString(hash);
	}

	// encrypts data using AES with key secretKey
	// returns Object[] 
	// {String secretKey, String initializationVector, byte[] encryptedData}
	public static Object[] encryptAES(byte[] data)
			throws NoSuchAlgorithmException, NoSuchPaddingException,
			IllegalBlockSizeException, BadPaddingException,
			InvalidKeyException, InvalidParameterSpecException {

		String secretKeyString = EncryptionUtils.generateAESSecret();
		byte[] secretKeyBytes = EncryptionUtils.fromHexString(secretKeyString);
		SecretKeySpec secretKey = new SecretKeySpec(secretKeyBytes, "AES");
		
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);

		byte[] iv = cipher.getParameters()
				.getParameterSpec(IvParameterSpec.class).getIV();
		// System.out.println("iv:	 	" + toHexString(iv));

		byte[] encryptedData = cipher.doFinal(data);

		return new Object[] { toHexString(secretKeyBytes), toHexString(iv), encryptedData };
	}

	// decrypts data using AES given key secretKey an initialization vector iv
	public static byte[] decryptAES(byte[] data, byte[] secretKey, byte[] iv)
			throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, InvalidAlgorithmParameterException,
			IllegalBlockSizeException, BadPaddingException {
		SecretKeySpec secret = new SecretKeySpec(secretKey, "AES");
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
		byte[] decryptedBytes = cipher.doFinal(data);
		return decryptedBytes;
	}

	// encodes bytes as hexadecimal string
	public static String toHexString(byte[] bytes) {
		String string = "";
		for (int i = 0; i < bytes.length; i++) {
			if ((0xff & bytes[i]) < 0x10) {
				string += "0" + Integer.toHexString((0xFF & bytes[i]));
			} else {
				string += Integer.toHexString(0xFF & bytes[i]);
			}
		}
		return string;
	}

	// decodes hexadecimal string back to bytes
	public static byte[] fromHexString(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
					.digit(s.charAt(i + 1), 16));
		}
		return data;
	}
	
	public static byte[] encryptWithPK(PublicKey pk, byte[] data){
		try {
		    // create RSA public key cipher
		    Cipher pkCipher = Cipher.getInstance("RSA");
		    pkCipher.init(Cipher.ENCRYPT_MODE, pk);  
		    //encode
		    byte[] encrypted = pkCipher.doFinal(data);
			return encrypted;
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}