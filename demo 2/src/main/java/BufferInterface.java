public interface BufferInterface {
    void put(Integer item) throws InterruptedException;
    Integer take() throws InterruptedException;
    int size();
}