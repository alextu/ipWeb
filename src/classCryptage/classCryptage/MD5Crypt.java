package classCryptage;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import com.webobjects.foundation.NSTimestamp;

import fr.univlr.cri.webapp.LRLog;


public class MD5Crypt
{
    /* Define our magic string to mark salt for MD5 "encryption"
       replacement.  This is meant to be the same as for other MD5 based
       encryption implementations.  */
    private static final byte [] salt_prefix = {'$', '1', '$'};

    public static byte[] crypt(byte[] key, byte[] salt)
	throws NoSuchAlgorithmException
    {
	byte[] buffer;
	byte[] alt_result;
	MessageDigest md5 = MessageDigest.getInstance("md5");
	MessageDigest alt_md5 = MessageDigest.getInstance("md5");
	int salt_len;
	int key_len;
	int cnt;
	int cp;

	/* Find beginning of salt string.  The prefix should normally always
	   be present.  Just in case it is not.  */
	if (startsWith(salt, salt_prefix)) {
	    /* Skip salt prefix.  */
	    salt_len = salt.length - salt_prefix.length;
	    byte[] copied_salt = new byte[salt_len];
	    System.arraycopy(salt, salt_prefix.length, copied_salt, 0, salt_len);
	    salt = copied_salt;
	}

	salt_len = Math.min(indexOf(salt, (byte) '$'), 8);
	if (salt_len == -1)
	    salt_len = 8;
	key_len = key.length;
//	buffer = new byte[salt_prefix.length + salt_len + 1 + 22];
	buffer = new byte[salt_prefix.length + salt_len + 1 + 41];

	/* Prepare for the real work.  */
	md5.reset();

	/* Add the key string.  */
	md5.update(key, 0, key_len);

        /* Because the SALT argument need not always have the salt prefix we
	   add it separately.  */
	md5.update(salt_prefix, 0, salt_prefix.length);

	/* The last part is the salt string.  This must be at most 8
	   characters and it ends at the first `$' character (for
	   compatibility which existing solutions).  */
	md5.update(salt, 0, salt_len);


	/* Compute alternate MD5 sum with input KEY, SALT, and KEY.  The
	   final result will be added to the first context.  */
	alt_md5.reset();

	/* Add key.  */
	alt_md5.update(key, 0, key_len);

	/* Add salt.  */
	alt_md5.update(salt, 0, salt_len);

	/* Add key again.  */
	alt_md5.update(key, 0, key_len);

	/* Now get result of this (16 bytes) and add it to the other
	   context.  */
	alt_result = alt_md5.digest();

	/* Add for any character in the key one byte of the alternate sum.  */
	for (cnt = key_len; cnt > 16; cnt -= 16)
	    md5.update(alt_result, 0, 16);
	md5.update(alt_result, 0, cnt);

	/* For the following code we need a NUL byte.  */
	alt_result[0] = '\000';

	/* The original implementation now does something weird: for every 1
	   bit in the key the first 0 is added to the buffer, for every 0
	   bit the first character of the key.  This does not seem to be
	   what was intended but we have to follow this to be compatible.  */
	for (cnt = key_len; cnt > 0; cnt >>= 1)
	  md5.update((cnt & 1) != 0 ? alt_result : key, 0, 1);

	/* Create intermediate result.  */
	alt_result = md5.digest();

	/* Now comes another weirdness.  In fear of password crackers here
	   comes a quite long loop which just processes the output of the
	   previous round again.  We cannot ignore this here.  */
	for (cnt = 0; cnt < 1000; ++cnt)
	{
	    /* New context.  */
	    md5.reset();

	    /* Add key or last result.  */
	    if ((cnt & 1) != 0)
		md5.update(key, 0, key_len);
	    else
		md5.update(alt_result, 0, 16);

	    /* Add salt for numbers not divisible by 3.  */
	    if (cnt % 3 != 0)
		md5.update(salt, 0, salt_len);

	    /* Add key for numbers not divisible by 7.  */
	    if (cnt % 7 != 0)
		md5.update(key, 0, key_len);

	    /* Add key or last result.  */
	    if ((cnt & 1) != 0)
		md5.update(alt_result, 0, 16);
	    else
		md5.update(key, 0, key_len);

	    /* Create intermediate result.  */
	    alt_result = md5.digest();
	}


	/* Now we can construct the result string.  It consists of three
	   parts.  */
	System.arraycopy(salt_prefix, 0, buffer, 0, salt_prefix.length);
	cp = salt_prefix.length;

	System.arraycopy(salt, 0, buffer, cp, salt_len);
	cp += salt_len;

	buffer[cp++] = '$';

	cp += b64_from_24bit(buffer, cp, alt_result[0],
			     alt_result[6], alt_result[12], 4);

	cp += b64_from_24bit(buffer, cp, alt_result[1],
			     alt_result[7], alt_result[13], 4);

	cp += b64_from_24bit(buffer, cp, alt_result[2],
			     alt_result[8], alt_result[14], 4);

	cp += b64_from_24bit(buffer, cp, alt_result[3],
			     alt_result[9], alt_result[15], 4);

	cp += b64_from_24bit(buffer, cp, alt_result[4],
			     alt_result[10], alt_result[5], 4);

	cp += b64_from_24bit(buffer, cp, (byte) 0,
			     (byte) 0, alt_result[11], 2);
 			System.out.println("cp:"+new Integer(cp).toString());

	buffer[cp] = '\000';

	return buffer;
    }


