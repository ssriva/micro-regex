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

	private static String rulesFile="src/main/resources/rules2.txt";
	private static String organiznRulesFile="src/main/resources/organizationrules2.txt";
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
				//"theDocument", "Harvey Samuel Firestone (December 20, 1868 – February 7, 1938) was an American businessman, and the founder of the Firestone Tire and Rubber Company, one of the first global makers of automobile tires. Firestone was born on the Columbiana, Ohio farm built by his paternal grandfather. He was the second of Benjamin and Catherine (nee Flickinger) Firestone's three sons; Benjamin had a son and a daughter by his first wife. India is the capital of Asia .",
				"document_1", "Harvey Samuel Firestone (December 20, 1868 - February 7, 1938) was an American businessman, and the founder of the Firestone Tire and Rubber Company, one of the first global makers of automobile tires. Firestone was born on the Columbiana, Ohio farm built by his paternal grandfather. He was the second of Benjamin and Catherine (nee Flickinger) Firestone's three sons; Benjamin had a son and a daughter by his first wife. India is the capital of Asia . Firestone's paternal great-great-great-grandfather, Nicholas Hans Feuerstein, immigrated from Berg/Alsace/France,[2] in 1753, and settled in Pennsylvania.[3] Three of Nicholas's sons - including Harvey's great-great-grandfather, Johan Nicholas - changed their surname to Firestone, the English translation of the family's German name. Firestone's birthplace was moved years later to Greenfield Village, a 90-acre (360,000 m2) historical site founded by Henry Ford. On 20 November 1895, Firestone married Idabelle Smith,[5] and had seven children. Notable great-grandchildren include: Andrew Firestone, Nick Firestone, and William Clay Ford, Jr. (the son of Henry Ford's grandson and Harvey and Idabelle's granddaughter Martha). After graduating from Columbiana High School, Firestone worked for the Columbus Buggy Company in Columbus, Ohio before starting his own company in 1890, making rubber tires for carriages. In 1900 he soon saw the huge potential for marketing tires for automobiles and then founded the Firestone Tire and Rubber Company, a pioneer in the mass production of tires. In 1926 he published a book, Men and Rubber: The Story of Business, which was written in collaboration with Samuel Crowther.[6] In 1938, Firestone died peacefully at his vacation home in Miami Beach, Florida at the age of 69.[1]",
				Language.English, stanfordPipe);	

		annotate(document);
		*/
	}

	public static void testRegex() {
		PipelineNLPStanford pipelineStanford = new PipelineNLPStanford();
		PipelineNLPExtendable pipelineExtendable = new PipelineNLPExtendable();

		pipelineExtendable.extend(new RegexExtractor());
		PipelineNLP pipeline = pipelineStanford.weld(pipelineExtendable);
		DataTools dataTools = new DataTools();
		dataTools.addAnnotationTypeNLP(REGEX_EXTRACTION);
		DocumentNLP document = new DocumentNLPInMemory(dataTools, 
				"Test document", "Harvey Samuel Firestone (December 20, 1868 – February 7, 1938) was an American businessman, and the founder of the Firestone Tire and Rubber Company, one of the first global makers of automobile tires. Firestone was born on the Columbiana, Ohio farm built by his paternal grandfather. He was the second of Benjamin and Catherine (nee Flickinger) Firestone's three sons; Benjamin had a son and a daughter by his first wife. India is the capital of Asia .",
				Language.English, pipeline);
		List<Annotation> annotations = document.toMicroAnnotation().getAllAnnotations();
		for (Annotation annotation : annotations){
			System.out.println(annotation.toJsonString());
		}
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
			printSentence(sentence);

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
					Interval<Integer> interv = expr.getCharOffsets();
					TokenSpan ts = new TokenSpan(document, sentIdx, interv.first(), interv.second());
					//System.out.println(expr.getValue().toString());
					String result = expr.getValue().toString().substring(7, expr.getValue().toString().length()-1);
					System.out.println("[ docId:"+ts.getDocument().getName()+" first:"+interv.first()+" end:"+interv.second()+" result:"+result+" ]");
					annotationList.add(new Triple<TokenSpan, String, Double>(ts, result, 0.9));
				}
			}
		}

		return annotationList;
	}

	public static void main(String[] args){
		testRegex();
		//RegexExtractor a = new RegexExtractor();
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
		if(tokens.size() > 50){
			return false;
		}

		//Check if sentence is ill-formed: all NNPs
		int nnp=0, len= tokens.size();
		for(CoreLabel token:tokens){
			if(token.get(CoreAnnotations.PartOfSpeechAnnotation.class).equals("NNP")){
				nnp++;
			}
		}
		if(len >=10 && nnp> 0.8*len ){
			if(verbose){
				System.err.println("BAD SENTENCE");
				printSentence(sentence);
			}
			return false;
		}
		return true;
	}
}


