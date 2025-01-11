package cc.meltryllis.ui.basic;

import cc.meltryllis.ui.MainApplication;
import cc.meltryllis.ui.event.LocaleListener;
import cc.meltryllis.utils.DesktopIniProcessor;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.util.StringUtils;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import net.miginfocom.layout.CC;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.util.Locale;

/**
 * 具有实时语言切换功能的文件选择器，支持手动输入和选择。
 *
 * @author Zachary W
 * @date 2024/12/25
 */
@Log4j2
public class LocaleFieldFileChooser extends JPanel implements LocaleListener {

    public static final int EMPTY = 0;
    public static final int INVALID = 1;
    public static final int VALID = 2;

    private final Timer validatePathTimer;
    private JTextField fieldFile;
    private JButton buttonBrowse;
    private final LocaleFileChooser fileChooser;
    private final String fileRuleI18nKey;
    private LocaleLabel labelValidateResultTip;
    @Getter
    private int validateResult;

    public LocaleFieldFileChooser(@NonNull LocaleFileChooser fileChooser, String fileRuleI18nKey) {
        this(fileChooser, 600, fileRuleI18nKey);
    }

    public LocaleFieldFileChooser(@NonNull LocaleFileChooser fileChooser, int timerDelay, String fileRuleI18nKey) {
        MigLayout layout = new MigLayout("ins 0, left top");
        this.fileChooser = fileChooser;
        setLayout(layout);
        validatePathTimer = new Timer(timerDelay, e -> validatePath());
        validatePathTimer.setRepeats(false);
        this.fileRuleI18nKey = fileRuleI18nKey;
        initComponents();
    }

    public void initComponents() {
        int row = 0, column = 0;
        initFileField();
        add(fieldFile, new CC().cell(column, row).pushX().growX());

        column++;
        initBrowseButton();
        add(buttonBrowse, new CC().cell(column, row));

        labelValidateResultTip = new LocaleLabel(fileRuleI18nKey, new FlatSVGIcon("icons/error.svg"));
        labelValidateResultTip.putClientProperty(FlatClientProperties.STYLE, "foreground:$Message.color;font:$Message.font");

    }

    public void initFileField() {
        fieldFile = new JTextField();
        fieldFile.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {

            }

            @Override
            public void focusLost(FocusEvent e) {
                log.debug("Focus Lost. Run validatePath().");
                validatePath();
            }
        });
        fieldFile.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                log.debug("Insert Update. Timer restart. Current path text is {}", fieldFile.getText());
                validatePathTimer.restart();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                log.debug("Remove Update. Timer restart. Current path text is {}", fieldFile.getText());
                validatePathTimer.restart();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });
    }

    public void initBrowseButton() {
        FlatSVGIcon browseIcon = new FlatSVGIcon("icons/browse.svg");
        buttonBrowse = new JButton(browseIcon);
        fileChooser.addActionListener(e -> {
            if (JFileChooser.APPROVE_SELECTION.equals(e.getActionCommand())) {
                log.info("FileChooser completed. Current selected file is {}", fileChooser.getSelectedFile().getPath());
                setText(fileChooser.getSelectedFile().getPath());
            }
        });
        buttonBrowse.addActionListener(e -> {
            // TODO 该方法会造成程序卡顿，等待FlatLaf更新
            // fileChooser.setCurrentDirectory(new File(DesktopIniProcessor.parseEnvironmentVariables(getText())));
            fileChooser.showOpenDialog(MainApplication.app);
        });
    }

    private void validatePath() {
        long start = System.currentTimeMillis();
        String path = getText();
        if (StringUtils.isEmpty(path)) {
            validateResult = EMPTY;
        } else {
            File file = new File(DesktopIniProcessor.parseEnvironmentVariables(path));
            validateResult = fileChooser.accept(file) ? VALID : INVALID;
        }
        updateFieldOutlineColor();
        updateValidateResultTip();
        updateFileChooserCurrentDirectory();
        log.debug("Time Spent: {}ms. Path: {}.", System.currentTimeMillis() - start, path);
    }

    private void updateFieldOutlineColor() {
        if (validateResult == EMPTY || validateResult == VALID) {
            fieldFile.putClientProperty(FlatClientProperties.OUTLINE, null);
        } else {
            fieldFile.putClientProperty(FlatClientProperties.OUTLINE, FlatClientProperties.OUTLINE_ERROR);
        }
    }

    private void updateValidateResultTip() {
        updateValidateTipStyle();

        boolean isTipExist = isTipExist();
        if (validateResult == EMPTY && isTipExist) {
            remove(labelValidateResultTip);
            revalidate();
        }
        if (validateResult != EMPTY && !isTipExist) {
            add(labelValidateResultTip, new CC().cell(0, 1));
            revalidate();
        }
    }

    private void updateValidateTipStyle() {
        boolean isValid = validateResult == VALID;
        labelValidateResultTip.setIcon(new FlatSVGIcon(isValid ? "icons/pass.svg" : "icons/error.svg"));
        labelValidateResultTip.setLocaleTextKey(isValid ? "ui.fileChooser.common.valid.tip" : fileRuleI18nKey);
    }

    private void updateFileChooserCurrentDirectory() {
        // TODO 该方法会造成程序卡顿，等待FlatLaf更新
        // if (this.validateResult == EMPTY || this.validateResult == INVALID) {
        //     fileChooser.setCurrentDirectory(null);
        // } else if (this.validateResult == VALID) {
        //     fileChooser.setCurrentDirectory(new File(DesktopIniProcessor.parseEnvironmentVariables(getText())));
        // }
    }

    public String getText() {
        return fieldFile.getText();
    }

    public void setText(String text) {
        String oldText = fieldFile.getText();
        log.debug("Old Text: {}. New Text: {}.", oldText, text);
        if (StringUtils.isEmpty(oldText)) {
            if (!StringUtils.isEmpty(text)) {
                fieldFile.setText(text);
            }
        } else if (!oldText.equals(text)) {
            fieldFile.setText(text);
        }
    }

    private boolean isTipExist() {
        return getComponentZOrder(labelValidateResultTip) != -1;
    }

    @Override
    public void localeChanged(Locale locale) {
        labelValidateResultTip.localeChanged(locale);
        fileChooser.localeChanged(locale);
    }

    @Override
    public void updateUI() {
        super.updateUI();
        // fileChooser并没有被添加到面板中，因此更新主题时有部分UI未更新，需要手动更新
        if (fileChooser != null) {
            fileChooser.updateUI();
        }
        // Fix 修复以下情形中标签颜色错误的问题：
        // 程序启动时标签颜色为明亮模式颜色。此时切换为暗黑模式，由于标签未被添加至组件中，因此updateUI不会更新标签颜色，因此需要手动更新。
        if (labelValidateResultTip != null) {
            labelValidateResultTip.updateUI();
        }
    }

    public void addDocumentListener(DocumentListener l) {
        if (l == null) {
            return;
        }
        fieldFile.getDocument().addDocumentListener(l);
    }

}
