package Virtual_Memory;

import java.util.HashMap;
import java.util.Map;

public class PageTable {
    private final int PAGE_SIZE;
    private final int totalPages;
    private Map<Integer, PageTableEntry> pageTable = new HashMap<>(); // vpn -> PageTableEntry

    PageTable(int vmSize, int pageSize){
        this.PAGE_SIZE = pageSize;
        this.totalPages = vmSize / this.PAGE_SIZE;
    }

    void setEntry(int pid, int vpn, int frameNumber){
        // if(pageTable.containsKey(vpn)){
        //     throw new RuntimeException("Page already mapped; Page number : "+vpn);
        // }
        if(vpn < 0 || vpn >= totalPages){
            throw new IllegalArgumentException("Cannot access virtual page number : "+vpn);
        }
        PageTableEntry entry = new PageTableEntry(pid, vpn, frameNumber);
        pageTable.put(vpn, entry);
    }

    PageTableEntry getEntry(int vpn){
        return pageTable.get(vpn);
    }

    boolean isValid(int vpn){
        PageTableEntry entry = pageTable.get(vpn);
        return entry != null && entry.valid;
    }

    void removeEntry(int vpn){
        if(pageTable.containsKey(vpn))
            pageTable.remove(vpn);
        else
            throw new RuntimeException("Invalid virtual page number : "+vpn);
    }

    void invalidate(int vpn){
        PageTableEntry ptEntry = pageTable.get(vpn);
        if(ptEntry != null){
            ptEntry.valid = false;
            ptEntry.frameNumber = -1;
        }
    }
    
    int getTotalPages(){
        return totalPages;
    }
}


class PageTableEntry{
    int pid;
    int vpn;
    int frameNumber;
    boolean valid;
    
    PageTableEntry(int pid, int vpn, int frameNumber){
        this.pid = pid;
        this.vpn = vpn;
        this.frameNumber = frameNumber;
        this.valid = true;
    }
}