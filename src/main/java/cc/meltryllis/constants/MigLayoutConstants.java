package cc.meltryllis.constants;

import net.miginfocom.layout.CC;

/**
 * {@link net.miginfocom.swing.MigLayout} 布局相关的常量。
 *
 * @author Zachary W
 * @date 2024/12/27
 */
public interface MigLayoutConstants {

    static CC cell(int row, int column) {
        return new CC().cell(row, column);
    }

}
