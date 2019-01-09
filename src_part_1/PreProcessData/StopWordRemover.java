package PreProcessData;
import Classes.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * 加载stopwords，用set储存，提供isStopword判断时候为stopword
 */
public class StopWordRemover {
	// Essential private methods or variables can be added.

    // create BufferedReader object
    private BufferedReader buf;
    // for every line
    private String line = null;
    // for stopwords
    private Set<String> stopWords = new HashSet<String>();
    // YOU SHOULD IMPLEMENT THIS METHOD.
	public StopWordRemover( ) throws IOException {
		// Load and store the stop words from the fileinputstream with appropriate data structure.
		// NT: address of stopword.txt is Path.StopwordDir
        File file = new File(Path.StopwordDir);
        this.buf = new BufferedReader(new FileReader(file));
        // read every stopwords
        while ((line = buf.readLine()) != null){
            stopWords.add(line);
        }
	}

	// YOU SHOULD IMPLEMENT THIS METHOD.
	public boolean isStopword( char[] word ) {
		// Return true if the input word is a stopword, or false if not.
        if(stopWords.contains(new String(word))) {
            return true;
        }
        else
            return false;
	}
}
