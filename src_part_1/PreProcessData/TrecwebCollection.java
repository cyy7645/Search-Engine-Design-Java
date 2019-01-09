package PreProcessData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Classes.Path;

/**
 * 读取原始文件，分成一篇篇文章(id和content)，然后储存在HashMap中
 *
 */
public class TrecwebCollection implements DocumentCollection {
	// Essential private methods or variables can be added.
	// create BufferedReader object
	private BufferedReader buf;
	// for mathching document_id
	private final static String pattern_id = "<DOCNO>(.+)</DOCNO>";

	// YOU SHOULD IMPLEMENT THIS METHOD.
	public TrecwebCollection() throws IOException {
		// 1. Open the file in Path.DataWebDir.
		// 2. Make preparation for function nextDocument().
		// NT: you cannot load the whole corpus into memory!!
        File file = new File(Path.DataWebDir);
        this.buf = new BufferedReader(new FileReader(file));
	}
	
	// YOU SHOULD IMPLEMENT THIS METHOD.
	public Map<String, Object> nextDocument() throws IOException {
		// 1. When called, this API processes one document from corpus, and returns its doc number and content.
		// 2. When no document left, return null, and close the file.
		// 3. the HTML tags should be removed in document content.
        // for every line
        String line = null;
        // for id of document
        String id = null;
        // for content of every document
        String content = null;
        while ((line = buf.readLine()) != null) {
            // reach new document
            if (line.equals("<DOC>")) {
                // reach line starting with <DOCNO>
                line = buf.readLine();
                // create pattern and match object
                Pattern r = Pattern.compile(pattern_id);
                Matcher m = r.matcher(line);
                if (m.find()) {
                    id = m.group(1);
                }
                // reach <DOCHDR>
                while (!(line = buf.readLine()).equals("<DOCHDR>")) ;
                // initialize builder object
                StringBuilder builder = new StringBuilder();
                // get content of WEB
                while (line != null && !(line = buf.readLine()).equals("</DOCHDR>")) {
                    builder.append(line).append(" ");
                }
                content = builder.toString();
                // convert to char array
                char[] textArray = content.toCharArray();
                // store content to hashmap
                Map<String, Object> textMap = new HashMap();
                textMap.clear();
                // put key-value pair into hashmap
                textMap.put(id, textArray);
                return textMap;
            }
        }

        buf.close();
		return null;
	}
	
}
