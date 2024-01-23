package com.example.listener;


public interface UsersListener {

    void initiateVideoMeeting(com.example.models.User user);

    void initiateAudioMeeting(com.example.models.User user);

    void onMultipleUsersAction(Boolean isMultipleUsersSelected);
}