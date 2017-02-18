package com.encrypt.system.launch;

import java.awt.Font;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.swing.UIManager;

import com.encrypt.system.algorithm.EncryptFactory;
import com.encrypt.system.view.MainFrame;

public class ProgramEntry {
	
	public static String[] DEFAULT_FONT  = new String[]{
		    "Table.font"
		    ,"TableHeader.font"
		    ,"CheckBox.font"
		    ,"Tree.font"
		    ,"Viewport.font"
		    ,"ProgressBar.font"
		    ,"RadioButtonMenuItem.font"
		    ,"ToolBar.font"
		    ,"ColorChooser.font"
		    ,"ToggleButton.font"
		    ,"Panel.font"
		    ,"TextArea.font"
		    ,"Menu.font"
		    ,"TableHeader.font"
		    ,"TextField.font"
		    ,"OptionPane.font"
		    ,"MenuBar.font"
		    ,"Button.font"
		    ,"Label.font"
		    ,"PasswordField.font"
		    ,"ScrollPane.font"
		    ,"MenuItem.font"
		    ,"ToolTip.font"
		    ,"List.font"
		    ,"EditorPane.font"
		    ,"Table.font"
		    ,"TabbedPane.font"
		    ,"RadioButton.font"
		    ,"CheckBoxMenuItem.font"
		    ,"TextPane.font"
		    ,"PopupMenu.font"
		    ,"TitledBorder.font"
		    ,"ComboBox.font"
		};

	public static void main(String[] args) {
		try {
			org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
			UIManager.put("RootPane.setupButtonVisible", false);
			for (int i = 0; i < DEFAULT_FONT.length; i++)
				UIManager.put(DEFAULT_FONT[i],new Font("Î¢ÈíÑÅºÚ", Font.PLAIN,14));
		} catch (Exception e) {
			e.printStackTrace();
		}
		MainFrame frame = new MainFrame();
		frame.setVisible(true);

	}
	
	interface FileReadListener{
		public void onLoad(long readLength);
	}
	
	public static byte[] getBytes(File file, FileReadListener listener){  
        byte[] buffer = null;  
        try {  
            FileInputStream fis = new FileInputStream(file);  
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);  
            byte[] b = new byte[1000]; 
            long readLength = 0;
            int n;  
            while ((n = fis.read(b)) != -1) { 
                bos.write(b, 0, n);  
                readLength += n;
                listener.onLoad(readLength);
            }  
            fis.close();  
            bos.close();  
            buffer = bos.toByteArray();  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        return buffer;  
    } 

}
