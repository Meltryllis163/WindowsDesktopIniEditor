package cc.meltryllis.utils;

import cc.meltryllis.ui.event.CustomEventManager;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * 国际化工具类。
 *
 * @author Zachary W
 * @date 2025/1/7
 */
public class I18nUtil {

    public static final String BASE_NAME = "i18n/message";

    private static ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME);

    public static void updateLocale(Locale locale) {
        if (locale == null || Locale.getDefault().equals(locale)) {
            return;
        }
        Locale.setDefault(locale);
        ResourceBundle.clearCache();
        bundle = ResourceBundle.getBundle(BASE_NAME);
        CustomEventManager.getInstance().fireLocaleChanged(locale);
    }

    public static String getString(String key) {
        return bundle.getString(key);
    }

}
