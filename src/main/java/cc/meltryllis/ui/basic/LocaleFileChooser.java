package cc.meltryllis.ui.basic;

import cc.meltryllis.ui.event.LocaleListener;
import cc.meltryllis.utils.I18nUtil;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * 具有实时语言切换功能的 {@link JFileChooser}。
 *
 * @author Zachary W
 * @date 2024/12/28
 */
public class LocaleFileChooser extends JFileChooser implements LocaleListener {

    private final String approveButtonTextKey;

    private LocaleFileChooser(Builder builder) {

        setFileSelectionMode(builder.fileSelectionMode);
        setAcceptAllFileFilterUsed(builder.useAcceptAllFileFilter);
        approveButtonTextKey = builder.approveButtonTextKey;
        setApproveButtonText(I18nUtil.getString(approveButtonTextKey));
        for (FileFilter fileFilter : builder.fileFilters) {
            addChoosableFileFilter(fileFilter);
        }
    }

    @Override
    public void localeChanged(Locale locale) {

        // JFileChooser的国际化，必须更新UI。由于UI的语言区域使用的是JFileChooser.getLocale()，而不是Locale.getDefault()，因此需要先setLocale更新。
        setLocale(locale);
        updateUI();
        setApproveButtonText(I18nUtil.getString(approveButtonTextKey));
    }

    public static class Builder {

        private int fileSelectionMode;
        private boolean useAcceptAllFileFilter;
        private String approveButtonTextKey;
        private final List<FileFilter> fileFilters;

        private Builder() {
            fileSelectionMode = JFileChooser.FILES_AND_DIRECTORIES;
            useAcceptAllFileFilter = false;
            approveButtonTextKey = "ui.button.ok";
            fileFilters = new ArrayList<>();
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

        public Builder addChoosableFileFilter(FileFilter fileFilter) {
            fileFilters.add(fileFilter);
            return this;
        }

        public Builder addChoosableFileFilter(String description, String... extensions) {
            fileFilters.add(new FileNameExtensionFilter(description, extensions));
            return this;
        }

        public LocaleFileChooser build() {
            return new LocaleFileChooser(this);
        }

    }

}
