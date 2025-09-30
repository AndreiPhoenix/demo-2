import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BoundedBufferTest {

    @Test
    public void testBufferCreation() {
        BoundedBuffer<Integer> buffer = new BoundedBuffer<>(5);
        assertTrue(buffer.isEmpty());
        assertFalse(buffer.isFull());
    }

    @Test
    public void testPutAndTake() throws InterruptedException {
        BoundedBuffer<Integer> buffer = new BoundedBuffer<>(3);

        buffer.put(1);
        buffer.put(2);
        buffer.put(3);

        assertTrue(buffer.isFull());

        assertEquals(1, buffer.take());
        assertEquals(2, buffer.take());
        assertEquals(3, buffer.take());

        assertTrue(buffer.isEmpty());
    }

    @Test
    public void testBufferFull() throws InterruptedException {
        BoundedBuffer<Integer> buffer = new BoundedBuffer<>(2);

        buffer.put(1);
        buffer.put(2);

        assertTrue(buffer.isFull());
        assertEquals(2, buffer.size());
    }
}