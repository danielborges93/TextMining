package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Word {

    private String word;
    private ArrayList<String> leftBag;
    private ArrayList<String> rightBag;

    public Word(String word, List<Document> documents) {
        this.word = word.toLowerCase();
        leftBag = new ArrayList<>();
        rightBag = new ArrayList<>();
        for (Document document : documents) {
            List<String> words = Arrays.asList(document.getPhrase().toLowerCase().split(" "));
            int index = words.indexOf(word);
            if (index == -1) {
                continue;
            }
            Techniques techniques = Techniques.sharedInstance();
            int left = index - 1;
            while (left >= 0) {
                String leftWord = words.get(left).toLowerCase();
                if (!techniques.isStopword(leftWord)) {
                    break;
                }
                left--;
            }
            if (left >= 0) {
                leftBag.add(words.get(left).toLowerCase());
            }
            int right = index + 1;
            while (right < words.size()) {
                String rightWord = words.get(right).toLowerCase();
                if (!techniques.isStopword(rightWord)) {
                    break;
                }
                right--;
            }
            if (right < words.size()) {
                rightBag.add(words.get(right).toLowerCase());
            }
        }
    }

    public ArrayList<String> getLeftBag() {
        return leftBag;
    }

    public ArrayList<String> getRightBag() {
        return rightBag;
    }

}
