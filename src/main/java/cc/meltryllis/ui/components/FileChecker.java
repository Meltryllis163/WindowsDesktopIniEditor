package cc.meltryllis.ui.components;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;

/**
 * 该类除了作为 {@link FileFilter} 过滤器以外。
 * 还额外承担以下功能：
 * <p>
 * 1.额外存储用于生成 {@link LocaleLabel} 错误提示的文本与图标。
 * <p>
 * 2.检查来自 {@link LocaleFileChooserField} 的文本路径是否符合过滤器规则。
 *
 * @author Zachary W
 * @date 2024/12/29
 */
public abstract class FileChecker extends FileFilter {

    /** 检测通过 */
    public static final int PASS = 0;
    /** 空字符串 */
    public static final int EMPTY = 1;
    /** 不是文件夹 */
    public static final int NOT_FOLDER = 2;
    /** 不是ICO图标文件 */
    public static final int NOT_ICO = 3;

    public abstract Icon getErrorIcon();

    public abstract String getI18nErrorKey();

}
