package com.common;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 定义服务端返回的对象，返回不为空的字段
 *
 * @param <T>
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ServerResponse<T> implements Serializable {
    private int status;

    private String msg;

    private T data;

    private List<T> dataList;

    private Map<String, Object> map = new HashMap<>();

    public ServerResponse<T> add(String key, Object value) {
        this.getMap().put(key, value);
        return this;
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }

    public List<T> getDataList() {
        return dataList;
    }

    private ServerResponse(int status) {
        this.status = status;
    }

    private ServerResponse(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    private ServerResponse(int status, T data) {
        this.status = status;
        this.data = data;
    }

    private ServerResponse(int status, List<T> dataList) {
        this.status = status;
        this.dataList = dataList;
    }


    private ServerResponse(int status, String msg, T data) {
        this.status = status;
        this.data = data;
        this.msg = msg;
    }

    private ServerResponse(int status, String msg, List<T> dataList) {
        this.status = status;
        this.dataList = dataList;
        this.msg = msg;
    }

    @JsonIgnore//让这个字段不要显示
    public boolean isSuccess() {
        return this.status == ResponseCode.SUCCESS.getCode();
    }

    public static <T> ServerResponse<T> createBySuccess() {
        return new ServerResponse<>(ResponseCode.SUCCESS.getCode());
    }

    public static <T> ServerResponse<T> createBySuccessMessage(String msg) {
        return new ServerResponse<>(ResponseCode.SUCCESS.getCode(), msg);
    }

    public static <T> ServerResponse<T> createBySuccess(T data) {
        return new ServerResponse<>(ResponseCode.SUCCESS.getCode(), data);
    }

    public static <T> ServerResponse<T> createBySuccess(List<T> dataList) {
        return new ServerResponse<>(ResponseCode.SUCCESS.getCode(), dataList);
    }

    public static <T> ServerResponse<T> createBySuccess(String msg, T data) {
        return new ServerResponse<>(ResponseCode.SUCCESS.getCode(), msg, data);
    }

    public static <T> ServerResponse<T> createBySuccess(String msg, List<T> dataList) {
        return new ServerResponse<>(ResponseCode.SUCCESS.getCode(), msg, dataList);
    }

    public static <T> ServerResponse<T> createByError() {
        return new ServerResponse<>(ResponseCode.ERROR.getCode());
    }

    public static <T> ServerResponse<T> createByError(T data) {
        return new ServerResponse<>(ResponseCode.ERROR.getCode(), data);
    }

    public static <T> ServerResponse<T> createByError(List<T> dataList) {
        return new ServerResponse<>(ResponseCode.ERROR.getCode(), dataList);
    }

    public static <T> ServerResponse<T> createByErrorMessage(String msg) {
        return new ServerResponse<>(ResponseCode.ERROR.getCode(), msg);
    }

    public static <T> ServerResponse<T> createByError(String msg, T data) {
        return new ServerResponse<>(ResponseCode.ERROR.getCode(), msg, data);
    }

    public static <T> ServerResponse<T> createByError(String msg, List<T> dataList) {
        return new ServerResponse<>(ResponseCode.ERROR.getCode(), msg, dataList);
    }

    public static <T> ServerResponse<T> createByErrorCodeMessage(int errorCode, String msg) {
        return new ServerResponse<>(errorCode, msg);
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
