package cc.meltryllis.ui.basic;

import cc.meltryllis.constants.I18nConstants;
import cc.meltryllis.ui.event.LocaleListener;

import javax.swing.*;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * 具有实时语言切换功能的 {@link JLabel}。
 *
 * @author Zachary W
 * @date 2024/12/28
 */
public class LocaleLabel extends JLabel implements LocaleListener {

    private String localeTextKey;

    public LocaleLabel(String localeTextKey) {
        this(localeTextKey, null, LEADING);
    }

    public LocaleLabel(String localeTextKey, Icon icon) {
        this(localeTextKey, icon, LEADING);
    }

    public LocaleLabel(String key, Icon icon, int horizontalAlignment) {
        this.localeTextKey = key;
        ResourceBundle bundle = ResourceBundle.getBundle(I18nConstants.BASE_NAME);
        setText(bundle.getString(localeTextKey));
        setIcon(icon);
        setHorizontalAlignment(horizontalAlignment);
    }

    public void setLocaleTextKey(String localeTextKey) {
        this.localeTextKey = localeTextKey;
        setText(ResourceBundle.getBundle(I18nConstants.BASE_NAME).getString(localeTextKey));
    }

    @Override
    public void localeChanged(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(I18nConstants.BASE_NAME);
        setText(bundle.getString(localeTextKey));
    }

}
