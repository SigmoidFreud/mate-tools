package se.lth.cs.srl.options;

import java.io.File;
import java.io.PrintStream;

import se.lth.cs.srl.languages.Language;
import se.lth.cs.srl.languages.Language.L;

public abstract class FullPipelineOptions {
	
	public L l;
	public File tokenizer;
	public File lemmatizer;
	public File tagger;
	public File morph;
	public File parser;
	public File srl;
	
	public boolean reranker=false;
	public int aiBeam=4;
	public int acBeam=4;
	public double alfa=1.0;
	
//	public void verifyFiles(boolean verifyTokenizer,boolean verifyLemmatizer,boolean verifyTagger,boolean verifyMTagger,boolean verifyParser,boolean verifySRL) {
//		List<File> files=new ArrayList<File>();
//		if(verifyTokenizer){
//			if(tokenizer==null){
//				System.err.println("You forgot to specify the model file for the tokenizer. Try again.");
//				System.exit(1);
//			} else
//				files.add(tokenizer);
//		}
//		if(verifyLemmatizer){
//			if(lemmatizer==null){
//				System.err.println("You forgot to specify the model file for the lemmatizer. Try again.");
//				System.exit(1);
//			} else
//				files.add(lemmatizer);
//		}
//		if(verifyTagger){
//			if(tagger==null){
//				System.err.println("You forgot to specify the model file for the pos tagger. Try again.");
//				System.exit(1);
//			} else 
//				files.add(tagger);
//		}
//		if(verifyMTagger){
//			if(morph==null){
//				System.err.println("You forgot to specify the model file for the morphological tagger. Try again.");
//				System.exit(1);
//			} else
//				files.add(morph);
//		}
//		if(verifyParser){
//			if(parser==null){
//				System.err.println("You forgot to specify the model file for the dependency parser. Try again.");
//				System.exit(1);
//			} else
//				files.add(parser);
//		}
//		if(verifySRL){
//			if(srl==null){
//				System.err.println("You forgot to specify the model file for the srl system. Try again.");
//				System.exit(1);
//			} else
//				files.add(srl);
//		}
//		for(File f:files){
//			if(!f.exists()){
//				System.err.println("File "+f+" does not exist. Aborting.");
//				System.exit(1);
//			}
//			if(!f.canRead()){
//				System.err.println("File "+f+" can not be read. Aborting.");
//				System.exit(1);
//			}
//		}
//	}
	
	public ParseOptions getParseOptions(){
		ParseOptions parseOptions=new ParseOptions();
		parseOptions.modelFile=srl;
		parseOptions.useReranker=reranker;
		parseOptions.global_aiBeam=aiBeam;
		parseOptions.global_acBeam=acBeam;
		parseOptions.global_alfa=alfa;
		return parseOptions;
	}
	
	public void parseCmdLineArgs(String[] args) {
		int ai=0;
		if(args.length<1){
			System.err.println("Not enough arguments. Aborting.");
			printUsage(System.err);
			System.exit(1);
		}
		try {
			l=L.valueOf(args[ai]);
		} catch (Exception e){
			System.err.println("Unknown language: "+args[ai]+", aborting.");
			System.err.println();
			printUsage(System.err);
			System.exit(1);
		}
		Language.setLanguage(l);
		ai++;
		while(ai<args.length){
			int newAi=tryParseArg(args,ai);
			if(ai==newAi)
				newAi=trySubParseArg(args,ai);
			if(ai==newAi){
				System.err.println("Unknown option: "+args[ai]);
				System.exit(1);
			}
			ai=newAi;
		}
	}


	abstract int trySubParseArg(String[] args,int ai);
	
	/**
	 * Tries to parse one argument off the cmdline. 
	 * @param args the args for the main method
	 * @param ai the current index
	 * @return the new ai
	 */
	public int tryParseArg(String[] args,int ai){
		if(args[ai].equals("-h") || args[ai].equals("-help") || args[ai].equals("--help")){
			printUsage(System.err);
			System.exit(1);
		} else if(args[ai].equals("-token")){
			ai++;
			tokenizer=new File(args[ai]);
			ai++;
		} else if(args[ai].equals("-lemma")){
			ai++;
			lemmatizer=new File(args[ai]);
			ai++;
		} else if(args[ai].equals("-tagger")){
			ai++;
			tagger=new File(args[ai]);
			ai++;
		} else if(args[ai].equals("-morph")){
			ai++;
			morph=new File(args[ai]);
			ai++;
		} else if(args[ai].equals("-parser")){
			ai++;
			parser=new File(args[ai]);
			ai++;
		} else if(args[ai].equals("-srl")){
			ai++;
			srl=new File(args[ai]);
			ai++;
		} else if(args[ai].equals("-reranker")){
			ai++;
			reranker=true;
		} else if(args[ai].equals("-aiBeam")){
			ai++;
			aiBeam=Integer.valueOf(args[ai]);
			ai++;
		} else if(args[ai].equals("-acBeam")){
			ai++;
			acBeam=Integer.valueOf(args[ai]);
			ai++;
		} else if(args[ai].equals("-alfa")){
			ai++;
			alfa=Double.parseDouble(args[ai]);
			ai++;
		}
		return ai;
	}

	abstract String getSubUsageOptions();
	public void printUsage(PrintStream out){		
			out.println("Usage:");
			out.println("java -cp ... "+this.getClass().getName()+" <lang> <options>");
			out.println();
			out.println("Where <lang> is one of: "+Language.getLsString());
			out.println();
			out.println("And <options> correnspond to one of the following:");
			out.println(USAGE_OPTIONS);
			out.println(getSubUsageOptions());
			out.println();
			out.println("The model files neccessary vary between languages. E.g. German uses a morphological tagger,\n" +
						"whereas Chinese and English doesn't. The parser and srl models are always required though.\n" +
						"For Chinese, the tokenizer model should point to the data directory of the Stanford Chinese\n" +
						"Segmenter, as provided in the 2008-05-21 distribution.\n" +
						"\n" +
						"For further information check the website:\n" +
						"http://code.google.com/p/mate-tools/");
		
	}
	private static final String USAGE_OPTIONS=
		"-token  <file>    path to the tokenizer model file\n"+
		"-lemma  <file>    path to the lemmatizer model file\n"+
		"-tagger <file>    path to the pos tagger model file\n"+
		"-morph  <file>    path to the morphological tagger model file\n"+
		"-parser <file>    path to the parser model file\n"+
		"-srl    <file>    path to the srl model file\n"+
		"-reranker         use the reranker for the srl-system (default is not)\n"+
		"-aibeam <int>     set the beam width of the ai component (default 4)\n"+
		"-acbeam <int>     set the beam width of the ac component (default 4)\n"+
		"-alfa   <double>  set the alfa for the reranker (default 1.0)";


	
}