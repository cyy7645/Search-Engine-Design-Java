
It’s a search engine for e-books in the library in School of Information Science at University of Pittsburgh. It’s going for finding the similar documents based on users’ input, the input could be keywords or the whole document. So I started with 6 gigabyte HTML documents. 

The whole project can be divided into four parts:
part 1: pre-processing, split the word, remove stop-words, word stemming...
Firstly, I extracted the document number and content from original HTML, save it into a map. And for every document, I removed stop-words. For every word, I applied word stemming. At this point, I solved the problem of memory cannot support the whole corpus. 

part 2: build inverted index (posting index)
After that, I created inverted index (posting index) of the corpus for fast access, (two dimensions array, the first dimension records the id of token, the second dimension records the frequency of token appearing in the document.)

part 3: design languagge model and explore smoothing method.
The point is that I implemented the statistical language model with smoothing methods, I spent several weeks working with my professor and teach assistant to explore how smoothing methods affect language model and how to choose the parameters according the type of queries. 

The basic idea of the approach is to estimate a language model for each document, and then rank documents by the likelihood of the query according to the model. But due to the problem of data sparseness, that is, when our training data is limited, they are always limited. Some data doesn’t appear in the training data, but it doesn’t mean this kind of data will not be in test data. So I have to take this sparse data into consideration. Otherwise, my model will be too simple to handle rare words. That’s where smoothing comes. it’s not only a project so far, it’s kind of like a research. Because I dive in to explore two questions. How sensitive is retrieval performance to the smoothing of a document language model? And how should a smoothing method be selected, and how should its parameters be chosen? You know, I did find unexcepted conclusions. 
I compared several popular smoothing methods on the five collections from TREC(Text Retrieval Conference). 
The smoothing methods require efficient computations.
The first smoothing method is Jelinek-Mercer smoothing, This method involves the frequency of words appear both in the document and corpus. Using a parameter lambda to control the coefficient of each model. 
The second one is Bayesian smoothing using Dirichlet priors.
The third one is absolute discounting. 
I examined the sensitivity of precision and recall with two versions of queries, which are title only and the whole content. 
For the Jelinek-Mercer smoothing method, the result is that precision and recall are more sensitive to parameter lambda for long queries than short title. Lambda should be smaller for short query. 
For Dirichlet Priors method, alpha should be smaller for long query. For parameter niu, 2000 is the best.
For absolute discounting, parameter is not important. )
So finally I use Dirichlet Priors method as my model because it brings a little bit more promising results, and I set niu to be 2000 for my model.

part 4: 
The final step is that I created a pseudo relevance feedback system. I obtained top K documents in the last step with language model. I treated them as relevant documents. For each query term qi, I calculated the probability of feedback documents generating this term. I also calculated the probability of one document generating query term. And I combined these two parts together with parameter lambda as final model. Get top N documents as results.


Chinese version:
该项目为一个搜索引擎的后端，可以根据query返回20篇文档。

分成4部分
1. 主要是对原数据的预处理，包括分词，大小写转换，去除停用词，stem保留词干等。
2. 为整个语料库建立倒排索引，方便快速查询和后期建模。
3. 建立概率语言模型，根据query返回top N篇文章。
4. 建立反馈系统，进一步提升模型性能。

ps:
原始语料库过大，只保留了其中一小部分。
