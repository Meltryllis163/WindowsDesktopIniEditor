package cc.meltryllis.constants;

/**
 * @author Zachary W
 * @date 2024/12/15
 */
public interface DesktopIniConstants {

    String FILE_NAME_DESKTOP_INI = "desktop.ini";

    String FILE_NAME_DESKTOP_INI_BAK = "desktop.ini.mbak";

    /** UTF-16 LE 字节顺序标记。 */
    int UTF_16_LE_BOM = 0xFEFF;

    String SECTION_SHELL_CLASS_INFO = ".ShellClassInfo";

    String KEY_LOCALIZED_RESOURCE_NAME = "LocalizedResourceName";

    String KEY_INFO_TIP = "InfoTip";

    String KEY_ICON_FILE = "IconFile";

    String KEY_ICON_INDEX = "IconIndex";

    String SECTION_ORIGINAL_FOLDER_ATTRIBUTE = "OriginalFolderAttribute";

    String KEY_IS_SYSTEM = "isFolderSystem";

}
