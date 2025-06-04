package Virtual_Memory;

public interface PageReplacementPolicy {
    int chooseEvictFrame(FrameTable frameTable);
}   


class LRUReplacementPolicy implements PageReplacementPolicy{
    public int chooseEvictFrame(FrameTable frameTable){
        int frameNumber = 0;
        int max = frameTable.getEntry(frameNumber).lastAccessedTime;
        for(int i=1;i<frameTable.getTotalFrames();i++){
            int accessedTime = frameTable.getEntry(i).lastAccessedTime;
            if(accessedTime > max){
                max = accessedTime;
                frameNumber = i;
            }
        }
        return frameNumber;
    }
}