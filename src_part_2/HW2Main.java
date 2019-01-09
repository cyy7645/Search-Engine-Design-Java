import java.util.Map;
import Indexing.*;

/**
 * !!! YOU CANNOT CHANGE ANYTHING IN THIS CLASS !!!
 * 
 * Main class for running your HW2.
 * 
 */
public class HW2Main {

	public static void main(String[] args) throws Exception {
	    // 实例化当前类
		HW2Main hm2 = new HW2Main();
		// 记录时间
		long startTime=System.currentTimeMillis();
		// 调用 WriteIndex方法
		hm2.WriteIndex("trecweb");

		long endTime=System.currentTimeMillis();
		System.out.println("index web corpus running time: "+(endTime-startTime)/60000.0+" min");
		startTime=System.currentTimeMillis();
		hm2.ReadIndex("trecweb", "acow");
		endTime=System.currentTimeMillis();
		System.out.println("load index & retrieve running time: "+(endTime-startTime)/60000.0+" min");

		startTime=System.currentTimeMillis();
		hm2.WriteIndex("trectext");
		endTime=System.currentTimeMillis();
		System.out.println("index text corpus running time: "+(endTime-startTime)/60000.0+" min");
		startTime=System.currentTimeMillis();
		hm2.ReadIndex("trectext", "bachelor");
		endTime=System.currentTimeMillis();
		System.out.println("load index & retrieve running time: "+(endTime-startTime)/60000.0+" min");
	}

    // 从assig1的结果文件 每次获得一篇文档 生成posting <单词,<文档id, 出现频率>> 并把临时结果写入硬盘
	public void WriteIndex(String dataType) throws Exception {
		// Initiate pre-processed collection file reader 实例化来自其他文件的类
		PreProcessedCorpusReader corpus=new PreProcessedCorpusReader(dataType);
		
		// initiate the output object 实例化来自其他文件的类
		MyIndexWriter output=new MyIndexWriter(dataType);
		
		// initiate a doc object, which will hold document number and document content
        // 用于文档编号和文档内容
		Map<String, String> doc = null;

		int count=0;
		// build index of corpus document by document
		while ((doc = corpus.NextDocument()) != null) {
			// doc返回一个HashMap,里面包含2个k-v对，分别为<DOCNO,编号> <CONTENT,内容>
			String docno = doc.get("DOCNO"); 
			String content = doc.get("CONTENT");			
			
			// index this document
			output.IndexADocument(docno, content);

			// 每完成30000篇文章后输出一次
			count++;
			if(count%30000==0)
				System.out.println("finish "+count+" docs");
		}
		System.out.println("totaly document count:  "+count);
		// 调用 output实例的Close函数
		output.Close();
	}
	
	public void ReadIndex(String dataType, String token) throws Exception {
		// Initiate the index file reader
		MyIndexReader ixreader=new MyIndexReader(dataType);
		
		// 该token在几篇文档中出现过
		int df = ixreader.GetDocFreq(token);
		// 该token在数据集中出现的总次数
		long ctf = ixreader.GetCollectionFreq(token);
		System.out.println(" >> the token \""+token+"\" appeared in "+df+" documents and "+ctf+" times in total");
		// 输出token所在文档的详细情况
		if(df>0){
			int[][] posting = ixreader.GetPostingList(token);
			for(int ix=0;ix<posting.length;ix++){
				int docid = posting[ix][0];
				int freq = posting[ix][1];
				String docno = ixreader.GetDocno(docid);
				System.out.printf("    %20s    %6d    %6d\n", docno, docid, freq);
			}
		}
		ixreader.Close();
	}
}
