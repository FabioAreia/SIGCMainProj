/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package open.nlp;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import opennlp.tools.cmdline.PerformanceMonitor;
import opennlp.tools.cmdline.postag.POSModelLoader;
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
 * @author hamaro
 */
public class TreatText {

    private String fileurl;

    public ArrayList<ArrayList> allNews = new ArrayList<>();
    public ArrayList<ArrayList> allTokens = new ArrayList<>();
    public ArrayList<ArrayList> allWordTokens = new ArrayList<>();
    public ArrayList<String> allClasses = new ArrayList<>();
    public ArrayList<ArrayList> allNNTokens = new ArrayList<>();

    public TreatText(String fileLocation) {
        this.fileurl = fileLocation;

    }

    public void run() {
        // TODO code application logic here
        sortNewsAndSentences();
        //SentenceDetect();
        tokinization();
        POSTag();
    }

    public void sortNewsAndSentences() {
        File file = new File(this.fileurl);

        try {

            InputStream is = new FileInputStream("en-sent.bin");
            SentenceModel model = new SentenceModel(is);
            SentenceDetectorME sdetector = new SentenceDetectorME(model);

            boolean haveTitle = false;
            boolean haveContent = false;
            int currNewsId = -1;
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                String sentencesTemp[] = sdetector.sentDetect(line);
                if (haveTitle == false) {
                    if (sentencesTemp.length == 1) {
                        if (sentencesTemp[0].length() > 1) {
                            allClasses.add(sentencesTemp[0]);
                            System.out.println(sentencesTemp[0]);
                            allNews.add(new ArrayList<String>());
                            currNewsId = allNews.size() - 1;
                            haveTitle = true;
                        }
                    }
                } else if (haveTitle==true && haveContent == false) {
                    if (sentencesTemp.length > 0) {
                        if (sentencesTemp[0].length() > 1) {
                            for (String sentencesTemp1 : sentencesTemp) {
                                //System.out.println(sentencesTemp1);
                                allNews.get(currNewsId).add(sentencesTemp1);
                            }
                            haveContent = true;
                        }
                    }
                }else {
                    if (sentencesTemp.length > 0) {
                        if (sentencesTemp[0].length() > 1) {
                            for (String sentencesTemp1 : sentencesTemp) {
                                //System.out.println(sentencesTemp1);
                                allNews.get(currNewsId).add(sentencesTemp1);
                            }
                        } else {
                            haveTitle = false;
                            haveContent = false;
                        }
                    } else {
                        haveTitle = false;
                        haveContent = false;
                    }
                }
            }
            System.out.println(allClasses.size() + " noticias com:");
            int soma = 0;
            for (ArrayList<String> temp : allNews){
                System.out.println("\t" + temp.size() + " sentences.");
                soma += temp.size();
            }
            System.out.println("Um total de " + soma + " frases.");
            br.close();
        } catch (IOException e) {

        }
    }

    public void tokinization(){
        
        InputStream is;
        try {
            is = new FileInputStream("en-token.bin");
            TokenizerModel model = new TokenizerModel(is);
            Tokenizer tokenizer = new TokenizerME(model);

            for (ArrayList<String> temp : allNews){
                allTokens.add(new ArrayList<String>());
                for (String frase : temp){
                    String tokensTemp[] = tokenizer.tokenize(frase);
                    
                    for (String tok:tokensTemp){
                        //System.out.println(tok);
                        allTokens.get(allTokens.size()-1).add(tok);
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TreatText.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex){
            Logger.getLogger(TreatText.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void POSTag(){
        POSModel model = new POSModelLoader()
                .load(new File("en-pos-maxent.bin"));
        PerformanceMonitor perfMon = new PerformanceMonitor(System.err, "sent");
        POSTaggerME tagger = new POSTaggerME(model);
        
        System.out.println(allTokens.size());
        for (ArrayList<String> tem : allTokens){
            allWordTokens.add(new ArrayList<String[]>());
            allNNTokens.add(new ArrayList<String[]>());
            int currTId = allWordTokens.size()-1;
            //System.out.println(tem.size());
            for (String tmp : tem){
                //allWordTokens.get(currTId).add(new ArrayList<POSSample>());
                int currId = allWordTokens.size()-1;
                //System.out.println(tmp);
                String whitespaceTokenizerLine[] = WhitespaceTokenizer.INSTANCE
                            .tokenize(tmp);
                    String[] tags = tagger.tag(whitespaceTokenizerLine);

                    //POSSample sample = new POSSample(whitespaceTokenizerLine, tags);
                    //((ArrayList<POSSample>)allWordTokens.get(currId)).add(sample);
                    ((ArrayList<String[]>)allWordTokens.get(currId)).add(tags);
                    ((ArrayList<String[]>)allNNTokens.get(currId)).add(whitespaceTokenizerLine);
                    //System.out.println(sample.toString());
            }
        }
    }

}
