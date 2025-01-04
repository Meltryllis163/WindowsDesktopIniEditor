package cc.meltryllis.ui;

import cc.meltryllis.constants.I18nConstants;
import cc.meltryllis.constants.UIConstants;
import cc.meltryllis.ui.basic.DialogBuilder;
import cc.meltryllis.ui.event.CustomEventManager;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.extras.FlatInspector;
import com.formdev.flatlaf.extras.FlatSVGUtils;
import com.formdev.flatlaf.extras.FlatUIDefaultsInspector;
import lombok.extern.log4j.Log4j2;
import org.ini4j.Config;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ResourceBundle;

/**
 * @author Zachary W
 * @date 2024/12/21
 */
@Log4j2
public class MainApplication extends JFrame {

    public static MainApplication app;

    public void initApplication() {
        setIconImages(FlatSVGUtils.createWindowIconImages("/icons/main.svg"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        registerGlobalHotKey();

        initSize();
        initMenuBar();
        initEditorPanel();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void initSize() {
        Config.getGlobal().setEscape(false);
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        setPreferredSize(new Dimension(screenSize.width / 2, screenSize.height / 2));
        setMinimumSize(UIConstants.MINIMUM_SIZE);
    }

    public void initMenuBar() {
        LocaleMenuBar menuBar = new LocaleMenuBar();
        CustomEventManager.getInstance().addLocaleListener(menuBar);
        setJMenuBar(menuBar);
    }

    public void initEditorPanel() {
        EditorPanel editorPanel = new EditorPanel();
        CustomEventManager.getInstance().addLocaleListener(editorPanel);
        add(editorPanel, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        FlatLightLaf.registerCustomDefaultsSource("themes");
        FlatLightLaf.setup();
        FlatInspector.install("ctrl shift alt X");
        FlatUIDefaultsInspector.install("ctrl shift alt Y");
        app = new MainApplication();
        app.initApplication();
    }

    private int magicCount = 0;

    public void registerGlobalHotKey() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            boolean isReleased = e.getID() == KeyEvent.KEY_RELEASED;
            if (!e.isControlDown()) {
                magicCount = 0;
                return false;
            }
            if (!isReleased) {
                return false;
            }
            switch (magicCount) {
                case 0:
                    magicCount = e.getKeyCode() == KeyEvent.VK_M ? magicCount + 1 : 0;
                    break;
                case 1:
                    magicCount = e.getKeyCode() == KeyEvent.VK_E ? magicCount + 1 : 0;
                    break;
                case 2:
                    magicCount = e.getKeyCode() == KeyEvent.VK_L ? magicCount + 1 : 0;
                    break;
                case 3:
                    magicCount = e.getKeyCode() == KeyEvent.VK_T ? magicCount + 1 : 0;
                    break;
                default:
                    magicCount = 0;
            }
            if (magicCount == 4) {
                DialogBuilder.JDialogBuilder.builder().owner(this).modal(true)
                        .title(ResourceBundle.getBundle(I18nConstants.BASE_NAME)
                                .getString("ui.dialog.meltryllis.title")).contentPane(new MeltryllisPanel())
                        .resizable(false)
                        .show();
                magicCount = 0;
                return true;
            }
            return false;
        });
    }

}
