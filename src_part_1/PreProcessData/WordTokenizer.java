package PreProcessData;

/**
 * This is for INFSCI 2140 in 2018
 * 
 * 把内容分成一个个token,提供方法nextWord获得下一个单词
 */
public class WordTokenizer {
	// Essential private methods or variables can be added.
    // for every word
	private char[] texts;
	// for position of words
    private int position = 0;
	// 构造函数
	public WordTokenizer( char[] texts ) {
		// Tokenize the input texts.
        this.texts=new char[texts.length];
        for(int i = 0; i < this.texts.length; i ++){
            this.texts[i] = texts[i];
        }
	}
	
	// YOU MUST IMPLEMENT THIS METHOD.
    public char[] nextWord() {
        // read and return the next word of the document
        // or return null if it is the end of the document
        int ptr=position;
        char[] word;

        //delete (space) and (") until it reaches a letter or digit
        for(;ptr<texts.length-1;ptr++){
            if(!Character.isLetterOrDigit(texts[ptr])) {
                position++;
                continue;
            }
            break;
        }

        // get a word
        for(position=ptr;ptr<texts.length-1;ptr++) {
            // reach space or reach punctuation or reach end of document
            if(texts[ptr]==' ' || (!Character.isLetterOrDigit(texts[ptr])&&texts[ptr+1]==' ') ||
              (ptr==texts.length-1&&ptr!=position)){
                word = new char[ptr - position];
                for(int i=0;i<ptr-position;i++)
                    word[i]=texts[position+i];
                position=ptr;
                return word;
            }
        }

        return null;
    }
	
}
