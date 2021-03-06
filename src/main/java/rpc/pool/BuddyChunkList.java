package rpc.pool;

public class BuddyChunkList {
    private PoolArea poolArea;
    BuddyChunkList nextList;
    BuddyChunkList preList;
    private final int minUseRate;
    private final int maxUseRate;

    private BuddyChunk head;

    public BuddyChunkList(PoolArea poolArea, int minUseRate, int maxUseRate) {
        this.poolArea = poolArea;
        this.minUseRate = minUseRate;
        this.maxUseRate = maxUseRate;
    }

    public boolean allocate(ByteBuff buff, int reqCapacity, int normCapacity) {
        if (head == null) {
            return false;
        }
        for (BuddyChunk cur = head; cur != null; ) {
            boolean rst = poolArea.allocateFromChunk(cur, buff, reqCapacity, normCapacity);
            if (rst) {
                checkUsageAndMove(cur);
                return true;
            } else {
                cur = cur.nextChunk;
            }
        }
        return false;
    }

    /**
     * check chunk usage, and move chunk between chunkList.
     * @param chunk buddyChunk
     */
    private void checkUsageAndMove(BuddyChunk chunk) {
        int usage = chunk.usage();
        if (usage >= maxUseRate) {
            this.removeChunk(chunk);
            nextList.addChunk(chunk);
            return;
        }
        if (usage < minUseRate) {
            this.removeChunk(chunk);
            preList.addChunk(chunk);
        }
    }

    void removeChunk(BuddyChunk chunk) {
        if (chunk == head) {
            head = chunk.nextChunk;
            head.preChunk = null;
        } else {
            BuddyChunk pre = chunk.preChunk;
            pre.nextChunk = chunk.nextChunk;
            if (chunk.nextChunk != null) {
                chunk.nextChunk.preChunk = pre;
            }
        }
        chunk.preChunk = null;
        chunk.nextChunk = null;
    }

    void addChunk(BuddyChunk chunk) {
        if (head == null) {
            head = chunk;
        } else {
            chunk.nextChunk = head.nextChunk;
            head.nextChunk = chunk;
            chunk.preChunk = head;
            if (chunk.nextChunk != null) {
                chunk.nextChunk.preChunk = chunk;
            }
        }
    }
}
