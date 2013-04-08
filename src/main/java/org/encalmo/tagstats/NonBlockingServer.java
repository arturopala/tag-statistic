package org.encalmo.tagstats;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;


public class NonBlockingServer extends SingleThreadService {
  private final InetSocketAddress address;
  private final ServerSocketListener listener;
  private ServerSocketChannel serverSocketChannel;
  private Selector selector;
  private SelectionKey selectionKey;

  public NonBlockingServer(InetSocketAddress address, ServerSocketListener listener) {
    this.address = address;
    this.listener = listener;
  }

  @Override
  protected void init() throws Exception {
    connect(address);
  }

  @Override
  protected void service() throws Exception {
    listen(selector);
  }

  @Override
  protected void destroy() throws Exception {
    selectionKey.cancel();
    selector.close();
    serverSocketChannel.close();
    selector = null;
    serverSocketChannel = null;
  }

  private void connect(InetSocketAddress address) throws IOException {
    serverSocketChannel = ServerSocketChannel.open();
    serverSocketChannel.configureBlocking(false);
    serverSocketChannel.bind(address);
    selector = Selector.open();
    selectionKey = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    System.out.println("server listening at " + address);
  }

  private void listen(Selector selector) throws IOException {
    while (!Thread.interrupted() && selector.isOpen()) {
      int count = selector.select(500);
      if (count == 0) {
        continue;
      }

      Iterator<SelectionKey> it = selector.selectedKeys().iterator();
      while (it.hasNext()) {
        final SelectionKey key = it.next();
        it.remove();
        if (!key.isValid()) {
          continue;
        }
        if (key.isAcceptable()) {
          accept(key, selector);
          continue;
        }
        if (key.isReadable()) {
          try {
            listener.read(key);
          } catch (Exception e) {
            e.printStackTrace();
          }
          continue;
        }
        if (key.isWritable()) {
          try {
            listener.write(key);
          } catch (Exception e) {
            e.printStackTrace();
          }
          continue;
        }
      }
    }
  }

  private void accept(SelectionKey key, Selector selector) throws IOException {
    ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
    SocketChannel client = serverSocketChannel.accept();
    client.configureBlocking(false);
    client.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
  }

  /*private void read(SelectionKey key) throws IOException {
      SocketChannel channel = (SocketChannel) key.channel();
      ByteBuffer buffer = ByteBuffer.allocate(8192);
      int numRead = -1;
      try {
          numRead = channel.read(buffer);
      }
      catch (IOException e) {
          e.printStackTrace();
      }
      if (numRead == -1) {
          channel.close();
          key.cancel();
          return;
      } else {
          byte[] data = new byte[numRead];
          System.arraycopy(buffer.array(), 0, data, 0, numRead);
          key.interestOps(SelectionKey.OP_WRITE);
      }
  }*/

  /*private void write(SelectionKey key) throws IOException {
      SocketChannel channel = (SocketChannel) key.channel();
      byte[] item = new byte[10];
      channel.write(ByteBuffer.wrap(item));
      key.interestOps(SelectionKey.OP_READ);
  }*/
}
