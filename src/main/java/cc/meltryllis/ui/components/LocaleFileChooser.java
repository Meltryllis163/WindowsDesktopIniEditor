package cc.meltryllis.ui.components;

import cc.meltryllis.constants.I18nConstants;
import lombok.Getter;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.util.*;

/**
 * 具有实时语言切换功能的 {@link JFileChooser}。
 *
 * @author Zachary W
 * @date 2024/12/28
 */
public class LocaleFileChooser extends JFileChooser implements LocaleListener {

    private final String approveButtonTextKey;

    @Getter
    private final FileCheckerManager fileCheckerManager;

    private LocaleFileChooser(Builder builder) {
        ResourceBundle bundle = ResourceBundle.getBundle(I18nConstants.BASE_NAME);
        setFileSelectionMode(builder.fileSelectionMode);
        setAcceptAllFileFilterUsed(builder.useAcceptAllFileFilter);
        approveButtonTextKey = builder.approveButtonTextKey;
        setApproveButtonText(bundle.getString(approveButtonTextKey));
        for (FileChecker fileChecker : builder.fileCheckers) {
            addChoosableFileFilter(fileChecker);
        }
        fileCheckerManager = new FileCheckerManager(builder.fileCheckers);
    }

    @Override
    public void localeChanged(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(I18nConstants.BASE_NAME);
        // JFileChooser的国际化，必须更新UI。由于UI的语言区域使用的是JFileChooser.getLocale()，而不是Locale.getDefault()，因此需要先setLocale更新。
        setLocale(locale);
        updateUI();
        setApproveButtonText(bundle.getString(approveButtonTextKey));
    }

    public static class Builder {

        private int fileSelectionMode;
        private boolean useAcceptAllFileFilter;
        private String approveButtonTextKey;
        private final List<FileChecker> fileCheckers;

        private Builder() {
            fileSelectionMode = JFileChooser.FILES_AND_DIRECTORIES;
            useAcceptAllFileFilter = false;
            approveButtonTextKey = "ui.button.ok";
            fileCheckers = new ArrayList<>();
        }

        public static Builder builder() {
            return new Builder();
        }

        public Builder fileSelectionMode(int mode) {
            this.fileSelectionMode = mode;
            return this;
        }

        public Builder useAcceptAllFileFilter(boolean useAcceptAllFileFilter) {
            this.useAcceptAllFileFilter = useAcceptAllFileFilter;
            return this;
        }

        public Builder approveButtonTextKey(String key) {
            this.approveButtonTextKey = key;
            return this;
        }

        public Builder addFileChecker(FileChecker fileChcker) {
            fileCheckers.add(fileChcker);
            return this;
        }

        public LocaleFileChooser build() {
            return new LocaleFileChooser(this);
        }


    }

}
