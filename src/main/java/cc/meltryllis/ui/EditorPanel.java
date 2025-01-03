package cc.meltryllis.ui;

import cc.meltryllis.constants.DesktopIniConstants;
import cc.meltryllis.constants.I18nConstants;
import cc.meltryllis.entity.DesktopIniEntity;
import cc.meltryllis.ui.basic.*;
import cc.meltryllis.ui.event.CustomEventManager;
import cc.meltryllis.ui.event.FolderChangeListener;
import cc.meltryllis.ui.event.LocaleListener;
import cc.meltryllis.utils.DesktopIniProcessor;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.util.StringUtils;
import lombok.extern.log4j.Log4j2;
import net.miginfocom.layout.CC;
import net.miginfocom.swing.MigLayout;
import org.ini4j.Ini;
import org.ini4j.Profile;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.nio.file.NoSuchFileException;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * desktop.ini 编辑器主面板。
 *
 * @author Zachary W
 * @date 2024/12/25
 */
@Log4j2
public class EditorPanel extends JPanel implements LocaleListener, FolderChangeListener {

    private LocaleFileChooserField fieldFolderPath;
    private JTextField fieldLocalizedResourceName;
    private JTextField fieldInfoTip;
    private LocaleFileChooserField fieldIconResourceFile;
    private JTextField fieldIconResourceIndex;
    private LocaleFileChooserField fieldIconFile;
    private JTextField fieldIconIndex;
    private JButton buttonGenerate;

    private DesktopIniProcessor processor;

    public EditorPanel() {
        MigLayout layout = new MigLayout("ins 20", "[fill, grow, 80%][fill, push, 20%]", "top");
        setLayout(layout);
        initComponents();
        CustomEventManager.getInstance().addFolderChangeListener(this);
    }

