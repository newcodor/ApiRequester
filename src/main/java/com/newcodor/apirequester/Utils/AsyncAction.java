package com.newcodor.apirequester.Utils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;

public class AsyncAction extends  Thread{

    private Object object;
    private Class _class;
    private String actionMethod;
    private Object[] params = null;

    private Class<?> [] paramTypes =null;
//    private String params;
    @Override
    public void run(){
        try{
            Method method;
            method= this._class.getMethod(actionMethod,paramTypes);
            if(method.isAccessible()){
                method.setAccessible(true);
            }
            System.out.println(method.getDeclaringClass());
            System.out.println(method.getAnnotatedParameterTypes());
            System.out.println(method.getDeclaredAnnotations());
            System.out.println(method.getParameterCount());
            System.out.println(method.getParameterTypes());
            Parameter [] parameters = method.getParameters();
            for(Parameter pam : parameters){
                System.out.println(""+pam.getName()+","+pam.getType());
            }
            System.out.println();
//          call static method for class
            if(Modifier.isStatic(method.getModifiers())){
                method.invoke(null,this.params);
            }else{
                //if no orgin object,we need a new instance
                if(this.object==null){
                    this.object=this._class.newInstance();
                }
                System.out.println(this.object);
                method.invoke(this.object,this.params);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public AsyncAction(Class _class,String actionMethod,Object ... params) throws ClassNotFoundException {
        this._class = _class;
        this.actionMethod = actionMethod;
        this.params = params;
        if(params.length>0){
            this.paramTypes =new Class[params.length];
            if(String.class.equals(this.params[0].getClass())){
                System.out.println("class ok ");
            }
            for(int i=0;i<params.length;i++){
                this.paramTypes[i]=params[i].getClass();
            }
        }
    }

    public AsyncAction(String className,String actionMethod,Object ... params) throws ClassNotFoundException {
        this(Class.forName(className),actionMethod,params);
    }

    public AsyncAction(Object object,String actionMethod,Object ... params) throws ClassNotFoundException {
        this(object.getClass(),actionMethod,params);
        this.object=object;
    }
}
