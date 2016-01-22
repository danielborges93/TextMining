package sumarization;

import Jama.Matrix;
import java.util.ArrayList;
import java.util.List;
import model.Document;

public class Techniques {

    private static Techniques singleton;

    private Techniques() {
    }

    public static Techniques sharedInstance() {
        if (singleton == null) {
            singleton = new Techniques();
        }
        return singleton;
    }

    public String removeStopwords(String text) {
        return text;
    }

    public Matrix TFIDF(Matrix frequencyMatrix, List<String> allWords, List<Document> documents) {
        Matrix TFIDFMatrix = frequencyMatrix.copy();
        for (int row = 0; row < TFIDFMatrix.getRowDimension(); row++) {
            for (int col = 0; col < TFIDFMatrix.getColumnDimension(); col++) {
                double tf = TFIDFMatrix.get(row, col);
                double[] rowValues = TFIDFMatrix.getArray()[row];
                double df = 0;
                for (double value : rowValues) {
                    df += value;
                }
                double log1 = Math.log(1+tf);
                double log2 = Math.log(df);
                double TFIDF = (double)documents.size()*(log1/log2);
                TFIDFMatrix.set(row, col, TFIDF);
            }
        }
        return TFIDFMatrix;
    }
}
