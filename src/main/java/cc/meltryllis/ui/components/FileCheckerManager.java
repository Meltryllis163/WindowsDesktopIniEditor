package cc.meltryllis.ui.components;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 管理以及检查 {@link javax.swing.filechooser.FileFilter} 规则。
 *
 * @author Zachary W
 * @date 2024/12/29
 */
public class FileCheckerManager extends FileChecker {

    public static final Icon PASS_ICON = new FlatSVGIcon("icons/pass.svg");
    private static final String PASS_I18N_KEY = "ui.defaultMessage.pathValid";

    private final List<FileChecker> fileCheckers;
    private FileChecker firstUnacceptedChecker;

    public FileCheckerManager() {
        this(new ArrayList<>());
    }

    public FileCheckerManager(List<FileChecker> fileCheckers) {
        this.fileCheckers = fileCheckers;
    }

    @Override
    public Icon getErrorIcon() {
        if (firstUnacceptedChecker == null) {
            return PASS_ICON;
        }
        return firstUnacceptedChecker.getErrorIcon();
    }

    @Override
    public String getI18nErrorKey() {
        if (firstUnacceptedChecker == null) {
            return PASS_I18N_KEY;
        }
        return firstUnacceptedChecker.getI18nErrorKey();
    }

    @Override
    public boolean accept(File f) {
        firstUnacceptedChecker = null;
        for (FileChecker fileChecker : fileCheckers) {
            if (!fileChecker.accept(f)) {
                firstUnacceptedChecker = fileChecker;
                return false;
            }
        }
        return true;
    }

    @Override
    public String getDescription() {
        return "";
    }

}
