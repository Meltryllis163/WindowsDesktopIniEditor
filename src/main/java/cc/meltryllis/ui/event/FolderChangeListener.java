package cc.meltryllis.ui.event;

import java.util.EventListener;

/**
 * 监听用户文件夹输入改变事件。
 *
 * @author Zachary W
 * @date 2024/12/31
 */
public interface FolderChangeListener extends EventListener {

    void folderChanged();

}
