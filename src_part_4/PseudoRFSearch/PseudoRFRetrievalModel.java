package PseudoRFSearch;

import java.io.IOException;
import java.util.*;

import Classes.Document;
import Classes.Query;
import IndexingLucene.MyIndexReader;
import SearchLucene.*;
//import Search.*;

public class PseudoRFRetrievalModel {

	MyIndexReader ixreader;
	int totalLength = 0;

	public PseudoRFRetrievalModel(MyIndexReader ixreader) throws IOException
	{
	    // get the number of tokens in the collection
		for ( int i = 0; i < ixreader.getTotal(); i++ )// indexReader.getDocNum()= 503473
			totalLength += ixreader.docLength(i);
		this.ixreader=ixreader;
	}

	/**
	 * Search for the topic with pseudo relevance feedback in 2017 spring assignment 4.
	 * The returned results (retrieved documents) should be ranked by the score (from the most relevant to the least).
	 *
	 * @param aQuery The query to be searched for.
	 * @param TopN The maximum number of returned document
	 * @param TopK The count of feedback documents
	 * @param alpha parameter of relevance feedback model
	 * @return TopN most relevant document, in List structure
	 */
	public List<Document> RetrieveQuery( Query aQuery, int TopN, int TopK, double alpha) throws Exception {
		// this method will return the retrieval result of the given Query, and this result is enhanced with pseudo relevance feedback
		// (1) you should first use the original retrieval model to get TopK documents, which will be regarded as feedback documents
		// (2) implement GetTokenRFScore to get each query token's P(token|feedback model) in feedback documents
		// (3) implement the relevance feedback model for each token: combine the each query token's original retrieval score P(token|document) with its score in feedback documents P(token|feedback model)
		// (4) for each document, use the query likelihood language model to get the whole query's new score, P(Q|document)=P(token_1|document')*P(token_2|document')*...*P(token_n|document')
		// 1. 把原始模型返回的结果作为feedback document  2. 计算query中每个单词在feedback中的频率
		// 3. 使用反馈模型计算每个单词的score

		//get P(token|feedback documents)
		HashMap<String,Double> TokenRFScore=GetTokenRFScore(aQuery,TopK);

		// get query via api
		String query = aQuery.GetQueryContent();
		// spilt query into tokens
		String[] tokens = query.split(" ");
		// store the frequency of every token
		double[] colFreqs = new double[tokens.length];
		int[][] postingList;
		// Map<DocID, HashMap<token, frequency>>
		Map<Integer, HashMap<String, Integer>> docx = new HashMap<>();
        // sort all retrieved documents from most relevant to least, and return TopN
        List<Document> results = new ArrayList<>();

        // fill content into docx
		for (int i=0;i<tokens.length;i++ ) {
		    // get the frequency of every token
			colFreqs[i] = (double)  ixreader.CollectionFreq(tokens[i]);
			// get the posting of every token
			postingList = ixreader.getPostingList(tokens[i]);
			if(postingList != null){
				for(int j = 0; j < postingList.length; j++){
				    // get document id in posting list
					int docId = postingList[j][0];
					if(docx.containsKey(docId)){
						docx.get(docId).put(tokens[i],postingList[j][1]);
					}else{
						HashMap<String, Integer> termFreqs = new HashMap<>();
						termFreqs.put(tokens[i],postingList[j][1]);
						docx.put(docId, termFreqs);
					}
				}
			}
		}

		long m=2000;
		// store document id and its score for every document
		List<Document> documents = new ArrayList<>();
		for(int i=0; i < ixreader.getTotal(); i++ ) {
			int docLength = ixreader.docLength(i);
			double score = 1;
			// calculate score for every document
			for(int j=0;j<tokens.length;j++) {
				int docFreq = 0;
				if (colFreqs[j] != 0) {
					if (docx.containsKey(i) && docx.get(i).containsKey(tokens[j])) {
						docFreq = docx.get(i).get(tokens[j]);
					}
					score = score * ((alpha * ((docFreq + m * (colFreqs[j] / totalLength)) / (docLength + m))) + ((1 - alpha) * TokenRFScore.get(tokens[j])));
				}
			}
			Document aDocument = new Document(Integer.toString(i), ixreader.getDocno(i), score);
			documents.add(aDocument);
		}
		// override the comparator and sort
		Collections.sort(documents, Collections.reverseOrder(new Comparator<Document>(){
			@Override
			public int compare(Document d1, Document d2) {
				return (new Double(d1.score())).compareTo(new Double(d2.score()));
			}
		}));
		//get top N documents
		for (int t=0;t<TopN;t++){
			results.add(documents.get(t));
		}
		return results;
	}

