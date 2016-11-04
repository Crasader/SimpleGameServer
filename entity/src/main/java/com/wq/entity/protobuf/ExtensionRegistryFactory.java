package com.wq.entity.protobuf;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ExtensionRegistry;

public class ExtensionRegistryFactory {
	
	private String path;
	private List<String> classNames = new ArrayList<>();
	Logger log = LoggerFactory.getLogger(this.getClass());
	
	public ExtensionRegistryFactory(){
		
	}
	
	private void readClassNames(File file){
		File[] files = file.listFiles();
		for(File temp : files){
			if(temp.isDirectory()){
				readClassNames(temp);
			}
			String className = temp.toString();
			if(className.endsWith(".class") && !className.contains("$")){
				className = className.substring(className.indexOf("com"), className.indexOf(".class"));
				
				// 反斜杠要加转意，正斜杠不用加
				if("\\".equals(System.getProperty("file.separator"))){
					className = className.replaceAll("\\\\", ".");
				}else{
					className = className.replaceAll(System.getProperty("separator"), ".");
				}
				classNames.add(className);
			}
		}
	}
	
	public static void main(String[] args){
		ExtensionRegistryFactory e = new ExtensionRegistryFactory();
		e.getInstance();
	}

	/**
	 * 读取协议生成协议扩展器
	 * @return
	 */
	public ExtensionRegistry getInstance(){
		if(path == null || "".equals(path)){
			path = getClass().getResource("/com/wq/entity/protobuf/").getPath().toString();
		}
		File classPath = new File(path);
		if(!classPath.isDirectory()){
			throw new IllegalArgumentException("Locate protocols fail");
		}
		File[] subPaths = classPath.listFiles();
		for(File temp : subPaths){
			if(temp.isDirectory()){
				readClassNames(temp);
			}
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

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
}
