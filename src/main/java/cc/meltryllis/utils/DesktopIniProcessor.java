package cc.meltryllis.utils;

import cc.meltryllis.constants.DesktopIniConstants;
import cc.meltryllis.ui.basic.DialogBuilder;
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
            log.error("Set Dos Attribute Failed.", e);
            return false;
        }
    }

    public static String parseEnvironmentVariables(String path) {
        if (path.startsWith("%")) {
            int index = path.indexOf("%", 1);
            if (index > 1) {
                String envKey = path.substring(1, index);
                String envValue = System.getenv(envKey);
                if (!StringUtils.isEmpty(envValue)) {
                    return path.replace(path.substring(0, index + 1), envValue);
                }
            }
        }
        return path;
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

    public Ini getDesktopIni() {
        if (!desktopIniFile.exists()) {
            return null;
        }
        try {
            return new Ini(desktopIniFile);
        } catch (IOException e) {
            return null;
        }
    }

    public boolean storeIni(Ini ini) {
        try {
            setDosAttribute(desktopIniFile.getPath(), false, false);
            Files.deleteIfExists(desktopIniFile.toPath());
        } catch (IOException e) {
            log.info("Delete Failed.", e);
            DialogBuilder.MessageDialogBuilder.builder(e.getMessage())
                    .title(I18nUtil.getString("ui.dialog.generate.fail.title"))
                    .show();
            return false;
        }
        try {
            if (!desktopIniFile.createNewFile()) {
                return false;
            }
            FileOutputStream fileOutputStream = new FileOutputStream(desktopIniFile);
            OutputStreamWriter oStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_16LE);
            oStreamWriter.write(DesktopIniConstants.UTF_16_LE_BOM);
            ini.store(oStreamWriter);
            setDosAttribute(folder.getPath(), true, null);
            setDosAttribute(desktopIniFile.getPath(), true, true);
            fileOutputStream.flush();
            oStreamWriter.flush();
            fileOutputStream.close();
            oStreamWriter.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

}
