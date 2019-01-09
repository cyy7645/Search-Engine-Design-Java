package Indexing;

import Classes.Path;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class MyIndexReader {
	private String docNoIdMappingPath, postingPath;
	// 储存 <文档索引，id>
	private HashMap <Integer, String> idDocNoMap;
	private int posting[][];

	// 构造函数
	public MyIndexReader( String type ) throws IOException {
		// 自动选择 /trecweb/docNoIdMapping 或者 /text/docNoIdMapping  都是生成的存在于磁盘的文件
		this.docNoIdMappingPath = (type.equals("trecweb")? Path.IndexWebDir:Path.IndexTextDir)+"docNoIdMapping";
		this.postingPath = (type.equals("trecweb")? Path.IndexWebDir:Path.IndexTextDir)+"Posting";
		BufferedReader idDocBufferedReader = new BufferedReader(new FileReader(docNoIdMappingPath));
		idDocNoMap = new HashMap<>();
		String line;
		// 把文章的索引-id对存放在idDocNoMap中
		while((line = idDocBufferedReader.readLine()) != null){
			String docId[] = line.split(",");
			idDocNoMap.put(Integer.parseInt(docId[0]),docId[1]);
		}
		idDocBufferedReader.close();
	}
	
	// 根据文档索引返回文档id
	public int GetDocid( String docno ) {
		for(Map.Entry entry : idDocNoMap.entrySet()){
			if(entry.getValue().equals(docno)){
				return (int)entry.getKey();
			}
		}
		return -1;
	}

	// 根据文档id返回文档索引
	public String GetDocno( int docid ) {
		if(idDocNoMap.containsKey(docid)){
			return idDocNoMap.get(docid);
		}
		return null;
	}
	
	/**
	 * Get the posting list for the requested token.
	 * 
	 * The posting list records the documents' docids the token appears and corresponding frequencies of the term, such as:
	 *  
	 *  [docid]		[freq]
	 *  1			3
	 *  5			7
	 *  9			1
	 *  13			9
	 * 
	 * ...
	 * 
	 * In the returned 2-dimension array, the first dimension is for each document, and the second dimension records the docid and frequency.
	 * 
	 * For example:
	 * array[0][0] records the docid of the first document the token appears.
	 * array[0][1] records the frequency of the token in the documents with docid = array[0][0]
	 * ...
	 * 
	 * NOTE that the returned posting list array should be ranked by docid from the smallest to the largest. 
	 * 
	 * @param token
	 * @return
	 */
	public int[][] GetPostingList( String token ) throws IOException {
		return posting;
	}

    // 返回包含该token的文档总数
	public int GetDocFreq( String token ) throws IOException {
	    // 根据postingPath读取流
		BufferedReader postingBufferedReader = new BufferedReader(new FileReader(postingPath));
		HashMap<Integer,Integer> idFrequencyMap = new HashMap<>();
		String line;
		while((line = postingBufferedReader.readLine()) != null){
			String term[] = line.split("\t");
			// 如果该token是需要查找的那个
			if(term[0].equals(token)){
				for(int j = 1; j < term.length; j++){
					String docAndFrequency[] = term[j].split(",");
					// 把 <文档id, 频率> 储存在idFrequencyMap中
					idFrequencyMap.put(Integer.parseInt(docAndFrequency[0]), Integer.parseInt(docAndFrequency[1]));
				}
			}
		}
		// 第一列为从0开始的索引，第二列为所在文档的id
		int size = idFrequencyMap.size();
		int i = 0;
		posting = new int [size][2];
		if(size > 0){
			for(int key : idFrequencyMap.keySet()){
				posting[i][0] = key;
				posting[i][1] = idFrequencyMap.get(key);
				i++;
			}
		}
		postingBufferedReader.close();
		return size;
	}
	
	// 返回该token在数据集中出现的总次数
	public long GetCollectionFreq( String token ) throws IOException {
		int res = 0;
		// 遍历posting,把第二列相加即可
		for(int i = 0; i < posting.length; i++){
			res += posting[i][1];
		}
		return res;
	}
	
	public void Close() throws IOException {

	}
	
}