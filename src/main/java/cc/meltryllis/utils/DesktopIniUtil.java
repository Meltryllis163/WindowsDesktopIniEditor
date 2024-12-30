package cc.meltryllis.utils;

import cc.meltryllis.constants.DesktopIniConstants;
import cc.meltryllis.entity.DesktopIniEntity;
import lombok.extern.log4j.Log4j2;
import org.ini4j.Ini;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

@Log4j2
public class DesktopIniUtil {

    public static String getDesktopIniPath(String folderPath) {
        return Paths.get(folderPath, DesktopIniConstants.FILE_NAME_DESKTOP_INI).toString();
    }

    public static String getBakDesktopIniPath(String folderPath) {
        return Paths.get(folderPath, DesktopIniConstants.FILE_NAME_DESKTOP_INI_BAK).toString();
    }

    public static boolean setDosAttribute(String filePath, Boolean isSystem, Boolean isHidden) {
        try {
            Path path = Paths.get(filePath);
            if (isSystem != null) {
                Files.setAttribute(path, "dos:system", isSystem, LinkOption.NOFOLLOW_LINKS);
            }
            if (isHidden != null) {
                Files.setAttribute(path, "dos:hidden", isHidden, LinkOption.NOFOLLOW_LINKS);
            }
            return true;
        } catch (IOException e) {
            log.error(e);
            return false;
        }
    }

    public static boolean thirdPartyIniExist(String folderPath) {
        File iniFile = new File(getDesktopIniPath(folderPath));
        if (!iniFile.exists()) {
            return false;
        }
        try {
            return new Ini(iniFile).get(DesktopIniConstants.SECTION_ORIGINAL_FOLDER_ATTRIBUTE, DesktopIniConstants.KEY_IS_SYSTEM) == null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean createDesktopIni(String folderPath, Ini ini) {
        File folder = new File(folderPath);
        if (!folder.exists() || !folder.isDirectory()) {
            return false;
        }
        File iniFile = new File(getDesktopIniPath(folderPath));
        if (iniFile.exists()) {
            iniFile.delete();
        }
        try {
            iniFile.createNewFile();
            OutputStreamWriter oStreamWriter = new OutputStreamWriter(new FileOutputStream(iniFile), StandardCharsets.UTF_16LE);
            oStreamWriter.write(DesktopIniConstants.UTF_16_LE_BOM);
            ini.store(oStreamWriter);
            setDosAttribute(folder.getPath(), true, null);
            setDosAttribute(iniFile.getPath(), true, true);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public static boolean backupThirdPartyDesktopIni(String folderPath) {
        File iniFile = new File(getDesktopIniPath(folderPath));
        File bakFile = new File(iniFile.getParent() + File.separator + DesktopIniConstants.FILE_NAME_DESKTOP_INI_BAK);
        // TODO 备份文件冲突时需要处理
        //  当前策略：如果不存在旧备份，就备份一次。如果已经存在，直接放弃备份。
        if (!bakFile.exists()) {
            return iniFile.renameTo(bakFile);
        }
        // 不存在备份文件，返回true默认备份任务完成
        return true;
    }

    public static boolean getOriginalFolderSystemAttribute(File iniFile) throws IOException {
        String isSystemStr = new Ini(iniFile).get(DesktopIniConstants.SECTION_ORIGINAL_FOLDER_ATTRIBUTE, DesktopIniConstants.KEY_IS_SYSTEM);
        return Boolean.parseBoolean(isSystemStr);
    }

    /**
     * 处理当前文件夹下的旧desktop.ini文件。
     * 如果该文件由本程序创建，则删除。否则重命名为desktop.ini.bak。
     *
     * @param iniFile desktop.ini文件
     * @return 当前文件夹的“系统”属性。
     */
    private static boolean handleOriginalDesktopIni(File iniFile) {
        if (!iniFile.exists()) {
            return isFolderSystem(iniFile.getParent());
        }
        try {
            Boolean originalFolderSystem = getOriginalFolderSystemAttribute(iniFile);
            // 如果是由本程序创建的desktop.ini
            if (originalFolderSystem != null) {
                iniFile.delete();
                return originalFolderSystem;
            } else {
                File bakFile = new File(iniFile.getParent() + File.separator + DesktopIniConstants.FILE_NAME_DESKTOP_INI_BAK);
                // TODO 备份文件冲突时需要处理
                if (!bakFile.exists()) {
                    iniFile.renameTo(bakFile);
                }
                return isFolderSystem(iniFile.getParent());
            }
        } catch (IOException e) {
            log.error(e);
            return false;
        }
    }

    public static void restoreOriginalDesktopIni(String folderPath) {
        File folder = new File(folderPath);
        File iniFile = new File(getDesktopIniPath(folder.getPath()));
        // 检测当前Ini是否由本程序创建
        try {
            Boolean originalFolderSystem = getOriginalFolderSystemAttribute(iniFile);
            // 如果desktop.ini是由本程序创建
            if (originalFolderSystem != null) {
                iniFile.delete();
                // 恢复desktop.ini.bak
                File bakFile = new File(getBakDesktopIniPath(folder.getPath()));
                if (bakFile.exists()) {
                    bakFile.renameTo(iniFile);
                }
                // 还原文件夹原本的系统属性
                setDosAttribute(folder.getPath(), originalFolderSystem, null);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    public static boolean isFolderSystem(String folderPath) {
        try {
            Object isSystem = Files.getAttribute(Paths.get(folderPath), "dos:system", LinkOption.NOFOLLOW_LINKS);
            return isSystem instanceof Boolean && (boolean) isSystem;
        } catch (IOException e) {
            log.error(e);
            return false;
        }
    }

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {

        boolean created = true;
        String folderPath = "C:/Users/Meltryllis/Desktop/123 + 123 TTT/Test";
        if (created) {
            DesktopIniEntity desktopIniEntity = DesktopIniEntity.Builder.builder().infoTip("TestInfoTip" + new Random().nextInt(1000)).build();
            Ini ini = desktopIniEntity.convertToIni();
            createDesktopIni(folderPath, ini);
            System.out.println("Current Folder System attr : " + isFolderSystem(folderPath));
        } else {
            restoreOriginalDesktopIni(folderPath);
        }

    }

}
