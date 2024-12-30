package cc.meltryllis.ui.event;

import java.util.EventListener;
import java.util.Locale;

/**
 * 国际化和语言监听器。
 *
 * @author Zachary W
 * @date 2024/12/26
 */
public interface LocaleListener extends EventListener {

    /**
     * 当界面语言发生改变。
     */
    void localeChanged(Locale locale);

}
