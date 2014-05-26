/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package open.nlp;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import opennlp.tools.cmdline.PerformanceMonitor;
import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerEvaluator;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSample;
import opennlp.tools.doccat.DocumentSampleStream;
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
public class OpenNLP {

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

        //SentenceDetect();
        //tokinization();
        //POSTag();
        
        //trainModel();
        //testModel();
        
        
        
        System.out.println(classifyModel("Since when did Captain Slow test fast cars?")+ " = TopGear");
        System.out.println(classifyModel("where do you get your materials from? my mother said she would kill me if I destroyed another alarm clock. :)")+ " = jewl");
        System.out.println(classifyModel("Hey, where can I get these parts commonly... I have a steampunk band and I need to make clothes and accessories for us... please help... great work by the way :)")+ " = jewl");
        System.out.println(classifyModel("got my first penta kill on jayce today. had to come listen to this song XD")+ " = RiotGamesInc");
        
        System.out.println(classifyModel("who cares the show has been shit this season. why do a season in the wet season any way. plus they do laps twin and split screen fucking annoying. they did a shitty piece on the P1 no hot lap. the Porsche 918 had sponsored placement written all over it. top gear is slipping!")+ " = TopGear");
        System.out.println(classifyModel("there was about 1,000 real black hddvd given to xbox workers for working on the the black xbox elite")+ " = retrogames");
        System.out.println(classifyModel("The quaility of the camera is excellent and 40 a bargin indeed,i paid much more for my HD camera,and it looks equally as good,lucky you for spotting a bargain.")+ " = retrogames");
        System.out.println(classifyModel("Could you do a honest trailer for the first Transformers movie? You know the real one thats a cartoon.")+ " = screenjunkies");
    }

    public static void SentenceDetect() throws InvalidFormatException,
            IOException {
        String paragraph = "Hi. How are you? This is Mikel. I'm Fine.";
        File file = new File("Dataset_Wikinews.txt");
        BufferedReader readerText = new BufferedReader(new FileReader(file));

        InputStream is = new FileInputStream("en-sent.bin");
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
            System.out.println(sentencesNews.get(i));
        }
        System.out.println("Numero de frazes: " + sentencesNews.size());
        is.close();
    }

    public static LinkedList<String> tokinization() throws InvalidFormatException,
            IOException {
        File file = new File("Dataset_Wikinews.txt");
        BufferedReader readerText = new BufferedReader(new FileReader(file));

        // always start with a model, a model is learned from training data
        InputStream is = new FileInputStream("en-token.bin");
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

    public static String classifyModel(String teste){
        InputStream is = null;
        String category = "";
        try {
            String modelFilePath= "model.bin";
            is = new FileInputStream(modelFilePath);
            DoccatModel model = new DoccatModel(is);
            DocumentCategorizerME myCategorizer = new DocumentCategorizerME(model);
            double[] outcomes = myCategorizer.categorize(teste);
            
            int u=0;
            double max = 0;
            int mids = -1;
            for(double d : outcomes){
                if(d>max){
                    mids = u;
                    max = d;
                }
                //System.out.println("out "+u+": "+d);
                u++;
            }
            //System.out.println("Max: "+max+" idx: "+mids);
            category = myCategorizer.getBestCategory(outcomes);
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(OpenNLP.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
                Logger.getLogger(OpenNLP.class.getName()).log(Level.SEVERE, null, ex);
            }  finally {
            try {
                is.close();
            } catch (IOException ex) {
                Logger.getLogger(OpenNLP.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return category;
    }
    
    public static void testModel() {
        InputStream is = null;
        try {
            String modelFilePath = "model.bin";
            is = new FileInputStream(modelFilePath);
            DoccatModel model = new DoccatModel(is);
            DocumentCategorizerME myCategorizer = new DocumentCategorizerME(model);
            DocumentCategorizerEvaluator evaluator = new DocumentCategorizerEvaluator(myCategorizer);
            //ArrayList<DocumentSample> docsamples = new ArrayList<>();
            String category = "TopGear";
            String content = "the back of most lamborghinis are pretty ugly, to put it nicely :)";
            DocumentSample sample = new DocumentSample(category, content);
            evaluator.evaluteSample(sample);
            double result = evaluator.getAccuracy();
            System.out.println("Accuracy = " + result);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(OpenNLP.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
                Logger.getLogger(OpenNLP.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
            try {
                is.close();
            } catch (IOException ex) {
                Logger.getLogger(OpenNLP.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void trainModel() {
        String modelFilePath = "model.bin";
        String trainingFile = "traindata.txt";
        // Instance of openNLP's default model class
        DoccatModel model = null;
        InputStream dataIn = null;
        try {
            dataIn = new FileInputStream(trainingFile);
            ObjectStream<String> lineStream = new PlainTextByLineStream(dataIn,
                    "UTF-8");
            ObjectStream<DocumentSample> sampleStream = new DocumentSampleStream(lineStream);
            // "en" is language code of English.
            model = DocumentCategorizerME.train("en", sampleStream);
        } catch (IOException e) {
            //log.error("Failed to read or parse training data, training failed", e);
        } finally {
            if (dataIn != null) {
                try {
                    // free the memory resources.
                    dataIn.close();
                } catch (IOException e) {
                    //log.warn(e.getLocalizedMessage());
                }
            }
        }
        OutputStream modelOut = null;
        try {
            modelOut = new BufferedOutputStream(new FileOutputStream(modelFilePath));
            model.serialize(modelOut);
        } catch (IOException e) {
            //log.error("Failed to save model at location " + modelFilePath);
        } finally {
            if (modelOut != null) {
                try {
                    modelOut.close();
                } catch (IOException e) {
                    //log.error("Failed to correctly save model. Written model might be invalid.");
                }
            }
        }
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

//        String input = tokinization().get(6);
        String input = "";
        for (int i = 0; i < tokens.size(); i++) {
            input = tokens.get(i);
            String whitespaceTokenizerLine[] = WhitespaceTokenizer.INSTANCE
                    .tokenize(tokens.get(i));
            String[] tags = tagger.tag(whitespaceTokenizerLine);
            for (String tag : tags) {
                if (tag.contains("NN")) {
                    classificadoResultado.add(tag);
                    classificadoNome.add(tokens.get(i));

//                        HashMap
                    if (!contadorPalavra.containsKey(tokens.get(i))) {
                        System.out.println("ss");
                        contadorPalavra.put(tokens.get(i), 1);
                        Integer temp[] = new Integer[10];

                        for (int j = 0; j < 10; j++) {
                            temp[j] = 0;
                        }

                        contadorPalavraGlobal.put(tokens.get(i), temp);
                    } else if (contadorPalavra.containsKey(tokens.get(i))) {
                        contadorPalavra.put(tokens.get(i), contadorPalavra.get(tokens.get(i)) + 1);
                        System.out.println("REPETIDOS");
                    }

                }
            }

            POSSample sample = new POSSample(whitespaceTokenizerLine, tags);
            System.out.println(sample.toString());

        }

        ObjectStream<String> lineStream = new PlainTextByLineStream(
                new StringReader(input));

        perfMon.start();
        String line;
        while ((line = lineStream.read()) != null) {

            String whitespaceTokenizerLine[] = WhitespaceTokenizer.INSTANCE
                    .tokenize(line);
            String[] tags = tagger.tag(whitespaceTokenizerLine);

            POSSample sample = new POSSample(whitespaceTokenizerLine, tags);
            System.out.println(sample.toString());

            perfMon.incrementCounter();
        }
        perfMon.stopAndPrintFinalResult();
        System.out.println("========================\n\n");
        for (int i = 0; i < classificadoResultado.size(); i++) {
            System.out.println(classificadoNome.get(i) + "-->" + classificadoResultado.get(i));
        }

        File dadosWeka = new File("datasetRepetidos.csv");
        dadosWeka.createNewFile();
        FileWriter fw = new FileWriter(dadosWeka);
        BufferedWriter bw = new BufferedWriter(fw);

        for (int i = 0; i < classificadoNome.size(); i++) {
            bw.write(classificadoNome.get(i) + ",");
        }

        File dadosWeka2 = new File("datasetSemRepetidos2.csv");
        dadosWeka2.createNewFile();
        FileWriter fw2 = new FileWriter(dadosWeka2);
        BufferedWriter bw2 = new BufferedWriter(fw2);

//            for (int i = 0; i < tokens.size(); i++) {
//                bw2.write(tokens.get(i) +"  -->  " + contadorPalavra.get(tokens.get(i))+ "\n");
//            }
//            for (int i = 0; i < classificadoNome.size(); i++) {
//                bw.write(contadorPalavra.get(classificadoNome.get(i)).toString());
//            }
        for (int i = 0; i < 10; i++) {

            ArrayList<String[]> tags2 = treatText.allWordTokens.get(i);
            ArrayList<String[]> tempTokens = treatText.allNNTokens.get(i);
            int o = 0;
            for (String[] tags : tags2) {
                String[] tempTok = tempTokens.get(o);
                int m = 0;
                for (String tag : tags) {
                    String tempT = tempTok[m];
                    if (tag.contains("NN")) {
                        classificadoResultado.add(tag);
                        classificadoNome.add(tokens.get(i));

//                        HashMap
////                        if (!contadorPalavra.containsKey(tokens.get(i))) {
////                            System.out.println("ss");
////                            contadorPalavra.put(tokens.get(i), 1);
                        if (contadorPalavraGlobal.containsKey(tempT)) {
                            Integer[] arrayActual = contadorPalavraGlobal.get(tempT);
                            arrayActual[i] = arrayActual[i] + 1;
                            contadorPalavraGlobal.put(tempT, arrayActual);
                            System.out.println("REPETIDOS");
                        }

                    }
                    m++;
                }
                o++;
            }
        }

//                for (Entry<String, Integer> entry : contadorPalavra.entrySet()) {
////    System.out.println(entry.getKey() + "/" + entry.getValue());
//            bw2.write(entry.getKey() + "  -->  " + entry.getValue() + "\n");
//
//        }
        String[] listaLinhas = new String[11];
        for (int i = 0; i < listaLinhas.length; i++) {
            listaLinhas[i] = "";
        }

        for (Entry<String, Integer[]> entry : contadorPalavraGlobal.entrySet()) {
            listaLinhas[0] += entry.getKey() + ",";
            int contLinha = 1;
            for (Integer cc : entry.getValue()) {
                Integer[] arrayActual = entry.getValue();
                listaLinhas[contLinha] += arrayActual[contLinha - 1] + ",";
                contLinha++;
            }

//    System.out.println(entry.getKey() + "/" + entry.getValue());
        }
        for (String s : listaLinhas) {
            bw2.write(s + "\n");
        }

        bw.close();
        bw2.close();
        fw.close();
        fw2.close();
    }

}
