package sumarization;

public class Techniques {
    
    private static Techniques singleton;
    
    public static Techniques sharedInstance() {
        if (singleton == null) {
            singleton = new Techniques();
        }
        return singleton;
    }
    
    public String removeStopwords(String text) {
        return text;
    }
    
    public Double TFIDF(Double wordsForDocs, Double qtddPalavrasDoc, Double qtddDoc, Double palavraPorDocs){
        Double TF = Math.log(1 + wordsForDocs);
        Double IDF = Math.log(qtddDoc/palavraPorDocs);
        return TF*IDF;
    }
}
