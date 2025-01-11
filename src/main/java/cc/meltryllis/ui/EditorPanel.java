package cc.meltryllis.ui;

import cc.meltryllis.constants.DesktopIniConstants;
import cc.meltryllis.entity.DesktopIniEntity;
import cc.meltryllis.ui.basic.FolderFileFilter;
import cc.meltryllis.ui.basic.LocaleFieldFileChooser;
import cc.meltryllis.ui.basic.LocaleFileChooser;
import cc.meltryllis.ui.basic.LocaleLabel;
import cc.meltryllis.ui.event.CustomEventManager;
import cc.meltryllis.ui.event.FolderChangeListener;
import cc.meltryllis.ui.event.LocaleListener;
import cc.meltryllis.utils.DesktopIniProcessor;
import cc.meltryllis.utils.I18nUtil;
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
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.nio.file.NoSuchFileException;
import java.util.Locale;
import java.util.Random;

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
        CustomEventManager.getInstance().addFolderChangeListener(this);
    }

    public void initComponents() {

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
                .fileSelectionMode(JFileChooser.DIRECTORIES_ONLY).addChoosableFileFilter(new FolderFileFilter())
                .approveButtonTextKey("ui.button.ok").build();
        chooserFolderPath = new LocaleFieldFileChooser(folderChooser, "ui.fileChooser.folder.tip");
        chooserFolderPath.addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                CustomEventManager.getInstance().fireFolderChanged();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                CustomEventManager.getInstance().fireFolderChanged();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                CustomEventManager.getInstance().fireFolderChanged();
            }
        });
        add(chooserFolderPath, new CC().cell(column, row));

        row++;
        LocaleLabel labelLocalizedResourceName = new LocaleLabel("ui.label.localizedResourceName");
        add(labelLocalizedResourceName, new CC().cell(column, row));
        row++;
        fieldLocalizedResourceName = new JTextField();
        fieldLocalizedResourceName.getInputMap()
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK | InputEvent.ALT_DOWN_MASK), "Random Text");
        fieldLocalizedResourceName.getActionMap().put("Random Text", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fieldLocalizedResourceName.setText("随机别名" + new Random().nextInt(1000));
            }
        });
        add(fieldLocalizedResourceName, new CC().cell(column, row));

        row++;
        LocaleLabel labelInfoTip = new LocaleLabel("ui.label.infoTip");
        add(labelInfoTip, new CC().cell(column, row));
        row++;
        fieldInfoTip = new JTextField();
        fieldInfoTip.getInputMap()
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK | InputEvent.ALT_DOWN_MASK), "Random Text");
        fieldInfoTip.getActionMap().put("Random Text", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fieldInfoTip.setText("随机备注" + new Random().nextInt(1000));
            }
        });
        add(fieldInfoTip, new CC().cell(column, row));

        row++;
        chooserIconResource = new IconChooserPanel("ui.label.iconResourceFile", "ui.label.iconResourceIndex");
        add(chooserIconResource, new CC().cell(column, row));

        row++;
        chooserIconFile = new IconChooserPanel("ui.label.iconFile", "ui.label.iconIndex");
        add(chooserIconFile, new CC().cell(column, row));

        row++;
        buttonGenerate = new JButton(I18nUtil.getString("ui.button.generate"));
        buttonGenerate.addActionListener(e -> {
            if (generateDesktopIni()) {
                JOptionPane.showMessageDialog(EditorPanel.this, I18nUtil.getString("ui.dialog.generate.success"));
            }
        });
        add(buttonGenerate, new CC().cell(column, row).grow(0));
    }

    public boolean generateDesktopIni() {

        if (chooserFolderPath.getValidateResult() == LocaleFieldFileChooser.INVALID) {
            JOptionPane.showMessageDialog(MainApplication.app, I18nUtil.getString("ui.dialog.folder.invalid"));
            return false;
        }
        if (chooserIconResource.getValidateResult() == LocaleFieldFileChooser.INVALID) {
            JOptionPane.showMessageDialog(MainApplication.app, I18nUtil.getString("ui.dialog.iconResource.invalid"));
            return false;
        }
        if (chooserIconFile.getValidateResult() == LocaleFieldFileChooser.INVALID) {
            JOptionPane.showMessageDialog(MainApplication.app, I18nUtil.getString("ui.dialog.iconFile.invalid"));
            return false;
        }
        try {
            processor = new DesktopIniProcessor(chooserFolderPath.getText());
        } catch (NoSuchFileException e) {
            return false;
        }
        DesktopIniEntity entity = generateDesktopIniEntity();
        return processor.storeIni(entity.convertToIni());
    }

    private DesktopIniEntity generateDesktopIniEntity() {
        return DesktopIniEntity.Builder.builder().localizedResourceName(fieldLocalizedResourceName.getText())
                .infoTip(fieldInfoTip.getText())
                .iconResource(chooserIconResource.getIconPath(), chooserIconResource.getIconIndex())
                .icon(chooserIconFile.getIconPath(), chooserIconFile.getIconIndex()).build();
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
        log.info("Start loading desktop.ini content. Current Path: {}", chooserFolderPath.getText());
        Ini desktopIni = processor.getDesktopIni();
        Profile.Section section =
                desktopIni == null ? null : desktopIni.get(DesktopIniConstants.SECTION_SHELL_CLASS_INFO);
        if (section != null) {
            fieldInfoTip.setText(section.get(DesktopIniConstants.KEY_INFO_TIP));
            fieldLocalizedResourceName.setText(section.get(DesktopIniConstants.KEY_LOCALIZED_RESOURCE_NAME));
            loadIconResource(section.get(DesktopIniConstants.KEY_ICON_RESOURCE));
            chooserIconFile.setIconPath(section.get(DesktopIniConstants.KEY_ICON_FILE));
            chooserIconFile.setIconIndex(section.get(DesktopIniConstants.KEY_ICON_INDEX));
        } else {
            log.info("Desktop.ini not exist. Clear all text.");
            fieldInfoTip.setText(null);
            fieldLocalizedResourceName.setText(null);
            chooserIconResource.clearText();
            chooserIconFile.clearText();
        }
    }

    private void loadIconResource(String iconResource) {
        if (!StringUtils.isEmpty(iconResource)) {
            int index = iconResource.lastIndexOf(',');
            if (index > 0) {
                chooserIconResource.setIconPath(iconResource.substring(0, index));
                chooserIconResource.setIconIndex(iconResource.substring(index + 1).trim());
            }
        }
    }

    @Override
    public void localeChanged(Locale locale) {

        for (Component component : getComponents()) {
            if (component instanceof LocaleListener) {
                ((LocaleListener) component).localeChanged(locale);
            }
        }
        buttonGenerate.setText(I18nUtil.getString("ui.button.generate"));
    }

    @Override
    public void folderChanged() {
        // TODO 读取到系统路径后不允许编辑
        log.info("Folder changed. Current folder is {}.", chooserFolderPath.getText());
        loadDesktopIniContent();
    }

}
