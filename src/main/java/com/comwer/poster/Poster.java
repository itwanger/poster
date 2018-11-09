package com.comwer.poster;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Poster {
	private static Logger logger = LoggerFactory.getLogger(Poster.class);
	
	private JFrame frame;
	private JTextField img_input;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Poster window = new Poster();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Poster() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 663, 509);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JLabel label = new JLabel("中文(标题)");
		label.setBounds(10, 115, 114, 15);
		frame.getContentPane().add(label);

		JLabel label_1 = new JLabel("英文(内容)");
		label_1.setBounds(10, 249, 114, 15);
		frame.getContentPane().add(label_1);

		JTextArea zh_text = new JTextArea();
		zh_text.setText("沉默王二");
		zh_text.setLineWrap(true);
		zh_text.setBounds(136, 6, 501, 183);
		frame.getContentPane().add(zh_text);

		JTextArea en_text = new JTextArea();
		en_text.setText("");
		en_text.setLineWrap(true);
		en_text.setBounds(136, 205, 501, 101);
		frame.getContentPane().add(en_text);

		JLabel label_2 = new JLabel("图片路径(备注)");
		label_2.setBounds(10, 323, 114, 15);
		frame.getContentPane().add(label_2);

		img_input = new JTextField();
		img_input.setBounds(136, 319, 501, 21);
		frame.getContentPane().add(img_input);
		img_input.setColumns(10);

		JTextArea poster_path = new JTextArea();
		poster_path.setLineWrap(true);
		poster_path.setBounds(79, 387, 558, 71);
		frame.getContentPane().add(poster_path);

		JLabel lblNewLabel = new JLabel("海报路径");
		lblNewLabel.setBounds(10, 406, 54, 15);
		frame.getContentPane().add(lblNewLabel);
		
		JButton button = new JButton("生成海报1");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String zh = zh_text.getText();
					if (StringUtils.isEmpty(zh)) {
						throw new Exception("中文不能为空");
					}
					String en = en_text.getText();
					String imgURL = img_input.getText();

					// 获取词霸的图片
					if (StringUtils.isEmpty(imgURL)) {
						String formatDate = DateFormatUtils.format(new Date(), "yyyyMMdd");
						imgURL = "http://cdn.iciba.com/news/word/big_" + formatDate + "b.jpg";
					}

					String filePath = QrcodeUtils.createQrcodeFile(zh, en, imgURL, 1);
					poster_path.setText(filePath);
				} catch (Exception e1) {
					logger.error(e1.getMessage());
					logger.error(e1.getMessage(), e1);
					
					poster_path.setText(e1.getMessage());
				}
			}
		});
		button.setBounds(79, 352, 93, 23);
		frame.getContentPane().add(button);
		
		JButton button_1 = new JButton("生成海报2");
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String title = zh_text.getText();
					if (StringUtils.isEmpty(title)) {
						throw new Exception("标题不能为空");
					}
					
					if (title.length() > 4) {
						throw new Exception("建议标题不超过4个字");
					}
					
					String content = en_text.getText();
					if (StringUtils.isEmpty(content)) {
						throw new Exception("内容不能为空");
					}
					
					String memo = img_input.getText();

					// 获取词霸的图片
					if (StringUtils.isEmpty(memo)) {
						throw new Exception("备注不能为空");
					}

					String filePath = QrcodeUtils.createPoster2File(title, content, memo);
					poster_path.setText(filePath);
				} catch (Exception e1) {
					logger.error(e1.getMessage());
					logger.error(e1.getMessage(), e1);
					
					poster_path.setText(e1.getMessage());
				}
			}
		});
		button_1.setBounds(201, 349, 117, 29);
		frame.getContentPane().add(button_1);
		
		JButton button_2 = new JButton("生成海报3");
		button_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String title = zh_text.getText();
					if (StringUtils.isEmpty(title)) {
						throw new Exception("标题不能为空");
					}
					
					String content = en_text.getText();
					if (StringUtils.isEmpty(content)) {
						throw new Exception("内容不能为空");
					}
					
					String imgURL = img_input.getText();
					// 获取词霸的图片
					if (StringUtils.isEmpty(imgURL)) {
						String formatDate = DateFormatUtils.format(new Date(), "yyyyMMdd");
						imgURL = "http://cdn.iciba.com/news/word/big_" + formatDate + "b.jpg";
					}

					String filePath = QrcodeUtils.createQrcodeFile(title, content, imgURL, 3);
					poster_path.setText(filePath);
				} catch (Exception e1) {
					logger.error(e1.getMessage());
					logger.error(e1.getMessage(), e1);
					
					poster_path.setText(e1.getMessage());
				}
			}
		});
		button_2.setBounds(328, 349, 117, 29);
		frame.getContentPane().add(button_2);
	}
}
