package ch.unibas.medizin.osce.client.util;
/** This class create hash that is used for password hashing
 * @author manishp
 */
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;

public class HashGenerator {
	public static String generateHash(String randomString){
		
		StringBuffer stringBuffer = new StringBuffer();
		final Logger log = Logger.getLogger(HashGenerator.class);
		try {
		
			//System.out.println("Random no is : " + RandomStringUtils.randomAlphanumeric(10));
			//randomString= RandomStringUtils.randomAlphanumeric(SPConstants.RANDOM_STRING_LENGTH);
			log.info("generating hash of random string");
			MessageDigest md = MessageDigest.getInstance("SHA-256");
		
			md.update(randomString.getBytes());
			 
		    byte byteData[] = md.digest();
		 
		     //convert the byte to hex format method 1
		    for (int count = 0; count < byteData.length; count++) {
		    		stringBuffer.append(Integer.toString((byteData[count] & 0xff) + 0x100, 16).substring(1));
		        }
		 
		} catch (NoSuchAlgorithmException e) {
			log.error(e.getMessage(), e);
			return randomString;
		}
		log.info(" returning generating hash of random string");
	    return stringBuffer.toString();
	}
	
}