    private final static byte [] b64t = {  '.', '/', '0', '1', '2', '3',
	'4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G',
	'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
	'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g',
	'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
	'u', 'v', 'w', 'x', 'y', 'z' };

    private static int b64_from_24bit(byte[] buffer, int cp, byte B2,
				      byte B1, byte B0, int N)
    {
	int w = ((((int) B2) & 0xFF) << 16) | ((((int) B1) & 0xFF) << 8) | B0;
	int n = N;
	while (n-- > 0) {
	    buffer[cp++] = b64t[w & 0x3f];
	    w >>= 6;
	}
	return N;
    }


    private static boolean startsWith(byte[] ar, byte[] pre)
    {
	if (ar.length < pre.length)
	    return false;

	for (int i = 0; i < pre.length; i++)
	    if (ar[i] != pre[i])
	        return false;

	return true;
    }


    private static int indexOf(byte[] ar, byte b)
    {
	for (int i = 0; i < ar.length; i++)
	    if (ar[i] == b)
		return i;

	return ar.length;
    }
    
    public static String crypt(String key, String salt)	throws NoSuchAlgorithmException{
 
  	byte[] md5crypt = crypt(key.getBytes(), salt.getBytes());

        StringBuffer unString=new StringBuffer();
        for (int i=0;i<md5crypt.length;i++) {
            unString.append((char)md5crypt[i]);
        }
//        System.out.println("key crypte:"+unString.toString());

        return unString.toString();

    }
	
   public static void main(String[] args) throws NoSuchAlgorithmException{
       try{
           String messCode=MD5Crypt.crypt(args[0], args[1]);
           System.out.println("message initial:"+args[0]+"  salt:"+args[1]+"  message code:"+messCode);

       }
       catch (NoSuchAlgorithmException e) {
           throw new Error("no MD5 support in this VM");
       }
   }
    
     /**
   * convert an encoded unsigned byte value into a int
   * with the unsigned value.
   */

	/**
	 * G�n�ration d'un salt de 8 caract�re aleatoire
	 * @return le password non crypter !!!!
	 */
	public static String genSalt(){
		char[] pwd = new char[8];
		char[] listeCarAuto = {'2','3','4','5','6','7','8','9',
		'a','b','c','d','e','f','g','h','j','k','m','n','p','q','r','s','t','u','v','w','x','y','z',
		'A','B','C','D','E','F','G','H','J','K','L','M','N','P','Q','R','S','T','U','V','W','X','Y','Z'};
		listeCarAuto = (new String(listeCarAuto)).toCharArray();
		Random nb = new Random((new NSTimestamp()).getTime());
		
		for (int i = 0; i < 8; i++) {
			int length = nb.nextInt(1000)+1;
			int noCar=0;
			Random rand = new Random((new NSTimestamp()).getTime()*length);
			for (int j = 0; j < length; j++) {
				noCar = rand.nextInt(listeCarAuto.length);
			}
			if((new String(pwd)).indexOf(listeCarAuto[noCar])<0)
				pwd[i] = listeCarAuto[noCar];
			else
				i = i-1;
			LRLog.trace("i = "+i);
		}
		return new String(pwd);
	}
}
