package Virtual_Memory;

import java.util.HashMap;
import java.util.Map;

import Cache.CacheManager;
import Main.MainMemory;
import Main.SecondaryMemory;

public class VirtualMemoryManager {
    private final int MM_SIZE; 
    private final int VM_SIZE;
    private final int PAGE_SIZE;
    private final Map<Integer, PageTable> pageTables = new HashMap<>();  // process id -> pagetable
    private final FrameTable frameTable;
    private final PageReplacementPolicy replacementPolicy;
    private final MainMemory mm;
    private final SecondaryMemory sm;
    public final CacheManager cacheManager;
    private int pageFaultCount = 0;

    public VirtualMemoryManager(int mmSize, int vmSize, int pageSize, MainMemory mm, SecondaryMemory sm, int cacheSize, int lineSize){
        this.MM_SIZE = mmSize;
        this.VM_SIZE = vmSize;
        this.PAGE_SIZE = pageSize;
        this.frameTable = new FrameTable(this.MM_SIZE, pageSize);
        this.replacementPolicy = new LRUReplacementPolicy();
        this.mm = mm;
        this.sm = sm;
        this.cacheManager = new CacheManager(cacheSize, lineSize);
    }

    public void createProcess(int pid){
        PageTable pt = new PageTable(VM_SIZE, PAGE_SIZE);
        pageTables.putIfAbsent(pid, pt);
    }

    public byte read(int pid, int virtualAddress){
        PageTable pt = pageTables.get(pid);
        if(pt == null){
            throw new RuntimeException("Cannot find process id : "+pid);
        }

        int vpn = virtualAddress / PAGE_SIZE;
        int offset = virtualAddress % PAGE_SIZE;
        PageTableEntry ptEntry = pt.getEntry(vpn);

        if(pt.isValid(vpn)){
            int frameNumber = ptEntry.frameNumber;
            FrameTableEntry ftEntry = frameTable.getEntry(frameNumber);
            ftEntry.lastAccessedTime = 0;
            frameTable.updateAccessTime(frameNumber);
            return cacheManager.read(frameNumber * PAGE_SIZE + offset, mm);
        }
        else{
            handlePageFault(pid, vpn);
            return read(pid, virtualAddress);
        }
    }

    public void write(int pid, int virtualAddress, byte value){
        PageTable pt = pageTables.get(pid);
        if(pt == null){
            throw new RuntimeException("Cannot find process id : "+pid);
        }

        int vpn = virtualAddress / PAGE_SIZE;
        int offset = virtualAddress % PAGE_SIZE;
        PageTableEntry ptEntry = pt.getEntry(vpn);

        if(pt.isValid(vpn)){
            int frameNumber = ptEntry.frameNumber;
            FrameTableEntry ftEntry = frameTable.getEntry(frameNumber);
            ftEntry.modified = true;
            ftEntry.lastAccessedTime = 0;
            ftEntry.free = false;
            frameTable.updateAccessTime(frameNumber);
            cacheManager.write(frameNumber * PAGE_SIZE + offset, mm, value);
        }
        else{
            handlePageFault(pid, vpn);
            write(pid, virtualAddress, value);
        }
    }

    void handlePageFault(int pid, int vpn){
        pageFaultCount++;
        PageTable pt = pageTables.get(pid);
        int frameNumber = frameTable.getFreeFrame();
        if(frameNumber == -1){
            frameNumber = replacementPolicy.chooseEvictFrame(frameTable);
            FrameTableEntry evictedEntry = frameTable.getEntry(frameNumber);
            pageTables.get(evictedEntry.pid).invalidate(evictedEntry.vpn);
        }
        FrameTableEntry entry = frameTable.getEntry(frameNumber);
        if(entry.modified){
            int address = entry.vpn * PAGE_SIZE;
            for(int i=0;i<PAGE_SIZE;i++){
                sm.write(address+i, mm.read(frameNumber*PAGE_SIZE+i));
            }
        }
        int address = vpn * PAGE_SIZE;
        for(int i=0;i<PAGE_SIZE;i++){
            mm.write(frameNumber*PAGE_SIZE +i, sm.read(address+i));
        }

        frameTable.setEntry(frameNumber, pid, vpn);
        pt.setEntry(pid, vpn, frameNumber);
    }
    
    public void printFrameTable(){
        System.out.println("\nFRAME TABLE :- ");
        System.out.printf("%-12s %-12s %-22s %-8s %-10s%n", "Frame No", "Process ID", "Virtual Page Number", "Free", "Modified");
        for(int i=0;i<frameTable.getTotalFrames();i++){
            FrameTableEntry ftEntry = frameTable.getEntry(i);
            System.out.printf("%-12d %-12d %-22d %-8b %-10b%n", i, ftEntry.pid, ftEntry.vpn, ftEntry.free, ftEntry.modified);
        }
    }

    public void printPageTable(int pid){
        System.out.println("\nPAGE TABLE :-");
        PageTable pt = pageTables.get(pid);
        if(pt == null){
            throw new RuntimeException("Cannot find process id : "+pid);
        }
        System.out.printf("%-12s %-20s %-15s %-10s%n","Process ID", "Virtual Page Number", "Frame Number", "Valid");
        for(int i=0;i<pt.getTotalPages();i++){
            PageTableEntry ptEntry = pt.getEntry(i);
            if(ptEntry != null){
                System.out.printf("%-12d %-20d %-15d %-10b%n", pid, i, ptEntry.frameNumber, ptEntry.valid);
            }
        }
    }

    public void printStats(){
        System.out.println("Page fault count : "+pageFaultCount);
    }
}
