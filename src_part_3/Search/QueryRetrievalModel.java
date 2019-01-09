package Search;//package Search;

import java.io.IOException;
import java.util.*;

import Classes.Query;
import Classes.Document;
import IndexingLucene.MyIndexReader;

public class QueryRetrievalModel {

	protected MyIndexReader indexReader;
    public int colLen = 0;

	public QueryRetrievalModel(MyIndexReader ixreader) throws IOException {

        indexReader = ixreader;
        this.colLen = 0;
        // get collection length (how many tokens in the collection) 获取数据集总长度
        for(int i = 0; i < indexReader.getTotal(); i++){	// for each document in the collection, calculate the collection length
            try {
                colLen += indexReader.docLength(i);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
	}

	/**
	 * Search for the topic information.
	 * The returned results (retrieved documents) should be ranked by the score (from the most relevant to the least).
	 * TopN specifies the maximum number of results to be returned.
	 *
	 * @param aQuery The query to be searched for.
	 * @param TopN The maximum number of returned document
	 * @return
	 */

	public List<Document> retrieveQuery( Query aQuery, int TopN ) throws IOException {
		// NT: you will find our IndexingLucene.Myindexreader provides method: docLength()
		// implement your retrieval model here, and for each input query, return the topN retrieved documents
		// sort the documents based on their relevance score, from high to low

        // get query via api
        String query = aQuery.GetQueryContent();
        // split query into tokens
		String[] tokens = query.split(" ");
		// 储存每个文档中每个单词的频率
		Map<Integer, HashMap<String, Integer>> docx = new HashMap<>();
        // 储存每个单词在整个数据集中的频率
		HashMap<String, Long> tokenFrequency = new HashMap<>();

        // traverse every token in query
        for(String token : tokens){
            tokenFrequency.put(token, indexReader.CollectionFreq(token));
            // 第一列为文档id， 第二列为该token在该文档出现的频率
            int [][]postingList = indexReader.getPostingList(token);
            if(postingList == null) continue;
            // 遍历所有posting，生成<文档id,<token,频率>>
            for (int[] aPostingList : postingList) {
                int docid = aPostingList[0];
                int freq = aPostingList[1];
                if (docx.containsKey(docid)) {
                    docx.get(docid).put(token, freq);
                } else {
                    HashMap<String, Integer> tokenFrequencyMap = new HashMap<>();
                    tokenFrequencyMap.put(token, freq);
                    docx.put(docid, tokenFrequencyMap);
                }
            }
        }
		// it is a parameter can be adjusted
        double U = 2000.0;
        List<Document> documents = new ArrayList<>();
        // calculate a score for every document in collection
        for(Map.Entry<Integer, HashMap<String,Integer>> entry : docx.entrySet()){
            double score = 1.0;
            int docid = entry.getKey();
            int docLen = indexReader.docLength(docid);	// get document length
            HashMap<String,Integer> tokenFrequencyInDocx = docx.get(docid);
            // calculate a score for every token and merge them
            for(String token : tokens){
                // get frequency of word from tokenFrequency
                long colFreq = tokenFrequency.getOrDefault(token,0l);
                if(colFreq != 0){
                    // default frequency is 0
                    long docFreq = 0;
                    if(tokenFrequencyInDocx.containsKey(token)){
                        docFreq = tokenFrequencyInDocx.get(token);
                    }
                    score = score * (docFreq + U * ((double)colFreq / colLen)) / (docLen + U);	//equation
                }
            }
            Document document = new Document(docid+"", indexReader.getDocno(docid),score);
            documents.add(document);
        }
        // override the comparator in order to get document with higher score
        documents.sort(Collections.reverseOrder(new Comparator<Document>() {
            @Override
            public int compare(Document d1, Document d2) {
                return Double.compare(d1.score(), d2.score());
            }
        }));

        // store final results by rank
        List<Document> doc = new ArrayList<>();
        for (int t = 0; t < TopN; t++){
            doc.add(documents.get(t));
        }

		return doc;
	}

}