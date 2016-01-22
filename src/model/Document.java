package model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Document {
    
    private final String phrase;
    public double TFIDFSum;
    
    public Document(String phrase) {
        this.phrase = phrase;
        TFIDFSum = 0;
    }
    
    public String getPhrase() {
        return phrase;
    }
    
    public int frequencyOfWord(String word) {
        Pattern pattern = Pattern.compile(" ?" + word + "\\W");
        Matcher matcher = pattern.matcher(phrase);
        int i = 0;
        while (matcher.find()) i++;
        return i;
    }
    
}
