package cc.meltryllis.ui.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 统一管理自定义事件。
 *
 * @author Zachary W
 * @date 2024/12/31
 */
public class CustomEventManager {

    private static CustomEventManager manager;
    private final List<LocaleListener> localeListeners;

    private final List<FolderChangeListener> folderChangeListeners;

    public CustomEventManager() {
        this.localeListeners = new ArrayList<>();
        this.folderChangeListeners = new ArrayList<>();
    }

    public static CustomEventManager getInstance() {
        if (manager == null) {
            manager = new CustomEventManager();
        }
        return manager;
    }

    public void addLocaleListener(LocaleListener l) {
        if (l == null || localeListeners.contains(l)) {
            return;
        }
        localeListeners.add(l);
    }

    public void fireLocaleChanged(Locale locale) {
        for (LocaleListener localeListener : localeListeners) {
            localeListener.localeChanged(locale);
        }
    }

    public void addFolderChangeListener(FolderChangeListener l) {
        if (l == null || folderChangeListeners.contains(l)) {
            return;
        }
        folderChangeListeners.add(l);
    }

    public void fireFolderChanged() {
        for (FolderChangeListener folderChangeListener : folderChangeListeners) {
            folderChangeListener.folderChanged();
        }
    }

}
