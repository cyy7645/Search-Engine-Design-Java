import java.util.List;

import Classes.Document;
import Classes.Query;
import IndexingLucene.MyIndexReader;
import PseudoRFSearch.*;
import SearchLucene.*;
//import Search.*;

public class HW4Main {

	public static void main(String[] args) throws Exception {
		
		// Open index, initialize the pseudo relevance feedback retrieval model,and extract queries
		MyIndexReader ixreader = new MyIndexReader("trectext");
		PseudoRFRetrievalModel PRFSearchModel = new PseudoRFRetrievalModel(ixreader);
		ExtractQuery queries = new ExtractQuery();
		
		// begin search
		long startTime = System.currentTimeMillis();
		while (queries.hasNext()) {
			Query aQuery = queries.next();
			List<Document> results = PRFSearchModel.RetrieveQuery(aQuery, 20, 100, 0.4);
			if (results != null) {
				int rank = 1;
				for (Document result : results) {
					System.out.println(aQuery.GetTopicId() + " Q0 " + result.docno() + " " + rank + " "
							+ result.score() + " MYRUN");
					rank++;
				}
			}
		}
		long endTime = System.currentTimeMillis(); 
		
		// output running time
		System.out.println("\n\n4 queries search time: " + (endTime - startTime) / 60000.0 + " min");
		ixreader.close();
	}

}
