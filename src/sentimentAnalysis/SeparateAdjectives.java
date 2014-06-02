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
//        tokinization();
        evaluateComment("My wife and I saw a three-wheeled car just a few days ago. My wife asked me if it was safe, and I said not nearly as safe as a four-wheel car. Now we know exactly how not-safe :-) ﻿");
    }

    public static LinkedList<String> tokinization(String comment) throws InvalidFormatException,
            IOException {
//        File file = new File("Dataset_Wikinews.txt");
//        BufferedReader readerText = new BufferedReader(new FileReader(file));

        // always start with a model, a model is learned from training data
        InputStream is = new FileInputStream("models\\en-token.bin");
        TokenizerModel model = new TokenizerModel(is);
        Tokenizer tokenizer = new TokenizerME(model);

        LinkedList<String> tokens = new LinkedList<>();

//        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = comment;
//        while ((line = br.readLine()) != null) {
        String tokensTemp[] = tokenizer.tokenize(line);
        for (int i = 0; i < tokensTemp.length; i++) {
            tokens.add(tokensTemp[i]);
        }
//        }
//        br.close();

//        System.out.println(sentences[0]);
//        System.out.println(sentences[1]);
//        System.out.println(sentences[2]);
//        for (int i = 0; i < tokens.size(); i++) {
//            System.out.println(tokens.get(i));
//        }
        System.out.println("Numero de palavras: " + tokens.size());
        is.close();
        return tokens;
    }

    public static double evaluateComment(String comment) throws IOException {
        double score = 0;
        boolean butclause = false;
        boolean opinioShifter = false;
//        TreatText treatText = new TreatText("Dataset_Wikinews.txt");
//        treatText.run();
        LinkedList<String> classificadoResultado = new LinkedList<>();
        LinkedList<String> classificadoNome = new LinkedList<>();
        HashMap<String, Integer> contadorPalavra = new HashMap<String, Integer>();
        HashMap<String, Integer[]> contadorPalavraGlobal = new HashMap<String, Integer[]>();
        POSModel model = new POSModelLoader()
                .load(new File("en-pos-maxent.bin"));
        PerformanceMonitor perfMon = new PerformanceMonitor(System.err, "sent");
        POSTaggerME tagger = new POSTaggerME(model);
        LinkedList<String> tokens = new LinkedList<>();
        tokens = tokinization(comment);
        System.out.println("tamanho dos tokens" + tokens.size());

        SentiWordNet sentidor = new SentiWordNet("SentiWordNet_3.0.0_20130122.txt");
        LinkedList<String> adjectives = new LinkedList<>();
        LinkedList<String> adjectivesShiftados = new LinkedList<>();
        LinkedList<String> adjectivesWithBut = new LinkedList<>();
        

//        String input = tokinization().get(6);
        String input = "";
        for (int i = 0; i < tokens.size(); i++) {
            input = tokens.get(i);
            String whitespaceTokenizerLine[] = WhitespaceTokenizer.INSTANCE
                    .tokenize(tokens.get(i));
            String[] tags = tagger.tag(whitespaceTokenizerLine);
            for (String tag : tags) {
                if (tag.contains("JJ") && !opinioShifter && !butclause) {
                    adjectives.add(tokens.get(i));
//                        sentidor.pontuar(tokens.get(i));
                }
                
              if (tag.contains("JJ") && opinioShifter) {
                    adjectivesShiftados.add(tokens.get(i));
                    System.out.println("APANHEI UM NOT ANTES");
//  
                }
              
               if (tag.contains("JJ") && butclause) {
                    adjectivesWithBut.add(tokens.get(i));
                    System.out.println("APANHEI UM NOT ANTES");
                }
               
              
               if (tag.contains("JJ")){
                       butclause = false;
                       opinioShifter = false;
               }
                       
                if (tokens.get(i).equals("not") || tokens.get(i).equals("never") || tokens.get(i).equals("none") || tokens.get(i).equals("nobody") || tokens.get(i).equals("nowhere") || tokens.get(i).equals("neither") || tokens.get(i).equals("cannot")){
                    opinioShifter=true;
//                    System.out.println("Apanhado opinion Shifter");
                }
                
                if (tokens.get(i).equals("but")){
                    butclause=true;
//                    System.out.println("Apanhado butclause");
                }

            }

            POSSample sample = new POSSample(whitespaceTokenizerLine, tags);
//            System.out.println(sample.toString());
        }
        score = sentidor.scoreComment(adjectives);
        return score;
    }
}
