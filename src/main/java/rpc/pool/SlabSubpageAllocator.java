package rpc.pool;

public class SlabSubpageAllocator {
    private int maxEleNum;
    private int bitMapLength;
    private long[] bitMap; // 0 - idle, 1 - used

    public SlabSubpageAllocator(int maxElemNum) {
        assert maxElemNum > 0;
        this.maxEleNum = maxElemNum;
        bitMapLength = (maxElemNum + 63) >>> 6;
        bitMap = new long[bitMapLength];
    }

    int obtainIdelPosition() {
        for (int i = 0; i < bitMapLength; i++) {
            if (~bitMap[i] != 0) {
                int pos = obtainIdelPosition(bitMap[i]);
                if (pos == -1) {
                    return -1;
                }
                bitMap[i] |= (1L << pos); // set idle state to used
                return i * 64 + pos;
            }
        }
        return -1;
    }

    private int obtainIdelPosition(long state) {
        for (int j = 0; j < 64; j++) {
            if ((state & 1) == 0) {
                return j;
            }
            state >>>= 1;
        }
        return -1;
    }

    void free(int pos) {
        assert (pos > 0) && (pos < maxEleNum);
        int i = pos / 64;
        int j = pos % 64;
        bitMap[i] &= (~(1L << j));
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < maxEleNum; i++) {
            int j = i / 64;
            int k = i % 64;
            sb.append((bitMap[j] >> k) & 1L).append(' ');
        }
        return sb.toString();
    }
}
