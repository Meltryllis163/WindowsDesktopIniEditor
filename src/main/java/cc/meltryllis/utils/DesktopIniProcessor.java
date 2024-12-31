package cc.meltryllis.utils;

import cc.meltryllis.constants.DesktopIniConstants;
import com.formdev.flatlaf.util.StringUtils;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.ini4j.Ini;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

/**
 * Desktop.ini文件全流程处理器。
 *
 * @author Zachary W
 * @date 2024/12/31
 */
@NoArgsConstructor
@Log4j2
public class DesktopIniProcessor {

    private File folder;

    private File desktopIniFile;

    public DesktopIniProcessor(String folderPath) throws NoSuchFileException {
        registerFolderPath(folderPath);
    }

    public static boolean setDosAttribute(String filePath, Boolean isSystem, Boolean isHidden) {
        try {
            Path path = Paths.get(filePath);
            if (isSystem != null) {
                Files.setAttribute(path, DesktopIniConstants.DOS_ATTRIBUTE_SYSTEM, isSystem, LinkOption.NOFOLLOW_LINKS);
            }
            if (isHidden != null) {
                Files.setAttribute(path, DesktopIniConstants.DOS_ATTRIBUTE_HIDDEN, isHidden, LinkOption.NOFOLLOW_LINKS);
            }
            return true;
        } catch (IOException e) {
            log.error(e);
            return false;
        }
    }

    public void registerFolderPath(String folderPath) throws NoSuchFileException {
        if (folderPath == null || StringUtils.isEmpty(folderPath)) {
            throw new NoSuchFileException("The string path (" + folderPath + ") does not point to an existing folder.");
        }
        File folder = new File(folderPath);
        if (!folder.exists() || !folder.isDirectory()) {
            throw new NoSuchFileException("The string path (" + folderPath + ") does not point to an existing folder.");
        }
        this.folder = folder;
        desktopIniFile = Paths.get(folder.getAbsolutePath(), DesktopIniConstants.FILE_NAME_DESKTOP_INI)
                .toFile();
    }

    public boolean desktopIniExist() {
        return desktopIniFile.exists();
    }

    public Ini getDesktopIni() {
        if (!desktopIniExist()) {
            return null;
        }
        try {
            return new Ini(desktopIniFile);
        } catch (IOException e) {
            return null;
        }
    }

    public boolean createDesktopIniFile(Ini ini) {
        if (desktopIniFile.exists()) {
            desktopIniFile.delete();
        }
        try {
            if (!desktopIniFile.createNewFile()) {
                return false;
            }
            OutputStreamWriter oStreamWriter = new OutputStreamWriter(new FileOutputStream(desktopIniFile), StandardCharsets.UTF_16LE);
            oStreamWriter.write(DesktopIniConstants.UTF_16_LE_BOM);
            ini.store(oStreamWriter);
            setDosAttribute(folder.getPath(), true, null);
            setDosAttribute(desktopIniFile.getPath(), true, true);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

}
