package cn.wzy.view;

import cn.wzy.client.Client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main {
	public static void main(String[] args) throws InterruptedException {
		JFrame frame = new JFrame("ChatRoom");
		frame.setSize(500, 500);
		frame.setLocation(500, 500);
		frame.setContentPane(new JPanel());
		frame.getContentPane().setLayout(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JTextArea board = new JTextArea("");
		JScrollPane js = new JScrollPane(board);
		js.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		js.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		js.setBounds(50, 50, 400, 300);
		frame.getContentPane().add(js);

		JTextArea pf = new JTextArea("");
		JScrollPane js2 = new JScrollPane(pf);
		js2.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		js2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		js2.setBounds(50, 350, 250, 100);
		frame.getContentPane().add(js2);

		JButton b = new JButton("发送");
		b.setBounds(300, 375, 100, 50);
		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (Client.success) {
						Client.sendMsg(pf.getText());
						board.append("你说：" + pf.getText() + "\r\n");
						board.setCaretPosition(board.getText().length());
					} else {
						Client.setName(pf.getText());
					}
				} catch (Exception ee) {
					board.append("服务器错误！即将关闭程序.....");
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					System.exit(-1);
				}
			}
		});
		frame.getContentPane().add(b);
		frame.setVisible(true);
		try {
			Client.start();
			new Client.MessageThread(board).start();
		} catch (Exception e) {
			board.append("服务器错误！即将关闭程序.....");
			Thread.sleep(1000);
			System.exit(-1);
		}
	}
}
