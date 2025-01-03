package cc.meltryllis.ui.basic;

import cc.meltryllis.ui.MainApplication;
import cc.meltryllis.ui.event.LocaleListener;
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
    private JTextField fileField;
    private JButton browseButton;
    private final LocaleFileChooser fileChooser;
    private final String fileRuleI18nKey;
    private LocaleLabel validateResultTip;
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

    private static String parseEnvironmentVariables(String path) {
        if (path.startsWith("%")) {
            int index = path.indexOf("%", 1);
            if (index > 1) {
                String envKey = path.substring(1, index);
                String envValue = System.getenv(envKey);
                if (!StringUtils.isEmpty(envValue)) {
                    return path.replace(path.substring(0, index + 1), envValue);
                }
            }
        }
        return path;
    }

    public void initComponents() {
        int row = 0, column = 0;
        initFileField();
        add(fileField, new CC().cell(column, row).pushX().growX());

        column++;
        initBrowseButton();
        add(browseButton, new CC().cell(column, row));

        validateResultTip = new LocaleLabel(fileRuleI18nKey, new FlatSVGIcon("icons/error.svg"));
        validateResultTip.putClientProperty(FlatClientProperties.STYLE, "foreground:$Message.color;font:$Message.font");

    }

    public void initFileField() {
        fileField = new JTextField();
        fileField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {

            }

            @Override
            public void focusLost(FocusEvent e) {
                log.info("Focus Lost. validatePath()");
                validatePath();
            }
        });
        fileField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                log.info("Text changed. Timer restart.");
                validatePathTimer.restart();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                log.info("Text changed. Timer restart.");
                validatePathTimer.restart();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });
    }

    public void initBrowseButton() {
        FlatSVGIcon browseIcon = new FlatSVGIcon("icons/browse.svg");
        browseButton = new JButton(browseIcon);
        fileChooser.addActionListener(e -> {
            if (JFileChooser.APPROVE_SELECTION.equals(e.getActionCommand())) {
                setText(fileChooser.getSelectedFile().getPath());
            }
        });
        browseButton.addActionListener(e -> fileChooser.showOpenDialog(MainApplication.app));
    }

    private void validatePath() {
        String path = getText();
        if (StringUtils.isEmpty(path)) {
            this.validateResult = EMPTY;
        } else {
            File file = new File(parseEnvironmentVariables(path));
            boolean isPathValid = fileChooser.accept(file);
            if (isPathValid) {
                this.validateResult = VALID;
            } else {
                this.validateResult = INVALID;
            }
        }
        log.info("validatePath, result is {}", validateResult);
        updateFileChooserCurrentDirectory();
        updateValidateResultTip();
    }

    private void updateFileChooserCurrentDirectory() {
        if (this.validateResult == EMPTY || this.validateResult == INVALID) {
            fileChooser.setCurrentDirectory(null);
        } else if (this.validateResult == VALID) {
            fileChooser.setCurrentDirectory(new File(parseEnvironmentVariables(getText())));
        }
    }

    public String getText() {
        return fileField.getText();
    }

    private void updateValidateResultTip() {
        boolean isTipExist = getComponentZOrder(validateResultTip) != -1;
        if (validateResult == EMPTY) {
            if (isTipExist) {
                remove(validateResultTip);
            }
        } else {
            if (!isTipExist) {
                add(validateResultTip, new CC().cell(0, 1));
            }
            boolean isValid = validateResult == VALID;
            validateResultTip.setIcon(new FlatSVGIcon(isValid ? "icons/pass.svg" : "icons/error.svg"));
            validateResultTip.setLocaleTextKey(isValid ? "ui.fileChooser.common.valid.tip" : fileRuleI18nKey);
        }
        revalidate();
    }

    public void setText(String text) {
        String oldText = fileField.getText();
        log.info("setText(), oldText is {}, newTest is {}.", oldText, text);
        if (StringUtils.isEmpty(oldText)) {
            if (!StringUtils.isEmpty(text)) {
                fileField.setText(text);
            }
        } else if (!oldText.equals(text)) {
            fileField.setText(text);
        }
    }

    @Override
    public void localeChanged(Locale locale) {
        if (validateResultTip.isVisible()) {
            validateResultTip.localeChanged(locale);
        }
        fileChooser.localeChanged(locale);
    }

    @Override
    public void updateUI() {
        super.updateUI();
        // fileChooser并没有被添加到面板中，因此更新主题时有部分UI未更新，需要手动更新
        if (fileChooser != null) {
            fileChooser.updateUI();
        }
    }

    public void addDocumentListener(DocumentListener l) {
        if (l == null) {
            return;
        }
        fileField.getDocument().addDocumentListener(l);
    }

}
