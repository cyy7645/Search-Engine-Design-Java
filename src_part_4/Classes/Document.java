package Classes;

public class Document {
	
	protected String docid;
	protected String docno;
	protected double score;
	
	public Document( String docid, String docno, double score ) {
		this.docid = docid;
		this.docno = docno;
		this.score = score;
	}
	
	public String docid() {
		return docid;
	}
	
	public String docno() {
		return docno;
	}
	
	public double score() {
		return score;
	}
	
	public void setDocid( String docid ) {
		this.docid = docid;
	}
	
	public void setDocno( String docno ) {
		this.docno = docno;
	}
	
	public void setScore( double score ) {
		this.score = score;
	}
	
}
