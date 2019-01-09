package Search;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import Classes.Path;
import Classes.Query;
import Classes.Stemmer;

public class ExtractQuery {
	// stores query id
	private ArrayList<String> topics = new ArrayList<>();
	private ArrayList<String> titles = new ArrayList<>();

	private int Index = 0;

	// 通过本地路径获取queries
	public ExtractQuery() throws IOException {
		//you should extract the 4 queries from the Path.TopicDir
		//NT: the query content of each topic should be 1) tokenized, 2) to lowercase, 3) remove stop words, 4) stemming
		//NT: you can simply pick up title only for query, or you can also use title + description + narrative for the query content.

		// 停用词表路径
		FileInputStream file = new FileInputStream(Path.StopwordDir);
		//
		HashSet<String> stopword = new HashSet<>();
		// for reading stopwords
		BufferedReader bufferstop = new BufferedReader(new InputStreamReader(file));
		// read a line in stopwords first
		String stopLine = bufferstop.readLine();
		// add every word in stopwords into stopword set
		while (stopLine != null) {
			stopword.add(stopLine);
			stopLine = bufferstop.readLine();
		}
		bufferstop.close();
        // 读取Topic
		BufferedReader bufferTopic = new BufferedReader(new FileReader(Path.TopicDir));
		// read a line in rd first
		String Topicline = bufferTopic.readLine();

		while (Topicline != null) {
			// if it starts with <num>, it is query id
			if (Topicline.contains("<num>")) {
				// get the substring after index 14
				String queryid = Topicline.substring(14);
				topics.add(queryid);
			}
			// if it starts with <title>, it is title
			if (Topicline.contains("<title>")) {
				// // get the substring after index 14
				String content = Topicline.substring(7);
				String[] token = content.replaceAll("[\\pP‘’“”]", "").split(" ");
				StringBuilder res = new StringBuilder();
				// remove stop words, apply stemming, save cleaned title in title array
				for (String word : token) {
					word = word.toLowerCase();
					if (!stopword.contains(word)) {
						String str = "";
						char[] c = word.toCharArray();
						Stemmer stemming = new Stemmer();
						stemming.add(c, c.length);
						stemming.stem();
						str = stemming.toString();
						res.append(" ").append(str);
					}
				}
				titles.add(res.toString());
			}
			// read next line
			Topicline = bufferTopic.readLine();
		}
		bufferTopic.close();
	}
	// decide whether there is a next query in topics
	public boolean hasNext()
	{
		if(Index<topics.size()){
			Index++;
			return true;
		}
		else
			return false;
	}

	// return next query
	public Query next()
	{
		Query query = new Query();

		query.SetQueryContent(titles.get(Index-1));
		query.SetTopicId(topics.get(Index-1));
		return query;
	}
}
