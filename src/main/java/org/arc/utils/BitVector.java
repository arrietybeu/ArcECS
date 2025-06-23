package org.arc.utils;

/**
 * <p>Cấu trúc dữ liệu BitVector dùng để đại diện cho tổ hợp component của một entity.</p>
 * <p>Hỗ trợ các thao tác logic nhanh như AND, OR, XOR, và được tối ưu hóa cho ECS backend.</p>
 *
 * @author Arriety
 */
public class BitVector {

    long[] words = {0};

    public BitVector() {}

    public void unsafeClear(int index) {
        words[index >>> 6] &= ~(1L << index);
    }

}
