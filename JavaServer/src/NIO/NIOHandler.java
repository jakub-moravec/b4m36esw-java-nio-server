package NIO;

import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;

abstract class NIOHandler implements Runnable {
    private final SelectableChannel channel;
    private final int initialSelectableOps;
    private SelectionKey selectionKey = null;

    public NIOHandler(SelectableChannel channel, int initialSelectableOps) {
        this.channel = channel;
        this.initialSelectableOps = initialSelectableOps;
    }

    SelectableChannel getSelectableChannel() {
        return channel;
    }

    int getInitialSelectableOps() {
        return initialSelectableOps;
    }

    SelectionKey getSelectionKey() {
        return selectionKey;
    }

    void setSelectionKey(SelectionKey selectionKey) {
        this.selectionKey = selectionKey;
    }

}
