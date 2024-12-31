package cc.meltryllis.ui.basic;

import cc.meltryllis.ui.MainApplication;
import cc.meltryllis.ui.event.LocaleListener;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.util.StringUtils;
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
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.util.Locale;

/**
 * 具有实时语言切换功能的文件选择器，支持手动输入和选择。
 *
 * @author Zachary W
 * @date 2024/12/25
 */
@Log4j2
public class LocaleFileChooserField extends JPanel implements LocaleListener {


    private int timerDelay;
    private Timer checkTimer;
    private JTextField fileField;
    private JButton browseButton;
    private final LocaleFileChooser fileChooser;
    private String fileRuleI18nKey;
    private LocaleLabel messageLabel;

    public LocaleFileChooserField(@NonNull LocaleFileChooser fileChooser, String fileRuleI18nKey) {
        this(fileChooser, 600, fileRuleI18nKey);
    }

    public LocaleFileChooserField(@NonNull LocaleFileChooser fileChooser, int timerDelay, String fileRuleI18nKey) {
        MigLayout layout = new MigLayout("ins 0, left top");
        this.fileChooser = fileChooser;
        setLayout(layout);
        this.timerDelay = timerDelay;
        checkTimer = new Timer(timerDelay, e -> check());
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

        messageLabel = new LocaleLabel(fileRuleI18nKey, new FlatSVGIcon("icons/error.svg"));
        messageLabel.putClientProperty(FlatClientProperties.STYLE, "foreground:$Message.color;font:$Message.font");

    }

    public void initFileField() {
        fileField = new JTextField();
        fileField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                check();
            }

            @Override
            public void focusLost(FocusEvent e) {

            }
        });
        fileField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                checkTimer.restart();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkTimer.restart();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });
    }

    public void initBrowseButton() {
        FlatSVGIcon browseIcon = new FlatSVGIcon("icons/browse.svg");
        browseButton = new JButton(browseIcon);
        modifyFileChooser();
        browseButton.addActionListener(e -> {
            String text = fileField.getText();
            fileChooser.setCurrentDirectory(StringUtils.isEmpty(text) || !Files.exists(Paths.get(text), LinkOption.NOFOLLOW_LINKS) ? null : new File(text));
            fileChooser.showOpenDialog(MainApplication.app);
        });
    }

    private void modifyFileChooser() {
        fileChooser.addActionListener(e -> {
            if (JFileChooser.APPROVE_SELECTION.equals(e.getActionCommand())) {
                fileField.setText(fileChooser.getSelectedFile().getPath());
                check();
            }
        });
    }

    public boolean check() {
        String path = fileField.getText();
        if (StringUtils.isEmpty(path)) {
            clearMessage();
            return false;
        } else {
            boolean checkRes = fileChooser.accept(new File(parseEnvironmentVariables(path)));
            showMessage(checkRes);
            return checkRes;
        }
    }

    private void showMessage(boolean pass) {
        if (getComponentZOrder(messageLabel) == -1) {
            add(messageLabel, new CC().cell(0, 1));
        }
        // TODO 关于I18n Key相关的变量命名太混乱了，以后要统一一下
        messageLabel.setIcon(new FlatSVGIcon(pass ? "icons/pass.svg" : "icons/error.svg"));
        messageLabel.setLocaleTextKey(pass ? "ui.fileChooser.validPath" : fileRuleI18nKey);
        revalidate();
    }

    private void clearMessage() {
        if (getComponentZOrder(messageLabel) != -1) {
            remove(messageLabel);
            revalidate();
        }
    }

    public String getText() {
        return fileField.getText();
    }

    public void setText(String text) {
        fileField.setText(text);
        if (StringUtils.isEmpty(text)) {
            fileChooser.setSelectedFile(null);
        } else {
            fileChooser.setSelectedFile(new File(text));
        }
    }

    @Override
    public void localeChanged(Locale locale) {
        if (messageLabel.isVisible()) {
            messageLabel.localeChanged(locale);
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
