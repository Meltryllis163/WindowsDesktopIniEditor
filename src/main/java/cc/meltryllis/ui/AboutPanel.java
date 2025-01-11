package cc.meltryllis.ui;

import cc.meltryllis.constants.UIConstants;
import cc.meltryllis.ui.basic.LocaleLabel;
import cc.meltryllis.ui.event.LocaleListener;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import lombok.extern.log4j.Log4j2;
import net.miginfocom.layout.CC;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.util.Locale;

/**
 * @author Zachary W
 * @date 2024/12/31
 */
@Log4j2
public class AboutPanel extends JPanel implements LocaleListener {

    private static final String VERSION = "1.0.1";
    private static final String GITHUB_SITE = "https://github.com/Meltryllis163/WindowsDesktopIniEditor";

    private LocaleLabel versionKey;
    private LocaleLabel githubKey;
    private LocaleLabel githubValue;

    public AboutPanel() {
        MigLayout layout = new MigLayout("ins 20, gap 10! 15!", "[][60::, center]");
        setLayout(layout);
        initComponents();
    }

    public void initComponents() {
        int row = 0;
        int column = 0;
        versionKey = new LocaleLabel("ui.aboutPane.version", new FlatSVGIcon("icons/version.svg"));
        versionKey.setIconTextGap(UIConstants.ICON_TEXT_GAP);
        add(versionKey, new CC().cell(column, row));
        add(new JLabel(VERSION), new CC().cell(column + 1, row));

        row++;
        githubKey = new LocaleLabel("ui.aboutPane.github", new FlatSVGIcon("icons/github.svg"));
        githubKey.setIconTextGap(UIConstants.ICON_TEXT_GAP);
        add(githubKey, new CC().cell(column, row));
        githubValue = new LocaleLabel("ui.aboutPane.clickHere");
        githubValue.setToolTipText(GITHUB_SITE);
        githubValue.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        githubValue.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(URI.create(GITHUB_SITE));
                } catch (IOException ex) {
                    log.error(ex);
                    throw new RuntimeException(ex);
                }
            }
        });
        add(githubValue, new CC().cell(column + 1, row));
    }


    @Override
    public void localeChanged(Locale locale) {
        versionKey.localeChanged(locale);
        githubKey.localeChanged(locale);
        githubValue.localeChanged(locale);
    }

}
