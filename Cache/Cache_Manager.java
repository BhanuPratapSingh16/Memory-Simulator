package Cache;

import Main.MainMemory;

public class CacheManager {
    private final CacheLine[] cache;
    private final int CACHE_SIZE;
    private final int LINE_SIZE;
    private final int NUM_LINES;

    private int hits = 0;
    private int misses = 0;

    private final ReplacementPolicy replacementPolicy; 

    public CacheManager(int cacheSize, int lineSize){
        this.CACHE_SIZE = cacheSize;
        this.LINE_SIZE = lineSize;
        this.NUM_LINES = this.CACHE_SIZE / this.LINE_SIZE;
        cache= new CacheLine[this.NUM_LINES];
        for(int i=0;i<NUM_LINES;i++){
            cache[i] = new CacheLine(this.LINE_SIZE);
        }
        replacementPolicy = new FIFOReplacementPolicy();
    }

    public byte read(int address, MainMemory mm){
        int tag = address / LINE_SIZE;
        int offset = address % LINE_SIZE;

        for(CacheLine cacheLine:cache){
            if(cacheLine.valid && cacheLine.tag == tag){
                hits++;
                return cacheLine.data[offset];
            }
        }
        CacheLine cacheLine = replacementPolicy.chooseEvictLine(cache);
        if(cacheLine.modified){
            int add = cacheLine.tag * LINE_SIZE;
            System.arraycopy(cacheLine.data, 0, mm.memory, add, LINE_SIZE);
        }
        misses++;
        cacheLine.tag = tag;
        cacheLine.valid = true;
        cacheLine.modified = false;
        cacheLine.entryTime = 0;
        updateAccessTime(cacheLine);
        System.arraycopy(mm.memory, address-offset, cacheLine.data, 0, LINE_SIZE);
        return cacheLine.data[offset];
    }

    public void write(int address, MainMemory mm, byte value){
        int tag = address / LINE_SIZE;
        int offset = address % LINE_SIZE;

        for(CacheLine cacheLine:cache){
            if(cacheLine.valid && cacheLine.tag == tag){
                hits++;
                cacheLine.data[offset] = value;
                mm.memory[address] = value;
                cacheLine.modified = true;
                return;
            }
        }
        CacheLine cacheLine = replacementPolicy.chooseEvictLine(cache);
        if(cacheLine.modified){
            int add = cacheLine.tag * LINE_SIZE;
            System.arraycopy(cacheLine.data, 0, mm.memory, add, LINE_SIZE);
        }
        misses++;
        cacheLine.tag = tag;
        cacheLine.valid = true;
        cacheLine.entryTime = 0;
        updateAccessTime(cacheLine);
        System.arraycopy(mm.memory, address - offset, cacheLine.data, 0, LINE_SIZE);
        cacheLine.data[offset] = value;
        mm.memory[address] = value;
    }

    private void updateAccessTime(CacheLine cacheLine){
        for(CacheLine line : cache){
            if(!line.equals(cacheLine)){
                line.entryTime++;
            }
        }
    }

    public void printCache(){
        System.out.println("\nCACHE DATA :- ");
        for(int i=0;i<NUM_LINES;i++){
            CacheLine line = cache[i];
            if(line.valid){
                for(byte b : line.data){
                    System.out.printf("%-6d", b);
                }
                System.out.println();
            }
        }
    }

    public void printStats(){
        System.out.println("Cache Hits: " + hits);
        System.out.println("Cache Misses: " + misses);
        double hitRate = (hits + misses) == 0 ? 0 : (100.0 * hits) / (hits + misses);
        System.out.println("Hit Rate: "+hitRate);
    }
}


class CacheLine{
    int tag;
    boolean valid;
    boolean modified;
    byte[] data;
    int entryTime;

    CacheLine(int lineSize){
        this.tag = -1;
        this.valid = false;
        this.modified = false;
        this.data = new byte[lineSize];
    }
}
