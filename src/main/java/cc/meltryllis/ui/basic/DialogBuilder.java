package cc.meltryllis.ui.basic;

import lombok.NonNull;

import javax.swing.*;
import java.awt.*;

/**
 * 提供Builder模式给各种弹出面板。
 *
 * @author Zachary W
 * @date 2025/1/3
 */
@SuppressWarnings("unused, MagicConstant")
public final class DialogBuilder {

    private DialogBuilder() {
        throw new RuntimeException();
    }

    public static class MessageDialogBuilder {

        private final Object message;
        private Component parentComponent;
        private String title;
        private int messageType;
        private Icon icon;

        private MessageDialogBuilder(@NonNull String message) {
            this.message = message;
            this.title = UIManager.getString("OptionPane.messageDialogTitle");
            this.messageType = JOptionPane.INFORMATION_MESSAGE;
        }

        public static MessageDialogBuilder builder(@NonNull String message) {
            return new MessageDialogBuilder(message);
        }

        public MessageDialogBuilder parentComponent(Component c) {
            this.parentComponent = c;
            return this;
        }

        public MessageDialogBuilder title(String title) {
            this.title = title;
            return this;
        }

        public MessageDialogBuilder messageType(int type) {
            this.messageType = type;
            return this;
        }

        public MessageDialogBuilder icon(Icon icon) {
            this.icon = icon;
            return this;
        }

        public void show() {
            JOptionPane.showMessageDialog(parentComponent, message, title, messageType, icon);
        }

    }

    public static class OptionDialogBuilder {

        private final Object message;
        private Component parentComponent;
        private String title;
        private int optionType;
        private int messageType;
        private Icon icon;
        private Object[] options;
        private Object initialValue;

        private OptionDialogBuilder(Object message) {
            this.optionType = JOptionPane.DEFAULT_OPTION;
            this.messageType = JOptionPane.PLAIN_MESSAGE;
            this.message = message;
        }

        public static OptionDialogBuilder builder(Object message) {
            return new OptionDialogBuilder(message);
        }

        public OptionDialogBuilder parent(Component parentComponent) {
            this.parentComponent = parentComponent;
            return this;
        }

        public OptionDialogBuilder title(String title) {
            this.title = title;
            return this;
        }

        public OptionDialogBuilder optionType(int type) {
            this.optionType = type;
            return this;
        }

        public OptionDialogBuilder messageType(int type) {
            this.messageType = type;
            return this;
        }

        public OptionDialogBuilder icon(Icon icon) {
            this.icon = icon;
            return this;
        }

        public OptionDialogBuilder options(Object[] options) {
            this.options = options;
            return this;
        }

        public OptionDialogBuilder initialValue(Object initialValue) {
            this.initialValue = initialValue;
            return this;
        }

        public int show() {
            return JOptionPane.showOptionDialog(parentComponent, message, title, optionType, messageType, icon, options, initialValue);
        }


    }

    public static class JDialogBuilder {

        private Frame owner;
        private String title;
        private boolean modal;
        private Container contentPane;
        private boolean resizable;

        public JDialogBuilder() {
            this.owner = null;
            this.title = "";
            this.modal = false;
            this.contentPane = null;
            this.resizable = true;
        }

        public static JDialogBuilder builder() {
            return new JDialogBuilder();
        }

        public JDialogBuilder owner(Frame owner) {
            this.owner = owner;
            return this;
        }

        public JDialogBuilder title(String title) {
            this.title = title;
            return this;
        }

        public JDialogBuilder modal(boolean modal) {
            this.modal = modal;
            return this;
        }

        public JDialogBuilder contentPane(Container contentPane) {
            this.contentPane = contentPane;
            return this;
        }

        public JDialogBuilder resizable(boolean resizable) {
            this.resizable = resizable;
            return this;
        }

        public void show() {
            JDialog dialog = new JDialog(owner, title, modal);
            dialog.setContentPane(contentPane);
            dialog.pack();
            dialog.setLocationRelativeTo(owner);
            dialog.setResizable(resizable);
            dialog.setVisible(true);
        }

    }

}
