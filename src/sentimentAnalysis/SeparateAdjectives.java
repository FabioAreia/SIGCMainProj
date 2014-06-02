/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sentimentAnalysis;

import com.sun.org.apache.bcel.internal.generic.AALOAD;
import com.sun.org.apache.bcel.internal.generic.RETURN;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import open.nlp.TreatText;
import opennlp.maxent.GISModel;
import opennlp.maxent.io.PlainTextGISModelReader;
import opennlp.tools.cmdline.PerformanceMonitor;
import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;

/**
 *
 * @author Fábio
 */
public class SeparateAdjectives {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
//        File file = new File("C:\\Users\\Fábio\\Documents\\NetBeansProjects\\Open NLP\\en-pos-maxent.bin");
//        BufferedReader reader = new BufferedReader(new FileReader(file));
//        
//        PlainTextGISModelReader readerNLP = new PlainTextGISModelReader(file);
//        GISModel model = (GISModel) readerNLP.getModel();
//        DocumentCategorizerME dc = new DocumentCategorizerME (model);

        SentenceDetect();
        tokinization();
        POSTag();
    }

    public static void SentenceDetect() throws InvalidFormatException,
            IOException {
        String paragraph = "Hi. How are you? This is Mikel. I'm Fine.";
        File file = new File("Dataset_Wikinews.txt");
        BufferedReader readerText = new BufferedReader(new FileReader(file));

        InputStream is = new FileInputStream("models\\en-sent.bin");
        SentenceModel model = new SentenceModel(is);
        SentenceDetectorME sdetector = new SentenceDetectorME(model);

        String sentences[] = sdetector.sentDetect(paragraph);
        LinkedList<String> sentencesNews = new LinkedList<>();

        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        while ((line = br.readLine()) != null) {
            String sentencesTemp[] = sdetector.sentDetect(line);
            for (int i = 0; i < sentencesTemp.length; i++) {
                sentencesNews.add(sentencesTemp[i]);
            }
        }
        br.close();

        for (int i = 0; i < sentencesNews.size(); i++) {
//            System.out.println(sentencesNews.get(i));
        }
//        System.out.println("Numero de frazes: " + sentencesNews.size());
        is.close();
    }

    public static LinkedList<String> tokinization() throws InvalidFormatException,
            IOException {
        File file = new File("Dataset_Wikinews.txt");
        BufferedReader readerText = new BufferedReader(new FileReader(file));

        // always start with a model, a model is learned from training data
        InputStream is = new FileInputStream("models\\en-token.bin");
        TokenizerModel model = new TokenizerModel(is);
        Tokenizer tokenizer = new TokenizerME(model);

        LinkedList<String> tokens = new LinkedList<>();

        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        while ((line = br.readLine()) != null) {
            String tokensTemp[] = tokenizer.tokenize(line);
            for (int i = 0; i < tokensTemp.length; i++) {
                tokens.add(tokensTemp[i]);
            }
        }
        br.close();

//        System.out.println(sentences[0]);
//        System.out.println(sentences[1]);
//        System.out.println(sentences[2]);
        for (int i = 0; i < tokens.size(); i++) {
            System.out.println(tokens.get(i));
        }
        System.out.println("Numero de palavras: " + tokens.size());
        is.close();
        return tokens;
    }

    public static void POSTag() throws IOException {
        TreatText treatText = new TreatText("Dataset_Wikinews.txt");
        treatText.run();
        LinkedList<String> classificadoResultado = new LinkedList<>();
        LinkedList<String> classificadoNome = new LinkedList<>();
        HashMap<String, Integer> contadorPalavra = new HashMap<String, Integer>();
        HashMap<String, Integer[]> contadorPalavraGlobal = new HashMap<String, Integer[]>();
        POSModel model = new POSModelLoader()
                .load(new File("en-pos-maxent.bin"));
        PerformanceMonitor perfMon = new PerformanceMonitor(System.err, "sent");
        POSTaggerME tagger = new POSTaggerME(model);
        LinkedList<String> tokens = new LinkedList<>();
        tokens = tokinization();
        System.out.println("tamanho dos tokens" + tokens.size());

        SentiWordNet sentidor = new SentiWordNet("SentiWordNet_3.0.0_20130122.txt");
        LinkedList<String> adjectives = new LinkedList<>();

//        String input = tokinization().get(6);
        String input = "";
        for (int i = 0; i < tokens.size(); i++) {
            input = tokens.get(i);
            String whitespaceTokenizerLine[] = WhitespaceTokenizer.INSTANCE
                    .tokenize(tokens.get(i));
            String[] tags = tagger.tag(whitespaceTokenizerLine);
            for (String tag : tags) {
                                    if (tag.contains("JJ")) {
                                        adjectives.add(tokens.get(i));
//                        sentidor.pontuar(tokens.get(i));
                                        
                                        
                    }
                                    
            }

            POSSample sample = new POSSample(whitespaceTokenizerLine, tags);
//            System.out.println(sample.toString());
        }
        sentidor.scoreComment(adjectives);
    }
    
}
       

