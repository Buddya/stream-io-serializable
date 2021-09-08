import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Main {
    public static List<String> fileNames = new ArrayList<>();
    public static String folderPath = "C:\\Users\\nikolskaya\\Desktop\\Games\\savegames";

    public static void main(String[] args) {
        GameProgress[] gameProgresses = {
                new GameProgress(100, 3, 5, 10.45),
                new GameProgress(80, 4, 7, 20.45),
                new GameProgress(70, 5, 9, 30.45)
        };

        saveGame(folderPath, gameProgresses);
        boolean isZipped = zipFiles(folderPath, fileNames);
        deletingNonZippedFiles(fileNames, isZipped);
    }

    public static void saveGame(String folderPath, GameProgress... gameProgress) {
        int counter = 0;
        for (GameProgress progress : gameProgress) {
            String fileName = String.format("%s\\save%d.dat", folderPath, ++counter);
            try (FileOutputStream fos = new FileOutputStream(fileName);
                 ObjectOutputStream oos = new ObjectOutputStream(fos)) {
                oos.writeObject(progress);
                fileNames.add(fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean zipFiles(String folderPath, List<String> fileNames) {
        boolean isZipped = true;
        try (ZipOutputStream zos = new ZipOutputStream
                (new FileOutputStream(folderPath + "\\saves.zip"))) {
            for (String filename : fileNames) {
                String nameForZip = getFileNameForZip(filename);
                try (FileInputStream fis = new FileInputStream(filename)) {
                    ZipEntry zipEntry = new ZipEntry(nameForZip);
                    zos.putNextEntry(zipEntry);
                    byte[] buffer = new byte[fis.available()];
                    fis.read(buffer);
                    zos.write(buffer);
                }
            }
            zos.closeEntry();
        } catch (IOException e) {
            isZipped = false;
            e.printStackTrace();
        }
        return isZipped;
    }

    public static String getFileNameForZip(String fileName) {
        String[] shortFileName = fileName.split("\\\\");
        return shortFileName[shortFileName.length - 1];
    }

    public static void deletingNonZippedFiles(List<String> fileNames, boolean isZipped) {
        for (String name : fileNames) {
            File file = new File(name);
            file.deleteOnExit();
        }
    }
}

