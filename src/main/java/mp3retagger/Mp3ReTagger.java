package mp3retagger;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.ID3v23Tag;
import com.mpatric.mp3agic.Mp3File;

import java.io.File;
import java.util.Objects;

/**
 * Updates ID3v2 artist/title tags based on filename.
 */
public class Mp3ReTagger {

    public static final String RETAGGED = ".retagged";

    public static void main(String[] args) throws Exception {
        for (String arg : args) {
            reTagFile(arg);
        }
    }

    static void reTagFile(String filePath) throws Exception {

        File file = new File(filePath);
        if (file.isDirectory()) {
            System.out.println("File is a directory");
            return;
        }

        Mp3File mp3File = new Mp3File(filePath);
        ID3v23Tag guessedId3Tag = guessArtistAndTitle(file);

        if (guessedId3Tag != null) {
            if (!needsReTagging(mp3File, guessedId3Tag)) {
                System.out.println("ID3 is up-to-date");
                return;
            }

            mp3File.removeId3v1Tag();
            mp3File.setId3v2Tag(guessedId3Tag);

            String tmpFileName = filePath + RETAGGED;
            mp3File.save(tmpFileName);

            if (new File(tmpFileName).renameTo(file)) {
                System.out.println("Successfully re-tagged " + file.getName());
            } else {
                System.out.println("Couldn't move re-tagged file back to it's original place");
            }
        } else {
            System.out.println("Couldn't guess artist/title for " + file.getName());
        }
    }

    static boolean needsReTagging(Mp3File mp3File, ID3v23Tag guessedId3Tag) {
        return isId3TagIncomplete(mp3File.getId3v2Tag()) || guessVsTagMismatch(mp3File, guessedId3Tag);
    }

    static boolean isId3TagIncomplete(ID3v1 id3v1Tag) {
        return id3v1Tag == null || isNullOrEmpty(id3v1Tag.getArtist()) || isNullOrEmpty(id3v1Tag.getTitle());
    }

    static boolean guessVsTagMismatch(Mp3File mp3File, ID3v23Tag guessedId3Tag) {
        ID3v2 originalId3Tag = mp3File.getId3v2Tag();
        if (!Objects.equals(originalId3Tag.getArtist(), guessedId3Tag.getArtist())) return true;
        if (!Objects.equals(originalId3Tag.getTitle(), guessedId3Tag.getTitle())) return true;
        return false;
    }

    static boolean isNullOrEmpty(String s) {
        return s == null || s.length() == 0;
    }

    static ID3v23Tag guessArtistAndTitle(File file) {
        String name = file.getName();
        name = name.substring(0, name.length() - 4);

        String[] parts = name.split(" *- *", 2);

        if (parts.length == 2) {
            String artist = parts[0];
            String title = parts[1];

            System.out.println("Guessed artist: " + artist + "  title: " + title);

            ID3v23Tag id3v23Tag = new ID3v23Tag();
            id3v23Tag.setArtist(artist);
            id3v23Tag.setTitle(title);

            return id3v23Tag;
        }

        return null;
    }
}
