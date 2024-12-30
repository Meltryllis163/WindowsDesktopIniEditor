package cc.meltryllis.ui.components;

import cc.meltryllis.ui.MainApplication;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.util.StringUtils;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import net.miginfocom.layout.CC;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
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

    private JTextField pathField;
    private JButton browseButton;
    private final LocaleFileChooser fileChooser;
    private LocaleLabel messageLabel;

    public LocaleFileChooserField(@NonNull LocaleFileChooser fileChooser) {
        MigLayout layout = new MigLayout("ins 0, left top");
        this.fileChooser = fileChooser;
        setLayout(layout);
        initComponents();
    }

    public void initComponents() {
        int row = 0, column = 0;
        pathField = new JTextField();
        pathField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {

            }

            @Override
            public void focusLost(FocusEvent e) {
                maybeCheck();
            }
        });
        add(pathField, new CC().cell(column, row).pushX().growX());

        column++;
        initBrowseButton();
        add(browseButton, new CC().cell(column, row));

        messageLabel = LocaleLabel.createDebugLabel();
        messageLabel.putClientProperty(FlatClientProperties.STYLE, "foreground:$Message.color;font:$Message.font");

    }

    public void initBrowseButton() {
        FlatSVGIcon browseIcon = new FlatSVGIcon("icons/browse.svg");
        browseButton = new JButton(browseIcon);
        modifyFileChooser();
        browseButton.addActionListener(e -> {
            String text = pathField.getText();
            fileChooser.setCurrentDirectory(StringUtils.isEmpty(text) || !Files.exists(Paths.get(text), LinkOption.NOFOLLOW_LINKS) ? null : new File(text));
            fileChooser.showOpenDialog(MainApplication.app);
        });
    }

    private void modifyFileChooser() {
        fileChooser.addActionListener(e -> {
            if (JFileChooser.APPROVE_SELECTION.equals(e.getActionCommand())) {
                pathField.setText(fileChooser.getSelectedFile().getPath());
                maybeCheck();
            }
        });
    }

    public void maybeCheck() {
        String pathText = pathField.getText();
        if (StringUtils.isEmpty(pathText)) {
            clearMessage();
        } else {
            check(pathText);
            showMessage();
        }
    }

    // TODO 这里的逻辑还不太清楚，主要是路径有三种状态
    //  为空，什么都不提示，而且要清楚现在存在的提示
    //  不为空但是不符合要求，提示错误信息
    //  不为空且符合要求，提示路径有效
    //  这导致布尔值不太够用，还没想到简单又好用的办法
    //  而且check和showMessage绑定的有点严重，很难拆开
    //  最好是能直接check，不用传入String，统一到一个方法里，不要再拆出一个maybeCheck
    public boolean check(String pathText) {
        return !StringUtils.isEmpty(pathText) && fileChooser.getFileCheckerManager().accept(new File(pathText));
    }

    private void showMessage() {
        if (getComponentZOrder(messageLabel) == -1) {
            add(messageLabel, new CC().cell(0, 1));
        }
        messageLabel.setIcon(fileChooser.getFileCheckerManager().getErrorIcon());
        // TODO 关于I18n Key相关的变量命名太混乱了，以后要统一一下
        messageLabel.setLocaleTextKey(fileChooser.getFileCheckerManager().getI18nErrorKey());
        revalidate();
    }

    private void clearMessage() {
        if (getComponentZOrder(messageLabel) != -1) {
            remove(messageLabel);
            revalidate();
        }
    }

    public String getText() {
        return pathField == null ? null : pathField.getText();
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

}
