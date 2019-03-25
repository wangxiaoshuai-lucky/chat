package cn.wzy.client;

import javax.swing.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

public class Client {

	private static ByteBuffer buffer = ByteBuffer.allocate(1024);
	private final static InetSocketAddress address = new InetSocketAddress("127.0.0.1", 7777);

	static private String rec(SocketChannel channel) throws IOException {
		buffer.clear();
		int count = channel.read(buffer);
		return new String(buffer.array(), 0, count, StandardCharsets.UTF_8);
	}

	static private void write(SocketChannel channel, String content) throws IOException {
		buffer.clear();
		buffer.put(content.getBytes(StandardCharsets.UTF_8));
		buffer.flip();
		channel.write(buffer);
	}

	public static volatile boolean success = false;

	private static volatile String name = "wzy";
	private static Selector selector;
	private static SocketChannel socketChannel;

	public static class MessageThread extends Thread{
		private JTextArea target;
		public MessageThread(JTextArea target){
			this.target = target;
		}
		@Override
		public void run() {
			SocketChannel client = null;
			try {
				while (true) {
					selector.select();
					Set<SelectionKey> set = selector.selectedKeys();
					Iterator<SelectionKey> iterator = set.iterator();
					while (iterator.hasNext()) {
						SelectionKey key = iterator.next();
						iterator.remove();
						if (key.isReadable()) {
							client = ((SocketChannel) key.channel());
							String msg = rec(client);
							if (msg.contains("hello")) {
								name = msg.substring(6);
								success = true;
							}
							target.append(msg + "\r\n");
							target.setCaretPosition(target.getText().length());
							key.interestOps(SelectionKey.OP_READ);
						}
					}
					set.clear();
				}
			} catch (Exception e) {
				if (client != null) {
					try {
						client.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		}
	}
	public static void start() throws IOException{
		socketChannel = SocketChannel.open(address);
		selector = Selector.open();
		socketChannel.configureBlocking(false);
		socketChannel.register(selector, SelectionKey.OP_READ);
	}
	public static void setName(String name) throws IOException {
		Client.name = name;
		write(socketChannel, name);
	}
	public static void sendMsg(String msg) throws IOException {
		write(socketChannel, name + "###" + msg);
	}
	public static void main(String[] args)  {

	}
}
