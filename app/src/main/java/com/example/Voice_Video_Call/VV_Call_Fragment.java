package com.example.Voice_Video_Call;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.quickconnect.Customer;
import com.example.quickconnect.Employee;
import com.example.quickconnect.User;
import com.example.quickconnect.databinding.FragmentVVCallBinding;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.zegocloud.uikit.components.audiovideocontainer.ZegoLayout;
import com.zegocloud.uikit.components.audiovideocontainer.ZegoLayoutGalleryConfig;
import com.zegocloud.uikit.components.audiovideocontainer.ZegoLayoutMode;
import com.zegocloud.uikit.components.audiovideocontainer.ZegoLayoutPictureInPictureConfig;
import com.zegocloud.uikit.components.common.ZegoShowFullscreenModeToggleButtonRules;
import com.zegocloud.uikit.plugin.invitation.ZegoInvitationType;
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallConfig;
import com.zegocloud.uikit.prebuilt.call.config.DurationUpdateListener;
import com.zegocloud.uikit.prebuilt.call.config.ZegoCallDurationConfig;
import com.zegocloud.uikit.prebuilt.call.config.ZegoHangUpConfirmDialogInfo;
import com.zegocloud.uikit.prebuilt.call.config.ZegoMenuBarButtonName;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoCallInvitationData;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallConfigProvider;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationService;
import com.zegocloud.uikit.prebuilt.call.invite.widget.ZegoSendCallInvitationButton;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class VV_Call_Fragment extends Fragment {
    FragmentVVCallBinding binding;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentVVCallBinding.inflate(inflater,container,false);



        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        TextView yourUserID = binding.yourUserId;
//        TextView yourUserName = binding.yourUserName;

//        User user =getUserDetailsFromSharedPreferences();
//        String userID = user.getFirstName().toString().toLowerCase();
//        String userName = user.getFullName();
//
//
//        yourUserID.setText("Your User ID :" + user.getFirstName());
//        yourUserName.setText("Your User Name :" + user.getFullName());
//
//        long appID = 479817296;
//        String appSign = "db0f7d073f55e3553552fe098454ae400f0b80572a520bce6ef56058a53d3728";
//
//        initCallInviteService(appID, appSign, userID, userName);

        initVoiceButton();

        initVideoButton();

//        findViewById(R.id.user_logout).setOnClickListener(v -> {
//            AlertDialog.Builder builder = new AlertDialog.Builder(MainVVActivity.this);
//            builder.setTitle("Sign Out");
//            builder.setMessage("Are you sure to Sign Out?");
//            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                }
//            });
//            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                    ZegoUIKitPrebuiltCallInvitationService.unInit();
//                    finish();
//                }
//            });
//            builder.create().show();
//        });


    }




    public void initCallInviteService(long appID, String appSign, String userID, String userName) {
        ZegoUIKitPrebuiltCallInvitationConfig callInvitationConfig = new ZegoUIKitPrebuiltCallInvitationConfig();
        callInvitationConfig.innerText.incomingCallPageDeclineButton = "Decline";
        callInvitationConfig.innerText.incomingCallPageAcceptButton = "Accept";
        callInvitationConfig.provider = new ZegoUIKitPrebuiltCallConfigProvider() {
            @Override
            public ZegoUIKitPrebuiltCallConfig requireConfig(ZegoCallInvitationData invitationData) {
                ZegoUIKitPrebuiltCallConfig config;
                boolean isVideoCall = invitationData.type == ZegoInvitationType.VIDEO_CALL.getValue();
                boolean isGroupCall = invitationData.invitees.size() > 1;
                if (isVideoCall && isGroupCall) {

                    Log.d("HI", "requireConfig: 1");
                    config = ZegoUIKitPrebuiltCallConfig.groupVideoCall();
                } else if (!isVideoCall && isGroupCall) {
                    Log.d("HI", "requireConfig: 2");
                    config = ZegoUIKitPrebuiltCallConfig.groupVoiceCall();
                } else if (!isVideoCall) {
                    Log.d("HI", "requireConfig: 3");
                    config = ZegoUIKitPrebuiltCallConfig.oneOnOneVoiceCall();
                } else {
                    Log.d("HI", "requireConfig: 4");
                    config = ZegoUIKitPrebuiltCallConfig.oneOnOneVideoCall();
                }
                // Modify your custom calling configurations here.

                if (isGroupCall) {
                    Log.d("HI", "requireConfig: 5");
                    ZegoLayoutGalleryConfig galleryConfig = new ZegoLayoutGalleryConfig();
                    galleryConfig.addBorderRadiusAndSpacingBetweenView = false;
                    config.layout.mode = ZegoLayoutMode.GALLERY;
                    config.layout.config = galleryConfig;
                    config.hangUpConfirmDialogInfo = new ZegoHangUpConfirmDialogInfo();
                    config.hangUpConfirmDialogInfo.title = "Hangup confirm";
                    config.hangUpConfirmDialogInfo.message = "Do you want to hangup?";
                    config.hangUpConfirmDialogInfo.cancelButtonName = "Cancel";
                    config.hangUpConfirmDialogInfo.confirmButtonName = "Confirm";
                    config.durationConfig = new ZegoCallDurationConfig();
                    config.durationConfig.isVisible = true;
                    config.durationConfig.durationUpdateListener = new DurationUpdateListener() {
                        @Override
                        public void onDurationUpdate(long seconds) {
                            Log.d(TAG, "onDurationUpdate() called with: seconds = [" + seconds + "]");
                            if (seconds == 60 * 5) {
                                ZegoUIKitPrebuiltCallInvitationService.endCall();
                            }
                        }
                    };
                    config.bottomMenuBarConfig.buttons = Arrays.asList(ZegoMenuBarButtonName.TOGGLE_CAMERA_BUTTON,ZegoMenuBarButtonName.SWITCH_CAMERA_BUTTON, ZegoMenuBarButtonName.HANG_UP_BUTTON,ZegoMenuBarButtonName.TOGGLE_MICROPHONE_BUTTON, ZegoMenuBarButtonName.SCREEN_SHARING_TOGGLE_BUTTON);
                    galleryConfig.removeViewWhenAudioVideoUnavailable = true;
                    galleryConfig.showNewScreenSharingViewInFullscreenMode = true;
                    galleryConfig.showScreenSharingFullscreenModeToggleButtonRules = ZegoShowFullscreenModeToggleButtonRules.SHOW_WHEN_SCREEN_PRESSED;
                    config.layout = new ZegoLayout(ZegoLayoutMode.GALLERY, galleryConfig);
                    config.topMenuBarConfig.isVisible = true;
                    config.topMenuBarConfig.buttons.add(ZegoMenuBarButtonName.MINIMIZING_BUTTON);
                    return config;


                } else {
                    Log.d("HI", "requireConfig: 6");

                    ZegoLayoutPictureInPictureConfig pipConfig = new ZegoLayoutPictureInPictureConfig();
                    pipConfig.isSmallViewDraggable = true;
                    pipConfig.switchLargeOrSmallViewByClick = true;
                    config.layout.mode = ZegoLayoutMode.PICTURE_IN_PICTURE;
                    config.layout.config = pipConfig;
                    config.hangUpConfirmDialogInfo = new ZegoHangUpConfirmDialogInfo();
                    config.hangUpConfirmDialogInfo.title = "Hangup confirm";
                    config.hangUpConfirmDialogInfo.message = "Do you want to hangup?";
                    config.hangUpConfirmDialogInfo.cancelButtonName = "Cancel";
                    config.hangUpConfirmDialogInfo.confirmButtonName = "Confirm";
                    config.durationConfig = new ZegoCallDurationConfig();
                    config.durationConfig.isVisible = true;
                    config.topMenuBarConfig.isVisible = true;
                    config.topMenuBarConfig.buttons.add(ZegoMenuBarButtonName.MINIMIZING_BUTTON);
                    config.durationConfig.durationUpdateListener = new DurationUpdateListener() {
                        @Override
                        public void onDurationUpdate(long seconds) {
                            Log.d(TAG, "onDurationUpdate() called with: seconds = [" + seconds + "]");
                            if (seconds == 60 * 5) {
                                ZegoUIKitPrebuiltCallInvitationService.endCall();
                            }
                        }

                    };

                    return config;
                }
            }
        };
        Log.d("HI", "requireConfig: 7");
        ZegoUIKitPrebuiltCallInvitationService.init(this.requireActivity().getApplication(), appID,appSign,userID,userName,callInvitationConfig);

        Log.d("HI", "requireConfig: 8");
    };






    private void initVideoButton () {
        ZegoSendCallInvitationButton newVideoCall = binding.newVideoCall;
        newVideoCall.setIsVideoCall(true);
        newVideoCall.setOnClickListener(v -> {
            TextInputLayout inputLayout = binding.targetUserId;
            String targetUserID = inputLayout.getEditText().getText().toString();
            String[] split = targetUserID.split(",");
            List<ZegoUIKitUser> users = new ArrayList<>();
            for (String userID : split) {
                String userName = userID + "_name";
                users.add(new ZegoUIKitUser(userID, userName));
            }
            newVideoCall.setInvitees(users);
        });
    }

    private void initVoiceButton () {
        ZegoSendCallInvitationButton newVoiceCall = binding.newVoiceCall;
        newVoiceCall.setIsVideoCall(false);
        newVoiceCall.setOnClickListener(v -> {
            TextInputLayout inputLayout = binding.targetUserId;
            String targetUserID = inputLayout.getEditText().getText().toString();
            String[] split = targetUserID.split(",");
            List<ZegoUIKitUser> users = new ArrayList<>();
            for (String userID : split) {
                String userName = userID + "_name";
                users.add(new ZegoUIKitUser(userID, userName));
            }
            newVoiceCall.setInvitees(users);
        });
    }

    private User getUserDetailsFromSharedPreferences() {
        // Obtain SharedPreferences instance
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE);

        // Retrieve the stored JSON string
        String userJson = sharedPreferences.getString("UserDetails", null);

        // Check if the JSON string is not null
        if (userJson != null) {
            // Use Gson to deserialize the JSON string into your User object
            Gson gson = new Gson();
            User user = gson.fromJson(userJson, User.class);

            if (user.getUserType().equals("Customer")) {
                // If the user is a Customer, store it as a Customer object
                Customer customer = gson.fromJson(userJson, Customer.class);
                return customer;
            } else if (user.getUserType().equals("Employee")) {
                // If the user is an Employee, store it as an Employee object
                Employee employee = gson.fromJson(userJson, Employee.class); // Replace 'Employee' with your actual Employee class
                return employee;
            }

            return null; // Return null if the user type is neither Customer nor Employee
        }

        return null; // Return null if the JSON string is null or if there's an error in deserialization
    }

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        ZegoUIKitPrebuiltCallInvitationService.unInit();
//    }
}

