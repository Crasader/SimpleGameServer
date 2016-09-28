package com.wq.entity.protobuf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ExtensionRegistry;

public class ExtensionRegistryFactory {
	
	private String path = "";
	Logger log = LoggerFactory.getLogger(this.getClass());
	
	public ExtensionRegistryFactory(){
		
	}
	
	public ExtensionRegistryFactory(String path){
		this.path = path;
	}

	/**
	 * 读取协议生成协议扩展器
	 * @return
	 */
	public ExtensionRegistry getInstance(){
		
		// 如果没有设置路径，默认当前路径
		if("".equals(path)){
			path = getClass().getResource("/com/wq/entity/protobuf/").getFile().toString();
		}
		
		// 转化文件开始目录
		File beginPath = new File(path);
		if(!beginPath.isDirectory()){
			throw new IllegalArgumentException("Path is wrong");
		}
		
		// proto文件目录，要在开始目录以下
		File protoPath = new File(beginPath.toString()+"/proto/");
		if(!protoPath.isDirectory()){
			throw new IllegalArgumentException("ProtoPath is wrong");
		}
		
		// 获取proto文件的名字
		String[] protoNames = protoPath.list(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".proto");
			}
		});
		
		// 编译proto文件
		makeProto(beginPath.toString(), protoPath.toString(), protoNames);
		
		// 读包名
		try {
			for(int i = 0; i < protoNames.length; i++){
				String name = protoNames[i];
				FileInputStream fis = new FileInputStream(new File(protoPath+"/"+name));
				InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
				BufferedReader br = new BufferedReader(isr);
				String line = null;
				String packageStr = null;
				while((line = br.readLine()) != null){
					
					// 读到有package的行，取最后一段去掉分号作为包名
					if(line.contains("package")){
						String[] packageLine = line.trim().split(" ");
						packageStr = packageLine[packageLine.length-1];
						packageStr = packageStr.replace(";", "");
						break;
					}
				}
				
				// 如果没有读到package行，使用当前包名
				if(packageStr == null){
					packageStr = this.getClass().getPackage().getName();
				}
				
				// 包名加在类名前
				name = name.replace(".proto", "");
				name = packageStr + "." +name;
				protoNames[i] = name;
				br.close();
				isr.close();
				fis.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// 注册协议到扩展器
		ExtensionRegistry registry = ExtensionRegistry.newInstance();
		for(String name : protoNames){
			try {
				Method method = Class.forName(name).getMethod("registerAllExtensions", ExtensionRegistry.class);
				method.invoke(null, registry);
			}catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return registry;
	}
	
	public void makeProto(String beginPath, String protoPath, String[] protoNames){
		if(!protoPath.contains(beginPath)){
			log.error("ProtoPath is not under beginPath,protocols will not been built");
			return;
		}
		protoPath = protoPath.replace(beginPath+"\\", ""); // 此处应做平台兼容
		String command = beginPath + "/protoc --proto_path=./" + protoPath + " --java_out=../../../../ " + protoPath + "/";
		try {
			Runtime r = Runtime.getRuntime();
			for(String name : protoNames){
				Process p = r.exec(command+name, null, new File(beginPath));
				BufferedReader br = new BufferedReader(new InputStreamReader(p.getErrorStream(),"GBK"));//注意中文编码问题
				String line;
				while ((line = br.readLine()) != null) {
					log.error("Error in building proto : " + line);
				}
				br.close();
				p.waitFor();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		ExtensionRegistryFactory factory = new ExtensionRegistryFactory("E:/mywork/entity/src/main/java/com/wq/entity/protobuf/");
		factory.getInstance();
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
