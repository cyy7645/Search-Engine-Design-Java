package Indexing;

import Classes.Path;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PreProcessedCorpusReader {
	private BufferedReader bufferedReader;
	
	// PreProcessedCorpusReader的构造函数
	public PreProcessedCorpusReader(String type) throws IOException {
        // 读取results.trecweb文件
        // 每条记录如下
        // lists-000-0012197
        // http://lists.w3.org/Archives/Public/copras-public/2004JanMar/0000.html Cache-Control max-age=31104000 Connect close Date Tue 15 Jun 2004 08:37:48 GMT Content-Length 5917 Content-Languag en Content-Typ text/html Etag 1bfsetn:vcifshbo Expire Fri 10 Jun 2005 08:37:48 GMT Last-Modifi Tue 25 Mai 2004 00:06:32 GMT Server
		File file = new File(Path.ResultHM1 + type);
		this.bufferedReader = new BufferedReader(new FileReader(file));
	}
	
    // 用于读取下一个文件，返回<文档编号，内容>的Map
	public Map<String, String> NextDocument() throws IOException {
		// read a line for docNo, put into the map with <"DOCNO", docNo>
		// read another line for the content , put into the map with <"CONTENT", content>
		String docNo, content;
		HashMap<String, String> map = new HashMap<>();
		if((docNo = bufferedReader.readLine()) != null){
			map.put("DOCNO", docNo);
			content = bufferedReader.readLine();
			map.put("CONTENT", content);

			return map;
		// 读到最后要关闭流
		}else {
			bufferedReader.close();
			return null;
		}

	}

}
