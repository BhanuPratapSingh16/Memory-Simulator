package Main;
public class SecondaryMemory {
    private byte[] secondaryMemory;
    private int size;

    public SecondaryMemory(int size){
        this.size = size;
        secondaryMemory = new byte[size];
    }

    public byte read(int address){
        if(address >= 0 && address < size){
            return secondaryMemory[address];
        }
        throw new IllegalArgumentException("Invalid memory location : "+address);
    }

    public void write(int address, byte value){
        if(address >= 0 && address < size){
            secondaryMemory[address] = value;
            return;
        }
        throw new IllegalArgumentException("Invalid memory location : "+address);
    }
}
