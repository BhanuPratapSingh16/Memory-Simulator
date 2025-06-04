package Main;
public class MainMemory {
    public byte[] memory;
    private int size;
    public MainMemory(int size){
        this.size = size;
        memory = new byte[size];
    }

    public byte read(int address){
        if(address >= 0 && address < size){
            return memory[address];
        }
        throw new IllegalArgumentException("Invalid memory location : "+address);
    }

    public void write(int address, byte value){
        if(address >= 0 && address < size){
            memory[address] = value;
            return;
        }
        throw new IllegalArgumentException("Invalid memory location : "+address);
    }
}
