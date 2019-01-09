import java.util.Map;

import IndexingLucene.*;

/**
 * !!! YOU CANNOT CHANGE ANYTHING IN THIS CLASS !!!
 * 
 * Main class for running your HW2.
 * 
 */
public class HW2MainLucene {

	public static void main(String[] args) throws Exception {
		// main entrance
		HW2MainLucene hm2 = new HW2MainLucene();

		
		long startTime=System.currentTimeMillis();
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
		hm2.ReadIndex("trectext", "chancellor");
		endTime=System.currentTimeMillis();
		System.out.println("load index & retrieve running time: "+(endTime-startTime)/60000.0+" min");
		
		
		
	}

	public void WriteIndex(String dataType) throws Exception {
		// Initiate pre-processed collection file reader
		PreProcessedCorpusReader corpus=new PreProcessedCorpusReader(dataType);
		
		// initiate the output object
		MyIndexWriter output=new MyIndexWriter(dataType);
		
		// initiate a doc object, which can hold document number and document content of a document
		Map<String, String> doc = null;

		int count=0;
		// build index of corpus document by document
		while ((doc = corpus.nextDocument()) != null) {
			// load document number and content of the document
			String docno = doc.keySet().iterator().next();
			String content = doc.get(docno);
			// index this document
			output.index(docno, content); 
			
			count++;
			if(count%10000==0)
				System.out.println("finish "+count+" docs");
		}
		System.out.println("totaly document count:  "+count);
		output.close();
	}
	
	public void ReadIndex(String dataType, String token) throws Exception {
		// Initiate the index file reader
		MyIndexReader ixreader=new MyIndexReader(dataType);
		
		// do retrieval
		int df = ixreader.DocFreq(token);
		long ctf = ixreader.CollectionFreq(token);
		System.out.println(" >> the token \""+token+"\" appeared in "+df+" documents and "+ctf+" times in total");
		if(df>0){
			int[][] posting = ixreader.getPostingList(token);
			for(int ix=0;ix<posting.length;ix++){
				int docid = posting[ix][0];
				int freq = posting[ix][1];
				String docno = ixreader.getDocno(docid);
				System.out.printf("    %20s    %6d    %6d\n", docno, docid, freq);
			}
		}
		ixreader.close();
	}
}
