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

    private LocaleFieldFileChooser chooserFolderPath;
    private JTextField fieldLocalizedResourceName;
    private JTextField fieldInfoTip;
    private IconChooserPanel chooserIconResource;
    private IconChooserPanel chooserIconFile;
    private JButton buttonGenerate;

    private DesktopIniProcessor processor;

    public EditorPanel() {
        MigLayout layout = new MigLayout("ins 20", "fill, grow");
        setLayout(layout);
        initComponents();
        CustomEventManager.getInstance()
                .addFolderChangeListener(this);
    }

    public void initComponents() {

        ResourceBundle bundle = ResourceBundle.getBundle(I18nConstants.BASE_NAME);

        int row = 0, column = 0;
        LocaleLabel titleLabel = new LocaleLabel("ui.title");
        titleLabel.putClientProperty(FlatClientProperties.STYLE, "font: 115%");
        add(titleLabel, new CC());

        row++;
        add(new JSeparator(), new CC().cell(column, row));

        row++;
        LocaleLabel labelFolderPath = new LocaleLabel("ui.label.folder");
        add(labelFolderPath, new CC().cell(column, row));
        row++;
        LocaleFileChooser folderChooser = LocaleFileChooser.Builder.builder()
                .fileSelectionMode(JFileChooser.DIRECTORIES_ONLY)
                .addChoosableFileFilter(new FolderFileFilter())
                .approveButtonTextKey("ui.button.ok")
                .build();
        chooserFolderPath = new LocaleFieldFileChooser(folderChooser, "ui.fileChooser.folder.tip");
        chooserFolderPath.addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                log.info("Folder text changed. Fire FolderChanged event.");
                CustomEventManager.getInstance()
                        .fireFolderChanged();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                log.info("Folder text changed. Fire FolderChanged event.");
                CustomEventManager.getInstance()
                        .fireFolderChanged();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                CustomEventManager.getInstance()
                        .fireFolderChanged();
            }
        });
        add(chooserFolderPath, new CC().cell(column, row));

        row++;
        LocaleLabel labelLocalizedResourceName = new LocaleLabel("ui.label.localizedResourceName");
        add(labelLocalizedResourceName, new CC().cell(column, row));
        row++;
        fieldLocalizedResourceName = new JTextField();
        add(fieldLocalizedResourceName, new CC().cell(column, row));

        row++;
        LocaleLabel labelInfoTip = new LocaleLabel("ui.label.infoTip");
        add(labelInfoTip, new CC().cell(column, row));
        row++;
        fieldInfoTip = new JTextField();
        add(fieldInfoTip, new CC().cell(column, row));

        row++;
        chooserIconResource = new IconChooserPanel("ui.label.iconResourceFile", "ui.label.iconResourceIndex");
        add(chooserIconResource, new CC().cell(column, row));

        row++;
        chooserIconFile = new IconChooserPanel("ui.label.iconFile", "ui.label.iconIndex");
        add(chooserIconFile, new CC().cell(column, row));

        row++;
        buttonGenerate = new JButton(bundle.getString("ui.button.generate"));
        buttonGenerate.addActionListener(e -> {
            if (generateDesktopIni()) {
                JOptionPane.showMessageDialog(EditorPanel.this, bundle.getString("ui.dialog.generate.success"));
            }
        });
        add(buttonGenerate, new CC().cell(column, row)
                .grow(0));
    }

    public boolean generateDesktopIni() {
        ResourceBundle bundle = ResourceBundle.getBundle(I18nConstants.BASE_NAME);
        if (chooserFolderPath.getValidateResult() == LocaleFieldFileChooser.INVALID) {
            JOptionPane.showMessageDialog(MainApplication.app, bundle.getString("ui.dialog.folder.invalid"));
            return false;
        }
        if (chooserIconResource.getValidateResult() == LocaleFieldFileChooser.INVALID) {
            JOptionPane.showMessageDialog(MainApplication.app, bundle.getString("ui.dialog.icon.invalid"));
            return false;
        }
        try {
            processor = new DesktopIniProcessor(chooserFolderPath.getText());
        } catch (NoSuchFileException e) {
            return false;
        }
        DesktopIniEntity entity = generateDesktopIniEntity();
        return processor.createDesktopIniFile(entity.convertToIni());
    }

    public void loadDesktopIniContent() {
        try {
            if (processor == null) {
                processor = new DesktopIniProcessor(chooserFolderPath.getText());
            } else {
                processor.registerFolderPath(chooserFolderPath.getText());
            }
        } catch (NoSuchFileException e) {
            return;
        }
        log.info("Start loading desktop.ini content, current path : {}", chooserFolderPath.getText());
        Ini desktopIni = processor.getDesktopIni();
        Profile.Section section =
                desktopIni == null ? null : desktopIni.get(DesktopIniConstants.SECTION_SHELL_CLASS_INFO);
        if (section != null) {
            if (containDllSource(desktopIni)) {
                ResourceBundle bundle = ResourceBundle.getBundle(I18nConstants.BASE_NAME);
                Object[] options = new Object[]{bundle.getString("ui.dialog.dllWarning.options.continue"), bundle.getString("ui.dialog.dllWarning.options.cancel")};
                int res = DialogBuilder.OptionDialogBuilder.builder(bundle.getString("ui.dialog.dllWarning.message"))
                        .parent(this)
                        .title(bundle.getString("ui.dialog.dllWarning.title"))
                        .optionType(JOptionPane.YES_NO_OPTION)
                        .messageType(JOptionPane.WARNING_MESSAGE)
                        .options(options)
                        .initialValue(JOptionPane.NO_OPTION)
                        .show();
                log.info("DLL file detected. User choose {}.", res == JOptionPane.YES_OPTION ? "continue" : "cancel");
                if (res != JOptionPane.YES_OPTION) {
                    // TODO 这个地方会出现set以后立刻清除的一个闪烁
                    SwingUtilities.invokeLater(() -> chooserFolderPath.setText(null));
                    return;
                }
            }
            fieldInfoTip.setText(section.get(DesktopIniConstants.KEY_INFO_TIP));
            fieldLocalizedResourceName.setText(section.get(DesktopIniConstants.KEY_LOCALIZED_RESOURCE_NAME));
            String iconResource = section.get(DesktopIniConstants.KEY_ICON_RESOURCE);
            if (!StringUtils.isEmpty(iconResource)) {
                int index = iconResource.lastIndexOf(',');
                if (index > 0) {
                    chooserIconResource.setIconPath(iconResource.substring(0, index));
                    chooserIconResource.setIconIndex(iconResource.substring(index + 1)
                            .trim());
                }
            }
            chooserIconFile.setIconPath(section.get(DesktopIniConstants.KEY_ICON_FILE));
            chooserIconFile.setIconIndex(section.get(DesktopIniConstants.KEY_ICON_INDEX));
        } else {
            log.info("No desktop.ini exist. Clear all text.");
            fieldInfoTip.setText(null);
            fieldLocalizedResourceName.setText(null);
            chooserIconResource.clearValues();
        }
    }

    private boolean containDllSource(Ini ini) {
        Profile.Section section = ini.get(DesktopIniConstants.SECTION_SHELL_CLASS_INFO);
        if (section == null) {
            return false;
        }
        return containDllSource(section.get(DesktopIniConstants.KEY_LOCALIZED_RESOURCE_NAME))
                || containDllSource(section.get(DesktopIniConstants.KEY_INFO_TIP))
                || containDllSource(section.get(DesktopIniConstants.KEY_ICON_FILE));
    }

    private boolean containDllSource(String path) {
        return !StringUtils.isEmpty(path) && path.startsWith("@") && path.toUpperCase()
                .contains(".DLL");
    }

    private DesktopIniEntity generateDesktopIniEntity() {
        return DesktopIniEntity.Builder.builder()
                .localizedResourceName(fieldLocalizedResourceName.getText())
                .infoTip(fieldInfoTip.getText())
                .iconResource(chooserIconResource.getIconPath(), chooserIconResource.getIconIndex())
                .icon(chooserIconFile.getIconPath(), chooserIconFile.getIconIndex())
                .build();
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
        log.info("Folder changed. Current folder is {}", chooserFolderPath.getText());
        loadDesktopIniContent();
    }

}
