import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Main {

    public static void main(String[] args) {
        StringBuilder feedback = new StringBuilder();
        String initDirectory = "C:" + File.separator + File.separator +"Games";
        File games = creationDirectory(initDirectory, feedback);

        File temp = creationDirectory(initDirectory + File.separator + "temp", feedback);
        File file3 = creationFile(initDirectory + File.separator + "temp"
                + File.separator + "temp.txt", feedback);

        String srcFolder = initDirectory + File.separator + "src";
        String savegamesFolder = initDirectory + File.separator + "savegames";
        String resFolder = initDirectory + File.separator + "res";

        File src = creationDirectory(srcFolder, feedback);
        File res = creationDirectory(initDirectory + File.separator + "res", feedback);
        File savegames = creationDirectory(savegamesFolder, feedback);


        File main = creationDirectory(srcFolder + File.separator +"main", feedback);
        File test = creationDirectory(srcFolder + File.separator + "test", feedback);

        String mainFolder = srcFolder + File.separator;
        File file1 = creationFile(mainFolder + "Main.java", feedback);
        File file2 = creationFile(mainFolder + "Utils.java", feedback);

        String resContain = resFolder + File.separator;

        File drawables = creationDirectory(resContain + "drawables", feedback);
        File vectors = creationDirectory(resContain + "vectors",feedback);
        File icons = creationDirectory(resContain + "icons", feedback);

        String[] created = feedback.toString().split("\n");

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file3.getPath(), true))) {

            for (String i : created) {
                bw.write(i);
                bw.append("\n");
            }

            bw.flush();
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }

        GameProgress game1 = new GameProgress(100, 12, 10, 1);
        GameProgress game2 = new GameProgress(86, 3, 80, 345);
        GameProgress game3 = new GameProgress(94, 524, 76, 44);

        File save1 = creationFile(savegamesFolder + File.separator + "save1.dat", feedback);
        saveGame(save1.getPath(), game1);

        File save2 = creationFile(savegamesFolder + File.separator + "save2.dat", feedback);
        saveGame(save2.getPath(), game2);

        File save3 = creationFile(savegamesFolder + File.separator + "save3.dat", feedback);
        saveGame(save3.getPath(), game3);

        List<File> savedVersions = new ArrayList<>();

        savedVersions.add(save1);
        savedVersions.add(save2);
        savedVersions.add(save3);

        File zipedVersions = creationFile(savegamesFolder + File.separator + "packed_versions.zip", feedback);

        zipFiles(zipedVersions.getPath(), savedVersions);

        delitingFiles(save1);
        delitingFiles(save2);
        delitingFiles(save3);

        unzip(zipedVersions, feedback);

        GameProgress gameDeSerialized = null;

        deSerialization(savegamesFolder + File.separator + "packed_save1.dat.txt", gameDeSerialized);

    }

    private static File creationDirectory(String path, StringBuilder feedback) {
        File directory = new File(path);
        if (directory.mkdir()) {
            feedback.append("Каталог " + directory.getName() + " создан \n");
            System.out.println("Каталог " + directory.getName() + " создан");
        }
        return directory;
    }

    private static File creationFile(String path, StringBuilder feedback) {
        File file = new File(path);
        try {
            if (file.createNewFile()) {
                feedback.append("Файл" + file.getName() + " создан \n");
                System.out.println("Файл" + file.getName() + " создан");
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return file;
    }

    private static void saveGame(String path, GameProgress version) {
        try (FileOutputStream fis = new FileOutputStream(path);
            ObjectOutputStream oos = new ObjectOutputStream(fis)) {
            oos.writeObject(version);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void zipFiles(String path, List<File> files) {
        try (ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(path))) {
            for (File file : files) {
                try (FileInputStream fis = new FileInputStream(file.getPath())) {
                    ZipEntry entry = new ZipEntry("packed_" + file.getName() + ".txt");
                    zout.putNextEntry(entry);
                    byte[] buffer = new byte[fis.available()];
                    fis.read(buffer);
                    zout.write(buffer);
                    zout.closeEntry();
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void delitingFiles (File file) {
        if (file.delete()) {
            System.out.println("Файл " + file.getName() +  " удален");
        }
    }

    public static void unzip(File file, StringBuilder feedback) {
        try (ZipInputStream zin = new ZipInputStream(new FileInputStream(file.getPath()))) {
            ZipEntry entry;
            String name;
            while ((entry = zin.getNextEntry()) != null) {
                name = entry.getName();

                File f = creationFile("C:" + File.separator + File.separator + "Games" + File.separator
                        + "savegames"+ File.separator +name, feedback);

                FileOutputStream fout = new FileOutputStream(f);
                for (int c = zin.read(); c != -1; c = zin.read()) {
                    fout.write(c);
                }
                fout.flush();
                zin.closeEntry();
                fout.close();
            }
            System.out.println("Файл усешно разархивирован");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void deSerialization(String name, GameProgress gameProgress) {
        try (FileInputStream fis = new FileInputStream(name)) {
            ObjectInputStream ois = new ObjectInputStream(fis);
            gameProgress = (GameProgress) ois.readObject();
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
        System.out.println(gameProgress);
    }
}
