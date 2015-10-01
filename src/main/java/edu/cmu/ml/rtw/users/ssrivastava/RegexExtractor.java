package edu.cmu.ml.rtw.users.ssrivastava;

import java.util.ArrayList;
import java.util.List;

import edu.cmu.ml.rtw.generic.data.DataTools;
import edu.cmu.ml.rtw.generic.data.annotation.AnnotationType;
import edu.cmu.ml.rtw.generic.data.annotation.nlp.AnnotationTypeNLP;
import edu.cmu.ml.rtw.generic.data.annotation.nlp.AnnotationTypeNLP.Target;
import edu.cmu.ml.rtw.generic.data.annotation.nlp.DocumentNLP;
import edu.cmu.ml.rtw.generic.data.annotation.nlp.DocumentNLPInMemory;
import edu.cmu.ml.rtw.generic.data.annotation.nlp.Language;
import edu.cmu.ml.rtw.generic.data.annotation.nlp.PoSTag;
import edu.cmu.ml.rtw.generic.data.annotation.nlp.TokenSpan;
import edu.cmu.ml.rtw.generic.data.annotation.nlp.micro.Annotation;
import edu.cmu.ml.rtw.generic.model.annotator.nlp.AnnotatorTokenSpan;
import edu.cmu.ml.rtw.generic.model.annotator.nlp.PipelineNLP;
import edu.cmu.ml.rtw.generic.model.annotator.nlp.PipelineNLPExtendable;
import edu.cmu.ml.rtw.generic.model.annotator.nlp.PipelineNLPStanford;
import edu.cmu.ml.rtw.generic.util.OutputWriter;
import edu.cmu.ml.rtw.generic.util.Pair;
import edu.cmu.ml.rtw.generic.util.Triple;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.tokensregex.CoreMapExpressionExtractor;
import edu.stanford.nlp.ling.tokensregex.MatchedExpression;
import edu.stanford.nlp.ling.tokensregex.TokenSequencePattern;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.CharacterOffsetBeginAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.CharacterOffsetEndAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.ArrayCoreMap;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.Interval;


public class RegexExtractor implements AnnotatorTokenSpan<String> {

    /* bkdb quick hack because somehow it's not finding these when running from micro-hadoop
	private static String rulesFile= RegexExtractor.class.getResource("/rules2.txt").getFile();
	private static String organiznRulesFile= RegexExtractor.class.getResource("/organizationrules2.txt").getFile();
    */
    private static String rulesFile= "rules2.txt";
    private static String organiznRulesFile= "organizationrules2.txt";

	private static CoreMapExpressionExtractor<MatchedExpression> extractor; // = CoreMapExpressionExtractor.createExtractorFromFiles( TokenSequencePattern.getNewEnv(), rulesFile, organiznRulesFile);
	private static boolean verbose=false;