    public void initComponents() {

        ResourceBundle bundle = ResourceBundle.getBundle(I18nConstants.BASE_NAME);

        int row = 0, column = 0;
        LocaleLabel titleLabel = new LocaleLabel("ui.title");
        titleLabel.putClientProperty(FlatClientProperties.STYLE, "font: 115%");
        add(titleLabel, new CC().spanX(2));

        row++;
        add(new JSeparator(), new CC().cell(column, row).spanX(2));

        row++;
        LocaleLabel labelFolderPath = new LocaleLabel("ui.label.folder");
        add(labelFolderPath, new CC().cell(column, row).spanX(2));
        row++;
        LocaleFileChooser folderChooser = LocaleFileChooser.Builder.builder()
                .fileSelectionMode(JFileChooser.DIRECTORIES_ONLY).addChoosableFileFilter(new FolderFileFilter())
                .approveButtonTextKey("ui.button.ok").build();
        fieldFolderPath = new LocaleFileChooserField(folderChooser, "ui.fileChooser.folder");
        fieldFolderPath.addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                log.info("Folder text changed. Fire FolderChanged event.");
                CustomEventManager.getInstance().fireFolderChanged();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                log.info("Folder text changed. Fire FolderChanged event.");
                CustomEventManager.getInstance().fireFolderChanged();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                CustomEventManager.getInstance().fireFolderChanged();
            }
        });
        add(fieldFolderPath, new CC().cell(column, row).spanX(2));

        row++;
        LocaleLabel labelLocalizedResourceName = new LocaleLabel("ui.label.localizedResourceName");
        add(labelLocalizedResourceName, new CC().cell(column, row).spanX(2));
        row++;
        fieldLocalizedResourceName = new JTextField();
        add(fieldLocalizedResourceName, new CC().cell(column, row).spanX(2));

        row++;
        LocaleLabel labelInfoTip = new LocaleLabel("ui.label.infoTip");
        add(labelInfoTip, new CC().cell(column, row).spanX(2));
        row++;
        fieldInfoTip = new JTextField();
        add(fieldInfoTip, new CC().cell(column, row).spanX(2));

        row++;
        LocaleLabel labelIconResourceFile = new LocaleLabel("ui.label.iconResourceFile");
        add(labelIconResourceFile, new CC().cell(column, row));
        LocaleLabel labelIconResourceIndex = new LocaleLabel("ui.label.iconResourceIndex");
        add(labelIconResourceIndex, new CC().cell(column + 1, row));
        row++;
        LocaleFileChooser iconResourceChooser = LocaleFileChooser.Builder.builder()
                .fileSelectionMode(JFileChooser.FILES_ONLY).addChoosableFileFilter(new IconFileFilter()).build();
        fieldIconResourceFile = new LocaleFileChooserField(iconResourceChooser, "ui.fileChooser.icon");
        fieldIconResourceFile.addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                String text = fieldIconResourceFile.getText();
                if (!StringUtils.isEmpty(text)) {
                    int index = text.lastIndexOf('.');
                    if (index >= 0) {
                        String extension = text.substring(index);
                        if (".dll".equalsIgnoreCase(extension)) {
                            fieldIconResourceIndex.setEnabled(true);
                            return;
                        }
                    }
                }
                SwingUtilities.invokeLater(() -> {
                    fieldIconResourceIndex.setText("0");
                    fieldIconResourceIndex.setEnabled(false);
                });
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                String text = fieldIconResourceFile.getText();
                if (!StringUtils.isEmpty(text)) {
                    int index = text.lastIndexOf('.');
                    if (index >= 0) {
                        String extension = text.substring(index);
                        if (".dll".equalsIgnoreCase(extension)) {
                            SwingUtilities.invokeLater(() -> fieldIconResourceIndex.setEnabled(true));
                            return;
                        }
                    }
                }
                SwingUtilities.invokeLater(() -> {
                    fieldIconResourceIndex.setText("0");
                    fieldIconResourceIndex.setEnabled(false);
                });
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });
        add(fieldIconResourceFile, new CC().cell(column, row));
        fieldIconResourceIndex = new JTextField("0");
        fieldIconResourceIndex.setEnabled(false);
        add(fieldIconResourceIndex, new CC().cell(column + 1, row));

        row++;
        LocaleLabel labelIconFile = new LocaleLabel("ui.label.iconFile");
        add(labelIconFile, new CC().cell(column, row));
        LocaleLabel labelIconIndex = new LocaleLabel("ui.label.iconIndex");
        add(labelIconIndex, new CC().cell(column + 1, row));

        row++;
        LocaleFileChooser iconChooser = LocaleFileChooser.Builder.builder().fileSelectionMode(JFileChooser.FILES_ONLY)
                .addChoosableFileFilter("图标文件 (*.ico; *.bmp; *.exe; *.dll)", "ico", "bmp", "exe", "dll").build();
        fieldIconFile = new LocaleFileChooserField(iconChooser, "ui.fileChooser.icon");
        fieldIconFile.addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                String text = fieldIconFile.getText();
                if (!StringUtils.isEmpty(text)) {
                    int index = text.lastIndexOf('.');
                    if (index >= 0) {
                        String extension = text.substring(index);
                        if (".dll".equalsIgnoreCase(extension)) {
                            SwingUtilities.invokeLater(() -> fieldIconIndex.setEnabled(true));
                            return;
                        }
                    }
                }
                SwingUtilities.invokeLater(() -> {
                    fieldIconIndex.setEnabled(false);
                    fieldIconIndex.setText("0");
                });
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                String text = fieldIconFile.getText();
                if (!StringUtils.isEmpty(text)) {
                    int index = text.lastIndexOf('.');
                    if (index >= 0) {
                        String extension = text.substring(index);
                        if (".dll".equalsIgnoreCase(extension)) {
                            SwingUtilities.invokeLater(() -> fieldIconIndex.setEnabled(true));
                            return;
                        }
                    }
                }
                SwingUtilities.invokeLater(() -> {
                    fieldIconIndex.setEnabled(false);
                    fieldIconIndex.setText("0");
                });
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });

        add(fieldIconFile, new CC().cell(column, row));
        fieldIconIndex = new JTextField("0");
        fieldIconIndex.setEnabled(false);
        add(fieldIconIndex, new CC().cell(column + 1, row));

        row++;
        buttonGenerate = new JButton(bundle.getString("ui.button.generate"));
        buttonGenerate.addActionListener(e -> {
            if (generateDesktopIni()) {
                JOptionPane.showMessageDialog(EditorPanel.this, bundle.getString("ui.message.generateSuccess"));
            }
        });
        add(buttonGenerate, new CC().cell(column, row).grow(0).spanX(2));
    }

    public boolean generateDesktopIni() {
        // TODO 这个方法有点混乱
        //  null判断感觉到处都是，最好梳理一次
        ResourceBundle bundle = ResourceBundle.getBundle(I18nConstants.BASE_NAME);
        String folderPath = fieldFolderPath.getText();
        if (StringUtils.isEmpty(folderPath)) {
            JOptionPane.showMessageDialog(MainApplication.app, bundle.getString("ui.message.noFolderSelected"));
            return false;
        }
        if (fieldFolderPath.getValidateResult() == LocaleFileChooserField.INVALID) {
            JOptionPane.showMessageDialog(MainApplication.app, bundle.getString("ui.message.folderInvalid"));
            return false;
        }
        String iconPath = fieldIconResourceFile.getText();
        if (!StringUtils.isEmpty(iconPath) && fieldIconResourceFile.getValidateResult() == LocaleFileChooserField.INVALID) {
            JOptionPane.showMessageDialog(MainApplication.app, bundle.getString("ui.message.iconInvalid"));
            return false;
        }
        try {
            processor = new DesktopIniProcessor(folderPath);
        } catch (NoSuchFileException e) {
            log.error(e);
            return false;
        }
        DesktopIniEntity entity = generateDesktopIniEntity();
        return processor.createDesktopIniFile(entity.convertToIni());
    }

    public void loadDesktopIniContent() {
        try {
            if (processor == null) {
                processor = new DesktopIniProcessor(fieldFolderPath.getText());
            } else {
                processor.registerFolderPath(fieldFolderPath.getText());
            }
        } catch (NoSuchFileException e) {
            return;
        }
        log.info("Start loading desktop.ini content, current path : {}", fieldFolderPath.getText());
        Ini desktopIni = processor.getDesktopIni();
        Profile.Section section =
                desktopIni == null ? null : desktopIni.get(DesktopIniConstants.SECTION_SHELL_CLASS_INFO);
        if (section != null) {
            if (containDllSource(desktopIni)) {
                ResourceBundle bundle = ResourceBundle.getBundle(I18nConstants.BASE_NAME);
                Object[] options = new Object[]{bundle.getString("ui.dialog.dllWarning.option.continue"), bundle.getString("ui.dialog.dllWarning.option.cancel")};
                int res = JOptionPane.showOptionDialog(this, bundle.getString("ui.dialog.dllWarning.message"), bundle.getString("ui.dialog.dllWarning.title"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, JOptionPane.NO_OPTION);
                log.info("DLL file detected. User choose {}.", res == JOptionPane.YES_OPTION ? "continue" : "cancel");
                if (res != JOptionPane.YES_OPTION) {
                    // TODO 这个地方会出现set以后立刻清除的一个闪烁
                    SwingUtilities.invokeLater(() -> fieldFolderPath.setText(null));
                    return;
                }
            }
            fieldInfoTip.setText(section.get(DesktopIniConstants.KEY_INFO_TIP));
            fieldLocalizedResourceName.setText(section.get(DesktopIniConstants.KEY_LOCALIZED_RESOURCE_NAME));
            String iconResource = section.get(DesktopIniConstants.KEY_ICON_RESOURCE);
            if (!StringUtils.isEmpty(iconResource)) {
                int index = iconResource.lastIndexOf(',');
                if (index > 0) {
                    fieldIconResourceFile.setText(iconResource.substring(0, index));
                    fieldIconResourceIndex.setText(iconResource.substring(index + 1).trim());
                }
            }
            fieldIconFile.setText(section.get(DesktopIniConstants.KEY_ICON_FILE));
            fieldIconIndex.setText(section.get(DesktopIniConstants.KEY_ICON_INDEX));
        } else {
            log.info("No desktop.ini exist. Clear all text.");
            fieldInfoTip.setText(null);
            fieldLocalizedResourceName.setText(null);
            fieldIconResourceFile.setText(null);
        }
    }

    private boolean containDllSource(Ini ini) {
        Profile.Section section = ini.get(DesktopIniConstants.SECTION_SHELL_CLASS_INFO);
        if (section == null) {
            return false;
        }
        return containDllSource(section.get(DesktopIniConstants.KEY_LOCALIZED_RESOURCE_NAME)) || containDllSource(section.get(DesktopIniConstants.KEY_INFO_TIP)) || containDllSource(section.get(DesktopIniConstants.KEY_ICON_FILE));
    }

    private boolean containDllSource(String path) {
        return !StringUtils.isEmpty(path) && path.startsWith("@") && path.toUpperCase().contains(".DLL");
    }

    private DesktopIniEntity generateDesktopIniEntity() {
        return DesktopIniEntity.Builder.builder().localizedResourceName(fieldLocalizedResourceName.getText())
                .infoTip(fieldInfoTip.getText())
                .iconResource(fieldIconResourceFile.getText(), fieldIconResourceIndex.getText())
                .icon(fieldIconFile.getText(), fieldIconIndex.getText()).build();
    }

    @Override
    public void localeChanged(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(I18nConstants.BASE_NAME);
        for (Component component : getComponents()) {
            if (component instanceof LocaleListener) {
                ((LocaleListener) component).localeChanged(locale);
            }
        }
        buttonGenerate.setText(bundle.getString("ui.button.generate"));
    }

    @Override
    public void folderChanged() {
        log.info("Folder Changed.");
        loadDesktopIniContent();
    }

}
