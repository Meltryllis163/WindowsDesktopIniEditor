package cc.meltryllis.ui;

import cc.meltryllis.constants.DesktopIniConstants;
import cc.meltryllis.constants.I18nConstants;
import cc.meltryllis.entity.DesktopIniEntity;
import cc.meltryllis.ui.components.*;
import cc.meltryllis.utils.DesktopIniUtil;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.util.StringUtils;
import lombok.extern.log4j.Log4j2;
import net.miginfocom.layout.CC;
import net.miginfocom.swing.MigLayout;
import org.ini4j.Ini;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * desktop.ini 编辑器主面板。
 *
 * @author Zachary W
 * @date 2024/12/25
 */
@Log4j2
public class EditorPanel extends JPanel implements LocaleListener {

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

    public EditorPanel() {
        MigLayout layout = new MigLayout("ins 20", "[fill, grow, 80%][fill, push, 20%]", "top");
        setLayout(layout);
        initComponents();
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
        folderPathLabel = new LocaleLabel("ui.label.folder");
        add(folderPathLabel, new CC().cell(column, row).spanX(2));
        row++;
        LocaleFileChooser folderChooser = LocaleFileChooser.Builder.builder().fileSelectionMode(JFileChooser.DIRECTORIES_ONLY).addFileChecker(new FolderFileChecker()).approveButtonTextKey("ui.button.ok").build();
        folderPathField = new LocaleFileChooserField(folderChooser);
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
        LocaleFileChooser icoChooser = LocaleFileChooser.Builder.builder().fileSelectionMode(JFileChooser.FILES_ONLY).addFileChecker(new IconFileChecker()).build();
        iconPathField = new LocaleFileChooserField(icoChooser);
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
                generateDesktopIni();
            }
        });
        add(generateButton, new CC().cell(column, row).grow(0).spanX(2));

    }

    public boolean generateDesktopIni() {
        // TODO 先检查用户所有输入是否能够生成有效的Desktop.ini
        //  最好封装成check方法，暂时先这样放在一起，跑起来再说
        ResourceBundle bundle = ResourceBundle.getBundle(I18nConstants.BASE_NAME);
        String folderPath = folderPathField.getText();
        if (StringUtils.isEmpty(folderPath)) {
            JOptionPane.showMessageDialog(MainApplication.app, bundle.getString("ui.message.notChooseFolder"));
            return false;
        }
        if (!folderPathField.check(folderPath)) {
            JOptionPane.showMessageDialog(MainApplication.app, bundle.getString("ui.message.folderInvalid"));
            return false;
        }
        String icoPath = iconPathField.getText();
        if (!StringUtils.isEmpty(icoPath) && !iconPathField.check(icoPath)) {
            JOptionPane.showMessageDialog(MainApplication.app, bundle.getString("ui.message.icoPathInvalid"));
            return false;
        }
        // 如果存在第三方的Desktop.ini
        File iniFile = new File(DesktopIniUtil.getDesktopIniPath(folderPath));
        boolean isFolderSystem = false;
        if (!iniFile.exists()) {
            isFolderSystem = DesktopIniUtil.isFolderSystem(folderPath);
        } else {
            try {
                Ini ini = new Ini(iniFile);
                // 第三方desktop.ini
                if (ini.get(DesktopIniConstants.SECTION_ORIGINAL_FOLDER_ATTRIBUTE, DesktopIniConstants.KEY_IS_SYSTEM) == null) {
                    isFolderSystem = DesktopIniUtil.isFolderSystem(folderPath);
                    Object[] options = new Object[]{bundle.getString("ui.button.backup"), bundle.getString("ui.button.cancelGenerate")};
                    int choice = JOptionPane.showOptionDialog(MainApplication.app, bundle.getString("ui.message.thirdPartyIniExist"), bundle.getString("ui.message.warn"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
                    if (choice == JOptionPane.NO_OPTION || choice == JOptionPane.CLOSED_OPTION) {
                        return false;
                    }
                    // 备份
                    else {
                        DesktopIniUtil.backupThirdPartyDesktopIni(folderPath);
                    }
                } else {
                    isFolderSystem = Boolean.parseBoolean(ini.get(DesktopIniConstants.SECTION_ORIGINAL_FOLDER_ATTRIBUTE, DesktopIniConstants.KEY_IS_SYSTEM));
                }
            } catch (IOException e) {
                log.error(e);
                throw new RuntimeException(e);
            }
        }
        DesktopIniEntity entity = generateDesktopIniEntity();
        entity.setIsFolderSystem(isFolderSystem);
        DesktopIniUtil.createDesktopIni(folderPath, entity.convertToIni());
        return false;
    }

    private DesktopIniEntity generateDesktopIniEntity() {
        return DesktopIniEntity.Builder.builder().localizedResourceName(null)
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

}
