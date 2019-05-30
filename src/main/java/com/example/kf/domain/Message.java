package com.example.kf.domain;

import com.example.kf.domain.util.ListToStringConverter;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * 服务端发送给客户端消息实体
 * @author xiongzichao
 *
 */
@Entity
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ColumnDefault("int default 0")
    private int id;

    private  String  alert;   //

    @Convert(converter = ListToStringConverter.class)
    private  List<String>  names;

    private  String  sendMsg;

    private String  fromUser;

    private String toUser;

    private String  date;


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSendMsg() {
        return sendMsg;
    }

    public void setSendMsg(String sendMsg) {
        this.sendMsg = sendMsg;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public String getAlert() {
        return alert;
    }

    public void setAlert(String alert) {
        this.alert = alert;
    }

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    public String getToUser() {
        return toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    public Message() {
        super();
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", alert='" + alert + '\'' +
                ", names=" + names +
                ", sendMsg='" + sendMsg + '\'' +
                ", fromUser='" + fromUser + '\'' +
                ", toUser='" + toUser + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
