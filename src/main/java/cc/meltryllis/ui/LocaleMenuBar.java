package cc.meltryllis.ui;

import cc.meltryllis.ui.basic.DialogBuilder;
import cc.meltryllis.ui.event.LocaleListener;
import cc.meltryllis.utils.I18nUtil;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.components.FlatButton;
import lombok.extern.log4j.Log4j2;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * 本应用程序的菜单栏。
 *
 * @author Zachary W
 * @date 2024/12/26
 */
@Log4j2
public class LocaleMenuBar extends JMenuBar implements LocaleListener {

    private JMenu fileMenu;
    private JMenu languageMenu;
    /** 用于生成对应语言的 {@link JMenuItem} */
    private final List<Locale> locales = Arrays.asList(Locale.SIMPLIFIED_CHINESE, Locale.ENGLISH);
    private JMenuItem quitItem;

    private JMenu helpMenu;
    private JMenuItem aboutItem;

    private boolean isDark;

    public LocaleMenuBar() {
        initComponents();
    }

    public void initComponents() {
        isDark = false;

        fileMenu = new JMenu(I18nUtil.getString("ui.menu.file"));
        languageMenu = new JMenu(I18nUtil.getString("ui.menu.file.languages"));
        fileMenu.add(languageMenu);
        for (Locale locale : locales) {
            JMenuItem item = new JMenuItem(locale.getDisplayName(locale));
            item.addActionListener(e -> I18nUtil.updateLocale(locale));
            languageMenu.add(item);
        }
        quitItem = new JMenuItem(I18nUtil.getString("ui.menu.file.quit"));
        quitItem.addActionListener(e -> MainApplication.app.dispose());
        fileMenu.add(quitItem);
        add(fileMenu);

        helpMenu = new JMenu(I18nUtil.getString("ui.menu.help"));
        aboutItem = new JMenuItem(I18nUtil.getString("ui.menu.help.about"));
        aboutItem.addActionListener(e -> DialogBuilder.JDialogBuilder.builder().resizable(false)
                .title(I18nUtil.getString("ui.aboutPane.title"))
                .contentPane(new AboutPanel())
                .show());
        helpMenu.add(aboutItem);
        add(helpMenu);

        // 右侧按钮
        add(Box.createGlue());

        // 切换主题模式
        FlatButton lightDarkButton = createLightDarkButton();
        add(lightDarkButton);
    }

    private FlatButton createLightDarkButton() {
        FlatButton lightDarkButton = new FlatButton();
        FlatSVGIcon lightIcon = new FlatSVGIcon("icons/light.svg");
        FlatSVGIcon darkIcon = new FlatSVGIcon("icons/dark.svg");
        lightDarkButton.setButtonType(FlatButton.ButtonType.toolBarButton);
        lightDarkButton.setIcon(darkIcon);
        lightDarkButton.setFocusable(false);
        lightDarkButton.addActionListener(e -> {
            isDark = !isDark;
            if (isDark) {
                lightDarkButton.setIcon(lightIcon);
                FlatDarkLaf.setup();
                FlatDarkLaf.updateUI();
            } else {
                lightDarkButton.setIcon(darkIcon);
                FlatLightLaf.setup();
                FlatLightLaf.updateUI();
            }
        });
        return lightDarkButton;
    }

    @Override
    public void localeChanged(Locale locale) {
        fileMenu.setText(I18nUtil.getString("ui.menu.file"));
        languageMenu.setText(I18nUtil.getString("ui.menu.file.languages"));
        quitItem.setText(I18nUtil.getString("ui.menu.file.quit"));
        helpMenu.setText(I18nUtil.getString("ui.menu.help"));
        aboutItem.setText(I18nUtil.getString("ui.menu.help.about"));
    }

}
