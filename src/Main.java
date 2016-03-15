import java.io.File;

public class Main {

    public static void main(String[] args) {
//        similarity("/Users/danielborges93/Dropbox/Mestrado/Mineração de texto/Atividade/Dataset.txt");
//        sumarization("/Users/danielborges93/Dropbox/Mestrado/Mineração de texto/Atividade/sumarização/");
        blogSumarization("database/");
    }

    private static void sumarization(String folder) {
        try {
            File file = new File(folder);
            for (String fileLocation : file.list()) {
                fileLocation = folder + fileLocation;
                MiningFile documentsFile = new MiningFile(fileLocation);
                documentsFile.prepareForSumarization();
                System.out.println(documentsFile.summary(6));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private static void similarity(String folder) {
        try {
            MiningFile file = new MiningFile(folder);
            file.prepareForSimilarity();
            int numberOfWords = 4;
            String word = "apple";
            System.out.println("The " + numberOfWords + " similar to " + word + ":");
            System.out.println(file.similarWordsTo(word, numberOfWords));
            System.out.println();
            System.out.println();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private static void blogSumarization(String folder) {
        try {
            File file = new File(folder);
            int j = 0;
            for (String fileLocation : file.list()) {
                j++;
                fileLocation = folder + fileLocation;
                MiningFile file1 = new MiningFile(fileLocation);
                file1.prepareForBlogSummarization();
                String mySummary = file1.summary(7);
                String[] personSumary = file1.getPersonSumaries();
                System.out.println("My:");
                System.out.println(mySummary);
                for (int i=0; i<personSumary.length; i++) {
                    String summary = personSumary[i];
                    System.out.println("Person " + (i+1));
                    System.out.println(summary);
                }
                System.out.println();
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

}