	public HashMap<String,Double> GetTokenRFScore(Query aQuery,  int TopK) throws Exception {
		// for each token in the query, you should calculate token's score in feedback documents: P(token|feedback documents)
		// use Dirichlet smoothing
		// save <token, score> in HashMap TokenRFScore, and return it
		HashMap<String, Double> TokenRFScore = new HashMap<String, Double>();
		String query = aQuery.GetQueryContent();
		String[] tokens = query.split(" ");
		double[] colFreqs = new double[tokens.length];
		int[][] postingList;
		Map<Integer, HashMap<String, Integer>> docx = new HashMap<>();

		for (int i = 0; i < tokens.length; i++) {
			colFreqs[i] = (double) ixreader.CollectionFreq(tokens[i]);
			postingList = ixreader.getPostingList(tokens[i]);
			if (postingList != null) {
				for (int j = 0; j < postingList.length; j++) {
					int docId = postingList[j][0];
					if (docx.containsKey(docId)) {
						docx.get(docId).put(tokens[i], postingList[j][1]);
					}
					else {
						HashMap<String, Integer> termFreqs = new HashMap<>();
						termFreqs.put(tokens[i], postingList[j][1]);
						docx.put(docId, termFreqs);
					}
				}
			}
		}

		long m = 2000;
		List<Document> documents = new ArrayList<>();
		for (int i = 0; i < ixreader.getTotal(); i++) {
			int docLength = ixreader.docLength(i);
			double score = 1;
			for (int j = 0; j < tokens.length; j++) {
				int docFreq = 0;
				if (colFreqs[j] != 0) {
					if (docx.containsKey(i) && docx.get(i).containsKey(tokens[j])) {
						docFreq = docx.get(i).get(tokens[j]);
					}
					score = score * ((docFreq + m * (colFreqs[j] / totalLength)) / (docLength + m));
				}
			}
			Document aDocument = new Document(Integer.toString(i), ixreader.getDocno(i), score);
			documents.add(aDocument);
		}

		Collections.sort(documents, Collections.reverseOrder(new Comparator<Document>() {
			@Override
			public int compare(Document d1, Document d2) {
				return new Double(d1.score()).compareTo(new Double(d2.score()));
			}
		}));
		List<Document> results = new ArrayList<>();
		// get top k documents
		for (int i = 0; i < TopK; i++) {
			results.add(documents.get(i));
		}


        // store document id
		HashSet<String> RF = new HashSet<>();
		for (int i = 0; i < TopK; i++) {
			results.add(documents.get(i));
			String temp = documents.get(i).docid();
			RF.add(temp);
		}
		Double colLengthTopN = 0.0;

		// get the number of tokens in top n documents
		for (String s : RF) {
			int docId = Integer.parseInt(s);
			int dLength = ixreader.docLength(docId);
			colLengthTopN = colLengthTopN + dLength;
		}

		for (int i = 0; i < tokens.length; i++) {
			postingList = ixreader.getPostingList(tokens[i]);
			Double tokenFreq = 0.0;
			Double tokenTotalFreq = 0.0;
			if (postingList != null) {
				for (int j = 0; j < postingList.length; j++) {
					int docid = postingList[j][0];
					if (RF.contains(String.valueOf(docid))) {
						tokenFreq = tokenFreq + postingList[j][1];
					}
					tokenTotalFreq = tokenTotalFreq + postingList[j][1];
				}
			}
			// calculate score for every token and save it into TokenRFScore
			Double score = (tokenFreq + (m * (tokenTotalFreq / totalLength))) / (colLengthTopN + m);
			TokenRFScore.put(tokens[i], score);
		}
		return TokenRFScore;
	}
}