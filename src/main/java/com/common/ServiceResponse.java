package com.common;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;
import java.util.List;

/**
 * 定义服务端返回的对象，返回不为空的字段
 * @param <T>
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ServiceResponse<T> implements Serializable {
    private int status;

    private String msg;

    private T data;

    private List<T> dataList;

    public List<T> getDataList() {
        return dataList;
    }

    private ServiceResponse(int status) {
        this.status = status;
    }

    private ServiceResponse(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    private ServiceResponse(int status, T data) {
        this.status = status;
        this.data = data;
    }

    private ServiceResponse(int status, List<T> dataList) {
        this.status = status;
        this.dataList = dataList;
    }


    private ServiceResponse(int status, String msg, T data) {
        this.status = status;
        this.data = data;
        this.msg = msg;
    }

    private ServiceResponse(int status, String msg, List<T> dataList) {
        this.status = status;
        this.dataList = dataList;
        this.msg = msg;
    }

    @JsonIgnore//让这个字段不要显示
    public boolean isSuccess() {
        return this.status == ResponseCode.SUCCESS.getCode();
    }

    public static <T> ServiceResponse<T> createBySuccess(){
        return new ServiceResponse<>(ResponseCode.SUCCESS.getCode());
    }

    public static <T> ServiceResponse<T> createBySuccessMessage(String msg){
        return new ServiceResponse<>(ResponseCode.SUCCESS.getCode(),msg);
    }

    public static <T> ServiceResponse<T> createBySuccess(T data) {
        return new ServiceResponse<>(ResponseCode.SUCCESS.getCode(), data);
    }

    public static <T> ServiceResponse<T> createBySuccess(List<T> dataList) {
        return new ServiceResponse<>(ResponseCode.SUCCESS.getCode(), dataList);
    }

    public static <T> ServiceResponse<T> createBySuccess(String msg,T data) {
        return new ServiceResponse<>(ResponseCode.SUCCESS.getCode(), msg,data);
    }

    public static <T> ServiceResponse<T> createBySuccess(String msg, List<T> dataList) {
        return new ServiceResponse<>(ResponseCode.SUCCESS.getCode(), msg, dataList);
    }

    public static <T> ServiceResponse<T> createByError() {
        return new ServiceResponse<>(ResponseCode.ERROR.getCode());
    }

    public static <T> ServiceResponse<T> createByError(T data) {
        return new ServiceResponse<>(ResponseCode.ERROR.getCode(),data);
    }

    public static <T> ServiceResponse<T> createByError(List<T> dataList) {
        return new ServiceResponse<>(ResponseCode.ERROR.getCode(), dataList);
    }

    public static <T> ServiceResponse<T> createByErrorMessage(String msg) {
        return new ServiceResponse<>(ResponseCode.ERROR.getCode(),msg);
    }

    public static <T> ServiceResponse<T> createByError(String msg,T data) {
        return new ServiceResponse<>(ResponseCode.ERROR.getCode(), msg,data);
    }

    public static <T> ServiceResponse<T> createByError(String msg, List<T> dataList) {
        return new ServiceResponse<>(ResponseCode.ERROR.getCode(), msg, dataList);
    }

    public static <T> ServiceResponse<T> createByErrorCodeMessage(int  errorCode, String msg) {
        return new ServiceResponse<>(errorCode, msg);
    }



    public int getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }
}
