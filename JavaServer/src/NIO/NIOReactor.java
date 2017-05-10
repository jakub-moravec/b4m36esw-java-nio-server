package NIO;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.concurrent.ConcurrentLinkedQueue;

public class NIOReactor implements Runnable{
    private final Selector selector = Selector.open();
    private final ConcurrentLinkedQueue<NIOHandler> toRegister = new ConcurrentLinkedQueue<NIOHandler>();
    public NIOReactor() throws IOException {
        Thread t = new Thread(this);
//        t.setDaemon(true);
        t.start();
    }
    void register(NIOHandler target){
        toRegister.add(target);
        selector.wakeup();
    }

    @Override
    public void run() {
        try {
            while (true){
                selector.select();
                for (SelectionKey key: selector.selectedKeys()){
                    if (key.attachment()!=null)((NIOHandler)key.attachment()).run();
                }
                selector.selectedKeys().clear();
                NIOHandler handler;
                while ((handler=toRegister.poll())!=null){
                    handler.setSelectionKey(handler.getSelectableChannel().register(selector, handler.getInitialSelectableOps(), handler));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
