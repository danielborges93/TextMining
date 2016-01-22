import sumarization.File;

public class Main {
    
    public static void main(String[] args) {
        sumarization("");
    }
    
    private static void sumarization(String folder) {
        
        File file = new File("");
        System.out.println(file.summary(2));
        
    }
    
}
