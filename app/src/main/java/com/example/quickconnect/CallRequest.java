package com.example.quickconnect;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Date;

public class CallRequest implements Parcelable {
    private String requestId;
    private String customerId;
    private String customerName;
    private String supportId;
    private String supportName;
    private Chat chat;
    private String query;
    private String category;
    private int queueNo;
    private Date requestDate;
    private Boolean isAccepted;
    private Boolean isClosed;
    private String callType;

    public CallRequest() {
    }

    public CallRequest(String requestId, String customerId, String customerName, String supportId, String supportName, Chat chat, String query, String category, int queueNo, Date requestDate, Boolean isAccepted, Boolean isClosed, String callType) {
        this.requestId = requestId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.supportId = supportId;
        this.supportName = supportName;
        this.chat = chat;
        this.query = query;
        this.category = category;
        this.queueNo = queueNo;
        this.requestDate = requestDate;
        this.isAccepted = isAccepted;
        this.isClosed = isClosed;
        this.callType = callType;
    }


    protected CallRequest(Parcel in) {
        requestId = in.readString();
        customerId = in.readString();
        customerName = in.readString();
        supportId = in.readString();
        supportName = in.readString();
        chat = in.readParcelable(Chat.class.getClassLoader());
        query = in.readString();
        category = in.readString();
        queueNo = in.readInt();
        byte tmpIsAccepted = in.readByte();
        isAccepted = tmpIsAccepted == 0 ? null : tmpIsAccepted == 1;
        byte tmpIsClosed = in.readByte();
        isClosed = tmpIsClosed == 0 ? null : tmpIsClosed == 1;
        callType = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(requestId);
        dest.writeString(customerId);
        dest.writeString(customerName);
        dest.writeString(supportId);
        dest.writeString(supportName);
        dest.writeParcelable(chat, flags);
        dest.writeString(query);
        dest.writeString(category);
        dest.writeInt(queueNo);
        dest.writeByte((byte) (isAccepted == null ? 0 : isAccepted ? 1 : 2));
        dest.writeByte((byte) (isClosed == null ? 0 : isClosed ? 1 : 2));
        dest.writeString(callType);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CallRequest> CREATOR = new Creator<CallRequest>() {
        @Override
        public CallRequest createFromParcel(Parcel in) {
            return new CallRequest(in);
        }

        @Override
        public CallRequest[] newArray(int size) {
            return new CallRequest[size];
        }
    };

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getSupportId() {
        return supportId;
    }

    public void setSupportId(String supportId) {
        this.supportId = supportId;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Boolean getAccepted() {
        return isAccepted;
    }

    public void setAccepted(Boolean accepted) {
        isAccepted = accepted;
    }

    public Boolean getClosed() {
        return isClosed;
    }

    public void setClosed(Boolean closed) {
        isClosed = closed;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getSupportName() {
        return supportName;
    }

    public void setSupportName(String supportName) {
        this.supportName = supportName;
    }

    public int getQueueNo() {
        return queueNo;
    }

    public void setQueueNo(int queueNo) {
        this.queueNo = queueNo;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

}
