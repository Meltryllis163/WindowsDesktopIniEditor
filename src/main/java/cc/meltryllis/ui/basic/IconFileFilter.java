package cc.meltryllis.ui.basic;

import cc.meltryllis.utils.I18nUtil;

import javax.swing.filechooser.FileFilter;
import java.io.File;


/**
 * @author Zachary W
 * @date 2025/1/2
 */
public class IconFileFilter extends FileFilter {

    @Override
    public boolean accept(File f) {
        if (f == null || !f.exists()) {
            return false;
        }
        if (f.isDirectory()) {
            return true;
        } else {
            String filePath = f.getPath();
            String extension = filePath.substring(filePath.lastIndexOf('.') + 1);
            return "exe".equalsIgnoreCase(extension)
                    || "bmp".equalsIgnoreCase(extension)
                    || "ico".equalsIgnoreCase(extension)
                    || "dll".equalsIgnoreCase(extension);
        }
    }

    @Override
    public String getDescription() {

        return I18nUtil.getString("ui.fileChooser.iconFile.description");
    }

}
