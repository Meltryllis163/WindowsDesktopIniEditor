package cc.meltryllis.ui;

import cc.meltryllis.ui.basic.IconFileFilter;
import cc.meltryllis.ui.basic.LocaleFieldFileChooser;
import cc.meltryllis.ui.basic.LocaleFileChooser;
import cc.meltryllis.ui.basic.LocaleLabel;
import cc.meltryllis.ui.event.LocaleListener;
import cc.meltryllis.utils.I18nUtil;
import com.formdev.flatlaf.util.StringUtils;
import net.miginfocom.layout.CC;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.Locale;

/**
 * 含有图标和图标索引输入框的面板。
 *
 * @author Zachary W
 * @date 2025/1/3
 */
public class IconChooserPanel extends JPanel implements DocumentListener, LocaleListener {

    public static final String DEFAULT_INDEX = "0";

    private LocaleFieldFileChooser fieldIconFile;
    private JTextField fieldIconIndex;

    public IconChooserPanel(String fileI18nKey, String indexI18nKey) {
        MigLayout layout = new MigLayout("ins 0", "[80%, fill, grow][20%, fill, grow]", "top");
        setLayout(layout);
        initComponents(fileI18nKey, indexI18nKey);
    }

    public void initComponents(String fileI18nKey, String indexI18nKey) {
        int row = 0, column = 0;
        LocaleLabel labelIconFile = new LocaleLabel(fileI18nKey);
        add(labelIconFile, new CC().cell(column, row));
        LocaleLabel labelIconIndex = new LocaleLabel(indexI18nKey);
        add(labelIconIndex, new CC().cell(column + 1, row));

        row++;
        LocaleFileChooser iconChooser = LocaleFileChooser.Builder.builder()
                .fileSelectionMode(JFileChooser.FILES_ONLY)
                .addChoosableFileFilter(new IconFileFilter())
                .build();
        fieldIconFile = new LocaleFieldFileChooser(iconChooser, "ui.fileChooser.icon.tip");
        fieldIconFile.addDocumentListener(this);
        add(fieldIconFile, new CC().cell(column, row));

        fieldIconIndex = new JTextField(DEFAULT_INDEX);
        fieldIconIndex.setEnabled(false);
        fieldIconIndex.setToolTipText(I18nUtil.getString("ui.field.iconIndex.tooltip"));
        add(fieldIconIndex, new CC().cell(column + 1, row));
    }

    private boolean notDllFile() {
        String text = fieldIconFile.getText();
        if (!StringUtils.isEmpty(text)) {
            int index = text.lastIndexOf('.');
            return index < 0 || !".dll".equalsIgnoreCase(text.substring(index));
        }
        return true;
    }

    public String getIconPath() {
        return fieldIconFile.getText();
    }

    public void setIconPath(String path) {
        fieldIconFile.setText(path);
    }

    public String getIconIndex() {
        return fieldIconIndex.getText();
    }

    public void setIconIndex(String index) {
        fieldIconIndex.setText(index);
    }

    public int getValidateResult() {
        return fieldIconFile.getValidateResult();
    }

    public void clearText() {
        setIconPath(null);
        setIconIndex(DEFAULT_INDEX);
    }

    private void updateIconIndexEnabled() {
        if (notDllFile()) {
            fieldIconIndex.setEnabled(false);
            fieldIconIndex.setText(DEFAULT_INDEX);
        } else {
            fieldIconIndex.setEnabled(true);
        }
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        updateIconIndexEnabled();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        updateIconIndexEnabled();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {

    }

    @Override
    public void localeChanged(Locale locale) {
        for (Component component : getComponents()) {
            if (component instanceof LocaleListener) {
                ((LocaleListener) component).localeChanged(locale);
            }
        }
        fieldIconIndex.setToolTipText(I18nUtil.getString("ui.field.iconIndex.tooltip"));
    }

}
