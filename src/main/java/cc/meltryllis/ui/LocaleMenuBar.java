package cc.meltryllis.ui;

import cc.meltryllis.constants.I18nConstants;
import cc.meltryllis.ui.event.CustomEventManager;
import cc.meltryllis.ui.event.LocaleListener;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.components.FlatButton;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * 本应用程序的菜单栏。
 *
 * @author Zachary W
 * @date 2024/12/26
 */
public class LocaleMenuBar extends JMenuBar implements LocaleListener {

    private JMenu fileMenu;
    private JMenu languageMenu;
    /** 用于生成对应的{@link JMenuItem} */
    private final List<Locale> locales = Arrays.asList(Locale.SIMPLIFIED_CHINESE, Locale.ENGLISH);
    private JMenuItem quitItem;

    private JMenu helpMenu;
    private JMenuItem aboutItem;

    private boolean isDark;

    public LocaleMenuBar() {
        initialize();
    }

    public void initialize() {
        isDark = false;
        ResourceBundle bundle = ResourceBundle.getBundle(I18nConstants.BASE_NAME);
        fileMenu = new JMenu(bundle.getString("ui.menu.file"));
        languageMenu = new JMenu(bundle.getString("ui.menu.file.languages"));
        fileMenu.add(languageMenu);
        for (Locale locale : locales) {
            JMenuItem item = new JMenuItem(locale.getDisplayName(locale));
            item.addActionListener(e -> {
                Locale.setDefault(locale);
                ResourceBundle.clearCache();
                CustomEventManager.getInstance().fireLocaleChanged(locale);
            });
            languageMenu.add(item);
        }
        quitItem = new JMenuItem(bundle.getString("ui.menu.file.quit"));
        quitItem.addActionListener(e -> MainApplication.app.dispose());
        fileMenu.add(quitItem);
        add(fileMenu);

        helpMenu = new JMenu(bundle.getString("ui.menu.help"));
        aboutItem = new JMenuItem(bundle.getString("ui.menu.help.about"));
        aboutItem.addActionListener(e -> {
            JDialog aboutDialog = new JDialog(MainApplication.app);
            aboutDialog.setResizable(false);
            aboutDialog.setTitle(ResourceBundle.getBundle(I18nConstants.BASE_NAME)
                    .getString("ui.aboutPane.title"));
            aboutDialog.setContentPane(new AboutPanel());
            aboutDialog.pack();
            aboutDialog.setLocationRelativeTo(MainApplication.app);
            aboutDialog.setVisible(true);
        });
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
        ResourceBundle bundle = ResourceBundle.getBundle(I18nConstants.BASE_NAME);
        fileMenu.setText(bundle.getString("ui.menu.file"));
        languageMenu.setText(bundle.getString("ui.menu.file.languages"));
        quitItem.setText(bundle.getString("ui.menu.file.quit"));
        helpMenu.setText(bundle.getString("ui.menu.help"));
        aboutItem.setText(bundle.getString("ui.menu.help.about"));
    }

}
