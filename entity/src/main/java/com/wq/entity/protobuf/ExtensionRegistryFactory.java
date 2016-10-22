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
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ExtensionRegistry;

public class ExtensionRegistryFactory {
	
	private String path;
	private String[] protoNames;
	Logger log = LoggerFactory.getLogger(this.getClass());
	
	public ExtensionRegistryFactory(){
		init();
	}
	
	public ExtensionRegistryFactory(String path){
		this.path = path;
		init();
	}
	
	public void init(){
		
		// 如果没有设置路径，默认当前路径
		if(path == null || "".equals(path)){
			path = getClass().getResource("/com/wq/entity/protobuf/proto/").getPath().toString();
		}
		
		// 获取proto文件夹
		File protoPath = new File(path);
		if(!protoPath.isDirectory()){
			throw new IllegalArgumentException("Path is wrong");
		}
		
		// 获取proto文件的名字
		protoNames = protoPath.list(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".proto");
			}
		});
	}

	/**
	 * 读取协议生成协议扩展器
	 * @return
	 */
	public ExtensionRegistry getInstance(){
		List<String> classNames = new ArrayList<>();
		
		// 拼类全名
		try {
			
			// 读取取每个proto
			for(int i = 0; i < protoNames.length; i++){
				String protoName = protoNames[i];
				FileInputStream fis = new FileInputStream(new File(path+"/"+protoName));
				InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
				BufferedReader br = new BufferedReader(isr);
				
				// 读proto内容，查找包名
				String line = null;
				while((line = br.readLine()) != null){
					
					// 读到有package的行，取包名
					if(line.contains("package")){
						String packageName = line.trim().split(" ")[1];
						packageName = packageName.replace(";", "");
						protoName = packageName+"."+protoName.replace(".proto", "");
						break;
					}
				}
				classNames.add(protoName);
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
		for(String className : classNames){
			try {
				Method method = Class.forName(className).getMethod("registerAllExtensions", ExtensionRegistry.class);
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
	
	public void makeProto(){
		String protobufPath = path.substring(0,path.lastIndexOf("\\"));
		String command = protobufPath + "/protoc --proto_path=./proto/ --java_out=../../../../ proto/";
		try {
			Runtime r = Runtime.getRuntime();
			for(String name : protoNames){
				Process p = r.exec(command+name, null, new File(protobufPath));
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
		ExtensionRegistryFactory factory = new ExtensionRegistryFactory("E:\\MyGit\\SimpleGameServer\\entity\\src\\main\\java\\com\\wq\\entity\\protobuf\\proto");
		factory.makeProto();
	}

}
