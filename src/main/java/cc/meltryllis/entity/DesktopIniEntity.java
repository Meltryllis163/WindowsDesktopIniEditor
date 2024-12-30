package cc.meltryllis.entity;

import cc.meltryllis.constants.DesktopIniConstants;
import lombok.Setter;
import lombok.ToString;
import org.ini4j.Ini;

@Setter
@ToString
public class DesktopIniEntity {

    /** 所在文件夹路径 */
    private String folderPathString;
    /** 别名 */
    private String localizedResourceName;
    /** 备注 */
    private String infoTip;
    /** 图标文件路径 */
    private String iconFile;
    /** 图标文件索引 */
    private Integer iconIndex;
    /** 文件原本的System属性 */
    private Boolean isFolderSystem;

    private DesktopIniEntity(Builder builder) {
        setInfoTip(builder.tip);
        setIcon(builder.iconFile, builder.iconIndex);
        setLocalizedResourceName(builder.localizedResourceName);
        setIsFolderSystem(builder.isFolderSystem);
    }

    public void setIcon(String iconFile) {
        setIcon(iconFile, 0);
    }

    public void setIcon(String iconFile, int iconIndex) {
        setIconFile(iconFile);
        setIconIndex(iconIndex);
    }

    public Ini convertToIni() {
        Ini iniFile = new Ini();
        iniFile.add(DesktopIniConstants.SECTION_SHELL_CLASS_INFO, DesktopIniConstants.KEY_LOCALIZED_RESOURCE_NAME, localizedResourceName);
        iniFile.add(DesktopIniConstants.SECTION_SHELL_CLASS_INFO, DesktopIniConstants.KEY_INFO_TIP, infoTip);
        iniFile.add(DesktopIniConstants.SECTION_SHELL_CLASS_INFO, DesktopIniConstants.KEY_ICON_FILE, iconFile);
        iniFile.add(DesktopIniConstants.SECTION_SHELL_CLASS_INFO, DesktopIniConstants.KEY_ICON_INDEX, iconIndex);
        iniFile.add(DesktopIniConstants.SECTION_ORIGINAL_FOLDER_ATTRIBUTE, DesktopIniConstants.KEY_IS_SYSTEM, isFolderSystem);
        return iniFile;
    }

    public static class Builder {

        private String localizedResourceName;
        private String tip;
        private String iconFile;
        private int iconIndex;
        private boolean isFolderSystem;

        public static Builder builder() {
            return new Builder();
        }

        public Builder localizedResourceName(String name) {
            this.localizedResourceName = name;
            return this;
        }

        public Builder infoTip(String tip) {
            this.tip = tip;
            return this;
        }

        public Builder icon(String iconFile) {
            return icon(iconFile, 0);
        }

        public Builder icon(String iconFile, int iconIndex) {
            this.iconFile = iconFile;
            this.iconIndex = iconIndex;
            return this;
        }

        public Builder folderSystem(boolean isSystem) {
            this.isFolderSystem = isSystem;
            return this;
        }

        // TODO null检测
        public DesktopIniEntity build() {
            return new DesktopIniEntity(this);
        }

    }

}
