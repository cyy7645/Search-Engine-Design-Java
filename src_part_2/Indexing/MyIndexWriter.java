package Indexing;

import Classes.Path;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

// 文件很大，如何避免挤满内存
// 把原文件以60000篇文章为一个block，先对每个block进行词频统计，生成temp文件存硬盘，
// 然后遍历每个block，合并block成一个大文件， 这个文件中同样的单词出现了很多次，因为还是简单的合并

public class MyIndexWriter {
	// I suggest you to write very efficient code here, otherwise, your memory cannot hold our corpus...
	private HashMap<Integer, String> idDocNoMap;
	private HashMap<String, HashMap<Integer,Integer>> tokenIdFrequencyMap;
	// 文档编号  从1开始
	private int index = 1;
	// 临时存储块编号 从1开始
	private int blockNo = 1;
	private String type;
	private BufferedWriter idDocBufferedWriter;

	// 构造函数
	public MyIndexWriter(String type) throws IOException {
		// This constructor should initiate the FileWriter to output your index files
		// remember to close files if you finish writing the index
		this.type = type;
		this.idDocNoMap = new HashMap<>();
		// 存放 <单词,<文档id, 出现频率>> !!!!!
		this.tokenIdFrequencyMap = new HashMap<>();
		// 自动选择 web数据集或者Text数据集
		this.idDocBufferedWriter = new BufferedWriter(new FileWriter((type.equals("trecweb")? Path.IndexWebDir:Path.IndexTextDir)+"docNoIdMapping"));
	}
	
	public void IndexADocument(String docno, String content) throws IOException {
		// you are strongly suggested to build the index by installments
		// you need to assign the new non-negative integer docId to each document, which will be used in MyIndexReader
		idDocNoMap.put(index, docno); // 存放<索引1开始,文档编号> e.g. <1, lists-000-0000000>
		String [] temp = content.split(" "); // 内容 e.g. [http://lists.w3.org/Archives/AboutArchives.html, Connect, close, Date, Tue, 15, Jun, 2004, 08:37:39, GMT, Content-Length, 11892, Content-Typ, text/html, Etag, 1pj15e7:uu8l86cg, Last-Modifi, Thu, 11, Sep, 2003, 15:45:47, GMT, Server ]
		HashMap<Integer,Integer> posting = new HashMap<>();
        // 遍历 content中的每一个单词
		for(String token : temp){
			if(tokenIdFrequencyMap.containsKey(token)){
				posting = tokenIdFrequencyMap.get(token);
				if(posting.containsKey(index)){
					posting.put(index, posting.get(index)+1);
				}else{
					posting.put(index, 1);
				}
			}
			else{
			    // 存放 <文档id, 出现次数> 代表该单词在该文档出现的次数
				posting = new HashMap<>();
				posting.put(index, 1);
				tokenIdFrequencyMap.put(token, posting);
			}
		}
        // 记录遍历了多少篇文章，每60000篇保存一次
		index = index + 1;
		if(index % 60000 == 0){
			saveBlock();
		}
	}
	
	public void Close() throws IOException {
		// close the index writer, and you should output all the buffered content (if any).
		// if you write your index into several files, you need to fuse them here.
        // 保存最后一个block内容  关闭写入流
		saveBlock();
		fuse();
		idDocBufferedWriter.close();
	}

	public void saveBlock() throws IOException {
	    // key为索引 idDocNoMap.get(key)得到文档编号  把该内容写入文档
		for(Integer key : idDocNoMap.keySet()){
			idDocBufferedWriter.write(key+","+idDocNoMap.get(key)+"\n");
		}
        // 新建一个写入缓存流  用于写入  <单词,<文档id, 出现频率>>
		BufferedWriter tempBufferedWriter = new BufferedWriter(new FileWriter((type.equals("trecweb")? Path.IndexWebDir:Path.IndexTextDir)+".temp"+blockNo));
		for(String key : tokenIdFrequencyMap.keySet()){
		    // key为单词 用\t分割
			tempBufferedWriter.write(key + "\t");
			HashMap<Integer, Integer> map = tokenIdFrequencyMap.get(key);
			for(Map.Entry entry : map.entrySet()){
			    // 文档索引,单词出现频率
                // 1,3  在索引为1的文档中 该单词出现了3次
                // 2,2
                // 3,9
				tempBufferedWriter.write(entry.getKey()+","+entry.getValue()+"\t");
			}
			// 插入空行  即每个token为一行 e.g. hello    1,3    2,2    3,9\t
			tempBufferedWriter.write("\n");
		}
		// 每次写入完成要clear缓存，关闭写入流
		blockNo++;
		idDocNoMap.clear();
		tokenIdFrequencyMap.clear();
		tempBufferedWriter.close();
	}

	public void fuse() throws IOException {
		BufferedWriter bufferedWriter = new BufferedWriter((new FileWriter((type.equals("trecweb")? Path.IndexWebDir:Path.IndexTextDir)+"Posting")));
		// 储存每个单词出现的总频率
		BufferedWriter termColFrequencybufferedWriter = new BufferedWriter(new FileWriter((type.equals("trecweb")? Path.IndexWebDir:Path.IndexTextDir)+"Dictionary Term"));
		// 对之前的所有Block遍历
		for(int i = 1; i < blockNo; i++){
		    // 读取硬盘上对应的block内容
			BufferedReader bufferedReader = new BufferedReader(new FileReader((type.equals("trecweb")? Path.IndexWebDir+".temp":Path.IndexTextDir+".temp")+i));
			String line;
            // 每次读取一行  e.g. hello    1,3    2,2    3,9
			while((line = bufferedReader.readLine()) != null){
				bufferedWriter.write(line+"\n");

				String term[] = line.split("\t");
				// key 为单词
				String key = term[0];
				// count 记录每个单词出现的总频率
				int count = 0;
				// 遍历每个<文档id, 频率>
				for(int j = 1; j < term.length; j++){
					String docAndFrequency[] = term[j].split(",");
					count += Integer.parseInt(docAndFrequency[1]);
				}
				termColFrequencybufferedWriter.write(key+" "+count+"\n");
			}
			bufferedReader.close();
			File file = new File((type.equals("trecweb")? Path.IndexWebDir+".temp":Path.IndexTextDir+".temp")+i);
			// 删除该block
			file.delete();
		}
		// 关闭 写入流
		bufferedWriter.close();
		termColFrequencybufferedWriter.close();
	}
}
