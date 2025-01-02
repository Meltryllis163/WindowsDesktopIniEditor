package cc.meltryllis.ui.basic;

import cc.meltryllis.constants.I18nConstants;

import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.ResourceBundle;

/**
 * 过滤文件夹。
 *
 * @author Zachary W
 * @date 2024/12/31
 */
public class FolderFileFilter extends FileFilter {

    @Override
    public boolean accept(File f) {
        return f != null && f.exists() && f.isDirectory();
    }

    @Override
    public String getDescription() {
        ResourceBundle bundle = ResourceBundle.getBundle(I18nConstants.BASE_NAME);
        return bundle.getString("ui.label.folder");
    }

}
