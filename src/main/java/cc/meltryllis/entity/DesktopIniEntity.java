package cc.meltryllis.entity;

import cc.meltryllis.constants.DesktopIniConstants;
import com.formdev.flatlaf.util.StringUtils;
import lombok.ToString;
import org.ini4j.Ini;

/**
 * @author Zachary W
 * @date 2024/12/30
 */
@ToString
public class DesktopIniEntity {

    private final String DEFAULT_ICON_INDEX = "0";

    /** 别名 */
    private final String localizedResourceName;
    /** 备注 */
    private final String infoTip;
    /** 图标资源文件路径 */
    private String iconResourceFile;
    /** 图标资源文件索引 */
    private String iconResourceIndex;
    /** 图标文件路径 */
    private String iconFile;
    /** 图标文件索引 */
    private String iconIndex;

    private DesktopIniEntity(Builder builder) {
        this.infoTip = builder.infoTip;
        this.localizedResourceName = builder.localizedResourceName;
        setIcon(builder.iconFile, builder.iconIndex);
        setIconResource(builder.iconResourceFile, builder.iconResourceIndex);
    }

    private void setIcon(String iconFile) {
        setIcon(iconFile, DEFAULT_ICON_INDEX);
    }

    private void setIcon(String iconFile, String iconIndex) {
        this.iconFile = iconFile;
        this.iconIndex = iconIndex;
    }

    private void setIconResource(String iconResourceFile, String iconResourceIndex) {
        this.iconResourceFile = iconResourceFile;
        this.iconResourceIndex = iconResourceIndex;
    }

    private String getIconResource() {
        String iconResourceIndex = StringUtils.isEmpty(iconIndex) ? "" : ", " + this.iconResourceIndex;
        return StringUtils.isEmpty(iconResourceFile) ? null : iconResourceFile + iconResourceIndex;
    }

    private void setIconResource(String iconResourceFile) {
        setIconResource(iconResourceFile, null);
    }

    public Ini convertToIni() {
        Ini iniFile = new Ini();
        iniFile.add(DesktopIniConstants.SECTION_SHELL_CLASS_INFO, DesktopIniConstants.KEY_LOCALIZED_RESOURCE_NAME, localizedResourceName);
        iniFile.add(DesktopIniConstants.SECTION_SHELL_CLASS_INFO, DesktopIniConstants.KEY_INFO_TIP, infoTip);
        iniFile.add(DesktopIniConstants.SECTION_SHELL_CLASS_INFO, DesktopIniConstants.KEY_ICON_RESOURCE,
                getIconResource());
        iniFile.add(DesktopIniConstants.SECTION_SHELL_CLASS_INFO, DesktopIniConstants.KEY_ICON_FILE, iconFile);
        iniFile.add(DesktopIniConstants.SECTION_SHELL_CLASS_INFO, DesktopIniConstants.KEY_ICON_INDEX, iconIndex);
        return iniFile;
    }

    public static class Builder {

        private String localizedResourceName;
        private String infoTip;
        private String iconResourceFile;
        private String iconResourceIndex;
        private String iconFile;
        private String iconIndex;

        public static Builder builder() {
            return new Builder();
        }

        private static String emptyToNull(String str) {
            if (str == null) {
                return null;
            }
            if (str.isEmpty() || str.trim().isEmpty()) {
                return null;
            }
            return str;
        }

        public Builder localizedResourceName(String name) {
            this.localizedResourceName = emptyToNull(name);
            return this;
        }

        public Builder infoTip(String infoTip) {
            this.infoTip = emptyToNull(infoTip);
            return this;
        }

        public Builder iconResource(String iconResourceFile) {
            iconResource(iconResourceFile, iconResourceIndex);
            return this;
        }

        public Builder icon(String iconFile) {
            return icon(iconFile, null);
        }

        public Builder iconResource(String iconResourceFile, String iconResourceIndex) {
            this.iconResourceFile = emptyToNull(iconResourceFile);
            this.iconResourceIndex = StringUtils.isEmpty(this.iconResourceFile) ? null : iconResourceIndex;
            return this;
        }

        // TODO null检测
        public DesktopIniEntity build() {
            return new DesktopIniEntity(this);
        }

        public Builder icon(String iconFile, String iconIndex) {
            this.iconFile = emptyToNull(iconFile);
            this.iconIndex = StringUtils.isEmpty(this.iconFile) ? null : iconIndex;
            return this;
        }

    }

}
