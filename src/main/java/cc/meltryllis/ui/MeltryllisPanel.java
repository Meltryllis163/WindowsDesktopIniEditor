package cc.meltryllis.ui;

import cc.meltryllis.utils.I18nUtil;
import net.miginfocom.layout.CC;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;


/**
 * 一个神秘的面板。
 *
 * @author Zachary W
 * @date 2025/1/3
 */
public class MeltryllisPanel extends JPanel {

    public MeltryllisPanel() {
        MigLayout layout = new MigLayout("ins 20, gap 10! 15!");
        setLayout(layout);
        initComponents();
    }

    public void initComponents() {

        ImageIcon icon = new ImageIcon("src/main/resources/pic/114153183_p0.jpg");
        int width = 150;
        int height = width * icon.getIconHeight() / icon.getIconWidth();
        icon.setImage(icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
        JLabel labelPic = new JLabel(icon);
        add(labelPic, new CC().cell(0, 0).spanY(2));

        JTextArea textArea = new JTextArea(I18nUtil.getString("ui.dialog.meltryllis.message"));
        textArea.setEnabled(false);
        add(textArea, new CC().cell(1, 0).alignY("top"));

        JTextArea textAreaArt = new JTextArea(I18nUtil.getString("ui.dialog.meltryllis.artInfo"));
        textAreaArt.setEnabled(false);
        add(textAreaArt, new CC().cell(1, 1).alignY("bottom"));
    }

}
