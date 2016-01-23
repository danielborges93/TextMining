
import Jama.Matrix;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import model.Document;
import model.Techniques;
import model.Word;

public class MiningFile {

    private final String location;
    private String content;
    private ArrayList<Document> documents;
    private ArrayList<String> allWords;
    private Matrix frequencyMatrix;
    private Matrix TFIDF;
    private ArrayList<Document> orderedDocuments;
    private HashMap<String, Word> mapWords;
    private Matrix similarityMatrix;

    public MiningFile(String location) throws Exception {
        this.location = location;
    }

    public void prepareForSumarization() throws Exception {
        getContent();
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

    public void prepareForSimilarity() throws Exception {
        getContent();
        treatContent();
        createDocuments();
        getWords();
        createBagsOfWords();
        createFrequencyMatrix();
        getTFIDFMatrix();
        calculateSimilarity();
    }

    public List<String> similarWordsTo(String word, int number) {
        int index = allWords.indexOf(word);
        double[] similarityRow = similarityMatrix.getArray()[index];
        List<String> orderedWords = new ArrayList<>(allWords);
        Collections.sort(orderedWords, (String o1, String o2) -> {
            int index1 = allWords.indexOf(o1);
            int index2 = allWords.indexOf(o2);
            double value1 = similarityRow[index1];
            double value2 = similarityRow[index2];
            return value1 >= value2 ? 1 : -1;
        });
        if (number > orderedWords.size()) {
            number = orderedWords.size();
        }
        return orderedWords.subList(0, number);
    }

    private void getContent() throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(location), "ISO-8859-1"));
        StringBuilder contentBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            contentBuilder.append(line).append(" ");
        }
        content = contentBuilder.toString();
    }

    private void treatContent() {
        content = content.replaceAll("([\\n\\s]+)", " ");
    }

    private void getWords() {
        Pattern pattern = Pattern.compile("([a-zA-ZáàâãéèêíïóôõöúçñÁÀÂÃÉÈÊÍÏÓÔÕÖÚÇÑ0-9'-]+)");
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

    private void createBagsOfWords() {
        mapWords = new HashMap<>();
        for (String word : allWords) {
            Word w = new Word(word, documents);
            mapWords.put(word, w);
        }
    }

    private void calculateSimilarity() {
        int numberOfWords = allWords.size();
        similarityMatrix = new Matrix(numberOfWords, numberOfWords);
        for (int i1 = 0; i1 < numberOfWords; i1++) {
            for (int i2 = 0; i2 < numberOfWords; i2++) {
                String word1 = allWords.get(i1);
                String word2 = allWords.get(i2);
                if (word1.equals(word2)) {
                    continue;
                }
                List<String> leftBagOfWord1 = mapWords.get(word1).getLeftBag();
                List<String> rightBagOfWord1 = mapWords.get(word1).getRightBag();
                List<String> leftBagOfWord2 = mapWords.get(word2).getLeftBag();
                List<String> rightBagOfWord2 = mapWords.get(word2).getRightBag();
                List<String> leftCommonWords = new ArrayList<>();
                List<String> rightCommonWords = new ArrayList<>();
                for (String leftBagWord1 : leftBagOfWord1) {
                    if (leftBagOfWord2.contains(leftBagWord1)) {
                        leftCommonWords.add(leftBagWord1);
                    }
                }
                for (String rightBagWord1 : rightBagOfWord1) {
                    if (rightBagOfWord2.contains(rightBagWord1)) {
                        rightCommonWords.add(rightBagWord1);
                    }
                }
                double tfidfSum = 0;
                for (String commonWord : leftCommonWords) {
                    int index = allWords.indexOf(commonWord);
                    for (double tfidf : TFIDF.getArray()[index]) {
                        tfidfSum += tfidf;
                    }
                }
                for (String commonWord : rightCommonWords) {
                    int index = allWords.indexOf(commonWord);
                    if (index == -1) {
                        System.out.println(commonWord);
                        System.out.println(allWords);
                    }
                    for (double tfidf : TFIDF.getArray()[index]) {
                        tfidfSum += tfidf;
                    }
                }
                double similarValue = tfidfSum;
                similarityMatrix.set(i2, i1, similarValue);
            }
        }
    }

}
