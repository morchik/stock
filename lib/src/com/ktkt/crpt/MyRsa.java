package com.ktkt.crpt;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class MyRsa {
	// 

	public static byte hex2Byte(char a1, char a2) {
		int k;
		if (a1 >= '0' && a1 <= '9')
			k = a1 - 48;
		else if (a1 >= 'a' && a1 <= 'f')
			k = (a1 - 97) + 10;
		else if (a1 >= 'A' && a1 <= 'F')
			k = (a1 - 65) + 10;
		else
			k = 0;
		k <<= 4;
		if (a2 >= '0' && a2 <= '9')
			k += a2 - 48;
		else if (a2 >= 'a' && a2 <= 'f')
			k += (a2 - 97) + 10;
		else if (a2 >= 'A' && a2 <= 'F')
			k += (a2 - 65) + 10;
		else
			k += 0;
		return (byte) (k & 0xff);
	}

	public static byte[] hex2Byte(String str) {
		int len = str.length();
		if (len % 2 != 0)
			return null;
		byte r[] = new byte[len / 2];
		int k = 0;
		for (int i = 0; i < str.length() - 1; i += 2) {
			r[k] = hex2Byte(str.charAt(i), str.charAt(i + 1));
			k++;
		}
		return r;
	}

	public static String byte2Hex(byte b[]) {
		java.lang.String hs = "";
		java.lang.String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = java.lang.Integer.toHexString(b[n] & 0xff);
			if (stmp.length() == 1)
				hs = hs + "0" + stmp;
			else
				hs = hs + stmp;
		}
		return hs.toLowerCase();
	}
	
	private static byte[] getPubKey(String hexKey) {
		return CryptoUtil.hex2Byte(hexKey);
	}

	private static byte[] getPubKey() {
		return CryptoUtil.hex2Byte("30820122300d06092a864886f70d01010105000382010f003082010a02820101009fde491de233924fb1924d070e20468a36ca5f9a7a022a6e18bec96cdcedf4e7a78e0a142b27a4a3b1a995cc01bf6479ebbafb768a2afb7c10e94519d17eeaa08cc599b3fb35052b493520ff0ccb65bdeed6153fd72b42b60e4c6c5ff815c78a6adc73535c918f0e7e5f17f7c1454ca93671099ab682299cb741e3616ce7c453cf79262ed84a6f1eaf4965fe16b3f51fbe93bf4078a63cd20cf7765bfe0b5d5755ff1a3ea6007f652d764a9747ea872617cb332c4c137f61d616c2a66d2d1e1cc4f1c75320e5ead7aa2430bd938917b04293df97efe2a7737472d377becf89d7a5c8d8378b0201ee9b73241ded29d0aded21c5bdec45a2179a0d28b29c039b4f0203010001");
	}

	private static PublicKey getPublicKey(String hexKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
		KeyFactory keyFactory = KeyFactory.getInstance(CryptoUtil.ALGORITHM);
		EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(getPubKey(hexKey));
		return keyFactory.generatePublic(publicKeySpec);
	}
	
	// only less than 240 chars
	public static String encrypt(String text, String hexKey) {
		if (text.length() > 240) // only truncate now
			text = text.substring(0, 240);
		try {
			return CryptoUtil.byte2Hex(CryptoUtil.encrypt(text, getPublicKey(hexKey)));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return text+e.toString();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
			return text+e.toString();
		}
	}

	public static String[] generateKey() {
		String res[] = new String[]{"", ""};
		try {
			final KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA", "BC");
			keyGen.initialize(2048, new SecureRandom());
			final KeyPair key = keyGen.generateKeyPair();

			//FileOutputStream privateKeyFile = new FileOutputStream(PRIVATE_KEY_FILE, true);
			//FileOutputStream publicKeyFile = new FileOutputStream(PUBLIC_KEY_FILE, true);

			//BufferedWriter pubOut = new BufferedWriter(new OutputStreamWriter(publicKeyFile));
			res[0] = byte2Hex(key.getPublic().getEncoded());
			//pubOut.flush();
			//pubOut.close();

			//BufferedWriter privOut = new BufferedWriter(new OutputStreamWriter(privateKeyFile));
			res[1] = byte2Hex(key.getPrivate().getEncoded());
			//privOut.flush();
			//privOut.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	public static void main() throws Exception {
		// Security.addProvider(new
		// org.bouncycastle.jce.provider.BouncyCastleProvider());

		byte[] input = new byte[] { (byte) 0x45, (byte) 0x55 };
		System.out.println("input: " + new String(input));
		Cipher cipher = Cipher.getInstance("RSA/None/NoPadding", "BC");
		/*
		 * KeyFactory keyFactory = KeyFactory.getInstance("RSA", "BC");
		 * RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(new BigInteger(
		 * "12345678", 16), new BigInteger("11", 16)); RSAPrivateKeySpec
		 * privKeySpec = new RSAPrivateKeySpec(new BigInteger( "12345678", 16),
		 * new BigInteger("12345678", 16));
		 * 
		 * RSAPublicKey pubKey = (RSAPublicKey) keyFactory
		 * .generatePublic(pubKeySpec); RSAPrivateKey privKey = (RSAPrivateKey)
		 * keyFactory .generatePrivate(privKeySpec);
		 */
		SecureRandom random = new SecureRandom();
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "BC");

		generator.initialize(256, random);
		KeyPair pair = generator.generateKeyPair();
		Key pubKey = pair.getPublic();
		Key privKey = pair.getPrivate();

		System.out.println("pubKey: " + pubKey.toString());
		System.out.println("privKey: " + privKey.toString());

		cipher.init(Cipher.ENCRYPT_MODE, pubKey);

		byte[] cipherText = cipher.doFinal(input);
		System.out.println("cipher: " + new String(cipherText));

		cipher.init(Cipher.DECRYPT_MODE, privKey);
		byte[] plainText = cipher.doFinal(cipherText);
		System.out.println("plain : " + new String(plainText));
	}
}