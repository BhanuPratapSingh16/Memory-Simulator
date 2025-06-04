import Main.MainMemory;
import Main.SecondaryMemory;
import Virtual_Memory.VirtualMemoryManager;

public class Main {
    public static void main(String[] args) {
        int MM_SIZE = 1024;
        int VM_SIZE = 2048;
        int SM_SIZE = 4096;
        int PAGE_SIZE = 128;
        int CACHE_SIZE = 256;
        int LINE_SIZE = 16;
        MainMemory mm = new MainMemory(MM_SIZE);
        SecondaryMemory sm = new SecondaryMemory(SM_SIZE);
        VirtualMemoryManager vm = new VirtualMemoryManager(MM_SIZE, VM_SIZE, PAGE_SIZE, mm,  sm, CACHE_SIZE, LINE_SIZE);
        vm.createProcess(1);
        vm.createProcess(2);
        for(int i=0;i<SM_SIZE;i++){
            sm.write(i, (byte)i);
        }
        for(int i=0;i<MM_SIZE+PAGE_SIZE;i++){
            vm.read(1, i);
        }
        vm.write(1, 2, (byte)21);
        for(int i=0;i<PAGE_SIZE;i++){
            vm.read(2, i);
        }
        vm.printFrameTable();
        vm.printPageTable(1);
        vm.printPageTable(2);
        vm.cacheManager.printCache();
        vm.cacheManager.printStats();
        vm.printStats();
    }
}
