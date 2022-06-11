import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Main {

    public static void main(String[] args) {
        File games = new File("C://Games");
        StringBuilder feedback = new StringBuilder();

        creationDirectory(games, feedback);

        File temp = new File("C://Games/temp");
        File file3 = new File(temp,"temp.txt");

        creationDirectory(temp, feedback);
        creationFile(file3, feedback);

        File src = new File("C://Games/src");
        File res = new File("C://Games/res");
        File savegames = new File("C://Games/savegames");

        creationDirectory(src, feedback);
        creationDirectory(res, feedback);
        creationDirectory(savegames, feedback);

        File main = new File("C://Games/src/main");
        File test = new File("C://Games/src/test");

        creationDirectory(main, feedback);
        creationDirectory(test, feedback);

        File file1 = new File(main,"Main.java");
        File file2 = new File(main,"Utils.java");

        creationFile(file1, feedback);
        creationFile(file2, feedback);

        File drawables = new File("C://Games/res/drawables");
        File vectors = new File("C://Games/res/vectors");
        File icons = new File("C://Games/res/icons");

        creationDirectory(drawables, feedback);
        creationDirectory(vectors, feedback);
        creationDirectory(icons, feedback);

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

        File save1 = new File("C://Games/savegames/save1.dat");
        creationFile(save1, feedback);
        saveGame("C://Games/savegames/save1.dat", game1);

        File save2 = new File("C://Games/savegames/save2.dat");
        creationFile(save2, feedback);
        saveGame("C://Games/savegames/save2.dat", game2);

        File save3 = new File("C://Games/savegames/save3.dat");
        creationFile(save3, feedback);
        saveGame("C://Games/savegames/save3.dat", game3);

        List<File> savedVersions = new ArrayList<>();

        savedVersions.add(save1);
        savedVersions.add(save2);
        savedVersions.add(save3);

        File zipedVersions = new File("C://Games/savegames/packed_versions.zip");

        creationFile(zipedVersions, feedback);

        zipFiles(zipedVersions.getPath(), savedVersions);

        delitingFiles(save1);
        delitingFiles(save2);
        delitingFiles(save3);

        unzip(zipedVersions, feedback);

        GameProgress gameDeSerialized = null;

        deSerialization("C://Games/savegames/packed_save1.dat.txt", gameDeSerialized);

    }

    public static void creationDirectory(File directory, StringBuilder feedback) {
        if (directory.mkdir()) {
            feedback.append("Каталог " + directory.getName() + " создан \n");
            System.out.println("Каталог " + directory.getName() + " создан");
        }
    }

    public static void creationFile(File myFile, StringBuilder feedback) {
        try {
            if (myFile.createNewFile()) {
                feedback.append("Файл" + myFile.getName() + " создан \n");
                System.out.println("Файл" + myFile.getName() + " создан");
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void saveGame(String path, GameProgress version) {
        try (FileOutputStream fis = new FileOutputStream(path);
            ObjectOutputStream oos = new ObjectOutputStream(fis)) {
            oos.writeObject(version);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void zipFiles(String path, List<File> files) {
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

    public static void delitingFiles (File file) {
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

                File f = new File("C://Games/savegames/"+name);
                creationFile(f, feedback);

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

    public static void deSerialization(String name, GameProgress gameProgress) {
        try (FileInputStream fis = new FileInputStream(name)) {
            ObjectInputStream ois = new ObjectInputStream(fis);
            gameProgress = (GameProgress) ois.readObject();
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
        System.out.println(gameProgress);
    }
}