	static{
		try{
			extractor = CoreMapExpressionExtractor.createExtractorFromFiles( TokenSequencePattern.getNewEnv(), rulesFile, organiznRulesFile);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static final AnnotationTypeNLP<String> REGEX_EXTRACTION = new AnnotationTypeNLP<String>("nell-regex", String.class, Target.TOKEN_SPAN);

	private static final AnnotationType<?>[] REQUIRED_ANNOTATIONS = new AnnotationType<?>[] {
		AnnotationTypeNLP.TOKEN,
		AnnotationTypeNLP.SENTENCE,
		AnnotationTypeNLP.POS,
		AnnotationTypeNLP.NER
	};

	public RegexExtractor() {

		/*
		PipelineNLPStanford stanfordPipe = new PipelineNLPStanford();
		stanfordPipe = new PipelineNLPStanford(stanfordPipe);
		DocumentNLP document = new DocumentNLPInMemory(new DataTools(new OutputWriter()), 
				//"theDocument", "Mahatma Gandhi founded Cairns Corporation in NYC in August 2014.",
				//"0001", "Google DeepMind is a British artificial intelligence company. Founded in 2010 as DeepMind Technologies, it was acquired by Google in 2014. DeepMind was founded by Demis Hassabis, Shane Legg and Mustafa Suleyman. Hassabis and Legg first met at UCL's Gatsby Computational Neuroscience Unit. On 26 January 2014, Google announced that it had agreed to acquire DeepMind Technologies. The acquisition reportedly took place after Facebook ended negotiations with DeepMind Technologies in 2013. Following the acquisition, the company was renamed Google DeepMind . ",
				//"theDocument", "Muhammad Ali ( born Cassius Marcellus Clay Jr.) is an American former professional boxer, generally considered among the greatest heavyweights in the sport's history.",
				//"document_1", "Harvey Samuel Firestone (December 20, 1868 - February 7, 1938) was an American businessman, and the founder of the Firestone Tire and Rubber Company, one of the first global makers of automobile tires. Mr. Firestone was born on the Columbiana, Ohio farm built by his paternal grandfather. He was the second of Benjamin and Catherine (nee Flickinger) Firestone's three sons; Benjamin had a son and a daughter by his first wife. India is the capital of Asia . Mr. Firestone's paternal great-great-great-grandfather, Nicholas Hans Feuerstein, immigrated from Berg/Alsace/France,[2] in 1753, and settled in Pennsylvania.[3] Three of Nicholas's sons - including Harvey's great-great-grandfather, Johan Nicholas - changed their surname to Firestone, the English translation of the family's German name. Mr. Firestone's birthplace was moved years later to Greenfield Village, a 90-acre (360,000 m2) historical site founded by Henry Ford. On 20 November 1895, Mr. Firestone married Idabelle Smith,[5] and had seven children. Notable great-grandchildren include: Andrew Firestone, Nick Firestone, and William Clay Ford, Jr. (the son of Henry Ford's grandson and Harvey and Idabelle's granddaughter Martha). After graduating from Columbiana High School, Mr. Firestone worked for the Columbus Buggy Company in Columbus, Ohio before starting his own company in 1890, making rubber tires for carriages. In 1900 he soon saw the huge potential for marketing tires for automobiles and then founded the Firestone Tire and Rubber Company, a pioneer in the mass production of tires. In 1926 he published a book, Men and Rubber: The Story of Business, which was written in collaboration with Samuel Crowther.[6] In 1938, Mr. Firestone died peacefully at his vacation home in Miami Beach, Florida at the age of 69.[1]",
				"0002", "James \"Jimmy\" Howard is a retired American baseball player who played 22 seasons in Major League Baseball (MLB), from 1991 to 2012. He played for six different teams, most notably the Cleveland Indians during the 1990s and the Philadelphia Phillies in the early 2000s. A prolific power hitter, Thome grew up in Peoria, Illinois, as part of a large blue-collar family of athletes, who predominantly played baseball and basketball. After attending Illinois Central College, he was drafted by the Indians in 1989 and made his major league debut in 1999.",
				//"0003", "William H. Ford (December 10, 1826 – March 8, 1905) was an Irish-born American businessman and was the father of Ford Motor Company founder Henry Ford. William was born in Ballinascarthy near Clonakilty, County Cork, Ireland and was the son of Thomasine (née Smith), and Jonathan \"John\" Ford. Ford was an Anglican and attended a local Episcopal Church. On April 21, 1861, William married Mary Litogot in Wayne County, Michigan, daughter of Belgian immigrants. He and Mary had eight children, six of whom survived into adulthood. They moved to Detroit, Michigan. William retired in 1879. He died of natural causes on March 8, 1905 in his home and was buried in the Ford Cemetery in Detroit, Michigan, USA, Plot: St. Martha's Episcopal Church yard.",
				//"0004","Amanda Marie Knox ( born July 9, 1987) is an American woman who spent almost four years in an Italian prison accused of the 2007 murder of Meredith Kercher, one of the women who shared her apartment, before being definitively acquitted by the Supreme Court of Cassation. Knox had raised the alarm after returning from spending the night with her boyfriend, Raffaele Sollecito. Police initially thought a faked burglary at the apartment indicated a single male known to Kercher, but investigations began to focus on Knox, although she was officially a witness. Four days later, she and Sollecito were arrested, over the objections of the policeman heading the investigation. Knox and Sollecito were initially accused of acting with a bar owner she worked for, but he was released and substituted for Rudy Guede whose bloodstained fingerprints had been found on Kercher's possessions. Amanda Knox grew up in West Seattle with three younger sisters. Her mother, Edda Mellas, a mathematics teacher, and her father, Curt Knox, a vice president of finance at the local Macy's, divorced when Amanda was a few years old. Her stepfather, Chris Mellas, is an information-technology consultant. Amanda studied linguistics at the University of Washington; making the university's dean's list, and working at part-time jobs to fund an academic year in Italy. ",
				//"0005","Mohandas Karamchand Gandhi (2 October 1869 – 30 January 1948) was the preeminent leader of the Indian independence movement in British-ruled India. Employing nonviolent civil disobedience, Gandhi led India to independence and inspired movements for civil rights and freedom across the world. The honorific Mahatma (Sanskrit: \"high-souled\", \"venerable\")[3]—applied to him first in 1914 in South Africa,[4]—is now used worldwide. He is also called Bapu (Gujarati: endearment for \"father\") in India. Born and raised in a Hindu merchant caste family in coastal Gujarat, western India, and trained in law at the Inner Temple, London, Gandhi first employed nonviolent civil disobedience as an expatriate lawyer in South Africa, in the resident Indian community's struggle for civil rights. After his return to India in 1915, he set about organising peasants, farmers, and urban labourers to protest against excessive land-tax and discrimination. Assuming leadership of the Indian National Congress in 1921, Gandhi led nationwide campaigns for easing poverty, expanding women's rights, building religious and ethnic amity, ending untouchability, but above all for achieving Swaraj or self-rule.Nathuram Godse, a Hindu nationalist, assassinated Gandhi on 30 January 1948 by firing three bullets into his chest at point-blank range. Indians widely describe Gandhi as the father of the nation. His birthday, 2 October, is commemorated as Gandhi Jayanti, a national holiday, and world-wide as the International Day of Nonviolence. In May 1883, the 13-year-old Mohandas was married to 14-year-old Kasturbai Makhanji Kapadia (her first name was usually shortened to \"Kasturba\", and affectionately to \"Ba\") in an arranged child marriage, according to the custom of the region. At the request of Gokhale, conveyed to him by C.F. Andrews, Gandhi returned to India in 1915. He brought an international reputation as a leading Indian nationalist, theorist and organiser. He joined the Indian National Congress and was introduced to Indian issues, politics and the Indian people primarily by Gopal Krishna Gokhale. Gokhale was a key leader of the Congress Party best known for his restraint and moderation, and his insistence on working inside the system. Gandhi took Gokhale's liberal approach based on British Whiggish traditions and transformed it to make it look wholly Indian.",
				//"0006","Microsoft Corporation (commonly referred to as Microsoft) is an American multinational technology company headquartered in Redmond, Washington, that develops, manufactures, licenses, supports and sells computer software, consumer electronics and personal computers and services. Microsoft was founded by Paul Allen and Bill Gates on April 4, 1975, to develop and sell BASIC interpreters for Altair 8800. It rose to dominate the personal computer operating system market with MS-DOS in the mid-1980s, followed by Microsoft Windows. The company's 1986 initial public offering, and subsequent rise in its share price, created three billionaires and an estimated 12,000 millionaires from Microsoft employees. Since the 1990s, it has increasingly diversified from the operating system market and has made a number of corporate acquisitions. In May 2011, Microsoft acquired Skype Technologies for $8.5 billion in its largest acquisition to date. With the acquisition of Nokia's devices and services division to form Microsoft Mobile Oy, the company re-entered the smartphone hardware market, after its previous attempt, Microsoft Kin, which resulted from their acquisition of Danger Inc . On September 3, 2013, Microsoft agreed to buy Nokia's mobile unit for $7 billion.[73] Also in 2013, Amy Hood became the CFO of Microsoft. On February 4, 2014, Steve Ballmer stepped down as CEO of Microsoft and was succeeded by Satya Nadella, who previously led Microsoft's Cloud and Enterprise division.[77] On the same day, John W. Thompson took on the role of chairman, with Bill Gates stepping down from the position to become more active within the company as Technology Advisor. The company is run by a board of directors made up of mostly company outsiders, as is customary for publicly traded companies. Members of the board of directors as of September 2014 are: John W. Thompson, Dina Dublon, Bill Gates, Maria Klawe, David Marquardt, Mason Morfit,[82] Satya Nadella, Charles Noski, Helmut Panke and John W. Stanton. In July 2014, Microsoft announced plans to lay off 18,000 employees. Microsoft employed 127,104 people as of June 5, 2014, making this about a 14 percent reduction of its workforce as the biggest Microsoft lay off ever. William Henry \"Bill\" Gates III (born October 28, 1955) is an American business magnate, philanthropist, investor, computer programmer, and inventor.[3][4][5] In 1975, Gates co-founded Microsoft, the world’s largest PC software company, with Paul Allen. During his career at Microsoft, Gates held the positions of chairman, CEO and chief software architect, and was the largest individual shareholder until May 2014.[6][a] Gates has authored and co-authored several books.",
				//"Test document", "Harvey Samuel Firestone (December 20, 1868 – February 7, 1938) was an American businessman, and the founder of the Firestone Tire and Rubber Company, one of the first global makers of automobile tires. Firestone was born on the Columbiana, Ohio farm built by his paternal grandfather. He was the second of Benjamin and Catherine (nee Flickinger) Firestone's three sons; Benjamin had a son and a daughter by his first wife. India is the capital of Asia .",
				Language.English, stanfordPipe);	

		annotate(document);
		 */
	}

	@Override
	public String getName() {
		return "cmunell_regexExtractor-0.0.1";
	}

	@Override
	public boolean measuresConfidence() {
		return true;
	}

	@Override
	public AnnotationType<String> produces() {
		return REGEX_EXTRACTION;
	}

	@Override
	public AnnotationType<?>[] requires() {
		return REQUIRED_ANNOTATIONS;
	}

	@Override
	public List<Triple<TokenSpan, String, Double>> annotate(DocumentNLP document) {

		List<Triple<TokenSpan, String, Double>> annotationList = new ArrayList<Triple<TokenSpan, String, Double>>();
		for (int sentIdx = 0; sentIdx < document.getSentenceCount(); sentIdx++) {

			CoreMap sentence = getStanfordSentence(document, sentIdx);
			if(verbose){
				printSentence(sentence);
			}

			if( !isGoodToProcess(sentence) ){
				continue;
			}

			//System.out.println("Extracting patterns from sentence with index "+sentIdx);
			List<MatchedExpression> matchedExpressions = extractor.extractExpressions(sentence);
			if(matchedExpressions.size()>0){
				if(verbose){
					System.out.println(matchedExpressions.size());
				}
				for(MatchedExpression expr:matchedExpressions){
					//Interval<Integer> interv = expr.getCharOffsets();
					Interval<Integer> interv = expr.getTokenOffsets();
					TokenSpan ts = new TokenSpan(document, sentIdx, interv.first(), interv.second()-1);
					String result = expr.getValue().toString().substring(7, expr.getValue().toString().length()-1);
					if(verbose){
						System.out.println("[ docId:"+ts.getDocument().getName()+" first:"+interv.first()+" end:"+interv.second()+" result:"+result+" ]");
					}
					annotationList.add(new Triple<TokenSpan, String, Double>(ts, result, 0.8));
				}
			}
		}

		return annotationList;
	}

	public static void main(String[] args){
		RegexExtractor a = new RegexExtractor();
	}

	public static CoreMap getStanfordSentence(DocumentNLP document, int sentIdx){
		List<String> words = document.getSentenceTokenStrs(sentIdx);
		List<PoSTag> posTags = document.getSentencePoSTags(sentIdx);

		List<CoreLabel> tokenList = new ArrayList<CoreLabel>();
		for(int i=0;i<words.size();i++){
			/*Re-create Stanford tokens*/
			CoreLabel token = new CoreLabel();
			token.setWord(words.get(i));
			token.setTag(posTags.get(i).toString());	
			token.setNER("O");
			token.setDocID(document.getName());
			token.setSentIndex(sentIdx);
			token.setBeginPosition(document.getToken(sentIdx, i).getCharSpanStart());
			token.setEndPosition(document.getToken(sentIdx, i).getCharSpanEnd());

			//System.out.println(token.word()+" "+token.beginPosition()+" "+token.endPosition());
			tokenList.add(token);
		}

		//Add NER labels for sentence
		List<Pair<TokenSpan, String>> ners = document.getNer(sentIdx);
		for(Pair<TokenSpan, String> p:ners){
			for(int k = p.getFirst().getStartTokenIndex();k<p.getFirst().getEndTokenIndex();k++){
				tokenList.get(k).setNER(p.getSecond());
			}
		}

		//Convert to Stanford Sentence
		CoreMap sentence = new ArrayCoreMap();
		sentence.set(TokensAnnotation.class, tokenList );
		sentence.set(CharacterOffsetBeginAnnotation.class, tokenList.get(0).beginPosition());
		sentence.set(CharacterOffsetEndAnnotation.class, tokenList.get(words.size()-1).endPosition());
		return sentence;
	}

	public static void printSentence(CoreMap sentence){
		for (CoreLabel token : (List<CoreLabel>) sentence.get(CoreAnnotations.TokensAnnotation.class)) {
			String word = token.get(CoreAnnotations.TextAnnotation.class);
			String tag = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
			String ner = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
			System.out.print(word + "|" + tag + "|" +ner+"\t");
		}
		System.out.println();
	}

	public static boolean isGoodToProcess(CoreMap sentence){

		List<CoreLabel> tokens = (List<CoreLabel>) sentence.get(CoreAnnotations.TokensAnnotation.class);
		//Check if sentence is not too long
		if(tokens.size() > 60){
			return false;
		}

		//Check if sentence is ill-formed: all NNPs
		int nnp=0, len= tokens.size(), nnseq=0;
		for(CoreLabel token:tokens){
			if(token.get(CoreAnnotations.PartOfSpeechAnnotation.class).startsWith("NN") || token.get(CoreAnnotations.PartOfSpeechAnnotation.class).equals("FW")){
				nnp++;
				nnseq++;
				if(nnseq >= 8){
					if(verbose){
						System.err.println("BAD SENTENCE");
						printSentence(sentence);
					}
					return false;
				}
			}else{
				nnseq = 0;
			}
		}
		if(len >=10 && nnp> 0.7*len ){
			if(verbose){
				System.err.println("BAD SENTENCE");
				printSentence(sentence);
			}
			return false;
		}
		return true;
	}
}
