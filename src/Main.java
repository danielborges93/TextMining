
import java.io.File;
import sumarization.DocumentsFile;

public class Main {

    public static void main(String[] args) {
        sumarization("/Users/danielborges93/Dropbox/Mestrado/Mineração de texto/Atividade/sumarização/");
    }

    private static void sumarization(String folder) {

        try {
            File file = new File(folder);
            for (String fileLocation : file.list()) {
                fileLocation = folder + fileLocation;
                DocumentsFile documentsFile = new DocumentsFile(fileLocation);
                System.out.println(documentsFile.summary(6));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

}
