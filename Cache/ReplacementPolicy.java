package Cache;

public interface ReplacementPolicy {
    CacheLine chooseEvictLine(CacheLine[] cache);
}


class FIFOReplacementPolicy implements ReplacementPolicy{
    public CacheLine chooseEvictLine(CacheLine[] cache){
        for(CacheLine cacheLine:cache){
            if(!cacheLine.valid){
                return cacheLine;
            }
        }
        CacheLine line = cache[0];
        int max = cache[0].entryTime;
        for(CacheLine cacheLine:cache){
            if(cacheLine.entryTime > max){
                max = cacheLine.entryTime;
                line = cacheLine;
            }
        }
        return line;
    }
}