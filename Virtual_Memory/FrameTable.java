package Virtual_Memory;

import java.util.ArrayList;
import java.util.List;

public class FrameTable {
    private final int FRAME_SIZE;
    private final int totalFrames;
    final List<FrameTableEntry> ftEntries = new ArrayList<>();
    
    FrameTable(int memorySize, int frameSize){
        this.FRAME_SIZE = frameSize;
        this.totalFrames = memorySize / this.FRAME_SIZE;
        for(int i=0;i<totalFrames;i++){
            ftEntries.add(new FrameTableEntry(i, true));
        }
    }

    void setEntry(int frameNumber, int pid, int vpn){
        if(frameNumber < 0 || frameNumber >= totalFrames){
            throw new IllegalArgumentException("Invalid frame number : "+frameNumber);
        }
        FrameTableEntry entry = ftEntries.get(frameNumber);
        entry.pid = pid;
        entry.vpn = vpn;
        entry.free = false;
        entry.lastAccessedTime = 0;
        entry.modified = false;
        updateAccessTime(frameNumber);
    }

    FrameTableEntry getEntry(int frameNumber){
        if(frameNumber < 0 || frameNumber >= totalFrames){
            throw new IllegalArgumentException("Invalid frame number : "+frameNumber);
        }
        FrameTableEntry entry = ftEntries.get(frameNumber);
        return entry;
    }

    void updateAccessTime(int frameNumber){
        for(int i=0;i<totalFrames;i++){
            if(i != frameNumber){
                ftEntries.get(i).lastAccessedTime++;
            }
        }
    }

    int getFreeFrame(){
        for(FrameTableEntry entry: ftEntries){
            if(entry.free){
                return entry.frameNumber;
            }
        }
        return -1;
    }

    int getTotalFrames(){
        return totalFrames;
    }
}


class FrameTableEntry{
    int pid;
    int vpn;
    int frameNumber;
    boolean free;
    boolean modified;
    int lastAccessedTime;

    FrameTableEntry(int frameNumber, boolean free){
        this.frameNumber = frameNumber;
        this.free = free;
        this.modified = false;
        this.lastAccessedTime = 0;
        this.pid = -1;
        this.vpn = -1;
    }
}