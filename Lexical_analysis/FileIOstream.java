package demo2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileIOstream{
	public String InputFile(String pathname){//�����ļ�
		File file=new File(pathname);
		String ret_content=new String("");
		try {
			FileInputStream fis=new FileInputStream(file);
			byte[] b=new byte[1024];
			int n=0;
			while((n=fis.read(b, 0, b.length))!=-1){
				ret_content+=new String(b,0,n);
			}
			fis.close();
		} catch (FileNotFoundException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		} catch (IOException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}
		return ret_content;
	}
	public void OuputFile(String out,String pathname) {
		File file=new File(pathname);
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO �Զ����ɵ� catch ��
				e.printStackTrace();
			}
		}
		try {
			FileOutputStream fos=new FileOutputStream(file);
			fos.write(out.getBytes());
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		} catch (IOException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}
	}
}
