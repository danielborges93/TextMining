package sumarization;

import Jama.Matrix;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import model.Document;

public class File {

    private String content;
    private ArrayList<Document> documents;
    private ArrayList<String> allWords;
    private Matrix frequencyMatrix;
    private Matrix TFIDF;
    private ArrayList<Document> orderedDocuments;

    public File(String location) {
        content = "Does anybody have document of .RTF file or know where I can get it? "
                + "I got one from Microsoft tech support. "
                + "Does repeted.";
        treatContent();
        createDocuments();
        getWords();
        createFrequencyMatrix();
        getTFIDFMatrix();
        applyWeight();
        rank();
    }

    public String summary(int i) {
        ArrayList<Document> copy = new ArrayList<>(documents);
        copy.removeIf(document -> !orderedDocuments.contains(document));
        StringBuilder string = new StringBuilder();
        for (int j = 0; j < i; j++) {
            if (j > copy.size()) {
                break;
            }
            Document document = copy.get(j);
            string.append(document.getPhrase());
            string.append(". ");
        }
        return string.toString();
    }

    private void treatContent() {
        content = content.replaceAll("([\\n\\s]+)", " ");
    }

    private void getWords() {
        Pattern pattern = Pattern.compile("([\\w\\.-]+)");
        Matcher matcher = pattern.matcher(content);
        HashSet words = new HashSet<>();
        while (matcher.find()) {
            String word = matcher.group().toLowerCase();
            Techniques techniques = Techniques.sharedInstance();
            if (techniques.isStopword(word)) {
                continue;
            }
            if (word.endsWith(".")) {
                word = word.replace(".", "");
            }
            words.add(word);
        }
        allWords = new ArrayList<>(words);
    }

    private void createDocuments() {
        documents = new ArrayList<>();
        String[] phrases = content.split("[\\.?!]{1,} ?");
        for (String phrase : phrases) {
            Document document = new Document(phrase);
            documents.add(document);
        }
    }

    private void createFrequencyMatrix() {
        int numberOfWords = allWords.size();
        int numberOfDocuments = documents.size();
        frequencyMatrix = new Matrix(numberOfWords, numberOfDocuments);
        for (int row = 0; row < frequencyMatrix.getRowDimension(); row++) {
            for (int col = 0; col < frequencyMatrix.getColumnDimension(); col++) {
                String word = allWords.get(row);
                Document document = documents.get(col);
                int frequency = document.frequencyOfWord(word);
                frequencyMatrix.set(row, col, frequency);
            }
        }
    }

    private void getTFIDFMatrix() {
        Techniques techniques = Techniques.sharedInstance();
        TFIDF = techniques.TFIDF(frequencyMatrix, allWords, documents);
    }

    private void applyWeight() {
        int midle = documents.size() / 2;
        int weight = midle;
        for (int col = 0; col < midle; col++) {
            for (int row = 0; row < TFIDF.getRowDimension(); row++) {
                double value = TFIDF.get(row, col);
                value *= weight;
                TFIDF.set(row, col, value);
            }
            weight--;
        }
        weight = 1;
        for (int col = midle; col < TFIDF.getColumnDimension(); col++) {
            for (int row = 0; row < TFIDF.getRowDimension(); row++) {
                double value = TFIDF.get(row, col);
                value *= weight;
                TFIDF.set(row, col, value);
            }
            weight++;
        }
    }

    private void rank() {
        for (int col = 0; col < TFIDF.getColumnDimension(); col++) {
            Document document = documents.get(col);
            double sum = 0;
            for (int row = 0; row < TFIDF.getRowDimension(); row++) {
                sum += TFIDF.get(row, col);
            }
            document.TFIDFSum = sum;
        }
        orderedDocuments = new ArrayList<>(documents);
        Collections.sort(orderedDocuments, (Document o1, Document o2)
                -> o1.TFIDFSum > o2.TFIDFSum ? 1 : -1
        );
    }

}
