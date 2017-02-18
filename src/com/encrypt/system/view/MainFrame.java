package com.encrypt.system.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileSystemView;
import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;
import org.jb2011.lnf.beautyeye.widget.ImageBgPanel;

import com.encrypt.system.algorithm.EncryptFactory;
import com.encrypt.system.algorithm.FileEncryptFactory;

import javafx.scene.image.Image;

public class MainFrame extends JFrame {

	public static final String Application = "File Encrypt System";

	public static final int DEFAULT_WIDTH = 1100;
	public static final int DEFAULT_HEIGHT = 700;

	public static final String[] tabStrs = { "文件校验", "文件加密", "文件解密" };

	private JTabbedPane tabbedPane;
	private JPanel[] panels = new JPanel[3];

	public MainFrame() {
		setTitle(Application);
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		initComponent();

		initPage1();
		initPage2();
		initPage3();
	}
	
	private static String algorithm = FileEncryptFactory.DES_ALGORITHM;
	
	private String line = "--------------------------------------------------------------------------------";
	private String newLine = "\n";
	
	private void initPage3() {
		JPanel north = new JPanel(new GridLayout(5, 1, 10, 10));
		panels[2].add(north, BorderLayout.NORTH);
		
		JPanel top_1 = new JPanel(new BorderLayout(10,10));
		
		JLabel tip = new JLabel("文件路径: ");
		top_1.add(tip, BorderLayout.WEST);

		JTextField filePath = new JTextField("");
		filePath.setEnabled(false);
		top_1.add(filePath, BorderLayout.CENTER);

		JButton fileButton = new JButton("选择文件");
		fileButton.setForeground(Color.WHITE);
		fileButton.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.blue));
		top_1.add(fileButton, BorderLayout.EAST);
		
		north.add(top_1);
		
		JPanel top_2 = new JPanel(new BorderLayout(10,10));
		
		JLabel saveTip = new JLabel("保存路径: ");
		top_2.add(saveTip, BorderLayout.WEST);

		JTextField dirPath = new JTextField("");
		dirPath.setEnabled(false);
		top_2.add(dirPath, BorderLayout.CENTER);

		JButton dirButton = new JButton("选择目录");
		dirButton.setForeground(Color.WHITE);
		dirButton.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.red));
		top_2.add(dirButton, BorderLayout.EAST);
		
		north.add(top_2);
		
		JPanel center = new JPanel(new BorderLayout(10, 10));
		
		JLabel algorithmTip = new JLabel("加密算法: ");
		center.add(algorithmTip, BorderLayout.WEST);
		
		JComboBox<String> algorithmBox = new JComboBox<String>(new String[]{"DES (56)","DESede (168)","AES (128)"});
		algorithmBox.setSelectedIndex(0);
		center.add(algorithmBox, BorderLayout.CENTER);
		
		north.add(center);
		
		JPanel bottom = new JPanel(new BorderLayout(10, 10));
		JLabel key = new JLabel("密钥: ");
		bottom.add(key, BorderLayout.WEST);
		JTextField keyField = new JTextField("test key");
		bottom.add(keyField, BorderLayout.CENTER);
		north.add(bottom);

		JButton encryptButton = new JButton("开始解密");
		encryptButton.setForeground(Color.WHITE);
		encryptButton.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));
		north.add(encryptButton);
	

		JTextArea infoArea = new JTextArea();
		infoArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(infoArea);
		panels[2].add(scrollPane, BorderLayout.CENTER);
		JScrollBar bar = scrollPane.getVerticalScrollBar();
		bar.addAdjustmentListener(new AdjustmentListener() {
			boolean state = false;
			public void adjustmentValueChanged(AdjustmentEvent e) {
				if(e.getAdjustmentType() == AdjustmentEvent.TRACK && !e.getValueIsAdjusting() && !state){
					bar.setValue(bar.getModel().getMaximum() - bar.getModel().getExtent()); 
				}
				state = e.getValueIsAdjusting();
			}
		});
		
		JProgressBar progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		panels[2].add(progressBar, BorderLayout.SOUTH);
		
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(line);
		
		encryptButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(filePath.getText().equals("") || dirPath.getText().equals("")){
					JOptionPane.showMessageDialog(MainFrame.this, "请选择解密文件和保存目录!");
					return;
				}
				if(keyField.getText().equals("")){
					JOptionPane.showMessageDialog(MainFrame.this, "请输入密钥!");
					return;
				}
				
				new Thread(){public void run() {
					
					stringBuilder.append(newLine + "开始解密");
					stringBuilder.append(newLine + line);
					infoArea.setText(stringBuilder.toString());
					
					File destFile = new File(filePath.getText());
					File file = new File(dirPath.getText() + "//decode_" + destFile.getName().substring(0, destFile.getName().lastIndexOf('.')));
					long fileLength = (long) (destFile.length() * 0.99);
					FileReadListener fileReadListener = new FileReadListener(){
						public void onLoad(long readLength) {
							double value = (double) readLength / fileLength;
							value *= 100;
							progressBar.setValue((int) value);
						}};
					FileEncryptFactory decodeFactory = new FileEncryptFactory(algorithm, keyField.getText(), fileReadListener);
					try {
						decodeFactory.decrypt(destFile, file);
					} catch (Exception e1) {
						System.out.println(e1);
						JOptionPane.showMessageDialog(MainFrame.this, "加密异常\n信息:" + e1, "错误", JOptionPane.ERROR_MESSAGE);
					} finally{
						stringBuilder.append(newLine + "完成");
						stringBuilder.append(newLine + line);
						stringBuilder.append(newLine + "解密文件已保存到:" + destFile.getPath());
						stringBuilder.append(newLine + line);
						infoArea.setText(stringBuilder.toString());
					}
					
				};}.start();
				
			}
		});
		
		algorithmBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange()==e.SELECTED){
					switch(algorithmBox.getSelectedIndex()){
					case 0:
						algorithm = FileEncryptFactory.DES_ALGORITHM;
						stringBuilder.append(newLine + "更改加密方式为:" + algorithm);
						stringBuilder.append(newLine + line);
						infoArea.setText(stringBuilder.toString());
						break;
					case 1:
						algorithm = FileEncryptFactory.DESede_ALGORITHM;
						stringBuilder.append(newLine + "更改加密方式为:" + algorithm);
						stringBuilder.append(newLine + line);
						infoArea.setText(stringBuilder.toString());
						break;
					case 2:
						algorithm = FileEncryptFactory.AES_ALGORITHM;
						stringBuilder.append(newLine + "更改加密方式为:" + algorithm);
						stringBuilder.append(newLine + line);
						infoArea.setText(stringBuilder.toString());
						break;
					}
				}
			}
		});
		
		dirButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File dir = showDirChooserDialog();
				dirPath.setText(dir.getPath());
				stringBuilder.append(newLine + "选择保存目录:" + dirPath.getText());
				stringBuilder.append(newLine + line);
				infoArea.setText(stringBuilder.toString());
			}
		});
		
		fileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File file = showFileChooserDialog();
				filePath.setText(file.getPath());
				stringBuilder.append(newLine + "选择加密文件:" + filePath.getText());
				stringBuilder.append(newLine + line);
				infoArea.setText(stringBuilder.toString());
			}
		});

	}

	private void initPage2() {
		JPanel north = new JPanel(new GridLayout(5, 1, 10, 10));
		panels[1].add(north, BorderLayout.NORTH);
		
		JPanel top_1 = new JPanel(new BorderLayout(10,10));
		
		JLabel tip = new JLabel("文件路径: ");
		top_1.add(tip, BorderLayout.WEST);

		JTextField filePath = new JTextField("");
		filePath.setEnabled(false);
		top_1.add(filePath, BorderLayout.CENTER);

		JButton fileButton = new JButton("选择文件");
		fileButton.setForeground(Color.WHITE);
		fileButton.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.blue));
		top_1.add(fileButton, BorderLayout.EAST);
		
		north.add(top_1);
		
		JPanel top_2 = new JPanel(new BorderLayout(10,10));
		
		JLabel saveTip = new JLabel("保存路径: ");
		top_2.add(saveTip, BorderLayout.WEST);

		JTextField dirPath = new JTextField("");
		dirPath.setEnabled(false);
		top_2.add(dirPath, BorderLayout.CENTER);

		JButton dirButton = new JButton("选择目录");
		dirButton.setForeground(Color.WHITE);
		dirButton.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.red));
		top_2.add(dirButton, BorderLayout.EAST);
		
		north.add(top_2);
		
		JPanel center = new JPanel(new BorderLayout(10, 10));
		
		JLabel algorithmTip = new JLabel("加密算法: ");
		center.add(algorithmTip, BorderLayout.WEST);
		
		JComboBox<String> algorithmBox = new JComboBox<String>(new String[]{"DES (56)","DESede (168)","AES (128)"});
		algorithmBox.setSelectedIndex(0);
		center.add(algorithmBox, BorderLayout.CENTER);
		
		north.add(center);
		
		JPanel bottom = new JPanel(new BorderLayout(10, 10));
		JLabel key = new JLabel("密钥: ");
		bottom.add(key, BorderLayout.WEST);
		JTextField keyField = new JTextField("test key");
		bottom.add(keyField, BorderLayout.CENTER);
		north.add(bottom);

		JButton encryptButton = new JButton("开始加密");
		encryptButton.setForeground(Color.WHITE);
		encryptButton.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));
		north.add(encryptButton);
	

		JTextArea infoArea = new JTextArea();
		infoArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(infoArea);
		panels[1].add(scrollPane, BorderLayout.CENTER);
		JScrollBar bar = scrollPane.getVerticalScrollBar();
		bar.addAdjustmentListener(new AdjustmentListener() {
			boolean state = false;
			public void adjustmentValueChanged(AdjustmentEvent e) {
				if(e.getAdjustmentType() == AdjustmentEvent.TRACK && !e.getValueIsAdjusting() && !state){
					bar.setValue(bar.getModel().getMaximum() - bar.getModel().getExtent()); 
				}
				state = e.getValueIsAdjusting();
			}
		});
		
		JProgressBar progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		panels[1].add(progressBar, BorderLayout.SOUTH);
		
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(line);
		
		encryptButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(filePath.getText().equals("") || dirPath.getText().equals("")){
					JOptionPane.showMessageDialog(MainFrame.this, "请选择加密文件和保存目录!");
					return;
				}
				if(keyField.getText().equals("")){
					JOptionPane.showMessageDialog(MainFrame.this, "请输入密钥!");
					return;
				}
				
				new Thread(){public void run() {
					
					stringBuilder.append(newLine + "开始加密");
					stringBuilder.append(newLine + line);
					infoArea.setText(stringBuilder.toString());
					
					File file = new File(filePath.getText());
					File destFile = new File(dirPath.getText() + "//" + file.getName() + "." + algorithm);
					long fileLength = file.length();
					FileReadListener fileReadListener = new FileReadListener(){
						public void onLoad(long readLength) {
							double value = (double) readLength / fileLength;
							value *= 100;
							progressBar.setValue((int) value);
						}};
						FileEncryptFactory encryptFactory = new FileEncryptFactory(algorithm, keyField.getText(), fileReadListener);
					try {
						encryptFactory.encrypt(file, destFile);
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(MainFrame.this, "加密异常\n信息:" + e1, "错误", JOptionPane.ERROR_MESSAGE);
					} finally{
						stringBuilder.append(newLine + "完成");
						stringBuilder.append(newLine + line);
						stringBuilder.append(newLine + "加密文件已保存到:" + destFile.getPath());
						stringBuilder.append(newLine + line);
						infoArea.setText(stringBuilder.toString());
					}
					
				};}.start();
				
			}
		});
		
		algorithmBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange()==e.SELECTED){
					switch(algorithmBox.getSelectedIndex()){
					case 0:
						algorithm = FileEncryptFactory.DES_ALGORITHM;
						stringBuilder.append(newLine + "更改加密方式为:" + algorithm);
						stringBuilder.append(newLine + line);
						infoArea.setText(stringBuilder.toString());
						break;
					case 1:
						algorithm = FileEncryptFactory.DESede_ALGORITHM;
						stringBuilder.append(newLine + "更改加密方式为:" + algorithm);
						stringBuilder.append(newLine + line);
						infoArea.setText(stringBuilder.toString());
						break;
					case 2:
						algorithm = FileEncryptFactory.AES_ALGORITHM;
						stringBuilder.append(newLine + "更改加密方式为:" + algorithm);
						stringBuilder.append(newLine + line);
						infoArea.setText(stringBuilder.toString());
						break;
					}
				}
			}
		});
		
		dirButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File dir = showDirChooserDialog();
				dirPath.setText(dir.getPath());
				stringBuilder.append(newLine + "选择保存目录:" + dirPath.getText());
				stringBuilder.append(newLine + line);
				infoArea.setText(stringBuilder.toString());
			}
		});
		
		fileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File file = showFileChooserDialog();
				filePath.setText(file.getPath());
				stringBuilder.append(newLine + "选择加密文件:" + filePath.getText());
				stringBuilder.append(newLine + line);
				infoArea.setText(stringBuilder.toString());
			}
		});

	}

	private void initPage1() {

		JPanel north = new JPanel(new BorderLayout(10, 10));
		panels[0].add(north, BorderLayout.NORTH);

		JTextArea infoArea = new JTextArea();
		infoArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(infoArea);
		panels[0].add(scrollPane, BorderLayout.CENTER);
		JScrollBar bar = scrollPane.getVerticalScrollBar();
		bar.addAdjustmentListener(new AdjustmentListener() {
			boolean state = false;
			public void adjustmentValueChanged(AdjustmentEvent e) {
				if(e.getAdjustmentType() == AdjustmentEvent.TRACK && !e.getValueIsAdjusting() && !state){
					bar.setValue(bar.getModel().getMaximum() - bar.getModel().getExtent()); 
				}
				state = e.getValueIsAdjusting();
			}
		});
		
		JProgressBar progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		panels[0].add(progressBar, BorderLayout.SOUTH);

		JLabel tip = new JLabel("文件路径: ");
		north.add(tip, BorderLayout.WEST);

		JTextField filePath = new JTextField("");
		filePath.setEnabled(false);
		north.add(filePath, BorderLayout.CENTER);

		JButton fileButton = new JButton("选择文件");
		fileButton.setForeground(Color.WHITE);
		fileButton.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.blue));
		north.add(fileButton, BorderLayout.EAST);
		fileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File file = showFileChooserDialog();
				if (file != null) {
					new Thread() {
						public void run() {

							StringBuilder stringBuilder = new StringBuilder();
							// 显示文件路径
							filePath.setText(file.getPath());
							stringBuilder.append(line + newLine);
							stringBuilder.append(getTime() + "获取到文件:" + newLine);
							stringBuilder.append(file.getPath() + newLine);
							stringBuilder.append(line + newLine);
							infoArea.setText(stringBuilder.toString());
							// 获取文件大小
							stringBuilder.append(getTime() + "文件体积:" + newLine);
							long fileLength = file.length();
							double fileSize = (double) fileLength / 1024 / 1024;
							stringBuilder.append(fileSize + " MB" + newLine);
							stringBuilder.append(line + newLine);
							infoArea.setText(stringBuilder.toString());

							FileReadListener readListener = new FileReadListener() {
								public void onLoad(long readLength) {
									double value = (double) readLength / fileLength;
									value *= 100;
									progressBar.setValue((int) value);
								}
							};
							
							long start = 0;
							long stop = 0;

							// 解析文件
							// MD5
							stringBuilder.append(getTime() + "MD5:" + newLine);
							start = System.currentTimeMillis();
							stringBuilder.append(get(file, readListener, EncryptFactory.MD5_ALGORITHM));
							stop = System.currentTimeMillis();
							stringBuilder.append(newLine + "耗时: "  + (stop-start) + " ms");
							stringBuilder.append(newLine + line + newLine);
							infoArea.setText(stringBuilder.toString());

							// SHA-1
							stringBuilder.append(getTime() + "SHA-1:" + newLine);
							start = System.currentTimeMillis();
							stringBuilder.append(get(file, readListener, EncryptFactory.SHA_1_ALGORITHM));
							stop = System.currentTimeMillis();
							stringBuilder.append(newLine + "耗时: "  + (stop-start) + " ms");
							stringBuilder.append(newLine + line + newLine);
							infoArea.setText(stringBuilder.toString());

							// SHA-224
							stringBuilder.append(getTime() + "SHA-224:" + newLine);
							start = System.currentTimeMillis();
							stringBuilder.append(get(file, readListener, EncryptFactory.SHA_224_ALGORITHM));
							stop = System.currentTimeMillis();
							stringBuilder.append(newLine + "耗时: "  + (stop-start) + " ms");
							stringBuilder.append(newLine + line + newLine);
							infoArea.setText(stringBuilder.toString());

							// SHA-256
							stringBuilder.append(getTime() + "SHA-256:" + newLine);
							start = System.currentTimeMillis();
							stringBuilder.append(get(file, readListener, EncryptFactory.SHA_256_ALGORITHM));
							stop = System.currentTimeMillis();
							stringBuilder.append(newLine + "耗时: "  + (stop-start) + " ms");
							stringBuilder.append(newLine + line + newLine);
							infoArea.setText(stringBuilder.toString());

							// SHA-384
							stringBuilder.append(getTime() + "SHA-384:" + newLine);
							start = System.currentTimeMillis();
							stringBuilder.append(get(file, readListener, EncryptFactory.SHA_384_ALGORITHM));
							stop = System.currentTimeMillis();
							stringBuilder.append(newLine + "耗时: "  + (stop-start) + " ms");
							stringBuilder.append(newLine + line + newLine);
							infoArea.setText(stringBuilder.toString());

							// SHA-512
							stringBuilder.append(getTime() + "SHA-512:" + newLine);
							start = System.currentTimeMillis();
							stringBuilder.append(get(file, readListener, EncryptFactory.SHA_512_ALGORITHM));
							stop = System.currentTimeMillis();
							stringBuilder.append(newLine + "耗时: "  + (stop-start) + " ms");
							stringBuilder.append(newLine + line + newLine);
							infoArea.setText(stringBuilder.toString());
							
							stringBuilder.append("完成");
							infoArea.setText(stringBuilder.toString());

						};
					}.start();
				}
			}
		});

	}

	public interface FileReadListener {
		public void onLoad(long readLength);
	}

	private static EncryptFactory encryptFactory = EncryptFactory.getInstance();

	public static String get(File file, FileReadListener listener, String algorithm) {
		InputStream ins = null;
		try {
			long readLength = 0;
			int len;
			byte[] buffer = new byte[8192];
			ins = new FileInputStream(file);
			while ((len = ins.read(buffer)) != -1) {
				encryptFactory.update(buffer, 0, len, algorithm);
				readLength += len;
				listener.onLoad(readLength);
			}
			ins.close();
			return encryptFactory.toHexString(encryptFactory.toHashBytes(algorithm));
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (ins != null)
				try {
					ins.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return null;
	}

	private File showFileChooserDialog() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.setCurrentDirectory(FileSystemView.getFileSystemView().getHomeDirectory());
		int returnVal = fileChooser.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			return fileChooser.getSelectedFile();
		}
		return null;
	}
	
	private File showDirChooserDialog() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setCurrentDirectory(FileSystemView.getFileSystemView().getHomeDirectory());
		int returnVal = fileChooser.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			return fileChooser.getSelectedFile();
		}
		return null;
	}

	private String getTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss   \n");
		return "System Time : " + dateFormat.format(new Date(System.currentTimeMillis()));
	}

	private void initComponent() {
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		for (int i = 0; i < panels.length; i++) {
			panels[i] = new JPanel(new BorderLayout(10, 10));
			panels[i].setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			tabbedPane.add(tabStrs[i], panels[i]);
		}
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(tabbedPane, BorderLayout.CENTER);
		getContentPane().add(mainPanel);
		
		JLabel logo = new JLabel(new ImageIcon(System.getProperty("user.dir")+"\\logo.png"));
		mainPanel.add(logo, BorderLayout.NORTH);
	}

}
