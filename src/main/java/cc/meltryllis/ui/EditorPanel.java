package cc.meltryllis.ui;

import cc.meltryllis.constants.DesktopIniConstants;
import cc.meltryllis.constants.I18nConstants;
import cc.meltryllis.entity.DesktopIniEntity;
import cc.meltryllis.ui.basic.FolderFileFilter;
import cc.meltryllis.ui.basic.LocaleFileChooser;
import cc.meltryllis.ui.basic.LocaleFileChooserField;
import cc.meltryllis.ui.basic.LocaleLabel;
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

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

    private LocaleLabel titleLabel;
    private LocaleLabel folderPathLabel;
    private LocaleFileChooserField folderPathField;
    private LocaleLabel localizedResourceNameLabel;
    private JTextField localizedResourceNameField;
    private LocaleLabel infoTipLabel;
    private JTextField infoTipField;
    private LocaleLabel iconLabel;
    private LocaleFileChooserField iconPathField;
    private LocaleLabel iconIndexLabel;
    private JComboBox<Integer> iconIndexList;
    private JButton generateButton;

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
        titleLabel = new LocaleLabel("ui.title");
        titleLabel.putClientProperty(FlatClientProperties.STYLE, "font: 115%");
        add(titleLabel, new CC().spanX(2));

        row++;
        add(new JSeparator(), new CC().cell(column, row).spanX(2));

        row++;
        folderPathLabel = new LocaleLabel("ui.file.folder");
        add(folderPathLabel, new CC().cell(column, row).spanX(2));
        row++;
        LocaleFileChooser folderChooser = LocaleFileChooser.Builder.builder()
                .fileSelectionMode(JFileChooser.DIRECTORIES_ONLY)
                .addChoosableFileFilter(new FolderFileFilter())
                .approveButtonTextKey("ui.button.ok")
                .build();
        folderPathField = new LocaleFileChooserField(folderChooser, "ui.fileChooser.folder");
        folderPathField.addDocumentListener(new DocumentListener() {
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
        add(folderPathField, new CC().cell(column, row).spanX(2));

        row++;
        localizedResourceNameLabel = new LocaleLabel("ui.label.localizedResourceName");
        add(localizedResourceNameLabel, new CC().cell(column, row).spanX(2));
        row++;
        localizedResourceNameField = new JTextField();
        add(localizedResourceNameField, new CC().cell(column, row).spanX(2));

        row++;
        infoTipLabel = new LocaleLabel("ui.label.infoTip");
        add(infoTipLabel, new CC().cell(column, row).spanX(2));
        row++;
        infoTipField = new JTextField();
        add(infoTipField, new CC().cell(column, row).spanX(2));

        row++;
        iconLabel = new LocaleLabel("ui.label.iconFile");
        add(iconLabel, new CC().cell(column, row));
        iconIndexLabel = new LocaleLabel("ui.label.iconIndex");
        add(iconIndexLabel, new CC().cell(column + 1, row));

        row++;
        LocaleFileChooser iconChooser = LocaleFileChooser.Builder.builder()
                .fileSelectionMode(JFileChooser.FILES_ONLY)
                .addChoosableFileFilter("图标文件 (*.ico; *.bmp; *.exe)", "ico", "bmp", "exe")
                .build();
        iconPathField = new LocaleFileChooserField(iconChooser, "ui.fileChooser.icon");
        add(iconPathField, new CC().cell(column, row));
        iconIndexList = new JComboBox<>(new Integer[]{0});
        iconIndexList.setEnabled(false);
        iconIndexList.setToolTipText(bundle.getString("ui.tip.featureDeveloped"));
        add(iconIndexList, new CC().cell(column + 1, row));

        row++;
        generateButton = new JButton(bundle.getString("ui.button.generate"));
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (generateDesktopIni()) {
                    JOptionPane.showMessageDialog(EditorPanel.this, bundle.getString("ui.message.generateSuccess"));
                }
            }
        });
        add(generateButton, new CC().cell(column, row).grow(0).spanX(2));

    }

    public boolean generateDesktopIni() {
        // TODO 这个方法有点混乱
        //  null判断感觉到处都是，最好梳理一次
        ResourceBundle bundle = ResourceBundle.getBundle(I18nConstants.BASE_NAME);
        String folderPath = folderPathField.getText();
        if (StringUtils.isEmpty(folderPath)) {
            JOptionPane.showMessageDialog(MainApplication.app, bundle.getString("ui.message.noFolderSelected"));
            return false;
        }
        if (!folderPathField.check()) {
            JOptionPane.showMessageDialog(MainApplication.app, bundle.getString("ui.message.folderInvalid"));
            return false;
        }
        String iconPath = iconPathField.getText();
        if (!StringUtils.isEmpty(iconPath) && !iconPathField.check()) {
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
        infoTipField.setText(null);
        localizedResourceNameField.setText(null);
        iconPathField.setText(null);
        try {
            if (processor == null) {
                processor = new DesktopIniProcessor(folderPathField.getText());
            } else {
                processor.registerFolderPath(folderPathField.getText());
            }
        } catch (NoSuchFileException e) {
            return;
        }
        Ini desktopIni = processor.getDesktopIni();
        // TODO 这个iconIndex现在有点莫名其妙，需要尽快跟进dll的图标索引把这个正常放开
        if (desktopIni != null) {
            infoTipField.setText(desktopIni.get(DesktopIniConstants.SECTION_SHELL_CLASS_INFO, DesktopIniConstants.KEY_INFO_TIP));
            localizedResourceNameField.setText(desktopIni.get(DesktopIniConstants.SECTION_SHELL_CLASS_INFO, DesktopIniConstants.KEY_LOCALIZED_RESOURCE_NAME));
            iconPathField.setText(desktopIni.get(DesktopIniConstants.SECTION_SHELL_CLASS_INFO, DesktopIniConstants.KEY_ICON_FILE));
            iconIndexList.setSelectedItem(desktopIni.get(DesktopIniConstants.SECTION_SHELL_CLASS_INFO, DesktopIniConstants.KEY_ICON_INDEX));
        }
    }

    private DesktopIniEntity generateDesktopIniEntity() {
        return DesktopIniEntity.Builder.builder()
                .localizedResourceName(StringUtils.isEmpty(localizedResourceNameField.getText()) ? null : localizedResourceNameField.getText())
                .infoTip(StringUtils.isEmpty(infoTipField.getText()) ? null : infoTipField.getText())
                .icon(StringUtils.isEmpty(iconPathField.getText()) ? null : iconPathField.getText(), (Integer) iconIndexList.getSelectedItem())
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
        generateButton.setText(bundle.getString("ui.button.generate"));
        iconIndexList.setToolTipText(bundle.getString("ui.tip.featureDeveloped"));
    }

    @Override
    public void folderChanged() {
        loadDesktopIniContent();
    }

}
