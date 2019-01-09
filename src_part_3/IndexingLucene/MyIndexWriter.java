package IndexingLucene;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;


public class MyIndexWriter {
	
	protected File dir;
	private Directory directory;
	private IndexWriter ixwriter;
	private FieldType type;
	
	public MyIndexWriter( String dataType ) throws IOException {
		if (dataType.equals("trectext")) {
			directory = FSDirectory.open(Paths.get(Classes.Path.IndexTextDir));  
		} else {
			directory = FSDirectory.open(Paths.get(Classes.Path.IndexWebDir)); 
		}
		IndexWriterConfig indexConfig=new IndexWriterConfig(new WhitespaceAnalyzer());
		indexConfig.setMaxBufferedDocs(10000);
		ixwriter = new IndexWriter( directory, indexConfig);
		type = new FieldType();
		type.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
		type.setStored(false);
		type.setStoreTermVectors(true);
	}
	
	/**
	 * This method build index for each document.
	 * NOTE THAT: in your implementation of the index, you should transform your string docnos into non-negative integer docids !!!
	 * In MyIndexReader, you should be able to request the integer docid for docnos.
	 * 
	 * @param docno 
	 * @param content
	 * @throws IOException
	 */
	public void index( String docno, String content) throws IOException {
		// you should implement this method to build index for each document
		Document doc = new Document();
		doc.add(new StoredField("DOCNO", docno));		
		doc.add(new Field("CONTENT", content, type));
		ixwriter.addDocument(doc);
	}
	
	/**
	 * Close the index writer, and you should output all the buffered content (if any).
	 * @throws IOException
	 */
	public void close() throws IOException {
		// you should implement this method if necessary
		ixwriter.close();
		directory.close();
	}
	
}
