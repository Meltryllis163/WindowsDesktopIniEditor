package cc.meltryllis.ui.components;

import cc.meltryllis.constants.I18nConstants;
import com.formdev.flatlaf.extras.FlatSVGIcon;

import javax.swing.*;
import java.io.File;
import java.util.ResourceBundle;

/**
 * 文件夹过滤器。
 *
 * @author Zachary W
 * @date 2024/12/28
 */
public class FolderFileChecker extends FileChecker {

    @Override
    public boolean accept(File f) {
        return f != null && f.exists() && f.isDirectory();
    }

    @Override
    public String getDescription() {
        ResourceBundle bundle = ResourceBundle.getBundle(I18nConstants.BASE_NAME);
        return bundle.getString("ui.filter.folder");
    }

    @Override
    public Icon getErrorIcon() {
        return new FlatSVGIcon("icons/error.svg");
    }

    @Override
    public String getI18nErrorKey() {
        return "ui.message.folderNotExist";
    }

}
