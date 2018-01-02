public class OpcodeTest {

    private Object mutext = new Object();

    private int a;

    public void inc(){
        synchronized (mutext){
            a ++;
        }
    }

    public int get(){
        return a;
    }
}
