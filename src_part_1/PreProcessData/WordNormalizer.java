package PreProcessData;

import Classes.*;

/**
 * 对单词token的操作，提供lowercase()方法和stem()方法
 * 
 */
public class WordNormalizer {
	// Essential private methods or variables can be added.

	// YOU MUST IMPLEMENT THIS METHOD.
	public char[] lowercase(char[] chars) {
		// Transform the word uppercase characters into lowercase.
		for(int i=0; i<chars.length; i++){
		    // if char is a character
			if (chars[i] >= 'a' && chars[i] <= 'z'){
				chars[i]=Character.toLowerCase(chars[i]);
			}
		}

		return chars;
	}

	// YOU MUST IMPLEMENT THIS METHOD.
	public String stem(char[] chars) {
		// Return the stemmed word with Stemmer in Classes package.
        Stemmer stem = new Stemmer();
        stem.add(chars, chars.length);
        stem.stem();
		String str = "";
		str = stem.toString();
		return str;
	}

}
