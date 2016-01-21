import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sumarization.Techniques;

public class Main {
    
    public static void main(String[] args) {
        sumarization("");
    }
    
    private static void sumarization(String folder) {
        
        Techniques techniques = Techniques.sharedInstance();
        String text = "Does anybody have document of .RTF file or know where I can get it? "
                + "I got one from Microsoft tech support. "
                + "Does repeted.";
        text = treatText(text);
        text = techniques.removeStopwords(text);
        ArrayList words = getWords(text);
        
        
    }
    
    private static String treatText(String text) {
        return null;
    }
    
    private static ArrayList<String> getWords(String text) {
        Pattern pattern = Pattern.compile("([\\w\\.-]+)");
        Matcher matcher = pattern.matcher(text);
        HashSet words = new HashSet<>();
        while (matcher.find()) {
            String word = matcher.group().toLowerCase();
            if (word.endsWith(".")) {
                word = word.replace(".", "");
            }
            words.add(word);
        }
        return new ArrayList<>(words);
    }
    
}
