package cc.meltryllis.ui.basic;

import cc.meltryllis.utils.I18nUtil;

import javax.swing.filechooser.FileFilter;
import java.io.File;


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

        return I18nUtil.getString("ui.label.folder");
    }

}
